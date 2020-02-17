/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evocomp.genetics.equationsolver;

/**
 *
 * @author jamhb
 */
public class CoeficientsWithSolution {
    
    public CoeficientsWithSolution (byte [] coeficients){
        this.coeficients = coeficients;
    }

    /**
     * @return the coeficients
     */
    public byte[] getCoeficients() {
        return coeficients;
    }

    /**
     * @param coeficients the coeficients to set
     */
    public void setCoeficients(byte[] coeficients) {
        this.coeficients = coeficients;
    }

    /**
     * @return the solution
     */
    public double getSolution() {
        return solution;
    }

    /**
     * @param solution the solution to set
     */
    public void setSolution(double solution) {
        this.solution = solution;
    }
    
    private byte [] coeficients;
    private double solution;
    
}
