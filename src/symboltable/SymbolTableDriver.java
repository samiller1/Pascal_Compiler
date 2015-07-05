/*
 * Sandra Miller
 */

package symboltable;

import errors.*;
import tokenizer.*;
import parser.*;


public class SymbolTableDriver {

    public SymbolTableDriver() {
        super();
    }

    protected void run(String filename) throws LexicalError {
        SymbolTable KeywordTable = new SymbolTable(17);
        SymbolTable GlobalTable = new SymbolTable(37);
        SymbolTable ConstantTable = new SymbolTable(37);

        GlobalTable.dumpTable();
        
        GlobalTable.insert(new VariableEntry("main", TokenType.FUNCTION,0));
        GlobalTable.insert(new VariableEntry("read", TokenType.FUNCTION,0));
        GlobalTable.insert(new VariableEntry("write", TokenType.FUNCTION,0));

         // Generate entries for keywords in keywordTable
        for (int i = 0; i < 17; i++){
            KeywordTable.insert(new VariableEntry(TokenType.values()[i].toString(), TokenType.values()[i],0));
        }
       
        //Add Relop, Addop, and Mulop keyword operators to keywordTable
        KeywordTable.insert(new VariableEntry("OR", TokenType.ADDOP,0));
        KeywordTable.insert(new VariableEntry("DIV",TokenType.MULOP,0));
        KeywordTable.insert(new VariableEntry("MOD", TokenType.MULOP,0));
        KeywordTable.insert(new VariableEntry("AND", TokenType.MULOP,0));
      

        Tokenizer tokenizer =
                new Tokenizer(filename);

        Token token;

        try {
            token = tokenizer.getNextToken();

            while (!(token.getType() == TokenType.ENDOFFILE)) {

                if ((token.getType() == TokenType.INTCONSTANT) || (token.getType() == TokenType.REALCONSTANT)) {
                    
                    // If the token is a constant, add it to constantTable
                    ConstantTable.insert(new ConstantEntry(token.getValue().toString(), token.getType()));
                } else if (token.getType() == TokenType.IDENTIFIER && (!KeywordTable.keys().contains((String)token.getValue()))) {

                    //  If it is an identifier add it to Global table
                    // as a variable entry
                    GlobalTable.insert(new VariableEntry(token.getValue().toString(), token.getType(),0));

                }
                token = tokenizer.getNextToken();
            }
        } catch (LexicalError ex) {
            System.err.println(ex);
        }
        
        System.out.println("-----------------------");
        System.out.println("-----KEYWORD TABLE-----");
        System.out.println("-----------------------");
	KeywordTable.dumpTable();
       
        
        System.out.println("-----------------------");
        System.out.println("------GLOBAL TABLE-----");
        System.out.println("-----------------------");
        GlobalTable.dumpTable();
  
        
        System.out.println("-----------------------");
        System.out.println("-----CONSTANT TABLE----");
        System.out.println("-----------------------");
        ConstantTable.dumpTable();
      
    }


    public static void main(String[] args) throws LexicalError {
        SymbolTableDriver test = new SymbolTableDriver();
        test.run("src/symboltable/symtabtest.dat");
    }


}