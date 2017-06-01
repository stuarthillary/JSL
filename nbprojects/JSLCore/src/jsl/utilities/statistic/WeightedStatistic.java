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

/**
 * Collects basic weighted statistical summary
 *
 * @author rossetti
 *
 */
public class WeightedStatistic extends AbstractCollector implements WeightedStatisticIfc, GetCSVStatisticIfc {

    /**
     * Used to count the number of missing data points presented When a data
     * point having the value of (Double.NaN, Double.POSITIVE_INFINITY,
     * Double.NEGATIVE_INFINITY) are presented it is excluded from the summary
     * statistics and the number of missing points is noted. Implementers of
     * subclasses are responsible for properly collecting this value and
     * resetting this value.
     *
     */
    protected double myNumMissing = 0.0;

    /**
     * Holds the minimum of the observed data.
     */
    protected double min = Double.POSITIVE_INFINITY;

    /**
     * Holds the maximum of the observed data
     */
    protected double max = Double.NEGATIVE_INFINITY;

    /**
     * Holds the number of observations observed
     */
    protected double num = 0.0;

    /**
     * Holds the weighted sum of the data.
     */
    protected double wsum = 0.0;

    /**
     * Holds the weighted sum of squares of the data.
     */
    protected double wsumsq = 0.0;

    /**
     * Holds the sum of the weights observed.
     */
    protected double sumw = 0.0;

    /**
     *
     */
    public WeightedStatistic() {
        this(null);
    }

    /**
     * @param name
     */
    public WeightedStatistic(String name) {
        super(name);
        reset();
    }

    @Override
    public final boolean collect(double x, double weight) {
        if (isTurnedOff()) {
            return false;
        }
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            myNumMissing++;
            return true;
        }

        if (getSaveDataOption()) {
            saveData(x, weight);
        }
        // update moments
        num = num + 1.0;
        sumw = sumw + weight;
        wsum = wsum + x * weight;
        wsumsq = wsumsq + x * x * weight;

        // update min, max, current value, current weight
        if (x > max) {
            max = x;
        }
        if (x < min) {
            min = x;
        }
        return true;
    }

    /**
     * Returns a statistic that summarizes the passed in array of values
     *
     * @param x the values to compute statistics for
     * @return
     */
    public static WeightedStatistic collectStatistics(double[] x) {
        WeightedStatistic s = new WeightedStatistic();
        s.collect(x);
        return (s);
    }

    /**
     * Returns a statistic that summarizes the passed in arrays The lengths of
     * the arrays must be the same.
     *
     * @param x the values
     * @param w the weights
     * @return
     */
    public static WeightedStatistic collectStatistics(double[] x, double[] w) {
        if (x.length != w.length) {
            throw new IllegalArgumentException("The supplied arrays are not of equal length");
        }

        WeightedStatistic s = new WeightedStatistic();
        s.collect(x, w);

        return (s);
    }

    /**
     * Creates a instance of Statistic that is a copy of the supplied Statistic
     * All internal state is the same except for the id of the returned
     * Statistic
     *
     * @param stat
     * @return
     */
    public static WeightedStatistic newInstance(WeightedStatistic stat) {
        WeightedStatistic s = new WeightedStatistic();
        s.max = stat.max;
        s.min = stat.min;
        s.myName = stat.myName;
        s.num = stat.num;
        s.sumw = stat.sumw;
        s.wsum = stat.wsum;
        s.wsumsq = stat.wsumsq;
        return (s);
    }

    /**
     * Creates a instance of Statistic that is a copy of this Statistic All
     * internal state is the same except for the id of the returned Statistic
     *
     * @return
     */
    public final WeightedStatistic newInstance() {
        WeightedStatistic s = new WeightedStatistic();
        s.max = max;
        s.min = min;
        s.myName = myName;
        s.num = num;
        s.sumw = sumw;
        s.wsum = wsum;
        s.wsumsq = wsumsq;
        return (s);
    }

    /* (non-Javadoc)
     * @see jsl.utilities.statistic.AbstractCollector#reset()
     */
    @Override
    public final void reset() {
        num = 0.0;
        wsum = 0.0;
        sumw = 0.0;
        wsumsq = 0.0;
        min = Double.POSITIVE_INFINITY;
        max = Double.NEGATIVE_INFINITY;
        myNumMissing = 0.0;
        clearSavedData();
    }

    /**
     * Gets the weighted average of the collected observations.
     *
     * @return A double representing the weighted average or Double.NaN if no
     * observations.
     */
    @Override
    public final double getAverage() {
        if (sumw <= 0.0) {
            return Double.NaN;
        }
        return (wsum / sumw);
    }

    /**
     * Gets the count of the number of the observations.
     *
     * @return A double representing the count
     */
    @Override
    public final double getCount() {
        return (num);
    }

    /**
     * Gets the weighted sum of observations observed.
     *
     * @return A double representing the weighted sum
     */
    @Override
    public final double getWeightedSum() {
        return (wsum);
    }

    /**
     * Gets the sum of the observed weights.
     *
     * @return A double representing the sum of the weights
     */
    @Override
    public final double getSumOfWeights() {
        return (sumw);
    }

    /**
     * Gets the weighted sum of squares (sum of x*x*w)
     *
     * @return
     */
    @Override
    public final double getWeightedSumOfSquares() {
        return wsumsq;
    }

    /**
     * Gets the minimum of the observations.
     *
     * @return A double representing the minimum
     */
    @Override
    public final double getMin() {
        return (min);
    }

    /**
     * Gets the maximum of the observations.
     *
     * @return A double representing the maximum
     */
    @Override
    public final double getMax() {
        return (max);
    }

    /**
     * When a data point having the value of (Double.NaN,
     * Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY) are presented it is
     * excluded from the summary statistics and the number of missing points is
     * noted. This method reports the number of missing points that occurred
     * during the collection
     *
     * @return
     */
    public double getNumberMissing() {
        return (myNumMissing);
    }

    /**
     * Returns a String representation of the Statistic
     *
     * @return A String with basic summary statistics
     */
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID ");
        sb.append(getId());
        sb.append("\n");

        sb.append("Name ");
        sb.append(getName());
        sb.append("\n");

        sb.append("Number ");
        sb.append(getCount());
        sb.append("\n");

        sb.append("Minimum ");
        sb.append(getMin());
        sb.append("\n");

        sb.append("Maximum ");
        sb.append(getMax());
        sb.append("\n");

        sb.append("Weighted Average ");
        sb.append(getAverage());
        sb.append("\n");

        sb.append("Weighted Sum ");
        sb.append(getWeightedSum());
        sb.append("\n");

        sb.append("Weighted Sum of Squares ");
        sb.append(getWeightedSumOfSquares());
        sb.append("\n");

        sb.append("Sum of Weights ");
        sb.append(getSumOfWeights());
        sb.append("\n");

        sb.append("Number Missing ");
        sb.append(getNumberMissing());
        sb.append("\n");

        return (sb.toString());
    }

    /**
     * Fills up the supplied array with the statistics defined by index =
     * statistic
     *
     * statistics[0] = getCount(); statistics[1] = getAverage(); statistics[2] =
     * getMin(); statistics[3] = getMax(); statistics[4] = getSum();
     * statistics[5] = getSumOfWeights(); statistics[6] =
     * getWeightedSumOfSquares();
     *
     * The array must be of size 6 or an exception will be thrown
     *
     * @param statistics the array to fill
     */
    public final void getStatistics(double[] statistics) {
        if (statistics.length != 7) {
            throw new IllegalArgumentException("The supplied array was not of size 7");
        }

        statistics[0] = getCount();
        statistics[1] = getAverage();
        statistics[2] = getMin();
        statistics[3] = getMax();
        statistics[4] = getWeightedSum();
        statistics[5] = getSumOfWeights();
        statistics[6] = getWeightedSumOfSquares();
    }

    /**
     * Returns an array with the statistics defined by index = statistic
     *
     * statistics[0] = getCount(); statistics[1] = getAverage(); statistics[2] =
     * getMin(); statistics[3] = getMax(); statistics[4] = getSum();
     * statistics[5] = getSumOfWeights(); statistics[6] =
     * getWeightedSumOfSquares();
     *
     * @return the array of statistics
     */
    public final double[] getStatistics() {
        double[] x = new double[7];
        getStatistics(x);
        return (x);
    }

    /**
     * s[0] = "Count"; s[1] = "Average"; s[2] = "Minimum"; s[3] = "Maximum";
     * s[4] = "Weighted Sum"; s[5] = "Sum of Weights"; s[6] = "Weighted sum of
     * squares";
     *
     * @return the headers
     */
    public String[] getStatisticsHeader() {
        String[] s = new String[7];
        s[0] = "Count";
        s[1] = "Average";
        s[2] = "Minimum";
        s[3] = "Maximum";
        s[4] = "Weighted Sum";
        s[5] = "Sum of Weights";
        s[6] = "Weighted sum of squares";
        return s;
    }

    @Override
    public String getCSVStatistic() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(",");
        double[] stats = getStatistics();
        for (int i = 0; i < stats.length; i++) {
            if (Double.isNaN(stats[i]) || Double.isInfinite(stats[i])) {
                sb.append("");
            } else {
                sb.append(stats[i]);
            }
            if (i < stats.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    @Override
    public String getCSVStatisticHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("Statistic Name,");
        sb.append("Count,");
        sb.append("Average,");
        sb.append("Minimum,");
        sb.append("Maximum,");
        sb.append("Weighted Sum,");
        sb.append("Sum of Weights,");
        sb.append("Weighted sum of squares");
        return sb.toString();
    }
}
