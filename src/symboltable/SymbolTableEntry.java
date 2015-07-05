/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symboltable;

import tokenizer.*;
import parser.*;
import errors.*;
import java.util.ArrayList;
import java.util.Stack;
/**
 *
 * @author sandramiller
 */
public class SymbolTableEntry {
    
    
  private String name;
  private boolean restricted;
  private boolean error, result;

    public SymbolTableEntry(String name){
        this.name = name;
        this.restricted = false;
        this.error = false;
        this.result = true;
    }

    
    public boolean isRestricted(){
        return this.restricted;
    }
    
    public void setRestricted(){
        this.restricted = true;
    }
    
    public String getName(){
        return this.name;
    }
    
    public void makeResult(){
        this.result = true;
    }
    
    public boolean isFuncResult(){
        return this.result;
    }
    
    public boolean hasAddress(){
        return ((this instanceof ArrayEntry) || (this instanceof ConstantEntry) || (this instanceof VariableEntry));
    }
    
    public void setError(){
        this.error = true;
    }
    
    public boolean getError(){
        return false;
    }
    
    public TokenType getType(){
        if ((this instanceof VariableEntry)||(this instanceof ConstantEntry)){
            return this.getType();
        }
        else{
            return null;
        }
    }
    
    public void setResult(SymbolTableEntry entry){}
    public void setType(TokenType type){}
    public void setParms(int numberOfParameters){}
    public void addParm(SymbolTableEntry param){}
    public void print(){ }
        
    public Stack<SymbolTableEntry> getParmInfo(){
        return new Stack<SymbolTableEntry>();
    }
    
    public int getParameters(){
        return 0;
    }
    
    public int getAddress(){
        return 0;
    }
    
    public boolean isParm(){
        return false;
    }
    
    public SymbolTableEntry getResult(){
        return new NullEntry("null");
    }
    
        public void setParmInfo(Stack<SymbolTableEntry> parmInfo){
    }
}
