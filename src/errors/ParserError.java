/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package errors;
import tokenizer.*;

/**
 *
 * @author samiller
 */
public class ParserError extends CompilerError{
   
     public ParserError(String message)
   {
      super(message);
   }

     /**
      * predicted not equal error
      * @param predicted
      * @param current
      * @param lineNumber
      * @return 
      */
   public static ParserError PredictedNotEqual(TokenType predicted, Token current, int lineNumber, String currLine)
   {
      return new ParserError(
                              "Expecting: " + predicted + ", Found: " + current.getValue()+ " ---> See line " + lineNumber + "." + '\n' + "<<<  " + currLine + "  >>>");
      
   }
   
   /**
    * unexpected token error
    * @param current
    * @param lineNumber
    * @return 
    */
    public static ParserError UnexpectedToken(Token current, int lineNumber, String currLine)
   {
       if ((current.getType() == TokenType.ADDOP)||(current.getType() == TokenType.MULOP)|(current.getType() == TokenType.RELOP)){
        return new ParserError(
                              "Unexpected: " + current.getOpType() + " ---> See line " + lineNumber +"." + '\n' + "<<<  " + currLine + "  >>>");
                             }
       else{
           return new ParserError(
                   "Unexpected: " + current.getValue() + " ---> See line " + lineNumber +"." + '\n' + "<<<  " + currLine + "  >>>");
       }
   }
}
