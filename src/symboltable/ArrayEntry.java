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
public class ArrayEntry extends SymbolTableEntry {
    
    int address, upperBound, lowerBound;
    TokenType type;
    boolean error, parm;
    
    
    /*
    Constructor for ArrayEntry
    */
    public ArrayEntry(String Name, int address, int upperBound, int lowerBound, TokenType type){
        super(Name);
        this.address = address;
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
        this.type = type;
        this.error = false;
        this.parm = false;
    }
    
    public int getLowerBound(){
        return this.lowerBound;
    }
    
    public int getUpperBound(){
        return this.upperBound;
    }
    
    public int getAddress(){
        return this.address;
    }
    
    public void setAddress(int address){
        this.address = address;
    }
    
    public boolean isArray(){
        return true;
    }
    
    public TokenType getType(){
        return this.type;
    }
    
    public void setType(TokenType type){
        this.type = type;
    }
    
    public void setError(boolean bool){
        this.error = bool;
    }
    
    public void print () {
		System.out.println("Array Entry:");
		System.out.println("   Name    : " + this.getName());
		System.out.println("   Type    : " + this.getType());
		System.out.println("   Address      : " + this.getAddress());
                System.out.println("   Upper Bound      : " + this.getUpperBound());
                System.out.println("   Lower Bound      : " + this.getLowerBound());
                if (this.isRestricted()){
                    System.out.println("-- restricted --");
                }
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
