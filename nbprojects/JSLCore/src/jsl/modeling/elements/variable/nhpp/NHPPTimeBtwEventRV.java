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
package jsl.modeling.elements.variable.nhpp;

import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.distributions.Exponential;

/**
 *
 */
public class NHPPTimeBtwEventRV extends RandomVariable {

    /** Holds the time that the cycle started, where a cycle
     *  is the time period over which the rate function is defined.
     *
     */
    protected double myCycleStartTime;

    /** The length of a cycle if it repeats
     *
     */
    protected double myCycleLength;

    /** The number of cycles completed if cycles
     *
     */
    protected int myNumCycles;

    /** Holds the time of the last event from the underlying Poisson process
     *
     */
    protected double myPPTime;

    /** Supplied to invert the rate function.
     *
     */
    protected InvertibleCumulativeRateFunctionIfc myRateFunction;

    /** If supplied and the repeat flag is false then this rate will
     *  be used after the range of the rate function has been passed
     *
     */
    protected double myLastRate = Double.NaN;

    /** Indicates whether or not the rate function should repeat
     *  when its range has been covered
     *
     */
    protected boolean myRepeatFlag = true;

    /** Turned on if the time goes past the rate function's range
     *  and a last rate was supplied
     *
     */
    protected boolean myUseLastRateFlag = false;

    /** Used to schedule the end of cycles if they repeat
     *
     */
    protected EndOfCycle myCycle;

    /**
     *
     * @param parent the parent
     * @param rateFunction the rate function
     */
    public NHPPTimeBtwEventRV(ModelElement parent, InvertibleCumulativeRateFunctionIfc rateFunction) {
        this(parent, rateFunction, Double.NaN, null);
    }

    /**
     *
     * @param parent the parent
     * @param rateFunction the rate function
     * @param name the name
     */
    public NHPPTimeBtwEventRV(ModelElement parent, InvertibleCumulativeRateFunctionIfc rateFunction, String name) {
        this(parent, rateFunction, Double.NaN, name);
    }

    /**
     *
     * @param parent the parent
     * @param rateFunction the rate function
     * @param lastRate the last rate
     */
    public NHPPTimeBtwEventRV(ModelElement parent, InvertibleCumulativeRateFunctionIfc rateFunction, double lastRate) {
        this(parent, rateFunction, lastRate, null);
    }

    /**
     *
     * @param parent the parent
     * @param rateFunction the rate function
     * @param lastRate the last rate
     * @param name the name
     */
    public NHPPTimeBtwEventRV(ModelElement parent, InvertibleCumulativeRateFunctionIfc rateFunction, double lastRate, String name) {
        super(parent, new Exponential(), name);

        setRateFunction(rateFunction);

        if (!Double.isNaN(lastRate)) {
            if (lastRate < 0.0) {
                throw new IllegalArgumentException("The rate must be >= 0");
            }

            if (lastRate >= Double.POSITIVE_INFINITY) {
                throw new IllegalArgumentException("The rate must be < infinity");
            }

            myLastRate = lastRate;
            myRepeatFlag = false;
        }

        this.setResetInitialParametersWarningFlag(false);
        if (myRepeatFlag == true) {
            myCycle = new EndOfCycle(this);
            myCycleLength = myRateFunction.getTimeRangeUpperLimit() - myRateFunction.getTimeRangeLowerLimit();
        }

    }

    /** Returns the rate function
     *
     * @return the function
     */
    public InvertibleCumulativeRateFunctionIfc getRateFunction() {
        return myRateFunction;
    }

    /** Sets the rate function for the random variable.  Must not be null
     *
     * @param rateFunction the rate function
     */
    protected final void setRateFunction(InvertibleCumulativeRateFunctionIfc rateFunction) {
        if (rateFunction == null) {
            throw new IllegalArgumentException("The rate function must not be null");
        }
        myRateFunction = rateFunction;
    }

    @Override
    protected void initialize() {
        myCycleStartTime = getTime();
        myPPTime = myCycleStartTime;
        myNumCycles = 0;
        myUseLastRateFlag = false;
        Exponential e = (Exponential) myRandomSource;
        e.setMean(1.0);

        //   	if (myCycle != null)
        //   		myCycle.scheduleEvent(myCycleLength);
    }

    @Override
    public final double getValue() {

        if (myUseLastRateFlag == true) {
            // if this option is on the exponential distribution
            // should have been set to use the last rate
            // just return the time between arrivals
            return (myRandomSource.getValue());
        }

        double t = getTime(); // the current time
        //System.out.println("Current time = " + t);
        // exponential time btw events for rate 1 PP
        double x = myRandomSource.getValue();
        // compute the time of the next event on the rate 1 PP scale
        double tppne = myPPTime + x;
        // tne cannot go past the rate range of the cumulative rate function
        // if this happens then the corresponding time will be past the
        // time range of the rate function
        double crul = myRateFunction.getCumulativeRateRangeUpperLimit();
        //System.out.println("tppne = " + tppne);
        //System.out.println("crul =" + crul);
        if (tppne >= crul) {
            // compute the residual into the next cycle
            double residual = tppne - crul;
            //System.out.println("residual = " + residual);
            // must either repeat or use constant rate forever
            if (myRepeatFlag == false) {
                // a last rate has been set, use constant rate forever
                myUseLastRateFlag = true;
                //System.out.println("setting use last rate flag");
                // set source for last rate, will be used from now on
                Exponential e = (Exponential) myRandomSource;
                e.setMean(1.0 / myLastRate);
                // need to use the residual amount, to get the time of the next event
                // using the inverse function for the final constant rate
                double tone = myRateFunction.getTimeRangeUpperLimit() + residual / myLastRate;
                //System.out.println("computing tone using residual: tone = " + tone);
                return (tone - t);
            }

            //  set up to repeat
            myPPTime = residual;
            myNumCycles++;
//			myCycleStartTime = myRateFunction.getTimeRangeUpperLimit();
        } else {
            myPPTime = tppne;
        }

//		double nt = myCycleStartTime + myRateFunction.getInverseCumulativeRate(myPPTime);
        double nt = myCycleLength * myNumCycles + myRateFunction.getInverseCumulativeRate(myPPTime);

        double tbe = nt - t;
        //System.out.println("nt = " + nt);
        //System.out.println("tbe = " + tbe);
        return (tbe);
    }

    protected class EndOfCycle extends SchedulingElement {

        public EndOfCycle(ModelElement parent) {
            super(parent);
        }

        /* (non-Javadoc)
         * @see jsl.modeling.SchedulingElement#handleEvent(jsl.modeling.JSLEvent)
         */
        @Override
        protected void handleEvent(JSLEvent event) {
            myCycleStartTime = getTime();
            rescheduleEvent(event, myCycleLength);
        }

    }
}
