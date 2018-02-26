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
    private int size; //size of stack
    private Node top; // top of stack
    
    private class Node 
    {
        private T data; //data in node
        private Node next; //link to next node
    }

    public void ChainStack()
    {
        top = null;   //default constructor for empty stack
        size = 0;   // stack empty
    }

    public boolean isEmpty()
    {
        return top == null; //is stack empty
    } 

    @Override
    public int size()
    {
        return size; //size of stack
    }

    @Override
    public T pop()
    {
        if (top == null) //if empty, throw error
            throw new NoSuchElementException();
        T data1 = top.data; //clone top data
        top = top.next;  //next is the new top of stack
        size--; //reduce stack size to indicate top has been removed
        return data1; //return data that was removed from stack
}

    @Override
    public T peek()
{
    if (top == null) //if empty, throw error
        throw new NoSuchElementException();
    return top.data; //data at top of stack
    
}
    @Override
    public void push(T x)
    {
        this.add(x); //add data to node at top of stack
    }
    
    @Override
    public void printStack()
    { //print using iterator
        if (!isEmpty())
        {
            Iterator it = iterator(); //new iterator object
            while (it.hasNext()) //while stack is not empty
            System.out.println(it.next()); //print contents
        }
    }
    
    public ChainStack<T> add(T x)
    {
        Node current = top; //clone top node
        top = new Node();  //new node to put at top of stack
        top.data = x;   // x is added to new top node
        top.next = current; //next updated to previous top
        size++; //increase size of stack
        return this; //return node
    }   

    public Iterator<T> iterator()  
    { 
        return new ListIterator(); //default constructor
    }  

    // an iterator, doesn't implement remove() since it's optional
    private class ListIterator implements Iterator<T> 
    {
        private Node current; // current node
    
    ListIterator()
    {
        current = top; //start at top of stack
    }
    
    @Override
    public boolean hasNext()  
    {
        return current != null; // is there another node
    }
    
    @Override
    public void remove()      
    {
        throw new UnsupportedOperationException(); //not implemented
    }
    
    @Override
    public T next() 
    {
        if(!hasNext()){
            throw new NoSuchElementException();}
        T item = current.data; //clone current data
        current = current.next; //current is equal to next
        return item; //return current data
    }
}

}


