/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package running;

import models.DriveThroughPharmacy;

import jsl.modeling.Model;
import jsl.modeling.Simulation;
import jsl.modeling.SimulationReporter;
import jsl.modeling.StatisticalBatchingElement;
import jsl.modeling.elements.variable.TWBatchingElement;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.reporting.StatisticReporter;
import jsl.utilities.statistic.Statistic;

/**
 *
 * @author rossetti
 */
public class SimulationDemos {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //batchingDemo();
        //batchingDemo2();
        //replicationDeletion();
        //runEachRepSeparately();
        halfWidthSequentialSampling();
    }

    public static void batchingDemo() {
        Simulation sim = new Simulation("Drive Through Pharmacy");
        // getTWBatchStatisticObserver the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DriveThroughPharmacy driveThroughPharmacy = new DriveThroughPharmacy(m);
        driveThroughPharmacy.setArrivalRS(new Exponential(6.0));
        driveThroughPharmacy.setServiceRS(new Exponential(3.0));
        StatisticalBatchingElement be = sim.getStatisticalBatchingElement();
        TWBatchingElement twbe = new TWBatchingElement(driveThroughPharmacy);
        TimeWeighted tw = m.getTimeWeighted("# in System");
        twbe.add(tw);

        // set the parameters of the experiment
        sim.setNumberOfReplications(1);
        sim.setLengthOfReplication(200000.0);
        sim.setLengthOfWarmUp(5000.0);
//        SimulationReporter r = sim.makeSimulationReporter();
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");

        System.out.println(sim);
        System.out.println(be);
//        r.writeAcrossReplicationSummaryStatistics();  
        System.out.println(twbe);
    }

    public static void batchingDemo2() {
        Simulation sim = new Simulation("Drive Through Pharmacy");
        // getTWBatchStatisticObserver the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DriveThroughPharmacy driveThroughPharmacy = new DriveThroughPharmacy(m);
        driveThroughPharmacy.setArrivalRS(new Exponential(6.0));
        driveThroughPharmacy.setServiceRS(new Exponential(3.0));
        StatisticalBatchingElement be = new StatisticalBatchingElement(m);
        // set the parameters of the experiment
        sim.setNumberOfReplications(1);
        sim.setLengthOfReplication(200000.0);
        sim.setLengthOfWarmUp(5000.0);
//        SimulationReporter r = sim.makeSimulationReporter();
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");

        System.out.println(sim);
        System.out.println(be);
//        r.writeAcrossReplicationSummaryStatistics();  
    }

    public static void replicationDeletion() {
        Simulation sim = new Simulation("Drive Through Pharmacy");
        // getTWBatchStatisticObserver the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DriveThroughPharmacy driveThroughPharmacy = new DriveThroughPharmacy(m);
        driveThroughPharmacy.setArrivalRS(new Exponential(1.0));
        driveThroughPharmacy.setServiceRS(new Exponential(0.7));
        // set the parameters of the experiment
        sim.setNumberOfReplications(10);
        sim.setLengthOfReplication(30000.0);
        sim.setLengthOfWarmUp(10000.0);
        SimulationReporter r = sim.makeSimulationReporter();
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");
        r.writeAcrossReplicationSummaryStatistics();
        StatisticReporter sr = new StatisticReporter(r.getAcrossReplicationStatisticsList());
        System.out.println(sr.getHalfWidthSummaryReport());
        r.writeAcrossReplicationSummaryStatisticsAsLaTeX();
        System.out.println(sr.getHalfWidthSummaryReportAsLaTeXTabular());
    }

    public static void halfWidthSequentialSampling() {
        Simulation sim = new Simulation("Drive Through Pharmacy");
        // getTWBatchStatisticObserver the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DriveThroughPharmacy driveThroughPharmacy = new DriveThroughPharmacy(m);
        driveThroughPharmacy.setArrivalRS(new Exponential(6.0));
        driveThroughPharmacy.setServiceRS(new Exponential(3.0));
        StatisticalBatchingElement be = sim.getStatisticalBatchingElement();
        TWBatchingElement twbe = new TWBatchingElement(driveThroughPharmacy);
        TimeWeighted tw = m.getTimeWeighted("# in System");
        TWBatchingElement.TWBatchStatisticObserver bo = twbe.add(tw);
        bo.setDesiredHalfWidth(0.5);
        bo.setCollectionRule(Statistic.CollectionRule.HALF_WIDTH);
        
//        ResponseVariable rs = m.getResponseVariable("System Time");
//        ResponseVariableBatchingElement rvbe = new ResponseVariableBatchingElement(m);
//        BatchStatisticObserver rbo = rvbe.add(rs);
//        rbo.setDesiredHalfWidth(0.2);
//        rbo.setCollectionRule(Statistic.CollectionRule.HALF_WIDTH);
        
        // set the parameters of the experiment
        sim.setNumberOfReplications(1);
        //sim.setLengthOfReplication(200000.0);
        sim.setLengthOfWarmUp(5000.0);
//        SimulationReporter r = sim.makeSimulationReporter();
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");

        System.out.println(sim);
        System.out.println(be);
//        r.writeAcrossReplicationSummaryStatistics();  
        System.out.println(twbe);
//        System.out.println(rvbe);
    }

    public static void runEachRepSeparately() {
        Simulation sim = new Simulation("Drive Through Pharmacy");
        // getTWBatchStatisticObserver the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DriveThroughPharmacy driveThroughPharmacy = new DriveThroughPharmacy(m);
        driveThroughPharmacy.setArrivalRS(new Exponential(6.0));
        driveThroughPharmacy.setServiceRS(new Exponential(3.0));
        // set the parameters of the experiment
        sim.setNumberOfReplications(30);
        sim.setLengthOfReplication(20000.0);
        sim.setLengthOfWarmUp(5000.0);
        SimulationReporter r = sim.makeSimulationReporter();
        System.out.println("Simulation started.");
        sim.initialize();
        while(sim.hasNextReplication()){
            sim.runNext();
        }
        
        System.out.println("Simulation completed.");
        r.writeAcrossReplicationSummaryStatistics();
    }
}
