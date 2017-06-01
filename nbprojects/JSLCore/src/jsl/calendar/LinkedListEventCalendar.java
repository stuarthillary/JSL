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

import jsl.modeling.JSLEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/** LinkedListEventCalendar is a concrete implementation of the CalendarIfc for use with the Scheduler
 *  This class provides an event calendar by using a java.util.LinkedList to hold the underlying events.
 *
*/
public class LinkedListEventCalendar implements CalendarIfc {
    
    //  ===========================================
    //      CLASS AND OBJECT ATTRIBUTES
    //  ===========================================
    
    private List<JSLEvent> myEventSet;
    
    /** Creates new Calendar */
    public LinkedListEventCalendar(){
        myEventSet = new LinkedList<JSLEvent>();
    }
        
    /** The add method will place the provided JSLEvent into the
     * underlying data structure ensuring the ordering of the events
     * to be processed
     * @param e The JSLEvent to be added to the calendar
     */    
    public void add(JSLEvent e){
        
        // nothing in calendar, just add it, and return
        if (myEventSet.isEmpty()){ 
            myEventSet.add(e);
            return;
        }
        
        // might as well check for worse case, if larger than the largest then put it at the end and return
        if (e.compareTo(myEventSet.get(myEventSet.size()-1)) >= 0){
            myEventSet.add(e);
            return;
        }
        
         // now iterate through the list
        for (ListIterator<JSLEvent> i=myEventSet.listIterator(); i.hasNext(); ){
            if ( e.compareTo(i.next()) < 0 ){
                // next() move the iterator forward, if it is < what was returned by next(), then it
                // must be inserted at the previous index
                myEventSet.add(i.previousIndex(),e);
                return;
            }
        }
    }
    
    /** Returns the next JSLEvent to be executed.
     * @return The JSLEvent to be executed next
     */    
    public JSLEvent nextEvent(){
        if (!isEmpty())
            return ((JSLEvent)myEventSet.remove(0));
        else
            return(null);
    }
    
    /** Returns the next JSLEvent without removing it
     *  or null if there is no next event
     * @return The JSLEvent to be executed next
     */    
    public JSLEvent peekNext(){
        if (!isEmpty())
            return ((JSLEvent)myEventSet.get(0));
        else
            return(null);
    }
    
    /** Checks to see if the calendar is empty
     * @return true is empty, false is not empty
     */    
    public boolean isEmpty(){
        return myEventSet.isEmpty();
    }
    
    /** Clears or cancels every event in the data structure.  Removes all JSLEvents
     * from the data structure.
     */    
    public void clear(){
        myEventSet.clear();
    }
    
    /** Cancels the supplied JSLEvent in the calendar.  Does not remove the event
     * from the calendar.  This method simply sets the cancel flag on the supplied event.
     * @param e The JSLEvent to be cancelled
     */    
    public void cancel(JSLEvent e){
        e.setCanceledFlag(true);
    }
    
    /** Returns the number of events in the calendar
     * @return An int representing the number of events.
     */    
    public int size(){
        return(myEventSet.size());
    }
    
    /** Returns A String representing the calendar.
     * @return A String representing the calendar.
     */    
    public String toString(){
        return(myEventSet.toString());
    }   
}