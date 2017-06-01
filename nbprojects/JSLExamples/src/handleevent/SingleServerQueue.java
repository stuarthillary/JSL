package handleevent;

import jsl.modeling.SchedulingElement;
import jsl.modeling.Experiment;
import jsl.modeling.JSLEvent;
import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.Simulation;
import jsl.modeling.elements.queue.*;
import jsl.modeling.elements.variable.*;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.SimulationReporter;
import jsl.utilities.welch.WelchDataCollector;
import jsl.utilities.welch.WelchDataCollectorTW;

public class SingleServerQueue extends SchedulingElement {

    public static final int ARRIVAL = 0;

    public static final int SERVICE1END = 1;

    Queue myQueue1;

    RandomVariable myTBA;

    RandomVariable myServiceTime1;

    TimeWeighted myServer1State;

    ResponseVariable mySystemTime;

    public SingleServerQueue(ModelElement parent) {
        super(parent);
        myQueue1 = new Queue(this, getName() + " Queue1");
        myTBA = new RandomVariable(this, new Exponential(1));
        myServiceTime1 = new RandomVariable(this, new Exponential(0.7));
        myServer1State = new TimeWeighted(this, 0.0, " Server Utilization");
        mySystemTime = new ResponseVariable(this, "System Time");
        // collect welch data on queue
        WelchDataCollectorTW wdctw = new WelchDataCollectorTW(10.0, 2000, 5);
        WelchDataCollector wdc = new WelchDataCollector(2000, 5);
        
        mySystemTime.addObserver(wdc);
        myQueue1.addNumberInQueueObserver(wdctw);
    }

    @Override
    protected void initialize() {
        super.initialize();
        double t = myTBA.getValue();
        scheduleEvent(t, ARRIVAL);
    }

    @Override
    protected void handleEvent(JSLEvent event) {
        switch (event.getType()) {
            case ARRIVAL:
                arriveToQ1(event);
                break;
            case SERVICE1END:
                departQ1(event);
                break;
            default:
                System.out.println("Invalid event type");

        }
    }

    private void arriveToQ1(JSLEvent event) {
        QObject arrival = createQObject();
        myQueue1.enqueue(arrival);
        if (myServer1State.getValue() == 0.0) {
            myServer1State.setValue(1.0);
            QObject nc = myQueue1.removeNext();
            scheduleEvent(myServiceTime1, SERVICE1END, nc);
        }
        scheduleEvent(myTBA, ARRIVAL);
    }

    private void departQ1(JSLEvent event) {
        myServer1State.setValue(0.0);
        if (!myQueue1.isEmpty()) {
            myServer1State.setValue(1.0);
            QObject nc = myQueue1.removeNext();
            scheduleEvent(myServiceTime1, SERVICE1END, nc);
        }

        QObject customer = (QObject) event.getMessage();
        mySystemTime.setValue(getTime() - customer.getCreateTime());
    }

    public static void main(String[] args) {
        Simulation sim = new Simulation("Single Server Q via HE");

        new SingleServerQueue(sim.getModel());

        sim.setNumberOfReplications(10);
        sim.setLengthOfReplication(10000.0);

        // tell the experiment to run
        sim.run();

        SimulationReporter r = sim.makeSimulationReporter();

        r.writeAcrossReplicationSummaryStatistics();

        System.out.println("Done!");
    }
}
