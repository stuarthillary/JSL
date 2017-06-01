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
package jsl.utilities.random.distributions;

import jsl.utilities.random.AbstractRandom;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *
 * @author rossetti
 */
public class JohnsonB extends AbstractRandom {

    private double myAlpha1;

    private double myAlpha2;

    private double myMin;

    private double myMax;

    private RngIfc myRNG;

    public JohnsonB() {
        this(0.0, 1.0, 0.0, 1.0, RNStreamFactory.getDefault().getStream());
    }

    public JohnsonB(double[] parameters) {
        this(parameters[0], parameters[1], parameters[2], parameters[3],
                RNStreamFactory.getDefault().getStream());
    }

    public JohnsonB(double[] parameters, RngIfc rng) {
        this(parameters[0], parameters[1], parameters[2], parameters[3], rng);
    }

    public JohnsonB(double alpha1, double alpha2, double min, double max) {
        this(alpha1, alpha2, min, max, RNStreamFactory.getDefault().getStream());
    }

    public JohnsonB(double alpha1, double alpha2, double min, double max, RngIfc rng) {
        setParameters(alpha1, alpha2, min, max);
        setRandomNumberGenerator(rng);
    }

    public final JohnsonB newInstance() {
        return (new JohnsonB(getParameters()));
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    public final JohnsonB newInstance(RngIfc rng) {
        return (new JohnsonB(getParameters(), rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    public final JohnsonB newAntitheticInstance() {
        RngIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    public void setParameters(double alpha1, double alpha2, double min, double max) {
        setAlpha1(alpha1);
        setAlpha2(alpha2);
        setRange(min, max);
    }

    public void setParameters(double[] parameters) {
        setAlpha1(parameters[0]);
        setAlpha2(parameters[1]);
        setRange(parameters[2], parameters[3]);
    }

    public double[] getParameters() {
        double[] p = new double[4];
        p[0] = getAlpha1();
        p[1] = getAlpha2();
        p[2] = getMin();
        p[3] = getMax();
        return p;
    }

    public double getValue() {
        double u = myRNG.randU01();
        double z = Normal.stdNormalInvCDF(u);
        double y = Math.exp((z - myAlpha1) / myAlpha2);
        double x = (myMin + myMax * y) / (y + 1.0);
        return x;
    }

    public void resetStartStream() {
        myRNG.resetStartStream();
    }

    public void resetStartSubstream() {
        myRNG.resetStartSubstream();
    }

    public void advanceToNextSubstream() {
        myRNG.advanceToNextSubstream();
    }

    public void setAntitheticOption(boolean flag) {
        myRNG.setAntitheticOption(flag);
    }

    public boolean getAntitheticOption() {
        return myRNG.getAntitheticOption();
    }

    public double getMin() {
        return myMin;
    }

    public double getAlpha1() {
        return myAlpha1;
    }

    public void setAlpha1(double alpha1) {
        myAlpha1 = alpha1;
    }

    public double getAlpha2() {
        return myAlpha2;
    }

    public void setAlpha2(double alpha2) {
        if (alpha2 <= 0) {
            throw new IllegalArgumentException("alpha2 must be > 0");
        }
        myAlpha2 = alpha2;
    }

    public double getMax() {
        return myMax;
    }

    public void setRange(double min, double max) {
        if (max <= min) {
            throw new IllegalArgumentException("the min must be < than the max");
        }
        myMin = min;
        myMax = max;
    }

    public RngIfc getRandomNumberGenerator() {
        return (myRNG);
    }

    public void setRandomNumberGenerator(RngIfc rng) {
        if (rng == null) {
            throw new NullPointerException("RngIfc rng must be non-null");
        }
        myRNG = rng;
    }
}
