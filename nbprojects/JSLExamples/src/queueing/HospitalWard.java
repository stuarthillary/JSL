/*
 * Created on Mar 27, 2007
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
import jsl.modeling.Experiment;
import jsl.modeling.JSLEvent;
import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.modeling.elements.variable.Variable;
import jsl.observers.ObserverIfc;
import jsl.observers.scheduler.ExecutiveTraceReport;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.distributions.Constant;
import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.random.distributions.Lognormal;
import jsl.modeling.SimulationReporter;

/**
 * @author rossetti
 *
 */
public class HospitalWard extends SchedulingElement {

    public static final double IDLE = 0.0;

    public static final double BUSY = 1.0;

    public static final double OPEN = 1.0;

    public static final double CLOSED = 0.0;

    protected RandomVariable myNonOpPatientStayTime;

    protected RandomVariable myPreOpStayTime;

    protected RandomVariable myOperationTime;

    protected RandomVariable myPostOpStayTime;

    protected RandomVariable myOpRoomOpenTime;

    protected RandomVariable myOpRoomCloseTime;

    protected RandomVariable myNonOpPatientTBA;

    protected RandomVariable myOpPatientTBA;

    protected TimeWeighted myNonOpPatientQ;

    protected TimeWeighted myOpPatientQ;

    protected TimeWeighted myOpRoomQ;

    protected TimeWeighted myAvailableBeds;

    protected TimeWeighted myNumBusyBeds;

    protected TimeWeighted myORRoomOpenStatus;

    protected TimeWeighted myORRoomIdleStatus;

    protected NonOperationPatientArrivalAction myNonOperationPatientArrivalAction;

    protected NonOperationPatientDepartureAction myNonOperationPatientEndOfStayAction;

    protected OperationPatientArrivalAction myOperationPatientArrivalAction;

    protected EndOfPreOperationStayAction myEndOfPreOperationStayAction;

    protected EndOfOperationAction myEndOfOperationAction;

    protected EndOfPostOperationStayAction myEndOfPostOperationStayAction;

    protected OpenOperatingRoomAction myOpenOperatingRoomAction;

    protected CloseOperatingRoomAction myCloseOperatingRoomAction;

    /**
     * @param parent
     */
    public HospitalWard(ModelElement parent) {
        this(parent, null);
    }

    /**
     * @param parent
     * @param name
     */
    public HospitalWard(ModelElement parent, String name) {
        super(parent, name);
        myNonOpPatientStayTime = new RandomVariable(this, new Exponential(60.0));
        myPreOpStayTime = new RandomVariable(this, new Exponential(24.0));
        myOperationTime = new RandomVariable(this, new Lognormal(0.75, 0.25 * 0.25));
        myPostOpStayTime = new RandomVariable(this, new Exponential(72.0));
        myOpRoomOpenTime = new RandomVariable(this, new Constant(24.0));
        myOpRoomCloseTime = new RandomVariable(this, new Constant(4.0));
        myNonOpPatientTBA = new RandomVariable(this, new Exponential(12.0));
        myOpPatientTBA = new RandomVariable(this, new Exponential(6.0));

        myNonOpPatientQ = new TimeWeighted(this, 0.0, "NonOpPatientQ");
        myOpPatientQ = new TimeWeighted(this, 0.0, "OpPatientQ");
        myOpRoomQ = new TimeWeighted(this, 0.0, "OpRoomQ");
        myAvailableBeds = new TimeWeighted(this, 20.0, "Beds Available");
        myNumBusyBeds = new TimeWeighted(this, 0.0, "Beds Busy");
        myAvailableBeds.addObserver(new BedObserver());
        myORRoomOpenStatus = new TimeWeighted(this, OPEN, "OR-Open-Status");
        myORRoomIdleStatus = new TimeWeighted(this, IDLE, "OR-Idle-Status");

        myNonOperationPatientArrivalAction = new NonOperationPatientArrivalAction();
        myNonOperationPatientEndOfStayAction = new NonOperationPatientDepartureAction();
        myOperationPatientArrivalAction = new OperationPatientArrivalAction();
        myEndOfPreOperationStayAction = new EndOfPreOperationStayAction();
        myEndOfOperationAction = new EndOfOperationAction();
        myEndOfPostOperationStayAction = new EndOfPostOperationStayAction();
        myOpenOperatingRoomAction = new OpenOperatingRoomAction();
        myCloseOperatingRoomAction = new CloseOperatingRoomAction();

    }

    public void setInitialNumberofBeds(double value) {
        myAvailableBeds.setInitialValue(value);
    }

    public void setORInitialStatusToOpen() {
        myORRoomOpenStatus.setInitialValue(OPEN);
    }

    public void setORInitialStatusToClosed() {
        myORRoomOpenStatus.setInitialValue(CLOSED);
    }

    public void setNonOpPatientStayTimeInitialRandomSource(RandomIfc source) {
        myNonOpPatientStayTime.setInitialRandomSource(source);
    }

    public void setPreOperationTimeInitialRandomSource(RandomIfc source) {
        myPreOpStayTime.setInitialRandomSource(source);
    }

    public void setPostOperationTimeInitialRandomSource(RandomIfc source) {
        myPostOpStayTime.setInitialRandomSource(source);
    }

    public void setOperationTimeInitialRandomSource(RandomIfc source) {
        myOperationTime.setInitialRandomSource(source);
    }

    public void setOperatingRoomOpenTimeInitialRandomSource(RandomIfc source) {
        myOpRoomOpenTime.setInitialRandomSource(source);
    }

    public void setOperatingRoomCloseTimeInitialRandomSource(RandomIfc source) {
        myOpRoomCloseTime.setInitialRandomSource(source);
    }

    public void setNonOpPatientTBAInitialRandomSource(RandomIfc source) {
        myNonOpPatientTBA.setInitialRandomSource(source);
    }

    public void setOpPatientTBAInitialRandomSource(RandomIfc source) {
        myOpPatientTBA.setInitialRandomSource(source);
    }

    protected void initialize() {
        scheduleEvent(myNonOperationPatientArrivalAction, myNonOpPatientTBA.getValue());
        scheduleEvent(myOperationPatientArrivalAction, myOpPatientTBA.getValue());
        scheduleEvent(myCloseOperatingRoomAction, myOpRoomOpenTime.getValue());
    }

    protected class NonOperationPatientArrivalAction implements EventActionIfc {

        public void action(JSLEvent evt) {
            if (myAvailableBeds.getValue() > 0.0) {
                myAvailableBeds.decrement();
                scheduleEvent(myNonOperationPatientEndOfStayAction, myNonOpPatientStayTime.getValue());
            } else {
                myNonOpPatientQ.increment();
            }
            scheduleEvent(myNonOperationPatientArrivalAction, myNonOpPatientTBA.getValue());
        }
    }

    protected class NonOperationPatientDepartureAction implements EventActionIfc {

        public void action(JSLEvent evt) {
            if (myNonOpPatientQ.getValue() > 0.0) {
                myNonOpPatientQ.decrement();
                scheduleEvent(myNonOperationPatientEndOfStayAction, myNonOpPatientStayTime.getValue());
            } else if (myOpPatientQ.getValue() > 0.0) {
                myOpPatientQ.decrement();
                scheduleEvent(myEndOfPreOperationStayAction, myPreOpStayTime.getValue());
            } else {
                myAvailableBeds.increment();
            }
        }
    }

    protected class OperationPatientArrivalAction implements EventActionIfc {

        public void action(JSLEvent evt) {
            if (myAvailableBeds.getValue() > 0.0) {
                myAvailableBeds.decrement();
                scheduleEvent(myEndOfPreOperationStayAction, myPreOpStayTime.getValue());
            } else {
                myOpPatientQ.increment();
            }
            scheduleEvent(myOperationPatientArrivalAction, myOpPatientTBA.getValue());
        }
    }

    protected class EndOfPreOperationStayAction implements EventActionIfc {

        public void action(JSLEvent evt) {
            if ((myORRoomIdleStatus.getValue() == IDLE) && (myORRoomOpenStatus.getValue() == OPEN)) {
                myORRoomIdleStatus.setValue(BUSY);
                scheduleEvent(myEndOfOperationAction, myOperationTime.getValue());
            } else {
                myOpRoomQ.increment();
            }
        }
    }

    protected class EndOfOperationAction implements EventActionIfc {

        public void action(JSLEvent evt) {
            if ((myOpRoomQ.getValue() > 0.0) && (myORRoomOpenStatus.getValue() == OPEN)) {
                myOpRoomQ.decrement();
                scheduleEvent(myEndOfOperationAction, myOperationTime.getValue());
            } else {
                myORRoomIdleStatus.setValue(IDLE);
            }
            scheduleEvent(myEndOfPostOperationStayAction, myPostOpStayTime.getValue());
        }
    }

    protected class EndOfPostOperationStayAction implements EventActionIfc {

        public void action(JSLEvent evt) {
            if (myNonOpPatientQ.getValue() > 0.0) {
                myNonOpPatientQ.decrement();
                scheduleEvent(myNonOperationPatientEndOfStayAction, myNonOpPatientStayTime.getValue());
            } else if (myOpPatientQ.getValue() > 0.0) {
                myOpPatientQ.decrement();
                scheduleEvent(myEndOfPreOperationStayAction, myPreOpStayTime.getValue());
            } else {
                myAvailableBeds.increment();
            }
        }
    }

    protected class OpenOperatingRoomAction implements EventActionIfc {

        public void action(JSLEvent evt) {
            myORRoomOpenStatus.setValue(OPEN);
            if ((myORRoomIdleStatus.getValue() == IDLE) && (myOpRoomQ.getValue() > 0.0)) {
                myOpRoomQ.decrement();
                myORRoomIdleStatus.setValue(BUSY);
                scheduleEvent(myEndOfOperationAction, myOperationTime.getValue());
            }
            scheduleEvent(myCloseOperatingRoomAction, myOpRoomOpenTime.getValue());
        }
    }

    protected class CloseOperatingRoomAction implements EventActionIfc {

        public void action(JSLEvent evt) {
            myORRoomOpenStatus.setValue(CLOSED);
            scheduleEvent(myOpenOperatingRoomAction, myOpRoomCloseTime.getValue());
        }
    }

    protected class BedObserver implements ObserverIfc {

        public void update(Object arg0, Object arg1) {
            Variable v = (Variable) arg0;

            if (v.checkForUpdate()) {
                double na = myAvailableBeds.getValue(); // current value
                double beds = myAvailableBeds.getInitialValue();
                myNumBusyBeds.setValue(beds - na);
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        Simulation sim = new Simulation("HospitalWard");
                
        // create the model element and attach it to the main model
        new HospitalWard(sim.getModel());

        // set the parameters of the experiment
        sim.setNumberOfReplications(30);
        sim.setLengthOfWarmUp(1000.0);
        sim.setLengthOfReplication(11000.0);

//        sim.turnOnDefaultEventTraceReport();
        ExecutiveTraceReport etr = sim.getDefaultExecutiveTraceReport();
        etr.setOffTime(100.0);
        
        // tell the experiment to run
        sim.run();

        SimulationReporter r = sim.makeSimulationReporter();
        
        r.writeAcrossReplicationSummaryStatistics();

    }
}
