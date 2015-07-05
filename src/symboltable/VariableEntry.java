/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symboltable;


import tokenizer.*;
import parser.*;
import errors.*;

/**
 *
 * @author sandramiller
 */
public class VariableEntry extends SymbolTableEntry {
    
	int address;
        TokenType type;
        boolean error, parm;


	public int getAddress() {
		return address;
	}
        
        public void setError(boolean bool){
            this.error = bool;
        }
            
        public TokenType getType(){
            return this.type;
        }

        public void setType(TokenType type){
            this.type = type;
        }
	public void setAddress(int address) {
		this.address = address;
	}


	public VariableEntry(String name, TokenType type, int address) {
		super(name);	
                this.type = type;
                this.address = address;
                this.parm = false;
	} 

	public boolean isVariable() { 
		return true; 
	}
        
        public boolean isError(){
            return this.error;
        }
	
	public void print () {
		
		System.out.println("Variable Entry:");
		System.out.println("   Name    : " + this.getName());
		System.out.println("   Type    : " + this.getType());
		System.out.println("   Address      : " + this.getAddress());
                                if (this.isRestricted()){
                    System.out.println("-- restricted --");
                }
                System.out.println("   Is Param:  " + this.isParm());
                System.out.println("   Error Flag:  " + this.error);
		System.out.println();
	}
        
        public void setParameter (boolean parm) {
		this.parm = parm;
	}
	
	public void setParm() {
		this.parm = true;
	} 
        
        public boolean isParm(){
            return this.parm;
        }
       
	
}