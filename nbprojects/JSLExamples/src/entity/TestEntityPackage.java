/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * Copyright (c) Manuel D. Rossetti (rossetti@uark.edu)
 *
 * Contact:
 *	Manuel D. Rossetti, Ph.D., P.E.
 *	Department of Industrial Engineering
 *	University of Arkansas
 *	4207 Bell Engineering Center
 *	Fayetteville, AR 72701
 *	Phone: (479) 575-6756
 *	Email: rossetti@uark.edu
 *	Web: www.uark.edu/~rossetti
 *
 * This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 * of Java classes that permit the easy development and execution of discrete event
 * simulation programs.
 *
 * The JSL is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The JSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the JSL (see file COPYING in the distribution);
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA, or see www.fsf.org
 *
 */
package entity;

import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.Simulation;
import jsl.modeling.elements.resource.Delay;
import jsl.modeling.elements.resource.Entity;
import jsl.modeling.elements.resource.EntityGenerator;
import jsl.modeling.elements.resource.EntityReceiver;
import jsl.modeling.elements.resource.EntityReceiverAbstract;
import jsl.modeling.elements.resource.EntityType;
import jsl.modeling.elements.resource.DisposeEntity;
import jsl.modeling.elements.resource.Request;
import jsl.modeling.elements.resource.Resource;
import jsl.modeling.elements.resource.ResourcedActivity;
import jsl.modeling.elements.resource.SQSRWorkStation;
import jsl.modeling.elements.resource.WorkStation;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.distributions.Constant;
import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.random.distributions.Uniform;
import jsl.modeling.SimulationReporter;

/**
 *
 * @author rossetti
 */
public class TestEntityPackage {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         test1();
        // test2();
        // test3();
        //test4();
        //test5();
        //test6();
        //test7();
        //test8();
        //test9();
        //test10();
        //test11();
        //test12();
        //test13();
        //test14();
    }

    public static void test1() {

        Simulation s = new Simulation("test1");

        Model m = s.getModel();

        EntityGenerator g = new EntityGenerator(m, new Constant(10.0),
                new Constant(10.0));
        g.setDirectEntityReceiver(new NothingReceiver());
        g.useDefaultEntityType();

        s.setNumberOfReplications(5);
        s.setLengthOfReplication(100.0);

        s.run();

        SimulationReporter r = s.makeSimulationReporter();
        r.writeAcrossReplicationStatistics();
    }

    public static void test2() {
        Simulation s = new Simulation("test2");

        Model m = s.getModel();

        EntityGenerator g = new EntityGenerator(m, new Constant(10.0),
                new Constant(10.0));
        g.setDirectEntityReceiver(new TestReceiver(m));
        g.useDefaultEntityType();

        s.setNumberOfReplications(5);
        s.setLengthOfReplication(100.0);

        s.run();

        SimulationReporter r = s.makeSimulationReporter();
        r.writeAcrossReplicationStatistics();
    }

    public static void test3() {
        Simulation s = new Simulation("test3");

        Model m = s.getModel();

        RandomIfc tba = new Exponential(1.0);
        EntityGenerator g = new EntityGenerator(m, tba, tba);
        g.useDefaultEntityType();

        WorkStation w = new WorkStation(m);
        w.setServiceTimeInitialRandomSource(new Exponential(0.5));
        g.setDirectEntityReceiver(w);

        DisposeEntity x = new DisposeEntity();
        w.setDirectEntityReceiver(x);

        // set the parameters of the experiment
        s.setNumberOfReplications(10);
        s.setLengthOfReplication(20000.0);
        s.setLengthOfWarmUp(5000.0);

        s.run();

        SimulationReporter r = s.makeSimulationReporter();
        r.writeAcrossReplicationStatistics();
        System.out.println("Done!");

    }

    public static void test4() {
        Simulation s = new Simulation("test4");

        Model m = s.getModel();

        RandomIfc tba = new Exponential(1.0);
        EntityGenerator g = new EntityGenerator(m, tba, tba);
        g.useDefaultEntityType();

        WorkStation w1 = new WorkStation(m);
        w1.setServiceTimeInitialRandomSource(new Exponential(0.5));
        g.setDirectEntityReceiver(w1);

        WorkStation w2 = new WorkStation(m);
        w2.setServiceTimeInitialRandomSource(new Exponential(0.5));
        w1.setDirectEntityReceiver(w2);

        DisposeEntity x = new DisposeEntity();
        w2.setDirectEntityReceiver(x);

        // set the parameters of the experiment
        s.setNumberOfReplications(10);
        s.setLengthOfReplication(20000.0);
        s.setLengthOfWarmUp(5000.0);

        s.run();

        SimulationReporter r = s.makeSimulationReporter();
        r.writeAcrossReplicationStatistics();
        System.out.println("Done!");

    }

    public static void test5() {
        Simulation s = new Simulation("test5");

        Model m = s.getModel();

        EntityType et = new EntityType(m, "Job");
        et.turnOnNumberInSystemCollection();
        et.turnOnTimeInSystemCollection();

        RandomIfc tba = new Exponential(1.0);
        EntityGenerator g = new EntityGenerator(m, tba, tba);
        g.setEntityType(et);

        WorkStation w = new WorkStation(m);
        w.setServiceTimeInitialRandomSource(new Exponential(0.5));
        g.setDirectEntityReceiver(w);

        DisposeEntity x = new DisposeEntity();
        w.setDirectEntityReceiver(x);

        // set the parameters of the experiment
        s.setNumberOfReplications(10);
        s.setLengthOfReplication(20000.0);
        s.setLengthOfWarmUp(5000.0);

        s.run();

        SimulationReporter r = s.makeSimulationReporter();
        r.writeAcrossReplicationStatistics();
        System.out.println("Done!");

    }

    public static void test6() {
        Simulation s = new Simulation("test6");

        Model m = s.getModel();

        EntityType et = new EntityType(m, "Job");
        et.turnOnNumberInSystemCollection();
        et.turnOnTimeInSystemCollection();

        RandomIfc tba = new Exponential(1.0);
        EntityGenerator g = new EntityGenerator(m, tba, tba);
        g.setEntityType(et);
        g.setSendingOption(EntityType.SendOption.SEQ);

        WorkStation w1 = new WorkStation(m);
        w1.setServiceTimeInitialRandomSource(new Exponential(0.5));
        w1.setSendingOption(EntityType.SendOption.SEQ);

        WorkStation w2 = new WorkStation(m);
        w2.setServiceTimeInitialRandomSource(new Exponential(0.5));
        w2.setSendingOption(EntityType.SendOption.SEQ);

        DisposeEntity x = new DisposeEntity();

        et.addToSequence(w1);
        et.addToSequence(w2);
        et.addToSequence(x);

        // set the parameters of the experiment
        s.setNumberOfReplications(10);
        s.setLengthOfReplication(20000.0);
        s.setLengthOfWarmUp(5000.0);

        s.run();

        SimulationReporter r = s.makeSimulationReporter();
        r.writeAcrossReplicationStatistics();
        System.out.println("Done!");

    }

    public static void test7() {
        Simulation s = new Simulation("test7");

        Model m = s.getModel();

        EntityType et = new EntityType(m, "Job");
        et.turnOnNumberInSystemCollection();
        et.turnOnTimeInSystemCollection();

        RandomIfc tba = new Exponential(1.0);
        EntityGenerator g = new EntityGenerator(m, tba, tba);
        g.setEntityType(et);
        g.setSendingOption(EntityType.SendOption.BY_TYPE);

        WorkStation w1 = new WorkStation(m);
        w1.setServiceTimeInitialRandomSource(new Exponential(0.5));
        w1.setSendingOption(EntityType.SendOption.BY_TYPE);

        WorkStation w2 = new WorkStation(m);
        w2.setServiceTimeInitialRandomSource(new Exponential(0.5));
        w2.setSendingOption(EntityType.SendOption.BY_TYPE);

        DisposeEntity x = new DisposeEntity();

        et.addDestination(g, w1);
        et.addDestination(w1, w2);
        et.addDestination(w2, x);

        // set the parameters of the experiment
        s.setNumberOfReplications(10);
        s.setLengthOfReplication(20000.0);
        s.setLengthOfWarmUp(5000.0);

        s.run();

        SimulationReporter r = s.makeSimulationReporter();
        r.writeAcrossReplicationStatistics();
        System.out.println("Done!");

    }

    public static void test8() {
        Simulation s = new Simulation("test8");

        Model m = s.getModel();
        Constant c1 = new Constant(10.0);
        EntityGenerator g = new EntityGenerator(m, c1, c1);
        Constant c2 = Constant.TWO;
        Delay a1 = new Delay(m);
        a1.setDelayTime(c2);
        Delay a2 = new Delay(m);
        a2.setDelayTime(c2);

        g.useDefaultEntityType();
        g.setDirectEntityReceiver(a1);
        a1.setDirectEntityReceiver(a2);

        DisposeEntity x = new DisposeEntity();
        a2.setDirectEntityReceiver(x);

        s.setNumberOfReplications(5);
        s.setLengthOfReplication(100.0);
        s.run();

        SimulationReporter r = s.makeSimulationReporter();
        r.writeAcrossReplicationStatistics();
    }

    public static void test9() {
        System.out.println("Tandem Queue");
        Simulation s = new Simulation("test9");

        Model m = s.getModel();

        EntityType et = new EntityType(m, "Job");
        et.turnOnNumberInSystemCollection();
        et.turnOnTimeInSystemCollection();

        RandomIfc tba = new Exponential(1.0);
        EntityGenerator g = new EntityGenerator(m, tba, tba);
        g.setEntityType(et);
        g.setSendingOption(EntityType.SendOption.SEQ);

        WorkStation w1 = new WorkStation(m, "W1");
        w1.setServiceTimeInitialRandomSource(new Exponential(0.7));
        w1.setSendingOption(EntityType.SendOption.SEQ);

        WorkStation w2 = new WorkStation(m, "W2");
        w2.setServiceTimeInitialRandomSource(new Exponential(0.9));
        w2.setSendingOption(EntityType.SendOption.SEQ);

        DisposeEntity x = new DisposeEntity();

        et.addToSequence(w1);
        et.addToSequence(w2);
        et.addToSequence(x);

        // set the parameters of the experiment
        s.setNumberOfReplications(30);
        s.setLengthOfReplication(20000.0);
        s.setLengthOfWarmUp(5000.0);

        s.run();

        SimulationReporter r = s.makeSimulationReporter();
        r.writeAcrossReplicationStatistics();
        System.out.println("Done!");

    }

    public static void test10() {
        System.out.println("Tandem Queue with Transport Delay");

        Simulation s = new Simulation("test10");

        Model m = s.getModel();
        EntityType et = new EntityType(m, "Job");
        et.turnOnNumberInSystemCollection();
        et.turnOnTimeInSystemCollection();

        RandomIfc tba = new Exponential(1.0);
        EntityGenerator g = new EntityGenerator(m, tba, tba);
        g.setEntityType(et);
        g.setSendingOption(EntityType.SendOption.SEQ);

        WorkStation w1 = new WorkStation(m, "W1");
        w1.setServiceTimeInitialRandomSource(new Exponential(0.7));
        w1.setSendingOption(EntityType.SendOption.SEQ);

        Delay a1 = new Delay(m);
        a1.setDelayTime(new Uniform(0.0, 2.0));
        a1.setSendingOption(EntityType.SendOption.SEQ);

        WorkStation w2 = new WorkStation(m, "W2");
        w2.setServiceTimeInitialRandomSource(new Exponential(0.9));
        w2.setSendingOption(EntityType.SendOption.SEQ);

        DisposeEntity x = new DisposeEntity();

        et.addToSequence(w1);
        et.addToSequence(a1);
        et.addToSequence(w2);
        et.addToSequence(x);

        // set the parameters of the experiment
        s.setNumberOfReplications(30);
        s.setLengthOfReplication(20000.0);
        s.setLengthOfWarmUp(5000.0);

        s.run();

        SimulationReporter r = s.makeSimulationReporter();
        r.writeAcrossReplicationStatistics();
        System.out.println("Done!");

    }

    public static void test11() {
        Simulation s = new Simulation("test11");

        Model m = s.getModel();

        EntityType et = new EntityType(m, "Job");
        et.turnOnNumberInSystemCollection();
        et.turnOnTimeInSystemCollection();

        RandomIfc tba = new Exponential(1.0);
        EntityGenerator g = new EntityGenerator(m, tba, tba);
        g.setEntityType(et);

        Resource r1 = new Resource(m, "W1_R");
        ResourcedActivity w1 = new ResourcedActivity(m, "W1");
        w1.setDelayTime(new Exponential(0.5));
        w1.addSeizeRequirement(r1, 1, Request.DEFAULT_PRIORITY, false);
        w1.addReleaseRequirement(r1, 1);
        g.setDirectEntityReceiver(w1);

        DisposeEntity x = new DisposeEntity();
        w1.setDirectEntityReceiver(x);

        // set the parameters of the experiment
        s.setNumberOfReplications(30);
        s.setLengthOfReplication(20000.0);
        s.setLengthOfWarmUp(5000.0);

        s.run();

        SimulationReporter r = s.makeSimulationReporter();
        r.writeAcrossReplicationStatistics();
        System.out.println("Done!");

    }

    public static void test12() {
        Simulation s = new Simulation("test12");

        Model m = s.getModel();

        EntityType et = new EntityType(m, "Job");
        et.turnOnNumberInSystemCollection();
        et.turnOnTimeInSystemCollection();

        RandomIfc tba = new Exponential(1.0);
        EntityGenerator g = new EntityGenerator(m, tba, tba);
        g.setEntityType(et);

        Resource r1 = new Resource(m, "W1_R");
        ResourcedActivity w1 = new ResourcedActivity(m, "W1");
        w1.setDelayTime(new Exponential(0.7));
        w1.addSeizeRequirement(r1, 1);
        w1.addReleaseRequirement(r1, 1);
        g.setDirectEntityReceiver(w1);

        Resource r2 = new Resource(m, "W2_R");
        ResourcedActivity w2 = new ResourcedActivity(m, "W2");
        w2.setDelayTime(new Exponential(0.9));
        w2.addSeizeRequirement(r2, 1);
        w2.addReleaseRequirement(r2, 1);
        w1.setDirectEntityReceiver(w2);

        DisposeEntity x = new DisposeEntity();
        w2.setDirectEntityReceiver(x);

        // set the parameters of the experiment
        s.setNumberOfReplications(30);
        s.setLengthOfReplication(20000.0);
        s.setLengthOfWarmUp(5000.0);

        s.run();

        SimulationReporter r = s.makeSimulationReporter();
        r.writeAcrossReplicationStatistics();
        System.out.println("Done!");

    }

    public static void test13() {
        Simulation s = new Simulation("test13");

        Model m = s.getModel();

        EntityType et = new EntityType(m, "Job");
        et.turnOnNumberInSystemCollection();
        et.turnOnTimeInSystemCollection();

        RandomIfc tba = new Exponential(1.0);
        EntityGenerator g = new EntityGenerator(m, tba, tba);
        g.setEntityType(et);

        SQSRWorkStation w = new SQSRWorkStation(m);
        w.setDelayTime(new Exponential(0.5));
        g.setDirectEntityReceiver(w);

        DisposeEntity x = new DisposeEntity();
        w.setDirectEntityReceiver(x);

        // set the parameters of the experiment
        s.setNumberOfReplications(30);
        s.setLengthOfReplication(20000.0);
        s.setLengthOfWarmUp(5000.0);

        s.run();

        SimulationReporter r = s.makeSimulationReporter();
        r.writeAcrossReplicationStatistics();
        System.out.println("Done!");

    }

    public static void test14() {
        Simulation s = new Simulation("test14");

        Model m = s.getModel();
        EntityType et = new EntityType(m, "Job");
        et.turnOnNumberInSystemCollection();
        et.turnOnTimeInSystemCollection();

        RandomIfc tba = new Exponential(1.0);
        EntityGenerator g = new EntityGenerator(m, tba, tba);
        g.setEntityType(et);

        SQSRWorkStation w1 = new SQSRWorkStation(m, "W1");
        w1.setDelayTime(new Exponential(0.7));
        g.setDirectEntityReceiver(w1);

        SQSRWorkStation w2 = new SQSRWorkStation(m, "W2");
        w2.setDelayTime(new Exponential(0.9));
        w1.setDirectEntityReceiver(w2);

        DisposeEntity x = new DisposeEntity();
        w2.setDirectEntityReceiver(x);

        // set the parameters of the experiment
        s.setNumberOfReplications(30);
        s.setLengthOfReplication(20000.0);
        s.setLengthOfWarmUp(5000.0);

        s.run();

        SimulationReporter r = s.makeSimulationReporter();
        r.writeAcrossReplicationStatistics();
        System.out.println("Done!");

    }

    static protected class NothingReceiver extends EntityReceiverAbstract {

        @Override
        protected void receive(Entity entity) {
            EntityType et = entity.getType();

            System.out.println(et.getTime() + " > " + entity + " was received");
        }
    }

    static protected class TestReceiver extends EntityReceiver {

        public TestReceiver(ModelElement parent, String name) {
            super(parent, name);
        }

        public TestReceiver(ModelElement parent) {
            super(parent);
        }

        @Override
        protected void receive(Entity entity) {
            EntityType et = entity.getType();

            System.out.println(et.getTime() + " > " + entity + " was received");
        }
    }
}
