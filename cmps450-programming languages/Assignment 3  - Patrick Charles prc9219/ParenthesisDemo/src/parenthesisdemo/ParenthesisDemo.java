
package parenthesisdemo;

public class ParenthesisDemo {

   
    public static void main(String[] args) 
    {
        ArrayStack a = new ArrayStack();
        String s = "(((((a + b) + (c + d)))"; //incorrect
        String t = "a + (c + d)";  // correct
        String u = "((a + b) * (c - d))"; //correct
        String v = "((a + b)";  //incorrect
        
        a.CheckParenthesis(s);
        a.CheckParenthesis(t);
        a.CheckParenthesis(u);
        a.CheckParenthesis(v);
        
        
    }
}
