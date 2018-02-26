/*
4. Parenthesis matching using ArrayStack
author: Patrick Charles prc9219
CMPS 450 - Dr Amini
assignment 3 - Object Oriented Programming
Fall 2015
*/

package parenthesisdemo;

import java.util.ArrayList;
import java.util.EmptyStackException;


public class ArrayStack<T> extends ArrayList<T>
{
   public ArrayList<T> arrayList;
   
   public ArrayStack()
   {
       arrayList = new ArrayList(10);
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
   public void push(T element)
   {
       arrayList.add(element);
   }
  
   public T pop()
   {
       if (!isEmpty())
           return arrayList.remove(size()-1);
       else
           throw new EmptyStackException();
   }
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
   
   @Override
   public int size()
   {
        return arrayList.size();
   }
   
   
public void CheckParenthesis(String string)
{
    if (string.isEmpty())
        System.out.println("parenthesis [ " + string + " ] is correct");

    ArrayStack<Character> stack = new ArrayStack();
    
    for (int i = 0; i < string.length(); i++)
    {
        char current = string.charAt(i);
        
        if (current == '{' || current == '(' || current == '[')
        {
            stack.push(current);
        }

        if (current == '}' || current == ')' || current == ']')
        {
            if (stack.isEmpty())
                System.out.println("parenthesis [ " + string + " ] is incorrect");

            char last = stack.peek();
            
            if (current == '}' && last == '{' || current == ')' && last 
                == '(' || current == ']' && last == '[')
                stack.pop();
            else 
                 System.out.println("parenthesis [ " + string + " ] is incorrect");
        }
    }
    
    if (stack.isEmpty()==true)
    {
        System.out.println("parenthesis [ " + string + " ] is correct");
    }
    else
    {
        System.out.println("parenthesis [ " + string + " ] is incorrect");
    }
}
}
