package jsl.modeling.elements;

import jsl.utilities.random.RandomIfc;

public interface EventGeneratorIfc {

    /**
     * If the generator was not started upon initialization at the beginning of
     * a replication, then this method can be used to start the generator
     *
     * The generator will be started t time units after the call
     *
     * If this method is used when the generator is already started it does
     * nothing. If this method is used after the generator is done it does
     * nothing. If this method is used after the generator has been suspended it
     * does nothing. Use suspend() and resume() to suspend and resume a
     * generator that has already been started.
     *
     * @param t The time until the generator should be turned on
     */
    public void turnOnGenerator(double t);

    /**
     * If the generator was not started upon initialization at the beginning of
     * a replication, then this method can be used to start the generator
     *
     * The generator will be started r.getValue() time units after the call
     *
     * If this method is used when the generator is already started it does
     * nothing. If this method is used after the generator is done it does
     * nothing. If this method is used after the generator has been suspended it
     * does nothing. Use suspend() and resume() to suspend and resume a
     * generator that has already been started.
     *
     * @param r The time until the generator should be turned on
     */
    public void turnOnGenerator(RandomIfc r);

    /**
     * If the generator was not started upon initialization at the beginning of
     * a replication, then this method can be used to start the generator
     *
     * The generator will be started 0.0 time units after the call
     *
     * If this method is used when the generator is already started it does
     * nothing. If this method is used after the generator is done it does
     * nothing. If this method is used after the generator has been suspended it
     * does nothing. Use suspend() and resume() to suspend and resume a
     * generator that has already been started.
     *
     */
    public void turnOnGenerator();

    /**
     * This method turns the generator off, the next scheduled generation event
     * will NOT occur, i.e. this method will also cancel a previously scheduled
     * generation event if one exists. No future events will be scheduled after
     * turning off the generator
     */
    public void turnOffGenerator();

    /**
     * This flag indicates whether or not the generator will automatically start
     * at the beginning of a replication when initialized. By default this
     * option is true.
     *
     * @return true if on
     */
    public boolean getStartOnInitializeFlag();

    /**
     * Sets the flag that indicates whether or not the generator will
     * automatically start at the beginning of a replication when initialized
     *
     * @param flag  true indicates automatic start
     */
    public void setStartOnInitializeFlag(boolean flag);

    /**
     * Suspends the generation of events and cancels the next scheduled event
     * from the generator
     */
    public void suspend();

    /**
     * Indicates whether or not the generator has been suspended
     *
     * @return true if generator is suspended
     */
    public boolean isSuspended();

    /**
     * Resume the generation of events according to the time between event
     * distribution.
     */
    public void resume();

    /**
     * This method checks to see if the generator is done. In other words, if it
     * has been turned off.
     *
     * @return True means that it is done.
     */
    public boolean isGeneratorDone();

    /**
     * Gets the maximum number of actions for the generator. This is set by the
     * supplied maxNum upon creation of the generator. This implies that it will
     * be the same for every simulation replication.
     *
     * @return A long representing the maximum number of actions for the
     * generator.
     */
    public long getMaximumNumberOfEvents();

    /**
     * Sets the time between event random source. Must not always evaluate to
     * 0.0, if the current setting of the maximum number of events is infinite
     * (Long.MAX_VALUE)
     *
     * @param timeUntilNext time until the next event
     */
    public void setTimeBetweenEvents(RandomIfc timeUntilNext);

    /**
     * Gets the random source controlling the time between events
     *
     * @return the random source controlling the time between events
     */
    public RandomIfc getTimeBetweenEvents();

    /**
     * Sets the maximum number of events for the generator. Must not be infinite
     * (Long.MAX_VALUE) if the current time between events is 0.0
     *
     * @param maxNum maximum number of events
     */
    public void setMaximumNumberOfEvents(long maxNum);

    /**
     * Sets the time between events and the maximum number of events for the
     * generator. These two parameters are dependent. The time between events
     * cannot always evaluate to 0.0 if the maximum number of events is infinite
     * (Long.MAX_VALUE). This method only changes these parameters for the
     * current replication. The changes take effect when the next event is
     * generated. If current number of events that have been generated is
     * greater than or equal to the supplied maximum number of events, the
     * generator will be turned off.
     *
     * @param timeBtwEvents the time between events
     * @param maxNumEvents the maximum number of events
     */
    public void setTimeBetweenEvents(RandomIfc timeBtwEvents, long maxNumEvents);

    /**
     * Sets the time between events and the maximum number of events to be used
     * to initialize each replication. These parameters are dependent. The time
     * between events cannot evaluate to a constant value of 0.0 if the maximum
     * number of events is infinite (Long.MAX_VALUE)
     *
     * @param timeBtwEvents time between events
     * @param maxNumEvents maximum number of events
     */
    public void setTimeBetweenEventsForReplications(RandomIfc timeBtwEvents,
            long maxNumEvents);

    /**
     * Sets the RandomIfc representing the time until the first event that is
     * used at the beginning of each replication to generate the time until the
     * first event. This change becomes effective at the beginning of the next
     * replication to execute
     *
     * @param timeUntilFirst, The supplied RandomIfc, cannot be null
     */
    public void setTimeUntilFirstEventForReplications(RandomIfc timeUntilFirst);

    /**
     * Gets the RandomIfc that will be used at the beginning of each replication
     * to generate the time until the first event
     *
     * @return RandomIfc that will be used at the beginning of each replication
     * to generate the time until the first event
     */
    public RandomIfc getTimeUntilFirstEventForReplications();

    /**
     * Sets the ending time for generating events for the current replication. A
     * new ending time will be applied to the generator. If this change results
     * in an ending time that is less than the current time, the generator will
     * be turned off
     *
     * @param endingTime the ending time for generating events
     */
    public void setEndingTime(double endingTime);

    /**
     * Gets the currently planned ending time of the generator.
     *
     * @return A double representing the ending time.
     */
    public double getEndingTime();

    /**
     * This value is used to set the ending time for generating actions for each
     * replication. Changing this variable during a replication cause the next
     * replication to use this value for its ending time.
     *
     * @param endingTime value is used to set the ending time for generating actions
     */
    public void setGenerationEndingTimeForReplications(double endingTime);

    /**
     * Returns the ending time that is to be used when the generator is
     * initialized for each replication.
     *
     * @return Returns the ending time that is to be used when the generator
     */
    public double getGenerationEndingTimeForReplications();

    /**
     * Gets the number of events that have been generated by the generator
     *
     * @return the number of events that have been generated
     */
    public long getNumberOfEventsGenerated();

    /**
     * Returns the time between events used to initialize each replication
     *
     * @return the time between events 
     */
    public RandomIfc getTimeBetweenEventsForReplications();

    /**
     * Returns the maximum number of events that is used for initializing each
     * replication
     *
     * @return the maximum number of events 
     */
    public long getMaximumNumberOfEventsForReplications();

    /**
     * Sets the time between events and the maximum number of events to be used
     * to initialize each replication. The time between events cannot evaluate
     * to a constant value of 0.0. The maximum number of events is kept at its
     * current value
     *
     * @param timeBtwEvents the time between events 
     */
    public void setTimeBetweenEventsForReplications(RandomIfc timeBtwEvents);

    /**
     * Sets the the maximum number of events to be used to initialize each
     * replication. The time between events cannot evaluate to a constant value
     * of 0.0 if the maximum number of events is infinite (Long.MAX_VALUE). Uses
     * the current value for initial time between events
     *
     * @param maxNumEvents the the maximum number of events
     */
    public void setMaximumNumberOfEventsForReplications(long maxNumEvents);

}
