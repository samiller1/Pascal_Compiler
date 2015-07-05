package tokenizer;

import errors.LexicalError;

/**
 *
 * @author Sandra Miller
 */
public class TokenizerDriver
{

	public TokenizerDriver()
	{
		super();
	}

	protected void run()    // change this function to call GetNextToken until all tokens are read
	{
            try{
		Tokenizer tokenizer = 
			new Tokenizer("src/parser/array.pas");  

		Token token =	tokenizer.getNextToken();

		while (!(token.getType() == TokenType.ENDOFFILE))
		{
			System.out.print("Recognized Token:  " + token.getType());
			if ((token.getType() == TokenType.IDENTIFIER) || (token.getType() == TokenType.REALCONSTANT) 
					|| (token.getType() == TokenType.INTCONSTANT) )
				System.out.print(" Value : " + token.getValue());
			else if ((token.getType() == TokenType.RELOP)
					|| (token.getType() == TokenType.ADDOP) || (token.getType() == TokenType.MULOP))
				System.out.print(" OpType : " + token.getOpType());
			System.out.println();

			token = tokenizer.getNextToken();
		}
                if (token.getType() == TokenType.ENDOFFILE){
                    System.out.println("Recognized Token:  " + token.getType());
                }
                }
                catch (LexicalError ex){
                    System.out.println(ex);
                }
	}


	public static void main(String[] args) throws LexicalError
	{
		TokenizerDriver test = new TokenizerDriver();
		test.run();
	}
}