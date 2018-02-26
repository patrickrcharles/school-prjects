// addrspace.cc 
//	Routines to manage address spaces (executing user programs).
//
//	In order to run a user program, you must:
//
//	1. link with the -N -T 0 option 
//	2. run coff2noff to convert the object file to Nachos format
//		(Nachos object code format is essentially just a simpler
//		version of the UNIX executable object code format)
//	3. load the NOFF file into the Nachos file system
//		(if you haven't implemented the file system yet, you
//		don't need to do this last step)
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// All rights reserved.  See copyright.h for copyright notice and limitation 
// of liability and disclaimer of warranty provisions.

#include "copyright.h"
#include "system.h"
#include "addrspace.h"
#include "noff.h"
#ifdef HOST_SPARC
#include <strings.h>
#endif

//----------------------------------------------------------------------
// SwapHeader
// 	Do little endian to big endian conversion on the bytes in the 
//	object file header, in case the file was generated on a little
//	endian machine, and we're now running on a big endian machine.
//----------------------------------------------------------------------

static void SwapHeader(NoffHeader *noffH) {
	noffH->noffMagic = WordToHost(noffH->noffMagic);
	noffH->code.size = WordToHost(noffH->code.size);
	noffH->code.virtualAddr = WordToHost(noffH->code.virtualAddr);
	noffH->code.inFileAddr = WordToHost(noffH->code.inFileAddr);
	noffH->initData.size = WordToHost(noffH->initData.size);
	noffH->initData.virtualAddr = WordToHost(noffH->initData.virtualAddr);
	noffH->initData.inFileAddr = WordToHost(noffH->initData.inFileAddr);
	noffH->uninitData.size = WordToHost(noffH->uninitData.size);
	noffH->uninitData.virtualAddr = WordToHost(noffH->uninitData.virtualAddr);
	noffH->uninitData.inFileAddr = WordToHost(noffH->uninitData.inFileAddr);
}

//----------------------------------------------------------------------
// AddrSpace::AddrSpace
// 	Create an address space to run a user program.
//	Load the program from a file "executable", and set everything
//	up so that we can start executing user instructions.
//
//	Assumes that the object code file is in NOFF format.
//
//	First, set up the translation from program memory to physical 
//	memory.  For now, this is really simple (1:1), since we are
//	only uniprogramming, and we have a single unsegmented page table
//
//	"executable" is the file containing the object code to load into memory
//----------------------------------------------------------------------

AddrSpace::AddrSpace(OpenFile *executable,PCB* pcb) {
	this->pcb = pcb;

	NoffHeader noffH;
	unsigned int i, size;

	executable->ReadAt((char *) &noffH, sizeof(noffH), 0);
	if ((noffH.noffMagic != NOFFMAGIC) && (WordToHost(noffH.noffMagic)
			== NOFFMAGIC))
		SwapHeader(&noffH);
	ASSERT(noffH.noffMagic == NOFFMAGIC);
	// how big is address space?
	size = noffH.code.size + noffH.initData.size + noffH.uninitData.size
			+ UserStackSize; // we need to increase the size
	// to leave room for the stack
	numPages = divRoundUp(size, PageSize);
	size = numPages * PageSize;

	// to run anything too big --
	// at least until we have
	// virtual memory
	DEBUG('a', "Initializing address space, num pages %d, size %d\n", numPages,
			size);
	// first, set up the translation
	pageTable = new TranslationEntry[numPages];
	for (i = 0; i < numPages; i++) {
		pageTable[i].virtualPage = i;
		//use pages allocated by your memory manager for the physical page index
		pageTable[i].physicalPage = -1;
		pageTable[i].diskLoc = vmmanager->GetStoreLocation();
		char blank[PageSize];
		bzero(blank,PageSize);
		vmmanager->BackStore(blank,PageSize,pageTable[i].diskLoc);
		// zero out the address space of physical page
		// and the stack segment
		pageTable[i].valid = FALSE;
		pageTable[i].use = FALSE;
		pageTable[i].dirty = FALSE;
		pageTable[i].readOnly = FALSE; // if the code segment was entirely on
		// a separate page, we could set its
		// pages to be read-only
		printf("z %d: %d\n",pcb->pid,pageTable[i].diskLoc/PageSize);
		DiskPageInfo * info = vmmanager->GetDiskPageInfo(pageTable[i].diskLoc/PageSize);
		pageTable[i].space = this;
		info->Add(&pageTable[i]);
	}

	// then, copy in the code and data segments into memory
	if (noffH.code.size > 0) {
		DEBUG('a', "Initializing code segment, at 0x%x, size %d\n",
				noffH.code.virtualAddr, noffH.code.size);
		ReadFile(noffH.code.virtualAddr, executable, noffH.code.size,
				noffH.code.inFileAddr);
		//        executable->ReadAt(&(machine->mainMemory[noffH.code.virtualAddr]),
		//			noffH.code.size, noffH.code.inFileAddr);
	}
	if (noffH.initData.size > 0) {
		DEBUG('a', "Initializing data segment, at 0x%x, size %d\n",
				noffH.initData.virtualAddr, noffH.initData.size);
		ReadFile(noffH.initData.virtualAddr, executable, noffH.initData.size,
				noffH.initData.inFileAddr);
		//        executable->ReadAt(&(machine->mainMemory[noffH.initData.virtualAddr]),
		//			noffH.initData.size, noffH.initData.inFileAddr);
	}
	printf("Loaded Program: %d code | %d data | %d bss\n", noffH.code.size,
			noffH.initData.size, noffH.uninitData.size);
}

AddrSpace::AddrSpace() {
}//Do nothing

//----------------------------------------------------------------------
// AddrSpace::~AddrSpace
// 	Dealloate an address space.  Nothing for now!
//----------------------------------------------------------------------

AddrSpace::~AddrSpace() {
	if (this->IsValid()) {
		vmmanager->ClearSpace(this);
		delete [] pageTable;
		delete pcb;
	}
}

//----------------------------------------------------------------------
// AddrSpace::InitRegisters
// 	Set the initial values for the user-level register set.
//
// 	We write these directly into the "machine" registers, so
//	that we can immediately jump to user code.  Note that these
//	will be saved/restored into the currentThread->userRegisters
//	when this thread is context switched out.
//----------------------------------------------------------------------

void AddrSpace::InitRegisters() {
	int i;

	for (i = 0; i < NumTotalRegs; i++)
		machine->WriteRegister(i, 0);

	// Initial program counter -- must be location of "Start"
	machine->WriteRegister(PCReg, 0);

	// Need to also tell MIPS where next instruction is, because
	// of branch delay possibility
	machine->WriteRegister(NextPCReg, 4);

	// Set the stack register to the end of the address space, where we
	// allocated the stack; but subtract off a bit, to make sure we don't
	// accidentally reference off the end!
	machine->WriteRegister(StackReg, numPages * PageSize - 16);
	DEBUG('a', "Initializing stack register to %d\n", numPages * PageSize - 16);
}

//----------------------------------------------------------------------
// AddrSpace::SaveState
// 	On a context switch, save any machine state, specific
//	to this address space, that needs saving.
//
//	For now, nothing!
//----------------------------------------------------------------------

void AddrSpace::SaveState() {

}

//----------------------------------------------------------------------
// AddrSpace::RestoreState
// 	On a context switch, restore the machine state so that
//	this address space can run.
//
//      For now, tell the machine where to find the page table.
//----------------------------------------------------------------------

void AddrSpace::RestoreState() {
	machine->pageTable = pageTable;
	machine->pageTableSize = numPages;
}

//----------------------------------------------------------------------
// AddrSpace::Translate
// 	Convert a virtual address to a physical address. It does so by breaking
//	the virtual address into a page table index and an offset. It then looks
//	up the physical page in the page table entry given by the page table
//	index and obtains the final physical address by combining the physical
//	page address with the page offset. It might help to pass a pointer to
//	the space you would like the physical address to be stored in as a
//	paramter.
//
//	Return a boolean TRUE or FALSE depending on whether or not the virtual
//	address was valid.
//
//	"virtAddr" is the virtual address to be translated.
//	"physAddr" is the physical address to be stored.
//----------------------------------------------------------------------

bool AddrSpace::Translate(int virtAddr, int* physAddr) {
	int i;
	unsigned int vpn, offset;
	unsigned int pageFrame;
	TranslationEntry entry;
	DEBUG('a', "\tTranslate 0x%x: ", virtAddr);

	// we must have either a TLB or a page table, but not both!
	ASSERT(pageTable != NULL);

	// calculate the virtual page number, and offset within the page,
	// from the virtual address
	vpn = (unsigned) virtAddr / PageSize;
	offset = (unsigned) virtAddr % PageSize;
	if (vpn >= numPages) {
		DEBUG('a', "virtual page # %d too large for page table size %d!\n",
				virtAddr, numPages);
		return false;
	} else if (!pageTable[vpn].valid) {
		DEBUG('a', "virtual page # %d invalid %d!\n", virtAddr, numPages);
		return false;
	}
	entry = pageTable[vpn];
	pageFrame = entry.physicalPage;

	// if the pageFrame is too big, there is something really wrong!
	if (pageFrame >= NumPhysPages) {
		DEBUG('a', "*** frame %d > %d!\n", pageFrame, NumPhysPages);
		return false;
	}

	//store the physical address
	*physAddr = pageFrame * PageSize + offset;
	ASSERT((*physAddr >= 0));
	DEBUG('a', "phys addr = 0x%x\n", *physAddr);
	return true;
}

//----------------------------------------------------------------------
// AddrSpace::ReadFile
// 	Load the code and data segments into the translated memory, instead
//	of at position 0 like the code in the AddrSpace constructor already
//	does. This is needed not only for Exec() but for the initial startup
//	of the machine when executing any test program with virtual memory.
//
//	ReadFile tries to read "size" bytes from "file" at offset "fileAddr"
//	and then store them at virtual memory address beginning at "virtAddr".
//	Then, buffer user file reads in a disk buffer called diskBuffer
//	(declare it in system.h). All of user-level file I/O must go through
//	the diskBuffer. It will try to read as many bytes as possible. It will
//	also use the functions: File::ReadAt(buff,size,addr) and bcopy(src,dst,num)
//	as well as the memory locations at machine->mainMemory[physAddr].
//
//	Return the number of bytes actually read, but has
//	no side effects (except that Write modifies the file, of course).
//
//	"virtAddr" is the beginning virtual address of the bytes to be read.
//	"file" is the file where some bytes are from.
//	"size" is the size of bytes need to be read.
//	"fileAddr" is the file address.
//----------------------------------------------------------------------

int AddrSpace::ReadFile(int virtAddr, OpenFile *file, int size, int fileAddr) {
	char diskBuffer[size]; // store read bytes
	int availSize;
	// store the number of bytes actually read
	int actualSize = file->ReadAt(diskBuffer, size, fileAddr);
	size = actualSize;
	int bytesCopied = 0;
	int offset=0;
	//read bytes in the "file"
	while (size > 0) {
		int pageNum = virtAddr / PageSize;
		offset = virtAddr%PageSize;
		availSize = min(PageSize,size);
		vmmanager->BackStore(diskBuffer + bytesCopied, availSize,
				pageTable[pageNum].diskLoc+offset);
		size -= availSize;
		bytesCopied += availSize;
		virtAddr += availSize;
	}
	return actualSize;
}

//----------------------------------------------------------------------
// AddrSpace::Clone
//	Create a new address space that is an exact copy of the original.
//	Allocate additional physical memory for this copy, set up the page
//	tables properly for the new address space, and copy the data from
//	the old address space to the new.
//
//	Return the clone space
//----------------------------------------------------------------------

AddrSpace* AddrSpace::Clone(PCB *pcb) {
	//test if the SWAP file is enough

	AddrSpace* clone = new AddrSpace();
	clone->pcb = pcb;
	clone->numPages = this->numPages;
	clone->pageTable = new TranslationEntry[numPages];
	for (int i = 0; i < numPages; i++) {
		clone->pageTable[i].virtualPage = i;
		clone->pageTable[i].physicalPage = this->pageTable[i].physicalPage;
		clone->pageTable[i].valid = this->pageTable[i].valid;
		clone->pageTable[i].use = this->pageTable[i].use;
		clone->pageTable[i].dirty = this->pageTable[i].dirty;
		clone->pageTable[i].diskLoc = this->pageTable[i].diskLoc;
		clone->pageTable[i].readOnly = this->pageTable[i].readOnly = TRUE;
		printf("Z %d: %d\n",clone->pcb->pid,pageTable[i].diskLoc/PageSize);
		DiskPageInfo * info = vmmanager->GetDiskPageInfo(this->pageTable[i].diskLoc/PageSize);
		info->Add(&clone->pageTable[i]);
		clone->pageTable[i].space = clone;
	}
	return clone;
}

//----------------------------------------------------------------------
// AddrSpace::IsValid
// 	Return TRUE if numPages != -1
//----------------------------------------------------------------------

bool AddrSpace::IsValid() {
	return numPages != -1;
}

//----------------------------------------------------------------------
// AddrSpace::GetNumPages
// 	Return numPages
//----------------------------------------------------------------------

int AddrSpace::GetNumPages() {
	return numPages;
}

TranslationEntry* AddrSpace::GetTranslationEntry(int pageNum) {
	return &pageTable[pageNum];
}

int AddrSpace::GetTransEntryIndex(TranslationEntry* entry){
	for(int i = 0;i < numPages;i++)
		if(entry==pageTable+i)
			return i;
	return -1;
}
