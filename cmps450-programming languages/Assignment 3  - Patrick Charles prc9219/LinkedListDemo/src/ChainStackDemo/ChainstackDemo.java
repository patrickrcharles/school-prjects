
package ChainStackDemo;

public class ChainstackDemo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        ChainStack a = new ChainStack();
        System.out.println("push (1,2,3,a,b) to stack");
        a.push(1);
        a.push(2);
        a.push(3);
        a.push("a");
        a.push("b");
        
        System.out.println("print stack");
        a.printStack();
       
        System.out.println("pop stack : " + a.pop());
       
        System.out.println("get size of stack : " + a.getSize());
       
        System.out.println("print stack");
        a.printStack();
       
        System.out.println("is stack empty : " +a.isEmpty());
       
        System.out.println("peek stack : " + a.peek());
       
       }
       
    
}
