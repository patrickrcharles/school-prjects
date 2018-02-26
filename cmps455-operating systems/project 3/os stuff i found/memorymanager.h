// memorymanager.h
//	Methods defining in Memory Manager that will be used to facilitate contiguous virtual memory.
//	The amount of memory available to the user space is the same as the amount
//	of physical memory.
//
//	Memory Manager allocates the first clear page, takes the index of a page
//	and frees it, and get the number of clear pages.
//
//  Created on: 2009-12-7
//      Author: hyper

#ifndef MEMORYMANAGER_H_
#define MEMORYMANAGER_H_
#include "bitmap.h"

// The following class defines a "Memory Manager",
// which can allocate the first clear page, take the index of a page
//	and free it, and get the number of clear pages by using a bitmap (in code/userprog/bitmap.*)
//	with one bit per page to track allocation.

class MemoryManager {
private:
	BitMap bitMap;	// a BitMap for memory manager methods
public:
	MemoryManager();	// Initialize a Memory Manager
	~MemoryManager();	// De-allocate Memory Manager
	int GetPage();		// Return the first clear page
	void ClearPage(int i);	// Take the index "i" of a page
							// and free it
	int GetClearPages();	// Return the number of clear pages
};

#endif /* MEMORYMANAGER_H_ */
