// exception.cc
//	Entry point into the Nachos kernel from user programs.
//	There are two kinds of things that can cause control to
//	transfer back to here from user code:
//
//	syscall -- The user code explicitly requests to call a procedure
//	in the Nachos kernel.  Right now, the only function we support is
//	"Halt".
//
//	exceptions -- The user code does something that the CPU can't handle.
//	For instance, accessing memory that doesn't exist, arithmetic errors,
//	etc.
//
//	Interrupts (which can also cause control to transfer from user
//	code into the Nachos kernel) are handled elsewhere.

#include "copyright.h"
#include "system.h"
#include "syscall.h"
#include "list.h"
const int FILE_NAME_SIZE = 128;

//----------------------------------------------------------------------
// StringClone
//	Return copied string
//
//	"old" is string to copy.
//----------------------------------------------------------------------

char* StringClone(char* old) {
	char * newString = new char[FILE_NAME_SIZE];
	for (int i = 0; i < FILE_NAME_SIZE; i++) {
		newString[i] = old[i];
		if (old[i] == NULL)
			break;
	}
	return newString;
}

//----------------------------------------------------------------------
// AdjustPCRegs
//	Change register.
//----------------------------------------------------------------------

void AdjustPCRegs() {
	int pc = machine->ReadRegister(PCReg);
	machine->WriteRegister(PrevPCReg, pc);
	pc = machine->ReadRegister(NextPCReg);
	machine->WriteRegister(PCReg, pc);
	machine->WriteRegister(NextPCReg, pc + 4);
}

//----------------------------------------------------------------------
// ReadFileName
//	Read file name from memory to a string
//
//	"*fileName" is the point to file.
//----------------------------------------------------------------------

void ReadFileName(char *fileName) {
	int pos = 0;
	int i;
	int arg1 = machine->ReadRegister(4);
	do {
		ReadWrite(arg1,fileName+pos, 1, USER_WRITE);
		arg1++;
	}while(fileName[pos++] != 0);
	fileName[pos]=0;
}

//----------------------------------------------------------------------
// HaltHandler
//	Halt current thread.
//----------------------------------------------------------------------

void HaltHandler() {
	printf("System Call: %d invoked Halt\n", currentThread->space->pcb->pid);
	interrupt->Halt();
}

//----------------------------------------------------------------------
// ForkHelper
// 	ForkHelper is a dummy function that will copy back the machine registers,
//	PC and return registers saved from before the yield was performed.
//
//	"funcAddr" is the function address.
//----------------------------------------------------------------------

void ForkHelper(int funcAddr) {
	int* state = (int*)funcAddr;
	for(int i =0; i < NumTotalRegs;i++)
		 machine->WriteRegister(i,state[i]);
		delete[] state;
	currentThread->space->RestoreState(); // load page table register
	machine->Run(); // jump to the user progam
	ASSERT(FALSE); // machine->Run never returns;
}

//----------------------------------------------------------------------
// ForkHandler
// 	User-level thread operations: Fork. To allow multiple threads to run
//	within a user program.
//	Fork a thread to run a procedure in the *same* address space (virtualSpace)
// 	as the current thread.
//
//	Fork will create a new kernel thread and set it's AddrSpace to be a
//	duplicate of the CurrentThread's space. It sets then Yields().
//	The new thread runs a dummy function that will copy back the
//	machine registers, PC and return registers saved from before the yield
//	was performed.
//
//	"virtualSpace" is the CurrentThread's space.
//----------------------------------------------------------------------

void ForkHandler(int virtualSpace) {
	int currentPid = currentThread->space->pcb->pid;
	printf("System Call: %d invoked Fork\n", currentPid);
	PCB *pcb = new PCB();
	pcb->pid = pm->GetPID();
	AddrSpace *space = currentThread->space->Clone(pcb);
	if(space==NULL){
		printf("Process %d is unable to fork a new process\n",currentPid);
		return ;
	}

	Thread* thread = new Thread("Forked thread.");
	//set PCB
	pcb->thread = thread;
	pcb->status = PRUNNING;
	ASSERT(pcb->pid!=-1);
	pcb->parentPid = currentThread->space->pcb->pid; //parent ID
	thread->space = space;
	pm->AddProcessAt(pcb, pcb->pid);
	space->SaveState();
	int* currentState = new int[NumTotalRegs];
	for(int i =0; i < NumTotalRegs;i++)
		currentState[i] = machine->ReadRegister(i);
	currentState[PCReg] = virtualSpace;
	currentState[NextPCReg] = virtualSpace+4;
	printf("Process %d Fork %d: start at address 0x%X with %d pages memory\n",currentPid,pcb->pid,virtualSpace,space->GetNumPages());
	thread->Fork(ForkHelper, (int)currentState);
	currentThread->Yield();
}

//----------------------------------------------------------------------
// ExitHandler
// 	Exit takes "status" of currently executing process. The currently
//	executing process is terminated.
//
//	"status" is an integer status value as in Unix.
//----------------------------------------------------------------------

void ExitHandler(int status) {
	printf("System Call: %d invoked Exit\n", currentThread->space->pcb->pid);
	printf("Process %d exists with %d\n", currentThread->space->pcb->pid,status);
	int pid = currentThread->space->pcb->pid;
	currentThread->space->pcb->status = status;
	pm->Broadcast(pid);
	delete currentThread->space;
	currentThread->space = NULL;
	pm->ClearPID(pid);
	currentThread->Finish();
}

//----------------------------------------------------------------------
// YieldHandler
// 	User-level thread operations: Yield.  To allow multiple threads to run
//	within a user program. Yield the CPU to another runnable thread,
//	whether in this address space or not.
//
// 	Yield is used by a process executing in user mode to temporarily
//	relinquish the CPU to another process.
//----------------------------------------------------------------------

void YieldHandler() {
	printf("System Call: %d invoked Yield\n", currentThread->space->pcb->pid);
	currentThread->Yield();
}

//----------------------------------------------------------------------
// NewProcess
//	Create new process in register.
//----------------------------------------------------------------------

void NewProcess(int arg) {
	currentThread->space->InitRegisters(); // set the initial register values
	currentThread->space->SaveState();
	currentThread->space->RestoreState(); // load page table register
	machine->Run(); // jump to the user progam
}

//----------------------------------------------------------------------
// ExecHandler
//	Run the executable, stored in the Nachos file "filename".
//	Exec call spawns a new user-level thread (process), but creates a
//	new address space and begins executing a new program given by the
//	object code in the Nachos file whose name is supplied as an argument
//	to the call. It should return to the parent a SpaceId which can be
//	used to uniquely identify the newly created process.
//
//	Return the address space identifier.
//
//	"filename" is the name of the file stored in the Nachos
//----------------------------------------------------------------------

SpaceId ExecHandler(char * filename) {
	printf("System Call: %d invoked Exec\n", currentThread->space->pcb->pid);
	printf("Exec Program: %d loading %s\n", currentThread->space->pcb->pid,filename);
	int spaceId;
	OpenFile *executable = fileSystem->Open(filename);
	if (executable == NULL) {
		printf("Unable to open file %s\n", filename);
		return -1;
	}
	PCB* pcb = new PCB();
	pcb->pid = pm->GetPID();
	AddrSpace *space;
	space = new AddrSpace(executable,pcb);
	if(!space->IsValid()){
		delete space;
		return -1;
	}
	Thread *t = new Thread("forked process");
	spaceId = pcb->pid;
	ASSERT(pcb->pid!=-1);
	pcb->status = PRUNNING;
	pcb->parentPid = currentThread->space->pcb->pid;
	pcb->thread = t;
	t->space = space;
	pm->AddProcessAt(pcb, pcb->pid);
	delete executable; // close file
	t->Fork(NewProcess, NULL);
	currentThread->Yield();
	return spaceId;
}

//----------------------------------------------------------------------
// JoinHandler
//	Only return once the the user program "id" has finished.
//	Join call waits and returns only after a process with the specified ID
//	(supplied as an arguemnt to that call) has finished.
//
//	Return the exit status.
//
//	"id" is the specified ID of the process
//----------------------------------------------------------------------

int JoinHandler(int id) {
	printf("System Call: %d invoked Join\n", currentThread->space->pcb->pid);
	currentThread->space->pcb->status = PBLOCKED;
	if(pm->GetStatus(id)<0)
		return pm->GetStatus(id);
	pm->Join(id);
	currentThread->space->pcb->status = PRUNNING;
	return pm->GetStatus(id);
}

//----------------------------------------------------------------------
// CreateHandler
//	Create a Nachos file, with "fileName".
//	This is a straight forward call that should simply get the fileName
//	from user space then use fileSystem->Create(fileName,0) to create a
//	new instance of an OpenFile object.
//
//	"fileName" is the name of file to create.
//----------------------------------------------------------------------

void CreateHandler(char *fileName) {
	printf("System Call: %d invoked Create\n", currentThread->space->pcb->pid);
	bool success = fileSystem->Create(fileName, 0);
	ASSERT(success);

}

//----------------------------------------------------------------------
// OpenHandler
//	Open the Nachos file "fileName".
//	This function will use an OpenFile object created previously by
//	fileSystem->Open(fileName). Once you have this object, check to see
//	if it is already open by some other process in the global array of
//	SysOpenFiles. If so, increment the userOpens count. If not, store
//	the OpenFile object in the global table at the next open slot.
//
//	Then find an open slot in the currentThread's PCB's UserOpenFile array
//	and let UserOpenFile index to the global array of SysOpenFiles. Finally,
//	return the OpenFileID (index to PCB's UserOpenFile array) to the user.
//
//	Note that 0 and 1 are for console input and output, respectively.
//
//	Return an "OpenFileId" that can be used to read and write to the file.
//
//	"fileName" is the name of file to open.
//----------------------------------------------------------------------

OpenFileId OpenHandler(char *fileName) {
	printf("System Call: %d invoked Open\n", currentThread->space->pcb->pid);
	int index=0;
	SysOpenFile * sysFile = fileManager->Get(fileName, index);
	if (sysFile == NULL) {
		OpenFile * openFile = fileSystem->Open(fileName);
		if (openFile == NULL) {
			//printf("Unable to open file %s\n", fileName);
			//ASSERT(FALSE);
			return -1;
		}
		SysOpenFile sysFile;
		sysFile.file = openFile;
		sysFile.userOpens = 1;
		sysFile.fileName = StringClone(fileName);
		index = fileManager->Add(sysFile);
	}
	else{
		sysFile->userOpens++;
	}
	UserOpenFile userFile;
	userFile.fileIndex = index;
	userFile.offset = 0;
	OpenFileId openFileId = currentThread->space->pcb->Add(userFile);
	return openFileId;
}

//----------------------------------------------------------------------
// CloseHandler
//	Close the file, we're done reading and writing to it.
//
//	"id" is the id of the openFile
//----------------------------------------------------------------------

void CloseHandler(OpenFileId id) {
	printf("System Call: %d invoked Close\n", currentThread->space->pcb->pid);
	UserOpenFile* userFile =  currentThread->space->pcb->Get(id);
	if(userFile == NULL){
		return ;
	}
	SysOpenFile * sysFile=fileManager->Get(userFile->fileIndex);
	sysFile->closeOne();
	currentThread->space->pcb->Remove(id);
}

//----------------------------------------------------------------------
// ReadHandler
// 	Read "size" bytes from the open file into "buffer".
// 	If the open file isn't long enough, or if it is an I/O device, and
//	there aren't enough	characters to read, return whatever is available
//	(for I/O devices, you should always wait until you can return at
//	least one character).
//
//	Get the arguments from the user in the same way as WriteHandler().
//	If the OpenFileID == ConsoleInput (syscall.h), use a routine to read
//	into a buffer of size 'size+1' one character at a time using getchar().
//	Otherwise, grab a handle to the OpenFile object in the same way you
//	did for WriteHandler() and use OpenFileObject->ReadAt(myOwnBuffer,size,pos)
//	to put n characters into your buffer. pos is the position listed in
//	the UserOpenFile object that represents the place in the current file
//	you are writing to. With this method, you must explictly put a null byte
//	after the last character read. The number read is returned from ReadAt().
//
//	Write that buffer into the user's memory using the ReadWrite() function.
//	Finally, return the number of bytes written.
//
//	Return the number of bytes actually read
//
//	"bufferAddr" is the address of the buffer to read.
//	"size" is the size of bytes to read.
//	"id" is the id for the open file.
//----------------------------------------------------------------------

int ReadHandler(int bufferAddr, int size, OpenFileId id) {
	printf("System Call: %d invoked Read\n", currentThread->space->pcb->pid);
	char *buffer = new char[size + 1];
	int actualSize = size;
	if (id == ConsoleInput) {
		int count = 0;
		while (count < size) {
			buffer[count]=getchar();
			count++;
		}
	} else {
		UserOpenFile* userFile =  currentThread->space->pcb->Get(id);
		if(userFile == NULL)
			return 0;
		SysOpenFile * sysFile=fileManager->Get(userFile->fileIndex);
		actualSize = sysFile->file->ReadAt(buffer,size,userFile->offset);
		userFile->offset+=actualSize;
	}
	ReadWrite(bufferAddr,buffer,actualSize,USER_READ);
	delete[] buffer;
	return actualSize;
}

//----------------------------------------------------------------------
// WriteHandler
//  Write "size" bytes from "buffer" to the open file.
//	First you will need to get the arguments from the user by reading
//	registers 4-6. If the OpenFileID is == ConsoleOutput (syscall.h),
//	then simply printf(buffer). Otherwise, grab a handle to the OpenFile
//	object from the user's openfile list pointing to the global file list.
//	The user may not have opened that file before trying to write to it.
//
//	Fill up a buffer created of size 'size+1' using your ReadWrite() function.
//	Then simply call OpenFileObject->Write(myOwnBuffer, size)
//
//	"bufferAddr" is the address of the buffer to write.
//	"size" is the size of bytes to read.
//	"id" is the id for the open file.
//----------------------------------------------------------------------

void WriteHandler(int bufferAddr, int size, OpenFileId id) {
	printf("System Call: %d invoked Write\n", currentThread->space->pcb->pid);
	char* buffer = new char[size+1];
	if (id == ConsoleOutput) {
		ReadWrite(bufferAddr,buffer,size,USER_WRITE);
		buffer[size]=0;
		printf("%s",buffer);
	} else {
		buffer = new char[size];
		int writeSize = ReadWrite(bufferAddr,buffer,size,USER_WRITE);
		UserOpenFile* userFile =  currentThread->space->pcb->Get(id);
		if(userFile == NULL)
			return ;
		SysOpenFile * sysFile=fileManager->Get(userFile->fileIndex);
		int bytes = sysFile->file->WriteAt(buffer,size,userFile->offset);
		userFile->offset+=bytes;
	}
	delete[] buffer;
}

void PageFaultHandler() {
	int faultAddr = machine->ReadRegister(BadVAddrReg);
	vmmanager->ReplacePage(faultAddr);
	printf("L %d: %d -> %d\n",currentThread->space->pcb->pid,faultAddr/PageSize,currentThread->space->GetTranslationEntry(faultAddr/PageSize)->physicalPage);
}

void ReadOnlyHandler(){
	int faultAddr = machine->ReadRegister(BadVAddrReg);
	vmmanager->ChangeRepresentee(currentThread->space,faultAddr);
	TranslationEntry* entry = currentThread->space->GetTranslationEntry(faultAddr/PageSize);
	printf("D %d: %d\n%",currentThread->space->pcb->pid,entry->diskLoc/PageSize);
	entry->diskLoc = vmmanager->GetStoreLocation();
	int oriPhyPage = entry->physicalPage;
	vmmanager->BackStore(machine->mainMemory+entry->physicalPage*PageSize,PageSize,entry->diskLoc);
	vmmanager->GetDiskPageInfo(entry->diskLoc/PageSize)->Add(entry);
	entry->dirty = FALSE;
	entry->readOnly = FALSE;
	entry->valid = FALSE;
	vmmanager->ReplacePage(faultAddr);
}

//----------------------------------------------------------------------
// ExceptionHandler
// 	Entry point into the Nachos kernel.  Called when a user program
//	is executing, and either does a syscall, or generates an addressing
//	or arithmetic exception.
//
// 	For system calls, the following is the calling convention:
//
// 	system call code -- r2
//		arg1 -- r4
//		arg2 -- r5
//		arg3 -- r6
//		arg4 -- r7
//
//	The result of the system call, if any, must be put back into r2.
//
// And don't forget to increment the pc before returning. (Or else you'll
// loop making the same system call forever!
//
//	"which" is the kind of exception.  The list of possible exceptions
//	are in machine.h.
//----------------------------------------------------------------------

void ExceptionHandler(ExceptionType which) {
	int type = machine->ReadRegister(2);
	int arg1, arg2, arg3, result;
	char* fileName = new char[FILE_NAME_SIZE];// file name
	int pos = 0;
	if (which == SyscallException) {
		switch (type) {
		case SC_Halt:
			DEBUG('a', "Shutdown, initiated by user program.\n");
			HaltHandler();
			break;
		case SC_Fork:
			arg1 = machine->ReadRegister(4);
			ForkHandler(arg1);
			break;
		case SC_Exit:
			arg1 = machine->ReadRegister(4);
			ExitHandler(arg1);
			break;
		case SC_Exec:
			ReadFileName(fileName);
			result = ExecHandler(fileName);
			machine->WriteRegister(2, result);
			break;
		case SC_Yield:
			YieldHandler();
			break;
		case SC_Join:
			arg1 = machine->ReadRegister(4);
			result = JoinHandler(arg1);
			machine->WriteRegister(2, result);
			break;
		case SC_Create:
			ReadFileName(fileName);
			CreateHandler(fileName);
			break;
		case SC_Open:
			ReadFileName(fileName);
			StringClone(fileName);
			result = OpenHandler(fileName);
			machine->WriteRegister(2, result);
			break;
		case SC_Close:
			arg1 = machine->ReadRegister(4);
			CloseHandler(arg1);
			break;
		case SC_Read:
			arg1 = machine->ReadRegister(4);
			arg2 = machine->ReadRegister(5);
			arg3 = machine->ReadRegister(6);
			result = ReadHandler(arg1, arg2, arg3);
			machine->WriteRegister(2, result);
			break;
		case SC_Write:
			arg1 = machine->ReadRegister(4);
			arg2 = machine->ReadRegister(5);
			arg3 = machine->ReadRegister(6);
			WriteHandler(arg1, arg2, arg3);
			break;
		default:
			printf("No such function.\n");
		}
		AdjustPCRegs();
		delete[] fileName;
	} else if (which == PageFaultException) {
		PageFaultHandler();
	} else if(which== ReadOnlyException){
		ReadOnlyHandler();
	}else {
		printf("Unexpected user mode exception %d %d\n", which, type);
		ASSERT(FALSE);
	}
}
