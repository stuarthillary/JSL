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
import jsl.modeling.elements.RandomElementIfc;
import jsl.modeling.elements.queue.FIFODiscipline;
import jsl.modeling.elements.queue.LIFODiscipline;
import jsl.modeling.elements.queue.RandomDiscipline;
import jsl.modeling.elements.queue.RankedQDiscipline;

import jsl.modeling.elements.spatial.SpatialModel;
import jsl.modeling.elements.variable.*;

//import jsl.spatial.spatial2D.SpatialModel2D;
import jsl.utilities.reporting.JSL;
import jsl.modeling.elements.resource.EntityType;
import jsl.observers.ObserverIfc;
import jsl.utilities.statistic.StatisticAccessorIfc;

/**
 *
 * This class serves as the base (container) model element for all model
 * elements in the simulation.
 */
public class Model extends ModelElement {

    /**
     * An "enum" to indicate that the model element was added to the model
     * element hierarchy
     */
    public static final int MODEL_ELEMENT_ADDED = JSL.getNextEnumConstant();

    /**
     * An "enum" to indicate that the model element was removed from the model
     * element hierarchy
     */
    public static final int MODEL_ELEMENT_REMOVED = JSL.getNextEnumConstant();

    /**
     * A list of all the response variables (including TimeWeighted) within the
     * model
     */
    protected List<ResponseVariable> myResponseVariables;

    /**
     * A list of all the Counters within the model
     */
    protected List<Counter> myCounters;

    /**
     * A list of all the Variables within the model
     */
    protected List<Variable> myVariables;

    /**
     * A list of all random elements within the model
     */
    protected List<RandomElementIfc> myRandomElements;

    /**
     * A Map that holds all the model elements in the order in which they are
     * created
     */
    private Map<String, ModelElement> myModelElementMap;

    /**
     * Indicates whether or not the model should automatically remove any
     * elements that have been marked for removal prior to each replication. The
     * default is false;
     *
     */
    private boolean myAutoRemoveMarkedElementsOption = false;

    /**
     * If a model element is added to the model after a replication has started
     * this is likely to be a conceptual error. A warning message will be
     * printed to the console in this case. The user can turn off this warning
     * message by setting this flag to false. The default is true.
     *
     */
    private boolean myElementAddedAfterReplicationStartedWarningOption = true;

    /**
     * The simulation that is running the model
     *
     */
    private Simulation mySimulation;

    /**
     * A reference to the default entity type
     */
    private EntityType myDefaultEntityType;

    /**
     * A QueueDiscipline to allow first in, first out behavior.
     */
    private FIFODiscipline FIFO;

    /**
     * A QueueDiscipline for last in, first out behavior.
     */
    private LIFODiscipline LIFO;

    /**
     * A QueueDiscipline for random selection from the queue. This queue
     * discipline uses a distribution which can have its stream controlled This
     * reference provides a shared discipline. To ensure that queues using the
     * random discipline use different streams, create additional instances of
     * RandomDiscipline for each Queue rather than sharing this instance
     */
    private RandomDiscipline RANDOM;

    /**
     * A QueueDiscipline for ranked ordering of the queue.
     */
    private RankedQDiscipline RANKED;

    private long myTimeUnit = ModelElement.TIME_UNIT_MILLISECOND;

//    /**
//     *
//     * @return
//     */
//    public static Model createModel() {
//        return (Model.createModel(null));
//    }
//
//    /**
//     *
//     * @param name
//     * @return
//     */
//    public static Model createModel(String name) {
//        return (new Model(name));
//    }
    /**
     * Constructs a model called "name"
     *
     * @param name The name of the model
     */
    protected Model(String name) {
        super(name);
        myModelElementMap = new LinkedHashMap<String, ModelElement>();
        myResponseVariables = new ArrayList<ResponseVariable>();
        myCounters = new ArrayList<Counter>();
        myRandomElements = new ArrayList<RandomElementIfc>();
        myVariables = new ArrayList<Variable>();
        myLengthOfWarmUp = 0.0; // zero means no warm up
        setModel(this);
        setParentModelElement(null);
        addToModelElementMap(this);
        addDefaultElements();
    }

    private void addDefaultElements() {
        myDefaultEntityType = new EntityType(this, "DEFAULT_ENTITY_TYPE");
        FIFO = new FIFODiscipline(this, "DEFAULT_FIFO_DISCIPLINE");
        LIFO = new LIFODiscipline(this, "DEFAULT_LIFO_DISCIPLINE");
        RANDOM = new RandomDiscipline(this, "DEFAULT_RANDOM_DISCIPLINE");
        RANKED = new RankedQDiscipline(this, "DEFAULT_RANKED_DISCIPLINE");
    }

    /**
     * Returns the current time unit. The default is the number of milliseconds
     * in one time unit.
     *
     * @return
     */
    public final long getTimeUnit() {
        return myTimeUnit;
    }

    /**
     * sets the time unit to a given number of milliseconds (tu). If you are
     * setting a standard time unit, you can use a constant from ModelElement
     *
     * @param timeUnit
     */
    public final void setTimeUnit(long timeUnit) {
        if (timeUnit <= 0) {
            throw new IllegalArgumentException("The time unit must be > 0");
        }
        myTimeUnit = timeUnit;
    }

    /**
     * Returns the variable associated with the name or null if named element is
     * not in the model. Note that this will also return ANY instances of
     * subclasses of Variable
     *
     * @param name The name of the Variable model element
     * @return the associated variable, may be null if provided name does not
     * exist in the model
     */
    @Override
    public final Variable getVariable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("The name must be non-null.");
        }

        if (!myModelElementMap.containsKey(name)) {
            return (null);
        }

        ModelElement v = myModelElementMap.get(name);

        if (v instanceof Variable) {
            return ((Variable) v);
        } else {
            return (null);
        }
    }

    /**
     * Returns the random variable associated with the name or null if named
     * element is not in the model. Note that this will also return ANY
     * instances of subclasses of RandomVariable
     *
     * @param name The name of the RandomVariable model element
     * @return the associated random variable, may be null if provided name does
     * not exist in the model
     */
    public final RandomVariable getRandomVariable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("The name must be non-null.");
        }

        if (!myModelElementMap.containsKey(name)) {
            return (null);
        }

        ModelElement v = myModelElementMap.get(name);

        if (v instanceof RandomVariable) {
            return ((RandomVariable) v);
        } else {
            return (null);
        }
    }

    /**
     * Returns the response variable associated with the name or null if named
     * element is not in the model. Note that this will also return ANY
     * instances of subclasses of ResponseVariable (i.e. including TimeWeighted)
     *
     * @param name The name of the ResponseVariable model element
     *
     * @return the associated ResponseVariable, may be null if provided name
     * does not exist in the model
     */
    public final ResponseVariable getResponseVariable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("The name must be non-null.");
        }

        if (!myModelElementMap.containsKey(name)) {
            return (null);
        }

        ModelElement v = myModelElementMap.get(name);

        if (v instanceof ResponseVariable) {
            return ((ResponseVariable) v);
        } else {
            return (null);
        }
    }

    /**
     * Returns the time weighted variable associated with the name or null if
     * named element is not in the model. Note that this will also return ANY
     * instances of subclasses of TimeWeighted
     *
     * @param name The name of the TimeWeighted model element
     *
     * @return the associated TimeWeighted, may be null if provided name does
     * not exist in the model
     */
    public final TimeWeighted getTimeWeighted(String name) {
        if (name == null) {
            throw new IllegalArgumentException("The name must be non-null.");
        }

        if (!myModelElementMap.containsKey(name)) {
            return (null);
        }

        ModelElement v = myModelElementMap.get(name);

        if (v instanceof TimeWeighted) {
            return ((TimeWeighted) v);
        } else {
            return (null);
        }
    }

    /**
     * Gets the AcrossReplicationStatisticIfc for the provided name. If there is
     * no response variable with the provided name then null is returned.
     *
     * @param name name of response variable in the model, must not be null
     * @return the associated across replication statistic information, may be
     * null if provided name does not exist in the model
     */
    public final AcrossReplicationStatisticIfc getAcrossReplicationResponseVariable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("The name must be non-null.");
        }

        if (!myModelElementMap.containsKey(name)) {
            return (null);
        }

        ModelElement v = myModelElementMap.get(name);

        if (v instanceof ResponseVariable) {
            return ((AcrossReplicationStatisticIfc) v);
        } else {
            return (null);
        }
    }

    /**
     * Returns the Counter associated with the name or null if named element is
     * not in the model.
     *
     * @param name The name of the Counter model element
     * @return the associated counter, may be null if provided name does not
     * exist in the model
     */
    public final Counter getCounter(String name) {
        if (name == null) {
            throw new IllegalArgumentException("The name must be non-null.");
        }

        if (!myModelElementMap.containsKey(name)) {
            return (null);
        }

        ModelElement v = myModelElementMap.get(name);

        if (v instanceof Counter) {
            return ((Counter) v);
        } else {
            return (null);
        }
    }

    /**
     * The responses as a list of StatisticAccessorIfc
     *
     * @return a list of response variables and counters
     */
    public final List<StatisticAccessorIfc> getListOfAcrossReplicationStatistics() {
        List<StatisticAccessorIfc> stats = new ArrayList<StatisticAccessorIfc>();

        for (ResponseVariable r : myResponseVariables) {
            StatisticAccessorIfc stat = r.getAcrossReplicationStatistic();
            if (r.getDefaultReportingOption()) {
                stats.add(stat);
            }
        }

        for (Counter c : myCounters) {
            StatisticAccessorIfc stat = c.getAcrossReplicationStatistic();
            if (c.getDefaultReportingOption()) {
                stats.add(stat);
            }
        }
        return stats;
    }

    /**
     * Turns on the collection of statistics across intervals of time, defined
     * by the interval length for all response variables (including TimeWeighted) 
     * and counters
     *
     * @param interval
     */
    public final void turnOnTimeIntervalCollection(double interval) {
        
        List<ResponseVariable> list = new ArrayList<>();
        getAllResponseVariables(list);
        
        for (ResponseVariable r : list) {
            r.turnOnTimeIntervalCollection(interval);
        }

        List<Counter> clist = new ArrayList<>();
        getAllCounters(clist);
        for (Counter c : clist) {
            c.turnOnTimeIntervalCollection(interval);
        }
    }

    /**
     * Checks to see if the model element has been registered with the Model
     * using it's uniquely assigned name.
     *
     * @param modelElementName
     * @return
     */
    public final boolean containsModelElement(String modelElementName) {
        return (myModelElementMap.containsKey(modelElementName));
    }

    /**
     * Returns the model element associated with the name. In some sense, this
     * model violates encapsulation for ModelElements, but since the client
     * knows the unique name of the ModelElement, we assume that the client
     * knows what he/she is doing.
     *
     * @param name The name of the model element, the name must not be null
     * @return The named ModelElement if it exists in the model by the supplied
     * name, null otherwise
     */
    public final ModelElement getModelElement(String name) {
        if (name == null) {
            throw new IllegalArgumentException("The name must be non-null.");
        }

        if (!myModelElementMap.containsKey(name)) {
            return (null);
        }

        ModelElement v = myModelElementMap.get(name);

        return (v);
    }

    /**
     * Returns the model element that has the provided unique id or null if not
     * found
     *
     * @param id
     * @return
     */
    public final ModelElement getModelElement(int id) {

        for (Map.Entry<String, ModelElement> entry : myModelElementMap.entrySet()) {
            ModelElement me = entry.getValue();
            if (me.getId() == id) {
                return me;
            }
        }

        return null;
    }

    /**
     * Gets all instances of the Variable class within the model hierarchy. Only
     * Variable objects (not its subclasses)
     *
     * @param c a Collection to hold the instances of Variable found
     */
    public final void getVariables(Collection<Variable> c) {
        getAllVariables(c);
    }

    /**
     * Returns an unmodifiable collection holding all the Variables within the
     * Model. The list is unchangeable, but the elements in the list can be
     * still be accessed and changed as necessary
     *
     * @return An unmodifiable List of the variables
     */
    public final List<Variable> getVariables() {
        return (Collections.unmodifiableList(myVariables));
    }

    /**
     * Gets all instances of the RandomElementIfc interface within the model
     * hierarchy. All instances of RandomElementIfc will be returned including
     * any instances of subclasses of RandomElementIfc
     *
     * @param c a Collection to hold the instances of RandomElementIfc found
     */
    public final void getRandomElements(Collection<RandomElementIfc> c) {
        getAllRandomElements(c);
    }

    /**
     * Returns an unmodifiable collection holding all the RandomElementIfc
     * within the Model. The list is unchangeable, but the elements in the list
     * can be still be accessed and changed as necessary
     *
     * @return An unmodifiable List of the RandomElementIfc
     */
    public final List<RandomElementIfc> getRandomElements() {
        return (Collections.unmodifiableList(myRandomElements));
    }

    /**
     * Causes RandomElementIfc that have been added to the model to immediately
     * turn on their antithetic generating streams.
     */
    public final void turnOnAntithetic() {
        for (RandomElementIfc rv : myRandomElements) {
            rv.setAntitheticOption(true);
        }
    }

    /**
     * Causes RandomElementIfc that have been added to the model to immediately
     * turn off their antithetic generating streams.
     */
    public final void turnOffAntithetic() {
        for (RandomElementIfc rv : myRandomElements) {
            rv.setAntitheticOption(false);
        }
    }

    /**
     * Advances the streams of all RandomElementIfc n times. If n &lt;= 0, no
     * advancing occurs
     *
     * @param n
     */
    public final void advanceSubstreams(int n) {
        if (n <= 0) {
            return;
        }
        for (int i = 1; i <= n; i++) {
            advanceToNextSubstream();
        }
    }

    /**
     * Causes RandomElementIfc that have been added to the model to immediately
     * advance their random number streams to the next substream in their
     * stream.
     */
    public final void advanceToNextSubstream() {

        for (RandomElementIfc rv : myRandomElements) {
            rv.advanceToNextSubstream();
        }
    }

    /**
     * Causes RandomElementIfc that have been added to the model to immediately
     * reset their random number streams to the beginning of their starting
     * stream.
     */
    public final void resetStartStream() {
        for (RandomElementIfc rv : myRandomElements) {
            rv.resetStartStream();
        }
    }

    /**
     * Causes RandomElementIfc that have been added to the model to immediately
     * reset their random number streams to the beginning of their current sub
     * stream.
     */
    public final void resetStartSubStream() {
        for (RandomElementIfc rv : myRandomElements) {
            rv.resetStartSubstream();
        }
    }

    /**
     * Fills up the provided collection with all of the response variables that
     * are contained by any model elements within the model. In other words, any
     * response variables (and subclasses, e.g. TimeWeighted that are in the
     * model element hierarchy below the model.
     *
     * @param c The collection to be filled.
     */
    public final void getResponseVariables(Collection<ResponseVariable> c) {
        getAllResponseVariables(c);
    }

    /**
     * Returns an unmodifiable collection holding all the ResponseVariables
     * within the Model. The list is unchangeable, but the elements in the list
     * can still be accessed and changed as necessary
     *
     * @return An unmodifiable List of the variables
     */
    public final List<ResponseVariable> getResponseVariables() {
        return (Collections.unmodifiableList(myResponseVariables));
    }

    /**
     * Returns a list of strings with each element being the unique model
     * element name for the ResponseVariables within the model
     *
     * @return
     */
    public final List<String> getResponseVariableNames() {
        List<String> list = new ArrayList<String>();

        for (ResponseVariable rv : myResponseVariables) {
            list.add(rv.getName());
        }
        return list;
    }

    /**
     * Fills up the provided collection with all of the Counters that are
     * contained by any model elements within the model. In other words, any
     * Counters that are in the model element hierarchy below the model.
     *
     * @param c The collection to be filled.
     */
    public final void getCounters(Collection<Counter> c) {
        getAllCounters(c);
    }

    /**
     * Returns an unmodifiable collection holding all the Counters within the
     * Model. The list is unchangeable, but the elements in the list can be
     * still be accessed and changed as necessary
     *
     * @return An unmodifiable List of the Counters
     */
    public final List<Counter> getCounters() {
        return (Collections.unmodifiableList(myCounters));
    }

    /**
     * Returns a list of strings with each element being the unique model
     * element name for the Counters within the model
     *
     * @return
     */
    public final List<String> getCounterNames() {
        List<String> list = new ArrayList<String>();

        for (Counter c : myCounters) {
            list.add(c.getName());
        }
        return list;
    }

    /**
     * Adds the supplied observer to the ModelElement with the given name. While
     * any model element can be observed the client needs a reference to tell
     * the model element to add an observer. This method provides way to add
     * observers to any ModelElement regardless of where it is in the model
     * hierarchy, provided the client knows the unique name of the ModelElement.
     *
     * @param name The name of the model element
     * @param observer The observer of the named model element
     * @return If this method returns false, then no such named model element
     * exists in the Model
     */
    public final boolean addModelElementObserver(String name, ObserverIfc observer) {
        if (name == null) {
            throw new IllegalArgumentException("The name must be non-null.");
        }

        if (observer == null) {
            throw new IllegalArgumentException("The observer must be non-null.");
        }

        if (!myModelElementMap.containsKey(name)) {
            return (false);
        }

        ModelElement me = myModelElementMap.get(name);

        me.addObserver(observer);

        return (true);
    }

    /**
     * Removes the supplied observer from the ModelElement with the given name.
     * While any model element can be observed the client needs a reference to
     * tell the model element to remove an observer. This method provides way to
     * remove observers from any ModelElement regardless of where it is in the
     * model hierarchy, provided the client knows the unique name of the
     * ModelElement.
     *
     * @param name The name of the model element
     * @param observer The observer of the named model element
     * @return If this method returns false, then no such named model element
     * exists in the Model
     */
    public final boolean deleteModelElementObserver(String name, ObserverIfc observer) {
        if (name == null) {
            throw new IllegalArgumentException("The name must be non-null.");
        }

        if (observer == null) {
            throw new IllegalArgumentException("The observer must be non-null.");
        }

        if (!myModelElementMap.containsKey(name)) {
            return (false);
        }

        ModelElement me = myModelElementMap.get(name);

        me.deleteObserver(observer);

        return (true);
    }

    /**
     * Returns an Iterator to all the ModelElements registered inside this model
     *
     * @return
     */
    public final Iterator<ModelElement> getModelElementIterator() {
        return (myModelElementMap.values().iterator());
    }

    /**
     * This method can be used to ensure that all model elements within the
     * model use the same spatial model
     *
     * @param model
     */
    public final void setSpatialModelForAllElements(SpatialModel model) {

        //set the model's spatial model
        setSpatialModel(model);
        // iterate through all elements and set their spatial model
        for (ModelElement m : myModelElementMap.values()) {
            m.setSpatialModel(model);
        }
    }

    /**
     * Places all model elements in the model that are marked for removal into
     * the supplied collection
     *
     * @param c
     */
    public final void getAllElementsMarkedForRemoval(Collection<ModelElement> c) {
        if (c == null) {
            throw new IllegalArgumentException("The supplied collection was null");
        }
        getAllElementsNeedingRemoval(c);
    }

    /**
     * Indicates whether or not the model will automatically remove elements
     * that are marked for removal prior to each replication. The default is
     * false.
     *
     * @return the option
     */
    public final boolean getAutoRemoveMarkedElementsOption() {
        return myAutoRemoveMarkedElementsOption;
    }

    /**
     * Sets the option for the model to automatically remove elements that are
     * marked for removal prior to each replication. False means elements will
     * not be automatically removed. True means that they will automatically be
     * removed.
     *
     * @param flag
     */
    public final void setAutoRemoveMarkedElementsOption(boolean flag) {
        myAutoRemoveMarkedElementsOption = flag;
    }

    /**
     * If a model element is added to the model after a replication has started
     * this is likely to be a conceptual error. A warning message will be
     * printed to the console in this case. The user can turn off this warning
     * message by setting this flag to false. The default is true.
     *
     * @return the option
     */
    public final boolean getElementAddedAfterReplicationStartedWarningOption() {
        return myElementAddedAfterReplicationStartedWarningOption;
    }

    /**
     * If a model element is added to the model after a replication has started
     * this is likely to be a conceptual error. A warning message will be
     * printed to the console in this case. The user can turn off this warning
     * message by setting this flag to false. The default is true.
     *
     * @param flag
     */
    public final void setElementAddedAfterReplicationStartedWarningOption(boolean flag) {
        myElementAddedAfterReplicationStartedWarningOption = flag;
    }

    /**
     * Sets the reset start stream option for all RandomElementIfc in the model
     * to the supplied value, true is the default behavior. This method is used
     * by an experiment prior to beforeExperiment_() being called Thus, any
     * RandomElementIfc must already have been created and attached to the model
     * elements
     *
     * @param option The option, true means to reset prior to each experiment
     */
    protected final void setAllRVResetStartStreamOptions(boolean option) {
        for (RandomElementIfc rv : myRandomElements) {
            rv.setResetStartStreamOption(option);
        }
    }

    /**
     * Sets the reset next sub stream option for all RandomElementIfc in the
     * model to the supplied value, true is the default behavior. True implies
     * that the substreams will be advanced at the end of the replication. This
     * method is used by an experiment prior to beforeExperiment_() being called
     * Thus, any RandomElementIfc must already have been created and attached to
     * the model elements
     *
     * @param option The option, true means to reset prior to each replication
     */
    protected final void setAllRVResetNextSubStreamOptions(boolean option) {
        for (RandomElementIfc rv : myRandomElements) {
            rv.setResetNextSubStreamOption(option);
        }
    }

    /**
     * Used to remove any flagged model elements from the model prior to a
     * replication
     *
     *
     */
    protected void removeModelElementsMarkedForRemovalPriorToReplication() {
//		System.out.println("In Model's removeModelElementsMarkedForRemovalPriorToReplication()");
//		System.out.println("------ Hierarchy before remove");
//		System.out.println(getModelHierarchyAsString());

        List<ModelElement> toRemove = new ArrayList<ModelElement>();
        // find all that need to be removed
        getAllElementsNeedingRemoval(toRemove);

        // now tell them to remove themselves from the model as well as their children
        for (ModelElement m : toRemove) {
            m.removeFromModel();
        }

        toRemove.clear();
//		System.out.println("--------- Hierarchy after remove");
//		System.out.println(getModelHierarchyAsString());
    }

    /**
     * Checks if there exists at least one model element that was added during
     * the replication. True means that at least one exists.
     *
     * @return
     */
    public final boolean checkForModelElementsAddedWhileReplicationWasRunning() {
        boolean found = false;

        for (ModelElement m : myModelElementMap.values()) {
            if (m.getAddedWhileReplicationWasRunningFlag()) {
                return true;
            }
        }
        return found;
    }

    /**
     * Returns as a String the names of the model elements returned by
     * getAllElementsNeedingRemoval()
     *
     * @return
     */
    public final String getModelElementsMarkedForRemovalPriorToReplicationAsString() {
        List<ModelElement> toRemove = new ArrayList<ModelElement>();
        // find all that need to be removed
        getAllElementsNeedingRemoval(toRemove);

        if (toRemove.isEmpty()) {
            toRemove = null;
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (ModelElement m : toRemove) {
            sb.append(m.getName());
            sb.append("\n");
        }
        return (sb.toString());
    }

    /**
     * Adds the model element to the Model's model element map This method is
     * called from the model element's constructor
     *
     * @param modelElement
     */
    final void addToModelElementMap(ModelElement modelElement) {
        if (modelElement == null) {
            throw new IllegalArgumentException("The ModelElement must be non-null.");
        }

        String key = modelElement.getName();

        if (key == null) {
            throw new IllegalArgumentException("The ModelElement Unique key must be non-null.");
        }

        if (myModelElementMap.containsKey(key)) {
            StringBuilder sb = new StringBuilder();
            sb.append("A ModleElement with the name: ");
            sb.append(modelElement.getName());
            sb.append(" has already been added to the Model.\n");
            sb.append("Every model element must have a unique name");
            throw new IllegalArgumentException(sb.toString());
        }

        myModelElementMap.put(key, modelElement);

        if (modelElement instanceof ResponseVariable) {
            myResponseVariables.add((ResponseVariable) modelElement);
        }

        if (modelElement instanceof Counter) {
            myCounters.add((Counter) modelElement);
        }

        if (modelElement instanceof RandomElementIfc) {
            myRandomElements.add((RandomElementIfc) modelElement);
        }

        if (modelElement instanceof Variable) {
            if (Variable.class == modelElement.getClass()) {
                myVariables.add((Variable) modelElement);
            }
        }

        if (getExecutive() != null) {
            if (getExecutive().isRunning()) {
                if (getElementAddedAfterReplicationStartedWarningOption()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("The model element: ");
                    sb.append(modelElement.getName());
                    sb.append(" was added after the replication started.\n");
                    sb.append("To not see this warning message call the method \n");
                    sb.append("setElementAddedAfterReplicationStartedWarningOption(false) on Model\n");
                    JSL.LOGGER.warning(sb.toString());
                }
                modelElement.setAddedWhileReplicationWasRunningFlag(true);
            }
        }

        notifyObservers(MODEL_ELEMENT_ADDED, modelElement);

    }

    /**
     * Removes the given model element from the Model's model element map. Any
     * child model elements of the supplied model element are also removed from
     * the map, until all elements below the given model element are removed.
     *
     * @param modelElement
     */
    final void removeFromModelElementMap(ModelElement modelElement) {
        if (modelElement == null) {
            throw new IllegalArgumentException("The ModelElement must be non-null.");
        }

        String key = modelElement.getName();

        if (myModelElementMap.containsKey(key)) {
            //	remove the associated model element from the map, if there
            myModelElementMap.remove(key);

            if (modelElement instanceof ResponseVariable) {
                myResponseVariables.remove((ResponseVariable) modelElement);
            }

            if (modelElement instanceof Counter) {
                myCounters.remove((Counter) modelElement);
            }

            if (modelElement instanceof RandomElementIfc) {
                myRandomElements.remove((RandomElementIfc) modelElement);
            }

            if (modelElement instanceof Variable) {
                if (Variable.class == modelElement.getClass()) {
                    myVariables.remove((Variable) modelElement);
                }
            }

            notifyObservers(MODEL_ELEMENT_REMOVED, modelElement);

            // remove any of the modelElement's children and so forth from the map
            Iterator<ModelElement> i = modelElement.getChildModelElementIterator();
            ModelElement m;
            while (i.hasNext()) {
                m = (ModelElement) i.next();
                removeFromModelElementMap(m);
            }
        }
    }

    /**
     * Used by ModelElement to assist with changing the model element's parent.
     * This method ensures that the model has model element and its children
     * registered in its map after the change.
     *
     * @param element
     */
    protected final void addModelElementAndChildrenToModelElementMap(ModelElement element) {
        if (element == null) {
            throw new IllegalArgumentException("The ModelElement must be non-null.");
        }

        String key = element.getName();

        if (key == null) {
            throw new IllegalArgumentException("The ModelElement Unique key must be non-null.");
        }

        if (myModelElementMap.containsKey(key)) {
            throw new IllegalArgumentException("The ModelElement has been already added to the Map.");
        }

        // make sure that the element knows its model
        element.setModel(this);

        // put the element in the map using its key
        myModelElementMap.put(key, element);

        // recursively add any of the modelElement's children and so forth to the map
        Iterator<ModelElement> i = element.getChildModelElementIterator();
        ModelElement m;
        while (i.hasNext()) {
            m = (ModelElement) i.next();
            addModelElementAndChildrenToModelElementMap(m);
        }

    }

    /**
     * Returns a reference to the default entity type
     *
     * @return
     */
    @Override
    protected final EntityType getDefaultEntityType() {
        return myDefaultEntityType;
    }

    @Override
    protected final FIFODiscipline getDefaultFIFOQueueDiscipline() {
        return FIFO;
    }

    @Override
    protected final LIFODiscipline getDefaultLIFOQueueDiscipline() {
        return LIFO;
    }

    @Override
    protected final RandomDiscipline getDefaultRandomQueueDiscipline() {
        return RANDOM;
    }

    @Override
    protected final RankedQDiscipline getDefaultRankedQueueDiscipline() {
        return RANKED;
    }

    /**
     * Returns a reference to the Simulation or null. The reference to the
     * Simulation will only be available after the Simulation is initialized.
     *
     * @return
     */
    @Override
    public final Simulation getSimulation() {
        return mySimulation;
    }

    /**
     * Returns a reference to the Executive or null. The reference to the
     * Executive will only be available after the Simulation is initialized.
     *
     * @return
     */
    @Override
    public final Executive getExecutive() {
        if (mySimulation == null) {
            return null;
        }

        return mySimulation.getExecutive();
    }

    /**
     * Returns true if the executive is running, false if it is not running or
     * if the model is not yet part of a simulation.
     *
     * @return
     */
    public final boolean isRunning() {
        if (getExecutive() == null) {
            return false;
        }
        return getExecutive().isRunning();
    }

    /**
     * Returns a reference to the Experiment or null. The reference to the
     * Experiment will only be available after the Simulation is initialized.
     *
     * @return
     */
    @Override
    public final ExperimentGetIfc getExperiment() {
        if (mySimulation == null) {
            return null;
        }
        return mySimulation.getExperiment();
    }

    protected void setUpExperiment() {

        advanceSubstreams(getExperiment().getNumberOfStreamAdvancesPriorToRunning());

        if (getExperiment().getAntitheticOption() == true) {
            //TODO implement antithetic option???
        } else {
            // tell the model to use the specifications from the experiment
            setAllRVResetStartStreamOptions(getExperiment().getResetStartStreamOption());
            setAllRVResetNextSubStreamOptions(getExperiment().getAdvanceNextSubStreamOption());
        }
        // do all model element beforeExperiment() actions
        beforeExperiment_();
    }

    private void removeMarkedModelElements() {
        //remove any model elements marked for removal, if automatic removal is on
        if (getAutoRemoveMarkedElementsOption()) {
            removeModelElementsMarkedForRemovalPriorToReplication();
        } else // automatic removal is not on, check for any model elements added during a replication
        if (checkForModelElementsAddedWhileReplicationWasRunning()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Automatic model element removal is turned off\n");
            sb.append("and there were model elements added during a replication.");
            JSL.LOGGER.warning(sb.toString());
        }
    }

    private void handleAntitheticReplications() {
        // handle antithetic replications
        if (getExperiment().getAntitheticOption() == true) {
            if ((getExperiment().getCurrentReplicationNumber() % 2) == 0) {
                // even number replication
                // return to beginning of substream
                resetStartSubStream();
                // turn on antithetic
                turnOnAntithetic();
            } else // odd number replication
            if (getExperiment().getCurrentReplicationNumber() > 1) {
                // turn off anthethics
                turnOffAntithetic();
                // advance to next substream
                advanceToNextSubstream();
            }
        }
    }

    protected void setUpReplication() {

        // remove any marked model elements were added during previous replication
        removeMarkedModelElements();

        // setup warm up period
        setLengthOfWarmUp(getExperiment().getLengthOfWarmUp());

        // control streams for antithetic option
        handleAntitheticReplications();

        // do all model element beforeReplication() actions
        beforeReplication_();

        // schedule the end of the replication
        scheduleEndOfReplication();

        // if necessary, initialize the model elements
        if (getExperiment().getReplicationInitializationOption() == true) {
            // initialize the model and all model elements with initialize option on
            initialize_();
        }

        // allow model elements to register conditional actions
        registerConditionalActions_(getExecutive());

        // if monte carlo option is on, call the model element's monteCarlo() methods
        if (getMonteCarloOption()) {
            // since monte carlo option was turned on, assume everyone wants to listen
            setMonteCarloOptionForModelElements(true);
            montecarlo_();
        }
    }

    protected void afterReplication(Experiment e) {
        // do all model element replicationEnded() actions
        replicationEnded_();
        // do all model element afterReplication() actions
        afterReplication_();

    }

    protected void afterExperiment(Experiment e) {
        // do all model element afterExperiment() actions
        afterExperiment_();
    }

    final void setSimulation(Simulation sim) {
        mySimulation = sim;
    }

    private void scheduleEndOfReplication() {
        ExperimentGetIfc e = getExperiment();
        double t = e.getLengthOfReplication();
        //getExecutive().scheduleEndEvent(t);
        if (!Double.isInfinite(t)) {
            JSLEvent event = getExecutive().scheduleEndEvent(t);
            event.setModelElement(Model.this);
        }
    }
}
