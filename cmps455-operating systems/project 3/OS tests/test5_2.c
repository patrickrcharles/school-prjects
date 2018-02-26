/* test5_2.c */
/* --------- testing system calls : Exit, Exec, Fork */
/* --------- size of the program : 11 pages          */

#include "syscall.h"

int main()
{
  Write( "Test5: This is test 5_2\n", 24, ConsoleOutput );
  Exit(52);
}
