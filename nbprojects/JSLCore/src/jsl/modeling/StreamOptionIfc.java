/*
 *  Copyright (C) 2010 rossetti
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
 *  of Java classes that permit the development and execution of discrete event
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

package jsl.modeling;

/**
 *
 * @author rossetti
 */
public interface StreamOptionIfc {

    /**
     * Gets the current reset next sub-stream option
     * true means, that it is set to jump to the next sub-stream after
     * each replication
     * @return the option
     */
    boolean getResetNextSubStreamOption();

    /**
     * Gets the current Reset Start Stream Option
     * @return the option
     */
    boolean getResetStartStreamOption();

    /**
     * Sets the current reset next sub-stream option
     * true means, that it is set to jump to the next sub-stream after
     * each replication
     * @param b
     */
    void setResetNextSubStreamOption(boolean b);

    /**
     * Sets the reset start stream option, true
     * means that it will be reset to the starting stream
     * @param b true means reset
     */
    void setResetStartStreamOption(boolean b);

}
