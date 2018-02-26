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
   private ArrayList<T> arrayList;
   
   public ArrayStack()
   {
       arrayList = new ArrayList(20);
   }
   
   public void  ArrayStack(int initialSize) 
   {
       arrayList = new ArrayList(initialSize);
   }
   
   @Override
   public boolean isEmpty()
   {
       return (arrayList.isEmpty());
   }
   
    @Override
    public void push(T x)
    {
        arrayList.add(x);
    }

   @Override
   public T pop()
   {
       if (!isEmpty())
           return arrayList.remove(size()-1);
       else
           throw new EmptyStackException();
   }
   @Override
   public T peek()
   {
    if (!isEmpty())
        return arrayList.get(size()-1);
    else
          throw new EmptyStackException();
   }

    
   public T peek(int x) 
   {
    int y = ((arrayList.size()-x)-1);
    
    if (y < 0)
        throw new EmptyStackException(); 
    else
         return arrayList.get(y);
    
   }
   
   public int size()
   {
        return arrayList.size();
   }   
   
   @Override
   public void printStack()
   {
       System.out.println(arrayList);
   }
   
   // Returns an Iterator to traverse the elements of this stack.
   
    public Iterator<T> iterator() 
    {
	return new ArrayStackIterator();
    }

   
    private class ArrayStackIterator implements Iterator<T>
    {
        private int index;
        
        public ArrayStackIterator() 
        {
            index = arrayList.size()- 1;
        }
        @Override
        public boolean hasNext()
        {
            return index >= 0;
        }
        
        @Override
        public T next()
        {
            if (!hasNext()) 
            {
                throw new NoSuchElementException();
            }
            T result = arrayList.get(index);
            index--;
            return result; 
        }
        
        @Override
        public void remove() 
        {
            throw new UnsupportedOperationException();
        }
    }
}


