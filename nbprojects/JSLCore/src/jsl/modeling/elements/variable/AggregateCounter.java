/**
 * 
 */
package jsl.modeling.elements.variable;

import jsl.modeling.ModelElement;
import jsl.utilities.statistic.StatisticAccessorIfc;

/**
 * @author rossetti
 *
 */
public class AggregateCounter extends Aggregate implements CounterActionIfc {

    /** This is used to remember the aggregate value
     *  when any of its aggregatable's change
     *
     */
    protected Counter myAggCounter;

    /**
     * @param parent
     */
    public AggregateCounter(ModelElement parent) {
        this(parent, null);
    }

    /**
     * @param parent
     * @param name
     */
    public AggregateCounter(ModelElement parent, String name) {
        super(parent, name);
        myAggCounter = new Counter(this, getName() + " : Aggregate");
        // the aggregate variable does not get initialized by the model
        // it must be initialized after all its observed variables are initialized
        myAggCounter.setInitializationOption(false);
        // the aggregate variable does not get warmed up by the model
        // it must be warmed up after all its observed counters are warmed up
        myAggCounter.setWarmUpOption(false);
    }

    /**
     * @return
     * @see jsl.modeling.elements.variable.Counter#getDefaultReportingOption()
     */
    public boolean getDefaultReportingOption() {
        return myAggCounter.getDefaultReportingOption();
    }

    /**
     * @param flag
     * @see jsl.modeling.elements.variable.Counter#setDefaultReportingOption(boolean)
     */
    public void setDefaultReportingOption(boolean flag) {
        myAggCounter.setDefaultReportingOption(flag);
    }

    /**
     * @return
     * @see jsl.modeling.elements.variable.PreviousValueIfc#getPreviousValue()
     */
    public double getPreviousValue() {
        return myAggCounter.getPreviousValue();
    }

    @Override
    public double getValue() {
        return myAggCounter.getValue();
    }

    /* (non-Javadoc)
     * @see jsl.modeling.elements.variable.Aggregate#valueChanged(jsl.modeling.elements.variable.Aggregatable)
     */
    @Override
    protected void valueChangedBeforeReplication(Aggregatable variable) {
        // a new variable has just been attached to the aggregate
        // either before or after the start of a replication
        // still need to make the variable have the correct value
        myAggCounter.setInitialValue((long) sumValues());
        myAggCounter.notifyUpdateObservers();
        notifyAggregatesOfValueChange();
    }

    /* (non-Javadoc)
     * @see jsl.modeling.elements.variable.Aggregate#variableAdded(jsl.modeling.elements.variable.Aggregatable)
     */
    @Override
    protected void variableAddedBeforeReplication(Aggregatable variable) {
        // a new variable has just been attached to the aggregate
        // either before or after the start of a replication
        // still need to make the variable have the correct value
        myAggCounter.setInitialValue((long) sumValues());
        myAggCounter.notifyUpdateObservers();
        notifyAggregatesOfValueChange();
    }

    /* (non-Javadoc)
     * @see jsl.modeling.elements.variable.Aggregate#variableRemoved(jsl.modeling.elements.variable.Aggregatable)
     */
    @Override
    protected void variableRemovedBeforeReplication(Aggregatable variable) {
        // a new variable has just been attached to the aggregate
        // either before or after the start of a replication
        // still need to make the variable have the correct value
        myAggCounter.setInitialValue((long) sumValues());
        myAggCounter.notifyUpdateObservers();
        notifyAggregatesOfValueChange();
    }

    /**
     * @param action
     * @return
     * @see jsl.modeling.elements.variable.CounterActionIfc#addCounterActionListener(jsl.modeling.elements.variable.CounterActionListenerIfc)
     */
    public boolean addCounterActionListener(CounterActionListenerIfc action) {
        return myAggCounter.addCounterActionListener(action);
    }

    /**
     *
     * @see jsl.modeling.elements.variable.CounterActionIfc#addStoppingAction()
     */
    public void addStoppingAction() {
        myAggCounter.addStoppingAction();
    }

    /**
     * @return
     * @see jsl.modeling.elements.variable.CounterActionIfc#checkForCounterLimitReachedState()
     */
    public boolean checkForCounterLimitReachedState() {
        return myAggCounter.checkForCounterLimitReachedState();
    }

    /**
     * @return
     * @see jsl.modeling.elements.variable.CounterActionIfc#getCounterActionLimit()
     */
    public long getCounterActionLimit() {
        return myAggCounter.getCounterActionLimit();
    }

    /**
     * @param action
     * @return
     * @see jsl.modeling.elements.variable.CounterActionIfc#removeCounterActionListener(jsl.modeling.elements.variable.CounterActionListenerIfc)
     */
    public boolean removeCounterActionListener(CounterActionListenerIfc action) {
        return myAggCounter.removeCounterActionListener(action);
    }

    /**
     * @return
     * @see jsl.modeling.elements.variable.AcrossReplicationStatisticIfc#getAcrossReplicationStatistic()
     */
    public StatisticAccessorIfc getAcrossReplicationStatistic() {
        return myAggCounter.getAcrossReplicationStatistic();
    }

    /**
     * @param name
     * @see jsl.modeling.elements.variable.Counter#setAcrossReplicationStatisticName(java.lang.String)
     */
    public void setAcrossReplicationStatisticName(String name) {
        myAggCounter.setAcrossReplicationStatisticName(name);
    }

    @Override
    protected void valueChangedDuringReplication(Aggregatable variable) {
        // get the amount incremented for changed counter
        double sum = variable.getValue() - variable.getPreviousValue();
        // now increment aggregate
        myAggCounter.increment((long) sum);
        notifyAggregatesOfValueChange();
    }

    @Override
    protected void variableAddedDuringReplication(Aggregatable variable) {
        // a new variable has just been attached to the aggregate
        // either before or after the start of a replication
        // still need to make the variable have the correct value
        myAggCounter.setValue((long) sumValues());
        myAggCounter.notifyUpdateObservers();
        notifyAggregatesOfValueChange();
    }

    @Override
    protected void variableRemovedDuringReplication(Aggregatable variable) {
        // a new variable has just been attached to the aggregate
        // either before or after the start of a replication
        // still need to make the variable have the correct value
        myAggCounter.setValue((long) sumValues());
        myAggCounter.notifyUpdateObservers();
        notifyAggregatesOfValueChange();
    }

    @Override
    protected void initializeAggregate() {
        myAggCounter.initialize_();
        myAggCounter.initialize();
    }

    @Override
    protected void warmUpAggregate() {
        myAggCounter.warmUp_();
    }

    protected void removedFromModel() {
        super.removedFromModel();
        myAggCounter = null;
    }
}
