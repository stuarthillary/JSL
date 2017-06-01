/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.welch;

import examples.modeling.DriveThroughPharmacy;
import java.io.File;
import java.io.IOException;
import jsl.modeling.Model;
import jsl.modeling.Simulation;
import jsl.modeling.SimulationReporter;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.reporting.JSL;
import jsl.utilities.statistic.BatchStatistic;

/**
 * Illustrates the use of the classes in the jsl.utilities.welch package
 *
 * @author rossetti
 */
public class CaptureWarmUpData {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        //tracingResponseVariables();
        displayWelchData();
    }

    public static void tracingResponseVariables() {
        Simulation sim = new Simulation("DTP");
        // get the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DriveThroughPharmacy driveThroughPharmacy = new DriveThroughPharmacy(m);
        driveThroughPharmacy.setArrivalRS(new Exponential(1.0));
        driveThroughPharmacy.setServiceRS(new Exponential(0.7));
        ResponseVariable rv = m.getResponseVariable("System Time");
        rv.turnOnTrace(true);
        //rv.turnOnTrace();
        // set the parameters of the experiment
        sim.setNumberOfReplications(5);
        sim.setLengthOfReplication(20.0);
        sim.setLengthOfWarmUp(5.0);
        SimulationReporter r = sim.makeSimulationReporter();
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");
        r.writeAcrossReplicationSummaryStatistics();
    }

    public static void testWelchDataFile() {
        Simulation sim = new Simulation("DTP");
        // get the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DriveThroughPharmacy driveThroughPharmacy = new DriveThroughPharmacy(m);
        driveThroughPharmacy.setArrivalRS(new Exponential(1.0));
        driveThroughPharmacy.setServiceRS(new Exponential(0.7));
        ResponseVariable rv = m.getResponseVariable("System Time");
        TimeWeighted tw = m.getTimeWeighted("# in System");
        tw.turnOnTrace(true);
        File d = JSL.makeOutputSubDirectory(sim.getName());
        WelchDataFileCollector wdfc = new WelchDataFileCollector(d, "welchsystime");
        rv.addObserver(wdfc);
        WelchDataFileCollectorTW wdfctw = new WelchDataFileCollectorTW(1, d, "numInSystem");
        tw.addObserver(wdfctw);
        WelchDataCollectorTW wtw = new WelchDataCollectorTW(1, 30);
        tw.addObserver(wtw);
        //rv.turnOnTrace();
        // set the parameters of the experiment
        sim.setNumberOfReplications(5);
        sim.setLengthOfReplication(20.0);
        SimulationReporter r = sim.makeSimulationReporter();
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");
        r.writeAcrossReplicationSummaryStatistics();
        System.out.println(wdfc);
        WelchDataFileAnalyzer wa = wdfc.makeWelchDataFileAnalyzer();
        System.out.println(wa);

        long n = wa.getMinNumObservationsInReplications();
        int rep = wa.getNumberOfReplications();

        System.out.println("Writing welch data to csv");
        wa.makeCSVWelchPlotDataFile();
        System.out.println("Writing welch data to welch data file");

        wa.makeWelchPlotDataFile();

        WelchDataFileAnalyzer wa1 = wdfctw.makeWelchDataFileAnalyzer();
        wa1.makeCSVWelchPlotDataFile();
        wa1.makeWelchPlotDataFile();
        wa1.displayWelchChart();

    }

    public static void displayWelchData() throws IOException {
        Simulation sim = new Simulation("DTP");
        // get the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DriveThroughPharmacy driveThroughPharmacy = new DriveThroughPharmacy(m);
        driveThroughPharmacy.setArrivalRS(new Exponential(1.0));
        driveThroughPharmacy.setServiceRS(new Exponential(0.7));
        // get access to the response variables
        ResponseVariable stRV = m.getResponseVariable("System Time");
        TimeWeighted nisTW = m.getTimeWeighted("# in System");

        // create a directory for the results
        File d = JSL.makeOutputSubDirectory("welchDir");
        // make the data collectors and attach them as observers
        WelchDataFileCollector stWDFC = new WelchDataFileCollector(d, "welchsystime");
        // need to specify the discretizing interval for time weighted
        WelchDataFileCollectorTW nisWDFC = new WelchDataFileCollectorTW(10, d, "numInSystem");
        stRV.addObserver(stWDFC);
        nisTW.addObserver(nisWDFC);

        // set up the simulation and run it
        sim.setNumberOfReplications(10);
        sim.setLengthOfReplication(30000.0);
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");
        System.out.println();

        // print out some stuff just to show it
        System.out.println(stWDFC);
        System.out.println(nisWDFC);

        // make the Welch data file analyzers
        WelchDataFileAnalyzer stWDFA = stWDFC.makeWelchDataFileAnalyzer();
        WelchDataFileAnalyzer nisWDFA = nisWDFC.makeWelchDataFileAnalyzer();

        stWDFA.makeCSVWelchPlotDataFile();
        nisWDFA.makeCSVWelchPlotDataFile();

        BatchStatistic batchWelchAverages = stWDFA.batchWelchAverages();
        System.out.println(batchWelchAverages);

        double ts1 = WelchDataFileAnalyzer.getNegativeBiasTestStatistic(batchWelchAverages);
        System.out.println("neg bias test statistic = " + ts1);

        double ts2 = WelchDataFileAnalyzer.getPositiveBiasTestStatistic(batchWelchAverages);
        System.out.println("pos bias test statistic = " + ts2);
        
        double[] psums = WelchDataFileAnalyzer.getPartialSums(batchWelchAverages);
        for(double ps: psums){
            JSL.out.println(ps);
        }
        // display the chart
        //stWDFA.displayWelchChart();
        // display the other chart
        //nisWDFA.displayWelchChart();
    }
}
