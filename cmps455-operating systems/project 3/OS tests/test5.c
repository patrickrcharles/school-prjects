#include "syscall.h"

void test_function1()
{
  int i=0;
  char data1[1000];

  OpenFileId OutFid, InFid;

//  Write("# FORKED 5-1 !!\n", 20, ConsoleOutput);
  
  /* maintain this function in memory */
  for( i=0 ; i < 10 ; ++i ){
	 Yield();
	data1[i*10]=0;
  }
}
void test_function2()
{
  int i=0;
  char data2[1500];

  OpenFileId OutFid, InFid;

//  Write("# FORKED 5-2 !!\n", 20, ConsoleOutput);
  
  /* maintain this function in memory */
  for( i=0 ; i < 10 ; ++i ){
	 Yield();
	data2[i*50]=0;
  }
}
void test_function3()
{
  int i=0;
  char data3[2000];

  OpenFileId OutFid, InFid;

//  Write("# FORKED 5-2 !!\n", 20, ConsoleOutput);
  
  /* maintain this function in memory */
  for( i=0 ; i < 10 ; ++i ){
	 Yield();
	data3[i*100]=0;
  }
}

int
main()
{
    
//    Write("Test5: First write!\n", 253, ConsoleOutput);
    
    Fork(test_function1);
    Yield(); 
    Fork(test_function2);
    Yield(); 
    Fork(test_function3);
    Yield(); 
    Exit(0);  
 }
