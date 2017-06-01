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

/** This class provides an event calendar by using a skew heap to hold the underlying events.
*/
public class SkewHeapEventCalendar implements CalendarIfc {
    
    //  ===========================================
    //      CLASS AND OBJECT ATTRIBUTES
    //  ===========================================
    private BinaryNode myRoot;
    private int myNumEvents;
    
    /** Creates new Calendar */
    public SkewHeapEventCalendar(){
        myNumEvents = 0;
        myRoot = null;
    }
    
    /** The add method will place the provided JSLEvent into the
     * underlying data structure ensuring the ordering of the events
     * to be processed
     * @param e The JSLEvent to be added to the calendar
     */    
    public void add(JSLEvent e){
        myRoot = merge(myRoot, new BinaryNode(e));
        myNumEvents++;
    }
    
    /** Returns the next JSLEvent to be executed.
     * @return The JSLEvent to be executed next
     */    
    public JSLEvent nextEvent(){
        if (!isEmpty()){
            JSLEvent e = (JSLEvent)myRoot.value;
            myRoot = merge(myRoot.leftChild, myRoot.rightChild);
            myNumEvents--;
            return (e);
        }
        else
            return(null);
    }
    
    /* (non-Javadoc)
     * @see jsl.calendar.CalendarIfc#peekNext()
     */
    public JSLEvent peekNext(){
        if (!isEmpty()){
            JSLEvent e = (JSLEvent)myRoot.value;
            return (e);
        }
        else
            return(null);   	
    }
    
    /** Checks to see if the calendar is empty
     * @return true is empty, false is not empty
     */    
    public boolean isEmpty(){
        return (myRoot == null);
    }
    
    /** Clears or cancels every event in the data structure.  Removes all JSLEvents
     * from the data structure.
     */    
    public void clear(){
        while (nextEvent() != null){
        }
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
        return(myNumEvents);
    }
    
    /** Returns the number of events in the calendar.  This may
     *  be augmented in the future to return the full calendar as a string.
     * @return A String representing the number of events in the calendar
     */    
    public String toString(){
        return("Number of events = " + myNumEvents);
    }
    
    //  ===========================================
    //      PRIVATE METHODS
    //  ===========================================

    private BinaryNode merge(BinaryNode left, BinaryNode right) {
        if (left == null) return right;
        if (right == null) return left;
        
        JSLEvent leftValue = (JSLEvent)left.value;
        JSLEvent rightValue = (JSLEvent)right.value;
        
        if (leftValue.compareTo(rightValue) < 0) {
            BinaryNode swap = left.leftChild;
            left.leftChild = merge(left.rightChild, right);
            left.rightChild = swap;
            return left;
        } else {
            BinaryNode swap = right.rightChild;
            right.rightChild = merge(right.leftChild, left);
            right.leftChild = swap;
            return right;
        }
    }    
    
    //  ===========================================
    //      PRIVATE INNER CLASS
    //  ===========================================

    
    private class BinaryNode {
        /**
         * value being held by node
         */
        public Object value;
        
        /**
         * left child of node
         */
        public BinaryNode leftChild = null;
        
        /**
         * right child of node
         */
        public BinaryNode rightChild = null;
        
        /**
         * initialize a newly created binary node
         */
        public BinaryNode(){
            value = null;
        }
        
        /**
         * initialize a newly created binary node
         *
         * @param v value to be associated with new node
         */
        public BinaryNode(Object v){
            value = v;
        }
        
        /** return true if we are not a sentinel node
         * @return true if not a sentinal node
         */
        public boolean isEmpty() {
            return false;
        }
    }
}
