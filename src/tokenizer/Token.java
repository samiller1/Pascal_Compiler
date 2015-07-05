/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tokenizer;

/**
 *
 * @author Sandra Miller
 * @param <V>
 */
public class Token<V> {
    
    private TokenType type;
    //Generic type allows for integer and string values 
    private V value;
    
    /*
     * Token constructor
     */
    public Token(TokenType type,V value){
        this.type = type;
        this.value = value;
        
    }

    /*
     * Type accessor function
     */
    public TokenType getType(){
        return this.type;
    }
    
    /*
     * Value accessor function
     */
    public V getValue(){
        return this.value;
    }
    
    //Return string representation of op type for mulop, addop and relop
    public String getOpType(){
        int opVal = (Integer) getValue();
        String opType = null;
        if (getType() == TokenType.RELOP){
                if (opVal == 1) {opType = "EQUAL";}
                else if (opVal == 2) {opType =  "NOT EQUAL";}
                else if (opVal == 3) {opType =  "LESS THAN";}
                else if (opVal == 4) {opType =  "GREATER THAN";}
                else if (opVal == 5) {opType =  "LESS THAN OR EQUAL";}
                else if (opVal == 6) {opType =  "GREATER THAN OR EQUAL";}           

        }
        else if (getType() == TokenType.ADDOP){

             if (opVal == 1){opType =  "ADDITION";}
             else if (opVal ==2){opType =  "SUBTRACTION";}
             else if (opVal == 3){opType =  "BOOLEAN OR";}       

        }
         else if (getType() == TokenType.MULOP){
                if (opVal == 1) {opType = "MULTIPLICATION";}
                else if (opVal == 2) {opType =  "DIVISION";}
                else if (opVal == 3) {opType =  "INTEGER DIVISION";}
                else if (opVal == 4) {opType =  "MODULO";}
                else if (opVal == 5) {opType =  "BOOLEAN AND";} 
        }
        return opType;
    }
    
    
    
        //Return string representation of op type for mulop, addop and relop
    public String getTVIOpType(){
        int opVal = (Integer) getValue();
        String opType = null;
      
        if (getType() == TokenType.RELOP){
                if (opVal == 1) {opType = "beq";}
                else if (opVal == 2) {opType =  "bne";}
                else if (opVal == 3) {opType =  "blt";}
                else if (opVal == 4) {opType =  "bgt";}
                else if (opVal == 5) {opType =  "ble";}
                else if (opVal == 6) {opType =  "bge";}           

        }   
        
        
        else  if (getType() == TokenType.ADDOP){

             if (opVal == 1){opType =  "add";}
             else if (opVal ==2){opType =  "sub";}
             //else if (opVal == 3){opType =  "BOOLEAN OR";}       

        }
         else if (getType() == TokenType.MULOP){
                if (opVal == 1) {opType = "mul";}
                else if (opVal == 2) {opType =  "div";}
                else if (opVal == 3) {opType =  "div";}
                else if (opVal == 4) {opType =  "mod";}
                //else if (opVal == 5) {opType =  "BOOLEAN AND";} 
        }
        return opType;
    }
    

}


