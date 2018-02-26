// addrspace.h 
//	Data structures to keep track of executing user programs 
//	(address spaces).
//
//	For now, we don't keep any information about address spaces.
//	The user level CPU state is saved and restored in the thread
//	executing the user program (see thread.h).
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// All rights reserved.  See copyright.h for copyright notice and limitation 
// of liability and disclaimer of warranty provisions.

#ifndef ADDRSPACE_H
#define ADDRSPACE_H

#include "copyright.h"
#include "filesys.h"

#define UserStackSize		2048 	// increase this as necessary!
class PCB;
class AddrSpace {
public:
	AddrSpace(OpenFile *executable,PCB *pcb); // Create an address space,
	AddrSpace();
	// initializing it with the program
	// stored in the file "executable"
	~AddrSpace(); // De-allocate an address space

	void InitRegisters(); // Initialize user-level CPU registers,
	// before jumping to user code

	void SaveState(); // Save/restore address space-specific
	void RestoreState(); // info on a context switch
	bool Translate(int virtAddr, int* physAddr); //converts a virtual address to a physical address
	int ReadFile(int virtAddr, OpenFile* file, int size,
			int fileAddr);	// Loads the code and data segments into the translated memory
	bool IsValid();	// Return TRUE if numPages != -1
	AddrSpace* Clone(PCB *pcb);	// Create a new address space, an exact copy of the original
	int GetNumPages();	// Return numPages
	TranslationEntry* GetTranslationEntry(int pageNum);
	int GetTransEntryIndex(TranslationEntry* entry);
public :
	PCB *pcb;	// store information of the process
private:
	TranslationEntry *pageTable; // Assume linear page table translation
	// for now!
	unsigned int numPages; // Number of pages in the virtual
	// address space
//	 int userRegisters[NumTotalRegs];	// user-level CPU register state
};

#endif // ADDRSPACE_H
