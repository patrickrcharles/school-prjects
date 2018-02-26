/*
3. Interface for ArrayStack and Chainstack
author: Patrick Charles prc9219
CMPS 450 - Dr Amini
assignment 3 - Object Oriented Programming
Fall 2015
*/

package interfacedemo;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ChainStack<T> implements OurStack<T>
{
    private int size;
    private Node top;
    
    private class Node 
    {
        private T data;
        private Node next;
    }

    public void ChainStack()
    {
        top = null;
        size = 0;  
    }

    public boolean isEmpty()
    {
        return top == null;
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public T pop()
    {
        if (top == null) 
            throw new NoSuchElementException();
        T data1 = top.data;
        top = top.next;
        size--;
        return data1;
}

    @Override
    public T peek()
{
    if (top == null) 
        throw new NoSuchElementException();
    return top.data;
    
}
    @Override
    public void push(T x)
    {
        this.add(x);
    }
    
    @Override
    public void printStack()
    {
        if (!isEmpty())
        {
            Iterator it = iterator();
            while (it.hasNext())
            System.out.println(it.next()); 
        }
    }
    
    public ChainStack<T> add(T x)
    {
        Node current = top;
        top = new Node();
        top.data = x;
        top.next = current;
        size++;
        return this;
    }   

    public Iterator<T> iterator()  
    { 
        return new ListIterator();
    }  

    // an iterator, doesn't implement remove() since it's optional
    private class ListIterator implements Iterator<T> 
    {
        private Node current;
    
    ListIterator()
    {
        current = top;
    }
    
    @Override
    public boolean hasNext()  
    {
        return current != null;
    }
    
    @Override
    public void remove()      
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public T next() 
    {
        if(!hasNext()){
            throw new NoSuchElementException();}
        T item = current.data;
        current = current.next; 
        return item;
    }
}

}


