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
package jsl.modeling.elements.variable;

import java.util.ArrayList;
import java.util.Collection;
import jsl.modeling.Experiment;
import jsl.modeling.ExperimentGetIfc;

import jsl.modeling.ModelElement;
import jsl.utilities.reporting.JSL;
import jsl.utilities.statistic.Statistic;
import jsl.utilities.statistic.StatisticAccessorIfc;

/**
 *
 */
public class Counter extends Aggregatable implements CounterActionIfc, DefaultReportingOptionIfc {

    /**
     * Can be used the reports to indicate whether or not the response should
     * appear The default is true
     *
     */
    private boolean myDefaultReportingOption = true;

    /**
     * An "enum" for the counter state.
     */
    public static final int COUNTER_LIMIT_REACHED = JSL.getNextEnumConstant();

    /**
     * The initial value of the counter
     *
     */
    protected long myInitialValue;

    /**
     * The current value of the counter
     */
    protected long myValue;

    /**
     * The previous value of the counter
     */
    protected long myPrevValue;

    /**
     * The time that the counter changed
     */
    protected double myTimeOfChange = 0.0;

    /**
     * The previous time that the counter changed
     */
    protected double myPrevTimeOfChange = 0.0;

    /**
     * Sets a limit for the counter to fire an action
     */
    protected long myLimit;

    /**
     * Indicates that the limit has not yet been reached
     */
    protected boolean myCountLimitFlag;

    /**
     * A collection containing the counter actions for the counter
     */
    protected Collection<CounterActionListenerIfc> myCounterActions;

    /**
     * Remembers the count at the previous timed update
     */
    protected long myCountAtPreviousTimedUpdate = 0;

    /**
     * Remembers the total during a timed update
     */
    protected long myTotalDuringTimedUpdate = 0;

    /**
     * Statistic used to collect the across replication statistics on the final
     * value of the counter for each replication
     */
    protected Statistic myAcrossRepStat;

    /**
     * A Counter can have a timed update action during a replication This
     * response collects the average of the total count during each timed
     * update interval. For example, a timed update interval of 1 hour can be
     * set. Each hour this response will observe the count for that hour and
     * collect averages across the timed update intervals
     */
    protected ResponseVariable myAcrossIntervalResponse;

    /**
     * Time of last update interval
     */
    protected double myLastUpdateTime;

    /**
     * The time of the warm up if it occurs, 0.0 otherwise
     */
    protected double myTimeOfWarmUp;

    /**
     * If used, represents a stopping action for the Counter Causes the Counter
     * to stop when its limit is reached
     */
    private StoppingAction myStoppingAction;

    /**
     * @param parent
     */
    public Counter(ModelElement parent) {
        this(parent, 0, Long.MAX_VALUE, null);
    }

    /**
     * @param parent
     * @param limit
     */
    public Counter(ModelElement parent, long limit) {
        this(parent, 0, limit, null);
    }

    /**
     *
     * @param parent
     * @param initialValue
     * @param limit
     */
    public Counter(ModelElement parent, long initialValue, long limit) {
        this(parent, initialValue, limit, null);
    }

    /**
     * @param parent
     * @param name
     */
    public Counter(ModelElement parent, String name) {
        this(parent, 0, Long.MAX_VALUE, name);
    }

    /**
     * @param parent
     * @param limit
     * @param name
     */
    public Counter(ModelElement parent, long limit, String name) {
        this(parent, 0, limit, name);
    }

    /**
     *
     * @param parent
     * @param initialValue
     * @param limit
     * @param name
     */
    public Counter(ModelElement parent, long initialValue, long limit, String name) {
        super(parent, name);
        setCounterLimit(limit);
        setInitialValue(initialValue);
        myCountLimitFlag = false;
        myCounterActions = new ArrayList<CounterActionListenerIfc>();
        myLastUpdateTime = 0.0;
        myTimeOfWarmUp = 0.0;
    }

    /**
     * Sets the counter limit for triggering counter actions This only sets the
     * limit for an action to occur. Any actions attached to the counter will
     * occur when this limit is reached.
     *
     * @param limit must be &gt;=0, zero implies no count limit
     */
    private void setCounterLimit(long limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("The counter's limit must be >= 0");
        }
        myLimit = limit;
    }

    /**
     * The limit used to trigger counter actions
     *
     * @return
     */
    public final long getCounterLimit() {
        return myLimit;
    }

    /**
     * Sets the count limit for determining when the count based stopping should
     * occur If countLimit is less than or equal to current count when set then
     * the action will occur the next time that the variable changes Sets a
     * limit and adds a stopping action. The executive will be stopped when the
     * limit is reached.
     *
     * @param countLimit must be &gt;=0, zero implies no count limit
     */
    public final void setCountBasedStopLimit(long countLimit) {
        if (countLimit < 0) {
            throw new IllegalArgumentException("Count Limit must be >= 0.");
        }
        setCounterLimit(countLimit);
        addStoppingAction();
    }

    /**
     * Sets the initial value of the counter. It might start at a value greater
     * than 1 prior to the simulation. This value will be used to initialize the
     * counter prior to each replication
     *
     * @param initialValue
     */
    protected final void setInitialValue(long initialValue) {
        if (initialValue < 0) {
            throw new IllegalArgumentException("The counter's initial value must be > 0");
        }
        if (initialValue >= myLimit) {
            throw new IllegalArgumentException("The counter's intial value must be < the supplied limit = " + myLimit);
        }
        myInitialValue = initialValue;
        myValue = myInitialValue;
        myTimeOfChange = 0.0;
        myPrevValue = myValue;
        myPrevTimeOfChange = myTimeOfChange;
    }

    /**
     * Returns the value used to initialize the counter prior to each
     * replication
     *
     * @return
     */
    public final long getInitialValue() {
        return myInitialValue;
    }

    /**
     * Increments the value of the variable by 1 at the current time.
     */
    public final void increment() {
        increment(1);
    }

    /**
     * Increments the value of the variable by the amount supplied. Throws an
     * IllegalArgumentException if the value is negative.
     *
     * @param value The amount to increment by. Must be non-negative.
     */
    public final void increment(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Invalid argument. Attempted an negative increment.");
        }
        setValue(myValue + value);
        notifyAggregatesOfValueChange();
        notifyUpdateObservers();
    }

    @Override
    public final double getValue() {
        return (myValue);
    }

    @Override
    public final double getPreviousValue() {
        return (myPrevValue);
    }

    /**
     * The time that it changed before the getValue() was set
     *
     * @return the PrevTimeOfChange
     */
    public double getPreviousTimeOfChange() {
        return myPrevTimeOfChange;
    }

    /**
     * The time that the counter was set to getValue()
     *
     * @return the TimeOfChange
     */
    public double getTimeOfChange() {
        return myTimeOfChange;
    }

    @Override
    public final long getCounterActionLimit() {
        return myLimit;
    }

    /**
     * Use to set the counter limit
     *
     * @param limit, must be &gt; 0
     */
    public final void setCounterActionLimit(long limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("The limit must be > 0");
        }

        myLimit = limit;
    }

    /**
     * The amount the counter changed during the timed update
     *
     * @return Returns the totalDuringTimedUpdate.
     */
    public final long getTotalDuringTimedUpdate() {
        return myTotalDuringTimedUpdate;
    }

    @Override
    public final boolean checkForCounterLimitReachedState() {
        return (this.checkObserverState(COUNTER_LIMIT_REACHED));
    }

    @Override
    public final void addStoppingAction() {
        if (myStoppingAction == null) {
            myStoppingAction = new StoppingAction();
            addCounterActionListener(myStoppingAction);
        }
    }

    @Override
    public final boolean addCounterActionListener(CounterActionListenerIfc action) {
        if (myCounterActions.contains(action)) {
            throw new IllegalArgumentException("The action has already been added to the counter's action list");
        }

        return myCounterActions.add(action);
    }

    @Override
    public final boolean removeCounterActionListener(CounterActionListenerIfc action) {
        return myCounterActions.remove(action);
    }

    /**
     * Resets the counter to the supplied value and clears the counter limit
     * flag If timed updates are on, then count since the last timed update will
     * be set to the supplied value. Notifies update observers of the state
     * change
     *
     * @param value, must be &lt; getCounterLimit() and &gt;=0
     */
    public final void resetCounter(long value) {
        resetCounter(value, true);
    }

    /**
     * Resets the counter to the supplied value and clears the counter limit
     * flag If timed updates are on, then count since the last timed update will
     * be set to the supplied value.
     *
     * @param value, must be &lt; getCounterLimit() and &gt;=0
     * @param notifyUpdateObservers If true, any update observers will be
     * notified otherwise they will not be notified
     */
    public final void resetCounter(long value, boolean notifyUpdateObservers) {
        if (value < 0) {
            throw new IllegalArgumentException("The counter's value must be >= 0");
        }
        if (value >= myLimit) {
            throw new IllegalArgumentException("The counter's value must be < the supplied limit = " + myLimit);
        }
        myPrevValue = value;
        myPrevTimeOfChange = getTime();

        myValue = value;
        myTimeOfChange = myPrevTimeOfChange;

        myCountLimitFlag = false;
        myCountAtPreviousTimedUpdate = value;

        if (notifyUpdateObservers) {
            notifyUpdateObservers();
        }
    }

    /**
     * Sets the default reporting option. True means the response will appear on
     * default reports
     *
     * @param flag
     */
    @Override
    public void setDefaultReportingOption(boolean flag) {
        myDefaultReportingOption = flag;
    }

    /**
     * Returns the default reporting option. True means that the response should
     * appear on the default reports
     *
     * @return
     */
    @Override
    public boolean getDefaultReportingOption() {
        return myDefaultReportingOption;
    }

    /**
     * Gets a copy of the statistics that have been accumulated across all
     * replications for this variable.
     *
     * @return A Statistic representing the across replication statistics.
     */
    public final StatisticAccessorIfc getAcrossReplicationStatistic() {
        if (myAcrossRepStat == null) {
            myAcrossRepStat = new Statistic("AcrossRepStat:" + getName());
        }
        //return (myAcrossRepStat.newInstance());
        return myAcrossRepStat;
    }

    /**
     * If the time interval collection is turned on a ResponseVariable is
     * created for capturing statistics across the intervals. This returns this
     * value or null if time interval collection has not been turned on
     *
     * @return the response or null
     */
    public final ResponseVariable getAcrossIntervalResponse() {
        return myAcrossIntervalResponse;
    }

    /**
     * Turns on the collection of statistics across intervals of time, defined
     * by the interval length
     *
     * @param interval
     */
    public final void turnOnTimeIntervalCollection(double interval) {
        setTimedUpdateInterval(interval);
        if (myAcrossIntervalResponse == null) {
            String s = String.format("_DT(%d)", (int) interval);
            myAcrossIntervalResponse = new ResponseVariable(this, getName() + s);
        }
    }

    /**
     * Turns on tracing to a file of the time interval response if and only if
     * time interval collection has been turned on
     *
     */
    public final void turnOnTimeIntervalTrace() {
        if (myAcrossIntervalResponse != null) {
            myAcrossIntervalResponse.turnOnTrace();
        }
    }

    /**
     * Turns on tracing to a file of the time interval response if and only if
     * time interval collection has been turned on
     *
     * @param header true means include the header
     */
    public final void turnOnTimeIntervalTrace(boolean header) {
        if (myAcrossIntervalResponse != null) {
            myAcrossIntervalResponse.turnOnTrace(header);
        }
    }

    /**
     * Turns on tracing to a file of the time interval response if and only if
     * time interval collection has been turned on
     *
     * @param fileName the name of the file to write the trace
     */
    public final void turnOnTimeIntervalTrace(String fileName) {
        if (myAcrossIntervalResponse != null) {
            myAcrossIntervalResponse.turnOnTrace(fileName);
        }
    }

    /**
     * Turns on tracing to a file of the time interval response if and only if
     * time interval collection has been turned on
     *
     * @param fileName the name of the file to write the trace
     * @param header true means include the header
     */
    public final void turnOnTimeIntervalTrace(String fileName, boolean header) {
        if (myAcrossIntervalResponse != null) {
            myAcrossIntervalResponse.turnOnTrace(fileName, header);
        }
    }

    /**
     * A convenience method to set the name of the underlying Statistic for
     * tabulating across replication statistics
     *
     * @param name
     */
    public final void setAcrossReplicationStatisticName(String name) {
        if (myAcrossRepStat == null) {
            myAcrossRepStat = new Statistic("AcrossRepStat:" + getName());
        }

        myAcrossRepStat.setName(name);
    }

    /**
     * Sets the value of the counter, ensures a check against the limit Does not
     * notify any update observers
     *
     * @param value The value to assign to the counter
     */
    protected void setValue(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Tried to set the counter to a value < 0");
        }

        // remember the old values
        myPrevValue = myValue;
        myPrevTimeOfChange = myTimeOfChange;

        // record the new value and time
        myValue = value;
        myTimeOfChange = getTime();

        if (myCountLimitFlag == false) {
            if (myValue >= myLimit) {
                myCountLimitFlag = true;
                notifyCounterLimitReachedObservers();
                notifyCounterActions();
            }
        }
    }

    @Override
    protected void beforeExperiment() {
        myLastUpdateTime = 0.0;
        myTimeOfWarmUp = 0.0;
        if (myAcrossRepStat != null) {
            myAcrossRepStat.reset();
        }
    }

    /**
     * Resets the counter and notifies any initialization observers
     *
     */
    @Override
    protected void initialize() {
        myLastUpdateTime = 0.0;
        myTimeOfWarmUp = 0.0;
        resetCounter(getInitialValue(), false);
        //if there are aggregates let them know about the initialization
        notifyAggregatesOfInitialization();
    }

    @Override
    protected void afterReplication() {

        ExperimentGetIfc e = getExperiment();
        if (e != null) {
            if (e.getNumberOfReplications() >= 1) {
                if (myAcrossRepStat == null) {
                    myAcrossRepStat = new Statistic("AcrossRepStat:" + getName());
                }
                myAcrossRepStat.collect(getValue());
            }
        }
    }

    /**
     * Initialize the value to zero at the warm up time and notify warm up
     * observers
     */
    @Override
    protected void warmUp() {
        myTimeOfWarmUp = getTime();
        resetCounter(0, false);
        //if there are aggregates let them know about the warmUp
        notifyAggregatesOfWarmUp();
    }

    @Override
    protected void timedUpdate() {
        myTotalDuringTimedUpdate = myValue - myCountAtPreviousTimedUpdate;
        myCountAtPreviousTimedUpdate = myValue;
        // record the count since last timed update

        if (myAcrossIntervalResponse != null) {
            if (!((myLastUpdateTime < myTimeOfWarmUp) && (myTimeOfWarmUp < getTime()))) {
                // if the warm up did not occur during the interval, then collect the total
                myAcrossIntervalResponse.setValue(myTotalDuringTimedUpdate);
            }
        }
        myLastUpdateTime = getTime();
    }

    /**
     * The method is used to notify observers that this model element has
     * triggered its counter limit
     */
    protected final void notifyCounterLimitReachedObservers() {
        notifyObservers(COUNTER_LIMIT_REACHED);
    }

    protected final void notifyCounterActions() {
        if (!myCounterActions.isEmpty()) {
            for (CounterActionListenerIfc a : myCounterActions) {
                a.action(this);
            }
        }
    }

    /**
     * The method is used to notify observers that this model element has been
     * updated.
     */
    @Override
    protected void notifyUpdateObservers() {
        super.notifyUpdateObservers();
    }

    @Override
    protected void removedFromModel() {
        super.removedFromModel();
        if (myCounterActions != null) {
            myCounterActions.clear();
            myCounterActions = null;
        }
        myAcrossRepStat = null;
        myStoppingAction = null;
        myAcrossIntervalResponse = null;
    }

    private class StoppingAction implements CounterActionListenerIfc {

        @Override
        public void action(Counter counter) {
            stopExecutive("Counter: " + counter.getName() + " reached limit:" + counter.getValue());
        }
    }
}
