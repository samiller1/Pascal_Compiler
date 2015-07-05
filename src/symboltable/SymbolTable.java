/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symboltable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author sandramiller
 */
public class SymbolTable {
   
    private Hashtable<String, SymbolTableEntry> table;
    
    public SymbolTable(int size){
        this.table = new Hashtable<String, SymbolTableEntry>(size);
    }
    
    public SymbolTableEntry lookup(String key){
        return this.table.get(key);
    }

    /*
    // Check symbol table to see if it already contains key,
    // if not, add new entry.
    */
    public void insert(SymbolTableEntry entry){
        if (!this.contains(entry)){
            this.table.put(entry.getName(), entry);  
        }
    }
    
    
    public void delete(SymbolTableEntry entry){
        if(this.contains(entry)){
            this.table.remove(entry.getName());
        }
    }
    
    public void clear(){
        this.table.clear();
    }
    
    public Set<String> keys(){
        return this.table.keySet();
    }
    
    public Collection<SymbolTableEntry> values(){
        return this.table.values();
    }
    
    public boolean contains(SymbolTableEntry entry){
        return this.table.keySet().contains(entry.getName());
    }
    
    public int size(){
        return this.table.size();
    }
    
    
    // Insert main, read, write into symbolTable
    public void installBuiltins(){
        this.insert(new ProcedureEntry("MAIN", 0, new Stack<SymbolTableEntry>()));
        this.insert(new ProcedureEntry("READ", 0, new Stack<SymbolTableEntry>()));
        this.insert(new ProcedureEntry("WRITE", 0, new Stack<SymbolTableEntry>()));
    }
    
    
    public void dumpTable(){
        for (SymbolTableEntry entry: this.table.values()){
            entry.print();
        }
        
        
        
        
    }
    
    
    
}
