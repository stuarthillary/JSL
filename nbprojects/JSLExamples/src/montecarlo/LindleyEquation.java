/**
 *
 */
package montecarlo;

import java.io.File;
import java.io.PrintWriter;
import jsl.utilities.random.*;
import jsl.utilities.random.distributions.Distribution;
import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.reporting.JSL;
import jsl.utilities.statistic.*;

/**
 * @author rossetti
 *
 */
public class LindleyEquation {

    /**
     * @param args
     */
    public static void main(String[] args) {

//		replicationDeletionExample();

//        oneLongRunExample();
        
        controlVariateExample();

    }

    public static void replicationDeletionExample() {
        // inter-arrival time distribution
        RandomIfc y = new Exponential(1.0);
        // service time distribution
        RandomIfc x = new Exponential(0.7);
        int r = 30; // number of replications
        int n = 100000; // number of customers
        int d = 10000; // warm up
        Statistic avgw = new Statistic("Across rep avg waiting time");
        Statistic avgpw = new Statistic("Across rep prob of wait");
        Statistic wbar = new Statistic("Within rep avg waiting time");
        Statistic pw = new Statistic("Within rep prob of wait");
        for (int i = 1; i <= r; i++) {
            double w = 0; // initial waiting time
            for (int j = 1; j <= n; j++) {
                w = Math.max(0.0, w + x.getValue() - y.getValue());
                wbar.collect(w);// collect waiting time
                pw.collect((w > 0.0)); // collect P(W>0)
                if (j == d) {// clear stats at warmup
                    wbar.reset();
                    pw.reset();
                }
            }
            //collect across replication statistics
            avgw.collect(wbar.getAverage());
            avgpw.collect(pw.getAverage());
            // clear within replication statistics for next rep
            wbar.reset();
            pw.reset();
        }
        System.out.println("Replication/Deletion Lindley Equation Example");
        System.out.println(avgw);
        System.out.println(avgpw);
    }

    public static void controlVariateExample() {
        File csvFile = JSL.makeFile("controlVariateOut", "csv");
        PrintWriter out = JSL.makePrintWriter(csvFile);
        // inter-arrival time distribution
        Distribution y = new Exponential(1.0);
        // service time distribution
        Distribution x = new Exponential(0.7);
        int r = 30; // number of replications
        int n = 10000; // number of customers
        int d = 5000; // warm up
        Statistic avgw = new Statistic("Across rep avg waiting time");
        Statistic avgpw = new Statistic("Across rep prob of wait");
        Statistic wbar = new Statistic("Within rep avg waiting time");
        Statistic pw = new Statistic("Within rep prob of wait");
        Statistic xStat = new Statistic("Service Time");
        Statistic yStat = new Statistic("TBA Time");
        for (int i = 1; i <= r; i++) {
            double w = 0; // initial waiting time
            for (int j = 1; j <= n; j++) {
                double xv = x.getValue();
                double yv = y.getValue();
                w = Math.max(0.0, w + xv - yv);
                wbar.collect(w);// collect waiting time
                xStat.collect(xv);
                yStat.collect(yv);
                pw.collect((w > 0.0)); // collect P(W>0)
                if (j == d) {// clear stats at warmup
                    wbar.reset();
                    pw.reset();
                    xStat.reset();
                    yStat.reset();
                }
            }
            //collect across replication statistics
            avgw.collect(wbar.getAverage());
            avgpw.collect(pw.getAverage());
            writeCSV(out, wbar.getAverage(), xStat.getAverage() - x.getMean(), yStat.getAverage() - y.getMean());
            // clear within replication statistics for next rep
            wbar.reset();
            pw.reset();
            xStat.reset();
            yStat.reset();
        }
        System.out.println("Replication/Deletion Lindley Equation Example");
        System.out.println(avgw);
        System.out.println(avgpw);
    }
    
    private static void writeCSV(PrintWriter out, double y, double x1, double x2){
        out.print(y);
        out.print(",");
        out.print(x1);
        out.print(",");
        out.print(x2);
        out.println();
    }

    public static void oneLongRunExample() {
        // inter-arrival time distribution
        RandomIfc y = new Exponential(1.0);
        // service time distribution
        RandomIfc x = new Exponential(0.7);
        int n = 100000; // number of customers
        int d = 10000; // warm up
        AbstractStatistic wbar = new BatchStatistic("Batch waiting time");
        AbstractStatistic wbarSTS = new StandardizedTimeSeriesStatistic("STS waiting time");
        double w = 0; // initial waiting time
        for (int j = 1; j <= n; j++) {
            w = Math.max(0.0, w + x.getValue() - y.getValue());
            wbar.collect(w);// collect waiting time
            wbarSTS.collect(w);
            if (j == d) {// clear stats at warmup
                wbar.reset();
                wbarSTS.reset();
            }
        }
        System.out.println("One long Run Lindley Equation Example");
        System.out.println(wbar);
        System.out.println(wbarSTS);
    }
}
