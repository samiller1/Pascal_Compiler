/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package errors;


import semanticactions.Etype;
import symboltable.*;
import tokenizer.*;
/**
 *
 * @author sandramiller
 */
public class SemanticError extends CompilerError{
    
    
    public SemanticError(String message){
        super(message);
    }
    
//   public static SemanticError ArraySubscripts(String predicted, Token current, int lineNumber, String currLine)
//   {
//      return new SemanticError(
//                              "Expecting array subscript of type integer. Found: " + current.getType()+ " ---> See line " + lineNumber + "." + '\n' + "<<<  " + currLine + "  >>>"); 
//      
//   }
    
    public static SemanticError UndeclaredVariable(Token current, int lineNumber, String currLine)
    {
        return new SemanticError("Undeclared variable detected: " + current.getValue() + " ---> See line " + lineNumber +  ". " + '\n' + "<<<  " + currLine + "  >>>");
    }
    
    
        public static SemanticError IllegalOperands(TokenType token1, TokenType token2, int lineNumber, String currLine)
    {
        return new SemanticError("Illegal operands. Expeciting operands of type integer, found:" + token1 + " and " + token2 + " ---> See line " + lineNumber +  ". " + '\n' + "<<<  " + currLine + "  >>>");
    }
        
        
   public static SemanticError IllegalVariableOffset(Token entry, int lineNumber, String currLine)
    {
        return new SemanticError("Illegal offset. Expeciting offset of type integer, found:" + entry.getValue() + " of type, " + entry.getType() + "  ---> See line " + lineNumber +  ". " + '\n' + "<<<  " + currLine + "  >>>");
    }
   
     public static SemanticError IllegalVariableOffset(SymbolTableEntry entry, int lineNumber, String currLine)
    {
        return new SemanticError("Illegal offset. Expeciting offset of type integer, found:" + entry + " of type, " + entry.getType() + "  ---> See line " + lineNumber +  ". " + '\n' + "<<<  " + currLine + "  >>>");
    }
   

   public static SemanticError IllegalEtype( int lineNumber, Etype expected, String currLine)
    {
        if (expected == Etype.RELATIONAL){
         return new SemanticError("Expecting relational operator, found arithmetic operator.   ---> See line " + lineNumber +  ". " + '\n' + "<<<  " + currLine + "  >>>"); 
        }
        else{
           return new SemanticError("Expecting arithmetic operator, found relational operator.   ---> See line " + lineNumber + "." + '\n' + "<<<  " + currLine + "  >>>"); 
        }
    }
 
   
   
    public static SemanticError IllegalFunction( int lineNumber, String expected, FunctionEntry actual, String currLine)
    {
        return new SemanticError("Illegal function call. Expecting current function " + expected + " , found: " + actual.getName() + "  ---> See line " + lineNumber+ '\n' + "<<<  " + currLine + "  >>>");
        
    }
    
    
 
       public static SemanticError IllegalProcedure( int lineNumber, SymbolTableEntry actual, String currLine)
    {
        return new SemanticError("Expecting prodedure, found: " + actual.getName() + "  ---> See line " + lineNumber + '\n' + "<<<  " + currLine + "  >>>");
        
    }
       
     
       public static SemanticError IllegalArray( int lineNumber, SymbolTableEntry actual, String currLine)
    {
        return new SemanticError("Expecting variable of type array, found: " + actual.getName() + " of type " + actual.getType() +  " .  ---> See line " + lineNumber + '\n' + "<<<  " + currLine + "  >>>");
        
    }
       
         public static SemanticError IllegalParameters( int lineNumber, int paramNum, String currLine)
    {
        return new SemanticError("Expecting " + paramNum + " parameters, found 0.  ---> See line " + lineNumber + '\n' + "<<<  " + currLine + "  >>>");
        
    }
         
                
         public static SemanticError IllegalParameters2( int lineNumber, int paramNum, String currLine)
    {
        return new SemanticError("Expecting " + paramNum + " parameters.  ---> See line " + lineNumber + '\n' + "<<<  " + currLine + "  >>>");
    }
        
         
         public static SemanticError IllegalResult( int lineNumber, SymbolTableEntry stackEntry, String currLine)
    {
        return new SemanticError("Expecting return value, found: " + stackEntry.getName() + " of type " + stackEntry.getType() + ".  ---> See line " + lineNumber + '\n' + "<<<  " + currLine + "  >>>");
    }

         public static SemanticError MismatchedParameters( int lineNumber, int expectedParamNum, int actualParamNum, String currLine)
    {
        return new SemanticError("Expecting " + expectedParamNum + " parameters, found " + actualParamNum + ".   ---> See line " + lineNumber + '\n' + "<<<  " + currLine + "  >>>");
        
    }

         public static SemanticError MismatchedParameterType( int lineNumber, TokenType expectedParamType, SymbolTableEntry actualParam, String currLine)
    {
        return new SemanticError("Expecting parameter of type " + expectedParamType + " , found " + actualParam.getName() + " of type " + actualParam.getType() +  ".   ---> See line " + lineNumber + '\n' + "<<<  " + currLine + "  >>>");
        
    }

         
         public static SemanticError MismatchedArrayParameter( int lineNumber, int expectedBound, int actualBound, String currLine)
    {
        return new SemanticError("Expecting array bound " + expectedBound + " , found " + actualBound + ".  ---> See line " + lineNumber + '\n' + "<<<  " + currLine + "  >>>");
        
    }

     public static SemanticError ExpectingFunction(int lineNumber, SymbolTableEntry found, String currLine)
    {
        return new SemanticError("Expecting function, found: " + found.getName() + " of type " + found.getType() + "  ---> See line " + lineNumber + '\n' + "<<<  " + currLine + "  >>>");
                
        
    }
}
