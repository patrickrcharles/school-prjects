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


public class ArrayStack<T> 
{
   public ArrayList<T> arrayList; //store data in arrayList object
   
   public ArrayStack()
   {
       arrayList = new ArrayList(10); //default constructor
   }
   
   public void  ArrayStack(int initialSize) 
   {
       arrayList = new ArrayList(initialSize); //default constructor of user size
   }
   
   public boolean isEmpty()
   {
       return (arrayList.isEmpty()); //is stack empty
   }
   public void push(T element)
   {
       arrayList.add(element); //add x to top of stack
   }
  
   public T pop()
   {
       if (!isEmpty())
           return arrayList.remove(size()-1); //if not empty, remove top of stack
       else
           throw new EmptyStackException(); //empty, throw error
   }
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
        throw new EmptyStackException();  // y < 0 , no such element
    else
         return arrayList.get(y); //return data at index y
    
   }
   
   public int size()
   {
        return arrayList.size(); //size of stack
   }
   
   
public void CheckParenthesis(String string)
{
    //if empty , empty string
    if (string.isEmpty())
        System.out.println("nothing was entered");

    ArrayStack<Character> stack = new ArrayStack(); //stack of characters
    
    // if left parenthesis push to stack, check each character for matching 
    // right parenthesis
    for (int i = 0; i < string.length(); i++)  //parse each char in stirng
    {
        char current = string.charAt(i); //store current char
        
        if (current == '{' || current == '(' || current == '[')
        {
            stack.push(current); //if left parenthesis, push to stack
        }

        //if right parenthesis
        if (current == '}' || current == ')' || current == ']')
        {   //if right parenthesis and stack is empty, incorrect
            if (stack.isEmpty()) 
                System.out.println("parenthesis [ " + string + " ] is incorrect");

            char last = stack.peek(); //last char = to top of stack
            
            // if top of stack is parenthesis that matches with current one, 
            // remove top of stack 
            if (current == '}' && last == '{' || current == ')' && last 
                == '(' || current == ']' && last == '[')
                stack.pop();
            //else is incorrect
            else 
                 System.out.println("parenthesis [ " + string + " ] is incorrect");
        }
    }
    
    if (stack.isEmpty()== true) //if stack empty, all left/right parenthesis have
        //been matched
    {
        System.out.println("parenthesis [ " + string + " ] is correct");
    }
    //if stack not empty. parenthesis are incorrect
    else
    {
        System.out.println("parenthesis [ " + string + " ] is incorrect");
    }
}
}
