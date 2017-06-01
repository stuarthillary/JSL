/*
 * Created on Sep 17, 2007
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
package jsl.modeling.elements.variable.nhpp;

import jsl.modeling.ModelElement;
import jsl.modeling.elements.EventGenerator;
import jsl.modeling.elements.EventGeneratorIfc;
import jsl.modeling.elements.EventGeneratorListenerIfc;
import jsl.utilities.random.RandomIfc;

/**
 * @author rossetti
 *
 */
public class NHPPEventGenerator extends ModelElement implements EventGeneratorIfc {

    protected EventGenerator myEventGenerator;

    protected NHPPTimeBtwEventRV myTBARV;

    /**
     * @param parent the parent
     * @param rateFunction the rate function
     * @param listener   the listener for generation
     */
    public NHPPEventGenerator(ModelElement parent, InvertibleCumulativeRateFunctionIfc rateFunction,
            EventGeneratorListenerIfc listener) {
        this(parent, rateFunction, listener, null);
    }

    /**
     * @param parent the parent
     * @param rateFunction the rate function
     * @param listener   the listener for generation
     * @param name the name to assign
     */
    public NHPPEventGenerator(ModelElement parent, InvertibleCumulativeRateFunctionIfc rateFunction,
            EventGeneratorListenerIfc listener, String name) {
        super(parent, name);
        myTBARV = new NHPPTimeBtwEventRV(this, rateFunction);
        myEventGenerator = new EventGenerator(this, listener, myTBARV, myTBARV);
    }

    /**
     * @param parent the parent
     * @param rateFunction the rate function
     * @param listener   the listener for generation
     * @param lastrate  the last rate
     * @param name the name to assign
     */
    public NHPPEventGenerator(ModelElement parent, InvertibleCumulativeRateFunctionIfc rateFunction,
            EventGeneratorListenerIfc listener, double lastrate, String name) {
        super(parent, name);
        myTBARV = new NHPPTimeBtwEventRV(this, rateFunction, lastrate);
        myEventGenerator = new EventGenerator(this, listener, myTBARV, myTBARV);
    }

    @Override
    public double getEndingTime() {
        return myEventGenerator.getEndingTime();
    }


    @Override
    public double getGenerationEndingTimeForReplications() {
        return myEventGenerator.getGenerationEndingTimeForReplications();
    }

    @Override
    public long getMaximumNumberOfEvents() {
        return myEventGenerator.getMaximumNumberOfEvents();
    }

    @Override
    public long getMaximumNumberOfEventsForReplications() {
        return myEventGenerator.getMaximumNumberOfEventsForReplications();
    }

    @Override
    public long getNumberOfEventsGenerated() {
        return myEventGenerator.getNumberOfEventsGenerated();
    }

    @Override
    public RandomIfc getTimeBetweenEventsForReplications() {
        return myEventGenerator.getTimeBetweenEventsForReplications();
    }

    @Override
    public RandomIfc getTimeUntilFirstEventForReplications() {
        return myEventGenerator.getTimeUntilFirstEventForReplications();
    }

    @Override
    public boolean isGeneratorDone() {
        return myEventGenerator.isGeneratorDone();
    }

    @Override
    public boolean isSuspended() {
        return myEventGenerator.isSuspended();
    }

    @Override
    public void resume() {
        myEventGenerator.resume();
    }

    @Override
    public void setEndingTime(double endingTime) {
        myEventGenerator.setEndingTime(endingTime);
    }

    @Override
    public void setGenerationEndingTimeForReplications(double endingTime) {
        myEventGenerator.setGenerationEndingTimeForReplications(endingTime);
    }

    @Override
    public void setMaximumNumberOfEvents(long maxNum) {
        myEventGenerator.setMaximumNumberOfEvents(maxNum);
    }

    @Override
    public void setMaximumNumberOfEventsForReplications(long maxNumEvents) {
        myEventGenerator.setMaximumNumberOfEventsForReplications(maxNumEvents);
    }

    @Override
    public void setTimeBetweenEvents(RandomIfc timeBtwEvents, long maxNumEvents) {
        myEventGenerator.setTimeBetweenEvents(timeBtwEvents, maxNumEvents);
    }

    @Override
    public void setTimeBetweenEvents(RandomIfc timeUntilNext) {
        myEventGenerator.setTimeBetweenEvents(timeUntilNext);
    }

    @Override
    public final RandomIfc getTimeBetweenEvents() {
        return myEventGenerator.getTimeBetweenEvents();
    }

    @Override
    public void setTimeBetweenEventsForReplications(RandomIfc timeBtwEvents, long maxNumEvents) {
        myEventGenerator.setTimeBetweenEventsForReplications(timeBtwEvents, maxNumEvents);
    }

    @Override
    public void setTimeBetweenEventsForReplications(RandomIfc timeBtwEvents) {
        myEventGenerator.setTimeBetweenEventsForReplications(timeBtwEvents);
    }

    @Override
    public void setTimeUntilFirstEventForReplications(RandomIfc timeUntilFirst) {
        myEventGenerator.setTimeUntilFirstEventForReplications(timeUntilFirst);
    }

    @Override
    public void suspend() {
        myEventGenerator.suspend();
    }

    @Override
    public final boolean getStartOnInitializeFlag() {
        return myEventGenerator.getStartOnInitializeFlag();
    }

    @Override
    public final void setStartOnInitializeFlag(boolean flag) {
        myEventGenerator.setStartOnInitializeFlag(flag);
    }

    @Override
    public final void turnOnGenerator() {
        myEventGenerator.turnOnGenerator();
    }

    @Override
    public final void turnOnGenerator(double t) {
        myEventGenerator.turnOnGenerator(t);
    }

    @Override
    public void turnOnGenerator(RandomIfc r) {
        myEventGenerator.turnOnGenerator(r);
    }

    @Override
    public void turnOffGenerator() {
        myEventGenerator.turnOffGenerator();
    }
}
