/*
 *  Copyright (C) 2010 rossetti
 * 
 *  Contact:
 * 	Manuel D. Rossetti, Ph.D., P.E.
 * 	Department of Industrial Engineering
 * 	University of Arkansas
 * 	4207 Bell Engineering Center
 * 	Fayetteville, AR 72701
 * 	Phone: (479) 575-6756
 * 	Email: rossetti@uark.edu
 * 	Web: www.uark.edu/~rossetti
 * 
 *  This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 *  of Java classes that permit the development and execution of discrete event
 *  simulation programs.
 * 
 *  The JSL is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  The JSL is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jsl.utilities.random.rng;

import java.util.Arrays;
import java.util.logging.Level;
import jsl.utilities.Identity;
import jsl.utilities.IdentityIfc;
import jsl.utilities.math.JSLMath;
import jsl.utilities.reporting.JSL;

/**
 * A RNStreamFactory has the ability to create objects that implement the RngIfc
 * interface. These objects represent a stream of random numbers. Random number
 * streams produce pseudo-random numbers U(0,1)
 *
 * This class is based on RngStream.java from Pierre L' Ecuyer but modified to
 * provide very flexible stream control. The streams of random numbers are
 * created from the RNStreamFactory class
 *
 * The generator is the combined multiple recursive generator (CMRG) Mrg32k3a
 * implemented in 64-bit floating-point arithmetic.
 *
 * The generator has period length 2^{191}
 *
 * The seed of the RNG, and the state of a stream at any given step, are
 * 6-dimensional vectors of 32-bit integers.
 *
 * The default initial seed of the package is 12345, 12345, 12345, 12345, 12345,
 * 12345
 *
 * Downloaded on 9-19-2007 from:
 * http://www.iro.umontreal.ca/~lecuyer/myftp/streams00/java/
 *
 * Copyright: Pierre L'Ecuyer, University of Montreal Notice: This code can be
 * used freely for personal, academic, or non-commercial purposes. For
 * commercial purposes, please contact P. L'Ecuyer at: lecuyer@iro.UMontreal.ca
 * Version 1.0 Date: 14 August 2001
 *
 *
 * @author rossetti
 */
public class RNStreamFactory extends Identity {

    private static RNStream DEFAULT_RNG;

    private static RNStreamFactory DefaultFactory = new RNStreamFactory("Default");

    private RNGStreamManager myStreamManager;

    /**
     * A counter to count the number of created streams
     */
    private static int myStreamCounter_ = 0;

    private final double a12 = 1403580.0;

    private final double a13n = 810728.0;

    private final double a21 = 527612.0;

    private final double a23n = 1370589.0;

    private final double machinePrecision = JSLMath.getMachinePrecision();

    private final double norm = 2.328306549295727688e-10;

    private final double invtwo24 = 5.9604644775390625e-8;

    private final double InvA1[][] = { // Inverse of A1p0
        {184888585.0, 0.0, 1945170933.0},
        {1.0, 0.0, 0.0},
        {0.0, 1.0, 0.0}
    };

    private final double InvA2[][] = { // Inverse of A2p0
        {0.0, 360363334.0, 4225571728.0},
        {1.0, 0.0, 0.0},
        {0.0, 1.0, 0.0}
    };

    private final double A1p0[][] = {
        {0.0, 1.0, 0.0},
        {0.0, 0.0, 1.0},
        {-810728.0, 1403580.0, 0.0}
    };

    private final double A2p0[][] = {
        {0.0, 1.0, 0.0},
        {0.0, 0.0, 1.0},
        {-1370589.0, 0.0, 527612.0}
    };

    private final double A1p76[][] = {
        {82758667.0, 1871391091.0, 4127413238.0},
        {3672831523.0, 69195019.0, 1871391091.0},
        {3672091415.0, 3528743235.0, 69195019.0}
    };

    private final double A2p76[][] = {
        {1511326704.0, 3759209742.0, 1610795712.0},
        {4292754251.0, 1511326704.0, 3889917532.0},
        {3859662829.0, 4292754251.0, 3708466080.0}
    };

    private final double A1p127[][] = {
        {2427906178.0, 3580155704.0, 949770784.0},
        {226153695.0, 1230515664.0, 3580155704.0},
        {1988835001.0, 986791581.0, 1230515664.0}
    };

    private final double A2p127[][] = {
        {1464411153.0, 277697599.0, 1610723613.0},
        {32183930.0, 1464411153.0, 1022607788.0},
        {2824425944.0, 32183930.0, 2093834863.0}
    };

    private final double m1 = 4294967087.0;

    private final double m2 = 4294944443.0;

    private final double two17 = 131072.0;

    private final double two53 = 9007199254740992.0;

    /**
     * Default seed of the package and seed for the next stream to be created.
     *
     */
    private double nextSeed[] = {12345, 12345, 12345, 12345, 12345, 12345};

    /**
     * Creates a factory with no name
     *
     */
    public RNStreamFactory() {
        this(null);
    }

    /**
     * Creates a factory with the provided name
     *
     * @param name
     */
    public RNStreamFactory(String name) {
        super(name);
    }

    /**
     * Returns a clone of the factory that will produce exactly the same streams
     *
     * @return
     */
    public RNStreamFactory newInstance() {
        return newInstance(getName() + " Clone");
    }

    /**
     * Returns a clone of the factory that will produce exactly the same streams
     *
     * @param name
     * @return
     */
    public RNStreamFactory newInstance(String name) {
        RNStreamFactory f = new RNStreamFactory(name);
        f.setFactorySeed(getFactorySeed());
        return f;
    }

    /**
     * Returns a reference to a "global" stream factory
     *
     * @return
     */
    public static final RNStreamFactory getDefault() {
        return DefaultFactory;
    }

    /**
     * Sets the default factory to the supplied factory
     *
     * @param f must not be null
     */
    public static final void setDefaultFactory(RNStreamFactory f) {
        if (f == null) {
            throw new IllegalArgumentException("The supplied RNStreamFactory was null");
        }
        DefaultFactory = f;
    }

    /**
     * Returns a global default stream
     *
     * @return
     */
    public final static RNStream getDefaultStream() {
        if (DEFAULT_RNG == null) {
            DEFAULT_RNG = getDefault().getStream();
        }
        return DEFAULT_RNG;
    }

    /**
     * Tells the factory to make and return a RNStream
     *
     * @return
     */
    public final RNStream getStream() {
        return getStream(null);
    }

    /**
     * Tells the factory to make and return a RNStream with the provided name
     *
     * @param name
     * @return
     */
    public final RNStream getStream(String name) {
        // create the stream using the current seed state of the factory
        RNStream stream = new RNStream(name);

        // advance the factory for next stream
        advanceSeeds(1);

        // save the stream if needed within a stream manager
        if (myStreamManager != null) {
            myStreamManager.add(stream);
        }

        return stream;
    }

    /**
     * Advances the seeds n times. Acts as if n streams were created, without
     * actually creating the streams
     *
     * @param n the number of times to advance
     */
    public final void advanceSeeds(int n) {
        for (int k = 1; k <= n; k++) {
            matVecModM(A1p127, nextSeed, nextSeed, m1);
            double temp[] = new double[3];
            for (int i = 0; i < 3; ++i) {
                temp[i] = nextSeed[i + 3];
            }
            matVecModM(A2p127, temp, temp, m2);
            for (int i = 0; i < 3; ++i) {
                nextSeed[i + 3] = temp[i];
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Random number Factory name: ");
        sb.append(getName());
        sb.append("\n");
        sb.append("Random number Factory ID: ");
        sb.append(getId());
        sb.append("\n");
        sb.append("The current package seed is now:\n");
        sb.append(Arrays.toString(getFactorySeed()));
        return sb.toString();
    }

    /* Compute (a*s + c) MOD m ; m must be < 2^35 */
 /* Works also for s, c < 0.                   */
    private double multModM(double a, double s, double c, double m) {
        double v;
        int a1;
        v = a * s + c;
        if (v >= two53 || v <= -two53) {
            a1 = (int) (a / two17);
            a -= a1 * two17;
            v = a1 * s;
            a1 = (int) (v / m);
            v -= a1 * m;
            v = v * two17 + a * s + c;
        }
        a1 = (int) (v / m);
        if ((v -= a1 * m) < 0.0) {
            return v += m;
        } else {
            return v;
        }
    }

    /* Returns v = A*s MOD m.  Assumes that -m < s[i] < m. */
 /* Works even if v = s.                                */
    private void matVecModM(double A[][], double s[], double v[], double m) {
        int i;
        double x[] = new double[3];
        for (i = 0; i < 3; ++i) {
            x[i] = multModM(A[i][0], s[0], 0.0, m);
            x[i] = multModM(A[i][1], s[1], x[i], m);
            x[i] = multModM(A[i][2], s[2], x[i], m);
        }
        for (i = 0; i < 3; ++i) {
            v[i] = x[i];
        }
    }

    /* Returns C = A*B MOD m */
 /* Note: work even if A = C or B = C or A = B = C.         */
    private void matMatModM(double A[][], double B[][], double C[][], double m) {
        int i, j;
        double V[] = new double[3], W[][] = new double[3][3];
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 3; ++j) {
                V[j] = B[j][i];
            }
            matVecModM(A, V, V, m);
            for (j = 0; j < 3; ++j) {
                W[j][i] = V[j];
            }
        }
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 3; ++j) {
                C[i][j] = W[i][j];
            }
        }
    }

    /* Compute matrix B = (A^(2^e) Mod m);  works even if A = B */
    private void matTwoPowModM(double A[][], double B[][], double m, int e) {
        int i, j;
        /* initialize: B = A */
        if (A != B) {
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; ++j) {
                    B[i][j] = A[i][j];
                }
            }
        }
        /* Compute B = A^{2^e} */
        for (i = 0; i < e; i++) {
            matMatModM(B, B, B, m);
        }
    }

    /* Compute matrix D = A^c Mod m ;  works even if A = B */
    private void matPowModM(double A[][], double B[][], double m, int c) {
        int i, j;
        int n = c;
        double W[][] = new double[3][3];

        /* initialize: W = A; B = I */
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; ++j) {
                W[i][j] = A[i][j];
                B[i][j] = 0.0;
            }
        }
        for (j = 0; j < 3; ++j) {
            B[j][j] = 1.0;
        }

        /* Compute B = A^c mod m using the binary decomp. of c */
        while (n > 0) {
            if ((n % 2) == 1) {
                matMatModM(W, B, B, m);
            }
            matMatModM(W, W, W, m);
            n /= 2;
        }
    }

    /**
     * Turns on stream management. Every stream created after this call will be
     * placed in a list so that they can be managed together
     *
     */
    public final void turnOnStreamManager() {
        if (myStreamManager == null) {
            myStreamManager = new RNGStreamManager();
        }
    }

    /**
     * Turns off stream management. Every stream previously managed will be
     * removed and cannot again be managed
     *
     */
    public final void turnOffStreamManager() {
        if (myStreamManager != null) {
            myStreamManager.clear();
        }
        myStreamManager = null;
    }

    /**
     * Causes all managed streams to return to the beginning of their starting
     * stream
     *
     */
    public final void resetAllStartStreams() {
        if (myStreamManager != null) {
            myStreamManager.resetStartStream();
        }
    }

    /**
     * Causes all managed stream to return to their start of their current
     * substream
     *
     */
    public final void resetAllStartSubstreams() {
        if (myStreamManager != null) {
            myStreamManager.resetStartSubstream();
        }
    }

    /**
     * Causes all managed streams to advance to their next substream
     *
     */
    public final void advanceAllNextSubstreams() {
        if (myStreamManager != null) {
            myStreamManager.advanceToNextSubstream();
        }
    }

    /**
     * Causes all the managed streams to use antithetic or not
     *
     * @param flag
     */
    public final void setAllAntithetic(boolean flag) {
        if (myStreamManager != null) {
            myStreamManager.setAntitheticOption(flag);
        }
    }

    /**
     * Returns the stream manager if stream management has been turned on, else
     * it will return null
     *
     * @return
     */
    public final RandomStreamManagerIfc getStreamManager() {
        return myStreamManager;
    }

    /**
     * Gets the default initial package seed: seed = {12345, 12345, 12345,
     * 12345, 12345, 12345};
     *
     *
     * @return
     */
    public final long[] getDefaultInitialFactorySeed() {
        long[] seed = {12345, 12345, 12345, 12345, 12345, 12345};
        return seed;
    }

    /**
     * Returns the current package seed
     *
     * @return
     */
    public final long[] getFactorySeed() {
        long[] seed = new long[6];
        for (int i = 0; i < 6; ++i) {
            seed[i] = (long) nextSeed[i];
        }
        return seed;
    }

    /**
     * Resets the package seed to the default initial package seed: seed =
     * {12345, 12345, 12345, 12345, 12345, 12345};
     *
     */
    public final void resetFactorySeed() {
        setFactorySeed(getDefaultInitialFactorySeed());
    }

    /**
     * Sets the initial seed to the six integers in the vector seed[0..5]. This
     * will be the seed (initial state) of the first stream. By default, this
     * seed is (12345, 12345, 12345, 12345, 12345, 12345).
     *
     * If it is	called,	the first 3 values of the seed must all be less than m1
     * = 4294967087, and not all 0; and the last 3 values must all be less than
     * m2 = 4294944443, and not all 0. Returns false for invalid seeds, and true
     * otherwise.
     *
     * @param seed the seeds
     * @return
     */
    public final boolean setFactorySeed(long seed[]) {
        // Must use long because there is no unsigned int type.
        if (CheckSeed(seed) != 0) {
            return false;                   // FAILURE
        }
        for (int i = 0; i < 6; ++i) {
            nextSeed[i] = seed[i];
        }
        return true;                     // SUCCESS
    }

    private int CheckSeed(long seed[]) {
        /* Check that the seeds are legitimate values. Returns 0 if legal seeds,
        -1 otherwise. */
        int i;
        for (i = 0; i < 3; ++i) {
            if (seed[i] >= m1 || seed[i] < 0) {
                JSL.LOGGER.warning("ERROR: Seed[" + i + "], Seed is not set.\n");
                return -1;
            }
        }
        for (i = 3; i < 6; ++i) {
            if (seed[i] >= m2 || seed[i] < 0) {
                JSL.LOGGER.warning("ERROR: Seed[" + i + "], Seed is not set.\n");
                return -1;
            }
        }
        if (seed[0] == 0 && seed[1] == 0 && seed[2] == 0) {
            JSL.LOGGER.warning("ERROR: First 3 seeds = 0.\n");
            return -1;
        }
        if (seed[3] == 0 && seed[4] == 0 && seed[5] == 0) {
            JSL.LOGGER.warning("ERROR: Last 3 seeds = 0.\n");
            return -1;
        }
        return 0;
    }

    /**
     * A concrete implementation of a random number stream (RngIfc)
     *
     */
    public class RNStream implements RngIfc, IdentityIfc,
            GetStreamCloneIfc, GetAntitheticStreamIfc {

        /**
         * Describes the stream (for writing the state, error messages, etc.).
         *
         */
        private String myName;

        /**
         * The id of this object
         */
        private int myId;

        /**
         * The current state of the stream
         *
         */
        private double Cg[] = new double[6];

        /**
         * The starting point of the current substream
         *
         */
        private double Bg[] = new double[6];

        /**
         * The starting point of the current stream
         *
         */
        private double Ig[] = new double[6];

        /**
         * This stream generates antithetic variates if and only if {\tt anti =
         * true}.
         */
        private boolean anti;

        /**
         * The precision of the output numbers is increased (see {\tt
         * increasedPrecis}) if and only if {\tt prec53 = true}.
         *
         */
        private boolean prec53;

        /**
         * The previous U generated (returned) by randU01()
         *
         */
        private double myPrevU;

        private RNStream() {
            this(null);
        }

        /**
         * Makes a stream with the given name
         *
         * @param name
         */
        private RNStream(String name) {
            myStreamCounter_ = myStreamCounter_ + 1;
            myId = myStreamCounter_;
            setName(name);
            anti = false;
            prec53 = false;
            myPrevU = Double.NaN;
            for (int i = 0; i < 6; ++i) {
                Bg[i] = Cg[i] = Ig[i] = nextSeed[i];
            }

        }

        /**
         * Returns a clone of the stream with exactly the same state
         *
         * @return
         */
        @Override
        public RNStream newInstance() {
            return newInstance(null);
        }

        /**
         * Returns a clone of the stream that has exactly the same state
         *
         * @param name
         * @return
         */
        @Override
        public RNStream newInstance(String name) {
            RNStream s = new RNStream(name);
            s.anti = anti;
            s.prec53 = prec53;
            s.myPrevU = myPrevU;
            for (int i = 0; i < 6; ++i) {
                s.Bg[i] = Bg[i];
                s.Cg[i] = Cg[i];
                s.Ig[i] = Ig[i];
            }
            return s;
        }

        /**
         * Returns a clone of the stream that has exactly the same state, but
         * generates antithetic values compared to its original
         *
         *
         * @return
         */
        @Override
        public RNStream newAntitheticInstance() {
            return newAntitheticInstance(null);
        }

        /**
         * Returns a clone of the stream that has exactly the same state, but
         * generates antithetic values compared to its original
         *
         *
         * @param name
         * @return
         */
        @Override
        public RNStream newAntitheticInstance(String name) {
            RNStream s = newInstance(name);
            if (s.getAntitheticOption()) {
                s.setAntitheticOption(false);
            } else {
                s.setAntitheticOption(true);
            }
            return s;
        }

        /**
         * Gets the name.
         *
         * @return The name of object.
         */
        @Override
        public final String getName() {
            return myName;
        }

        /**
         * Returns the id for this object
         *
         * @return
         */
        @Override
        public final long getId() {
            return (myId);
        }

        /**
         * Sets the name
         *
         * @param str The name as a string.
         */
        public final void setName(String str) {
            if (str == null) {
                myName = this.getClass().getSimpleName();
            } else {
                myName = str;
            }
        }

        /**
         * Returns the seed for the start of the stream
         *
         * @return
         */
        public final long[] getStartStreamSeed() {
            long[] seed = new long[6];
            for (int i = 0; i < 6; ++i) {
                seed[i] = (long) Ig[i];
            }
            return seed;
        }

        /**
         * Reinitializes the stream to its initial state: Cg and Bg are set to
         * Ig.
         */
        @Override
        public final void resetStartStream() {
            for (int i = 0; i < 6; ++i) {
                Cg[i] = Bg[i] = Ig[i];
            }
        }

        /**
         * Returns the seed for the start of the substream
         *
         * @return
         */
        public final long[] getStartSubStreamSeed() {
            long[] seed = new long[6];
            for (int i = 0; i < 6; ++i) {
                seed[i] = (long) Bg[i];
            }
            return seed;
        }

        /**
         * Reinitializes the stream to the beginning of its current substream:
         * Cg is set to Bg.
         */
        @Override
        public final void resetStartSubstream() {
            for (int i = 0; i < 6; ++i) {
                Cg[i] = Bg[i];
            }
        }

        /**
         * Reinitializes the stream to the beginning of its next substream: Ng
         * is computed, and Cg and Bg are set to Ng.
         */
        @Override
        public final void advanceToNextSubstream() {
            int i;
            matVecModM(A1p76, Bg, Bg, m1);
            double temp[] = new double[3];
            for (i = 0; i < 3; ++i) {
                temp[i] = Bg[i + 3];
            }
            matVecModM(A2p76, temp, temp, m2);
            for (i = 0; i < 3; ++i) {
                Bg[i + 3] = temp[i];
            }
            for (i = 0; i < 6; ++i) {
                Cg[i] = Bg[i];
            }
        }

        /**
         * If a = true, the stream will now generate antithetic variates.
         *
         * @param a flag to turn on antithetic variates
         */
        @Override
        public final void setAntitheticOption(boolean a) {
            anti = a;
        }

        /**
         * Returns whether or not the antithetic option is on (true) or off
         * (false)
         *
         * @return
         */
        @Override
        public final boolean getAntitheticOption() {
            return anti;
        }

        /**
         * If incp = true, each RNG call with this stream will now give 53 bits
         * of resolution instead of 32 bits (assuming that the machine follows
         * the IEEE-754 floating-point standard), and will advance the state of
         * the stream by 2 steps instead of 1.
         *
         * @param incp If incp = true, each RNG call with this stream will now
         * give 53 bits of resolution instead of 32 bits
         */
        public final void increasedPrecis(boolean incp) {
            prec53 = incp;
        }

        /**
         * Advances the state by n steps (see below for the meaning of n),
         * without modifying the states of the other streams or the values of Bg
         * and Ig in the current object. If e &gt;0, then n = 2^e +c; if e &lt;
         * 0, then n = -2^-e + c; and if e = 0, then n = c. Note, c is allowed
         * to take negative values
         *
         * @param e A long
         * @param c A long
         */
        public final void advanceState(int e, int c) {
            double B1[][] = new double[3][3], C1[][] = new double[3][3];
            double B2[][] = new double[3][3], C2[][] = new double[3][3];

            if (e > 0) {
                matTwoPowModM(A1p0, B1, m1, e);
                matTwoPowModM(A2p0, B2, m2, e);
            } else if (e < 0) {
                matTwoPowModM(InvA1, B1, m1, -e);
                matTwoPowModM(InvA2, B2, m2, -e);
            }

            if (c >= 0) {
                matPowModM(A1p0, C1, m1, c);
                matPowModM(A2p0, C2, m2, c);
            } else if (c < 0) {
                matPowModM(InvA1, C1, m1, -c);
                matPowModM(InvA2, C2, m2, -c);
            }

            if (e != 0) {
                matMatModM(B1, C1, C1, m1);
                matMatModM(B2, C2, C2, m2);
            }

            matVecModM(C1, Cg, Cg, m1);
            double[] cg3 = new double[3];
            for (int i = 0; i < 3; i++) {
                cg3[i] = Cg[i + 3];
            }
            matVecModM(C2, cg3, cg3, m2);
            for (int i = 0; i < 3; i++) {
                Cg[i + 3] = cg3[i];
            }
        }

        /**
         * Sets the initial seed Ig of the stream to the vector seed. The vector
         * seed should contain valid seed values. The state of the stream is
         * then reset to this initial seed. The states and seeds of the other
         * streams are not modified. As a result, after calling this procedure,
         * the initial seeds of the streams are no longer spaced Z values apart.
         * We discourage the use of this procedure; proper use of Reset is
         * preferable.
         *
         * @param seed the array of seeds
         * @return returns true if the seed is valid
         */
        public final boolean setSeed(long seed[]) {
            int i;
            if (CheckSeed(seed) != 0) {
                return false;                   // FAILURE
            }
            for (i = 0; i < 6; ++i) {
                Cg[i] = Bg[i] = Ig[i] = seed[i];
            }
            return true;                        // SUCCESS
        }

        /**
         * Returns the current state of this stream.
         *
         * @return An array representing the state
         */
        public final long[] getState() {
            long[] state = new long[6];
            for (int i = 0; i < 6; ++i) {
                state[i] = (long) Cg[i];
            }
            return state;
        }

        /**
         * Prints the current state of this stream.
         */
        public final void printState() {
            System.out.println("The RngStream");
            System.out.print("Name: ");
            System.out.println(getName());
            System.out.print("Id: ");
            System.out.println(getId());

            System.out.print("The current state:");

            System.out.print(":\n   Cg = { ");
            for (int i = 0; i < 5; i++) {
                System.out.print((long) Cg[i] + ", ");
            }
            System.out.println((long) Cg[5] + " }\n");
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("The RngStream\n");
            sb.append("Name: ");
            sb.append(getName());
            sb.append("\n");
            sb.append("Id: ");
            sb.append(getId());
            sb.append("\n");

            sb.append(":\n   anti = ");
            sb.append((anti ? "true" : "false"));
            sb.append("\n");
            sb.append("   prec53 = ");
            sb.append((prec53 ? "true" : "false"));
            sb.append("\n");

            sb.append("   Ig = { ");
            for (int i = 0; i < 5; i++) {
                sb.append((long) Ig[i]);
                sb.append(", ");
            }
            sb.append((long) Ig[5]);
            sb.append(" }");
            sb.append("\n");

            sb.append("   Bg = { ");
            for (int i = 0; i < 5; i++) {
                sb.append((long) Bg[i]);
                sb.append(", ");
            }
            sb.append((long) Bg[5]);
            sb.append(" }");
            sb.append("\n");

            sb.append("   Cg = { ");
            for (int i = 0; i < 5; i++) {
                sb.append((long) Cg[i]);
                sb.append(", ");
            }
            sb.append((long) Cg[5]);
            sb.append(" }");
            sb.append("\n");

            return sb.toString();
        }

        /**
         * Writes to the standard output the value of all internal variables of
         * this stream: name, anti, Ig, Bg, Cg
         */
        public final void printStateFull() {
            System.out.println("The RngStream");
            System.out.print("Name: ");
            System.out.println(getName());
            System.out.print("Id: ");
            System.out.println(getId());

            System.out.println(":\n   anti = " + (anti ? "true" : "false"));
            System.out.println("   prec53 = " + (prec53 ? "true" : "false"));

            System.out.print("   Ig = { ");
            for (int i = 0; i < 5; i++) {
                System.out.print((long) Ig[i] + ", ");
            }
            System.out.println((long) Ig[5] + " }");

            System.out.print("   Bg = { ");
            for (int i = 0; i < 5; i++) {
                System.out.print((long) Bg[i] + ", ");
            }
            System.out.println((long) Bg[5] + " }");

            System.out.print("   Cg = { ");
            for (int i = 0; i < 5; i++) {
                System.out.print((long) Cg[i] + ", ");
            }
            System.out.println((long) Cg[5] + " }\n");
        }

        /**
         * Returns a U(0, 1) (pseudo)random number, using this stream, after
         * advancing its state by one step. Exactly 0.0 or exactly 1.0 will not
         * be generated
         *
         * @return the uniform (0,1) pseudo random number
         */
        @Override
        public final double randU01() {
            double u = 0.0;
            if (prec53) {
                do {
                    u = U01d();
                    if (JSLMath.equal(u, 1.0, machinePrecision) || JSLMath.equal(u, 0.0, machinePrecision)) {
                        JSL.LOGGER.log(Level.WARNING, "randU01() machine precision check problem: u = {0}", u);
                    }
                } while (JSLMath.equal(u, 1.0, machinePrecision) || JSLMath.equal(u, 0.0, machinePrecision));
            } else {
                do {
                    u = U01();
                    if (JSLMath.equal(u, 1.0, machinePrecision) || JSLMath.equal(u, 0.0, machinePrecision)) {
                        JSL.LOGGER.log(Level.WARNING, "randU01() machine precision check problem: u = {0}", u);
                    }
                } while (JSLMath.equal(u, 1.0, machinePrecision) || JSLMath.equal(u, 0.0, machinePrecision));
            }
            myPrevU = u;
            return (u);
            //     if (prec53) return this.U01d();
            //    else return this.U01();
        }

        /**
         * The previous U(0,1) generated (returned) by randU01()
         *
         * @return
         */
        @Override
        public final double getPrevU01() {
            return myPrevU;
        }

        /**
         * Returns the antithetic of the previous U(0,1) i.e. 1.0 - getPrevU01()
         *
         * @return
         */
        @Override
        public final double getAntitheticValue() {
            return 1.0 - myPrevU;
        }

        /**
         * Returns a (pseudo)random number from the discrete uniform
         * distribution over the integers {i, i + 1, . . . , j }, using this
         * stream. Calls randU01 once.
         *
         * @param i start of range
         * @param j end of range
         * @return The integer pseudo random number
         */
        @Override
        public final int randInt(int i, int j) {
            if (i > j) {
                throw new IllegalArgumentException("The lower limit must be <= the upper limit");
            }
            return (i + (int) (randU01() * (j - i + 1)));
        }

        // Generate a uniform random number, with 32 bits of resolution.
        private double U01() {
            int k;
            double p1, p2, u;
            /* Component 1 */
            p1 = a12 * Cg[1] - a13n * Cg[0];
            k = (int) (p1 / m1);
            p1 -= k * m1;
            if (p1 < 0.0) {
                p1 += m1;
            }
            Cg[0] = Cg[1];
            Cg[1] = Cg[2];
            Cg[2] = p1;
            /* Component 2 */
            p2 = a21 * Cg[5] - a23n * Cg[3];
            k = (int) (p2 / m2);
            p2 -= k * m2;
            if (p2 < 0.0) {
                p2 += m2;
            }
            Cg[3] = Cg[4];
            Cg[4] = Cg[5];
            Cg[5] = p2;
            /* Combination */
            u = ((p1 > p2) ? (p1 - p2) * norm : (p1 - p2 + m1) * norm);
            return (anti) ? (1 - u) : u;
        }

        // Generate a uniform random number, with 52 bits of resolution.
        private double U01d() {
            double u = U01();
            if (anti) {
                // Antithetic case: note that U01 already returns 1-u.
                u += (U01() - 1.0) * invtwo24;
                return (u < 0.0) ? u + 1.0 : u;
            } else {
                u += U01() * invtwo24;
                return (u < 1.0) ? u : (u - 1.0);
            }
        }
    }
}
