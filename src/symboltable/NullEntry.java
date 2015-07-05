/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symboltable;

/**
 *
 * @author sandramiller
 */
public class NullEntry extends SymbolTableEntry{

    public NullEntry(String name) {
        super(name);
    }
    
    
    public void print(){
        System.out.println("Null Entry");
        System.out.println("");
        
    }
}
