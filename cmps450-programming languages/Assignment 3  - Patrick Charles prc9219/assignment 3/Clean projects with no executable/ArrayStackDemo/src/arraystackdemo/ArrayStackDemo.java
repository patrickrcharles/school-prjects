
package arraystackdemo;

import java.util.Iterator;

public class ArrayStackDemo {

    public static void main(String[] args) 
    {
       ArrayStack a = new ArrayStack();
       System.out.println("push (10,20,30,40,50,60) to stack");
       a.push(10);
       a.push(20);
       a.push(30);
       a.push(40);
       a.push(50);
       a.push(60);
        
        System.out.println("print stack");
        a.printStack();
       
        System.out.println("pop stack : " + a.pop());
       
        System.out.println("get size of stack : " + a.size());
       
        System.out.println("print stack");
        a.printStack();
       
        System.out.println("is stack empty : " +a.isEmpty());
       
        System.out.println("peek stack : " + a.peek());
       
       
       System.out.println("print array stack using iterator");
       if (!a.isEmpty())
       {
            Iterator iterator = a.iterator();
            while (iterator.hasNext())
            {
            System.out.println(iterator.next()); 
            }
       }
    }   
}
