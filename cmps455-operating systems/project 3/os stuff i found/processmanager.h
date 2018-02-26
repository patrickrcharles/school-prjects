// processmanager.h
//	Defines	PCB class -- Data structures to keep necessary information about a process,
//	Such as PID, parent PID, Thread*, and thread status.
//
//	Defines ProcessManager class -- like memory manager function, return an unused process
//	id, clear a process id respectively by using a bitmap, store PCBs in an array of PCB*.
//
//  Created on: 2009-12-9
//      Author: hyper

#ifndef PROCESSMANAGER_H_
#define PROCESSMANAGER_H_
#define MAX_PROCESS PageSize
#define MAX_FILE_OPEN 25
#include "bitmap.h"
#include "thread.h"
#include "synch.h"
#include "useropenfile.h"

// Definitions thread status
#define PRUNNING 2;
#define	PBLOCKED 3;
#define GOOD 0;
#define BAD 1;

// The following class defines a "PCB" -- an class of procese
// stored necessary information

class PCB {
public:
	int pid;	// pid information
	int parentPid;	// parent's pid information
	Thread* thread;	// thread information
	int status;	// the process status
	PCB();	// Initialize a PCB
	~PCB();	// De-allocate a PCB
	int Add(UserOpenFile uoFile);	// Add a UserOpenFile in PCB
	bool Remove(int fileId);	// Remove a UserOpenFile by file id
	UserOpenFile* Get(int fileId);	// Return the UserOpenFile by file id
private:
	UserOpenFile userOpenFileTable[MAX_FILE_OPEN];	// UserOpenFile in PCB
	BitMap bitMap;	// a BitMap for PCB methods
};

// The following class defines a "ProcessManager" -- an class to manage
// process like the memory manager for physical memory

class ProcessManager {
public:
	ProcessManager();	// Initialize a process manager
	~ProcessManager();	// De-allocate a process manager
	int GetPID();	// Return an unused process id
	void ClearPID(int pid);	// Clear a process id respectively
	void AddProcessAt(PCB *pcb,int pid);	// Add a process with exact pid
	void Join(int pid);		// keep track of who is waiting for who using a condition variable for each PCB
	void Broadcast(int pid);	// Broadcast the "pid" process in Exit
	int GetStatus(int pid); // Get process's status by pid
private:
	BitMap bitMap;	// a BitMap for process manager methods
	PCB** pcbs;	// an array of PCB* to store the PCBs
	Condition **conditions; //condition for PCBs join and exit
	Lock ** locks;	// Lock for join and exit
	int pcbStatus[MAX_PROCESS]; // array of PCB's status
	int joinProNum[MAX_PROCESS];	// array of
};
#endif /* PROCESSMANAGER_H_ */
