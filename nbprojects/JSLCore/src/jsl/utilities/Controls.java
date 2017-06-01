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
package jsl.utilities;

import java.util.Map;
import java.util.Set;
import java.util.Collections;

import jsl.utilities.random.RandomIfc;

/** This class acts like a Map to allow named controls and
 *  their associated values to be viewed and set. 
 *  
 *  Implementors of ControllableIfc are responsible for making
 *  instances of this class that are filled appropriately
 * 
 *  Note: None of the underlying Maps are made.  Implementers
 *  of sub-classes must create and populate the maps.
 * 
 *  Attempting to access a map when it has not been created
 *  will result in an IllegalStateException.
 * 
 *  The hasXControl() methods can be used to check if the 
 *  control datatype has been defined
 *
 */
abstract public class Controls {

    /** The controllableIfc that uses the Controls
     * 
     */
    protected ControllableIfc myControllable;

    /** The Map that hold the controls as pairs
     *  key = name of control
     *  value = value of the control as a String
     *  Not allocated unless at least one control is supplied
     * 
     */
    protected Map<String, String> myStringControls;

    /** The Map that hold the controls as pairs
     *  key = name of control
     *  value = value of the control as a Double
     *  Not allocated unless at least one control is supplied
     * 
     */
    protected Map<String, Double> myDoubleControls;

    /** The Map that hold the controls as pairs
     *  key = name of control
     *  value = value of the control as an Integer
     *  Not allocated unless at least one control is supplied
     * 
     */
    protected Map<String, Integer> myIntegerControls;

    /** The Map that hold the controls as pairs
     *  key = name of control
     *  value = value of the control as a Float
     *  Not allocated unless at least one control is supplied
     * 
     */
    protected Map<String, Float> myFloatControls;

    /** The Map that hold the controls as pairs
     *  key = name of control
     *  value = value of the control as a Long
     *  Not allocated unless at least one control is supplied
     * 
     */
    protected Map<String, Long> myLongControls;

    /** The Map that hold the controls as pairs
     *  key = name of control
     *  value = value of the control as a Boolean
     *  Not allocated unless at least one control is supplied
     * 
     */
    protected Map<String, Boolean> myBooleanControls;

    /** The Map that hold the controls as pairs
     *  key = name of control
     *  value = value of the control as a double[]
     *  Not allocated unless at least one control is supplied
     * 
     */
    protected Map<String, double[]> myDoubleArrayControls;

    /** The Map that hold the controls as pairs
     *  key = name of control
     *  value = value of the control as a RandomIfc
     *  Not allocated unless at least one control is supplied
     * 
     */
    protected Map<String, RandomIfc> myRandomIfcControls;

    /** The Map that hold the controls as pairs
     *  key = name of control
     *  value = value of the control as a ControllableIfc
     *  Not allocated unless at least one control is supplied
     * 
     */
    protected Map<String, ControllableIfc> myControllableIfcControls;

    /**
     * 
     * @param controllable 
     */
    protected Controls(ControllableIfc controllable) {
        if (controllable == null) {
            throw new IllegalArgumentException("The ControllableIfc was null");
        }
        myControllable = controllable;
    }

    /**
     * 
     * @return
     */
    public ControllableIfc getControllable() {
        return myControllable;
    }

    /** Gets the value associated with the supplied key as a String.  If the key is null
     *  or there is no control for the supplied key, then an exception occurs
     * 
     * @param key
     * @return
     */
    public String getStringControl(String key) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myStringControls == null) {
            throw new IllegalStateException("Attempted to get a string control when no string controls have been set.");
        }

        if (!myStringControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }


        return myStringControls.get(key);
    }

    /** Changes the value associated with the key to the supplied value.  The key must already
     *  exist in the controls and cannot be null.
     * 
     * @param key must not be null
     * @param value 
     * @return the previous value that was associated with the key
     */
    public String changeStringControl(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myStringControls == null) {
            throw new IllegalStateException("Attempted to change a string control when no string controls have been set.");
        }

        if (!myStringControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        return myStringControls.put(key, value);
    }

    /** Gets the value associated with the supplied key as a double.  If the key is null
     *  or there is no control for the supplied key, then an exception occurs
     * 
     * @param key
     * @return
     */
    public double getDoubleControl(String key) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myDoubleControls == null) {
            throw new IllegalStateException("Attempted to get a double control when no double controls have been set.");
        }

        if (!myDoubleControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        return myDoubleControls.get(key);
    }

    /** Changes the value associated with the key to the supplied value.  If the key is null
     *  or there is no control for the supplied key, then an exception occurs
     * 
     * @param key key with which the value is to be associated
     * @param value the value to be associated with key
     * @return the previous value that was associated with the key
     */
    public Double changeDoubleControl(String key, double value) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myDoubleControls == null) {
            throw new IllegalStateException("Attempted to change a double control when no double controls have been set.");
        }

        if (!myDoubleControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        return myDoubleControls.put(key, value);
    }

    /** Gets the value associated with the supplied key as a double{].  If the key is null
     *  or there is no control for the supplied key, then an exception occurs
     * 
     * @param key
     * @return  a copy of the associated double[] is returned
     */
    public double[] getDoubleArrayControl(String key) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myDoubleArrayControls == null) {
            throw new IllegalStateException("Attempted to get a DoubleArray control when no DoubleArray controls have been set.");
        }

        if (!myDoubleArrayControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        double[] value = myDoubleArrayControls.get(key);
        double[] tmp = new double[value.length];
        System.arraycopy(value, 0, tmp, 0, value.length);
        return tmp;
    }

    /** Returns the size (array length) of the DoubleArray control. If the key is null
     *  or there is no control for the supplied key, then an exception occurs
     * 
     * @param key
     * @return
     */
    public int getDoubleArrayControlSize(String key) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myDoubleArrayControls == null) {
            throw new IllegalStateException("Attempted to get a DoubleArray control size when no DoubleArray controls have been set.");
        }

        if (!myDoubleArrayControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        return myDoubleArrayControls.get(key).length;
    }

    /** Changes the value associated with the key to the supplied value.  If the key is null
     *  or there is no control for the supplied key, then an exception occurs.
     *  
     *  The supplied array is copied.
     * 
     * @param key key with which the double[] value is to be associated
     * @param value the double[] value to be associated with key, cannot be null, must be same size as original double[]
     * @return the previous double[] value that was associated with the key
     */
    public double[] changeDoubleArrayControl(String key, double[] value) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (value == null) {
            throw new IllegalArgumentException("The supplied array cannot be null");
        }

        if (myDoubleArrayControls == null) {
            throw new IllegalStateException("Attempted to change a DoubleArray control when no DoubleArray controls have been set.");
        }

        if (!myDoubleArrayControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        int size = this.getDoubleArrayControlSize(key);
        if (size != value.length) {
            throw new IllegalArgumentException("The supplied array is not the same size as the original double[]");
        }

        double[] tmp = new double[value.length];
        System.arraycopy(value, 0, tmp, 0, value.length);
        return myDoubleArrayControls.put(key, tmp);
    }

    /** Gets the value associated with the supplied key. If the key is null
     *  or there is no control for the supplied key, then an exception occurs.
     * 
     * 
     * @param key
     * @return
     */
    public int getIntegerControl(String key) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myIntegerControls == null) {
            throw new IllegalStateException("Attempted to get int control when no int controls have been set.");
        }

        if (!myIntegerControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        return myIntegerControls.get(key);
    }

    /** Changes the value of the key to the supplied value.  If the key is null
     *  or there is no control for the supplied key, then an exception occurs.
     * 
     * @param key
     * @param value
     * @return the previous value that was associated with the key
     */
    public Integer changeIntegerControl(String key, int value) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myIntegerControls == null) {
            throw new IllegalStateException("Attempted to change an integer control when no integer controls have been set.");
        }

        if (!myIntegerControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        return myIntegerControls.put(key, value);
    }

    /** Gets the value associated with the supplied key.  If the key is null
     *  or there is no control for the supplied key, then an exception occurs.
     * 
     * @param key
     * @return
     */
    public long getLongControl(String key) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myLongControls == null) {
            throw new IllegalStateException("Attempted to get long control when no long controls have been set.");
        }

        if (!myLongControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        return myLongControls.get(key);
    }

    /** Changes the value of the key to the supplied value.  If the key is null
     *  or there is no control for the supplied key, then an exception occurs.
     * 
     * @param key
     * @param value
     * @return the previous value that was associated with the key
     */
    public Long changeLongControl(String key, long value) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myLongControls == null) {
            throw new IllegalStateException("Attempted to change a long control when no long controls have been set.");
        }

        if (!myLongControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        return myLongControls.put(key, value);
    }

    /** Gets the value associated with the supplied key.  If the key is null
     *  or there is no control for the supplied key, then an exception occurs.
     * 
     * @param key
     * @return
     */
    public float getFloatControl(String key) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myFloatControls == null) {
            throw new IllegalStateException("Attempted to get float control when no float controls have been set.");
        }

        if (!myFloatControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        return myFloatControls.get(key);
    }

    /** Changes the value of the key to the supplied value.  If the key is null
     *  or there is no control for the supplied key, then an exception occurs.
     * 
     * @param key
     * @param value
     * @return the previous value that was associated with the key
     */
    public Float changeFloatControl(String key, float value) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myFloatControls == null) {
            throw new IllegalStateException("Attempted to change afloat control when no float controls have been set.");
        }

        if (!myFloatControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        return myFloatControls.put(key, value);
    }

    /** Gets the value associated with the supplied key.  If the key is null
     *  or there is no control for the supplied key, then an exception occurs.
     * 
     * @param key
     * @return
     */
    public boolean getBooleanControl(String key) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myBooleanControls == null) {
            throw new IllegalStateException("Attempted to get boolean control when no boolean controls have been set.");
        }

        if (!myBooleanControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        return myBooleanControls.get(key);
    }

    /** Sets the value of the key to the supplied value.  If the key is null
     *  or there is no control for the supplied key, then an exception occurs.
     * 
     * @param key
     * @param value
     * @return the previous value that was associated with the key
     */
    public Boolean setBooleanControl(String key, boolean value) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myBooleanControls == null) {
            throw new IllegalStateException("Attempted to change a boolean control when no boolean controls have been set.");
        }

        if (!myBooleanControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        return myBooleanControls.put(key, value);
    }

    /** Gets the value associated with the supplied key as a RandomIfc.  If the key is null
     *  or there is no control for the supplied key, then an exception occurs.
     * 
     * @param key
     * @return
     */
    public RandomIfc getRandomIfcControl(String key) {

        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myRandomIfcControls == null) {
            throw new IllegalStateException("Attempted to get RandomIfc control when no RandomIfc controls have been set.");
        }

        if (!myRandomIfcControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        return myRandomIfcControls.get(key);
    }

    /** Sets the value associated with the key to the supplied value.  If the key is null
     *  or there is no control for the supplied key, then an exception occurs.
     * 
     * @param key key with which the string form of value is to be associated
     * @param value the value to be associated with key
     * @return the previous value that was associated with the key
     */
    public RandomIfc changeRandomIfcControl(String key, RandomIfc value) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myRandomIfcControls == null) {
            throw new IllegalStateException("Attempted to change a RandomIfc control when no RandomIfc controls have been set.");
        }

        if (!myRandomIfcControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        return myRandomIfcControls.put(key, value);
    }

    /** Gets the value associated with the supplied key as a ControllableIfc.  If the key is null
     *  or there is no control for the supplied key, then an exception occurs.
     * 
     * @param key
     * @return
     */
    public ControllableIfc getControllableControl(String key) {

        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myControllableIfcControls == null) {
            throw new IllegalStateException("Attempted to get ControllableIfc control when no double controls have been set.");
        }

        if (!myControllableIfcControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        return myControllableIfcControls.get(key);
    }

    /** Sets the value associated with the key to the supplied value.  If the key is null
     *  or there is no control for the supplied key, then an exception occurs.
     * 
     * @param key key with which the string form of value is to be associated
     * @param value the value to be associated with key
     * @return the previous value that was associated with the key
     */
    public ControllableIfc setControllableControl(String key, ControllableIfc value) {
        if (key == null) {
            throw new IllegalArgumentException("The supplied key cannot be null");
        }

        if (myControllableIfcControls == null) {
            throw new IllegalStateException("Attempted to change a ControllableIfc control when no ControllableIfc controls have been set.");
        }

        if (!myControllableIfcControls.containsKey(key)) {
            throw new IllegalArgumentException("The supplied key is not associated with a control value");
        }

        return myControllableIfcControls.put(key, value);
    }

    /** Checks if the supplied key is contained in the controls
     * 
     * @param key
     * @return
     */
    public boolean containsControl(String key) {

        if ((myDoubleControls != null) && (myDoubleControls.containsKey(key))) {
            return true;
        } else if ((myDoubleArrayControls != null) && (myDoubleArrayControls.containsKey(key))) {
            return true;
        } else if ((myRandomIfcControls != null) && (myRandomIfcControls.containsKey(key))) {
            return true;
        } else if ((myIntegerControls != null) && (myIntegerControls.containsKey(key))) {
            return true;
        } else if ((myControllableIfcControls != null) && (myControllableIfcControls.containsKey(key))) {
            return true;
        } else if ((myLongControls != null) && (myLongControls.containsKey(key))) {
            return true;
        } else if ((myBooleanControls != null) && (myBooleanControls.containsKey(key))) {
            return true;
        } else if ((myStringControls != null) && (myStringControls.containsKey(key))) {
            return true;
        } else if ((myFloatControls != null) && (myFloatControls.containsKey(key))) {
            return true;
        } else {
            return false;
        }
    }

    /** Returns true if at least one String control has been set
     * 
     * @return
     */
    public boolean hasStringControl() {
        return (myStringControls != null) && (!myStringControls.isEmpty());
    }

    /** Returns true if at least one Double control has been set
     * 
     * @return
     */
    public boolean hasDoubleControl() {
        return (myDoubleControls != null) && (!myDoubleControls.isEmpty());
    }

    /** Returns true if at least one RandomIfc control has been set
     * 
     * @return
     */
    public boolean hasRandomIfcControl() {
        return (myRandomIfcControls != null) && (!myRandomIfcControls.isEmpty());
    }

    /** Returns true if at least one RandomIfc control has been set
     * 
     * @return
     */
    public boolean hasControllableIfcControl() {
        return (myControllableIfcControls != null) && (!myControllableIfcControls.isEmpty());
    }

    /** Returns true if at least one double[] control has been set
     * 
     * @return
     */
    public boolean hasDoubleArrayControl() {
        return (myDoubleArrayControls != null) && (!myDoubleArrayControls.isEmpty());
    }

    /** Returns true if at least one Integer control has been set
     * 
     * @return
     */
    public boolean hasIntegerControl() {
        return (myIntegerControls != null) && (!myIntegerControls.isEmpty());
    }

    /** Returns true if at least one Long control has been set
     * 
     * @return
     */
    public boolean hasLongControl() {
        return (myLongControls != null) && (!myLongControls.isEmpty());
    }

    /** Returns true if at least one Boolean control has been set
     * 
     * @return
     */
    public boolean hasBooleanControl() {
        return (myBooleanControls != null) && (!myBooleanControls.isEmpty());
    }

    /** Returns true if at least one Float control has been set
     * 
     * @return
     */
    public boolean hasFloatControl() {
        return (myFloatControls != null) && (!myFloatControls.isEmpty());
    }

    /** Returns an unmodifiable Set of the control's keys
     *  for String Controls
     *  
     * @return
     */
    public Set<String> getStringControlKeySet() {
        if (myStringControls != null) {
            return Collections.unmodifiableSet(myStringControls.keySet());
        } else {
            return null;
        }
    }

    /** Returns an unmodifiable Set of the control's keys
     *  for Double Controls
     *  
     * @return
     */
    public Set<String> getDoubleControlKeySet() {
        if (myDoubleControls != null) {
            return Collections.unmodifiableSet(myDoubleControls.keySet());
        } else {
            return null;
        }
    }

    /** Returns an unmodifiable Set of the control's keys
     *  for double[] Controls
     *  
     * @return
     */
    public Set<String> getDoubleArrayControlKeySet() {
        if (myDoubleArrayControls != null) {
            return Collections.unmodifiableSet(myDoubleArrayControls.keySet());
        } else {
            return null;
        }
    }

    /** Returns an unmodifiable Set of the control's keys
     *  for Integer Controls
     *  
     * @return
     */
    public Set<String> getIntegerControlKeySet() {
        if (myIntegerControls != null) {
            return Collections.unmodifiableSet(myIntegerControls.keySet());
        } else {
            return null;
        }
    }

    /** Returns an unmodifiable Set of the control's keys
     *  for Long Controls
     *  
     * @return
     */
    public Set<String> getLongControlKeySet() {
        if (myLongControls != null) {
            return Collections.unmodifiableSet(myLongControls.keySet());
        } else {
            return null;
        }
    }

    /** Returns an unmodifiable Set of the control's keys
     *  for Float Controls
     *  
     * @return
     */
    public Set<String> getFloatControlKeySet() {
        if (myFloatControls != null) {
            return Collections.unmodifiableSet(myFloatControls.keySet());
        } else {
            return null;
        }
    }

    /** Returns an unmodifiable Set of the control's keys
     *  for Boolean Controls
     *  
     * @return
     */
    public Set<String> getBooleanControlKeySet() {
        if (myBooleanControls != null) {
            return Collections.unmodifiableSet(myBooleanControls.keySet());
        } else {
            return null;
        }
    }

    /** Returns an unmodifiable Set of the control's keys
     *  for RandomIfc Controls
     *  
     * @return
     */
    public Set<String> getRandomIfcControlKeySet() {
        if (myRandomIfcControls != null) {
            return Collections.unmodifiableSet(myRandomIfcControls.keySet());
        } else {
            return null;
        }
    }

    /** Returns an unmodifiable Set of the control's keys
     *  for RandomIfc Controls
     *  
     * @return
     */
    public Set<String> getControllableIfcControlKeySet() {
        if (myControllableIfcControls != null) {
            return Collections.unmodifiableSet(myControllableIfcControls.keySet());
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID \t ");
        sb.append(myControllable.getId());
        sb.append("\n");
        sb.append("Name \t ");
        sb.append(myControllable.getName());
        sb.append("\n");
        if (myStringControls != null) {
            sb.append(myStringControls.toString());
            sb.append("\n");
        }
        if (myDoubleControls != null) {
            sb.append(myDoubleControls.toString());
            sb.append("\n");
        }
        if (myIntegerControls != null) {
            sb.append(myIntegerControls.toString());
            sb.append("\n");
        }
        if (myLongControls != null) {
            sb.append(myLongControls.toString());
            sb.append("\n");
        }
        if (myBooleanControls != null) {
            sb.append(myBooleanControls.toString());
            sb.append("\n");
        }
        if (myFloatControls != null) {
            sb.append(myFloatControls.toString());
            sb.append("\n");
        }
        if (myRandomIfcControls != null) {
            sb.append(myRandomIfcControls.toString());
            sb.append("\n");
        }
        if (myControllableIfcControls != null) {
            sb.append(myControllableIfcControls.toString());
            sb.append("\n");
        }

        //TODO double[] 
        return sb.toString();
    }
}
