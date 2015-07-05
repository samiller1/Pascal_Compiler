package errors;

/**
 *
 * @author Sandra Miller
 */
/** Exception class thrown when a lexical error is encountered. */
public class LexicalError extends CompilerError
{
   public LexicalError(String message)
   {
      super(message);
   }

   // Badly formed comment
   public static LexicalError BadComment(int lineNumber, String currLine)
   {
      return new LexicalError(
                              ">>> ERROR: Cannont include { inside a comment -----> See line " + lineNumber +"."  + '\n' + ">>>  " + currLine + "  <<<");
   }

   //Illegal character
   public static LexicalError IllegalCharacter(char c, int lineNumber, String currLine)
   {
      return new LexicalError(
                              ">>> ERROR: Illegal character: " + c + "-----> See line " + lineNumber + "." + '\n' + ">>>  " + currLine + "  <<<" );
   }
   
   // Unterminated comment
   public static LexicalError UnterminatedComment(int lineNumber, String currLine)
   {
      return new LexicalError(
                              ">>> ERROR: Unterminated comment -----> See line" + lineNumber + "." + '\n' + ">>>  " + currLine + "  <<<");
   }
   
   // Identifier too long
   public static LexicalError InvalidIdentifier(int lineNumber, String currLine)
    {
      return new LexicalError(
                              ">>> ERROR: Identifier exceeds maximum length -----> See line " + lineNumber + "." + '\n' + ">>>  " + currLine + "  <<<");
      
   }
   
   //Invalid decimal point -- ill-formed constants will be handled at parsing phase
     public static LexicalError InvalidDecimal(int lineNumber, String currLine)
    {
      return new LexicalError(
                              ">>> ERROR: No digit after decimal point -----> See line " + lineNumber + "." + '\n' + ">>>  " + currLine + "  <<<");
      
   }
   
   // File did not open properly
     public static LexicalError InvalidFile()
    {
      return new LexicalError(
                              ">>> ERROR: There was a problem opening the input file.");
      
   }
   
   //Constant too long
   public static LexicalError InvalidConstant(int lineNumber, String currLine)
   {
      return new LexicalError(
                              ">>> ERROR: Invalid constant -----> See line " + lineNumber + "." + '\n' + ">>>  " + currLine + "  <<<");
      
   }
}