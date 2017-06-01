/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resource;

import jsl.modeling.EventActionIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.SimulationReporter;
import jsl.modeling.elements.resource.Allocation;
import jsl.modeling.elements.resource.Entity;
import jsl.modeling.elements.resource.Resource;

/**
 *
 * @author rossetti
 */
public class ResourceTester extends SchedulingElement {

    private Resource myResource;

    private EventAction1 myAction1;
    
    private EventAction2 myAction2;
    
    public ResourceTester(ModelElement parent) {
        this(parent, null);
    }

    public ResourceTester(ModelElement parent, String name) {
        super(parent, name);

        myResource = new Resource(this, getName() + "_R");
        myAction1 = new EventAction1();
        myAction2 = new EventAction2();
        
    }

    @Override
    protected void initialize() {
        scheduleEvent(myAction1, 10.0);
    }

    class EventAction1 implements EventActionIfc {

        @Override
        public void action(JSLEvent event) {
            System.out.println(event);
            System.out.println("in action 1");
            Entity entity = createEntity();
            Allocation a = myResource.allocate(entity);
            scheduleEvent(myAction2, 3.0, a);
        }

    }

    class EventAction2 implements EventActionIfc {

        @Override
        public void action(JSLEvent evt) {
            System.out.println(evt);
            Allocation a = (Allocation)evt.getMessage();
            System.out.println(a);
            Resource r = a.getAllocatedResource();
            r.release(a);
            System.out.println(a);
            scheduleEvent(myAction1, 10.0);
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Resource Testing Example");

        Simulation s = new Simulation("Resource Testing");

        // create the model element and attach it to the main model
        new ResourceTester(s.getModel());

        // set the parameters of the experiment
        s.setNumberOfReplications(1);
        s.setLengthOfReplication(200.0);
        //s.setLengthOfWarmUp(5000.0);

        s.run();

        SimulationReporter r = s.makeSimulationReporter();
        r.writeAcrossReplicationSummaryStatistics();
        System.out.println("Done!");
    }

}
