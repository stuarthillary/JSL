/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package montecarlo;

import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.distributions.Uniform;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RandomStreamIfc;
import jsl.utilities.random.rng.RngIfc;
import jsl.utilities.statistic.Statistic;
import jsl.utilities.statistic.StatisticXY;

/**
 *
 * @author rossetti
 */
public class AntitheticVRT {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        example1();

        example2();

        example3();

        example4();

        example5();

    }

    public static void example1() {
        // estimating the mean of a rv via antithetic variates

        Uniform nf = new Uniform();

        //       Normal nf = new Normal(10, 2);
        Statistic s = new Statistic("Crude Estimator");
        StatisticXY sxy = new StatisticXY();

        int m = 1000; // number of antithetic pairs
        int n = 2 * m;  // number of samples

        // sample a total of n observations
        for (int i = 1; i <= n; i++) {
            s.collect(nf);
        }

        System.out.println(s);

        s.reset();
        s.setName("AV Estimator");
        double x = 0;
        double xa = 0;
        // sample a total of n observations but pair them
        for (int i = 1; i <= n; i++) {
            if ((i % 2) == 0) {
                // even number
                nf.resetStartSubstream();
                nf.setAntitheticOption(true);
                xa = nf.getValue();
                //System.out.println("\t xa = " + xa);
                s.collect((x + xa) / 2.0);
                sxy.collectXY(x, xa);
            } else {
                // odd number
                // there are so many substreams that advancing doesn't matter
                nf.advanceToNextSubstream();
                nf.setAntitheticOption(false);
                x = nf.getValue();
                //System.out.print("x = " + x);
            }
        }
        System.out.println("--------------------");
        System.out.println("Example 1");
        System.out.println(s);
        System.out.print(sxy);
    }

    public static void example2() {
        RNStreamFactory f1 = new RNStreamFactory();

        // get a stream
        RngIfc s1 = f1.getStream();

        System.out.println(s1);

        RNStreamFactory f2 = new RNStreamFactory();

        // get a stream
        RngIfc s2 = f2.getStream();

        s2.setAntitheticOption(true);

        System.out.println(s2);

        System.out.println("Both streams have same seed but are antithetic");

        int m = 1000; // number of antithetic pairs
        int n = 2 * m;  // number of samples

        // use the antithetic streams
        Normal nf1 = new Normal(10.0, 2.0, s1);
        Normal nf2 = new Normal(10.0, 2.0, s2);
        Statistic s = new Statistic("Antithetic");
        StatisticXY sxy = new StatisticXY();

        for (int i = 1; i <= m; i++) {
            double x = nf1.getValue();
            double xa = nf2.getValue();
            sxy.collectXY(x, xa);
            s.collect((x + xa) / 2.0);
        }
        System.out.println("--------------------");
        System.out.println("Example 2");
        System.out.println(s);
        System.out.println(sxy);
    }

    public static void example3() {
        // recall that you can just do inverse transform yourself

        Uniform uf = new Uniform();
        Normal nf = new Normal(10.0, 2.0);

        int m = 1000; // number of antithetic pairs
        int n = 2 * m;  // number of samples
        Statistic s = new Statistic("Antithetic");
        StatisticXY sxy = new StatisticXY();

        for (int i = 1; i <= m; i++) {
            double u = uf.getValue();
            double x = nf.invCDF(u);
            double xa = nf.invCDF(1.0 - u);
            sxy.collectXY(x, xa);
            s.collect((x + xa) / 2.0);
        }
        System.out.println("--------------------");
        System.out.println("Example 3");
        System.out.println(s);
        System.out.println(sxy);

    }

    public static void example4() {
        Normal nf = new Normal(10.0, 2.0);
        Normal nfa = nf.newAntitheticInstance();

        int m = 1000; // number of antithetic pairs
        int n = 2 * m;  // number of samples
        Statistic s = new Statistic("Antithetic");
        StatisticXY sxy = new StatisticXY();

        for (int i = 1; i <= m; i++) {
            double x = nf.getValue();
            double xa = nfa.getValue();
            sxy.collectXY(x, xa);
            s.collect((x + xa) / 2.0);
        }
        System.out.println("--------------------");
        System.out.println("Example 4");
        System.out.println(s);
        System.out.println(sxy);
    }

    public static void example5() {
        Normal nf = new Normal(10.0, 2.0);

        int m = 1000; // number of antithetic pairs
        int n = 2 * m;  // number of samples
        Statistic s = new Statistic("Antithetic");
        StatisticXY sxy = new StatisticXY();

        for (int i = 1; i <= m; i++) {
            double x = nf.getValue();
            double xa = nf.getAntitheticValue();
            sxy.collectXY(x, xa);
            s.collect((x + xa) / 2.0);
        }
        System.out.println("--------------------");
        System.out.println("Example 5");
        System.out.println(s);
        System.out.println(sxy);
    }
}
