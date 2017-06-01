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

import java.util.*;

import jsl.utilities.reporting.JSL;
import jsl.modeling.elements.spatial.SpatialModel;
import jsl.modeling.elements.variable.*;

import jsl.utilities.Controls;
import jsl.utilities.IdentityIfc;
import jsl.modeling.elements.RandomElementIfc;
import jsl.modeling.elements.queue.FIFODiscipline;
import jsl.modeling.elements.queue.LIFODiscipline;
import jsl.modeling.elements.queue.QObject;
import jsl.modeling.elements.queue.RandomDiscipline;
import jsl.modeling.elements.queue.RankedQDiscipline;
import jsl.modeling.elements.resource.Entity;
import jsl.modeling.elements.resource.EntityType;
import jsl.observers.ObservableComponent;
import jsl.observers.ObservableIfc;
import jsl.observers.ObserverIfc;

/**
 * The ModelElement represents elements within the simulation model that can
 * schedule and react to simulation events and default simulation actions (e.g.
 * setup, begin replication, warm up, after replication, end simulation, etc.)
 * The ModelElement is a component in the composite pattern
 *
 */
public abstract class ModelElement implements IdentityIfc, ObservableIfc {

    /**
     * A reference to a spatial model if available
     */
    private SpatialModel mySpatialModel;

    /**
     * incremented to give a running total of the number of model elements
     * created
     */
    private static int myCounter_;

    /**
     * A constant for the default batch priority
     */
    public static final int DEFAULT_TIMED_EVENT_PRIORITY = 3;

    /**
     * A mnemonic for zero
     */
    public static final int NONE = 0;

    /**
     * An "enum" for the setup state.
     */
    public static final int BEFORE_EXPERIMENT = JSL.getNextEnumConstant();

    /**
     * An "enum" for the before replication state.
     */
    public static final int BEFORE_REPLICATION = JSL.getNextEnumConstant();

    /**
     * An "enum" for the initialization state.
     */
    public static final int INITIALIZED = JSL.getNextEnumConstant();

    /**
     * An "enum" for the monte carlo state.
     */
    public static final int MONTE_CARLO = JSL.getNextEnumConstant();

    /**
     * An "enum" for the update state.
     */
    public static final int UPDATE = JSL.getNextEnumConstant();

    /**
     * An "enum" for the warmup state.
     */
    public static final int WARMUP = JSL.getNextEnumConstant();

    /**
     * An "enum" for the timed update state.
     */
    public static final int TIMED_UPDATE = JSL.getNextEnumConstant();

    /**
     * An "enum" for when the replication ends
     */
    public static final int REPLICATION_ENDED = JSL.getNextEnumConstant();

    /**
     * An "enum" for the after replication state.
     */
    public static final int AFTER_REPLICATION = JSL.getNextEnumConstant();

    /**
     * An "enum" for the end of simulation state.
     */
    public static final int AFTER_EXPERIMENT = JSL.getNextEnumConstant();

    /**
     * An "enum" to indicate that the model element was removed from the model
     * element hierarchy
     */
    public static final int REMOVED_FROM_MODEL = JSL.getNextEnumConstant();

    /**
     * An "enum" to indicate that the model element performed its
     * registerConditionalActions() method
     */
    public static final int CONDITIONAL_ACTION_REGISTRATION = JSL.getNextEnumConstant();

    /**
     * An "enum" to represent time unit conversion.
     *
     */
    public static final int TIME_UNIT_MILLISECOND = 1;
    /**
     * An "enum" to represent time unit conversion.
     *
     */
    public static final int TIME_UNIT_SECOND = 1000;
    /**
     * An "enum" to represent time unit conversion.
     *
     */
    public static final int TIME_UNIT_MINUTE = 60 * 1000;
    /**
     * An "enum" to represent time unit conversion.
     *
     */
    public static final int TIME_UNIT_HOUR = 60 * 60 * 1000;
    /**
     * An "enum" to represent time unit conversion.
     *
     */
    public static final int TIME_UNIT_DAY = 24 * 60 * 60 * 1000;
    /**
     * An "enum" to represent time unit conversion.
     *
     */
    public static final int TIME_UNIT_WEEK = 7 * 24 * 60 * 60 * 1000;

    /**
     * A reference to the overall model containing all model elements.
     */
    private Model myModel;

    /**
     * The id of the model element, currently if the model element is the ith
     * model element created then the id is equal to i
     */
    private int myId;

    /**
     * The name of the model element
     */
    private String myName;

    /**
     * A general string that can be used to label the model element Unlike the
     * model element's name (e.g. getName()) the label does not have to be
     * unique. By default it is set equal to the name of the model element.
     */
    private String myLabel;

    /**
     * A flag to control whether or not the model element reacts to before
     * experiment actions.
     */
    protected boolean myBeforeExperimentOption;

    /**
     * A flag to control whether or not the model element reacts to
     * initialization actions
     */
    protected boolean myInitializationOption;

    /**
     * A flag to control whether or not the model element reacts to before
     * replication actions.
     */
    protected boolean myBeforeReplicationOption;

    /**
     * A flag to control whether or not the model element participates in monte
     * carlo actions.
     */
    protected boolean myMonteCarloOption;

    /**
     * A flag to control whether or not the model element reacts to end
     * replication actions.
     */
    protected boolean myReplicationEndedOption;

    /**
     * A flag to control whether or not the model element reacts to after
     * replication actions.
     */
    protected boolean myAfterReplicationOption;

    /**
     * Specifies if this model element will be warmed up when the warmup action
     * occurs for its parent.
     */
    protected boolean myWarmUpOption;

    /**
     * Specifies whether or not this model element participates in time update
     * event specified by its parent
     */
    protected boolean myTimedUpdateOption;

    /**
     * Indicates whether or not update notifications will be sent by this model
     * element. The default is true.
     */
    private boolean myUpdateNotificationFlag = true;

    /**
     * A flag to control whether or not the model element reacts to after
     * experiment actions.
     */
    protected boolean myAfterExperimentOption;

    /**
     * A collection containing the first level children of this model element
     */
    protected List<ModelElement> myModelElements;

    /**
     * The parent of this model element
     */
    private ModelElement myParentModelElement;

    /**
     * Can be used by sub-classes to assist in implementing the ControllableIfc
     * By default myControls is null, unless setControls() is called Use the
     * protected method getControls() to access this in subclasses.
     */
    private Controls myControls;

    /**
     * The action listener that reacts to the warm up event.
     */
    protected WarmUpEventAction myWarmUpActionListener;

    /**
     * A reference to the warm up event
     */
    protected JSLEvent myWarmUpEvent;

    /**
     * Indicates whether or not the warm up action occurred sometime during the
     * simulation. False indicates that the warm up action has not occurred
     */
    protected boolean myWarmUpIndicator = false;

    /**
     * Specifies the priority of this model element's warm up event.
     */
    protected int myWarmUpPriority = JSLEvent.DEFAULT_WARMUP_EVENT_PRIORITY;

    /**
     * The length of time from the start of the simulation to the warm up event.
     */
    protected double myLengthOfWarmUp = 0.0; // zero is no warm up

    /**
     * The action listener that reacts to the timed update event.
     */
    protected TimedUpdateEventAction myTimedUpdateActionListener;

    /**
     * A reference to the TimedUpdate event.
     */
    protected JSLEvent myTimedUpdateEvent;

    /**
     * Specifies the priority of this model element's timed update event.
     */
    protected int myTimedUpdatePriority = DEFAULT_TIMED_EVENT_PRIORITY;

    /**
     * The time interval between TimedUpdate events. The default is zero,
     * indicating no timed update
     */
    protected double myTimedUpdateInterval = 0.0;

    /**
     * Keeps track of the current state for observers
     */
    private int myObserverState = 0;

    /**
     * Keeps track of the previous type of state change for observers
     */
    private int myPreviousObserverState = 0;

    /**
     * If this flag is set to true, the model element (and all its children)
     * will be marked for removal from the model prior to the beginning of the
     * next replication. The default is false. Setting this to true is useful if
     * model elements were added during the replication that need to be removed
     * after the replication has been completed in order to have the model have
     * the same configuration at the beginning of the next replication.
     *
     * NOTE: This only marks the element for removal. It does not automatically
     * remove the element since the order of removal is model dependent. It is
     * up to the user to remove the element prior to the beginning of the next
     * replication
     *
     */
    private boolean myRemoveFromModelPriorToRepFlag = false;

    /**
     * Indicates that the model element was added to the model while a
     * replication was running. The default is false.
     *
     */
    private boolean myAddedWhileReplicationWasRunningFlag = false;

    /**
     * Used to allow observers
     *
     */
    protected ObservableComponent myObservableComponent;

    /**
     * The default constructor is only called by Model so that the Model does
     * not have to have a parent
     */
    ModelElement(String name) {
        constructorCalls_(name);
    }

    /**
     * Creates a model element with name "null" as a child element of the
     * supplied parent
     *
     * @param parent
     */
    public ModelElement(ModelElement parent) {
        this(parent, null);
    }

    /**
     * Creates a model element with the given name as a child element of the
     * supplied parent. The defaults for a general model element are:
     *
     * before experiment option: true before replication option: true
     * initialization option: true after replication option: true after
     * experiment option: true timed update option: true batching option: true
     * length of warm up = 0.0 // no warm up length of batch interval = 0.0 //
     * no batch interval
     *
     * @param parent
     * @param name The name of the model element
     */
    public ModelElement(ModelElement parent, String name) {
        if (parent == null) {
            throw new IllegalArgumentException("The parent ModelElement was null");
        }
        constructorCalls_(name);
        // should not be leaking this because calls are only package scope
        // add this model element to the parent and also set this element's parent
        parent.addModelElement(this);
        // set the model for the element to the same as the parent's model
        setModel(parent.getModel());
        // add this model element to the model's map
        getModel().addToModelElementMap(this);
    }

    /**
     *
     * @param name the name of the model element
     */
    private void constructorCalls_(String name) {
        myCounter_ = myCounter_ + 1;
        myId = myCounter_;
        setName(name);
        setStringLabel(getName());
        myObservableComponent = new ObservableComponent();
        setBeforeExperimentOption(true);
        setBeforeReplicationOption(true);
        setMonteCarloOption(false);
        setInitializationOption(true);
        setReplicationEndedOption(true);
        setAfterReplicationOption(true);
        setAfterExperimentOption(true);
        setWarmUpOption(true);
        setTimedUpdateOption(true);

        myModelElements = new ArrayList<ModelElement>();

    }

    /**
     * Sets the name of this model element
     *
     * @param str The name as a string.
     */
    protected final void setName(String str) {

        if (str == null) { // no name is being passed, construct a default name
            String s = this.getClass().getName();
            int k = s.lastIndexOf(".");
            if (k != -1) {
                s = s.substring(k + 1);
            }
            str = s + "_" + getId();
        }

        if (myName == null) {// the model element's name has not yet been set
            myName = str;
        } else { // the model element's name has already been set
            // the user is trying to change it
            throw new UnsupportedOperationException("Tried to change the name of the model element\n"
                    + " after it had been already set.");
        }

    }

    /**
     * Returns a string representation of the model element and its child model
     * elements. Useful for realizing the model element hierarchy. null null null     {@literal <modelelement>}
     * {@literal<type> getClass().getSimpleName() <\type>}
     * {@literal<name> getName() <\name>} child elements here, etc.
     * {@literal</modelelement>}
     *
     * @return the model element as a string
     */
    public final String getModelElementsAsString() {
        StringBuilder sb = new StringBuilder();
        getModelElementsAsString(sb);
        return sb.toString();
    }

    /**
     * Fills up the supplied StringBuilder with a string representation of the
     * model element and its child model elements Useful for realizing the model
     * element hierarchy. null null null     {@literal <modelelement>
     * <type> getClass().getSimpleName() <\type>
     * <name> getName() <\name>
     * child elements here, etc.
     * </modelelement>}
     *
     * @param sb will hold the model element as a StringBuilder
     */
    public final void getModelElementsAsString(StringBuilder sb) {
        getModelElementsAsString(sb, 0);
    }

    /**
     * Fills up the supplied StringBuilder with a string representation of the
     * model element and its child model elements Useful for realizing the model
     * element hierarchy. null null null     {@literal <modelelement>}
     * {@literal<type> getClass().getSimpleName() <\type>}
     * {@literal<name> getName() <\name>} child elements here, etc.
     * {@literal</modelelement>}
     *
     * @param sb to hold the model element as a string
     * @param n The starting level of indentation for the model elements
     */
    public final void getModelElementsAsString(StringBuilder sb, int n) {
        indent(sb, n);
        sb.append("<modelelement>\r\n");
        indent(sb, n + 1);
        sb.append("<type>");
        sb.append(getClass().getSimpleName());
        sb.append("</type>\r\n");
        indent(sb, n + 1);
        sb.append("<name>");
        sb.append(getName());
        sb.append("</name>\r\n");
        for (ModelElement m : myModelElements) {
            m.getModelElementsAsString(sb, n + 1);
        }
        indent(sb, n);
        sb.append("</modelelement>\r\n");
    }

    /**
     * Add spaces representing the level of indention
     *
     * @param sb holds the stuff to be indented
     * @param n level of indentation
     */
    protected final void indent(StringBuilder sb, int n) {
        for (int i = 1; i <= n; i++) {
            sb.append("  ");
        }
    }

    /**
     * Gets this model element's name.
     *
     * @return The name of the model element.
     */
    @Override
    public final String getName() {
        return myName;
    }

    /**
     * Gets the a string that can be used to label the model element By default
     * it is the same as getName(), but can be changed via setStringLabel()
     *
     * @return the label
     */
    public final String getStringLabel() {
        return myLabel;
    }

    /**
     * Sets a string that can be used to label the model element
     *
     * @param label the myLabel to set
     */
    public final void setStringLabel(String label) {
        myLabel = label;
    }

    /**
     * Gets a uniquely assigned integer identifier for this model element. This
     * identifier is assigned when the model element is created. It may vary if
     * the order of creation changes.
     *
     * @return The identifier for the model element.
     */
    @Override
    public final long getId() {
        return (myId);
    }

    /**
     * Gets the current simulated time relative to the starting time of the run.
     *
     * @return The simulation time as a double
     */
    public final double getTime() {
        if (getExecutive() == null) {
            return (0.0);
        } else {
            return (getExecutive().getTime());
        }
    }

    /**
     * Returns the value of a 1 millisecond time interval in terms of the base
     * time unit
     *
     * @return the value of a 1 millisecond time interval in terms of the base
     * time unit
     */
    public final double millisecond() {
        return TIME_UNIT_MILLISECOND / getModel().getTimeUnit();
    }

    /**
     * Returns the value of a 1 second time interval in terms of the base time
     * unit
     *
     * @return Returns the value of a 1 second time interval in terms of the
     * base time unit
     */
    public final double second() {
        return TIME_UNIT_SECOND / getModel().getTimeUnit();
    }

    /**
     * Returns the value of a 1 minute time interval in terms of the base time
     * unit. For example, if the time unit is set to hours, then minute() should
     * return 0.0166 (TIME_UNIT_MINUTE/TIME_UNIT_HOUR)
     *
     * Thus, if base time unit is set to hours, then 5*minute() represents 5
     * minutes (5.0/60) and 2*day() represents 2 days. Use these methods to
     * convert units to the base time unit when scheduling events or defining
     * time parameters.
     *
     * @return Returns the value of a 1 minute time interval in terms of the
     * base time unit.
     */
    public final double minute() {
        return TIME_UNIT_MINUTE / getModel().getTimeUnit();
    }

    /**
     * Returns the value of a 1 hour time interval in terms of the base time
     * unit
     *
     * @return Returns the value of a 1 hour time interval in terms of the base
     * time unit
     */
    public final double hour() {
        return TIME_UNIT_HOUR / getModel().getTimeUnit();
    }

    /**
     * Returns the value of a 1 day time interval in terms of the base time unit
     *
     * @return Returns the value of a 1 day time interval in terms of the base
     * time unit
     */
    public final double day() {
        return TIME_UNIT_DAY / getModel().getTimeUnit();
    }

    /**
     * Returns the value of a 1 week time interval in terms of the base time
     * unit
     *
     * @return Returns the value of a 1 week time interval in terms of the base
     * time unit
     */
    public final double week() {
        return TIME_UNIT_WEEK / getModel().getTimeUnit();
    }

    /**
     * Returns a reference to the default entity type
     *
     * @return Returns a reference to the default entity type
     */
    protected EntityType getDefaultEntityType() {
        return getModel().getDefaultEntityType();
    }

    /**
     * Returns a reference to the default FIFOQueueDiscipline
     *
     * @return Returns a reference to the default FIFOQueueDiscipline
     */
    protected FIFODiscipline getDefaultFIFOQueueDiscipline() {
        return getModel().getDefaultFIFOQueueDiscipline();
    }

    /**
     * Returns a reference to the default LIFOQueueDiscipline
     *
     * @return Returns a reference to the default LIFOQueueDiscipline
     */
    protected LIFODiscipline getDefaultLIFOQueueDiscipline() {
        return getModel().getDefaultLIFOQueueDiscipline();
    }

    /**
     * Returns a reference to the default RandomQueueDiscipline
     *
     * @return Returns a reference to the default RandomQueueDiscipline
     */
    protected RandomDiscipline getDefaultRandomQueueDiscipline() {
        return getModel().getDefaultRandomQueueDiscipline();
    }

    /**
     * Returns a reference to the default RankedQueueDiscipline
     *
     * @return Returns a reference to the default RankedQueueDiscipline
     */
    protected RankedQDiscipline getDefaultRankedQueueDiscipline() {
        return getModel().getDefaultRankedQueueDiscipline();
    }

    /**
     * A convenience method for creating QObjects
     *
     * @return the created QObject with time properly set
     */
    protected final QObject createQObject() {
        return new QObject(getTime());
    }

    /**
     * Creates an instance of the DefaultEntityType
     *
     * @return an entity of the default type
     */
    protected Entity createEntity() {
        return getDefaultEntityType().createEntity();
    }

    /**
     * Creates an instance of the DefaultEntityType using the supplied name
     *
     * @param name the name for the entity
     * @return the default entity type with the supplied name
     */
    protected Entity createEntity(String name) {
        return getDefaultEntityType().createEntity(name);
    }

    /**
     * If this flag is set to true, the model element (and all its children)
     * will be automatically removed from the model before the beginning of the
     * next replication. The default is false. Setting this to true is useful if
     * model elements were added during the replication that need to be removed
     * after the replication has been completed in order to have the model have
     * the same state at the beginning of the next replication.
     *
     * @return the RemoveFromModelPriorToRepFlag
     */
    public final boolean getRemoveFromModelPriorToRepFlag() {
        return myRemoveFromModelPriorToRepFlag;
    }

    /**
     * If this flag is set to true, the model element (and all its children)
     * will be automatically removed from the model prior to the next
     * replication The default is false. Setting this to true is useful if model
     * elements were added during the replication that need to be removed after
     * the replication has been completed (prior to the next replication) in
     * order to have the model have the same configuration at the beginning of
     * the next replication.
     *
     * Calling this with true after the model element has been constructed turns
     * on the automatic removal. The automatic removal happens before each
     * replication (before beforeReplication() and initialize()) Thus,
     * statistics on the added elements can be retrieved after a replication.
     *
     * @param flag the RemoveFromModelPriorToRepFlag to set
     */
    public final void setRemoveFromModelPriorToRepFlag(boolean flag) {
        myRemoveFromModelPriorToRepFlag = flag;
    }

    /**
     * Indicates that the model element was added to the model while a
     * replication was running.
     *
     * @return true if model element was added while replication was running
     */
    public final boolean getAddedWhileReplicationWasRunningFlag() {
        return myAddedWhileReplicationWasRunningFlag;
    }

    /**
     *
     * @param flag true means added while replication was running
     */
    public final void setAddedWhileReplicationWasRunningFlag(boolean flag) {
        myAddedWhileReplicationWasRunningFlag = flag;
    }

    /**
     * Gets this model elements parent in the composite pattern, i.e. returns
     * the model element that contains this model element.
     *
     * @return The containing model element
     */
    public final ModelElement getParentModelElement() {
        return (myParentModelElement);
    }

    /**
     * Gets the before experiment flag that indicates whether or not this model
     * element will participate in the default before experiment action
     * controlled by its parent model element.
     *
     * @return True means it participates, false means it that it does not.
     */
    public final boolean getBeforeExperimentOption() {
        return (myBeforeExperimentOption);
    }

    /**
     * Sets the before experiment option for this model element.
     *
     * @param flag True means it participates.
     */
    public final void setBeforeExperimentOption(boolean flag) {
        myBeforeExperimentOption = flag;
    }

    /**
     * Sets the before experiment option of all model elements (children)
     * contained by this model element.
     *
     * @param flag True means that they participate in setup.
     */
    public final void setBeforeExperimentOptionForModelElements(boolean flag) {
        myBeforeExperimentOption = flag;
        for (ModelElement m : myModelElements) {
            m.setBeforeExperimentOptionForModelElements(flag);
        }
    }

    /**
     * Gets the after experiment flag that indicates whether or not this model
     * element will participate in the default after experiment action
     * controlled by its parent model element.
     *
     * @return True means it participates, false means it that it does not.
     */
    public final boolean getAfterExperimentOption() {
        return (myAfterExperimentOption);
    }

    /**
     * Sets the after experiment option for this model element.
     *
     * @param flag True means it participates.
     */
    public final void setAfterExperimentOption(boolean flag) {
        myAfterExperimentOption = flag;
    }

    /**
     * Sets the after experiment option of all model elements (children)
     * contained by this model element.
     *
     * @param option True means that they participate.
     */
    public final void setAfterExperimentOptionForModelElements(boolean option) {
        myAfterExperimentOption = option;
        for (ModelElement m : myModelElements) {
            m.setAfterExperimentOptionForModelElements(option);
        }
    }

    /**
     * Gets the before replication flag that indicates whether or not this model
     * element will participate in the default action controlled by its parent
     * model element.
     *
     * @return True means it participates, false means it that it does not.
     */
    public final boolean getBeforeReplicationOption() {
        return (myBeforeReplicationOption);
    }

    /**
     * Sets the before replication flag for this model element.
     *
     * @param flag True means it participates in the default action
     */
    public final void setBeforeReplicationOption(boolean flag) {
        myBeforeReplicationOption = flag;
    }

    /**
     * Sets the before replication flag of all model elements (children)
     * contained by this model element.
     *
     * @param flag True means that they participate in the default action
     */
    public final void setBeforeReplicationOptionForModelElements(boolean flag) {
        myBeforeReplicationOption = flag;
        for (ModelElement m : myModelElements) {
            m.setBeforeReplicationOptionForModelElements(flag);
        }
    }

    /**
     * Gets the monte carlo option flag that indicates whether or not this model
     * element will participate in the default action controlled by its parent
     * model element.
     *
     * @return True means it participates, false means it that it does not.
     */
    public final boolean getMonteCarloOption() {
        return (myMonteCarloOption);
    }

    /**
     * Sets the monte carlo option flag for this model element.
     *
     * @param flag True means it participates in the default action
     */
    public final void setMonteCarloOption(boolean flag) {
        myMonteCarloOption = flag;
    }

    /**
     * This flag indicates whether or not the notification of update observers
     * will occur for this model element. Update notifications can be
     * computationally intensive. By setting this flag to false, no update
     * observers will be called.
     *
     * @return Returns the updateNotificationFlag.
     */
    public final boolean getUpdateNotificationFlag() {
        return myUpdateNotificationFlag;
    }

    /**
     * This flag indicates whether or not the notification of update observers
     * will occur for this model element. Update notifications can be
     * computationally intensive. By setting this flag to false, no update
     * observers will be called.
     *
     * WARNING: Setting this flag to true on ResponseVariables and subclasses
     * will essentially turn off statistical collection since statistical
     * collection is done through update observers
     *
     * @param flag The updateNotificationFlag to set.
     */
    public final void setUpdateNotificationFlag(boolean flag) {
        myUpdateNotificationFlag = flag;
    }

    /**
     * Sets the monte carlo option flag of all model elements (children)
     * contained by this model element.
     *
     * @param flag True means that they participate in the default action
     */
    public final void setMonteCarloOptionForModelElements(boolean flag) {
        myMonteCarloOption = flag;
        for (ModelElement m : myModelElements) {
            m.setMonteCarloOptionForModelElements(flag);
        }
    }

    /**
     * Gets the initialization flag that indicates whether or not this model
     * element will participate in the default action controlled by its parent
     * model element.
     *
     * @return True means it participates, false means it that it does not.
     */
    public final boolean getInitializationOption() {
        return (myInitializationOption);
    }

    /**
     * Sets the initialization flag for this model element.
     *
     * @param flag True means it participates in the default action
     */
    public final void setInitializationOption(boolean flag) {
        myInitializationOption = flag;
    }

    /**
     * Sets the initialization option of all model elements (children) contained
     * by this model element.
     *
     * @param flag True means that they participate in the default action
     */
    public final void setInitializationOptionForModelElements(boolean flag) {
        myInitializationOption = flag;
        for (ModelElement m : myModelElements) {
            m.setInitializationOptionForModelElements(flag);
        }
    }

    /**
     * Gets the warm up flag that indicates whether or not this model element
     * will be warmed up when its parent warm up event/action occurs. The
     * default value for all model elements is true. A value of true implies
     * that the model element allows its parent's warm up event to call its warm
     * up action. A value of false implies that the model element does not allow
     * its parent's warm up event to call its warm up action. False does not
     * necessarily mean that the model element will not be warmed up. It may,
     * through the use of the setLengthOfWarmUp() method, have its own warm up
     * event and action.
     *
     * @return True means it will be warmed up by its parent model element,
     * false means it that it will not be warmed up by its parent.
     */
    public final boolean getWarmUpOption() {
        return (myWarmUpOption);
    }

    /**
     * Sets the warm up option flag for this model element. The warm up option
     * flag indicates whether or not the model element will allow its parent
     * model element to control its warm up event. That is, if the warm up
     * option flag is true, the model element does not have its own warm up
     * event and will be warmed up when its parent model element is warmed up.
     * If the warm up option flag is false, the model element will either not
     * participate at all in warm up events or will have its own warm up event
     * defined through the use of the method setLengthOfWarmUp(). Setting the
     * warm up option flag to false, without setting a warm up length for the
     * model element, implies that the model element will not react to any warm
     * up actions.
     *
     * @param warmUpFlag True represents that the model element participates.
     */
    public final void setWarmUpOption(boolean warmUpFlag) {
        myWarmUpOption = warmUpFlag;
    }

    /**
     * Sets the warm up option flag of all model elements (children) contained
     * by this model element.
     *
     * @param warmUpFlag True means that they participate in the default action
     */
    public final void setWarmUpOptionForModelElements(boolean warmUpFlag) {
        myWarmUpOption = warmUpFlag;
        for (ModelElement m : myModelElements) {
            m.setWarmUpOptionForModelElements(warmUpFlag);
        }
    }

    /**
     * Returns true if the warm up has occurred, false otherwise
     *
     * @return true if the warm up has occurred, false otherwise
     */
    public final boolean isWarmedUp() {
        return (myWarmUpIndicator);
    }

    /**
     * Gets the timed update flag that indicates whether or not this model
     * element will participate in the default action controlled by its parent
     * model element.
     *
     * @return True means it participates, false means it that it does not.
     */
    public final boolean getTimedUpdateOption() {
        return (myTimedUpdateOption);
    }

    /**
     * Sets the timed update option flag for this model element.
     *
     * @param timedUpdateOption True means it participates in the default action
     */
    public final void setTimedUpdateOption(boolean timedUpdateOption) {
        myTimedUpdateOption = timedUpdateOption;
    }

    /**
     * Sets the timed update option flag of all model elements (children)
     * contained by this model element.
     *
     * @param timedUpdateOption True means that they participate in the default
     * action
     */
    public final void setTimedUpdateOptionForModelElements(boolean timedUpdateOption) {
        myTimedUpdateOption = timedUpdateOption;
        for (ModelElement m : myModelElements) {
            m.setTimedUpdateOptionForModelElements(timedUpdateOption);
        }
    }

    /**
     * Gets the length of the warm up for this model element. Each model element
     * can have its own warm up length.
     *
     * @return the the length of the warm up
     */
    public final double getLengthOfWarmUp() {
        return (myLengthOfWarmUp);
    }

    /**
     * Sets the length of the warm up for this model element.
     *
     * Setting the length of the warm up to 0.0 will set the warm up option flag
     * to true.
     *
     * This is based on the assumption that a zero length warm up implies that
     * the model element's parent warm up event will take care of the warm up
     * action. If this is not the case, then calling setWarmUpFlag(false) after
     * setting the length of the warm up to 0.0, will cause the model element to
     * not have a warmup.
     *
     * In general, there is not a need to set the length of the warm up to zero
     * unless the user is resetting the value after explicitly specifying it for
     * a replication. The default value of the warm up length is zero. A zero
     * length warm up will not cause a separate event to be scheduled. The
     * default warm up flag option starts as true, which implies that the model
     * element lets its parent's warm up event take care of its warm up action.
     *
     * Setting the length of the warm up &gt; 0.0, will set the warm up option
     * flag to false.
     *
     * Prior to each replication the specified warm up length will be checked to
     * see if it is greater than zero. if the length of the warm up is greater
     * than zero, it is checked to see if it is less than the simulation run
     * length. If so, it is assumed that the model element wants its own warm up
     * event scheduled. It is also assumed that the model element does not
     * depend on its parent for a warm up action. The warm up option flag will
     * be set to false and a separate warm up event will be scheduled for the
     * model element.
     *
     * @param lengthOfWarmUp length of the warm up, must be &gt;= 0.0
     */
    public final void setLengthOfWarmUp(double lengthOfWarmUp) {
        if (lengthOfWarmUp < 0.0) {
            throw new IllegalArgumentException("Warmup time cannot be less than zero");
        }

        myLengthOfWarmUp = lengthOfWarmUp;
        if (myLengthOfWarmUp == 0.0) {
            setWarmUpOption(true);
        } else {
            setWarmUpOption(false);
        }
    }

    /**
     * This method returns the planned time for the warm up for this model
     * element
     *
     * @return the planned time
     */
    public final double getWarmUpEventTime() {

        ModelElement m = this;
        double time = 0.0;

        while (m != null) {
            if (m.isWarmUpEventScheduled() == true) {
                // element has its own warm up event
                time = m.getLengthOfWarmUp();
                break;
            }
            // does not have its own warm up event
            if (m.getWarmUpOption() == false) {
                // and doesn't listen to a parent
                time = 0.0;
                break;
            }

            m = m.getParentModelElement(); // get the parent
        }

        return (time);

    }

    /**
     * Checks if a warm up event has been scheduled for this model element
     *
     * @return True means that it has been scheduled.
     */
    public final boolean isWarmUpEventScheduled() {
        if (myWarmUpEvent == null) {
            return (false);
        } else {
            return (myWarmUpEvent.isScheduled());
        }
    }

    /**
     * Checks if a warm up event is scheduled for any model element directly
     * above this model element in the hierarchy of model elements all the way
     * until the top Model.
     *
     * True means that some warm up event is scheduled in the upward chain.
     * False means that no warm up event is scheduled in the upward chain.
     *
     * @return true if any warm up event is scheduled in the upward chain
     */
    public final boolean isWarmUpScheduled() {
        // if this model element doesn’t schedule the warm up
        // check if it’s parent does, and so on, until
        // reaching the Model
        if (!isWarmUpEventScheduled()) {
            // if it has a parent check it
            if (getParentModelElement() != null) {
                return getParentModelElement().isWarmUpScheduled();
            } else {
                // only Model has no parent to check
                // stop checking
            }
        }
        // current element has warm up scheduled, return that fact
        return true;
    }

    /** Find the first parent that has its own warm up event
     * this guarantees that all elements below the found model element
     * do not have their own warm up event. A model element that
     * has its own warm up event also opts out of the warm up action.
     * If the returned parent is the Model, then all are controlled
     * by the model (unless they opt out).  Elements can opt out
     * and not have their own warm up event. Thus, they have no
     * warm up at all.
     * 
     * Null indicates that no model element in the parent chain
     * has a warm up event.
     * 
     * @return the element or null
     */
    public final ModelElement findModelElementWithWarmUpEvent() {
        // if this model element doesn’t schedule the warm up
        // check if it’s parent does, and so on, until
        // reaching the Model
        if (!isWarmUpEventScheduled()) {
            // doesn't have a warm up event
            if (getParentModelElement() != null) {
                // check if parent exists and has a warm up event
                return getParentModelElement().findModelElementWithWarmUpEvent();
            } else {
                // parent does not exist, and there is no warm up event
                return null;
            }
        } else {// has a warm up event, return the model element
            return this;
        }
    }

    /**
     * Checks if this model element or any model element directly above this
     * model element in the hierarchy of model elements all the way until the
     * top Model participates in the warm up action.
     *
     * True means that this and every parent in the chain participates in the
     * warm up action. False means this element or some parent does not
     * participate in the warm up action
     *
     * @return true if this and every parent participates in the warm up action
     */
    public final boolean checkWarmUpOption() {
        // if this model element participates in the warm up
        // check if it’s parent does, and so on, until
        // reaching the Model
        if (getWarmUpOption()) {
            // if it has a parent check it
            if (getParentModelElement() != null) {
                return getParentModelElement().checkWarmUpOption();
            } else {
                // only Model has no parent to check
                // stop checking
            }
        }
        // current element does not participate, return that fact
        return false;
    }

    /**
     * Cancels the warm up event for this model element.
     */
    public final void cancelWarmUpEvent() {
        if (myWarmUpEvent != null) {
            getExecutive().cancel(myWarmUpEvent);
        }
    }

    /**
     * Gets the time between timed update events for this model element. If T is
     * the length of the run, and n is the number of timed updates, then the
     * timed update interval is T/n
     *
     * @return The time between timed update events.
     */
    public final double getTimedUpdateInterval() {
        return (myTimedUpdateInterval);
    }

    /**
     * Used to set the length of the timed update interval.
     *
     * @param deltaT timed update interval, must be &gt;= 0.0
     */
    public final void setTimedUpdateInterval(double deltaT) {
        if (deltaT <= 0.0) {
            throw new IllegalArgumentException("Timed Update interval cannot be less than or equal to zero");
        }

        myTimedUpdateInterval = deltaT;
    }

    /**
     * Checks if a timed update event has been scheduled for this model element
     *
     * @return True means that it has been scheduled.
     */
    public final boolean isTimedUpdateEventScheduled() {
        if (myTimedUpdateEvent == null) {
            return (false);
        } else {
            return (myTimedUpdateEvent.isScheduled());
        }
    }

    /**
     * Cancels the timed update event for this model element.
     */
    public final void cancelTimedUpdateEvent() {
        if (myTimedUpdateEvent != null) {
            getExecutive().cancel(myTimedUpdateEvent);
        }
    }

    /**
     * Gets the after replication flag that indicates whether or not this model
     * element will participate in the default action controlled by its parent
     * model element.
     *
     * @return True means it participates, false means it that it does not.
     */
    public final boolean getAfterReplicationOption() {
        return (myAfterReplicationOption);
    }

    /**
     * Sets the after replication flag for this model element.
     *
     * @param flag True means it participates in the default action
     */
    public final void setAfterReplicationOption(boolean flag) {
        myAfterReplicationOption = flag;
    }

    /**
     * Sets the after replication flag of all model elements (children)
     * contained by this model element.
     *
     * @param flag True means that they participate in the default action
     */
    public final void setAfterReplicationOptionForModelElements(boolean flag) {
        myAfterReplicationOption = flag;
        for (ModelElement m : myModelElements) {
            m.setAfterReplicationOptionForModelElements(flag);
        }
    }

    /**
     * Gets the end replication flag that indicates whether or not this model
     * element will participate in the default action controlled by its parent
     * model element. Determines whether or not the replicationEnded() method
     * will be called
     *
     * @return True means it participates, false means it that it does not.
     */
    public final boolean getReplicationEndedOption() {
        return (myReplicationEndedOption);
    }

    /**
     * Sets the end replication option flag for this model element. Determines
     * whether or not the replicationEnded() method will be called
     *
     * @param flag True means it participates in the default action
     */
    public final void setReplicationEndedOption(boolean flag) {
        myReplicationEndedOption = flag;
    }

    /**
     * Sets the end replication option flag of all model elements (children)
     * contained by this model element. Determines whether or not the
     * replicationEnded() method will be called
     *
     * @param flag True means that they participate in the default action
     */
    public final void setReplicationEndedOptionForModelElements(boolean flag) {
        myReplicationEndedOption = flag;
        for (ModelElement m : myModelElements) {
            m.setReplicationEndedOptionForModelElements(flag);
        }
    }

    /**
     * Gets the number of model elements contained by this model elements.
     *
     * @return a count of the number of child elements.
     */
    public final int getNumberOfModelElements() {
        return (myModelElements.size());
    }

    /**
     * Gets an iterator to the contained model elements.
     *
     * @return an iterator over the child elements.
     */
    public final Iterator<ModelElement> getChildModelElementIterator() {

        return (myModelElements.iterator());
    }

    @Override
    public void deleteObservers() {
        myObservableComponent.deleteObservers();
    }

    @Override
    public void deleteObserver(ObserverIfc observer) {
        myObservableComponent.deleteObserver(observer);
    }

    @Override
    public boolean contains(ObserverIfc observer) {
        return myObservableComponent.contains(observer);
    }

    @Override
    public int countObservers() {
        return myObservableComponent.countObservers();
    }

    @Override
    public void addObserver(ObserverIfc observer) {
        myObservableComponent.addObserver(observer);
    }

    /**
     * Adds an observer to this model element. Every model element implements
     * the Observer interface and thus can be observed at particular state
     * actions.
     *
     * @param o observer to be added.
     */
    public final void addObserverToModelElements(ObserverIfc o) {
        myObservableComponent.addObserver(o);
        for (ModelElement m : myModelElements) {
            m.addObserverToModelElements(o);
        }
    }

    /**
     * Removes the observer from this model element.
     *
     * @param o The observer to be removed.
     */
    public final void deleteObserverFromModelElements(ObserverIfc o) {
        myObservableComponent.deleteObserver(o);
        for (ModelElement m : myModelElements) {
            m.deleteObserverFromModelElements(o);
        }
    }

    /**
     * Returns the name of the class and the model element's name. This may be
     * embellished in the future.
     *
     * @return A string representing this model element.
     */
    @Override
    public String toString() {
        return (this.getClass().getName() + "\t" + getName());
    }

    /**
     * Returns an integer representing the state of the model element This can
     * be used by Observers to find out which action occurred for the model
     * element
     *
     * @return The state of the model element
     */
    public final int getObserverState() {
        return (myObserverState);
    }

    /**
     * Returns an integer representing the previous state of the model element
     * This can be used by Observers to find out which action occurred prior to
     * the current state change for the model element
     *
     * @return The state of the model element
     */
    public final int getPreviousObserverState() {
        return (myPreviousObserverState);
    }

    /**
     * Checks to see if the model element is in the given observer state. This
     * method can be used by observers that are interested in reacting to the
     * action associated with this state for the model element.
     *
     * BEFORE_EXPERIMENT	beforeExperiment() INITIALIZED	initialize()
     * BEFORE_REPLICATION	beforeReplication() MONTE_CARLO	monte carlo()
     * REPLICATION_ENDED	replicationEnded() AFTER_REPLICATION	afterReplication()
     * UPDATE	update() WARMUP	warmUp() TIMED_UPDATE	timedUpdate()
     * AFTER_EXPERIMENT	afterExperiment() BATCH	batch()
     * CONDITIONAL_ACTION_REGISTRATION registerConditionalActions()
     *
     * @param observerState The state to check
     * @return True means that this model element's corresponding action method
     * has been invoked and that it is in the given state.
     */
    public final boolean checkObserverState(int observerState) {
        return (myObserverState == observerState);
    }

    /**
     * Checks to see if the model element is in the setup state. That is, has
     * the setup action been invoked. This method can be used by observers that
     * are interested in reacting to the setup action for the model element.
     *
     * @return True means that this model element's setup method has been
     * invoked and that it is in the setup state.
     */
    public final boolean checkForBeforeExperiment() {
        return (checkObserverState(BEFORE_EXPERIMENT));
    }

    /**
     * Checks to see if the model element is in the initialize state. That is,
     * has the initialize action been invoked. This method can be used by
     * observers that are interested in reacting to the initialize action for
     * the model element.
     *
     * @return True means that this model element's initialize method has been
     * invoked and that it is in the initialized state.
     */
    public final boolean checkForInitialize() {
        return (checkObserverState(INITIALIZED));
    }

    /**
     * Checks to see if the model element is in the monte carlo state. That is,
     * has the monte carlo action been invoked. This method can be used by
     * observers that are interested in reacting to the monte carlo action for
     * the model element.
     *
     * @return True means that this model element's monte carlo method has been
     * invoked and that it is in the monte carlo state.
     */
    public final boolean checkForMonteCarlo() {
        return (checkObserverState(MONTE_CARLO));
    }

    /**
     * Checks to see if the model element is in the before replication state.
     * That is, has the before replication action been invoked. This method can
     * be used by observers that are interested in reacting to the before
     * replication action for the model element.
     *
     * @return True means that this model element's before replication method
     * has been invoked and that it is in the before replication state.
     */
    public final boolean checkForBeforeReplication() {
        return (checkObserverState(BEFORE_REPLICATION));
    }

    /**
     * Checks to see if the model element is in the after replication state.
     * That is, has the after replication action been invoked. This method can
     * be used by observers that are interested in reacting to the after
     * replication action for the model element.
     *
     * @return True means that this model element's after replication method has
     * been invoked and that it is in the after replication state.
     */
    public final boolean checkForAfterReplication() {
        return (checkObserverState(AFTER_REPLICATION));
    }

    /**
     * Checks to see if the model element is in the replication ended state.
     * That is, has the replication ended action been invoked. This method can
     * be used by observers that are interested in reacting to the replication
     * ended action for the model element.
     *
     * @return True means that this model element's replicationEnded() method
     * has been invoked and that it is in the replication ended state.
     */
    public final boolean checkForReplicationEnded() {
        return (checkObserverState(REPLICATION_ENDED));
    }

    /**
     * Checks to see if the model element is in the update state. That is, has
     * the notifyUpdateObservers been invoked. This method can be used by
     * observers that are interested in reacting to the update action for the
     * model element.
     *
     * @return True means that this model element is in the update state.
     */
    public final boolean checkForUpdate() {
        return (checkObserverState(UPDATE));
    }

    /**
     * Checks to see if the model element is in the warm up state. That is, has
     * the warm up action been invoked. This method can be used by observers
     * that are interested in reacting to the warm up action for the model
     * element.
     *
     * @return True means that this model element has experienced a warm up
     * action
     */
    public final boolean checkForWarmUp() {
        return (checkObserverState(WARMUP));
    }

    /**
     * Checks to see if the model element is in the timed update state. That is,
     * has the timed update action been invoked. This method can be used by
     * observers that are interested in reacting to the timed update action for
     * the model element.
     *
     * @return True means that this model element has experienced a timed update
     * action
     */
    public final boolean checkForTimedUpdate() {
        return (checkObserverState(TIMED_UPDATE));
    }

    /**
     * Checks to see if the model element is in the end simulation state. That
     * is, has the end simulation action been invoked. This method can be used
     * by observers that are interested in reacting to the end simulation action
     * for the model element.
     *
     * @return True means that this model element has experienced an end of
     * simulation action
     */
    public final boolean checkForAfterExperiment() {
        return (checkObserverState(AFTER_EXPERIMENT));
    }

    /**
     * Checks to see if the model element just been removed from the model This
     * method can be used by observers that are interested in reacting to when a
     * model element is removed from a model
     *
     * @return True means that this model element has just been removed from a
     * model
     */
    public final boolean checkForRemoveFromModel() {
        return (checkObserverState(REMOVED_FROM_MODEL));
    }

    /**
     * Checks to see if the model element has just called,
     * registerConditionalActions()
     *
     * @return True means that this model element has just called
     * registerConditionalActions()
     */
    public final boolean checkForConditionalActionRegistration() {
        return (checkObserverState(CONDITIONAL_ACTION_REGISTRATION));
    }

    /**
     * Gets the variable with the name given by the string provided
     *
     * @param variableKey the name of the variable
     * @return The Variable with the given name
     */
    public Variable getVariable(String variableKey) {
        return (getModel().getVariable(variableKey));
    }

    /**
     * @param observerState The myObserverState to set.
     */
    protected final void setObserverState(int observerState) {
        myPreviousObserverState = myObserverState;
        myObserverState = observerState;
    }

    /**
     * Fills a StringBuilder with the model element names in the order that they
     * will be initialized
     *
     * @param sb the StringBuilder to fill
     */
    public final void getInitializationOrderAsString(StringBuilder sb) {

        if (!myModelElements.isEmpty()) { // I have elements, so check them
            for (ModelElement m : myModelElements) {
                m.getInitializationOrderAsString(sb);
            }
        }

        sb.append(getName());
        sb.append("\n");
    }

    /**
     * Fills up the provided collection with all of the response variables that
     * are contained by any model elements within this model element. In other
     * words, any response variables that are in the model element hierarchy
     * below this model element.
     *
     * @param c The collection to be filled.
     */
    protected final void getAllResponseVariables(Collection<ResponseVariable> c) {

        if (!myModelElements.isEmpty()) { // I have elements, so check them
            for (ModelElement m : myModelElements) {
                m.getAllResponseVariables(c);
            }
        }

        // check if I'm a response variable, if so add me
        if (this instanceof ResponseVariable) {
            c.add((ResponseVariable) this);
        }

    }

    /**
     * Fills up the provided collection with the response variables that are
     * contained only by this model element
     *
     * @param c The collection to be filled.
     */
    protected final void getThisElementsResponseVariables(Collection<ResponseVariable> c) {

        if (!myModelElements.isEmpty()) { // I have elements, so check them
            for (ModelElement m : myModelElements) {
                if (m instanceof ResponseVariable) {
                    c.add((ResponseVariable) m);
                }
            }
        }
    }

    /**
     * Fills up the provided collection with all of the Counters that are
     * contained by any model elements within this model element. In other
     * words, any Counters that are in the model element hierarchy below this
     * model element.
     *
     * @param c The collection to be filled.
     */
    protected final void getAllCounters(Collection<Counter> c) {

        if (!myModelElements.isEmpty()) { // I have elements, so check them
            for (ModelElement m : myModelElements) {
                m.getAllCounters(c);
            }
        }

        // check if I'm a Counter, if so add me
        if (this instanceof Counter) {
            c.add((Counter) this);
        }

    }

    /**
     * Fills up the provided collection with the Counters that are contained
     * only by this model element
     *
     * @param c The collection to be filled.
     */
    protected final void getThisElementsCounters(Collection<Counter> c) {

        if (!myModelElements.isEmpty()) { // I have elements, so check them
            for (ModelElement m : myModelElements) {
                if (m instanceof Counter) {
                    c.add((Counter) m);
                }
            }
        }
    }

    /**
     * Fills up the provided collection with all of the RandomElementIfc and
     * subclasses of RandomElementIfc that are contained by any model elements
     * within this model element. In other words, any RandomElementIfc that are
     * in the model element hierarchy below this model element.
     *
     * @param c The collection to be filled.
     */
    protected final void getAllRandomElements(Collection<RandomElementIfc> c) {

        if (!myModelElements.isEmpty()) { // I have elements, so check them
            for (ModelElement m : myModelElements) {
                m.getAllRandomElements(c);
            }
        }

        //	check if I'm a random variable, if so add me
        if (this instanceof RandomElementIfc) {
            c.add((RandomElementIfc) this);
        }
    }

    /**
     * Fills up the provided collection with only the random variables
     * associated with this element
     *
     * @param c The collection to be filled.
     */
    protected final void getThisElementsRandomVariables(Collection<RandomVariable> c) {
        if (!myModelElements.isEmpty()) { // I have elements, so check them
            for (ModelElement m : myModelElements) {
                if (m instanceof RandomVariable) {
                    c.add((RandomVariable) m);
                }
            }
        }
    }

    /**
     * Fills up the provided collection with only the variables associated with
     * this element
     *
     * @param c The collection to be filled.
     */
    protected final void getThisElementsVariables(Collection<Variable> c) {
        if (!myModelElements.isEmpty()) { // I have elements, so check them
            for (ModelElement m : myModelElements) {
                if (m instanceof Variable) {
                    if (Variable.class == this.getClass()) {
                        c.add((Variable) this);
                    }
                }
            }
        }
    }

    /**
     * Fills up the provided collection with all of the variables that are
     * contained by any model elements within this model element. In other
     * words, any variables that are in the model element hierarchy below this
     * model element.
     *
     * @param c The collection to be filled.
     */
    protected final void getAllVariables(Collection<Variable> c) {

        if (!myModelElements.isEmpty()) { // I have elements, so check them
            for (ModelElement m : myModelElements) {
                m.getAllVariables(c);
            }
        }

        //	check if I'm a variable, if so add me
        if (this instanceof Variable) {
            if (Variable.class == this.getClass()) {
                c.add((Variable) this);
            }
        }
    }

    /**
     * Fills the supplied collection with any model elements below this element
     * and including this element that are marked for removal. Children of
     * elements marked for removal are not added to the collection since their
     * parent is already marked.
     *
     * @param c the collection
     */
    protected final void getAllElementsNeedingRemoval(Collection<ModelElement> c) {

        // if this element is marked for removal, its children do not need
        // to be checked since they will be removed when their parent (this element)
        // is removed
        if (getRemoveFromModelPriorToRepFlag()) {
            c.add(this);
            return;
        } else {
            if (myModelElements.isEmpty()) // no children to check
            {
                return;
            }
            // this element is not marked but some of it's children may be marked
            // check them
            for (ModelElement m : myModelElements) {
                m.getAllElementsNeedingRemoval(c);
            }
        }
    }

    /**
     * This method should be implemented by subclasses that need to use Controls
     * prior to an experiment being run. The instance variable myControls should
     * be set by a client and then can be used in this method to properly assign
     * the values from the controls to the inner state of the model element
     *
     */
    protected void useControls() {
    }

    /**
     * This method should be overridden by subclasses that need logic to be
     * performed prior to an experiment. The beforeExperiment method allows
     * model elements to be setup prior to the first replication within an
     * experiment. It is called once before any replications occur.
     */
    protected void beforeExperiment() {
    }

    /**
     * This method should be overridden by subclasses that need actions
     * performed to initialize prior to a replication. It is called once before
     * each replication occurs if the model element wants initialization. It is
     * called after beforeReplication() is called
     */
    protected void initialize() {
    }

    /**
     * This method should be overridden by subclasses that need to register
     * conditional actions prior to a replication. It is called once before each
     * replication, right after the initialize() method is called.
     *
     * @param e provides access to the executive
     */
    protected void registerConditionalActions(Executive e) {
    }

    /**
     * This method should be overridden by subclasses that need actions
     * performed prior to each replication. It is called prior to each
     * replication and can be used to initialize the model element. It is called
     * before initialize() is called.
     */
    protected void beforeReplication() {
    }

    /**
     * This method should be overridden by subclasses that need actions
     * performed after before replication. It is called after beforeReplication
     * but prior to afterReplication() and can be used to perform pure
     * monte-carlo (non event type) simulations with the model element
     */
    protected void montecarlo() {
    }

    /**
     * This method should be overridden by subclasses that need actions
     * performed at the warm up event during each replication. It is called once
     * during each replication if the model element reacts to warm up actions.
     */
    protected void warmUp() {
    }

    /**
     * This method should be overridden by subclasses that need actions
     * performed at each timed update event during each replication. It is
     * called for each timed update during each replication if the model element
     * reacts to timed update actions.
     */
    protected void timedUpdate() {
    }

    /**
     * This method should be overridden by subclasses that need actions
     * performed when the replication ends and prior to the calling of
     * afterReplication() . It is called when each replication ends and can be
     * used to collect data from the the model element, etc.
     */
    protected void replicationEnded() {
    }

    /**
     * This method should be overridden by subclasses that need actions
     * performed after each replication. It is called after replicationEnded()
     * has been called.
     */
    protected void afterReplication() {
    }

    /**
     * This method should be overridden by subclasses that need actions
     * performed after an experiment has been completed It is called after all
     * replications are done and can be used to collect data from the the model
     * element, etc.
     */
    protected void afterExperiment() {
    }

    /**
     * This method should be overridden by subclasses that need actions
     * performed when a model element is removed from a model
     */
    protected void removedFromModel() {
    }

    /**
     * Used to notify observers that this model element has entered the given
     * state. Valid values for observerState include:
     *
     * BEFORE_EXPERIMENT INITIALIZED BEFORE_REPLICATION MONTE_CARLO
     * REPLICATION_ENDED AFTER_REPLICATION UPDATE WARMUP TIMED_UPDATE
     * AFTER_EXPERIMENT CONDITIONAL_ACTION_REGISTRATION
     *
     * @param observerState the state as an integer
     * @param arg an object for notification
     */
    protected final void notifyObservers(int observerState, Object arg) {
        setObserverState(observerState);
        myObservableComponent.notifyObservers(this, arg);
    }

    protected final void notifyObservers(int observerState) {
        notifyObservers(observerState, null);
    }

    /**
     * The method is used to notify observers that this model element has
     * entered the setup state.
     */
    protected final void notifyBeforeExperimentObservers() {
        notifyObservers(BEFORE_EXPERIMENT);
    }

    /**
     * The method is used to notify observers that this model element has
     * entered the before replication state.
     */
    protected final void notifyBeforeReplicationObservers() {
        notifyObservers(BEFORE_REPLICATION);
    }

    /**
     * The method is used to notify observers that this model element has
     * entered the initialized state.
     */
    protected final void notifyInitializationObservers() {
        notifyObservers(INITIALIZED);
    }

    /**
     * The method is used to notify observers that this model element has
     * entered the monte carlo state.
     */
    protected final void notifyMonteCarloObservers() {
        notifyObservers(MONTE_CARLO);
    }

    /**
     * The method is used to notify observers that this model element has
     * entered the after replication state.
     */
    protected final void notifyReplicationEndedObservers() {
        notifyObservers(REPLICATION_ENDED);
    }

    /**
     * The method is used to notify observers that this model element has
     * entered the after replication state.
     */
    protected final void notifyAfterReplicationObservers() {
        notifyObservers(AFTER_REPLICATION);
    }

    /**
     * The method is used to notify observers that this model element has
     * entered the warmup update state.
     */
    protected final void notifyWarmUpObservers() {
        notifyObservers(WARMUP);
    }

    /**
     * The method is used to notify observers that this model element has
     * entered the timed update state.
     */
    protected final void notifyTimedUpdateObservers() {
        notifyObservers(TIMED_UPDATE);
    }

    /**
     * The method is used to notify observers that this model element has been
     * updated.
     */
    protected void notifyUpdateObservers() {
        if (myUpdateNotificationFlag == true) {
            notifyObservers(UPDATE);
        }
    }

    /**
     * This method is used to notify observers that this model element has
     * entered the end of simulation state.
     */
    protected final void notifyAfterExperimentObservers() {
        notifyObservers(AFTER_EXPERIMENT);
    }

    /**
     * This method is used to notify observers that this model element has been
     * removed from a model
     */
    protected final void notifyRemovingFromModelObservers() {
        notifyObservers(REMOVED_FROM_MODEL);
    }

    /**
     * This method is used to notify observers that this model element has
     * called registerConditionalActions()
     */
    protected final void notifyConditionalActionRegistrationObservers() {
        notifyObservers(CONDITIONAL_ACTION_REGISTRATION);
    }

    /**
     * Causes the model element to delete any observers that had been added
     *
     */
    protected final void clearModelElementObservers() {
        this.deleteObservers();
    }

    /**
     * The beforeExperiment_ method allows model elements to be setup prior to
     * the first replication within an experiment. It is called once before any
     * replications occur within the experiment. This method ensures that each
     * contained model element has its beforeExperiment method called and that
     * any observers will be notified of this action
     */
    protected final void beforeExperiment_() {

        myWarmUpIndicator = false;

        if (!myModelElements.isEmpty()) {
            for (ModelElement m : myModelElements) {
                m.beforeExperiment_();
            }
        }

        if (myControls != null) {
            useControls();
        }

        if (getBeforeExperimentOption()) {
            beforeExperiment();
            notifyBeforeExperimentObservers();
        }
    }

    /**
     * The beforeReplication_ method is called before each replication. This
     * method ensures that each contained model element is initialized and
     * performs default actions for all model elements.
     */
    protected final void beforeReplication_() {

        if (getLengthOfWarmUp() > 0) {
            // the warm up period is > 0, ==> element wants a warm up event
            myWarmUpActionListener = new WarmUpEventAction();
            myWarmUpEvent = getExecutive().scheduleEvent(myWarmUpActionListener, getLengthOfWarmUp(),
                    getName() + " Warm Up", myWarmUpPriority, null);
            myWarmUpEvent.setModelElement(this);
            setWarmUpOption(false); // no longer depends on parent's warm up
        }

        if (getTimedUpdateInterval() > 0) {
            // the timed update is > 0, ==> element wants a timed update event
            // schedule the timed update event
            myTimedUpdateActionListener = new TimedUpdateEventAction();
            myTimedUpdateEvent = getExecutive().scheduleEvent(myTimedUpdateActionListener, getTimedUpdateInterval(),
                    getName() + " TimedUpdate", myTimedUpdatePriority, null);
            myTimedUpdateEvent.setModelElement(this);
        }

        if (!myModelElements.isEmpty()) {
            for (ModelElement m : myModelElements) {
                m.beforeReplication_();
            }
        }

        if (getBeforeReplicationOption()) {
            beforeReplication();
            notifyBeforeReplicationObservers();
        }

    }

    /**
     * The initialize_ method allows model elements to be initialized to a
     * standard user defined state. It is called by default before each
     * replication
     *
     * This method ensures that each contained model element has its initialize
     * method called and that any observers will be notified of this action
     */
    protected void initialize_() {

        // first initialize any children associated with this model element
        if (!myModelElements.isEmpty()) {
            for (ModelElement m : myModelElements) {
                m.initialize_();
            }
        }

        // now initialize the model element itself
        if (getInitializationOption()) {
            initialize();
            notifyInitializationObservers();
        }

    }

    /**
     * The registerConditionalActions_ method allows model elements to be
     * register any conditional actions after initialization.
     *
     * It is called by default before each replication, right after the
     * initialize() method is invoked
     *
     * This method ensures that each contained model element has its
     * registerConditionalActions() method called and that any observers will be
     * notified of this action
     *
     * @param e provides access to the Executive
     */
    protected final void registerConditionalActions_(Executive e) {

        // first initialize any children associated with this model element
        if (!myModelElements.isEmpty()) {
            for (ModelElement m : myModelElements) {
                m.registerConditionalActions_(e);
            }
        }

        registerConditionalActions(e);
        notifyConditionalActionRegistrationObservers();

    }

    /**
     * The monte carlo_ method facilitates model elements to perform a monte
     * carlo simulation with no events being called. It is called by default
     * after beginReplication_() and initialize_().
     *
     * This method ensures that each contained model element has its monte carlo
     * method called and that any observers will be notified of this action
     */
    protected final void montecarlo_() {

        if (getMonteCarloOption()) {
            montecarlo();
            notifyMonteCarloObservers();
        }

        if (!myModelElements.isEmpty()) {
            for (ModelElement m : myModelElements) {
                m.montecarlo_();
            }
        }

    }

    /**
     * The warmUp_ method is called once during each replication. This method
     * ensures that each contained model element that requires a warm up action
     * will performs its actions.
     */
    protected void warmUp_() {

        // if we get here the warm up was scheduled, so do it
        warmUp();
        myWarmUpIndicator = true;
        notifyWarmUpObservers();

        // warm up the children that need it
        if (!myModelElements.isEmpty()) {
            for (ModelElement m : myModelElements) {
                if (m.getWarmUpOption() == true) {
                    m.warmUp_();
                }
            }
        }
    }

    /**
     * The timedUpdate_ method is called multiple times during each replication.
     * This method ensures that each contained model element that requires a
     * timed update action will performs its actions.
     */
    protected final void timedUpdate_() {

        if (getTimedUpdateOption()) {
            timedUpdate();
            notifyTimedUpdateObservers();
        }

        if (!myModelElements.isEmpty()) {
            for (ModelElement m : myModelElements) {
                if (!m.isTimedUpdateEventScheduled()) {
                    m.timedUpdate_();
                }
            }
        }
    }

    /**
     * The replicationEnded_ method is called when a replication ends This
     * method ensures that each contained model element that requires a end of
     * replication action will performs its actions.
     */
    protected final void replicationEnded_() {

        if (!myModelElements.isEmpty()) {
            for (ModelElement m : myModelElements) {
                m.replicationEnded_();
            }
        }

        if (getReplicationEndedOption()) {
            replicationEnded();
            notifyReplicationEndedObservers();
        }

    }

    /**
     * The afterReplication_ method is called at the end of each replication.
     * This method ensures that each contained model element that requires a end
     * of replication action will performs its actions.
     */
    protected final void afterReplication_() {

        if (!myModelElements.isEmpty()) {
            for (ModelElement m : myModelElements) {
                m.afterReplication_();
            }
        }

        if (getAfterReplicationOption()) {
            afterReplication();
            notifyAfterReplicationObservers();
        }

    }

    /**
     * The afterExperiment_ method is called after all replications are
     * completed for an experiment. This method ensures that each contained
     * model element that requires an action at the end of an experiment will
     * perform its actions.
     */
    protected final void afterExperiment_() {

        if (!myModelElements.isEmpty()) {
            for (ModelElement m : myModelElements) {
                m.afterExperiment_();
            }
        }

        if (getAfterExperimentOption()) {
            afterExperiment();
            notifyAfterExperimentObservers();
        }

    }

    /**
     * The update method can be called at user defined points to indicate that
     * the model element has been changed in some fashion that the update
     * observers need notification. This method ensures that each contained
     * model element that requires an update action will performs its actions.
     */
    protected final void update() {

        notifyUpdateObservers();

        if (!myModelElements.isEmpty()) {
            for (ModelElement m : myModelElements) {
                m.update();
            }
        }
    }

    /**
     * Gets the main model. The container of all model elements.
     *
     * @return The main model.
     */
    public final Model getModel() {
        return (myModel);
    }

    /**
     * Returns the current replication number.
     *
     * @return the current replication number.
     */
    public final int getCurrentReplicationNumber() {
        if (getExperiment() == null) {
            return 0;
        }
        return getExperiment().getCurrentReplicationNumber();
    }

    /**
     * Gets the spatial model that this ModelElement is currently using if it
     * exists
     *
     * @return the spatial model that this ModelElement is currently using if it
     * exists
     */
    public final SpatialModel getSpatialModel() {
        return mySpatialModel;
    }

    /**
     * Sets the spatial model for this ModelElement Note: Any ModelElements that
     * are children of this ModelElement do not have their SpatialModels
     * changed.
     *
     * @param spatialModel the spatial model for this ModelElement
     */
    protected final void setSpatialModel(SpatialModel spatialModel) {
        if (spatialModel == null) {
            throw new IllegalArgumentException("Tried to set the spatial model to null.");
        }
        mySpatialModel = spatialModel;
    }

    /**
     * Returns a reference to the Executive or null. The reference to the
     * Executive will only be available after the Simulation is initialized.
     *
     * @return a reference to the Executive or null.
     */
    public Executive getExecutive() {
        return getModel().getExecutive();
    }

    /**
     * Tells the Executive to stop executing events
     *
     * @param msg a stopping message
     */
    public final void stopExecutive(String msg) {
        getExecutive().stop(msg);
    }

    /**
     * Tells the Executive to stop executing events
     *
     */
    public final void stopExecutive() {
        getExecutive().stop(null);
    }

    /**
     * Returns a reference to the Experiment or null. The reference to the
     * Experiment will only be available after the Simulation is initialized.
     *
     * @return a reference to the Experiment or null.
     */
    public ExperimentGetIfc getExperiment() {
        return getModel().getExperiment();
    }

    /**
     * Returns a reference to the Simulation or null. The reference to the
     * Simulation will only be available after the Simulation is initialized.
     *
     * @return a reference to the Simulation or null
     */
    public Simulation getSimulation() {
        return getModel().getSimulation();
    }

    /**
     * Sets the model attribute for this element
     *
     * @param model The model for this element
     */
    protected final void setModel(Model model) {
        myModel = model;
    }

    /**
     * Sets the parent element to the supplied value
     *
     * @param parent the parent element to the supplied value
     */
    protected final void setParentModelElement(ModelElement parent) {
        //  a null for modelElement means no parent
        myParentModelElement = parent;
    }

    /**
     * This method is called from the constructor of a ModelElement. The
     * constructor of a ModelElement uses the passed in parent ModelElement to
     * call this method on the parent ModelElement in order to add itself as a
     * child element on the parent The modelElement's parent will be set to this
     * element's parent
     *
     * @param modelElement the model element to be added.
     */
    private void addModelElement(ModelElement modelElement) {

        // add the model element to the list of children
        myModelElements.add(modelElement);

        // set it's parent to this element
        modelElement.setParentModelElement(this);

    }

    /**
     * Changes the parent model element for this model element to the supplied
     * value. This will
     *
     * 1) remove this element from its parent's list of child model elements 2)
     * add this element to its new parent's list of child model elements 3)
     * ensure that the moved model elements are all in the same model 4) return
     * the model element's old parent
     *
     * @param newParent The new parent for this model element
     * @return The old (previous) parent for this model element
     */
    public final ModelElement changeParentModelElement(ModelElement newParent) {
        // get the current parent
        ModelElement oldParent = getParentModelElement();
        if (newParent != oldParent) {
            // remove this model element from its current parent's list of children
            oldParent.removeModelElement(this);
            // remove this model element from its current Model's ModelElementMap
            getModel().removeFromModelElementMap(this);
            // add this model element to its new parent
            newParent.addModelElement(this);
            // get its parent's model
            Model m = newParent.getModel();
            // tell its new model to add it and it's children to the Model's model element map
            m.addModelElementAndChildrenToModelElementMap(this);
        }
        return (oldParent);
    }

    /**
     * Recursively removes this model element and the children of this model
     * element and all their children, etc. The children will no longer have a
     * parent and will no longer have a model.
     *
     * It has very serious side-effects. After invoking this method:
     *
     * 1) All children of this model element will have been removed from the
     * model 2) This model element will be removed from its parent's model
     * element list and from the model. The getParentModelElement() method will
     * return null 3) This model element and all its children will no longer be
     * connected In other words, there is no longer a parent/child relationship
     * between this model element and its former children. 4) This model element
     * and all of its children will no longer belong to a model Their getModel()
     * method will return null 5) The removed elements are no longer part of
     * their former model's model element map 6) If there is an individual timed
     * update event or a warm up event scheduled for this model element or any
     * of its children, the events are canceled 7) The name and label are set to
     * null 8) Warm up and timed update listeners are set to null 9) Any
     * reference to a spatial model is set to null 10) All observers of this
     * model element are detached 11) All child model elements are removed. It
     * will no longer have any children
     *
     * If this method is used during the replication, then be aware that any
     * events scheduled by this model element (or any of its children) are still
     * going to be executed whether or not the model element is still in the
     * model. This may cause uncaught errors. The user is responsible for
     * handling these situations on a case by case basis.
     *
     * Since it has been removed from the model, it and its children will no
     * longer participate in any of the standard model element actions, e.g.
     * initialize(), afterReplication(), etc..
     *
     * Notes: 1) This method removes from the list of model elements. Thus, if a
     * client attempts to use this method,via code that is iterating the list a
     * concurrent modification exception will occur
     *
     */
    public final void removeFromModel() {

//		System.out.println("In " + getName() + " removeFromModel()");
        // first remove any of the model element's children
        while (!myModelElements.isEmpty()) {
            ModelElement child = myModelElements.get(myModelElements.size() - 1);
            child.removeFromModel();
        }

        // if the model element has a warm up event, cancel it
        if (myWarmUpEvent != null) {
            if (myWarmUpEvent.isScheduled()) {
                myWarmUpEvent.setCanceledFlag(true);
            }
            myWarmUpEvent = null;
            myWarmUpActionListener = null;
        }
        // if the model element has a timed update event, cancel it
        if (myTimedUpdateEvent != null) {
            if (myTimedUpdateEvent.isScheduled()) {
                myTimedUpdateEvent.setCanceledFlag(true);
            }
            myTimedUpdateEvent = null;
            myTimedUpdateActionListener = null;
        }

        // allow the sub-classes to provide specific removal behavior
        removedFromModel();

        // notify any model element observers of the removal
        notifyRemovingFromModelObservers();

        // need to ensure that any observers are detached
        clearModelElementObservers();

        // tell the parent to remove it, remove it from its parent
        getParentModelElement().removeModelElement(this);
        // remove it from the model element map
        getModel().removeFromModelElementMap(this);
        // no longer has a parent
        setParentModelElement(null);
        // no longer is in the model
        setModel(null);
        // can't be in a spatial model
        mySpatialModel = null;

        myModelElements.clear();
        myModelElements = null;
        myName = null;
        myLabel = null;

    }

    /**
     * Removes the "child" model element from this model element. The model
     * element to be removed must not be null; otherwise, an
     * IllegalArgumentException will be thrown.
     *
     * @param modelElement the model element to be removed.
     * @return True indicates that the remove was successful.
     */
    private boolean removeModelElement(ModelElement modelElement) {

        if (modelElement == null) {
            throw new IllegalArgumentException("Tried to remove a null model element");
        }

        return (myModelElements.remove(modelElement));
    }

    protected class WarmUpEventAction implements EventActionIfc {

        @Override
        public void action(JSLEvent event) {
            warmUp_();
        }
    }

    protected class TimedUpdateEventAction implements EventActionIfc {

        @Override
        public void action(JSLEvent event) {
            timedUpdate_();
            getExecutive().reschedule(event, getTimedUpdateInterval());
        }
    }
}
