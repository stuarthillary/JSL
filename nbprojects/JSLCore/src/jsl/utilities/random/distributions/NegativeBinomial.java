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

import jsl.utilities.math.JSLMath;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/** The number of failures (=0) before the rth success (=1) in a sequence of independent Bernoulli trials
 *  with probability p of success on each trial.  The range of this random variable is {0, 1, 2, ....}
 *  
 * 
 */
public class NegativeBinomial extends Distribution implements DiscreteDistributionIfc,
        LossFunctionDistributionIfc {

    /** the probability of success, p
     */
    private double myProbSuccess;

    /** the probability of failure, 1-p
     */
    private double myProbFailure;

    /** the desired number of successes to wait for
     */
    private double myDesiredNumSuccesses;

    /** indicates whether or not pmf and cdf calculations are
     *  done by recursive (iterative) algorithm based on logarithms
     *  or via beta incomplete function and binomial coefficients.
     *
     */
    private boolean myRecursiveAlgoFlag = true;

    /**
     * Constructs a NegativeBinomial with n=1, p=0.5
     */
    public NegativeBinomial() {
        this(0.5, 1, RNStreamFactory.getDefault().getStream());
    }

    /**
     * Constructs a NegativeBinomial using the supplied parameters
     *
     * @param parameter parameter[0] should be the probability (p) and parameter[1]
     * should be the desired number of successes
     */
    public NegativeBinomial(double[] parameter) {
        this(parameter[0], (int) parameter[1], RNStreamFactory.getDefault().getStream());
    }

    /**
     * Constructs a NegativeBinomial using the supplied parameters
     *
     * @param parameter parameter[0] should be the probability (p) and parameter[1]
     * should be the desired number of successes
     * @param rng
     */
    public NegativeBinomial(double[] parameter, RngIfc rng) {
        this(parameter[0], (int) parameter[1], rng);
    }

    /**
     * Constructs a NegativeBinomial with p probability of success based on n
     * success
     *
     * @param prob The success probability
     * @param numSuccess The desired number of successes
     */
    public NegativeBinomial(double prob, double numSuccess) {
        this(prob, numSuccess, RNStreamFactory.getDefault().getStream());
    }

    /**
     * Constructs a NegativeBinomial with p probability of success based on n
     * success
     *
     * @param prob The success probability
     * @param numSuccess The desired number of successes
     * @param rng A RngIfc
     */
    public NegativeBinomial(double prob, double numSuccess, RngIfc rng) {
        super(rng);
        setParameters(prob, numSuccess);
    }

    /** indicates whether or not pmf and cdf calculations are
     *  done by recursive (iterative) algorithm based on logarithms
     *  or via beta incomplete function and binomial coefficients.
     *
     * @return
     */
    public final boolean getRecursiveAlgorithmFlag() {
        return myRecursiveAlgoFlag;
    }

    /** indicates whether or not pmf and cdf calculations are
     *  done by recursive (iterative) algorithm based on logarithms
     *  or via beta incomplete function and binomial coefficients.
     *
     * @param flag true means recursive algorithm is used
     */
    public final void setRecursiveAlgorithmFlag(boolean flag) {
        myRecursiveAlgoFlag = flag;
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    public final NegativeBinomial newInstance() {
        return (new NegativeBinomial(getParameters()));
    }

       /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    public final NegativeBinomial newInstance(RngIfc rng) {
        return (new NegativeBinomial(getParameters(), rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    public final NegativeBinomial newAntitheticInstance() {
        RngIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    /**
     * Sets the number of success and success probability
     *
     * @param prob The success probability
     * @param numSuccess The desired number of successes
     */
    public final void setParameters(double prob, double numSuccess) {
        setProbabilityOfSuccess(prob);
        setDesiredNumberOfSuccesses(numSuccess);
    }

    /** Gets the mode of the distribution
     *
     * @return
     */
    public final int getMode() {
        if (myDesiredNumSuccesses > 1.0) {
            return (int) Math.floor((myDesiredNumSuccesses - 1.0) * myProbFailure / myProbSuccess);
        } else {
            return 0;
        }
    }

    /** Sets the parameters as an array parameters[0] is probability of success
     *  parameters[1] is number of desired successes
     *
     */
    public final void setParameters(double[] parameters) {
        setParameters(parameters[0], parameters[1]);
    }

    /**
     * Gets the parameters as an array parameters[0] is probability of success
     * parameters[1] is number of desired successes
     *
     */
    public final double[] getParameters() {
        double[] param = new double[2];
        param[0] = myProbSuccess;
        param[1] = myDesiredNumSuccesses;
        return (param);
    }

    /**
     * Gets the success probability
     *
     * @return The success probability
     */
    public final double getProbabilityOfSuccess() {
        return (myProbSuccess);
    }

    /**
     * Gets the desired number of successes
     *
     * @return the number of success
     */
    public final double getDesiredNumberOfSuccesses() {
        return (myDesiredNumSuccesses);
    }

    /**
     * Sets the number of success
     *
     * @param mumSuccess The desired number of successes
     */
    private void setDesiredNumberOfSuccesses(double numSuccess) {
        if (numSuccess <= 0) {
            throw new IllegalArgumentException("The desired number of successes must be > 0");
        }

        myDesiredNumSuccesses = numSuccess;
    }

    /**
     * Sets the probability throws IllegalArgumentException when probability is
     * outside the range (0,1)
     *
     * @param prob
     *            the probability of success
     */
    private void setProbabilityOfSuccess(double prob) {
        if ((prob <= 0.0) || (prob >= 1.0)) {
            throw new IllegalArgumentException("Probability must be in (0,1)");
        }
        myProbSuccess = prob;
        myProbFailure = 1.0 - myProbSuccess;
    }

    /**
     * Gets the expected value
     *
     * @return the expected value
     */
    public final double getMean() {
        return (myDesiredNumSuccesses * myProbFailure) / myProbSuccess;
    }

    /**
     * Gets the variance
     *
     * @return the variance
     */
    public final double getVariance() {
        return (myDesiredNumSuccesses * myProbFailure) / (myProbSuccess * myProbSuccess);
    }

    /** If x is not and integer value, then the probability must be zero
     *  otherwise pmf(int x) is used to determine the probability
     *
     * @param x
     * @return
     */
    public final double pmf(double x) {
        if (Math.floor(x) == x) {
            return pmf((int) x);
        } else {
            return 0.0;
        }
    }

    /**
     * Returns the prob of getting x failures before the rth success where r is
     * the desired number of successes parameter
     *
     * @param j
     * @return the probability
     */
    public final double pmf(int j) {
        return negBinomialPMF(j, myDesiredNumSuccesses, myProbSuccess, myRecursiveAlgoFlag);
    }

    /**
     * Computes the cumulative probability distribution function for given value
     * of failures
     *
     * @param x
     *            The value to be evaluated
     * @return The probability, P{X &lt;=x}
     */
    public final double cdf(double x) {
        return cdf((int) x);
    }

    /**
     * Computes the cumulative probability distribution function for given value
     * of failures
     *
     * @param j The value to be evaluated
     * @return The probability, P{X &lt;=j}
     */
    public final double cdf(int j) {
        return negBinomialCDF(j, myDesiredNumSuccesses, myProbSuccess, myRecursiveAlgoFlag);
    }

    /**
     * Computes the inverse of the cumulative probability distribution function
     * for the supplied probability throws IllegalArgumentException when
     * probability is outside the range [0,1]
     *
     * p = 0.0 returns 0.0
     * p = 1.0 returns Double.POSITIVE_INFINITY
     *
     * @param prob The probability to be evaluated for the inverse
     * @return The value associated with the inverse CDF at the probability
     */
    public final double invCDF(double prob) {
        if ((prob < 0.0) || (prob > 1.0)) {
            throw new IllegalArgumentException("Supplied probability was " + prob + " Probability must be [0,1]");
        }

        if (prob <= 0.0) {
            return 0.0;
        }

        if (prob >= 1.0) {
            return Double.POSITIVE_INFINITY;
        }

        // check for geometric case
        if (JSLMath.equal(myDesiredNumSuccesses, 1.0)) {
            double x = (Math.ceil((Math.log(1.0 - prob) / (Math.log(1.0 - myProbSuccess))) - 1.0));
            return (0.0 + x);
        }

        return negBinomialInvCDF(prob, myDesiredNumSuccesses, myProbSuccess, myRecursiveAlgoFlag);

    }

    /** Computes the binomial coefficient.  Computes the number of combinations of size k
     * that can be formed from n distinct objects.
     * @param n The total number of distinct items
     * @param k The number of subsets
     * @return
     */
    private static double binomialCoefficient(double n, double k) {
        return Math.exp(logFactorial(n) - logFactorial(k) - logFactorial(n - k));
    }

    /** Computes the natural logarithm of the factorial operator.
     * ln(n!)
     * @param n The value to be operated on.
     * @return
     */
    private static double logFactorial(double n) {
        return (Gamma.logGammaFunction(n + 1.0));
    }

    /** Computes the probability mass function at j using a
     *  recursive (iterative) algorithm using logarithms
     *
     * @param j
     * @param r
     * @param p
     * @return
     */
    public static double recursivePMF(int j, double r, double p) {
        if (r <= 0) {
            throw new IllegalArgumentException("The number of successes must be > 0");
        }

        if ((p <= 0.0) || (p >= 1.0)) {
            throw new IllegalArgumentException("Success Probability must be in (0,1)");
        }

        if (j < 0) {
            return 0.0;
        }

        double y = r * Math.log(p);
        double lnq = Math.log(1.0 - p);

        for (int i = 1; i <= j; i++) {
            y = Math.log(r - 1.0 + i) - +Math.log(i) + lnq + y;
        }

        if (y >= JSLMath.getLargestExponentialArgument()) {
            throw new IllegalArgumentException("Term overflow due to input parameters");
        }

        if (y <= JSLMath.getSmallestExponentialArgument()) {
            return 0.0;
        }

        return Math.exp(y);
    }

    /** Computes the cdf at j using a recursive (iterative) algorithm using logarithms
     *
     * @param j
     * @param r
     * @param p
     * @return
     */
    public static double recursiveCDF(int j, double r, double p) {
        if (r <= 0) {
            throw new IllegalArgumentException("The number of successes must be > 0");
        }

        if ((p <= 0.0) || (p >= 1.0)) {
            throw new IllegalArgumentException("Success Probability must be in (0,1)");
        }

        if (j < 0) {
            return (0.0);
        }

        double y = r * Math.log(p);
        if (j == 0) {
            if (y >= JSLMath.getLargestExponentialArgument()) {
                throw new IllegalArgumentException("Term overflow due to input parameters");
            }

            if (y <= JSLMath.getSmallestExponentialArgument()) {
                return 0.0;
            }
        }
        double lnq = Math.log(1.0 - p);
        double sum = Math.exp(y);

        for (int i = 1; i <= j; i++) {
            y = Math.log(r - 1.0 + i) - +Math.log(i) + lnq + y;
            if (y >= JSLMath.getLargestExponentialArgument()) {
                throw new IllegalArgumentException("Term overflow due to input parameters");
            }

            if (y <= JSLMath.getSmallestExponentialArgument()) {
                continue;
            } else {
                sum = sum + Math.exp(y);
            }
        }

        return sum;
    }

    /** Allows static computation of prob mass function
     *  assumes that distribution's range is {0,1, ...}
     *  Uses the recursive logarithmic algorithm
     * 
     * @param j value for which prob is needed
     * @param r num of successes
     * @param p prob of success
     * @return
     */
    public static double negBinomialPMF(int j, double r, double p) {
        return negBinomialPMF(j, r, p, true);
    }

    /** Allows static computation of prob mass function
     *  assumes that distribution's range is {0,1, ...}
     *
     * @param j value for which prob is needed
     * @param r num of successes
     * @param p prob of success
     * @param recursive true indicates that the recursive logarithmic algorithm should be used
     * @return
     */
    public static double negBinomialPMF(int j, double r, double p, boolean recursive) {
        if (r <= 0) {
            throw new IllegalArgumentException("The number of successes must be > 0");
        }

        if ((p <= 0.0) || (p >= 1.0)) {
            throw new IllegalArgumentException("Success Probability must be in (0,1)");
        }

        if (j < 0) {
            return (0.0);
        }

        if (recursive) {
            return recursivePMF(j, r, p);
        }

        double k = r - 1.0;
        double bc = binomialCoefficient(j + k, k);
        double lny = r * Math.log(p) + j * Math.log(1.0 - p);
        double y = Math.exp(lny);
        return bc * y;

    }

    /** Allows static computation of the CDF
     *  assumes that distribution's range is {0,1, ...}
     *  Uses the recursive logarithmic algorithm
     * 
     * @param j value for which cdf is needed
     * @param r num of successes
     * @param p prob of success
     * @return
     */
    public static double negBinomialCDF(int j, double r, double p) {
        return negBinomialCDF(j, r, p, true);
    }

    /** Allows static computation of the CDF
     *  assumes that distribution's range is {0,1, ...}
     *
     * @param j value for which cdf is needed
     * @param r num of successes
     * @param p prob of success
     * @param recursive true indicates that the recursive logarithmic algorithm should be used
     * @return
     */
    public static double negBinomialCDF(int j, double r, double p, boolean recursive) {
        if (r <= 0) {
            throw new IllegalArgumentException("The number of successes must be > 0");
        }

        if ((p <= 0.0) || (p >= 1.0)) {
            throw new IllegalArgumentException("Success Probability must be in (0,1)");
        }

        if (j < 0) {
            return 0.0;
        }

        if (recursive) {
            return recursiveCDF(j, r, p);
        }

        return Beta.regularizedIncompleteBetaFunction(p, r, j + 1);
    }

    /** Allows static computation of complementary cdf function
     *  assumes that distribution's range is {0,1, ...}
     *  Uses the recursive logarithmic algorithm
     *
     * @param j value for which ccdf is needed
     * @param r num of successes
     * @param p prob of success
     * @return
     */
    public static double negBinomialCCDF(int j, double r, double p) {
        return negBinomialCCDF(j, r, p, true);
    }

    /** Allows static computation of complementary cdf function
     *  assumes that distribution's range is {0,1, ...}
     *
     * @param j value for which ccdf is needed
     * @param r num of successes
     * @param p prob of success
     * @param recursive true indicates that the recursive logarithmic algorithm should be used
     * @return
     */
    public static double negBinomialCCDF(int j, double r, double p, boolean recursive) {
        if (r <= 0) {
            throw new IllegalArgumentException("The number of successes must be > 0");
        }

        if ((p <= 0.0) || (p >= 1.0)) {
            throw new IllegalArgumentException("Success Probability must be in (0,1)");
        }

        if (j < 0) {
            return (1.0);
        }

        return (1.0 - negBinomialCDF(j, r, p, recursive));

    }

    /** Allows static computation of 1st order loss function
     *  assumes that distribution's range is {0,1, ...}
     *  Uses the recursive logarithmic algorithm
     *
     * @param j value for which 1st order loss function is needed
     * @param r num of successes
     * @param p prob of success
     * @return
     */
    public static double negBinomialLF1(int j, double r, double p) {
        return negBinomialLF1(j, r, p, true);
    }

    /** Allows static computation of 1st order loss function
     *  assumes that distribution's range is {0,1, ...}
     *
     * @param j value for which 1st order loss function is needed
     * @param r num of successes
     * @param p prob of success
     * @param recursive true indicates that the recursive logarithmic algorithm should be used
     * @return
     */
    public static double negBinomialLF1(int j, double r, double p, boolean recursive) {
        if (r <= 0) {
            throw new IllegalArgumentException("The number of successes must be > 0");
        }

        if ((p <= 0.0) || (p >= 1.0)) {
            throw new IllegalArgumentException("Success Probability must be in (0,1)");
        }

        double mu = r * (1.0 - p) / p;// the mean

        if (j < 0) {
            return (Math.floor(Math.abs(j)) + mu);
        } else if (j > 0) {
            double b, g0, g, g1;
            b = (1.0 - p) / p;
            g = negBinomialPMF(j, r, p, recursive);
            g0 = negBinomialCCDF(j, r, p, recursive);
            g1 = -1.0 * (j - r * b) * g0 + (j + r) * b * g;
            return g1;
        } else { // j == 0
            return mu;
        }
    }

    /** Allows static computation of 2nd order loss function
     *  assumes that distribution's range is {0,1, ...}
     *  Uses the recursive logarithmic algorithm
     * 
     * @param j value for which 2nd order loss function is needed
     * @param r num of successes
     * @param p prob of success
     * @return
     */
    public static double negBinomialLF2(int j, double r, double p) {
        return negBinomialLF2(j, r, p, true);
    }

    /** Allows static computation of 2nd order loss function
     *  assumes that distribution's range is {0,1, ...}
     * 
     * @param j value for which 2nd order loss function is needed
     * @param r num of successes
     * @param p prob of success
     * @param recursive true indicates that the recursive logarithmic algorithm should be used
     * @return
     */
    public static double negBinomialLF2(int j, double r, double p, boolean recursive) {
        if (r <= 0) {
            throw new IllegalArgumentException("The number of successes must be > 0");
        }

        if ((p <= 0.0) || (p >= 1.0)) {
            throw new IllegalArgumentException("Success Probability must be in (0,1)");
        }

        double mu = r * (1.0 - p) / p;
        double var = mu / p;
        double sbm = 0.5 * (var + mu * mu - mu);// 1/2 the 2nd binomial moment

        if (j < 0) {
            double s = 0.0;
            for (int y = 0; y > j; y--) {
                s = s + negBinomialLF1(y, r, p, recursive);
            }
            return (s + sbm);
        } else if (j > 0) {
            double b, g0, g, g2;
            b = (1.0 - p) / p;
            if (j < 0) {
                g0 = 1.0;
                g = 0.0;
            } else {
                g = negBinomialPMF(j, r, p, recursive);
                g0 = negBinomialCCDF(j, r, p, recursive);
            }
            g2 = (r * (r + 1) * b * b - 2.0 * r * b * j + j * (j + 1)) * g0;
            g2 = g2 + ((r + 1) * b - j) * (j + r) * b * g;
            g2 = 0.5 * g2;
            return g2;
        } else {// j== 0
            return sbm;
        }
    }

    /** Returns the quantile associated with the supplied probablity, x
     *  assumes that distribution's range is {0,1, ...}
     *  Uses the recursive logarithmic algorithm
     *
     * @param x The probability that the quantile is needed for
     * @param r The number of successes parameter
     * @param p The probability of success, must be in range [0,1)
     * @return
     */
    public static int negBinomialInvCDF(double x, double r, double p) {
        return negBinomialInvCDF(x, r, p, true);
    }

    /** Returns the quantile associated with the supplied probablity, x
     *  assumes that distribution's range is {0,1, ...}
     * 
     * @param x The probability that the quantile is needed for
     * @param r The number of successes parameter
     * @param p The probability of success, must be in range [0,1)
     * @param recursive true indicates that the recursive logarithmic algorithm should be used
     * @return
     */
    public static int negBinomialInvCDF(double x, double r, double p, boolean recursive) {
        if (r <= 0) {
            throw new IllegalArgumentException("The number of successes must be > 0");
        }

        if ((p <= 0.0) || (p >= 1.0)) {
            throw new IllegalArgumentException("Success Probability must be in (0,1)");
        }

        if ((x < 0.0) || (x > 1.0)) {
            throw new IllegalArgumentException("Supplied probability was " + x + " Probability must be [0,1]");
        }

        if (x <= 0.0) {
            return 0;
        }

        if (x >= 1.0) {
            return Integer.MAX_VALUE;
        }

        // check for geometric case
        if (JSLMath.equal(r, 1.0)) {
            return ((int) Math.ceil((Math.log(1.0 - x) / (Math.log(1.0 - p))) - 1.0));
        }

        // get approximate quantile from normal approximation
        // and Cornish-Fisher expansion
        int start = invCDFViaNormalApprox(x, r, p);
        double cdfAtStart = negBinomialCDF(start, r, p, recursive);

        //System.out.println("start = " + start);
        //System.out.println("cdfAtStart = " + cdfAtStart);
        //System.out.println("p = " + p);
        //System.out.println();

        if (x >= cdfAtStart) {
            return searchUpCDF(x, r, p, start, cdfAtStart, recursive);
        } else {
            return searchDownCDF(x, r, p, start, cdfAtStart, recursive);
        }

    }

    /**
     *
     * @param x
     * @param r
     * @param p
     * @param start
     * @param cdfAtStart
     * @param recursive
     * @return
     */
    protected static int searchUpCDF(double x, double r, double p,
            int start, double cdfAtStart, boolean recursive) {
        int i = start;
        double cdf = cdfAtStart;
        while (x > cdf) {
            i++;
            cdf = cdf + negBinomialPMF(i, r, p, recursive);
        }
        return i;
    }

    /**
     *
     * @param x
     * @param r
     * @param p
     * @param start
     * @param cdfAtStart
     * @param recursive
     * @return
     */
    protected static int searchDownCDF(double x, double r, double p,
            int start, double cdfAtStart, boolean recursive) {
        int i = start;
        double cdfi = cdfAtStart;
        while (i > 0) {
            double cdfim1 = cdfi - negBinomialPMF(i, r, p, recursive);
            if ((cdfim1 <= x) && (x < cdfi)) {
                if (JSLMath.equal(cdfim1, x))// must handle invCDF(cdf(x) = x)
                {
                    return i - 1;
                } else {
                    return i;
                }
            }
            cdfi = cdfim1;
            i--;
        }
        return i;
    }

    /**
     * 
     * @param x
     * @param r
     * @param p
     * @return
     */
    protected static int invCDFViaNormalApprox(double x, double r, double p) {
        if (r <= 0) {
            throw new IllegalArgumentException("The number of successes must be > 0");
        }

        if ((p <= 0.0) || (p >= 1.0)) {
            throw new IllegalArgumentException("Success Probability must be in (0,1)");
        }

        if ((x < 0.0) || (x > 1.0)) {
            throw new IllegalArgumentException("Supplied probability was " + x + " Probability must be [0,1]");
        }

        if (x <= 0.0) {
            return 0;
        }

        if (x >= 1.0) {
            return Integer.MAX_VALUE;
        }

        double dQ = 1.0 / p;
        double dP = (1.0 - p) * dQ;
        double mu = r * dP;
        double sigma = Math.sqrt(r * dP * dQ);
        double g = (dQ + dP) / sigma;

        /* y := approx.value (Cornish-Fisher expansion) :  */
        double z = Normal.stdNormalInvCDF(x);
        double y = Math.floor(mu + sigma * (z + g * (z * z - 1.0) / 6.0) + 0.5);
        if (y < 0) {
            return 0;
        } else {
            return (int) y;
        }
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.LossFunctionDistributionIfc#firstOrderLossFunction(double)
     */
    public double firstOrderLossFunction(double x) {
        double mu = getMean();
        if (x < 0.0) {
            return (Math.floor(Math.abs(x)) + mu);
        } else if (x > 0.0) {
            double b, g0, g, g1, p;
            double r = myDesiredNumSuccesses;
            p = myProbSuccess;
            b = (1.0 - p) / p;
            g0 = complementaryCDF(x);
            g = pmf(x);
            g1 = -1.0 * (x - r * b) * g0 + (x + r) * b * g;
            return g1;
        } else // x== 0.0
        {
            return mu;
        }
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.LossFunctionDistributionIfc#secondOrderLossFunction(double)
     */
    public double secondOrderLossFunction(double x) {
        double mu = getMean();
        double sbm = 0.5 * (getVariance() + mu * mu - mu);// 1/2 the 2nd binomial moment
        if (x < 0.0) {
            double s = 0.0;
            for (int y = 0; y > x; y--) {
                s = s + firstOrderLossFunction(y);
            }
            return (s + sbm);
        } else if (x > 0.0) {
            double b, g0, g, g2, p;
            double r = myDesiredNumSuccesses;
            p = myProbSuccess;
            b = (1.0 - p) / p;
            g0 = complementaryCDF(x);
            g = pmf(x);

            g2 = (r * (r + 1) * b * b - 2.0 * r * b * x + x * (x + 1)) * g0;
            g2 = g2 + ((r + 1) * b - x) * (x + r) * b * g;
            g2 = 0.5 * g2;
            return g2;
        } else // x == 0.0
        {
            return sbm;
        }
    }
}
