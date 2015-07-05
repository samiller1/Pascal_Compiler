///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package tests;
//
//import errors.LexicalError;
//import java.util.Arrays;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import static org.junit.Assert.*;
//import symboltable.ConstantEntry;
//import symboltable.SymbolTable;
//import symboltable.VariableEntry;
//import tokenizer.Token;
//import tokenizer.TokenType;
//import tokenizer.Tokenizer;
//
///**
// *
// * @author sandramiller
// */
//public class SymbolTableTest {
//    
//    public SymbolTableTest() {
//    }
//    
//    @BeforeClass
//    public static void setUpClass() {
//    }
//    
//    @AfterClass
//    public static void tearDownClass() {
//    }
//    
//    @Before
//    public void setUp() {
//    }
//    
//    @After
//    public void tearDown() {
//    }
//
//    /**
//     * Test of parse method, of class Parser.
//     */
//    @Test
//    public void testSymbolTable() throws Exception {
//        SymbolTable KeywordTable = new SymbolTable(17);
//        SymbolTable GlobalTable = new SymbolTable(37);
//        SymbolTable ConstantTable = new SymbolTable(37);
//
//        GlobalTable.dumpTable();
//        
//        GlobalTable.insert(new VariableEntry("main", TokenType.PROGRAM));
//        GlobalTable.insert(new VariableEntry("read", TokenType.FUNCTION));
//        GlobalTable.insert(new VariableEntry("write", TokenType.FUNCTION));
//        // TODO: add read, write, main to global table
//
//         // Generate entries for keywords in keywordTable
//        for (int i = 0; i < 17; i++){
//            KeywordTable.insert(new VariableEntry(TokenType.values()[i].toString(), TokenType.values()[i]));
//        }
//       
//        //Add Relop, Addop, and Mulop keyword operators to keywordTable
//        KeywordTable.insert(new VariableEntry("OR", TokenType.ADDOP));
//        KeywordTable.insert(new VariableEntry("DIV",TokenType.MULOP));
//        KeywordTable.insert(new VariableEntry("MOD", TokenType.MULOP));
//        KeywordTable.insert(new VariableEntry("AND", TokenType.MULOP));
//        // Develop and use a routine to fill the KeywordTable, if appropriate
//
//        Tokenizer tokenizer =
//                new Tokenizer("src/symboltable/symtabtest.dat");
//
//        Token token;
//
//        try {
//            token = tokenizer.getNextToken();
//
//            while (!(token.getType() == TokenType.ENDOFFILE)) {
//
//                if ((token.getType() == TokenType.INTCONSTANT) || (token.getType() == TokenType.REALCONSTANT)) {
//                    // If the token is a constant, add it to constantTable
//                    ConstantTable.insert(new ConstantEntry(token.getValue().toString(), token.getType()));
//                } else if (token.getType() == TokenType.IDENTIFIER) {
//
//                    //  If it is an identifier add it to Global table
//                    // as a variable entry
//                    GlobalTable.insert(new VariableEntry(token.getValue().toString(), token.getType()));
//
//                }
//                token = tokenizer.getNextToken();
//            }
//        } catch (LexicalError ex) {
//            System.err.println(ex);
//        }
//        
//       
//        assertEquals(3, ConstantTable.size());
//
//        assertEquals(21, KeywordTable.size());
//        
//        //Factoring in that keywords are not placed in global table and there are repeat entries
//        assertEquals(50, GlobalTable.size());
//      
//        SymbolTable testTable = new SymbolTable(1);
//        assertEquals(0, testTable.size());
//        
//        VariableEntry testEntry = new VariableEntry("test", TokenType.IDENTIFIER);
//        testTable.insert(testEntry);
//        
//        assertTrue(testTable.keys().contains("test"));
//        assertEquals(1, testTable.size());
//        
//        VariableEntry testEntry1  = new VariableEntry("test", TokenType.FUNCTION);
//        testTable.insert(testEntry1);
//        assertTrue(testTable.keys().contains("test"));
//        
//        
//        assertTrue(testTable.values().contains(testEntry));
//        // Entry with key "test" had already been placed in symbol table, testEntry1 will not be added
//         assertFalse(testTable.values().contains(testEntry1));
//    }
//    
//}
