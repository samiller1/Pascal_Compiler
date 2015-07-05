
package tokenizer;

import errors.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Sandra Miller
 */
public class Tokenizer {

    public CharStream charStream;
    private Token prevToken;
    private Token currToken;
    private HashMap<String, Token> words;
    
    /*
     * Constructor for Tokenizer class
     */
    public Tokenizer(String inputFile) throws LexicalError{
        this.charStream = new CharStream(inputFile);
        if(!(this.charStream.isOpen()==true)){
            throw LexicalError.InvalidFile();
        }
        this.prevToken = new Token(TokenType.BEGIN, null);
        this.words = new HashMap<String, Token>();
        
        // Generate entries for keywords in words hashmap
        for (int i = 0; i < 17; i++){
            this.words.put(TokenType.values()[i].toString(), new Token<String>(TokenType.values()[i],TokenType.values()[i].toString()));
        }
       
        //Add Relop, Addop, and Mulop keyword operators to words hashmap
        this.words.put("OR", new Token<Integer>(TokenType.ADDOP, 3));
        this.words.put("DIV", new Token<Integer>(TokenType.MULOP, 3));
        this.words.put("MOD", new Token<Integer>(TokenType.MULOP, 4));
        this.words.put("AND", new Token<Integer>(TokenType.MULOP, 5));
    }
    
    /** Reads a character from the CharStream object and handles any
    * LexicalExceptions thrown.
    *
    * @see LexicalError
    */
   protected char getChar()
   {
      char ch = (char) CharStream.EOF;
      boolean done = false;
      while (!done)
      {
         try
         {
            ch = this.charStream.currentChar();
            done = true;
         }
         catch (LexicalError ex)
         {
            System.out.println(ex);
         }
      }
      return ch;
   }
   
   /*
    * Assemble simple tokens from characters in charStream
    */
   public void assemble() throws LexicalError{
       
      TokenType prevTokenType = this.prevToken.getType();
      
      // Character buffer to build lexemes -- wil
      ArrayList stringBuffer = new ArrayList<>(); 

      //Keywords and Identifiers
      char c = getChar();
      
      //Blank is read, skip and get next char
      if (c == CharStream.BLANK){
          c = getChar();
      }
      
      //Separators
      if (is_separator(c)){
          if (c == ';'){
              this.currToken = new Token<>(TokenType.SEMICOLON, "SEMICOLON");
          }
          else if(c == ','){
              this.currToken = new Token<>(TokenType.COMMA, "COMMA");
          }
          else if (c == '('){
              this.currToken = new Token<>(TokenType.LEFTPAREN, "LEFTPAREN");
          }
          else if (c == '['){
              this.currToken = new Token<>(TokenType.LEFTBRACKET, "LEFTBRACKET");
          }
          else if (c == ']'){
              this.currToken = new Token<>(TokenType.RIGHTBRACKET, "RIGHTBRACKET");
          }
          else{
              this.currToken = new Token<> (TokenType.RIGHTPAREN, "RIGHTPAREN");
          }
      }
      
      //Endmarkers and doubledots
      else if(c=='.'){
          char peek = getChar();
          if (peek == '.'){
              this.currToken = new Token<String>(TokenType.DOUBLEDOT, "DOUBLEDOT");
          }
          else{
              this.charStream.pushBack(peek);
              this.currToken = new Token<String>(TokenType.ENDMARKER, "ENDMARKER");
          }
              }   
      
      //Identifiers and keywords
      else if (is_letter(c)){
         for (int i = 0; i < 40; i++){
             
            //Identifier exceeds maximum length, throw lexicalError
            if (i == 39) {
                throw LexicalError.InvalidIdentifier(this.charStream.lineNumber(), this.charStream.getCurrentLine());
            }
            
            //getChar() is either a number or digit, add c to buffer
            if (is_letter_or_digit(c)){
                stringBuffer.add(c);
                c = getChar();
            }
            
            //Else there is a delimeter or operator symbol, break loop and assemble token from current buffer
            else {
                this.charStream.pushBack(c);
                break;
            }
      }
         
      // Build lexeme string from character buffer  
      StringBuilder lexemeBuilder = new StringBuilder(stringBuffer.size());
      for (Object ch: stringBuffer){
          lexemeBuilder.append(ch);
      }
      String lexeme = lexemeBuilder.toString();
      
      //Check words Hashmap to see if lexeme is a keyword, if so, return the Token value
      Token lexemeToken = this.words.get(lexeme.toUpperCase());
        if (!(lexemeToken == null)){
            this.currToken = lexemeToken;
        }
        
        // Else, lexeme is not a keyword, add new key-value pair to words hashmap and return new token
        else {
            Token newToken = new Token(TokenType.IDENTIFIER, lexeme.toUpperCase());
            this.words.put(lexeme, newToken);
            this.currToken = newToken;
        }    
      }
      
      
      //Colon or assignop
      else if (c == ':'){
          char peek = getChar();
          if (!(peek == '=')){
              this.charStream.pushBack(peek);
              this.currToken = new Token<>(TokenType.COLON, "COLON");
          }
          else{
              this.currToken = new Token<>(TokenType.ASSIGNOP, "ASSIGNOP");
          }
      }
      
      
      //Relops
      else if (is_relop(c)){
          if (c == '='){
              this.currToken = new Token<Integer>(TokenType.RELOP, 1);
          }
          if (c == '<'){
              char peek = getChar();
              if ((peek == CharStream.BLANK) || this.is_letter_or_digit(peek)) {
                  this.currToken = new Token<Integer>(TokenType.RELOP, 3);
                  this.charStream.pushBack(peek);
              }
              else if (peek == '>'){
                  this.currToken = new Token<Integer>(TokenType.RELOP, 2);
              }
              else if (peek == '='){
                  this.currToken = new Token<Integer>(TokenType.RELOP, 5);
              }
              else{
                  this.charStream.pushBack(peek);
              }
          }
          else if (c == '>'){
              char peek = getChar();
              if (peek == CharStream.BLANK || this.is_letter_or_digit(peek)){
                  this.currToken = new Token<Integer>(TokenType.RELOP, 4);
                  this.charStream.pushBack(peek);
              }
              else if (peek == '='){
                  this.currToken = new Token<Integer>(TokenType.RELOP, 6);
              }
              else{
                  this.charStream.pushBack(peek);
              }
          }
      }


      //Constants
      else if (is_digit(c)){
          boolean real = false;
          boolean e = false;
          //keep track of whether there has already been an occurence of '.' in constant
          boolean decimal = false;
          
  
            for (int i = 0; i < 25; i++){ 
                //Constant too long
                if (i==24){
                    throw LexicalError.InvalidConstant(this.charStream.lineNumber(), this.charStream.getCurrentLine());
                }
            if ((is_digit(c))){
                  stringBuffer.add(c);
                  c = getChar();
              }
            // E is found -- check if followig character is a digit and if a decimal point has already been encountered
              else if ((c == 'e') || (c == 'E')){
                  char peek = getChar();
                  if (!is_digit(peek)){
                      this.charStream.pushBack(peek);
                      this.charStream.pushBack(c);
                      break;
                  }
                  
                  else{   
                      this.charStream.pushBack(peek);
                    //if(!decimal){
                      stringBuffer.add(c);
                      c = getChar();
                      real = true;
                      e = true;
                      
                  //  }
                   // else{
                    //   this.charStream.pushBack(c);
                     //  break;
                   // }
                }
              }
              else if (c == '.'){
                 //Only digits can follow an 'E' -- if a decimal is encountered after an e has been found, separate tokens 
                 if (e){
                     this.charStream.pushBack(c);
                     break;
                 }
                 char peek = getChar();
                 if (peek == '.'){
                     this.charStream.pushBack(c);
                     this.charStream.pushBack(peek);
                     break;
                }
                 else if (peek == CharStream.BLANK){
                     this.charStream.pushBack(c);
                     this.charStream.pushBack(peek);
                     break;
                 }
                 else {
                   if (!decimal){
                     this.charStream.pushBack(peek);
                     stringBuffer.add(c);
                     c = getChar();
                     decimal = true;
                 }
                   else{
                     this.charStream.pushBack(peek);
                     break;
                   }
                }
              }
              else {
                  this.charStream.pushBack(c);
                  break;
              } 
            
          }
          
      //Build lexeme string from character buffer  
      StringBuilder lexemeBuilder = new StringBuilder(stringBuffer.size());
      for (Object ch: stringBuffer){
          lexemeBuilder.append(ch);
      }
      String lexeme = lexemeBuilder.toString();
      
      
      if (real){
          this.currToken = new Token<String>(TokenType.REALCONSTANT, lexeme);
      }
      else{
          //int lexemeInt = Integer.parseInt(lexeme);
          this.currToken = new Token<String>(TokenType.INTCONSTANT, lexeme);
      }
      }

      
      
      //+,-,*,/ operators     
      else if (is_operator(c)){
        if ((c == '+') || (c == '-')){
             if ((prevTokenType == TokenType.RIGHTPAREN)||
                     (prevTokenType == TokenType.RIGHTBRACKET)||
                        (prevTokenType == TokenType.IDENTIFIER)||
                            (prevTokenType == TokenType.INTCONSTANT)||
                                (prevTokenType == TokenType.REALCONSTANT)){
                 if (c == '+') {

                        this.currToken = new Token<>(TokenType.ADDOP, 1);
                     }
                 else {
                     this.currToken = new Token<>(TokenType.ADDOP, 2);
                     }
             }
             else{
                 if(c == '+'){
                     this.currToken = new Token<>(TokenType.UNARYPLUS, '+');
                 }
                 else{
                     this.currToken = new Token<>(TokenType.UNARYMINUS, '-');
                 }
             }
         }
        else{
            if (c == '*'){
                this.currToken = new Token<>(TokenType.MULOP, 1);
            }
            else{
                this.currToken = new Token<>(TokenType.MULOP, 2);
            }
        }
   }
      
      //EOF 
      else if (c == CharStream.EOF){
          this.currToken = new Token<>(TokenType.ENDOFFILE, "ENDOFFILE");
      }
   }
             
   /*
    * Return next token using assemble(), replace prevToken with the current token
    */
   public Token getNextToken()throws LexicalError{
       assemble();
       this.prevToken = this.currToken;
       return this.currToken;
   }
   
 
   
 // Character reading helper functions
   
 public boolean is_end_of_input(char ch){
     return ((ch) == '\0') ;
 }
 
  public boolean is_layout(char ch){
      return (!is_end_of_input(ch)  && (ch) <= ' ');
  }
  
 public boolean is_comment_starter(char ch){
     return ((ch) == '{');
 }
 
 public boolean is_comment_stopper(char ch){
     return ((ch) == '}' ); 
 }
 
 public boolean is_uc_letter(char ch){
     return ('A' <= (ch) && (ch) <= 'Z');
 }
 
 public boolean is_lc_letter(char ch){
     return ('a' <= (ch) && (ch) <= 'z');
 }
 
 public boolean is_letter(char ch){
     return (is_uc_letter(ch) || is_lc_letter(ch));
 }
 
 public boolean is_digit(char ch){
     return ('0' <= (ch) && (ch) <= '9');
 }
 
 public boolean is_letter_or_digit(char ch){
     return (is_letter(ch) || is_digit(ch));
 }
 
 public boolean is_operator(char ch){
     return ((ch == '+') || (ch == '-') ||(ch == '*') || (ch == '/'));
 }
 
 public boolean is_separator(char ch){
     return ((ch == ';') || (ch == ',') || (ch == '(') || (ch == ')') || (ch == '[') || (ch == ']'));
 }
 
 public boolean is_blank(char ch){
     return ((ch) == ' ');
 }
 
 public boolean is_delim(char ch){            
     return (is_operator(ch) || is_separator(ch) || is_blank(ch));
 }
 
 public boolean is_relop(char ch){
     return ((ch == '=') || (ch == '<') || (ch == '>'));
 }
         

}

