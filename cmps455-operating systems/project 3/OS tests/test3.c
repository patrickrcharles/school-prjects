/* test3.c
 *	Simple program to test whether running a user program works.
 *	
 *	be careful to allocate a big enough stack to hold the automatics!
 */

#include "syscall.h"

void foo(int s)
{
  char buf[60];

  Write("in function Foo\n",16,ConsoleOutput);
  Exec("../test/matmult");
}


int main()
{
    Write("In test3.c now...\n",20,ConsoleOutput);
    Fork(foo);
    Yield();
    Exec("../test/sort");
	Exit(3);
}
