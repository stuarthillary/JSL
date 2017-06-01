/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package randomnumbers;

import jsl.utilities.random.distributions.Binomial;
import jsl.utilities.random.distributions.DUniform;
import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.distributions.Uniform;
import jsl.utilities.random.rng.RNStreamFactory;

/**
 *
 * @author rossetti
 */
public class UseDistributionExample {

    public static void main(String[] args) {
        // make and use a Uniform(a, b) distribution
        Uniform uDF = new Uniform(10.0, 20.0);
        // make and use a DUniform(a, b) distribution
        DUniform duDF = new DUniform(5, 10);
        // make and use a Binomial(p, n) distribution
        Binomial bnDF = new Binomial(0.8, 10);
        // make and use a Normal(mean, var) distribution
        Normal nDF = new Normal(20, 5.0);
        System.out.printf("%10s %10s %10s %10s %10s\n", "i", "u",
                "du", "bn", "n");
        for (int i = 1; i <= 5; i++) {
            double u = uDF.getValue();
            double du = duDF.getValue();
            double bn = bnDF.getValue();
            double n = nDF.getValue();
            System.out.printf("%10d %10.4f %10.1f %10.1f %10.3f\n", i, u,
                    du, bn, n);
        }

        // reset some streams and run again
        bnDF.resetStartStream();
        nDF.resetStartStream();
        System.out.println();
        System.out.printf("%10s %10s %10s %10s %10s\n", "i", "u",
                "du", "bn", "n");
        for (int i = 1; i <= 5; i++) {
            double u = uDF.getValue();
            double du = duDF.getValue();
            double bn = bnDF.getValue();
            double n = nDF.getValue();
            System.out.printf("%10d %10.4f %10.1f %10.1f %10.3f\n", i, u,
                    du, bn, n);
        }

        // changing a distributions parameters
        nDF.setMean(100.0);
        nDF.setVariance(4.0);
        double[] param = new double[2];
        param[0] = 50.0;
        param[1] = 100.0;
        uDF.setParameters(param);
        System.out.println();
        System.out.printf("%10s %10s %10s\n", "i", "u", "n");
        for (int i = 1; i <= 5; i++) {
            double u = uDF.getValue();
            double n = nDF.getValue();
            System.out.printf("%10d %10.3f %10.3f\n", i, u, n);
        }

        //illustrate fine stream control
        // make a factory for creating streams
        RNStreamFactory f1 = new RNStreamFactory();

        // get the first stream from the factory
        RNStreamFactory.RNStream f1s1 = f1.getStream();

        // make another factory, the factories are identical
        RNStreamFactory f2 = new RNStreamFactory();

        // thus the first streams returned are identical
        RNStreamFactory.RNStream f2s1 = f2.getStream();

        // now tell the stream to produce antithetic random numbers
        // f2s1 and f1s1 are now antithetic to each other
        f2s1.setAntitheticOption(true);
        System.out.println();
        System.out.printf("%10s %10s %10s %10s\n", "i", "u1", "u2", "u1 + u2");
        for (int i = 1; i <= 10; i++) {
            double u1 = f1s1.randU01();
            double u2 = f2s1.randU01();
            System.out.printf("%10d %10.3f %10.3f %10.3f\n", i, u1, u2, u1+u2);
        }
        
        // set up the normals to use the antithetics
        Normal n1 = new Normal(20, 4, f1s1);
        Normal n2 = new Normal(20, 4, f2s1);
        System.out.println();
        System.out.printf("%10s %10s %10s\n", "i", "n1", "n2");
        for (int i = 1; i <= 10; i++) {
            double x = n1.getValue();
            double y = n2.getValue();
            System.out.printf("%10d %10.3f %10.3f\n", i, x, y);
        }

    }

}
