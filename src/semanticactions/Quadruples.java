/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package semanticactions;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

/**
 *
 * @author samiller
 */
public class Quadruples {
    
    private Vector<String[]> quadruple;
    private int nextQuad;
    
    public Quadruples(){
        this.nextQuad = 0;
        this.quadruple = new Vector<String[]>();
    }
    
    //Get contents of field in quad at location index
    public String getField(int quadIndex, int field){
        return this.quadruple.get(quadIndex)[field];
    }
    
    //Set contents of field in Quad at location index
    public void setField(int quadIndex, int fieldIndex, String field){
        this.quadruple.get(quadIndex)[fieldIndex] = field;
    }
    
    // Return index of next available quadruple
    public int getNextQuad(){
        return this.nextQuad;
    }
    
    //Increment the quad index variable
    public void incrementNextQuad(){
        this.nextQuad++;
    }
    
    //Return the quadruple at index
    public String[] getQuad(int index){
        return this.quadruple.get(index);
    }
    
    // Add a quadruple to the quadArray
    public void addQuad(String[] quad){
        this.quadruple.add(nextQuad, quad);
    }
    
    //Print the quadruple array
    public void print(String outputFile) throws FileNotFoundException, UnsupportedEncodingException{
        PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
        
        writer.println("CODE");
        for (int i = 0; i < quadruple.size(); i++){
            String[] fields = this.quadruple.get(i);
            int size = fields.length;
            String entry = fields[0];
            if (size > 1){
                entry = entry + " " + fields[1];
            for (int j = 2; j < size ; j++){
                entry = entry + ", " + fields[j];
            }
            }
           writer.println(i + ": " + entry);
        }
        writer.close();
    }
    
}
