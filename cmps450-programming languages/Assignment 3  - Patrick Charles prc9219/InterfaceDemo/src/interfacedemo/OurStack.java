/*
3. Interface for ArrayStack and Chainstack
author: Patrick Charles prc9219
CMPS 450 - Dr Amini
assignment 3 - Object Oriented Programming
Fall 2015
*/

package interfacedemo;

public interface OurStack<T>
{
  public boolean isEmpty();  // Return whether the stack is empty.

  public void push(T x);  // Push x onto the stack.

  public T peek();    // Return the object at the top of the stack.

  public T pop(); // Remove and return the object at the top of the stack.
  
  public void printStack(); //Print current contents of the stack
  
  public int size();
  
}
