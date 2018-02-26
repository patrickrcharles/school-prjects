/*
3. Interface for ArrayStack and Chainstack
author: Patrick Charles prc9219
CMPS 450 - Dr Amini
assignment 3 - Object Oriented Programming
Fall 2015
*/

package interfacedemo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.EmptyStackException;
import java.util.NoSuchElementException;


public class ArrayStack<T> implements OurStack<T>
{
   private ArrayList<T> arrayList; //store data in array
   
   public ArrayStack()
   {
       arrayList = new ArrayList(20); //default constructor
   }
   
   public void  ArrayStack(int initialSize) 
   {
       arrayList = new ArrayList(initialSize); //default constructor of user size
   }
   
   @Override
   public boolean isEmpty()
   {
       return (arrayList.isEmpty()); //is stack empty
   }
   
    @Override
    public void push(T x)
    {
        arrayList.add(x); //add x to top of stack
    }

   @Override
   public T pop()
   {
       if (!isEmpty())
           return arrayList.remove(size()-1); //if not empty, remove top of stack
       else
           throw new EmptyStackException(); //empty, throw error
   }
   @Override
   public T peek()
   {
    if (!isEmpty())
        return arrayList.get(size()-1); //return data at top of stack
    else
          throw new EmptyStackException(); //if empty throw error
   }

    
   public T peek(int x) 
   {
    int y = ((arrayList.size()-x)-1); // y = element at indicated psotion in stack
    
    if (y < 0)
        throw new EmptyStackException(); // y < 0 , no such element
    else
         return arrayList.get(y); //return data at index y
    
   }
   
   public int size()
   {
        return arrayList.size(); //size of stack
   }   
   
   @Override
   public void printStack()
   {
       System.out.println(arrayList); //print contenst of array
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
            return result; //return current
        }
        
        @Override
        public void remove() 
        {
            throw new UnsupportedOperationException();//not implemented
        }
    }
}


