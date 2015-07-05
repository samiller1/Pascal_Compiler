/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symboltable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

/**
 *
 * @author sandramiller
 */
public class ProcedureEntry extends SymbolTableEntry{
    
    int numberOfParameters;
    Stack<SymbolTableEntry> parameterInfo;
    boolean error;
    
    public ProcedureEntry(String name, int numberOfParameters, Stack<SymbolTableEntry> parameterInfo){
        super(name);
        this.numberOfParameters = numberOfParameters;
        this.parameterInfo = parameterInfo;
        this.error = false;
    }
    
    public void addParm(SymbolTableEntry param){
        this.parameterInfo.push(param);
    }
        public void setParmInfo(Stack<SymbolTableEntry> parmInfo){
        this.parameterInfo = parmInfo;
    }
    
      public void setParms(int numberOfParameters){
        this.numberOfParameters = numberOfParameters;
    }
  
    public void setError(boolean bool){
        this.error = bool;
    }
    
    public int getParameters(){
        return this.numberOfParameters;
    }
    
        
    public Stack<SymbolTableEntry> getParmInfo(){
        return this.parameterInfo;
    }
    
    	public void print () {
		
		System.out.println("Procedure Entry:");
		System.out.println("   Name    : " + this.getName());
		System.out.println("   Number of Parameters    : " + this.getParameters());
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
