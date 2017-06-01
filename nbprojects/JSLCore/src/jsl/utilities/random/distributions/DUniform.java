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
package jsl.utilities.random.distributions;

import jsl.utilities.random.rng.RNStreamFactory;

import jsl.utilities.random.rng.RngIfc;

/** Models discrete random variables that are uniformly distributed
 * over a contiguous range of integers.
 */
public class DUniform extends Distribution implements DiscreteDistributionIfc {

    // private data members
    private int myMinimum;

    private int myMaximum;

    private int myRange;

    // constructors
    /** Constructs a discrete uniform over the range {0,1}
     */
    public DUniform() {
        this(0, 1, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a discrete uniform where parameter[0] is the
     * lower limit and parameter[1] is the upper limit of the range.
     *  the lower limit must be &lt; upper limit
     * @param parameters A array containing the lower limit and upper limit
     */
    public DUniform(double[] parameters) {
        this((int) parameters[0], (int) parameters[1],
                RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a discrete uniform where parameter[0] is the
     * lower slimit and parameter[1] is the upper limit of the range.
     *  the lower limit must be &lt; upper limit
     * @param parameters A array containing the lower limit and upper limit
     * @param rng
     */
    public DUniform(double[] parameters, RngIfc rng) {
        this((int) parameters[0], (int) parameters[1], rng);
    }

    /** Constructs a discrete uniform over the supplied range
     *  the lower limit must be &lt; upper limit
     * @param minimum The lower limit of the range
     * @param maximum The upper limit of the range
     */
    public DUniform(int minimum, int maximum) {
        this(minimum, maximum, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a discrete uniform over the supplied range
     *  the lower limit must be &lt; upper limit
     * @param minimum The lower limit of the range
     * @param maximum The upper limit of the range
     * @param rng
     */
    public DUniform(int minimum, int maximum, RngIfc rng) {
        super(rng);
        setRange(minimum, maximum);
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    @Override
    public final DUniform newInstance() {
        return (new DUniform(getParameters()));
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    @Override
    public final DUniform newInstance(RngIfc rng) {
        return (new DUniform(getParameters(), rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    @Override
    public final DUniform newAntitheticInstance() {
        RngIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    /** Gets the distribution's lower limit
     * @return The lower limit
     */
    public final int getMinimum() {
        return (myMinimum);
    }

    /** Gets the distribution's upper limit
     * @return The upper limit
     */
    public final int getMaximum() {
        return (myMaximum);
    }

    /** Sets the range for the distribution
     *  the lower limit must be &lt; upper limit
     * @param minimum The lower limit for the range
     * @param maximum The upper limit for the range
     */
    public final void setRange(int minimum, int maximum) {
        if (minimum >= maximum) {
            throw new IllegalArgumentException("Lower limit must be < upper limit.");
        }
        myMinimum = minimum;
        myMaximum = maximum;
        myRange = myMaximum - myMinimum + 1;
    }

    /** The discrete maximum - minimum + 1
     * 
     * @return the returned range
     */
    public final int getRange(){
        return (int)myRange;
    }
    
    @Override
    public final double cdf(double x) {
        if (x < myMinimum) {
            return 0.0;
        } else if ((x >= myMinimum) && (x <= myMaximum)) {
            return ((Math.floor(x) - myMinimum + 1) / myRange);
        } else //if (x > myMaximum)
        {
            return 1.0;
        }
    }

    /** Provides the inverse cumulative distribution function for the distribution
     * @param prob The probability to be evaluated for the inverse, prob must be [0,1] or
     * an IllegalArgumentException is thrown
     */
    @Override
    public final double invCDF(double prob) {
        if ((prob < 0.0) || (prob > 1.0)) {
            throw new IllegalArgumentException("Probability must be [0,1]");
        }

        return (myMinimum + Math.floor(myRange * prob));
    }

    /** If x is not and integer value, then the probability must be zero
     *  otherwise pmf(int x) is used to determine the probability
     *
     * @param x
     * @return
     */
    @Override
    public final double pmf(double x) {
        if (Math.floor(x) == x) {
            return pmf((int) x);
        } else {
            return 0.0;
        }
    }

    @Override
    public final double getMean() {
        return ((myMinimum + myMaximum) / 2.0);
    }

    @Override
    public final double getVariance() {
        return ((myRange * myRange - 1) / 12.0);
    }

    /** Returns the probability associated with x
     * 
     * @param x
     * @return
     */
    public final double pmf(int x) {
        if ((x < myMinimum) || (x > myMaximum)) {
            return (0.0);
        }
        return (1.0 / myRange);
    }

    /** Sets the parameters for the distribution where parameters[0] is the
     * lowerlimit and parameters[1] is the upper limit of the range.
     *  the lower limit must be &lt; upper limit
     *
     * @param parameters an array of doubles representing the parameters for
     * the distribution
     */
    @Override
    public final void setParameters(double[] parameters) {
        setRange((int) parameters[0], (int) parameters[1]);
    }

    /** Gets the parameters for the distribution
     *
     * @return Returns an array of the parameters for the distribution
     */
    @Override
    public final double[] getParameters() {
        double[] param = new double[2];
        param[0] = myMinimum;
        param[1] = myMaximum;
        return (param);
    }
}
