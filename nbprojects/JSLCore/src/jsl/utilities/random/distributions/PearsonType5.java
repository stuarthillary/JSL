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

/** Represents a Pearson Type V distribution, 
 *  see Law (2007) Simulation Modeling and Analysis, McGraw-Hill, pg 293
 * 
 *  Code contributed by Seda Gumrukcu
 *
 */
public class PearsonType5 extends Distribution implements ContinuousDistributionIfc, InverseCDFIfc {

    private double myShape;

    private double myScale;

    private Gamma myGammaCDF;

    private double myGAlpha;

    /** Creates a PearsonType5 distribution
     *
     * shape = 1.0
     * scale = 1.0
     */
    public PearsonType5() {
        this(1.0, 1.0, RNStreamFactory.getDefault().getStream());
    }

    /** Creates a PearsonType5 distribution
     * parameters[0] = shape
     * parameters[1] = scale
     *
     * @param parameters
     */
    public PearsonType5(double[] parameters) {
        this(parameters[0], parameters[1], RNStreamFactory.getDefault().getStream());
    }

    /** Creates a PearsonType5 distribution
     * parameters[0] = shape
     * parameters[1] = scale
     *
     * @param parameters
     * @param rng
     */
    public PearsonType5(double[] parameters, RngIfc rng) {
        this(parameters[0], parameters[1], rng);
    }

    /** Creates a PearsonType5 distribution
     *
     * @param shape must be &gt;0
     * @param scale must be &gt; 0
     */
    public PearsonType5(double shape, double scale) {
        this(shape, scale, RNStreamFactory.getDefault().getStream());
    }

    /** Creates a PearsonType5 distribution
     *
     * @param shape must be &gt;0
     * @param scale must be &gt; 0
     * @param rng
     */
    public PearsonType5(double shape, double scale, RngIfc rng) {
        super(rng);
        setParameters(shape, scale);
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    public final PearsonType5 newInstance() {
        return (new PearsonType5(getParameters()));
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    public final PearsonType5 newInstance(RngIfc rng) {
        return (new PearsonType5(getParameters(), rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    public final PearsonType5 newAntitheticInstance() {
        RngIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    /** Sets the shape and scale parameters
     *
     * @param shape must be &gt; 0
     * @param scale must be &gt; 0
     */
    public final void setParameters(double shape, double scale) {
        if (shape <= 0) {
            throw new IllegalArgumentException("Alpha (shape parameter) should be > 0");
        }

        if (scale <= 0) {
            throw new IllegalArgumentException("Beta (scale parameter) should > 0");
        }

        myShape = shape;
        myGAlpha = Gamma.gammaFunction(shape);
        myScale = scale;

        if (myGammaCDF == null) {
            myGammaCDF = new Gamma(myShape, 1.0 / myScale, myRNG);
        } else {
            myGammaCDF.setShape(shape);
            myGammaCDF.setScale(1.0 / scale);
        }
    }

    /** Gets the shape parameter
     *
     * @return
     */
    public final double getShape() {
        return myShape;
    }

    /** Gets the scale parameter
     *
     * @return
     */
    public final double getScale() {
        return myScale;
    }

    public double cdf(double x) {
        if (x > 0) {
            return 1 - myGammaCDF.cdf(1 / x);
        }

        return 0.0;
    }

    /** If shape &lt;= 1.0, returns Double.NaN, otherwise, returns the mean
     *
     */
    public double getMean() {
        if (myShape <= 1.0) {
            return Double.NaN;
        }

        return (myScale / (myShape - 1.0));
    }

    /** Gets the parameters
     * parameters[0] = shape
     * parameters[1] = scale
     *
     */
    public double[] getParameters() {
        double[] param = new double[2];
        param[0] = myShape;
        param[1] = myScale;
        return (param);
    }

    /** If shape &lt;= 2.0, returns Double.NaN, otherwise returns the variance
     *
     */
    public double getVariance() {
        if (myShape <= 2.0) {
            return Double.NaN;
        }

        return (myScale * myScale) / ((myShape - 2.0) * (myShape - 1.0) * (myShape - 1.0));
    }

    public double invCDF(double p) {
        if ((p < 0.0) || (p > 1.0)) {
            throw new IllegalArgumentException("Probability must be [0,1]");
        }
        return 1.0 / (myGammaCDF.invCDF(1.0 - p));
    }

    public double pdf(double x) {
        if (x > 0.0) {
            return ((Math.pow(x, -(myShape + 1.0))) * (Math.exp(-myScale / x))) / (Math.pow(myScale, -myShape) * myGAlpha);
        }

        return 0.0;
    }

    /** Sets the parameters
     * parameters[0] = shape
     * parameters[1] = scale
     *
     * @param parameters
     */
    public void setParameters(double[] parameters) {
        setParameters(parameters[0], parameters[1]);
    }
}
