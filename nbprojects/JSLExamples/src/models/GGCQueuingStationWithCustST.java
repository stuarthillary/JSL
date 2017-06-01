/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import jsl.modeling.JSLEvent;
import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.Simulation;
import jsl.modeling.SimulationReporter;
import jsl.modeling.elements.EventGenerator;
import jsl.modeling.elements.EventGeneratorListenerIfc;
import jsl.modeling.elements.queue.QObject;
import jsl.modeling.elements.station.ReceiveQObjectIfc;
import jsl.modeling.elements.station.SResource;
import jsl.modeling.elements.station.SingleQueueStation;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.distributions.Constant;
import jsl.utilities.random.distributions.Exponential;

/**
 *
 * @author rossetti
 */
public class GGCQueuingStationWithCustST extends ModelElement {

    private final EventGenerator myArrivalGenerator;
    private final SingleQueueStation mySQS;
    private ResponseVariable mySystemTime;
    private TimeWeighted myNumInSystem;
    private final SResource myServers;
    private final RandomVariable mySTRV;

    public GGCQueuingStationWithCustST(ModelElement parent, RandomIfc tba, RandomIfc st,
            int numServers) {
        this(parent, tba, st, numServers, null);
    }

    public GGCQueuingStationWithCustST(ModelElement parent, RandomIfc tba, RandomIfc st,
            int numServers, String name) {
        super(parent, name);
        myArrivalGenerator = new EventGenerator(this, new Arrivals(), tba, tba);
        myServers = new SResource(this, numServers, "Servers");
        mySTRV = new RandomVariable(this, st);
        mySQS = new SingleQueueStation(this, myServers, null, "Station");
        mySQS.setUseQObjectServiceTimeOption(true);
        mySQS.setNextReceiver(new Dispose());
        mySystemTime = new ResponseVariable(this, "System Time");
        myNumInSystem = new TimeWeighted(this, "Num in System");
    }

    private class Arrivals implements EventGeneratorListenerIfc {

        @Override
        public void generate(EventGenerator generator, JSLEvent event) {
            myNumInSystem.increment();
            QObject customer = new QObject(getTime());
            customer.setValueObject(mySTRV);
            mySQS.receive(customer);
        }

    }

    protected class Dispose implements ReceiveQObjectIfc {

        @Override
        public void receive(QObject qObj) {
            // collect final statistics
            myNumInSystem.decrement();
            mySystemTime.setValue(getTime() - qObj.getCreateTime());
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulation sim = new Simulation("M/M/2");
        // get the model
        Model m = sim.getModel();
        // add system to the main model
        Exponential tba = new Exponential(1);
        Exponential st = new Exponential(.8);
        int ns = 2;
        GGCQueuingStationWithCustST system = new GGCQueuingStationWithCustST(m, tba, st, ns);
        // set the parameters of the experiment
        sim.setNumberOfReplications(30);
 //       sim.setNumberOfReplications(2);
 //       sim.setLengthOfReplication(20.0);
        sim.setLengthOfReplication(20000.0);
        sim.setLengthOfWarmUp(5000.0);
        SimulationReporter r = sim.makeSimulationReporter();
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");
        r.writeAcrossReplicationSummaryStatistics();
    }

}
