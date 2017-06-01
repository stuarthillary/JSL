package handleevent;

import jsl.modeling.*;
import jsl.modeling.elements.queue.*;
import jsl.modeling.elements.variable.*;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.SimulationReporter;
import jsl.utilities.statistic.WeightedStatisticIfc;

public class TwoQueuesinSeriesHE extends SchedulingElement {

    public static final int ARRIVAL = 0;

    public static final int SERVICE1END = 1;

    public static final int SERVICE2END = 2;

    Queue myQueue1;

    Queue myQueue2;

    RandomVariable myTBA;

    RandomVariable myServiceTime1;

    RandomVariable myServiceTime2;

    TimeWeighted myServer1State;

    TimeWeighted myServer2State;

    ResponseVariable mySystemTime;
    
    ResponseVariable myTotalQTime;
    
    ResponseVariable myMaxSysTime;
    
    AggregateTimeWeightedVariable myTotNumInQ;
    
    AveragePerTimeWeightedVariable myAvgPTWNInQ;
    
    TimeWeighted myTNInQ;
    
    ResponseVariableAverageObserver myAvgTimeInQ;

    public TwoQueuesinSeriesHE(ModelElement parent) {
        super(parent);
        myQueue1 = new Queue(this, getName() + " Queue1");
        myQueue2 = new Queue(this, getName() + " Queue2");
        myTBA = new RandomVariable(this, new Exponential(1));
        myServiceTime1 = new RandomVariable(this, new Exponential(0.7));
        myServiceTime2 = new RandomVariable(this, new Exponential(0.9));
        myServer1State = new TimeWeighted(this, 0.0, " Server1 Utilization");
        myServer2State = new TimeWeighted(this, 0.0, " Server2 Utilization");
        mySystemTime = new ResponseVariable(this, " System Time");
        myTotalQTime = new ResponseVariable(this, " Total Q Time");
        myMaxSysTime = new ResponseVariable(this, " Max Sys Time");
        
        myTotNumInQ = new AggregateTimeWeightedVariable(this, getName() + " : Total In Q");
        myTNInQ = new TimeWeighted(this, getName() + " : Tot N in Q");
        
        myAvgTimeInQ = new ResponseVariableAverageObserver(this, getName() + " : Avg Time in Qs");
        myAvgPTWNInQ = new AveragePerTimeWeightedVariable(this, getName() + " Avg Per N in Q");
        
        myQueue1.subscribeToNumberInQueue(myTotNumInQ);
        myQueue2.subscribeToNumberInQueue(myTotNumInQ);
        
        myQueue1.subscribeToNumberInQueue(myAvgPTWNInQ);
        myQueue2.subscribeToNumberInQueue(myAvgPTWNInQ);
        
        myQueue1.subscribeToTimeInQueue(myAvgTimeInQ);
        myQueue2.subscribeToTimeInQueue(myAvgTimeInQ);
        
        myQueue1.turnOnNumberInQTrace("Q1NinQTrace");
        myQueue2.turnOnNumberInQTrace("Q2NinQTrace");
        myTNInQ.turnOnTrace("TNInQTrace");
        myTotNumInQ.turnOnTrace("TotNumInQ");
    }

    @Override
    protected void initialize() {
        super.initialize();
        scheduleEvent(myTBA.getValue(), ARRIVAL);
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
            case SERVICE2END:
                departQ2(event);
        }
    }

    private void arriveToQ1(JSLEvent event) {
        QObject arrival = createQObject();
        myQueue1.enqueue(arrival);
        myTNInQ.increment();
        if (myServer1State.getValue() == 0.0) {
            myServer1State.setValue(1.0);
            QObject nc = myQueue1.removeNext();
            myTNInQ.decrement();
            scheduleEvent(myServiceTime1.getValue(), SERVICE1END, nc);
        }
        scheduleEvent(myTBA.getValue(), ARRIVAL);
    }

    private void departQ1(JSLEvent event) {
        myServer1State.setValue(0.0);
        if (!myQueue1.isEmpty()) {
            myServer1State.setValue(1.0);
            QObject nc = myQueue1.removeNext();
            myTNInQ.decrement();
            scheduleEvent(myServiceTime1.getValue(), SERVICE1END, nc);
        }

        QObject customer = (QObject) event.getMessage();
        myQueue2.enqueue(customer);
        myTNInQ.increment();
        if (myServer2State.getValue() == 0.0) {
            myServer2State.setValue(1.0);
            QObject nc = myQueue2.removeNext();
            myTNInQ.decrement();
            scheduleEvent(myServiceTime2.getValue(), SERVICE2END, nc);
        }
    }

    private void departQ2(JSLEvent event) {
        QObject dc = (QObject) event.getMessage();
        
        StateAccessorIfc state = dc.getQueuedState();
        double t = state.getTotalTimeInState();
        myTotalQTime.setValue(t);
        
        mySystemTime.setValue(getTime() - dc.getCreateTime());
        myServer2State.setValue(0.0);
        if (!myQueue2.isEmpty()) {
            myServer2State.setValue(1.0);
            QObject nc = myQueue2.removeNext();
            myTNInQ.decrement();
            scheduleEvent(myServiceTime2.getValue(), SERVICE2END, nc);
        }
    }

    protected void replicationEnded(){
        WeightedStatisticIfc x = mySystemTime.getWithinReplicationStatistic();
        double max = x.getMax();
        myMaxSysTime.setValue(max);
    }
    
    public static void main(String[] args) {
        Simulation sim = new Simulation("TwoQsInSeriesExample");
        
        new TwoQueuesinSeriesHE(sim.getModel());

        sim.setNumberOfReplications(1);
        //sim.setLengthOfWarmUp(50.0);
        sim.setLengthOfReplication(25.0);

        SimulationReporter r = sim.makeSimulationReporter();
        r.turnOnReplicationCSVStatisticReporting();
        r.turnOnAcrossReplicationCSVStatisticReporting();
        
        // tell the experiment to run
        sim.run();
        

        r.writeAcrossReplicationSummaryStatistics();

        System.out.println("Done!");
    }
}
