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
package jsl.calendar;

import java.util.PriorityQueue;

import jsl.modeling.JSLEvent;

/** This class provides an event calendar by using a priority queue to hold the underlying events.
*/
public class PriorityQueueEventCalendar implements CalendarIfc {

    //  ===========================================
    //      CLASS AND OBJECT ATTRIBUTES
    //  ===========================================
    
    private PriorityQueue<JSLEvent> myEventSet;
    
    /** Creates new Calendar */
	/**
	 * 
	 */
	public PriorityQueueEventCalendar() {
		super();
		myEventSet = new PriorityQueue<JSLEvent>();
	}

	/* (non-Javadoc)
	 * @see jsl.calendar.CalendarIfc#add(jsl.modeling.JSLEvent)
	 */
	public final void add(JSLEvent event) {
		myEventSet.add(event);
	}

	/* (non-Javadoc)
	 * @see jsl.calendar.CalendarIfc#nextEvent()
	 */
	public final JSLEvent nextEvent() {
		return((JSLEvent)myEventSet.poll());
	}

	/* (non-Javadoc)
	 * @see jsl.calendar.CalendarIfc#peekNext()
	 */
	public final JSLEvent peekNext(){
		return(myEventSet.peek());
	}
	
	/* (non-Javadoc)
	 * @see jsl.calendar.CalendarIfc#isEmpty()
	 */
	public final boolean isEmpty() {
		return(myEventSet.isEmpty());
	}

	/* (non-Javadoc)
	 * @see jsl.calendar.CalendarIfc#cancelAll()
	 */
	public final void clear() {
		myEventSet.clear();
	}

	/* (non-Javadoc)
	 * @see jsl.calendar.CalendarIfc#cancel(jsl.modeling.JSLEvent)
	 */
	public final void cancel(JSLEvent event) {
        event.setCanceledFlag(true);
	}

	/* (non-Javadoc)
	 * @see jsl.calendar.CalendarIfc#size()
	 */
	public int size() {
		return(myEventSet.size());
	}

}
