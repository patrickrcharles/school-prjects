#include "syscall.h"

int
main()
{
   
    int i ;
    
    Write("Test4: First write!\n", 253, ConsoleOutput);
    
    for(i=0;i<6;i++)
	Exec("test4_1");  
	Exit(4);
}
