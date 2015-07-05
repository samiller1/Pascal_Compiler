/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symboltable;

import tokenizer.*;
import parser.*;

/**
 *
 * @author sandramiller
 */
public class ConstantEntry extends SymbolTableEntry{
    

    TokenType type;
    boolean error;

        public TokenType getType(){
            return this.type;
        }
	/*
        Constructor for ConstantEntry
        */
	public ConstantEntry(String Name, TokenType type) {
		super(Name);	
                this.type = type;
                this.error = false;
	} 

       public void setError(boolean bool){
             this.error = bool;
        }
         
	public boolean isConstant() { 
		return true; 
	}
	
	public void print () {
		
		System.out.println("Constant Entry:");
		System.out.println("   Name    : " + this.getName());
		System.out.println("   Type    : " + this.getType());
                                if (this.isRestricted()){
                    System.out.println("-- restricted --");
                }
		System.out.println();
	}
}
