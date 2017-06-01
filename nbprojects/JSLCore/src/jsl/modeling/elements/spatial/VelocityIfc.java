/*
 *  Copyright (C) 2017 rossetti
 * 
 *  Contact:
 * 	Manuel D. Rossetti, Ph.D., P.E.
 * 	Department of Industrial Engineering
 * 	University of Arkansas
 * 	4207 Bell Engineering Center
 * 	Fayetteville, AR 72701
 * 	Phone: (479) 575-6756
 * 	Email: rossetti@uark.edu
 * 	Web: www.uark.edu/~rossetti
 * 
 *  This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 *  of Java classes that permit the easy development and execution of discrete event
 *  simulation programs.
 * 
 *  The JSL is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  The JSL is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jsl.modeling.elements.spatial;

import jsl.utilities.random.RandomIfc;

/**
 * An interface for working with velocity
 *
 * @author rossetti
 */
public interface VelocityIfc {

    /**
     * The factor will be used to increase or decrease the velocity returned by
     * getVelocity()
     *
     * @param factor must be greater than zero
     */
    void setVelocityChangeFactor(double factor);

    /**
     * The factor will be used to increase or decrease the velocity returned by
     * getVelocity()
     *
     * @return the factor
     */
    double getVelocityChangeFactor();

    /**
     *
     * @return the velocity
     */
    double getVelocity();

    /**
     * @return Returns the velocity.
     */
    RandomIfc getVelocityInitialRandomSource();

    /**
     * Sets the underlying initial random source associated with the
     * determination of the velocity
     *
     * @param source the source
     */
    void setVelocityInitialRandomSource(RandomIfc source);

    /**
     * @return Returns the velocity.
     */
    RandomIfc getVelocityRandomSource();

    /**
     * Sets the current underlying random source associated with the
     * determination of the velocity
     *
     * @param source the source
     */
    void setVelocityRandomSource(RandomIfc source);

}
