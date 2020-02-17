/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evocomp.genetics.equationsolver;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author jamhb
 */
public class Test extends JFrame{

    public static XYSeriesCollection solutionCollection = null;
    public static XYSeries solutionSeries = null;
    public static JPanel chartSolutionPanel = null;
    
    
    public static XYSeriesCollection distanceCollection = null;
    public static XYSeries distanceSeries = null;
    public static JPanel chartDistancePanel = null;
    
    public static JPanel mainPanel=null;
    
    public static CoeficientsWithSolution [] output = null;
    public static CoeficientsWithSolution [] previousOutput = null;
    public static Solver eSolver;
    public static int generation = 0;
    public static int elitism = 0;
    public static int maximumGenerations = 25;
    public static int interval = 200;
    public static Timer timer = new Timer();
    
    public static double GOAL = 13d;
    public static double SCALING = (1);
    public static double XVALUE = 2d;
    
    
    public Test() {
        super("XY Line Chart for Solution");
        
        initializeOutput();
 
        solutionCollection = new XYSeriesCollection();
        solutionSeries = new XYSeries("Solution with current coeficients");
        
        chartSolutionPanel = createProgressChartPanel();
        
         
        distanceCollection = new XYSeriesCollection();
        distanceSeries = new XYSeries("Distance from solution with current coeficients");
        
        chartDistancePanel = createDistanceChartPanel();
        
        mainPanel = new JPanel();
        mainPanel.setSize(1280,480);
        
        mainPanel.add(chartSolutionPanel);
        mainPanel.add(chartDistancePanel);
        add(mainPanel);
 
        setSize(1320,520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runAlgorithm();
                if(generation>maximumGenerations){
                    timer.cancel();
                }
            }
          }, interval, interval);
    }
     
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Test().setVisible(true);
        });
    }
    
    private static void initializeOutput(){
        eSolver = new Solver(
                3, //Number of coeficients
                50, //Number of children
                5, //Match Size
                GOAL, //Goal
                XVALUE, //X value for equation
                SCALING //scaling
        );
        
        System.out.println("Initial Random Matrix... ");
        output = eSolver.createMatrix();
        for (CoeficientsWithSolution is : output) {
            System.out.println(Arrays.toString(is.getCoeficients()) + " - S: " + is.getSolution());
        }
    }
    
    private static void runAlgorithm() {
        System.out.println("Running New Generation...");
        
        eSolver.solveEquationWithRandoms(output);
        CoeficientsWithSolution generationBest = eSolver.GetGenerationWinner(13d, output);
        System.out.println("Generation Winner: [" 
                + generationBest.getCoeficients()[0]/SCALING + "," 
                + generationBest.getCoeficients()[1]/SCALING + "," 
                + generationBest.getCoeficients()[2]/SCALING + "] " 
                + " = " + generationBest.getSolution());
       
        //System.out.println("Matching matrix... ");
        CoeficientsWithSolution [] matchedOutput;
        if(elitism>=0){
            matchedOutput = eSolver.matchMatrix(output, previousOutput);
            elitism--;
        }
        else{
            matchedOutput = eSolver.matchMatrix(output, null);
        }

        //System.out.println("Crossing generation...");
        CoeficientsWithSolution [] crossedOutput = eSolver.getCrossedChildren(matchedOutput);
        
        //System.out.println("Muting generation...");
        CoeficientsWithSolution [] mutedOutput = eSolver.getMutedChildren(crossedOutput, 5);

        solutionSeries.add(generation, generationBest.getSolution());
        solutionCollection.removeAllSeries();
        solutionCollection.addSeries(solutionSeries);
        
        distanceSeries.add(generation, eSolver.getDistanceFromSolution(GOAL, generationBest.getSolution()));
        distanceCollection.removeAllSeries();
        distanceCollection.addSeries(distanceSeries);
        
        previousOutput = output;
        output = mutedOutput;
        
        generation++;
    }
    
    private JPanel createProgressChartPanel() {
        String chartTitle = "Ax^2 + Bx + C = Y";
        String xAxisLabel = "Generation";
        String yAxisLabel = "Best Solution";

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                xAxisLabel, yAxisLabel, solutionCollection);
        ChartPanel cp = new ChartPanel(chart);
        
        
        cp.setPreferredSize(new Dimension(640,480));
        return cp;
    }
    
    private JPanel createDistanceChartPanel() {
        String chartTitle = "Distance from solution";
        String xAxisLabel = "Generation";
        String yAxisLabel = "Best Distance";

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                xAxisLabel, yAxisLabel, distanceCollection);
        ChartPanel cp = new ChartPanel(chart);
        
        
        cp.setPreferredSize(new Dimension(640,480));
        return cp;
    }
}
