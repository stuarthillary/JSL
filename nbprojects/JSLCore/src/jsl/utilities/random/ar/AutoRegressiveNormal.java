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
package jsl.utilities.random.ar;

import java.util.HashMap;

import jsl.utilities.ControllableIfc;
import jsl.utilities.Controls;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.SampleIfc;
import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *
 */
public class AutoRegressiveNormal implements RandomIfc, SampleIfc, ControllableIfc {

    /** A counter to count the number of created to assign "unique" ids
     */
    private static long myIdCounter_;

    /** The id of this object
     */
    protected long myId;

    /** Holds the name of the statistic for reporting purposes.
     */
    protected String myName;

    private double[] myARCoefficients;

    private double[] myHistory;

    private Normal myErrors;

    private double myX;

    /** Constructs AutoRegressiveNormal given the array of parameter coefficients
     *
     * Throws a NullPointerException if coefficients has not be allocated
     * WARNING:  No checking for valid coefficient values is performed
     *
     * @param coefficients an array of the coefficients for the AR process
     */
    public AutoRegressiveNormal(double[] coefficients) {
        this(0.0, 1.0, coefficients, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs AutoRegressiveNormal given the array of parameter coefficients
     *
     * Throws a NullPointerException if coefficients has not be allocated
     * WARNING:  No checking for valid coefficient values is performed
     *
     * @param mean the mean of the errors
     * @param variance the variance of the errors
     * @param coefficients an array of the coefficients for the AR process
     */
    public AutoRegressiveNormal(double mean, double variance, double[] coefficients) {
        this(mean, variance, coefficients, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs AutoRegressiveNormal given the array of parameter coefficients
     *
     * Throws a NullPointerException if coefficients has not be allocated
     * WARNING:  No checking for valid coefficient values is performed
     *
     * @param mean the mean of the errors
     * @param variance the variance of the errors
     * @param coefficients an array of the coefficients for the AR process
     * @param rng a RngIfc
     */
    public AutoRegressiveNormal(double mean, double variance, double[] coefficients, RngIfc rng) {
        this(mean, variance, coefficients, rng, null);
    }

    /** Constructs AutoRegressiveNormal given the array of parameter coefficients
     *
     * Throws a NullPointerException if coefficients has not be allocated
     * WARNING:  No checking for valid coefficient values is performed
     *
     * @param mean 
     * @param variance 
     * @param coefficients an array of the coefficients for the AR process
     * @param rng a RngIfc
     * @param name
     */
    public AutoRegressiveNormal(double mean, double variance, double[] coefficients, RngIfc rng, String name) {
        setId();
        setName(name);
        myErrors = new Normal(mean, variance, rng);
        setCoefficients(coefficients);
        initializeHistory();
    }

    @Override
    public final String getName() {
        return myName;
    }

    /** Sets the name
     * @param str The name as a string.
     */
    public final void setName(String str) {
        if (str == null) {
            String s = this.getClass().getName();
            int k = s.lastIndexOf(".");
            if (k != -1) {
                s = s.substring(k + 1);
            }
            myName = s;
        } else {
            myName = str;
        }
    }

    @Override
    public final long getId() {
        return (myId);
    }

    @Override
    public final AutoRegressiveNormal newInstance() {
        return (new AutoRegressiveNormal(getMeanOfErrors(), getVarianceOfErrors(), getCoefficients()));
    }

    @Override
    public final AutoRegressiveNormal newInstance(RngIfc rng) {
        return (new AutoRegressiveNormal(getMeanOfErrors(), getVarianceOfErrors(), getCoefficients(), rng));
    }

    /** Returns the underlying random number generator
     *
     * @return
     */
    public final RngIfc getRandomNumberGenerator() {
        return myErrors.getRandomNumberGenerator();
    }

    public final double getMeanOfErrors() {
        return myErrors.getMean();
    }

    public final double getVarianceOfErrors() {
        return myErrors.getVariance();
    }

    /** Gets the number of coefficients for the process
     *
     * @return
     */
    public final int getNumberOfCoefficients() {
        return (myARCoefficients.length);
    }

    /** Sets the parameters for the underlying normal distribution
     * mean = parameters[0] and variance = parameters[1]
     * @param mu
     * @param variance 
     * the distribution
     */
    public final void setNormalDistributionParameters(double mu, double variance) {
        myErrors.setMean(mu);
        myErrors.setVariance(variance);
    }

    /** Returns an array copy of the coefficients
     *
     * @return
     */
    public final double[] getCoefficients() {
        double[] c = new double[myARCoefficients.length];
        System.arraycopy(myARCoefficients, 0, c, 0, myARCoefficients.length);
        return c;
    }

    /** Sets the coefficients of the autoregressive process
     * Throws a NullPointerException if coefficients has not be allocated
     * WARNING:  No checking for valid coefficient values is performed
     * The coefficients are copied for use in the class
     * @param coefficients an array of the coefficients for the AR process
     */
    protected final void setCoefficients(double[] coefficients) {
        if (coefficients == null) {
            throw new NullPointerException("coefficients must be non-null");
        }

        myARCoefficients = new double[coefficients.length];
        System.arraycopy(coefficients, 0, myARCoefficients, 0, coefficients.length);
        myHistory = new double[coefficients.length];
    }

    /** Sets the history of the AR process
     * Throws IllegalArgumentException if history.length does not equal coefficients.length
     * The convention is that history[0] is the most recent value and thus
     * history[history.length-1] is the oldest value
     * the array history is copied for use has the history of the process
     * @param history an array containing the history of the process
     */
    public final void setHistory(double[] history) {
        if (history.length != myHistory.length) {
            throw new IllegalArgumentException("History of the process not the same as the order of the process");
        }
        System.arraycopy(history, 0, myHistory, 0, history.length);
    }

    /** Initialize the history of the AR process, by
     *  filling it with randomly generated values N(0,1)
     *  Obviously, these are iid and cause some
     *  initialization bias
     */
    public final void initializeHistory() {
        for (int i = 0; i < myHistory.length; i++) {
            myHistory[i] = myErrors.getValue();
        }
    }

    /** Sets the mean, variance of the process
     *
     *  parameters[0] = mean
     *  parameters[1] = variance
     * @param parameters
     */
    @Override
    public final void setParameters(double[] parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("The parameters were null");
        }

        if (parameters.length != 2) {
            throw new IllegalArgumentException("The parameters array does not have the necessary length");
        }

        myErrors.setMean(parameters[0]);
        myErrors.setVariance(parameters[1]);
    }

    @Override
    public double[] getParameters() {
        double[] parameters = new double[2];
        parameters[0] = myErrors.getMean();
        parameters[1] = myErrors.getVariance();
        return parameters;
    }

    protected class ARControls extends Controls {

        protected ARControls(ControllableIfc controllable) {
            super(controllable);
            myDoubleArrayControls = new HashMap<String, double[]>();
            myDoubleArrayControls.put("parameters", getParameters());
        }
    }

    @Override
    public Controls makeControls() {
        return new ARControls(this);
    }

    @Override
    public void setControls(Controls controls) {
        if (controls == null) {
            throw new IllegalArgumentException("The supplied controls were null!");
        }

        if (controls.getControllable() != this) {
            throw new IllegalArgumentException("The supplied controls do not belong to this distribution");
        }
        setParameters(controls.getDoubleArrayControl("parameters"));
    }

    @Override
    public double getValue() {
        myX = getARValue() + myErrors.getValue();
        updateHistory(myX);
        return myX;
    }

    @Override
    public final double[] getSample(int sampleSize) {
        double[] x = new double[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            x[i] = getValue();
        }
        return (x);
    }

    @Override
    public final void getSample(double[] values) {
        if (values == null) {
            throw new IllegalArgumentException("The supplied array was null");
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = getValue();
        }
    }

    protected final double getARValue() {
        double sum = 0.0;
        for (int i = 0; i < myHistory.length; i++) {
            sum = sum + myARCoefficients[i] * myHistory[i];
        }
        return (sum);
    }

    protected final void updateHistory(double x) {
        for (int i = 1; i < myHistory.length; i++) {
            myHistory[i] = myHistory[i - 1];
        }
        myHistory[0] = x;
    }

    @Override
    public final void resetStartStream() {
        myErrors.resetStartStream();
    }

    @Override
    public final void resetStartSubstream() {
        myErrors.resetStartSubstream();
    }

    @Override
    public final void advanceToNextSubstream() {
        myErrors.advanceToNextSubstream();
    }

    @Override
    public final void setAntitheticOption(boolean flag) {
        myErrors.setAntitheticOption(flag);
    }

    @Override
    public final boolean getAntitheticOption() {
        return myErrors.getAntitheticOption();
    }

    protected final void setId() {
        myIdCounter_ = myIdCounter_ + 1;
        myId = myIdCounter_;
    }
}
