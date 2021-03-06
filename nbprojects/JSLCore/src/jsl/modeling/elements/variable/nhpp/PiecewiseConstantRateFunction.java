/*
 * Created on Aug 30, 2007
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

/**
 * @author rossetti
 *
 */
public class PiecewiseConstantRateFunction extends PiecewiseRateFunction {

    /** Creates a PiecewiseConstantRateFunction given the first
     *  duration and rate pair, other pairs are added via 
     *  addRateSegment()
     * 
     * @param duration the first duration
     * @param rate  the first rate
     */
    public PiecewiseConstantRateFunction(double duration, double rate) {
        super();

        // create the first segment
        addFirstSegment(duration, rate);

    }

    /** Adds the segments represented by the duration, rate pairs
     *  The arrays must be the same length, not null, and have at 
     *  least 1 pair
     * 
     * @param durations the durations
     * @param rates the rates
     */
    public PiecewiseConstantRateFunction(double[] durations, double[] rates) {
        super();
        if (durations == null) {
            throw new IllegalArgumentException("durations was null");
        }
        if (rates == null) {
            throw new IllegalArgumentException("rates was null");
        }
        if (rates.length == 0) {
            throw new IllegalArgumentException("rates length was zero");
        }
        if (durations.length == 0) {
            throw new IllegalArgumentException("durations length was zero");
        }
        if (rates.length != durations.length) {
            throw new IllegalArgumentException("durations and rates must have the same length");
        }
        addFirstSegment(durations[0], rates[0]);
        for (int i = 1; i < rates.length; i++) {
            addRateSegment(durations[i], rates[i]);
        }
    }

    /** Returns a copy of the piecewise constance rate function
     * 
     * @return the piecewise constance rate function
     */
    @Override
    public PiecewiseConstantRateFunction newInstance() {
        return new PiecewiseConstantRateFunction(getDurations(), getRates());
    }

    /** Returns a copy of the piecewise constance rate function
     *  with each rate multiplied by the factor
     * 
     * @param factor multiplied by the factor
     * @return the piecewise constance rate function
     */
    @Override
    public PiecewiseConstantRateFunction newInstance(double factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("The multiplication factor must be > 0");
        }
        double[] rates = getRates();
        for (int i = 0; i < rates.length; i++) {
            rates[i] = rates[i] * factor;
        }
        return new PiecewiseConstantRateFunction(getDurations(), rates);
    }

    protected final void addFirstSegment(double duration, double rate) {
        ConstantRateSegment first = new ConstantRateSegment(0.0, 0.0, duration, rate);

        myRateSegments.add(first);

        if (rate > myMaxRate) {
            myMaxRate = rate;
        }

        if (rate < myMinRate) {
            myMinRate = rate;
        }
    }

    /** Allows the construction of the piecewise rate function starting at time zero.
     *  The user supplies the arrival rate and the duration for that arrival
     *  rate, by consecutive calls to addRateSegment().  
     * 
     * @param rate must be &gt; 0, and less than Double.POSITIVE_INFINITY
     * @param duration must be &gt; 0
     */
    @Override
    public final void addRateSegment(double duration, double rate) {

        int k = myRateSegments.size();
        RateSegmentIfc prev = myRateSegments.get(k - 1);

        ConstantRateSegment next = new ConstantRateSegment(prev.getCumulativeRateUpperLimit(),
                prev.getUpperTimeLimit(), duration, rate);

        myRateSegments.add(next);

        if (rate > myMaxRate) {
            myMaxRate = rate;
        }

        if (rate < myMinRate) {
            myMinRate = rate;
        }

    }

    /** Multiplies each rate by the factor. Changes each
     *  rate segment in this function
     * 
     * @param factor the factor to multiply
     */
    public void multiplyRates(double factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("The multiplication factor must be > 0");
        }
        int n = myRateSegments.size();
        ConstantRateSegment first = (ConstantRateSegment) myRateSegments.get(0);
        first.setRate(0.0, factor * first.getRate());
        for (int i = 1; i < n; i++) {
            ConstantRateSegment p = (ConstantRateSegment) myRateSegments.get(i - 1);
            ConstantRateSegment c = (ConstantRateSegment) myRateSegments.get(i);
            c.setRate(p.getCumulativeRateUpperLimit(), factor * c.getRate());
        }
    }

    /** Get the rates as an array
     * 
     * @return the rates as an array
     */
    @Override
    public double[] getRates() {
        double[] rates = new double[myRateSegments.size()];
        int i = 0;
        for (RateSegmentIfc s : myRateSegments) {
            ConstantRateSegment c = (ConstantRateSegment) s;
            rates[i] = c.getRate();
            i++;
        }
        return rates;
    }

    /** Get the durations as an array
     * 
     * @return the durations as an array
     */
    @Override
    public double[] getDurations() {
        double[] durations = new double[myRateSegments.size()];
        int i = 0;
        for (RateSegmentIfc s : myRateSegments) {
            durations[i] = s.getTimeWidth();
            i++;
        }
        return durations;
    }

    @Override
    public final boolean contains(double time) {
        return (getTimeRangeLowerLimit() <= time) && (time < getTimeRangeUpperLimit());
    }

    /** Searches for the interval that the supplied time
     *  falls within.  Returns -1 if no interval is found
     *  
     *  Interval indexing starts at index 0 (i.e. 0 is the first interval, 
     *  silly Java zero based indexing)
     * 
     * @param time the time to look up
     * @return the index of the interval
     */
    @Override
    public final int findTimeInterval(double time) {

        int k = -1;
        for (RateSegmentIfc i : myRateSegments) {
            k = k + 1;
            if (time < i.getUpperTimeLimit()) {
                return (k);
            }
        }
        return (-1);
    }
}
