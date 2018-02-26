/* test1.c
 *	Simple program to test whether running a user program works.
 *	
 *	Just do a "syscall" that shuts down the OS.
 *
 */

#include "syscall.h"

int
main()
{
    Write("try to exec matmult.c 1\n",26,ConsoleOutput);
    Exec("../test/matmult");
	Exit(0);
}
