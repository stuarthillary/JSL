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

/** Represents the Triangular distribution with
 * parameters - minimum value, maximum value and most likely value
 */
public class Triangular extends Distribution implements ContinuousDistributionIfc, InverseCDFIfc {

    /**
     * myMin the minimum value of the distribution
     */
    private double myMin;

    /**
     * myMax the maximum value of the distribution
     */
    private double myMax;

    /**
     *myMax the maximum value of the distribution
     */
    private double myMode;

    /**
     *myRange = myMax - myMin
     */
    private double myRange;

    /** Constructs a Triangular distribution with
     * min = 0.0, mode = 0.0, and max = 1.0
     */
    public Triangular() {
        this(0.0, 0.0, 1.0, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a Triangular distribution with
     * min = parameters[0], mode = parameters[1], max = parameters[2]
     * @param parameters The array of parameters
     */
    public Triangular(double[] parameters) {
        this(parameters[0], parameters[1], parameters[2], RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a Triangular distribution with
     * min = parameters[0], mode = parameters[1], max = parameters[2]
     * @param parameters The array of parameters
     * @param rng
     */
    public Triangular(double[] parameters, RngIfc rng) {
        this(parameters[0], parameters[1], parameters[2], rng);
    }

    /** Constructs a Triangular distribution with min, mode, and max
     *
     * @param min The minimum value of the distribution
     * @param mode The mode of the distribution
     * @param max The maximum value of the distribution
     */
    public Triangular(double min, double mode, double max) {
        this(min, mode, max, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a Triangular distribution with min, mode, and max
     *
     * @param min The minimum value of the distribution
     * @param mode The mode of the distribution
     * @param max The maximum value of the distribution
     * @param rng A RngIfc
     */
    public Triangular(double min, double mode, double max, RngIfc rng) {
        super(rng);
        setParameters(min, mode, max);
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    public final Triangular newInstance() {
        return (new Triangular(getParameters()));
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    public final Triangular newInstance(RngIfc rng) {
        return (new Triangular(getParameters(), rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    public final Triangular newAntitheticInstance() {
        RngIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    /** Sets the minimum, most likely and maximum value of the triangular distribution to the private data members myMin, myMode and myMax resp
     *  throws IllegalArgumentException when the min &gt;mode, min &gt;= max, mode &gt; max
     *
     * @param min The minimum value of the distribution
     * @param mode The mode of the distribution
     * @param max The maximum value of the distribution
     */
    public final void setParameters(double min, double mode, double max) {

        if (min > mode) {
            throw new IllegalArgumentException("min must be <= mode");
        }

        if (min >= max) {
            throw new IllegalArgumentException("min must be < max");
        }

        if (mode > max) {
            throw new IllegalArgumentException("mode must be <= max");
        }

        myMode = mode;
        myMin = min;
        myMax = max;
        myRange = myMax - myMin;
    }

    /** Returns true if the parameters are valid for the distribution
     *
     * min = param[0]
     * mode = param[1]
     *  max = param[2]
     * @param param
     * @return
     */
    public static boolean checkParameters(double[] param) {
        if (param == null) {
            return false;
        }
        if (param.length != 3) {
            return false;
        }
        double min = param[0];
        double mode = param[1];
        double max = param[2];

        if (min > mode) {
            return false;
        }

        if (min >= max) {
            return false;
        }

        if (mode > max) {
            return false;
        }

        return true;
    }

    public final double getMinimum() {
        return (myMin);
    }

    public final double getMode() {
        return (myMode);
    }

    public final double getMaximum() {
        return (myMax);
    }

    public final double getMean() {
        return (myMin + myMax + myMode) / 3.0;
    }

    public final double getMoment3() {
        return (1 / 10) * ((myMin * myMin * myMin) + (myMode * myMode * myMode) + (myMax * myMax * myMax) + (myMin * myMin * myMode) + (myMin * myMin * myMax) + (myMode * myMode * myMin) + (myMode * myMode * myMax) + (myMax * myMax * myMin) + (myMax * myMax * myMode) + (myMin * myMode * myMax));
    }

    public final double getMoment4() {
        return ((1 / 135) * ((myMin * myMin + myMode * myMode + myMax * myMax - myMin * myMode - myMin * myMax - myMode * myMax) * (myMin * myMin + myMode * myMode + myMax * myMax - myMin * myMode - myMin * myMax - myMode * myMax)) + 4 * ((1 / 270) * (myMin + myMode - (2 * myMax)) * (myMin + myMax - (2 * myMode)) * (myMode + myMax - (2 * myMin)) * ((myMin + myMode + myMax) / 3)) + ((1 / 3) * (myMin * myMin + myMode * myMode + myMax * myMax - myMin * myMode - myMin * myMax - myMode * myMax) * ((myMin + myMode + myMax) / 3) * ((myMin + myMode + myMax) / 3)) + ((myMin + myMode + myMax) / 3) * ((myMin + myMode + myMax) / 3) * ((myMin + myMode + myMax) / 3) * ((myMin + myMode + myMax) / 3));
    }

    public final double getVariance() {
        return ((myMin * myMin) + (myMax * myMax) + (myMode * myMode) - (myMax * myMin) - (myMin * myMode) - (myMax * myMode)) / 18.0;
    }

    public final double pdf(double x) {

        // Right triangular, mode = max
        if (myMode == myMax) {
            if ((myMin <= x) && (x <= myMax)) {
                return (2.0 * (x - myMin) / (myRange * myRange));
            } else {
                return (0.0);
            }
        }

        // Left triangular, min = mode
        if (myMin == myMode) {
            if ((myMin <= x) && (x <= myMax)) {
                return (2.0 * (myMax - x) / (myRange * myRange));
            } else {
                return (0.0);
            }
        }

        // regular triangular min < mode < max

        if ((myMin <= x) && (x <= myMode)) {
            return (2.0 * (x - myMin) / (myRange * (myMode - myMin)));
        } else if ((myMode < x) && (x <= myMax)) {
            return (2.0 * (myMax - x) / (myRange * (myMax - myMode)));
        } else {
            return (0.0);
        }
    }

    public final double cdf(double x) {

        // Right triangular, mode = max
        if (myMode == myMax) {
            if (x < myMin) {
                return (0.);
            } else if ((myMin <= x) && (x <= myMax)) {
                double y = (x - myMin) / myRange;
                return (y * y);
            } else {
                return (1.0);
            }
        }

        // Left triangular, min = mode
        if (myMin == myMode) {
            if (x < myMin) {
                return (0.);
            } else if ((myMin <= x) && (x <= myMax)) {
                double y = (myMax - x) / myRange;
                return (1.0 - y * y);
            } else {
                return (1.0);
            }
        }

        // regular triangular min < mode < max

        if (x < myMin) {
            return (0.0);
        } else if ((myMin <= x) && (x <= myMode)) {
            return (((x - myMin) * (x - myMin)) / (myRange * (myMode - myMin)));
        } else if ((myMode < x) && (x <= myMax)) {
            return (1.0 - ((myMax - x) * (myMax - x)) / (myRange * (myMax - myMode)));
        } else {
            return (1.0);
        }

    }

    /** Returns the inverse cumulative distribution function of the triangular distribution
     * throws IllegalArgumentException if the value of the argument passed is beyond the range [0,1]
     *
     * @param p the cumulative probability that requires the corresponding point
     * @return double the value in the triangular distribution at which the cumulative distribution funtion equals the value of p
     *
     */
    public final double invCDF(double p) {
        if ((p < 0.0) || (p > 1.0)) {
            throw new IllegalArgumentException("Probability must be [0,1]");
        }

        // if X ~ triang(0,(mode-min)/(max-min),1) then Y = min + (max-min)*X ~ triang(min, mode, max)
        // get parameters for triang(0,(mode-min)/(max-min),1)

        double c = (myMode - myMin) / myRange;

        // get the invCDF for a triang(0,c,1)

        double x;

        if (c == 0.0) { // left triangular, mode equals min
            x = 1.0 - Math.sqrt(1 - p);
        } else if (c == 1.0) { //right triangular, mode equals max
            x = Math.sqrt(p);
        } else {
            if (p < c) {
                x = Math.sqrt(c * p);
            } else {
                x = 1.0 - Math.sqrt((1.0 - c) * (1.0 - p));
            }
        }
        // scale it back to original scale
        return (myMin + myRange * x);
    }

    /** Gets the kurtosis of the distribution
     * mu4/mu2^2, www.mathworld.wolfram.com/Kurtosis.html
     * www.mathworld.wolfram.com/TriangularDistribution.html
     * @return the kurtosis
     */
    public final double getKurtosis() {
        return (2.4);
    }

    /** Gets the skewness of the distribution
     *  mu3/mu2^(3/2), www.mathworld.wolfram.com/Skewness.html
     *  www.mathworld.wolfram.com/TriangularDistribution.html
     * @return the skewness
     */
    public final double getSkewness() {
        double mu3 = -(myMin + myMax - 2.0 * myMode) * (myMin + myMode - 2.0 * myMax) * (myMax + myMode - 2.0 * myMin) / 270.0;
        double mu2 = getVariance();
        return (mu3 / Math.pow(mu2, (3 / 2)));
    }

    /** Sets the parameters for the distribution
     * parameters[0] min The minimum value of the distribution
     * parameters[1] mode The mode of the distribution
     * parameters[2] max The maximum value of the distribution
     *
     * @param parameters an array of doubles representing the parameters for
     * the distribution
     */
    public void setParameters(double[] parameters) {
        setParameters(parameters[0], parameters[1], parameters[2]);
    }

    /** Gets the parameters for the distribution
     *
     * @return Returns an array of the parameters for the distribution
     */
    public double[] getParameters() {
        double[] param = new double[3];
        param[0] = myMin;
        param[1] = myMode;
        param[2] = myMax;
        return (param);
    }
}
