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
package jsl.modeling.elements.resource;

import jsl.modeling.elements.*;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.resource.EntityType.SendOption;
import jsl.utilities.random.*;

/** An EntityGenerator creates entities according to a entity type
 *  and sends them to a receiver according to the specified sending option.
 *  A general pattern of creation can be specified, see EventGenerator
 *
 *  SendOption {DIRECT, SEQ, BY_TYPE}
 *  DIRECT, client must use setDirectEntityReceiver() to set the receiver
 *  SEQ, entity uses predefined sequence in its EntityType
 *  BY_TYPE, entity uses its EntityType to determine next receiver
 *
 *  The default sending option is DIRECT. If the user does not supply
 *  a receiver via setDirectEntityReceiver() then an error will occur.
 *  If the SEQ option is used, the user is responsible for providing
 *  a sequence for the EntityType generated. If BY_TYPE then the user
 *  is responsible for overriding the getNextReceiver() method for
 *  the EntityType.
 *
 *  To specify the type of entity generated, there are four options
 *
 *  1) override the getEntityType() method to determine the type
 *  2) supply an object that implements GetEntityTypeIfc
 *  3) supply an EntityType via setEntityType()
 *  4) indicate that the generator should use the default entity type via
 *     useDefaultEntityType() option
 *
 *  If one of these options is not used, then an error will occur
 *
 */
public class EntityGenerator extends EventGenerator implements GetEntityReceiverIfc {

    protected GetEntityTypeIfc myEntityTypeGetter;

    protected EntityReceiverAbstract myDirectEntityReceiver;

    protected SendOption mySendOption = SendOption.DIRECT;

    protected EntityType myEntityType;

    protected Receiver myFakeReceiver;

    /**
     * Creates an EntityGenerator
     *
     * @param parent 
     */
    public EntityGenerator(ModelElement parent) {
        this(parent, null, null, Long.MAX_VALUE, Double.POSITIVE_INFINITY, null);
    }

    /**
     * Creates an EntityGenerator
     *
     * @param parent
     * @param name  
     */
    public EntityGenerator(ModelElement parent, String name) {
        this(parent, null, null, Long.MAX_VALUE, Double.POSITIVE_INFINITY, name);
    }

    /**
     * Creates an EntityGenerator
     * @param parent 
     * @param timeUntilFirst
     *            A RandomIfc object that supplies the time until the first event
     *
     */
    public EntityGenerator(ModelElement parent, RandomIfc timeUntilFirst) {
        this(parent, timeUntilFirst, null, Long.MAX_VALUE, Double.POSITIVE_INFINITY, null);
    }

    /**
     * Creates an EntityGenerator
     * @param parent 
     * @param timeUntilFirst
     *            A RandomIfc object that supplies the time until the first event
     * @param name  
     *
     */
    public EntityGenerator(ModelElement parent, RandomIfc timeUntilFirst, String name) {
        this(parent, timeUntilFirst, null, Long.MAX_VALUE, Double.POSITIVE_INFINITY, name);
    }

    /**
     * Creates an EntityGenerator.
     * @param parent 
     * @param timeUntilFirst
     *            A RandomIfc object that supplies the time until the first event
     * @param timeUntilNext
     *            A RandomIfc object that supplies the time between events
     *
     */
    public EntityGenerator(ModelElement parent, RandomIfc timeUntilFirst,
            RandomIfc timeUntilNext) {
        this(parent, timeUntilFirst, timeUntilNext, Long.MAX_VALUE, Double.POSITIVE_INFINITY, null);
    }

    /**
     * Creates an EntityGenerator.
     * @param parent 
     * @param timeUntilFirst
     *            A RandomIfc object that supplies the time until the first event
     * @param timeUntilNext
     *            A RandomIfc object that supplies the time between events
     * @param name  
     *
     */
    public EntityGenerator(ModelElement parent, RandomIfc timeUntilFirst,
            RandomIfc timeUntilNext, String name) {
        this(parent, timeUntilFirst, timeUntilNext, Long.MAX_VALUE, Double.POSITIVE_INFINITY, name);
    }

    /**
     * Creates an EntityGenerator
     * @param parent 
     * @param timeUntilFirst
     *            A RandomIfc object that supplies the time until the first
     *            event.
     * @param timeUntilNext
     *            A RandomIfc object that supplies the time between events.
     * @param maxNum
     *            A RandomIfc object that supplies the maximum number of events
     *            to generate. This is typically a constant. When the generator
     *            is created, this variable is used to set the number of events
     *            to generate. The number is rounded to the closest long. Each
     *            time an event is to be scheduled the maximum number of events
     *            is checked. If the maximum has been reached, then the
     *            generator is turned off. If this variable is random, then it
     *            is only used once to set the number of events for the
     *            generator.
     * @param name  
     *
     */
    public EntityGenerator(ModelElement parent, RandomIfc timeUntilFirst,
            RandomIfc timeUntilNext, long maxNum, String name) {
        this(parent, timeUntilFirst, timeUntilNext, maxNum, Double.POSITIVE_INFINITY, name);
    }

    /**
     * Creates an EntityGenerator that generates entities
     *
     * @param parent 
     * @param timeUntilFirst
     *            A RandomIfc object that supplies the time until the first
     *            event.
     * @param timeUntilNext
     *            A RandomIfc object that supplies the time between events.
     * @param maxNum
     *            A RandomIfc object that supplies the maximum number of events
     *            to generate. This is typically a constant. When the generator
     *            is created, this variable is used to set the number of events
     *            to generate. The number is rounded to the closest long. Each
     *            time an event is to be scheduled the maximum number of events
     *            is checked. If the maximum has been reached, then the
     *            generator is turned off. If this variable is random, then it
     *            is only used once to set the number of events for the
     *            generator.
     * @param timeUntilLast
     *            A RandomIfc object that supplies a time to stop generating
     *            events. This is typically a constant. When the generator is
     *            created, this variable is used to set the ending time of the
     *            generator. Each time an event is to be scheduled the ending
     *            time is checked. If the time of the next event is past this
     *            time, then the generator is turned off and the event won't be
     *            scheduled. If this variable is random, then it is only used
     *            once to set the ending time of the generator.
     * @param name
     *            The name of the generator.
     */
    public EntityGenerator(ModelElement parent, RandomIfc timeUntilFirst,
            RandomIfc timeUntilNext, long maxNum, double timeUntilLast, String name) {
        super(parent, null, timeUntilFirst, timeUntilNext, maxNum,
                timeUntilLast, name);
    }

    /** Subclasses should override this to specify the type of entity
     *  that will be generated.  By default, the default entity type
     *  is returned.
     *
     * @return Returns the type.
     */
    protected EntityType getEntityType() {
        if (myEntityTypeGetter != null) {
            return myEntityTypeGetter.getEntityType();
        } else 
            return myEntityType;
    }

    @Override
    protected void generate(JSLEvent event) {

        // determine the type of entity
        EntityType et = getEntityType();
        if (et == null)
            throw new NoEntityTypeSpecifiedException("No entity type was " +
                    "provided for the generator");

        // create the entity
        Entity e = et.createEntity();
        e.setCurrentReceiver(this);

        // tell the entity to goto next receiver
        if (mySendOption == SendOption.DIRECT) {
            e.sendViaReceiver(myDirectEntityReceiver);
        } else if (mySendOption == SendOption.SEQ) {
            e.sendViaSequence();
        } else if (mySendOption == SendOption.BY_TYPE) {
            e.sendViaEntityType();
        }
    }

    /** Sets the sending option
     *  SendOption {DIRECT, SEQ, BY_TYPE}
     *  DIRECT, client must use setDirectEntityReceiver() to set receiver
     *  SEQ, entity uses predefined sequence in its EntityType
     *  BY_TYPE, entity uses its EntityType to determine next receiver
     * 
     * @param option
     */
    public final void setSendingOption(SendOption option) {
        mySendOption = option;
    }

    /** The object that determines the type of the entity
     *  generated
     *
     * @return
     */
    public final GetEntityTypeIfc getEntityTypeGetter() {
        return myEntityTypeGetter;
    }

    /** Can supply an object that will be used to determine
     *  the type of the entity that is generated. If no
     *  type is supplied getDefaultEntityType() is used
     *
     * @param typeGetter
     */
    public final void setEntityTypeGetter(GetEntityTypeIfc typeGetter) {
        myEntityTypeGetter = typeGetter;
    }

    /** An object that will directly receive the entity
     *
     * @return
     */
    public final EntityReceiverAbstract getDirectEntityReceiver() {
        return myDirectEntityReceiver;
    }

    /** Can be used to supply a direct reciever. If used
     *  the sending option is automatically changed to direct
     *
     * @param receiver
     */
    public final void setDirectEntityReceiver(EntityReceiverAbstract receiver) {
        myDirectEntityReceiver = receiver;
        setSendingOption(SendOption.DIRECT);
    }

    /** See setDirectEntityReceiver()
     *
     * @param g
     */
    public final void setDirectEntityReceiver(GetEntityReceiverIfc g){
        setDirectEntityReceiver(g.getEntityReceiver());
    }

    /** Can be used to supply a pre-determined type to generate
     *  Can be null, if so, getEntityType() can be overridden or
     *  a EntityTypeGetter supplied
     * 
     * @param entityType
     */
    public void setEntityType(EntityType entityType) {
        myEntityType = entityType;
    }

    /** Indicates that the generator should use the default
     *  entity type
     *
     */
    public void useDefaultEntityType(){
        myEntityType = getDefaultEntityType();
    }

    /** Allows the EntityGenerator to act like a location
     *  that can receive entities.  It does not actually
     *  receive entities, but this method can
     *  be used to return a receiver so that the generator
     *  can be an origin point
     *
     * @return
     */
    public final EntityReceiverAbstract getEntityReceiver() {
        if (myFakeReceiver == null)
            myFakeReceiver = new Receiver();
        return myFakeReceiver;
    }

    private class Receiver extends EntityReceiverAbstract {

        @Override
        protected void receive(Entity entity) {
            throw new UnsupportedOperationException("EntityGenerator's cannot receive entities");
        }

    }
}
