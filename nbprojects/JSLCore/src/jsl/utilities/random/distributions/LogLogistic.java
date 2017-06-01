/*
 * Created on Mar 24, 2007
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
import jsl.utilities.statistic.Statistic;

/**
 * @author rossetti
 *
 */
public class LogLogistic extends Distribution implements ContinuousDistributionIfc, InverseCDFIfc {

    private double myShape; // alpha

    private double myScale;  // beta

    /**
     *
     * @param shape
     * @param scale
     */
    public LogLogistic(double shape, double scale) {
        this(shape, scale, RNStreamFactory.getDefault().getStream());
    }

    /**
     *
     * @param parameters
     */
    public LogLogistic(double[] parameters) {
        this(parameters[0], parameters[1], RNStreamFactory.getDefault().getStream());
    }

    /**
     *
     * @param parameters
     * @param rng
     */
    public LogLogistic(double[] parameters, RngIfc rng) {
        this(parameters[0], parameters[1], rng);
    }

    /**
     *
     * @param shape
     * @param scale
     * @param rng
     */
    public LogLogistic(double shape, double scale, RngIfc rng) {
        super(rng);
        setShape(shape);
        setScale(scale);
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    public final LogLogistic newInstance() {
        return (new LogLogistic(getParameters()));
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    public final LogLogistic newInstance(RngIfc rng) {
        return (new LogLogistic(getParameters(), rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    public final LogLogistic newAntitheticInstance() {
        RngIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    /** Sets the shape parameter
     * @param shape The shape parameter must &gt; 0.0
     */
    public final void setShape(double shape) {
        if (shape <= 0) {
            throw new IllegalArgumentException("Shape parameter must be positive");
        }
        myShape = shape;
    }

    /** Sets the scale parameter
     * @param scale The scale parameter must be &gt; 0.0
     */
    public final void setScale(double scale) {
        if (scale <= 0) {
            throw new IllegalArgumentException("Scale parameter must be positive");
        }
        myScale = scale;
    }

    /** Gets the shape
     * @return The shape parameter as a double
     */
    public final double getShape() {
        return myShape;
    }

    /** Gets the scale parameter
     * @return The scale parameter as a double
     */
    public final double getScale() {
        return myScale;
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.Distribution#pdf(double)
     */
    public final double pdf(double x) {
        if (x > 0.0) {
            double t1 = x / myScale;
            double n = myShape * Math.pow(t1, myShape - 1.0);
            double t2 = Math.pow(t1, myShape);
            double d = myScale * (1.0 + t2) * (1.0 + t2);
            return (n / d);
        } else {
            return 0.0;
        }
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.Distribution#cdf(double)
     */
    public final double cdf(double x) {// alpha = shape, beta = scale
        if (x > 0.0) {
            double y = Math.pow(x / myScale, -myShape);
            return (1.0 / (1.0 + y));
        } else {
            return 0.0;
        }
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.Distribution#getMean()
     */
    public final double getMean() {
        if (myShape <= 1.0) {
            return Double.NaN;
        }
        double theta = Math.PI / myShape;
        double csctheta = 1.0 / Math.sin(theta);
        return (myScale * theta * csctheta);
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.Distribution#getVariance()
     */
    public final double getVariance() {// alpha = shape, beta = scale
        if (myShape <= 2.0) {
            return Double.NaN;
        }
        double theta = Math.PI / myShape;
        double csctheta = 1.0 / Math.sin(theta);
        double csc2theta = 1.0 / Math.sin(2.0 * theta);
        return (myScale * myScale * theta * (2.0 * csc2theta - theta * csctheta * csctheta));
    }

    /** Provides the inverse cumulative distribution function for the distribution
     * @param p The probability to be evaluated for the inverse, p must be [0,1] or
     * an IllegalArgumentException is thrown
     * p = 0.0 returns 0.0
     * p = 1.0 returns Double.POSITIVE_INFINITY
     * @see jsl.utilities.random.distributions.Distribution#invCDF(double)
     */
    public final double invCDF(double p) {// alpha = shape, beta = scale
        if ((p < 0.0) || (p > 1.0)) {
            throw new IllegalArgumentException("Probability must be [0,1]");
        }

        if (p <= 0.0) {
            return 0.0;
        }

        if (p >= 1.0) {
            return Double.POSITIVE_INFINITY;
        }

        double c = p / (1.0 - p);
        return (myScale * Math.pow(c, 1.0 / myShape));
    }

    /** Sets the parameters for the distribution with
     * shape = parameters[0] and scale = parameters[1]
     *
     * @param parameters an array of doubles representing the parameters for
     * the distribution
     */
    public final void setParameters(double[] parameters) {
        setShape(parameters[0]);
        setScale(parameters[1]);
    }

    /** Gets the parameters for the distribution
     *
     * @return Returns an array of the parameters for the distribution
     */
    public final double[] getParameters() {
        double[] param = new double[2];
        param[0] = myShape;
        param[1] = myScale;
        return (param);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        //		 alpha = shape, beta = scale
        LogLogistic x = new LogLogistic(3.0, 2.0);
        System.out.println(x);
        Statistic s = new Statistic();
        Statistic muhat = new Statistic("Estimated mean");
        Statistic varhat = new Statistic("Estimated variance");
        int m = 1000;
        int n = 1000;
        for (int j = 1; j <= m; j++) {
            for (int i = 1; i <= n; i++) {
                s.collect(x.getValue());
            }
            muhat.collect(s.getAverage());
            varhat.collect(s.getVariance());
            s.reset();
        }
        System.out.println(muhat);
        System.out.println(varhat);
    }
}
