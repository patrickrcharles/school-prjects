// memorymanager.cc
//	Routines to manage a memory manager -- that will be used to facilitate
//	contiguous virtual memory. The amount of memory available to the user
//	space is the same as the amount of physical memory.
//
//	Memory Manager allocates the first clear page, takes the index of a page
//	and frees it, and get the number of clear pages.
//
//  Created on: 2009-12-7
//      Author: hyper

#include "memorymanager.h"
#include "machine.h"

//----------------------------------------------------------------------
// MemoryManager::MemoryManager
// 	Initialize a memory manager with initialize a bitmap with "NumPhysPages"
//	bits, so that every bit is clear.
//	it can be added somewhere on a list.
//----------------------------------------------------------------------

MemoryManager::MemoryManager():bitMap(NumPhysPages){

}

//----------------------------------------------------------------------
// MemoryManager::~MemoryManager
// 	De-allocate a memory manager.
//----------------------------------------------------------------------

MemoryManager::~MemoryManager() {
}

//----------------------------------------------------------------------
// MemoryManager::GetPage
// 	Return the first clear page
//----------------------------------------------------------------------

int MemoryManager::GetPage() {
	return bitMap.Find();
}

//----------------------------------------------------------------------
// MemoryManager::ClearPage
// 	Take the index "i" of a page and free it.
//
//	"i" is the index of a page to be cleared and freed.
//----------------------------------------------------------------------

void MemoryManager::ClearPage(int i) {
	bitMap.Clear(i);
}

//----------------------------------------------------------------------
// MemoryManager::GetClearPages
// 	Return the number of clear pages
//----------------------------------------------------------------------

int MemoryManager::GetClearPages() {
	return bitMap.NumClear();
}
