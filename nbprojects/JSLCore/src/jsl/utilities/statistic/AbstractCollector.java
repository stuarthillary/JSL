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
package jsl.utilities.statistic;

import jsl.utilities.GetValueIfc;

/**
 * An abstract base class for building sub-classes that implement the
 * CollectorIfc
 *
 * @author rossetti
 *
 */
public abstract class AbstractCollector implements CollectorIfc, SaveDataIfc {

    /**
     * A counter to count the number of created to assign "unique" ids
     */
    private static long myIdCounter_;

    /**
     * The id of this object
     */
    protected long myId;

    /**
     * Holds the name of the statistic for reporting purposes.
     */
    protected String myName;

    /**
     * The array to collect the data if the saved flag is true If the statistic
     * is reset and data was saved, this array should be cleared (as if no data
     * had been collected). Sub-classes should use the protected method
     * clearSavedData() within their implementations of the abstract reset()
     * method
     *
     */
    protected double[] myData;

    /**
     * Used to set the array size increment when the collect data option is
     * turned on
     *
     */
    protected int myDataArraySize = DEFAULT_DATA_ARRAY_SIZE;

    /**
     * Counts the number of data points that were saved to the save array
     *
     */
    protected int mySaveCount = 0;

    /**
     * A flag to indicate whether or not the statistic should save the data as
     * it is collected. Sub-classes should use the protected method
     * saveData(double x) to store the data within their implementations of the
     * abstract collect() method if this flag is true, the data should start to
     * be saved.
     *
     */
    protected boolean mySaveDataFlag = false;

    /**
     * The weights associated with each saved data point
     *
     */
    protected double[] myWeights;

    /**
     * The flag that indicates if collection will continue
     *
     */
    private boolean myCollectionFlag = true;

    /**
     *
     */
    public AbstractCollector() {
        this(null);
    }

    /**
     *
     * @param name
     */
    public AbstractCollector(String name) {
        setId();
        setName(name);
    }

    @Override
    public final boolean isTurnedOff() {
        return myCollectionFlag == false;
    }

    @Override
    public final boolean isTurnedOn() {
        return myCollectionFlag == true;
    }

    @Override
    public final void turnOn() {
        myCollectionFlag = true;
    }

    @Override
    public final void turnOff() {
        myCollectionFlag = false;
    }

    @Override
    public final String getName() {
        return myName;
    }

    /**
     * Sets the name
     *
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
    public final boolean collect(GetValueIfc v) {
        return collect(v.getValue(), 1.0);
    }

    @Override
    public final boolean collect(GetValueIfc v, double weight) {
        if (v == null) {
            throw new IllegalArgumentException("The suppled GetValueIfc was null");
        }

        return collect(v.getValue(), weight);
    }

    @Override
    public final boolean collect(boolean value) {
        double x = 0.0;
        if (value) {
            x = 1.0;
        }
        return collect(x, 1.0);
    }

    @Override
    public final boolean collect(boolean value, double weight) {
        double x = 0.0;
        if (value) {
            x = 1.0;
        }
        return collect(x, weight);
    }

    @Override
    public final boolean collect(double value) {
        return collect(value, 1.0);
    }

    @Override
    public final boolean collect(double[] values) {
        boolean b = true;
        for (double x : values) {
            b = collect(x);
            if (b == false) {
                break;
            }
        }
        return b;
    }

    @Override
    public final boolean collect(double[] x, double[] w) {
        if (x.length != w.length) {
            throw new IllegalArgumentException("The supplied arrays are not of equal length");
        }
        boolean b = true;
        for (int i = 0; i < x.length; i++) {
            b = collect(x[i], w[i]);
            if (b == false) {
                break;
            }
        }
        return b;
    }

    @Override
    abstract public boolean collect(double x, double weight);

    @Override
    abstract public void reset();

    protected void setId() {
        myIdCounter_ = myIdCounter_ + 1;
        myId = myIdCounter_;
    }

    /**
     * Used to clear out data saved to an array during collection
     *
     */
    protected void clearSavedData() {
        if (myData == null) {
            return;
        }
        myData = null;
        myWeights = null;
        mySaveCount = 0;
    }

    @Override
    public boolean getSaveDataOption() {
        return mySaveDataFlag;
    }

    @Override
    public double[] getSavedData() {
        if (myData == null) {
            return null;
        }
        double[] tmp = new double[mySaveCount];
        System.arraycopy(myData, 0, tmp, 0, mySaveCount);
        return tmp;
    }

    @Override
    public double[] getSavedWeights() {
        if (myWeights == null) {
            return null;
        }
        double[] tmp = new double[mySaveCount];
        System.arraycopy(myWeights, 0, tmp, 0, mySaveCount);
        return tmp;
    }

    /**
     * Used to save data to an array during collection
     *
     * @param x
     * @param w
     */
    protected void saveData(double x, double w) {
        if (myData == null) {
            myData = new double[myDataArraySize];
            myWeights = new double[myDataArraySize];
        }
        // need to save x into the array
        mySaveCount++;
        if (mySaveCount > myData.length) {
            // need to grow the array
            double[] tmp = new double[myData.length + myDataArraySize];
            double[] tmp2 = new double[myData.length + myDataArraySize];
            System.arraycopy(myData, 0, tmp, 0, myData.length);
            System.arraycopy(myWeights, 0, tmp2, 0, myData.length);
            myData = tmp;
            myWeights = tmp2;
        }
        myData[mySaveCount - 1] = x;
        myWeights[mySaveCount - 1] = w;
    }

    @Override
    public void setSaveDataArraySizeIncrement(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("The array size increment must be > 0");
        }
        myDataArraySize = n;
    }

    @Override
    public void setSaveDataOption(boolean flag) {
        mySaveDataFlag = flag;
    }
}
