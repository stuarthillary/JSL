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

import java.util.List;

/** This interface governs the selection of resources from
 *  a set
 *
 * @author rossetti
 */
public interface ResourceSelectionRuleIfc {

    /** Finds a resource within the supplied list that has
     *  getNumberAvailable() &gt; = amtNeeded or returns null
     *
     *
     * @param list
     * @param amtNeeded
     * @return
     */
    public Resource selectAvailableResource(List<Resource> list, int amtNeeded);

    /** Places the supplied resource (if resource.hasAvailableUnits()) in the list
     *
     * @param list
     * @param resource
     */
    public void addAvailableResource(List<Resource> list, Resource resource);

    /** Selects an available resource from the list or returns null if none
     *  are found
     *
     * @param list
     * @return
     */
    public Resource selectAvailableResource(List<Resource> list);

}
