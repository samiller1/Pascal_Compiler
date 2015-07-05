/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semanticactions;

/**
 *
 * @author sandramiller
 */
public enum Etype {
    ARITHMETIC(0), RELATIONAL(1);
    private int n;
    private Etype(int i) { n = i; }
    public int getIndex () { return n; }
}
