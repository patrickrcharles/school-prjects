#include "syscall.h"

char temp[1000];
int
main()
{   int i;
    Write("Test4 is running !!\n",20, ConsoleOutput);
    for(i=0;i<20;i++) Yield();
	Exit(0);
}
