#include "syscall.h"

void f(){
Exec("../test/matmult");
}

void
main()
{

  Fork(f);
}
