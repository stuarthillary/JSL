/*
 * Copyright (c) 2007, Manuel D. Rossetti (rossetti@uark.edu)
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
package queueing;

import java.io.PrintWriter;
import jsl.modeling.EventActionIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.elements.queue.QObject;
import jsl.modeling.elements.queue.Queue;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.random.distributions.DistributionIfc;
import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.reporting.JSL;
import jsl.modeling.SimulationReporter;
import jsl.modeling.StatisticalBatchingElement;
import jsl.utilities.reporting.StatisticReporter;
import jsl.utilities.statistic.StatisticAccessorIfc;

public class SingleQueueStation extends SchedulingElement {

    private int myNumServers;

    private Queue myWaitingQ;

    private DistributionIfc myServiceDistribution;

    private DistributionIfc myArrivalDistribution;

    private RandomVariable myServiceRV;

    private RandomVariable myArrivalRV;

    private TimeWeighted myNumBusy;

    private TimeWeighted myNS;

    private ArrivalListener myArrivalListener;

    private EndServiceListener myEndServiceListener;

    private ResponseVariable mySysTime;

    public SingleQueueStation(ModelElement parent) {
        this(parent, 1, new Exponential(1.0), new Exponential(0.5));
    }

    public SingleQueueStation(ModelElement parent, int numServers, DistributionIfc ad, DistributionIfc sd) {
        super(parent);

        setNumberOfServers(numServers);
        setServiceDistributionInitialRandomSource(sd);
        setArrivalDistributionInitialRandomSource(ad);

        myWaitingQ = new Queue(this, getName() + "_Q");

        myNumBusy = new TimeWeighted(this, 0.0, getName() + "_NumBusy");

        myNS = new TimeWeighted(this, 0.0, getName() + "_NS");

        mySysTime = new ResponseVariable(this, getName() + "_System Time");

        myArrivalListener = new ArrivalListener();
        myEndServiceListener = new EndServiceListener();
    }

    public int getNumberOfServers() {
        return (myNumServers);
    }

    public final void setNumberOfServers(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }

        myNumServers = n;
    }

    public final void setServiceDistributionInitialRandomSource(DistributionIfc d) {

        if (d == null) {
            throw new IllegalArgumentException("Service Time Distribution was null!");
        }

        myServiceDistribution = d;

        if (myServiceRV == null) { // not made yet
            myServiceRV = new RandomVariable(this, myServiceDistribution, "Service RV");
        } else { // already had been made, and added to model
            // just change the distribution
            myServiceRV.setInitialRandomSource(myServiceDistribution);
        }

    }

    public final void setArrivalDistributionInitialRandomSource(DistributionIfc d) {

        if (d == null) {
            throw new IllegalArgumentException("Arrival Time Distribution was null!");
        }

        myArrivalDistribution = d;

        if (myArrivalRV == null) { // not made yet
            myArrivalRV = new RandomVariable(this, myArrivalDistribution, "Arrival RV");
        } else { // already had been made, and added to model
            // just change the distribution
            myArrivalRV.setInitialRandomSource(myArrivalDistribution);
        }
    }

    public final StatisticAccessorIfc getNumInQAcrossReplicationStatistic() {
        return myWaitingQ.getNumInQAcrossReplicationStatistic();
    }

    public final StatisticAccessorIfc getTimeInQAcrossReplicationStatistic() {
        return myWaitingQ.getTimeInQAcrossReplicationStatistic();
    }

    public final StatisticAccessorIfc getNBAcrossReplicationStatistic() {
        return myNumBusy.getAcrossReplicationStatistic();
    }

    public final StatisticAccessorIfc getNSAcrossReplicationStatistic() {
        return myNS.getAcrossReplicationStatistic();
    }

    public boolean isAvailable() {
        return myNumBusy.getValue() < myNumServers;
    }

    @Override
    protected void initialize() {
        super.initialize();

        // start the arrivals
        scheduleEvent(myArrivalListener, myArrivalRV.getValue(), "Arrival");
    }

    class ArrivalListener implements EventActionIfc {

        public void action(JSLEvent event) {
            myNS.increment(); // new customer arrived
            QObject arrival = createQObject();
            myWaitingQ.enqueue(arrival); // enqueue the newly arriving customer
            if (isAvailable()) { // server available
                myNumBusy.increment(); // make server busy
                QObject customer = myWaitingQ.removeNext(); //remove the next customer
                //	schedule end of service, include the customer as the event's message
                scheduleEvent(myEndServiceListener, myServiceRV, customer);
            }
            //	always schedule the next arrival
            scheduleEvent(myArrivalListener, myArrivalRV);
        }
    }

    class EndServiceListener implements EventActionIfc {

        public void action(JSLEvent event) {
            QObject leavingCustomer = (QObject) event.getMessage();
            mySysTime.setValue(getTime() - leavingCustomer.getCreateTime());
            myNS.decrement(); // customer departed
            myNumBusy.decrement(); // customer is leaving server is freed

            if (!myWaitingQ.isEmpty()) { // queue is not empty
                QObject customer = myWaitingQ.removeNext(); //remove the next customer
                myNumBusy.increment(); // make server busy
                //	schedule end of service
                scheduleEvent(myEndServiceListener, myServiceRV, customer);
            }
        }
    }

    public static void main(String[] args) {

        System.out.println("SingleQueueStation Model");

        runExperiment();

        System.out.println("Done!");

    }

    public static void runReplication() {

        Simulation sim = new Simulation("SingleQueueStation");

        // create the model element and attach it to the main model
        new SingleQueueStation(sim.getModel());

        sim.setLengthOfReplication(20000.0);
        sim.setLengthOfWarmUp(5000.0);

        sim.run();

        SimulationReporter r = sim.makeSimulationReporter();
        r.writeAcrossReplicationStatistics();

        System.out.println(r);

    }

    public static void runBatchReplication() {

        Simulation sim = new Simulation("SingleQueueStation");

        // create the model element and attach it to the main model
        new SingleQueueStation(sim.getModel());

        StatisticalBatchingElement be = sim.getStatisticalBatchingElement();

        sim.setLengthOfReplication(20000.0);
        sim.setLengthOfWarmUp(5000.0);
        sim.run();

        System.out.println(be);
        System.out.println(sim);

        PrintWriter w = JSL.makePrintWriter(sim.getName() + "BatchStatistics", "csv");

        StatisticReporter statisticReporter = be.getStatisticReporter();
        StringBuilder csvStatistics = statisticReporter.getCSVStatistics(true);
        w.print(csvStatistics);

    }

    public static void runExperiment() {

        Simulation sim = new Simulation("SingleQueueStation");

        // create the model element and attach it to the main model
        new SingleQueueStation(sim.getModel());

        // set the parameters of the experiment
        sim.setNumberOfReplications(30);
        
        sim.setLengthOfReplication(20000.0);
        sim.setLengthOfWarmUp(5000.0);

        SimulationReporter r = sim.makeSimulationReporter();
        r.turnOnReplicationCSVStatisticReporting("ReplicationData");
        // tell the experiment to run
        sim.run();
        System.out.println(sim);
        r.writeAcrossReplicationSummaryStatistics();
    }
}
