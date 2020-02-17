/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evocomp.genetics.equationsolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author jamhb
 */
public class Solver {
    
    private int coeficients = 0;
    private int children = 0;
    private int matchSize = 0;
    private double goal = 0;
    private double x = 0;
    private double scale = 0;
    private CoeficientsWithSolution bestSolution = null;
    private double bestDistance = 0;

    /**
     * @return the bestSolution
     */
    public CoeficientsWithSolution getBestSolution() {
        return bestSolution;
    }

    /**
     * @return the bestDistance
     */
    public double getBestDistance() {
        return bestDistance;
    }
    
    public Solver(int coeficients, int children, int matchSize, double goal, double x, double scale){
        
        bestSolution = new CoeficientsWithSolution(new byte[3]);
        
        this.coeficients = coeficients;
        this.children = children;
        this.goal = goal;
        this.x = x;
        this.scale = scale;
        this.matchSize = matchSize;
    }
    
    public CoeficientsWithSolution[] createMatrix (){
        Random r = new Random();
        CoeficientsWithSolution [] matrix = new CoeficientsWithSolution [children];
        for(int i=0;i<children;i++){
            byte [] array = new byte[coeficients];
            r.nextBytes(array);
            matrix[i] = new CoeficientsWithSolution(array);
        }
        return matrix;
    }
    
    public void solveEquationWithRandoms(CoeficientsWithSolution[] inputs){
        for (CoeficientsWithSolution input : inputs) {
            byte[] coeficientArray = input.getCoeficients();
            
            //Equation is Ax^2 + Bx + C = 13 WHEN X=2 Scaled at 12.75
            input.setSolution(solveSingleEquation(coeficientArray, x, scale));
        }
    }
    
    public double solveSingleEquation(byte[] bytes, double x, double scale){
        //MODIFY THIS FOR YOUR EQUATION
        //Equation is Ax^2 + Bx + C = 13
        
        double Ap=((double)bytes[0])/scale;
        double Bp=((double)bytes[1])/scale;
        double Cp=((double)bytes[2])/scale;
        
        return (Ap*Math.pow(x, 2)+Bp*x+Cp);
    }
    
    public CoeficientsWithSolution[] matchMatrix(CoeficientsWithSolution[] parents, CoeficientsWithSolution[] oldParents){
        if(oldParents != null){
            parents = ArrayUtils.addAll(parents,oldParents);
        }
        else{
            //do nothing
        }
        
        CoeficientsWithSolution[] matched = new CoeficientsWithSolution[children];
        
        for (int i = 0; i < matched.length; i++) {
            ArrayUtils.shuffle(parents);
            matched[i] = getMatchWinner(goal,parents,matchSize);
        }
        
        return matched;
    }
    
    private CoeficientsWithSolution getMatchWinner(double goal, CoeficientsWithSolution[] parents, int matchSize){
        int winner = 0;
        double minDistance = Double.MAX_VALUE;
        
        for (int i = 0; i < matchSize; i++) {
            double distance = getDistanceFromSolution(goal,parents[i].getSolution());
            //System.out.println("distance: " + distance);
            
            if(distance < minDistance){
                minDistance = distance;
                winner = i;
                //System.out.println("Min distance found:" + distance);
            }
            else{
                //continue;
            }
        }
        return parents[winner];
    }
    
    public CoeficientsWithSolution [] getCrossedChildren(CoeficientsWithSolution[] parents){
        ArrayUtils.shuffle(parents);
        Random r = new Random();
        
        List<CoeficientsWithSolution> childArray = new ArrayList<>();
        int arrayHalf = parents.length/2;
        int bitArraySize = coeficients*8;
        
        for (int i = 0; i < arrayHalf; i++) {
            boolean [] bitsParent1 = convertByteArrayToBitArray(parents[i].getCoeficients());
            boolean [] bitsParent2 = convertByteArrayToBitArray(parents[i+arrayHalf].getCoeficients());
            int crossPosition = r.nextInt(bitArraySize-2)+1;
            
            boolean [] x = ArrayUtils.subarray(bitsParent1, 0, crossPosition);
            boolean [] xx = ArrayUtils.subarray(bitsParent1, crossPosition, bitArraySize);
            boolean [] y = ArrayUtils.subarray(bitsParent2, 0, crossPosition);
            boolean [] yy = ArrayUtils.subarray(bitsParent2, crossPosition, bitArraySize);
            
            boolean [] child1 = ArrayUtils.addAll(x, yy);
            boolean [] child2 = ArrayUtils.addAll(y, xx);
            
            childArray.add(new CoeficientsWithSolution(convertBooleanArrayToByteArray(child1)));
            childArray.add(new CoeficientsWithSolution(convertBooleanArrayToByteArray(child2)));
            
//            System.out.println("Parent1: " + Arrays.toString(parents[i].getCoeficients()) + " -  Bits: " + Arrays.toString(bitsParent1));
//            System.out.println("Parent2: " + Arrays.toString(parents[i+arrayHalf].getCoeficients()) + " -  Bits: " + Arrays.toString(bitsParent2));
//            System.out.println("Cross: " + crossPosition);
//            System.out.println("Child1: " + Arrays.toString(convertBooleanArrayToByteArray(child1)) + " -  Bits: " + Arrays.toString(child1));
//            System.out.println("Child2: "+ Arrays.toString(convertBooleanArrayToByteArray(child2)) + " -  Bits: " + Arrays.toString(child2));
        }
        
        return childArray.toArray(parents);
    }
    
    public boolean [] convertByteArrayToBitArray(byte [] bytes){
        boolean[] bits = new boolean[bytes.length * 8];
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[i / 8] & (1 << (7 - (i % 8)))) > 0){
                bits[i] = true;
            }
        }
        return bits;
    }
    
    public byte [] convertBooleanArrayToByteArray(boolean [] bits){
        byte [] bytes = new byte[bits.length/8];
        for (int entry = 0; entry < bytes.length; entry++) {
            for (int bit = 0; bit < 8; bit++) {
                if (bits[entry * 8 + bit]) {
                    bytes[entry] |= (128 >> bit);
                }
            }
        }
        return bytes;
    }
    
    public CoeficientsWithSolution[] getMutedChildren(CoeficientsWithSolution[] children, int muteLength){
        ArrayUtils.shuffle(children);
        Random r = new Random();
        int bitArraySize = coeficients*8;
        for (int i = 0; i < muteLength; i++) {
            int mutePosition = r.nextInt(bitArraySize);
//            System.out.println("Mute Position: " + mutePosition);
            boolean [] originalChild = convertByteArrayToBitArray(children[i].getCoeficients());
//            System.out.println("Original Child: " + Arrays.toString(originalChild));
            originalChild[mutePosition] = !originalChild[mutePosition];
            children[i].setCoeficients(convertBooleanArrayToByteArray(originalChild));
//            System.out.println("Muted Child: " + Arrays.toString(originalChild));
        }
        
        return children;
    }
    
    public CoeficientsWithSolution GetGenerationWinner(double goal, CoeficientsWithSolution [] inputs){
        CoeficientsWithSolution generationWinner = getMatchWinner(goal,inputs,children);
        
        double distance = getDistanceFromSolution(goal,generationWinner.getSolution());
        if(distance < bestDistance){
            bestDistance = distance;
            bestSolution = generationWinner;
            System.out.println("New Best Solution: " + Arrays.toString(bestSolution.getCoeficients()) + " = " + bestSolution.getSolution());
        }
            
        return generationWinner;
    }
    
    public double getDistanceFromSolution(double goal, double solution){
        return Math.abs((goal + 255) - (solution + 255));
    }
}
