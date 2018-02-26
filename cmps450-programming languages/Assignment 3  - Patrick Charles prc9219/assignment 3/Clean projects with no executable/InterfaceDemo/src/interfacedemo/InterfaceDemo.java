
package interfacedemo;

public class InterfaceDemo {
    
    public static void main(String[] args) 
    
    {
        OurStack arrayStack;
        OurStack chainStack;
        
        arrayStack = new ArrayStack();
        chainStack = new ChainStack();        
        System.out.println("ArrayStack interface demo : ");     
        
        System.out.println("push (10,20,30,40,50,60) to stack");
        arrayStack.push(10);
        arrayStack.push(20);
        arrayStack.push(30);
        arrayStack.push(40);
        arrayStack.push(50);
        arrayStack.push(60);
        
        System.out.println("print stack");
        arrayStack.printStack();
       
        System.out.println("pop stack : " + arrayStack.pop());
       
        System.out.println("get size of stack : " + arrayStack.size());
       
        System.out.println("print stack");
        arrayStack.printStack();
       
        System.out.println("is stack empty : " +arrayStack.isEmpty());
       
        System.out.println("peek stack : " + arrayStack.peek());
       
        System.out.println("ChainStack interface demo : ");     
        
        System.out.println("push (1,2,3,a,b) to stack");
        chainStack.push(1);
        chainStack.push(2);
        chainStack.push(3);
        chainStack.push("a");
        chainStack.push("b");
        
        System.out.println("print stack");
        chainStack.printStack();
       
        System.out.println("pop stack : " + chainStack.pop());
       
        System.out.println("get size of stack : " + chainStack.size());
       
        System.out.println("print stack");
        chainStack.printStack();
       
        System.out.println("is stack empty : " +chainStack.isEmpty());
       
        System.out.println("peek stack : " + chainStack.peek());
    }
    
}
