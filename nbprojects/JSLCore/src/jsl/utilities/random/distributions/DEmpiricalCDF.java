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

import java.util.*;
import jsl.utilities.math.*;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;
import jsl.utilities.reporting.JSL;

/** Provides a representation for a discrete distribution with
 * arbitrary values and assigned probabilities to each value.
 * Allows the specification of the distribution via an array containing the 
 * following pairs
 * (v1, c1, v2, c2, ..., vn, 1.0)
 * where if p1 is the probability associated with v1, p2 with v2, etc
 * then c1 = p1, c2 = p1 + p2, c3 = p1 + p2 + p3, etc, 
 * with cn = 1.0 (the sum of all the probabilities). If cn is not 1.0, then 
 * an exception is thrown.
 * If the size of the array is not even, then an exception is thrown.
 */
public class DEmpiricalCDF extends Distribution implements DiscreteDistributionIfc {

    /** Holds the list of probability points
     */
    private LinkedList<ProbPoint> myProbabilityPoints;

    /** This constructor takes in an Array of probability points
     *  (value, cumulative probability), Eg. X[] = {v1, cp1, v2, cp2, ...},
     *  as the input parameter.
     *
     *  An IllegalStateException will be thrown if the user attempts
     *  to use the methods and the total probability does not sum up to 1.0
     *
     * @param pairs An array holding the value cumulative probability
     */
    public DEmpiricalCDF(double[] pairs) {
        this(pairs, RNStreamFactory.getDefault().getStream());
    }

    /** This constructor takes in an Array of probability points
     *  (value, cumulative probability), Eg. X[] = {v1, cp1, v2, cp2, ...},
     *  as the input parameter.
     *
     *  An IllegalStateException will be thrown if the user attempts
     *  to use the methods and the total probability does not sum up to 1.0
     *
     * @param pairs An array holding the value, cumulative probability pairs.
     * @param  rng
     */
    public DEmpiricalCDF(double[] pairs, RngIfc rng) {
        super(rng);
        myProbabilityPoints = new LinkedList<ProbPoint>();
        setParameters(pairs);
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    @Override
    public final DEmpiricalCDF newInstance() {
        return (new DEmpiricalCDF(getParameters()));
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    @Override
    public final DEmpiricalCDF newInstance(RngIfc rng) {
        return (new DEmpiricalCDF(getParameters(), rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    @Override
    public final DEmpiricalCDF newAntitheticInstance() {
        RngIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    @Override
    public final double cdf(double x) {
        ProbPoint lowpt = (ProbPoint) myProbabilityPoints.getFirst();

        if (x < lowpt.value) {
            return (0.0);
        }

        ProbPoint uppt = (ProbPoint) myProbabilityPoints.getLast();

        if (x >= uppt.value) {
            return (1.0);
        }
        
        ListIterator<ProbPoint> iter = myProbabilityPoints.listIterator();
        while (iter.hasNext()) {
            lowpt = (ProbPoint) iter.next();
            uppt = (ProbPoint) iter.next();
            double lv = lowpt.value;
            double uv = uppt.value;

            if ((lv <= x) && (x < uv)) {
                break;
            }
        }

        if (lowpt == null) {
            return (Double.NaN);
        } else {
            return (lowpt.cumProb);
        }
    }

    @Override
    public final double getMean() {
        double m = 0.0;
        for (ProbPoint p : myProbabilityPoints) {
            m = m + p.probability * p.value;
        }
        return (m);
    }

    @Override
    public final double getVariance() {
        double m1 = 0.0;
        double m2 = 0.0;
        for (ProbPoint pp : myProbabilityPoints) {
            double v = pp.value;
            double p = pp.probability;
            m1 = m1 + p * v;
            m2 = m2 + p * v * v;
        }
        return (m2 - m1 * m1);
    }

    /** The probability mass function for this discrete distribution.
     * Returns the same as pdf.
     * @param x The point to get the probability for
     * @return The probability associated with x
     */
    @Override
    public final double pmf(double x) {
        ProbPoint p = null;
        boolean ifExist = false;

        ListIterator<ProbPoint> iter = myProbabilityPoints.listIterator();
        while (iter.hasNext()) {
            p = (ProbPoint) iter.next();
            if (x == p.value) {
                ifExist = true;
                break;
            }
        }

        if (ifExist == false) {
            return (Double.NaN);
        } else {
            return (p.probability);
        }
    }

    /** Returns the pmf as a string.
     * @return A String of probability, value pairs.
     */
    @Override
    public String toString() {
        return (myProbabilityPoints.toString());
    }

    /** Provides the inverse cumulative distribution function for the distribution
     * @param p The probability to be evaluated for the inverse, p must be [0,1] or
     * an IllegalArgumentException is thrown
     * @return The inverse cdf evaluated at p
     */
    @Override
    public double invCDF(double p) {
        if ((p < 0.0) || (p > 1.0)) {
            throw new IllegalArgumentException("Probability must be [0,1]");
        }

        double x = 0.0;
        ListIterator<ProbPoint> iter = myProbabilityPoints.listIterator();

        while (iter.hasNext()) {
            ProbPoint pp = (ProbPoint) iter.next();
            double cp = pp.cumProb;
            if (p <= cp) {
                x = pp.value;
                break;
            }
        }
        return (x);
    }

    /** Sets the parameters for the distribution. Array of probability points
     *  (value, cumulative probability), Eg. X[] = [v1, cp1, v2, cp2, 7,0.5],
     *  as the input parameters.
     *
     * @param parameters an array of doubles representing the parameters for
     * the distribution
     */
    @Override
    public final void setParameters(double[] parameters) {
        if (parameters.length % 2 != 0) {
            throw new IllegalArgumentException("Input probability array does not have an even number of elements");
        }

        if (parameters[parameters.length - 1] != 1.0){
            throw new IllegalArgumentException("CDF must sum to 1.0, last prob was not 1.0");
           
        }

        double cp = 0.0;// last cp
        for (int i = 0; i < parameters.length; i = i + 2) {
            ProbPoint pp = new ProbPoint(parameters[i], parameters[i + 1]);
            pp.probability = pp.cumProb - cp;
            cp = pp.cumProb;
            myProbabilityPoints.add(pp);
        }
    }

    /** Gets the parameters for the distribution
     *
     * @return Returns an array of the parameters for the distribution
     */
    @Override
    public double[] getParameters() {
        int n = 2 * myProbabilityPoints.size();
        double[] param = new double[n];

        int i = 0;
        ProbPoint p;
        ListIterator<ProbPoint> iter = myProbabilityPoints.listIterator();
        while (iter.hasNext()) {
            p = (ProbPoint) iter.next();
            param[i] = p.value;
            param[i+1] = p.cumProb;
            i = i + 2;
        }

        return (param);
    }

    /** Gets the PMF parameters for a DEmpiricalPMF distribution
     *
     * @return Returns an array of the parameters for the distribution
     */
    public double[] getPMFParameters() {
        int n = 2 * myProbabilityPoints.size();
        double[] param = new double[n];

        int i = 0;
        ProbPoint p;
        ListIterator<ProbPoint> iter = myProbabilityPoints.listIterator();
        while (iter.hasNext()) {
            p = (ProbPoint) iter.next();
            param[i] = p.value;
            param[i+1] = p.probability;
            i = i + 2;
        }

        return (param);
    }
    
    /** Creates a DEmpiricalPMF based on the DEmpiricalCDF 
     * 
     * @return 
     */
    public DEmpiricalPMF createDEmpiricalPMF(){
        return new DEmpiricalPMF(getPMFParameters());
    }
    
    private final class ProbPoint {
        private double value;
        private double probability;
        private double cumProb;       
        private ProbPoint(double v, double cp) {
            if ((cp < 0.0) || (cp > 1.0)) {
                throw new IllegalArgumentException("Probability must be in interval [0,1]");
            }
            value = v;
            cumProb = cp;
        }
        @Override
        public String toString() {
            String s = "P(x=" + value + ")= " + probability + "\t";
            s = s + "P(x<=" + value + ")= " + cumProb + "\n";
            return (s);
        }
    }

    public static void main(String args[]) {
        double[] pm = {1.0, 1.0/6.0, 2.0, 3.0/6.0, 3.0, 5.0/6.0, 4.0, 1.0};
        
        DEmpiricalCDF n2 = new DEmpiricalCDF(pm);

        System.out.println("mean = " + n2.getMean());
        System.out.println("var = " + n2.getVariance());
        System.out.println("pmf");
        System.out.println(n2);

        for (int i = 1; i <= 10; i++) {
            System.out.println("x(" + i + ")= " + n2.getValue());
        }

	double[] pp = {1.0, 0.7, 2.0, 0.8, 4.0, 0.9, 5.0, 1.0};
	DEmpiricalCDF d = new DEmpiricalCDF(pp);

        System.out.println("mean = " + d.getMean());
        System.out.println("var = " + d.getVariance());
        System.out.println("pmf");
        System.out.println(d);

        for (int i = 1; i <= 5; i++) {
            System.out.println("x(" + i + ")= " + d.getValue());
        }

        System.out.println();
        System.out.println("invCDF(0.2) = " + d.invCDF(0.2));
        System.out.println("invCDF(0.983) = " + d.invCDF(0.983));
        System.out.println("invCDF(" + d.cdf(1.0) + ") = " + d.invCDF(d.cdf(1.0)));

        System.out.println("done");
    }
}
