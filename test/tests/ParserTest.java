/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import parser.Parser;
import static org.junit.Assert.*;

/**
 *
 * @author sandramiller
 */
public class ParserTest {
    
    public ParserTest() {
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
    public void testParse() throws Exception {
        Parser parser = new Parser("src/parser/parsetest.dat");
        int[][] parseTable = parser.getParseTable();
        int[] actualParseRow1 = new int[38];
        for (int i = 0; i < 38; i++){
            actualParseRow1[i] = parseTable[0][i];
        }
        
        int[] actualParseRow2 = new int[38];
        for (int i = 0; i < 38; i++){
            actualParseRow2[i] = parseTable[34][i];
        }
        
        int[] expectedParseRow1 = {001, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,999, 999, 999, 999, 999, 999, 999, 999, 65, 999};
        int[] expectedParseRow2 = {999,999 ,999 ,999 ,999 ,999 ,999 ,999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999};
        assertArrayEquals(expectedParseRow1, actualParseRow1);
        assertArrayEquals(expectedParseRow2, actualParseRow2);
        
        parser.parse();
        assertEquals(0, parser.getErrorCount());
        
        Parser parser1 = new Parser("src/parser/parsetest_1.dat");
        
        parser1.parse();
        assertEquals(4, parser1.getErrorCount());
    }
    
}
