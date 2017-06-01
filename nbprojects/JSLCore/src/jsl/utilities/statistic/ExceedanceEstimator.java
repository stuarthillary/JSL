/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.statistic;

import java.util.Arrays;
import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.distributions.Uniform;

/** Tabulates the proportion and frequency for a random variable X &gt; a(i)
 *  where a(i) are thresholds.
 *
 * @author rossetti
 */
public class ExceedanceEstimator extends AbstractCollector {

    /**
     * The thresholds for the exceedance estimates
     *
     */
    protected double[] myThresholds;

    /**
     * Counts the number of times threshold is exceeded
     *
     */
    protected double[] myCounts;

    /**
     * Holds the number of observations observed
     */
    protected double num = 0.0;

    public ExceedanceEstimator(double... thresholds) {
        this(null, thresholds);
    }

    public ExceedanceEstimator(String name, double[] thresholds) {
        super(name);
        setThresholds(thresholds);
        num = 0.0;
    }

    private void setThresholds(double[] thresholds) {
        if (thresholds == null) {
            throw new IllegalArgumentException("The threshold array was null");
        }
        myThresholds = new double[thresholds.length];
        myCounts = new double[thresholds.length];
        System.arraycopy(thresholds, 0, myThresholds, 0, thresholds.length);
        Arrays.sort(myThresholds);
    }

    @Override
    public boolean collect(double x, double weight) {
        if (isTurnedOff()){
            return false;
        }
        if (getSaveDataOption()) {
            saveData(x, weight);
        }
        num = num + 1.0;
        for (int i = 0; i < myThresholds.length; i++) {
            if (x > myThresholds[i]) {
                myCounts[i]++;
            }
        }
        return true;
    }

    @Override
    public void reset() {
        num = 0.0;
        for (int i = 0; i < myCounts.length; i++) {
            myCounts[i] = 0.0;
        }
        clearSavedData();
    }

    public final double[] getFrequencies() {
        double[] f = new double[myCounts.length];
        System.arraycopy(myCounts, 0, f, 0, myCounts.length);
        return f;
    }

    public final double getFrequency(int i) {
        return myCounts[i];
    }

    public final double getProportion(int i) {
        if (num > 0) {
            return myCounts[i] / num;
        } else {
            return 0.0;
        }
    }

    public final double[] getProportions() {
        double[] f = getFrequencies();
        if (num == 0.0) {
            return f;
        }
        for (int i = 0; i < f.length; i++) {
            f[i] = (f[i] / num);
        }
        return f;
    }

    public final double[][] getValueFrequencies() {
        double[][] f = new double[myCounts.length][2];
        for (int i = 0; i < myCounts.length; i++) {
            f[i][0] = myThresholds[i];
            f[i][1] = myCounts[i];
        }
        return f;
    }

    public final double[][] getValueProportions() {
        double[][] f = new double[myCounts.length][2];
        for (int i = 0; i < myCounts.length; i++) {
            f[i][0] = myThresholds[i];
            if (num > 0) {
                f[i][1] = myCounts[i] / num;
            }
        }
        return f;
    }

    /**
     * Gets the count of the number of the observations.
     *
     * @return A double representing the count
     */
    public final double getCount() {
        return (num);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Exceedance Tabulation ");
        sb.append(getName());
        sb.append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Number of thresholds = ");
        sb.append(myThresholds.length);
        sb.append("\n");
        sb.append("Count = ");
        sb.append(getCount());
        sb.append("\n");
        sb.append("----------------------------------------\n");
        if (getCount() > 0) {
            sb.append("Threshold \t Count \t p \t 1-p \n");
            for (int i = 0; i < myThresholds.length; i++) {
                double p = myCounts[i] / num;
                double cp = 1.0 - p;
                sb.append("{X > ");
                sb.append(myThresholds[i]);
                sb.append("}\t");
                sb.append(myCounts[i]);
                sb.append("\t");
                sb.append(p);
                sb.append("\t");
                sb.append(cp);
                sb.append("\n");
            }
            sb.append("----------------------------------------\n");
            sb.append("\n");
        }

        return (sb.toString());
    }

    public static void main(String[] args) {

        Uniform du = new Uniform(0, 100);

        double[] t = {0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0};
        ExceedanceEstimator f = new ExceedanceEstimator(t);

        f.collect(du.getSample(10000));
        System.out.println("Testing");
        System.out.println(f);

        Normal n = new Normal();
        ExceedanceEstimator e = new ExceedanceEstimator(Normal.stdNormalInvCDF(0.95));
        e.collect(n.getSample(10000));
        System.out.println(e);

    }
}
