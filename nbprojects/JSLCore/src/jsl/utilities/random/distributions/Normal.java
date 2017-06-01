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

/** Models normally distributed random variables
 *
 */
public class Normal extends Distribution implements ContinuousDistributionIfc, LossFunctionDistributionIfc, InverseCDFIfc {
    // private attributes

    private double myMean;

    private double myVar;

    private double myStdDev;

    private double nextNormal = 0.0;

    private boolean nextNormalFlag = false;

    private static double baseNorm = Math.sqrt(2 * Math.PI);

    private static double errorFunctionConstant = 0.2316419;

    private static double[] coeffs = {0.31938153, -0.356563782, 1.781477937, -1.821255978, 1.330274429};

    private static double[] a = {-3.969683028665376e+01, 2.209460984245205e+02,
        -2.759285104469687e+02, 1.383577518672690e+02,
        -3.066479806614716e+01, 2.506628277459239e+00};

    private static double[] b = {-5.447609879822406e+01, 1.615858368580409e+02,
        -1.556989798598866e+02, 6.680131188771972e+01, -1.328068155288572e+01};

    private static double[] c = {-7.784894002430293e-03, -3.223964580411365e-01,
        -2.400758277161838e+00, -2.549732539343734e+00,
        4.374664141464968e+00, 2.938163982698783e+00};

    private static double[] d = {7.784695709041462e-03, 3.224671290700398e-01,
        2.445134137142996e+00, 3.754408661907416e+00};

    /** Constructs a normal distribution with mean 0.0 and variance 1.0
     */
    public Normal() {
        this(0.0, 1.0, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a normal distribution with
     * mean = parameters[0] and variance = parameters[1]
     * @param parameters An array with the mean and variance
     */
    public Normal(double[] parameters) {
        this(parameters[0], parameters[1], RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a normal distribution with
     * mean = parameters[0] and variance = parameters[1]
     * @param parameters An array with the mean and variance
     * @param rng
     */
    public Normal(double[] parameters, RngIfc rng) {
        this(parameters[0], parameters[1], rng);
    }

    /** Constructs a normal distribution with mean and variance.
     *
     * @param mean of the distribution
     * @param variance must be &gt; 0
     */
    public Normal(double mean, double variance) {
        this(mean, variance, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a normal distribution with mean and variance.
     *
     * @param mean of the distribution
     * @param variance must be &gt; 0
     * @param rng A RngIfc
     */
    public Normal(double mean, double variance, RngIfc rng) {
        super(rng);
        setMean(mean);
        setVariance(variance);
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    @Override
    public final Normal newInstance() {
        return (new Normal(getParameters()));
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    @Override
    public final Normal newInstance(RngIfc rng) {
        return (new Normal(getParameters(), rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    @Override
    public final Normal newAntitheticInstance() {
        RngIfc ac = myRNG.newAntitheticInstance();
        return newInstance(ac);
    }

    /** Sets the mean of this normal distribution
     *
     * @param mean of the distribution
     */
    public final void setMean(double mean) {
        myMean = mean;
    }

    @Override
    public final double getMean() {
        return myMean;
    }

    /** Sets the variance of this normal distribution
     *
     * @param variance of the distribution, must be &gt; 0
     */
    public final void setVariance(double variance) {
        if (variance <= 0) {
            throw new IllegalArgumentException("Variance must be positive");
        }
        myVar = variance;
        myStdDev = getStandardDeviation();
    }

    @Override
    public final double getVariance() {
        return myVar;
    }

    /** Gets a random variate from this normal distribution
     *  via the polar method.
     *
     * @return a normally distributed random variate
     */
    public final double polarMethodRandomVariate() {
        if (nextNormalFlag == true) {
            nextNormalFlag = false;
            return (myMean + myStdDev * nextNormal);
        } else {
            double u1, u2;
            double v1, v2;
            double w, y;
            do {
                u1 = myRNG.randU01();
                u2 = myRNG.randU01();
                v1 = 2.0 * u1 - 1.0;
                v2 = 2.0 * u2 - 1.0;
                w = v1 * v1 + v2 * v2;
            } while (w > 1.0);
            y = Math.sqrt((-2.0 * Math.log(w) / w));

            nextNormal = v2 * y;
            nextNormalFlag = true;
            return (myMean + myStdDev * (v1 * y));
        }
    }

    /** Computes the cumulative distribution function for a standard
     *  normal distribution
     *  from Abramovitz  and Stegun, see also Didier H. Besset
     *  Object-oriented Implementation of Numerical Methods, Morgan-Kaufmann (2001)
     *
     * @param z the z-ordinate to be evaluated
     * @return the P(Z&lt;=z) for standard normal
     */
    public final static double stdNormalCDF(double z) {

        if (z == 0) {
            return 0.5;
        } else if (z > 0) {
            return (1 - stdNormalCDF(-z));
        }

        double t = 1 / (1 - errorFunctionConstant * z);

        double phi = coeffs[0] + t * (coeffs[1] + t * (coeffs[2] + t * (coeffs[3] + t * coeffs[4])));

        return (t * phi * stdNormalPDF(z));
    }

    /** Computes the pdf function for a standard normal distribution
     *  from Abramovitz and Stegun, see also Didier H. Besset
     *  Object-oriented Implementation of Numerical Methods, Morgan-Kaufmann (2001)
     *
     * @param z the z-ordinate to be evaluated
     * @return the f(z) for standard normal
     */
    public final static double stdNormalPDF(double z) {
        return (Math.exp(-0.5 * z * z) / baseNorm);
    }

    /** Computes the inverse cumulative distribution function for a standard
     *  normal distribution
     * see, W. J. Cody, Rational Chebyshev approximations for the error function
     * Math. Comp. pp 631-638
     * this is without the extra refinement and has relative error of 1.15e-9
     * {@literal http://www.math.uio.no/~jacklam/notes/invnorm/ }
     * @param p the probability to be evaluated, p must be within [0,1]
     * p = 0.0 returns Double.NEGATIVE_INFINTITY
     * p = 1.0 returns Double.POSITIVE_INFINITY
     * @return the "z" value associated with the p
     */
    public final static double stdNormalInvCDF(double p) {
        if ((p < 0.0) || (p > 1.0)) {
            throw new IllegalArgumentException("Supplied probability was " + p + " Probability must be (0,1)");
        }

        if (p <= 0.0) {
            return Double.NEGATIVE_INFINITY;
        }

        if (p >= 1.0) {
            return Double.POSITIVE_INFINITY;
        }

        // define the breakpoints
        double plow = 0.02425;
        double phigh = 1 - plow;

        double r = 0.0;
        double q = 0.0;
        double z = 0.0;
        double x = 0.0;
        double y = 0.0;

        if (p < plow) {// rational approximation for the lower region
            q = Math.sqrt(-2 * Math.log(p));
            x = (((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5]);
            y = ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1);
            z = x / y;
            return (z);
        }

        if (phigh < p) {// rational approximation for upper region
            q = Math.sqrt(-2 * Math.log(1.0 - p));
            x = (((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5]);
            y = ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1);
            z = -x / y;
            return (z);
        }

        // rational approximation for central region
        q = p - 0.5;
        r = q * q;
        x = (((((a[0] * r + a[1]) * r + a[2]) * r + a[3]) * r + a[4]) * r + a[5]) * q;
        y = (((((b[0] * r + b[1]) * r + b[2]) * r + b[3]) * r + b[4]) * r + 1);
        z = x / y;

        return (z);
    }

    /** Computes the complementary cumulative probability for the standard normal
     * distribution function for given value of z
     * @param z The value to be evaluated
     * @return The probability, 1-P{X&lt;=z}
     */
    public static final double stdNormalComplementaryCDF(double z) {
        return (1.0 - stdNormalCDF(z));
    }

    /** Computes the first order loss function for the standard normal
     * distribution function for given value of x, G1(z) = E[max(Z-z,0)]
     * @param z The value to be evaluated
     * @return The loss function value, E[max(Z-z,0)]
     */
    public static final double stdNormalFirstOrderLossFunction(double z) {
        return (-z * stdNormalComplementaryCDF(z) + stdNormalPDF(z));
    }

    /** Computes the 2nd order loss function for the standard normal
     * distribution function for given value of z, G2(z) = (1/2)E[max(Z-z,0)*max(Z-z-1,0)]
     * @param z The value to be evaluated
     * @return The loss function value, (1/2)E[max(Z-z,0)*max(Z-z-1,0)]
     */
    public final static double stdNormalSecondOrderLossFunction(double z) {
        return (0.5 * ((z * z + 1.0) * stdNormalComplementaryCDF(z) - z * stdNormalPDF(z)));
    }

    @Override
    public final double cdf(double x) {
        return (stdNormalCDF((x - myMean) / myStdDev));
    }

    @Override
    public final double pdf(double x) {
        return (stdNormalPDF((x - myMean) / myStdDev) / myStdDev);
    }

    /** Provides the inverse cumulative distribution function for the distribution
     * @param p The probability to be evaluated for the inverse, p must be [0,1] or
     * an IllegalArgumentException is thrown
     * p = 0.0 returns Double.NEGATIVE_INFINTITY
     * p = 1.0 returns Double.POSITIVE_INFINITY
     * @return The inverse cdf evaluated at p
     */
    @Override
    public final double invCDF(double p) {
        double z = stdNormalInvCDF(p);
        return (z * myStdDev + myMean);
    }

    /** Gets the kurtosis of the distribution
     * @return the kurtosis
     */
    public final double getKurtosis() {
        return (0.0);
    }

    /** Gets the skewness of the distribution
     * @return the skewness
     */
    public final double getSkewness() {
        return (0.0);
    }

    /** Computes the complementary cumulative probability
     * distribution function for given value of x
     * @param x The value to be evaluated
     * @return The probability, 1-P{X&lt;=x}
     */
    @Override
    public double complementaryCDF(double x) {
        return (stdNormalComplementaryCDF((x - myMean) / myStdDev));
    }

    /** Computes the first order loss function for the
     * distribution function for given value of x, G1(x) = E[max(X-x,0)]
     * @param x The value to be evaluated
     * @return The loss function value, E[max(X-x,0)]
     */
    @Override
    public double firstOrderLossFunction(double x) {
        return (myStdDev * stdNormalFirstOrderLossFunction((x - myMean) / myStdDev));
    }

    /** Computes the 2nd order loss function for the
     * distribution function for given value of x, G2(x) = (1/2)E[max(X-x,0)*max(X-x-1,0)]
     * @param x The value to be evaluated
     * @return The loss function value, (1/2)E[max(X-x,0)*max(X-x-1,0)]
     */
    @Override
    public double secondOrderLossFunction(double x) {
        return (myVar * stdNormalSecondOrderLossFunction((x - myMean) / myStdDev));
    }

    /** Sets the parameters for the distribution
     * mean = parameters[0] and variance = parameters[1]
     * @param parameters an array of doubles representing the parameters for
     * the distribution
     */
    @Override
    public void setParameters(double[] parameters) {
        setMean(parameters[0]);
        setVariance(parameters[1]);
    }

    /** Gets the parameters for the distribution
     *
     * @return Returns an array of the parameters for the distribution
     */
    @Override
    public double[] getParameters() {
        double[] param = new double[2];
        param[0] = myMean;
        param[1] = myVar;
        return (param);
    }
}
