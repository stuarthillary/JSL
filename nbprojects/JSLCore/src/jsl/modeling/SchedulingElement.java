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
package jsl.modeling;

import jsl.utilities.GetValueIfc;

/** A SchedulingElement is a ModelElement that facilitates the scheduling of
 *  events. Every SchedulingElement has a built in event action which will
 *  call the handleEvent() method.  A subclass can override the handleEvent()
 *  method to provide event routine logic.
 *  
 *  Alternatively (and more flexibly) events can be scheduled with their own 
 *  action provided by implementing the EventActionIfc.  The class
 *  that implements the EventActionIfc can be provided when scheduling the
 *  event to ensure that the proper event routine is called when the event is 
 *  executed by the event scheduler.
 *
 */
public class SchedulingElement extends ModelElement {

    /** A reference to an instance of an inner class that implements
     *  the EventActionIfc for handling the default calling of handleEvent()
     *  if used by subclasses.
     */
    private EventAction myHandleEventAction;

    /**
     * @param parent
     */
    public SchedulingElement(ModelElement parent) {
        this(parent, null);
    }

    /**
     * @param parent
     * @param name
     */
    public SchedulingElement(ModelElement parent, String name) {
        super(parent, name);

    }

    /** Tells the scheduler to cancel the provided event.
     * @param e A reference to the event to be canceled.
     */
    protected final void cancelEvent(JSLEvent e) {
        getExecutive().cancel(e);
    }

    /** This method allows a previously *executed* event to be reused
     * The event must have already been removed from the calendar through the
     * natural execute event mechanism and have been executed.
     * The user can reset the action, priority, and message as required
     * directly on the event prior to rescheduling.
     *
     * @param event The event that needs rescheduling
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     */
    protected final void rescheduleEvent(JSLEvent event, double time) {
        event.setModelElement(this);
        getExecutive().reschedule(event, time);
    }

    /** This method allows a previously *executed* event to be reused
     * The event must have already been removed from the calendar through the
     * natural execute event mechanism and have been executed.
     * The user can reset the action, priority, and message as required
     * directly on the event prior to rescheduling.
     *
     * @param event The event that needs rescheduling
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param message an Object to attach to the event
     */
    protected final void rescheduleEvent(JSLEvent event, double time, Object message) {
        event.setMessage(message);
        event.setModelElement(this);
        getExecutive().reschedule(event, time);
    }

    /** This method allows a previously *executed* event to be reused
     * The event must have already been removed from the calendar through the
     * natural execute event mechanism and have been executed.
     * The user can reset the action, priority, and message as required
     * directly on the event prior to rescheduling.
     *
     * @param event The event that needs rescheduling
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     */
    protected final void rescheduleEvent(JSLEvent event, GetValueIfc time) {
        event.setModelElement(this);
        getExecutive().reschedule(event, time.getValue());
    }

    /** This method allows a previously *executed* event to be reused
     * The event must have already been removed from the calendar through the
     * natural execute event mechanism and have been executed.
     * The user can reset the action, priority, and message as required
     * directly on the event prior to rescheduling.
     *
     * @param event The event that needs rescheduling
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param message an Object to attach to the event
     */
    protected final void rescheduleEvent(JSLEvent event, GetValueIfc time, Object message) {
        event.setMessage(message);
        event.setModelElement(this);
        getExecutive().reschedule(event, time.getValue());
    }

    /** Creates an event and schedules it onto the event calendar
     * Uses this scheduling element's default event action  which calls the
     * method handleEvent(JSLEvent e)
     *
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(double time) {
        return (scheduleEvent(time, JSLEvent.DEFAULT_TYPE));
    }

    /** Creates an event and schedules it onto the event calendar
     * Uses this scheduling element's default event action which calls the
     * method handleEvent(JSLEvent e)
     *
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param message An object to be sent with the event
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(double time, Object message) {
        JSLEvent e = scheduleEvent(time, JSLEvent.DEFAULT_TYPE);
        e.setMessage(message);
        return (e);
    }

    /** Creates an event and schedules it onto the event calendar
     * Uses this scheduling element's default event action which calls the
     * method handleEvent(JSLEvent e)
     *
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param type represents the type of event, user defined
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(double time, int type) {
        JSLEvent e = scheduleEvent(getHandleEventAction(), time, getName(), JSLEvent.DEFAULT_PRIORITY, null);
        e.setType(type);
        return (e);
    }

    /** Creates an event and schedules it onto the event calendar
     * Uses this scheduling element's default event action which calls the
     * method handleEvent(JSLEvent e)
     *
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param type represents the type of event, user defined
     * @param message an object to attach to the event
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(double time, int type, Object message) {
        JSLEvent e = scheduleEvent(getHandleEventAction(), time, getName(), JSLEvent.DEFAULT_PRIORITY, message);
        e.setType(type);
        return (e);
    }

    /** Creates an event and schedules it onto the event calendar
     * Uses this scheduling element's default event action which calls the
     * method handleEvent(JSLEvent e)
     *
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param type represents the type of event, user defined
     * @param priority the priority of the event
     * @param message an object to attach to the event
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(double time, int type, int priority, Object message) {
        JSLEvent e = scheduleEvent(getHandleEventAction(), time, getName(), priority, message);
        e.setType(type);
        return (e);
    }

    /** Creates an event and schedules it onto the event calendar
     * Uses this scheduling element's default event action which calls the
     * method handleEvent(JSLEvent e)
     *
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param type represents the type of event, user defined
     * @param priority the priority of the event
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(double time, int type, int priority) {
        JSLEvent e = scheduleEvent(getHandleEventAction(), time, getName(), priority, null);
        e.setType(type);
        return (e);
    }

    /** Creates an event and schedules it onto the event calendar
     * Uses this scheduling element's default event action which calls the
     * method handleEvent(JSLEvent e)
     *
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(GetValueIfc time) {
        return (scheduleEvent(time.getValue(), JSLEvent.DEFAULT_TYPE));
    }

    /** Creates an event and schedules it onto the event calendar
     * Uses this scheduling element's default event action which calls the
     * method handleEvent(JSLEvent e)
     *
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param message An object to be sent with the event
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(GetValueIfc time, Object message) {
        JSLEvent e = scheduleEvent(time.getValue(), JSLEvent.DEFAULT_TYPE, message);
        return (e);
    }

    /** Creates an event and schedules it onto the event calendar
     * Uses this scheduling element's default event action which calls the
     * method handleEvent(JSLEvent e)
     *
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param type represents the type of event, user defined
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(GetValueIfc time, int type) {
        JSLEvent e = scheduleEvent(getHandleEventAction(), time.getValue(), getName(), JSLEvent.DEFAULT_PRIORITY, null);
        e.setType(type);
        return (e);
    }

    /** Creates an event and schedules it onto the event calendar
     * Uses this scheduling element's default event action which calls the
     * method handleEvent(JSLEvent e)
     *
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param type represents the type of event, user defined
     * @param message an object to attach to the event
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(GetValueIfc time, int type, Object message) {
        JSLEvent e = scheduleEvent(getHandleEventAction(), time.getValue(), getName(), JSLEvent.DEFAULT_PRIORITY, message);
        e.setType(type);
        return (e);
    }

    /** Creates an event and schedules it onto the event calendar
     * Uses this scheduling element's default event action which calls the
     * method handleEvent(JSLEvent e)
     *
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param type represents the type of event, user defined
     * @param priority the priority of the event
     * @param message an object to attach to the event
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(GetValueIfc time, int type, int priority, Object message) {
        JSLEvent e = scheduleEvent(getHandleEventAction(), time.getValue(), getName(), priority, message);
        e.setType(type);
        return (e);
    }

    /** Creates an event and schedules it onto the event calendar
     * Uses this scheduling element's default event action which calls the
     * method handleEvent(JSLEvent e)
     *
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param type represents the type of event, user defined
     * @param priority the priority of the event
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(GetValueIfc time, int type, int priority) {
        JSLEvent e = scheduleEvent(getHandleEventAction(), time.getValue(), getName(), priority, null);
        e.setType(type);
        return (e);
    }

    /** Creates an event and schedules it onto the event calendar
     * @param action represents an ActionListener that will handle the change of state logic
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(EventActionIfc action, double time) {
        return (scheduleEvent(action, time, getName(), JSLEvent.DEFAULT_PRIORITY, null));
    }

    /** Creates an event and schedules it onto the event calendar
     * @param action represents an ActionListener that will handle the change of state logic
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param message is a generic Object that may represent data to be transmitted with the event
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(EventActionIfc action, double time, Object message) {
        return (scheduleEvent(action, time, getName(), JSLEvent.DEFAULT_PRIORITY, message));
    }

    /** Creates an event and schedules it onto the event calendar
     * @param action represents an ActionListener that will handle the change of state logic
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param name A string to name the event
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(EventActionIfc action, double time, String name) {
        return (scheduleEvent(action, time, name, JSLEvent.DEFAULT_PRIORITY, null));
    }

    /** Creates an event and schedules it onto the event calendar
     * @param action represents an ActionListener that will handle the change of state logic
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param name A string to name the event
     * @param priority is used to influence the ordering of events
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(EventActionIfc action, double time, String name, int priority) {
        return (scheduleEvent(action, time, name, priority, null));
    }

    /** Creates an event and schedules it onto the event calendar
     * @param action represents an ActionListener that will handle the change of state logic
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param priority is used to influence the ordering of events
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(EventActionIfc action, double time, int priority) {
        return (scheduleEvent(action, time, getName(), JSLEvent.DEFAULT_PRIORITY, null));
    }

    /** Creates an event and schedules it onto the event calendar
     * @param action represents an ActionListener that will handle the change of state logic
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param name A string to name the event
     * @param message is a generic Object that may represent data to be transmitted with the event
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(EventActionIfc action, double time, String name, Object message) {
        return (scheduleEvent(action, time, name, JSLEvent.DEFAULT_PRIORITY, message));
    }

    /** Creates an event and schedules it onto the event calendar
     * @param action represents an ActionListener that will handle the change of state logic
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param name the name
     * @param priority is used to influence the ordering of events
     * @param message is a generic Object that may represent data to be transmitted with the event
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(EventActionIfc action,
            double time, String name, int priority, Object message) {
        JSLEvent event = getExecutive().scheduleEvent(action, time, name, priority, message);
        event.setModelElement(this);
        return (event);
    }

    /** Creates an event and schedules it onto the event calendar
     * @param action represents an ActionListener that will handle the change of state logic
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(EventActionIfc action, GetValueIfc time) {
        return (scheduleEvent(action, time.getValue(), getName(), JSLEvent.DEFAULT_PRIORITY, null));
    }

    /** Creates an event and schedules it onto the event calendar
     * @param action represents an ActionListener that will handle the change of state logic
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param message is a generic Object that may represent data to be transmitted with the event
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(EventActionIfc action, GetValueIfc time, Object message) {
        return (scheduleEvent(action, time.getValue(), getName(), JSLEvent.DEFAULT_PRIORITY, message));
    }

    /** Creates an event and schedules it onto the event calendar
     * @param action represents an ActionListener that will handle the change of state logic
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param name A string to name the event
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(EventActionIfc action, GetValueIfc time, String name) {
        return (scheduleEvent(action, time.getValue(), name, JSLEvent.DEFAULT_PRIORITY, null));
    }

    /** Creates an event and schedules it onto the event calendar
     * @param action represents an ActionListener that will handle the change of state logic
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param name A string to name the event
     * @param priority is used to influence the ordering of events
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(EventActionIfc action, GetValueIfc time, String name, int priority) {
        return (scheduleEvent(action, time.getValue(), name, priority, null));
    }

    /** Creates an event and schedules it onto the event calendar
     * @param action represents an ActionListener that will handle the change of state logic
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param priority is used to influence the ordering of events
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(EventActionIfc action, GetValueIfc time, int priority) {
        return (scheduleEvent(action, time.getValue(), getName(), JSLEvent.DEFAULT_PRIORITY, null));
    }

    /** Creates an event and schedules it onto the event calendar
     * @param action represents an ActionListener that will handle the change of state logic
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param name A string to name the event
     * @param message is a generic Object that may represent data to be transmitted with the event
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(EventActionIfc action, GetValueIfc time, String name, Object message) {
        return (scheduleEvent(action, time.getValue(), name, JSLEvent.DEFAULT_PRIORITY, message));
    }

    /** Creates an event and schedules it onto the event calendar
     * @param action represents an ActionListener that will handle the change of state logic
     * @param time represents the inter-event time, i.e. the interval from the current time to when the
     *        event will need to occur
     * @param name A string to name the event
     * @param priority is used to influence the ordering of events
     * @param message is a generic Object that may represent data to be transmitted with the event
     * @return a valid JSLEvent
     */
    protected final JSLEvent scheduleEvent(EventActionIfc action, GetValueIfc time, String name, int priority, Object message) {
        return (scheduleEvent(action, time.getValue(), name, priority, message));
    }

    /** Can be used as a general event handler by setting the event type and
     *  conditioning on it within this method.  This alleviates the need for
     *  actions for each event type but is not as flexible as separate actions.
     *
     * @param event
     */
    protected void handleEvent(JSLEvent event) {
    }

    /** Returns a reference to the action that calls handleEvent()
     *
     * @return the action that calls handleEvent()
     */
    protected final EventActionIfc getHandleEventAction() {
        if (myHandleEventAction == null) {
            myHandleEventAction = new EventAction();
        }
        return (myHandleEventAction);
    }

    private class EventAction implements EventActionIfc {

        @Override
        public void action(JSLEvent evt) {
            handleEvent(evt);
        }
    }
}
