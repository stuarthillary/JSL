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
package jsl.modeling.elements.spatial;

import jsl.utilities.random.RandomIfc;

public interface MoverIfc extends SpatialElementIfc, VelocityIfc {

    /**
     * Returns a CoordinateIfc that represents the future position of the
     * spatial element at the given time.
     *
     * @param time Must be greater than or equal to zero
     * @return
     */
    public CoordinateIfc getFuturePosition(double time);

    /**
     * Indicates whether or not the element is currently moving
     *
     * @return
     */
    public boolean isMoving();

    /**
     * Indicates whether or not the element is currently on a trip
     *
     * @return
     */
    public boolean isOnTrip();

    /**
     * Gets the distance associated with the current movement
     *
     * @return Returns the movementDistance.
     */
    public double getMovementDistance();

    /**
     * Gets the time length for the current movement
     *
     * @return Returns the movementTime
     */
    public double getMovementTime();

    /**
     * Get the time that the current movement started
     *
     * @return Returns the movementStartTime.
     */
    public double getMovementStartTime();

    /**
     * Gets the velocity of the movement that is in progress
     *
     * @return Returns the movementVelocity.
     */
    public double getMovementVelocity();

    /**
     * Gets a reference to the CoordinateIfc representing the destination of the
     * mover
     *
     * @return the CoordinateIfcIfc for this element
     */
    public CoordinateIfc getDestination();

    /**
     * @return Returns the collisionDetector.
     */
    public CollisionDetectorIfc getCollisionDetector();

    /**
     * Sets the collision detector. If null no detection is used
     *
     * @param collisionDetector The collisionDetector to set.
     */
    public void setCollisionDetector(CollisionDetectorIfc collisionDetector);

    /**
     * @return Returns the collisionHandler.
     */
    public CollisionHandlerIfc getCollisionHandler();

    /**
     * Sets the collision handler, if null then no collision handling is
     * performed
     *
     * @param collisionHandler The collisionHandler to set.
     */
    public void setCollisionHandler(CollisionHandlerIfc collisionHandler);

    /**
     * @return Returns the movementController.
     */
    public MovementControllerIfc getMovementController();

    /**
     * Set the movement controller. If null, then the default movement uses the
     * point to point distance at velocity 1.
     *
     * @param movementController The movementController to set.
     */
    public void setMovementController(MovementControllerIfc movementController);

    /**
     *
     * @return a value from the velocity random source
     */
    public double getVelocity();

    /**
     * Sets the random source associated with the velocity used at the beginning
     * of each replication
     *
     * @param source
     */
    public void setVelocityInitialRandomSource(RandomIfc source);

    /**
     * Sets the random source associated with the velocity. This is reset at the
     * beginning of each replication to the value set with
     * setVelocityInitialRandomSource()
     *
     * @param source
     */
    public void setVelocityRandomSource(RandomIfc source);

    /**
     * Get the object the handles when the movement takes the element outside
     * the boundaries of the current spatial model
     *
     * @return Returns the OSMHandler.
     */
    public OutsideSpatialModelHandlerIfc getOSMHandler();

    /**
     * If the mover is on a trip then the movement along the trip is canceled
     * and the mover stays at the position where it is when this method is
     * called. This method cancels movement. If the mover is not moving or on a
     * trip nothing happens.
     */
    public void cancelTrip();

    /**
     * Set the handler for taking care of when the element goes outside the
     * boundaries of its spatial model. If null, then an exception will be
     * thrown if the element tries to go outside its spatial element.
     *
     * @param OSMHandler The OSMHandler to set.
     */
    public void setOSMHandler(OutsideSpatialModelHandlerIfc OSMHandler);

    /**
     * Sets up the movement before it gets processed. Takes in a velocity for
     * the movement (which must be &gt; 0) and the coordinate where the movement
     * should end. This method should be used by MovementControllers to set the
     * movement characteristics prior to the move.
     *
     * This method sets: 1) the velocity of the move 2) the distance of the move
     * 3) the direction of the move 4) the time the move starts 5) the total
     * time to move to the position at the given velocity
     *
     * @param velocity Must be &gt; 0
     * @param position Must not be null
     */
    public void setMovement(double velocity, CoordinateIfc position);

}
