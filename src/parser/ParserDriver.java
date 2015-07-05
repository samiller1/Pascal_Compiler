/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;
import errors.*;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import tokenizer.*;
import semanticactions.*;

/**
 *
 * @author samiller
 */
public class ParserDriver {

        private static String fileName;
        
    	public ParserDriver()
	{
		super();
	}

	protected void run() throws CompilerError, FileNotFoundException, UnsupportedEncodingException   
	{
            
            Parser parser = new Parser("src/parser/InputFiles/" + fileName);
            
            String[] fileSplit = fileName.split("\\.");
            String fileRoot = fileSplit[0];
            
            parser.setDebug(false);
            parser.parse();
   
           
           SemanticActions actions = parser.getSemanticActions();
           actions.getQuads().print("src/parser/OutputFiles/" + fileRoot + ".tvi");
           
       
           for (String error: actions.getErrors()){
            parser.getErrors().add(error);
        }
   
        
        
         if (parser.getErrors().size() > 0){
            System.out.println("=================");
            System.out.println(parser.getErrors().size() + " errors found.");
            System.out.println("=================");
            for (int i = 0; i < parser.getErrors().size(); i++){
                    System.out.println("Error " + (i + 1) + ": " + parser.getErrors().get(i));
                }
            if (!parser.eofReached()){
                System.out.println("____________________");
                System.out.println("End of file not reached. Please note there may be further errors.");
            }
            else{
                System.out.println("Entire file read.");
        }
         }
	}
    
    
    public static void main(String[] args) throws LexicalError, ParserError, CompilerError, FileNotFoundException, UnsupportedEncodingException
	{
            
            ///NOTE: INPUT FILE MUST BE SAVED IN SRC/PARSER/INPUTFILES. ALL OUTPUT FILES WILL BE CREATED AND SAVED IN SRC/PARSER/OUTPUTFILES.
            
		ParserDriver test = new ParserDriver();
                fileName = args[0];
                
               try{
		test.run();
               }
                catch(CompilerError ex){
                    System.out.println(ex);
                }
	}
}
