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


import jsl.modeling.EventActionIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.elements.queue.QObject;
import jsl.modeling.elements.queue.Queue;
import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.random.distributions.DistributionIfc;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.SimulationReporter;
import jsl.utilities.random.distributions.Bernoulli;
import jsl.utilities.statistic.StatisticAccessorIfc;

public class DLBWithTwoTypesOfCustomers extends SchedulingElement {

    private int myNumServers;

    private Queue myWaitingQ;

    private DistributionIfc myArrivalDistribution;

    private RandomVariable myArrivalRV;

    private TimeWeighted myNumBusy;

    private TimeWeighted myNS;

    private Counter myNumServed;

    private ResponseVariable mySysTime;

    private ArrivalEventAction myArrivalEventAction;

    private EndServiceEventAction myEndServiceEventAction;

    private RandomVariable myCommercialRV;

    private RandomVariable myResidentialRV;

    private RandomVariable myTypeRV;

    public DLBWithTwoTypesOfCustomers(ModelElement parent) {
        this(parent, 1, new Exponential(1.0));
    }

    public DLBWithTwoTypesOfCustomers(ModelElement parent, int numServers, DistributionIfc ad) {
        super(parent);

        setNumberOfServers(numServers);
        setArrivalDistributionInitialRandomSource(ad);

        myWaitingQ = new Queue(this, "DriverLicenseQ");

        myNumBusy = new TimeWeighted(this, 0.0, "NumBusy");

        myNS = new TimeWeighted(this, 0.0, "NS");

        myNumServed = new Counter(this, "Num Served");

        mySysTime = new ResponseVariable(this, "System Time");

        myCommercialRV = new RandomVariable(this, new Exponential(0.5));
        myResidentialRV = new RandomVariable(this, new Exponential(0.7));
        myTypeRV = new RandomVariable(this, new Bernoulli(0.3));

        myArrivalEventAction = new ArrivalEventAction();
        myEndServiceEventAction = new EndServiceEventAction();
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

    public final void setCustomerTypeProbability(double p) {

        if (myTypeRV == null) { // not made yet
            myTypeRV = new RandomVariable(this, new Bernoulli(p));
        } else { // already had been made, and added to model
            // just change the parameter
            double[] parameters = myTypeRV.getParameters();
            parameters[0] = p;
            myTypeRV.setParameters(parameters);
        }

    }

    public final void setResidentialCustomerServiceTimeInitialRandomSource(DistributionIfc d) {

        if (d == null) {
            throw new IllegalArgumentException("Service Time Distribution was null!");
        }

        if (myResidentialRV == null) { // not made yet
            myResidentialRV = new RandomVariable(this, d);
        } else { // already had been made, and added to model
            // just change the distribution
            myResidentialRV.setInitialRandomSource(d);
        }

    }

    public final void setCommericalCustomerServiceTimeInitialRandomSource(DistributionIfc d) {

        if (d == null) {
            throw new IllegalArgumentException("Service Time Distribution was null!");
        }

        if (myCommercialRV == null) { // not made yet
            myCommercialRV = new RandomVariable(this, d);
        } else { // already had been made, and added to model
            // just change the distribution
            myCommercialRV.setInitialRandomSource(d);
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

    @Override
    protected void initialize() {
        super.initialize();

        // start the arrivals
        scheduleEvent(myArrivalEventAction, myArrivalRV);
    }

    class Customer extends QObject {

        public double myST;
        
        public Customer(double creationTime) {
            this(creationTime, null);
        }

        public Customer(double creationTime, String name) {
            super(creationTime, name);
            
            if (myTypeRV.getValue() <= 0.0){
                myST = myResidentialRV.getValue();
            } else {
                myST = myCommercialRV.getValue();
            }
            
        }
        
    }
    
    class ArrivalEventAction implements EventActionIfc {

        @Override
        public void action(JSLEvent event) {
            myNS.increment(); // new customer arrived
            myWaitingQ.enqueue(new Customer(getTime())); // enqueue the newly arriving customer
            if (myNumBusy.getValue() < myNumServers) { // server available
                myNumBusy.increment(); // make server busy
                Customer customer = (Customer)myWaitingQ.removeNext(); //remove the next customer
                //	schedule end of service, include the customer as the event's message
                scheduleEvent(myEndServiceEventAction, customer.myST, customer);
            }
            //	always schedule the next arrival
            scheduleEvent(myArrivalEventAction, myArrivalRV);
        }
    }

    class EndServiceEventAction implements EventActionIfc {

        @Override
        public void action(JSLEvent event) {
            QObject leavingCustomer = (QObject) event.getMessage();
            mySysTime.setValue(getTime() - leavingCustomer.getCreateTime());
            myNS.decrement(); // customer departed
            myNumBusy.decrement(); // customer is leaving server is freed
            myNumServed.increment();
            if (!myWaitingQ.isEmpty()) { // queue is not empty
                Customer customer = (Customer)myWaitingQ.removeNext(); //remove the next customer
                myNumBusy.increment(); // make server busy
                //	schedule end of service, include the customer as the event's message
                scheduleEvent(myEndServiceEventAction, customer.myST, customer);
            }
        }
    }

    public static void main(String[] args) {

        Simulation sim = new Simulation("DLB_with_Two_Types_Customers");

        // create the model element and attach it to the main model
        new DLBWithTwoTypesOfCustomers(sim.getModel());

        // set the parameters of the experiment
        sim.setNumberOfReplications(30);
        sim.setLengthOfReplication(20000.0);
        sim.setLengthOfWarmUp(5000.0);

        SimulationReporter r = sim.makeSimulationReporter();

        //r.turnOnReplicationCSVStatisticReporting();
        System.out.println(sim);

        // tell the simulation to run
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");

        r.writeAcrossReplicationSummaryStatistics();
    }

}
