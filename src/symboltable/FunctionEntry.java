/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symboltable;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author sandramiller
 */
public class FunctionEntry extends SymbolTableEntry{
    
    int numberOfParameters;
    Stack<SymbolTableEntry> parameterInfo;
    SymbolTableEntry result;
    boolean error;

    
    public FunctionEntry(String name, int numberOfParameters, Stack<SymbolTableEntry> parameterInfo, SymbolTableEntry result){
        super(name);
        this.numberOfParameters = numberOfParameters;
        this.parameterInfo = parameterInfo;
        this.result = result;
        this.error = false;
    }
    
    public void setParmInfo(Stack<SymbolTableEntry> parmInfo){
        this.parameterInfo = parmInfo;
    }
        
    public void addParm(SymbolTableEntry param){
        this.parameterInfo.push(param);
    }
    
    public SymbolTableEntry getResult(){
        return this.result;
    }
    
    public void setError(boolean bool){
        this.error = bool;
    }
    
    public void setParms(int numberOfParameters){
        this.numberOfParameters = numberOfParameters;
    }
    
    
    public void setResult(SymbolTableEntry result){
        this.result = result;
    }
    
    
    public Stack<SymbolTableEntry> getParmInfo(){
        return this.parameterInfo;
    }
    
    public int getParameters(){
        return this.numberOfParameters;
    }
    	public void print () {
		System.out.println("Function Entry:");
		System.out.println("   Name    : " + this.getName());
                System.out.println("   Number of Parameters     : " + this.getParameters());
                                if (this.isRestricted()){
                    System.out.println("-- restricted --");
                }
                
                 if (!(this.parameterInfo.size() == 0)){
                    System.out.println("Parameter Info List:");
                    System.out.println(this.parameterInfo.toString());
                } 
		System.out.println();
	}
    
    
    
}
