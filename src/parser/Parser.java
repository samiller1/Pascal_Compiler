/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;
import errors.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;
import tokenizer.*;
import semanticactions.*;

/**
 *
 * @author samiller
 */
public class Parser {
    
    
    private Stack parseStack;
    private Tokenizer tokenizer;
    private Object predicted;
    private Token currToken;
    private int[][] parseTable;
    private RHSTable rhsTable;
    private boolean debug, errorFound, eofReached;
    private boolean irrecoverable;
    private int lineNumber, errorCount;
    private int parseHeight = 35;
    private int parseWidth = 38;
    private int errorVal = 999;
    private ArrayList<String> errors;
    private SemanticActions actions;
    
/**
 * Constructor for class parser.
 * @param file
 * @throws LexicalError 
 */
    public Parser(String file) throws LexicalError{
        //initialize empty stack
        this.parseStack = new Stack();
        this.tokenizer = new Tokenizer(file);
        
        //push EOF and start symbol
        Token eofToken = new Token<>(TokenType.ENDOFFILE, "ENDOFFILE");
        this.parseStack.push(eofToken);
        this.parseStack.push(NonTerminal.Goal);

        this.currToken = this.tokenizer.getNextToken();
        this.lineNumber = this.tokenizer.charStream.lineNumber();
        this.predicted = null;
        
        //Initialize right-hand-side table
        this.rhsTable = new RHSTable();
        
        //Initialize parse Table
        this.parseTable = new int[parseHeight][parseWidth];
        
        //Toggle dumpStack function
        this.debug = false;
        this.eofReached = true;
        
        //Initialize to no errors found yet
        this.errorFound = false;
        this.errorCount = 0;
        this.errors = new ArrayList<String>();
        
        //Initialize parse table
        this.initParseTable();     
        
        this.actions = new SemanticActions();
        this.actions.setDebug(false);
        
        this.irrecoverable = false;
    }

    
    /**
     * Parse input, collecting next input token from tokenizer.
     * @throws CompilerError 
     */
    public void parse() throws CompilerError{
        Token prevToken = null;

        while (!this.parseStack.empty()){
            //get top symbol off the parse stack
            this.predicted = this.parseStack.pop();

            if (!irrecoverable){
            //if top symbol is a semantic action, execute the appropriate action
            if (this.predicted instanceof SemanticAction){
                SemanticAction action = (SemanticAction) this.predicted;
                try{    
                actions.Execute(action,prevToken, this.lineNumber, this.tokenizer.charStream.getCurrentLine());
                }
                //Irrecoverable error found.
                catch(SemanticError ex){
                    this.errors.add(ex.toString());
                    this.irrecoverable = true;
                }
                
                if (this.debug){
                    System.out.println("Current semantic stack:");
                    actions.dumpStack();
                    System.out.println("-----------------------");
                }
        }
            }
            
            //Predicted is a terminal/token -- TokenType is class used in RHSTable entries
            if (this.predicted instanceof TokenType){

                //current token matches predicted, get next token
                if (this.predicted == this.currToken.getType()){
                    this.lineNumber = this.tokenizer.charStream.lineNumber();
                    
                    //token matched, store token for semantic action purposes 
                    prevToken = this.currToken;
                    
                    this.currToken = tokenizer.getNextToken();
                }
                
                //else, tokens do not match, error recovery
                else {
                    try{
                 throw ParserError.PredictedNotEqual((TokenType) this.predicted, this.currToken, this.lineNumber, this.tokenizer.charStream.getCurrentLine());
                    }
                    catch(ParserError ex){
                        this.recover(ex);
                    }
                }
            }
            
         //Else predicted is a non-terminal
        if (this.predicted instanceof NonTerminal){
            
                //Cast predicted as nonterminal
                NonTerminal pred1 = (NonTerminal) this.predicted;
                int predIndex = pred1.getIndex();
                int currIndex = this.currToken.getType().getIndex();
                int parseTableVal = this.parseTable[currIndex][predIndex];
                
                //Parse Table entry is an error
                if (parseTableVal == errorVal){
                 try{
                    throw ParserError.UnexpectedToken(this.currToken, this.lineNumber, this.tokenizer.charStream.getCurrentLine());
                    }
                   catch(ParserError ex){
                       this.recover(ex);
                    }
                }
                
                //Parse table entry is positive, else production is empty string, ignore
                else{
                    if (parseTableVal > 0){
                    Object[] rules = this.rhsTable.getRule(parseTableVal);
                    for (int i = rules.length - 1; i >= 0; i--){
                        
                    //Push RHS production values onto the stack, ignoring semantic actions for now
                  //  if(!(rules[i] instanceof SemanticAction)){
                        this.parseStack.push(rules[i]);
                    //}
                    }
                    }
                  }
                }
            }
        

        }

    public boolean eofReached(){
        return this.eofReached;
    }
    public boolean errorFound(){
        return this.errorFound;
    }
    public ArrayList<String> getErrors(){
        return this.errors;
    }
    public int errorCount(){
        return this.errorCount;
    }
    
    
    /**
     * Pop items from the stack and get new tokens from tokenizer
     * until predicted and currToken are both semicolons, then note error and continue parsing.
     * @param ex
     * @throws CompilerError 
     */
       public void recover(ParserError ex) throws CompilerError{
                      this.errorCount++;
                      
                      //Store error to print later
                      this.errors.add(ex.toString());
                      this.errorFound = true;
                     
                      
                      // Read in tokens from tokenizer until we read a semicolon
                        while (!(this.currToken.getType() == TokenType.SEMICOLON)){
                            this.currToken = tokenizer.getNextToken();
                        }
                        
                        // Pop items from parse stack until we find a semicolon, 
                        // if only 2 elements left (EOF and GOAL), recovery is complete and end of file was not reached
                        while(!(this.parseStack.peek() == TokenType.SEMICOLON) && (this.parseStack.size()>=2)){
                            this.parseStack.pop();
                            if(this.parseStack.size() == 2){
                                this.eofReached = false;
                                }
                               
                            }
                        }
   
      
       /**
        * Initialize parse table array with info parsed from the parse table file.
        * @throws LexicalError 
        */
      public void initParseTable() throws LexicalError{
        
        CharStream matrixText = new CharStream("src/parser/parsetable-2const.dat");
        char c = matrixText.currentChar();
        ArrayList stringBuffer  = new ArrayList<Integer>();
        ArrayList matrixEntries  = new ArrayList<Integer>();
        while(!((c==' ') || (c == CharStream.EOF))){               
                    if (this.tokenizer.is_letter_or_digit(c) || (c == '-')){
                        stringBuffer.add(c);
                        try {c = matrixText.currentChar();}
                             catch(LexicalError ex){
                        }
                    }        
                    else{
                       if (!stringBuffer.isEmpty()){
                            StringBuilder intBuilder = new StringBuilder(stringBuffer.size());
                            for (int i =0; i < stringBuffer.size(); i++){
                                 intBuilder.append(stringBuffer.get(i));
                            }
                       
                       stringBuffer.clear();
                       String intString = intBuilder.toString();
                       matrixEntries.add(Integer.parseInt(intString));
                      }
                      try{ c = matrixText.currentChar();}
                        catch(LexicalError ex){
                            //do nothing
                       }
                    }
        }

        // Place matrixEntries into matrix parseTable
        int rowCount = -1;
        for (int i = 0; i < matrixEntries.size(); i++){
            if (!((i % parseWidth)==0)){
                parseTable[rowCount][i % parseWidth] = (int) matrixEntries.get(i);
        }
            else{
                rowCount++;
                parseTable[rowCount][i % parseWidth] = (int) matrixEntries.get(i);
            }
        } 
       }
      
          
    //Print contents of stack -- toggled with boolean 'debug' in constructor
    public void dumpStack(){
        System.out.println(this.parseStack.toString());
    }
    
    
    public int[][] getParseTable(){
        return this.parseTable;
    }
 
    public int getErrorCount(){
        return this.errorCount;
    }
    
    public SemanticActions getSemanticActions(){
        return this.actions;
    }
    
    public void setDebug(boolean bool){
        this.debug = bool;
    }
    }

   
