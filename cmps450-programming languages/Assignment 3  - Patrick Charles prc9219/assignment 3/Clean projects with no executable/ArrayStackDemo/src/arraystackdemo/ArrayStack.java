/*
1. ArrayStack
author: Patrick Charles prc9219
CMPS 450 - Dr Amini
assignment 3 - Object Oriented Programming
Fall 2015
*/
package arraystackdemo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.EmptyStackException;
import java.util.NoSuchElementException;

public class ArrayStack<T> 
{
   private ArrayList<T> arrayList; //stores data in array
   
   public ArrayStack()
   {
       arrayList = new ArrayList(20); // default constructor
   }
   
   public void  ArrayStack(int initialSize) 
   {
       arrayList = new ArrayList(initialSize); //constructor with user imputted size
   }
   
   public boolean isEmpty()
   {
       return (arrayList.isEmpty()); //true if stack is empty else false
   }
   public void push(T element)
   {
       arrayList.add(element); //add element to top of stack
   }
  
   public T pop()
   {
       if (!isEmpty())
           return arrayList.remove(size()-1); //remove top element
       else
           throw new EmptyStackException(); // if empty throw error
   }
   public T peek()
   {
    if (!isEmpty())
        return arrayList.get(size()-1); //return top of stack
    else
          throw new EmptyStackException(); //if empty throw error
   }
   
   public T peek(int x) 
   {
    int y = ((arrayList.size()-x)-1); //return element at specific index
    
    if (y < 0)
        throw new EmptyStackException(); //if stack empty throw error
    else
         return arrayList.get(y); //return data at index
    
   }
   
  
   public int size()
   {
        return arrayList.size(); //size of stack
   }   
   
   public void printStack()
   {
       System.out.println(arrayList); //print contents of stack
   }
   
   // Returns an Iterator to traverse the elements of this stack.
   
    public Iterator<T> iterator() 
    {
	return new ArrayStackIterator(); // default constructor
    }
   
    private class ArrayStackIterator implements Iterator<T>
    {
        private int index; // stores current position
        
        public ArrayStackIterator() 
        {
            index = arrayList.size()- 1; //current position is top of stack
        }
        @Override
        public boolean hasNext()
        {
            return index >= 0; // index > 0 means there is a next element
        }
        
        @Override
        public T next()
        {
            if (!hasNext()) // if empty throw error
            {
                throw new NoSuchElementException();
            }
            T result = arrayList.get(index); //result = current
            index--; // next element becomes current by decrementing index
            return result;  //return current
        }
        
        @Override
        public void remove() 
        {
            throw new UnsupportedOperationException(); //not implemented
        }
    }
}


