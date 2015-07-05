package tokenizer;

import errors.*;
/**
 * Test driver for the CharStream class.
 */
public class CharStreamDriver
{
   /** The CharStream being excercised. */
   private CharStream stream = null;

   /** Default constructor. */
   public CharStreamDriver()
   {
      super();
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
            ch = stream.currentChar();
            done = true;
         }
         catch (LexicalError ex)
         {
            System.out.println(ex);
         }
      }
      return ch;
   }

   protected void run()
   {
      stream = new CharStream("src/tokenizer/lextest.dat");
      char c = getChar();
      while (c != CharStream.EOF)
      {
         if (c == CharStream.BLANK)
            System.out.println("Next character is : BLANK");
         else
            System.out.println("Next character is : " + c);
         c = getChar();
      }
   }

   public static void main(String[] args)
   {
      CharStreamDriver test = new CharStreamDriver();
      test.run();
   }
}
