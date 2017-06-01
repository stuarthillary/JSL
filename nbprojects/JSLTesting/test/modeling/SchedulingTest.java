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
package modeling;

import jsl.modeling.Experiment;
import jsl.modeling.ExperimentGetIfc;
import jsl.modeling.Model;
import jsl.modeling.Simulation;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author rossetti
 */
public class SchedulingTest {

    @Before
    public void setUp() {
    }

    @Test
    public void test1() {
        Simulation mySim = new Simulation();
        ExperimentGetIfc myExp = mySim.getExperiment();
        Model myModel = mySim.getModel();

        mySim.setNumberOfReplications(2);

        System.out.println("Begin Test 1 ------------------------------------------");
        System.out.println("Test scheduling in the constructor");

        ScheduleInConstructor s = new ScheduleInConstructor(myModel);

        mySim.run();

        System.out.println(mySim);
        System.out.println(mySim.getExecutive());
        System.out.println("End Test 1 ------------------------------------------");
        assertTrue(true);
    }

    @Test
    public void test2() {
        Simulation mySim = new Simulation();
        ExperimentGetIfc myExp = mySim.getExperiment();
        Model myModel = mySim.getModel();

        mySim.setNumberOfReplications(2);
        mySim.setLengthOfReplication(25.0);

        System.out.println();
        System.out.println("Begin Test 2 ------------------------------------------");
        System.out.println("Test scheduling in the constructor");

        ScheduleInConstructor s = new ScheduleInConstructor(myModel);

        mySim.run();

        System.out.println(mySim);
        System.out.println(mySim.getExecutive());
        System.out.println("End Test 2 ------------------------------------------");
        assertTrue(true);
    }
}
