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

/** A concrete sub-class of AbstractRG2Mover that randomly moves around
 *
 * @author rossetti
 */
public class RG2DMover extends AbstractRG2DMover {

    public RG2DMover(RectangularGridModel parent) {
        this(parent, new Vector3D(), null);
    }

    public RG2DMover(RectangularGridModel parent, String name) {
        this(parent, new Vector3D(), name);
    }

    public RG2DMover(RectangularGridModel parent, CoordinateIfc coordinate, String name) {
        super(parent, coordinate, name);
    }

    @Override
    protected void initialize() {
        super.initialize();
        if (isStartRandomlyOption()) {
            moveToCenterOfCell(selectNeighborRandomly());
        }
    }

    @Override
    protected void startNextTrip() {
        moveToCenterOfCell(selectNeighborRandomly());
    }

}
