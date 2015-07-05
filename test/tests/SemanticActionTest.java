/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import errors.CompilerError;
import errors.LexicalError;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import parser.*;
import semanticactions.*;
import symboltable.*;
import tokenizer.*;

/**
 *
 * @author sandramiller
 */
public class SemanticActionTest {
    
    public SemanticActionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of parse method, of class Parser.
     */
    @Test
    public void testSemanticAction() throws Exception {
        Parser parser = new Parser("src/parser/InputFiles/array.pas");
        parser.setDebug(true);
        
        
           try{
            parser.parse();
           }
           catch(CompilerError ex){
               System.out.println(ex);
           }
           
           SemanticActions actions = parser.getSemanticActions();
           actions.dumpGlobalTable();
           
           assertTrue(actions.getGlobalTable().keys().contains("M"));
           assertTrue(actions.getGlobalTable().lookup("M") instanceof ArrayEntry);
           
           ArrayEntry mArray = (ArrayEntry) actions.getGlobalTable().lookup("M");
          assertTrue(mArray.getLowerBound() == 1);
           assertTrue(mArray.getUpperBound() == 5);
           
          assertEquals(actions.getGlobalTable().lookup("MAIN").getName(), "MAIN");
           assertEquals(actions.getGlobalTable().lookup("nonexistentEntry"), null);
           
           int[] list1 = new int[4];
           list1[0] = 1;
           list1[1] = 2;
           list1[3] = 4;
           list1[2] = 3;
           int[] list2 = new int[2];
           list2[0] = 10;
           list2[1] = 20;
           
           
          assertEquals(Arrays.toString(actions.merge(list1,list2)), "[1, 2, 3, 4, 10, 20]");
           
           ConstantEntry constant = new ConstantEntry("3", TokenType.INTCONSTANT);
           
           actions.generateConstant(constant,7);
           assertEquals(actions.getGlobalTable().lookup("$$TEMP7").getType(), TokenType.INTCONSTANT);
           assertEquals(Arrays.toString(actions.makeList(3)), "[3]" );
           
           int[] backpatchList = new int[2];
           backpatchList[0] = 5;
           backpatchList[1] = 7;
           actions.backpatch(backpatchList, 33);
           assertEquals(actions.getQuads().getQuad(5)[actions.getQuads().getQuad(5).length - 1], "33");
           

           VariableEntry varEntry = new VariableEntry("Q", TokenType.REAL, 6);
           assertEquals(actions.makeOperand(varEntry), "_6");
           varEntry.setParm();
           assertEquals(actions.makeOperand(varEntry), "^_6");
           assertEquals(actions.makeParamOperand(varEntry), "@_6");
           

           
    }
    
}
