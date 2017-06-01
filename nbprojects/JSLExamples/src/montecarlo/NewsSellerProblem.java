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
package montecarlo;

import jsl.utilities.random.distributions.DEmpiricalPMF;
import jsl.utilities.random.distributions.DistributionIfc;
import jsl.utilities.statistic.*;

/**
 * 
 */
public class NewsSellerProblem {

    private DEmpiricalPMF typeofday;

    private DEmpiricalPMF gd;

    private DEmpiricalPMF fd;

    private DEmpiricalPMF pd;

    private DistributionIfc[] demand = new DistributionIfc[4];

    private double price = 0.50;

    private double cost = 0.33;

    private double lostRevenueCost = 0.17;

    private double scrapPrice = 0.05;

    private int qMin = 50;

    private int qMax = 100;

    /**
     *
     */
    public NewsSellerProblem() {
        //super();
        System.out.println("Constructing NSP");
        typeofday = new DEmpiricalPMF();
        typeofday.addProbabilityPoint(1.0, 0.35);
        typeofday.addProbabilityPoint(2.0, 0.45);
        typeofday.addLastProbabilityPoint(3.0);

        gd = new DEmpiricalPMF();
        gd.addProbabilityPoint(40.0, 0.03);
        gd.addProbabilityPoint(50.0, 0.05);
        gd.addProbabilityPoint(60.0, 0.15);
        gd.addProbabilityPoint(70.0, 0.2);
        gd.addProbabilityPoint(80.0, 0.35);
        gd.addProbabilityPoint(90.0, 0.15);
        gd.addLastProbabilityPoint(100.0);

        fd = new DEmpiricalPMF();
        fd.addProbabilityPoint(40.0, 0.1);
        fd.addProbabilityPoint(50.0, 0.18);
        fd.addProbabilityPoint(60.0, 0.4);
        fd.addProbabilityPoint(70.0, 0.2);
        fd.addProbabilityPoint(80.0, 0.08);
        fd.addLastProbabilityPoint(90.0);

        pd = new DEmpiricalPMF();
        pd.addProbabilityPoint(40.0, 0.44);
        pd.addProbabilityPoint(50.0, 0.22);
        pd.addProbabilityPoint(60.0, 0.16);
        pd.addProbabilityPoint(70.0, 0.12);
        pd.addLastProbabilityPoint(80.0);

        demand = new DistributionIfc[4];

        demand[1] = gd;
        demand[2] = fd;
        demand[3] = pd;

    }

    public void setPrice(double p) {
        price = p;
    }

    public double getPrice() {
        return (price);
    }

    public void runSimulation() {

        Statistic profitStat = new Statistic("Profit Statistics");

        for (int q = qMin; q <= qMax; q = q + 10) {
            for (int k = 1; k <= 500; k++) {
                double d = demand[(int) typeofday.getValue()].getValue();
                double profit = price * Math.min(d, q) - cost * q
                        - lostRevenueCost * Math.max(0, d - q) + scrapPrice
                        * Math.max(0, q - d);
                profitStat.collect(profit);
            }
            System.out.println("Statistics for q = " + q);
            System.out.println(profitStat.getAverage());
            profitStat.reset();
        }
    }

    public static void main(String[] args) {

        NewsSellerProblem nsp = new NewsSellerProblem();
        nsp.setPrice(.75);
        nsp.runSimulation();
        nsp.setPrice(1.00);
        nsp.runSimulation();
    }
}
