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
package jsl.modeling.elements.station;

import jsl.modeling.EventActionIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.queue.QObject;
import jsl.modeling.elements.queue.Queue;
import jsl.modeling.elements.queue.QueueDiscipline;
import jsl.modeling.elements.queue.QueueListenerIfc;
import jsl.modeling.elements.variable.Aggregate;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.observers.ObserverIfc;
import jsl.utilities.GetValueIfc;
import jsl.utilities.random.distributions.Constant;
import jsl.utilities.statistic.StatisticAccessorIfc;
import jsl.utilities.statistic.WeightedStatisticIfc;

/**
 * Models a service station with a resource that has a single queue to hold
 * waiting customers. Customers can only use 1 unit of the resource while in
 * service.
 *
 * @author rossetti
 */
public class SingleQueueStation extends Station {

    private Queue myWaitingQ;

    private GetValueIfc myServiceTime;

    private TimeWeighted myNS;

    private SResource myResource;

    private EndServiceAction myEndServiceAction;

    private boolean myUseQObjectSTFlag;

    /**
     * Uses a resource with capacity 1 and service time Constant.ZERO
     *
     * @param parent
     */
    public SingleQueueStation(ModelElement parent) {
        this(parent, null, Constant.ZERO, null, null);
    }

    /**
     * Uses a resource with capacity 1
     *
     * @param parent
     * @param sd
     */
    public SingleQueueStation(ModelElement parent, GetValueIfc sd) {
        this(parent, null, sd, null, null);
    }

    /**
     * Uses a resource with capacity 1 and service time Constant.ZERO
     *
     * @param parent
     * @param name
     */
    public SingleQueueStation(ModelElement parent, String name) {
        this(parent, null, Constant.ZERO, null, name);
    }

    /**
     * Uses a resource with capacity 1
     *
     * @param parent
     * @param sd
     * @param name
     */
    public SingleQueueStation(ModelElement parent, GetValueIfc sd, String name) {
        this(parent, null, sd, null, name);
    }

        /**
     * No sender is provided.
     *
     * @param parent
     * @param resource
     */
    public SingleQueueStation(ModelElement parent, SResource resource) {
        this(parent, resource, null, null, null);
    }
    
    /**
     * No sender is provided.
     *
     * @param parent
     * @param resource
     * @param sd
     */
    public SingleQueueStation(ModelElement parent, SResource resource,
            GetValueIfc sd) {
        this(parent, resource, sd, null, null);
    }

    /**
     * No sender is provided.
     *
     * @param parent
     * @param resource
     * @param sd
     * @param name
     */
    public SingleQueueStation(ModelElement parent, SResource resource,
            GetValueIfc sd, String name) {
        this(parent, resource, sd, null, name);
    }

    /**
     * Default resource of capacity 1 is used
     *
     * @param parent
     * @param sd
     * @param sender
     * @param name
     */
    public SingleQueueStation(ModelElement parent, GetValueIfc sd,
            SendQObjectIfc sender, String name) {
        this(parent, null, sd, sender, name);
    }

    /**
     *
     * @param parent
     * @param resource
     * @param sd Represents the time using the resource
     * @param sender handles sending to next
     * @param name
     */
    public SingleQueueStation(ModelElement parent, SResource resource,
            GetValueIfc sd, SendQObjectIfc sender, String name) {
        super(parent, name);
        setSender(sender);
        if (resource == null) {
            myResource = new SResource(this, 1, getName() + ":R");
        } else {
            myResource = resource;
        }
        setServiceTime(sd);
        myWaitingQ = new Queue(this, getName() + ":Q");
        myNS = new TimeWeighted(this, 0.0, getName() + ":NS");
        myUseQObjectSTFlag = false;
        myEndServiceAction = new EndServiceAction();
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    protected double getServiceTime(QObject customer) {
        double t;
        if (getUseQObjectServiceTimeOption()) {
            GetValueIfc v = customer.getValueObject();
            t = v.getValue();
        } else {
            t = getServiceTime().getValue();
        }
        return t;
    }

    /**
     * Called to determine which waiting QObject will be served next Determines
     * the next customer, seizes the resource, and schedules the end of the
     * service.
     */
    protected void serveNext() {
        QObject customer = myWaitingQ.removeNext(); //remove the next customer
        myResource.seize();
        // schedule end of service
        scheduleEvent(myEndServiceAction, getServiceTime(customer), customer);
    }

    @Override
    public void receive(QObject customer) {
        myNS.increment(); // new customer arrived
        myWaitingQ.enqueue(customer); // enqueue the newly arriving customer
        if (isResourceAvailable()) { // server available
            serveNext();
        }
    }

    class EndServiceAction implements EventActionIfc {

        @Override
        public void action(JSLEvent event) {
            QObject leavingCustomer = (QObject) event.getMessage();
            myNS.decrement(); // customer departed
            myResource.release();
            if (isQueueNotEmpty()) { // queue is not empty
                serveNext();
            }
            send(leavingCustomer);
        }
    }

    /**
     * Tells the station to use the QObject to determine the service time
     *
     * @param option
     */
    public final void setUseQObjectServiceTimeOption(boolean option) {
        myUseQObjectSTFlag = option;
    }

    /**
     * Whether or not the station uses the QObject to determine the service time
     *
     * @return
     */
    public final boolean getUseQObjectServiceTimeOption() {
        return myUseQObjectSTFlag;
    }

    /**
     * The current number in the queue
     *
     * @return
     */
    public final int getNumberInQueue() {
        return myWaitingQ.size();
    }

    /**
     * The current number in the station (in queue + in service)
     *
     * @return
     */
    public final int getNumberInStation() {
        return (int) myNS.getValue();
    }

    /**
     * The initial capacity of the resource at the station
     *
     * @return
     */
    public final int getInitialResourceCapacity() {
        return (myResource.getInitialCapacity());
    }

    /**
     * Sets the initial capacity of the station's resource
     *
     * @param capacity
     */
    public final void setInitialCapacity(int capacity) {
        myResource.setInitialCapacity(capacity);
    }

    /**
     * If the service time is null, it is assumed to be zero
     *
     * @param st
     */
    public final void setServiceTime(GetValueIfc st) {
        if (st == null) {
            st = Constant.ZERO;
        }
        myServiceTime = st;
    }

    /**
     * The object used to determine the service time when not using the QObject
     * option
     *
     * @return
     */
    public final GetValueIfc getServiceTime() {
        return myServiceTime;
    }

    /**
     * Across replication statistics on the number in queue
     *
     * @return
     */
    public final StatisticAccessorIfc getNumInQAcrossReplicationStatistic() {
        return myWaitingQ.getNumInQAcrossReplicationStatistic();
    }

    /**
     * Across replication statistics on the time in queue
     *
     * @return
     */
    public final StatisticAccessorIfc getTimeInQAcrossReplicationStatistic() {
        return myWaitingQ.getTimeInQAcrossReplicationStatistic();
    }

    /**
     * Across replication statistics on the number busy servers
     *
     * @return
     */
    public final StatisticAccessorIfc getNBAcrossReplicationStatistic() {
        return myResource.getNBAcrossReplicationStatistic();
    }

    /**
     * Across replication statistics on the number in system
     *
     * @return
     */
    public final StatisticAccessorIfc getNSAcrossReplicationStatistic() {
        return myNS.getAcrossReplicationStatistic();
    }

    /**
     * Within replication statistics on the number in system
     *
     * @return
     */
    public final WeightedStatisticIfc getNSWithinReplicationStatistic() {
        return myNS.getWithinReplicationStatistic();
    }

    /**
     *
     * @return true if a resource has available units
     */
    public final boolean isResourceAvailable() {
        return myResource.hasAvailableUnits();
    }

    /**
     * The capacity of the resource. Maximum number of units that can be busy.
     *
     * @return
     */
    public final int getCapacity() {
        return myResource.getCapacity();
    }

    /**
     * Current number of busy servers
     *
     * @return
     */
    public final int getNumBusyServers() {
        return myResource.getNumBusy();
    }

    /**
     * Fraction of the capacity that is busy.
     *
     * @return
     */
    public final double getFractionBusy() {
        return getNumBusyServers() / getCapacity();
    }

    /**
     * Whether the queue is empty
     *
     * @return
     */
    public final boolean isQueueEmpty() {
        return myWaitingQ.isEmpty();
    }

    /**
     * Whether the queue is not empty
     *
     * @return
     */
    public final boolean isQueueNotEmpty() {
        return myWaitingQ.isNotEmpty();
    }

    /**
     * Turns on the collection of statistics on the maximum time spent in queue
     *
     */
    public final void turnOnAcrossReplicationMaxTimeInQueueCollection() {
        myWaitingQ.turnOnAcrossReplicationMaxTimeInQueueCollection();
    }

    /**
     * Turns on the collection of statistics on the maximum number in queue
     *
     */
    public final void turnOnAcrossReplicationMaxNumInQueueCollection() {
        myWaitingQ.turnOnAcrossReplicationMaxNumInQueueCollection();
    }

    /**
     * Turns on the collection of statistics on the maximum time and number in
     * queue
     */
    public final void turnOnAcrossReplicationMaxCollection() {
        myWaitingQ.turnOnAcrossReplicationMaxCollection();
    }

    /**
     * Adds a QueueListenerIfc to the underlying queue
     *
     * @param listener
     * @return
     */
    public final boolean addQueueListener(QueueListenerIfc listener) {
        return myWaitingQ.addQueueListener(listener);
    }

    /**
     * Removes a QueueListenerIfc from the underlying queue
     *
     * @param listener
     * @return
     */
    public boolean removeQueueListener(QueueListenerIfc listener) {
        return myWaitingQ.removeQueueListener(listener);
    }

    /**
     * Add an observer to the underlying time in queue
     *
     * @param observer
     */
    public final void addTimeInQueueObserver(ObserverIfc observer) {
        myWaitingQ.addTimeInQueueObserver(observer);
    }

    /**
     * Removes an observer from the underlying time in queue
     *
     * @param observer
     */
    public final void removeTimeInQueueObserver(ObserverIfc observer) {
        myWaitingQ.removeTimeInQueueObserver(observer);
    }

    /**
     * Adds an observer from the underlying number in queue
     *
     * @param observer
     */
    public final void addNumberInQueueObserver(ObserverIfc observer) {
        myWaitingQ.addNumberInQueueObserver(observer);
    }

    /**
     * Removes an observer from the underlying number in queue
     *
     * @param observer
     */
    public final void removeNumberInQueueObserver(ObserverIfc observer) {
        myWaitingQ.removeNumberInQueueObserver(observer);
    }

    /** Subscribes and aggregate
     * 
     * @param aggregate 
     */
    public final void subscribeToTimeInQueue(Aggregate aggregate) {
        myWaitingQ.subscribeToTimeInQueue(aggregate);
    }

    /** Unsubscribes an aggregate
     * 
     * @param aggregate 
     */
    public final void unsubscribeFromTimeInQueue(Aggregate aggregate) {
        myWaitingQ.unsubscribeFromTimeInQueue(aggregate);
    }

    public final void subscribeToNumberInQueue(Aggregate aggregate) {
        myWaitingQ.subscribeToNumberInQueue(aggregate);
    }

    public final void unsubscribeFromNumberInQueue(Aggregate aggregate) {
        myWaitingQ.unsubscribeFromNumberInQueue(aggregate);
    }

    public final void changeDiscipline(QueueDiscipline discipline) {
        myWaitingQ.changeDiscipline(discipline);
    }

    public final QueueDiscipline getInitialDiscipline() {
        return myWaitingQ.getInitialDiscipline();
    }

    public final void setInitialDiscipline(QueueDiscipline discipline) {
        myWaitingQ.setInitialDiscipline(discipline);
    }

    public final void turnOnTimeInQTrace() {
        myWaitingQ.turnOnTimeInQTrace();
    }

    public final void turnOnTimeInQTrace(boolean header) {
        myWaitingQ.turnOnTimeInQTrace(header);
    }

    public final void turnOnTimeInQTrace(String fileName) {
        myWaitingQ.turnOnTimeInQTrace(fileName);
    }

    public final void turnOnTimeInQTrace(String fileName, boolean header) {
        myWaitingQ.turnOnTimeInQTrace(fileName, header);
    }

    public final void turnOnNumberInQTrace() {
        myWaitingQ.turnOnNumberInQTrace();
    }

    public final void turnOnNumberInQTrace(boolean header) {
        myWaitingQ.turnOnNumberInQTrace(header);
    }

    public final void turnOnNumberInQTrace(String fileName) {
        myWaitingQ.turnOnNumberInQTrace(fileName);
    }

    public final void turnOnNumberInQTrace(String fileName, boolean header) {
        myWaitingQ.turnOnNumberInQTrace(fileName, header);
    }

    public final void turnOffTimeInQTrace() {
        myWaitingQ.turnOffTimeInQTrace();
    }

    public final void turnOffNumberInQTrace() {
        myWaitingQ.turnOffNumberInQTrace();
    }
}
