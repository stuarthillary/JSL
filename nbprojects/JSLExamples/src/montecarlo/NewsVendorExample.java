/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package montecarlo;

import jsl.utilities.random.distributions.DEmpiricalCDF;
import jsl.utilities.statistic.Statistic;

/**
 *
 * @author rossetti
 */
public class NewsVendorExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        double q = 30; // order qty
        double s = 0.25; //sales price
        double c = 0.15; // unit cost
        double u = 0.02; //salvage value
        double[] pm = {5, 0.1, 10, 0.3, 40, 0.6, 45, 0.8, 50, 0.9, 55, 0.95, 60, 1.0};
        DEmpiricalCDF dCDF = new DEmpiricalCDF(pm);
        Statistic stat = new Statistic("Profit");
        double n = 1994; // sampel size
        for (int i = 1; i <= n; i++) {
            double d = dCDF.getValue();
            double amtSold = Math.min(d, q);
            double amtLeft = Math.max(0, q - d);
            double g = s * amtSold + u * amtLeft - c * q;
            stat.collect(g);
        }
        System.out.printf("%s \t %f %n", "Count = ", stat.getCount());
        System.out.printf("%s \t %f %n", "Average = ", stat.getAverage());
        System.out.printf("%s \t %f %n", "Std. Dev. = ", stat.getStandardDeviation());
        System.out.printf("%s \t %f %n", "Half-width = ", stat.getHalfWidth());
        System.out.println(stat.getConfidenceLevel() * 100 + "% CI = " + stat.getConfidenceInterval());
    }

}
