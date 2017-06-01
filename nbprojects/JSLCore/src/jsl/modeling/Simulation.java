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

import java.util.TimerTask;
import jsl.observers.ObservableIfc;
import jsl.observers.ObserverIfc;
import jsl.observers.scheduler.ExecutiveTraceReport;
import jsl.observers.textfile.IPLogReport;
import jsl.utilities.IdentityIfc;
import jsl.utilities.reporting.JSL;

/**
 * Simulation represents a model and experiment that can be run. It encapsulates
 * a model to which model elements can be attached. It allows an experiment and
 * its run parameters to be specified. Finally, it allows reporting of results
 * to files via a SimulationReporter
 *
 * @author Manuel Rossetti (rossetti@uark.edu)
 */
public class Simulation implements IdentityIfc, ObservableIfc, IterativeProcessIfc,
        ExperimentGetIfc {

    /**
     * A counter to count the number of objects created to assign "unique" ids
     */
    private static int myIdCounter_;

    /**
     * The name of this object
     */
    private String myName;

    /**
     * The id of this object
     */
    private int myId;

    /**
     * The executive for running events
     *
     */
    protected Executive myExecutive;

    /**
     * The experiment for running the simulation
     *
     */
    protected Experiment myExperiment;

    /**
     * The model to simulate
     *
     */
    protected Model myModel;

    /**
     * Controls the execution of replications
     */
    protected ReplicationExecutionProcess myReplicationExecutionProcess;

    /**
     * A flag to control whether or not a warning is issues if the user does not
     * set the replication run length
     *
     */
    private boolean myRepLengthWarningMsgOption = true;

    /**
     * Used to control statistical batching
     *
     */
    private StatisticalBatchingElement myBatchingElement;

    /**
     * Creates a simulation with name, "Simulation" to run an empty model with
     * default experimental parameters using the default scheduling executive
     *
     */
    public Simulation() {
        this(null, null, null, null);
    }

    /**
     * Creates a simulation to run the model according to the experimental
     * parameters using the default scheduling executive
     *
     * @param simName
     */
    public Simulation(String simName) {
        this(simName, null, null, null);
    }

    /**
     * Creates a simulation to run the model according to the experimental
     * parameters using the default scheduling executive
     *
     * @param simName
     * @param expName
     */
    public Simulation(String simName, String expName) {
        this(simName, null, expName, null);
    }

    /**
     * Creates a simulation to run the model according to the experimental
     * parameters using the default scheduling executive
     *
     * @param simName
     * @param modelName
     * @param expName
     */
    public Simulation(String simName, String modelName, String expName) {
        this(simName, modelName, expName, null);
    }

    /**
     * Creates a simulation to run a model according to the experimental
     * parameters using the supplied scheduling executive
     *
     * @param simName
     * @param modelName
     * @param expName
     * @param executive
     */
    public Simulation(String simName, String modelName, String expName,
            Executive executive) {
        myIdCounter_ = myIdCounter_ + 1;
        myId = myIdCounter_;
        setName(simName);
        myReplicationExecutionProcess = new ReplicationExecutionProcess();
        myExperiment = new Experiment(expName);
        if (executive == null) {
            executive = new Executive();
        }
        myExecutive = executive;
        if (modelName == null) {
            modelName = getName() + "_Model";
        }
        myModel = new Model(modelName);
        //setSimulation() is package final, should be no leaking this
        myModel.setSimulation(this);
    }

    /**
     * Gets the name.
     *
     * @return The name of object.
     */
    @Override
    public final String getName() {
        return myName;
    }

    /**
     * Returns the id for this object
     *
     * @return
     */
    @Override
    public final long getId() {
        return (myId);
    }

    /**
     * The Experiment associated with the simulation
     *
     * @return
     */
    public final ExperimentGetIfc getExperiment() {
        return myExperiment;
    }

    /**
     * The Model associated with the simulation
     *
     * @return
     */
    public final Model getModel() {
        return myModel;
    }

    /**
     * The Executive associated with the simulation
     *
     * @return
     */
    public final Executive getExecutive() {
        return myExecutive;
    }

    /**
     * A StatisticalBatchingElement is used to control statistical batching for single
     * replication simulations. This method creates and attaches a
     * StatisticalBatchingElement to the model
     *
     * @return
     */
    public final StatisticalBatchingElement getStatisticalBatchingElement() {
        if (myBatchingElement == null) {
            return new StatisticalBatchingElement(getModel());
        } else {
            return myBatchingElement;
        }
    }

    /**
     * Sets the name of the simulation
     *
     * @param str The name as a string.
     */
    @Override
    public final void setName(String str) {
        if (str == null) {
            myName = this.getClass().getSimpleName() + "_" + getId();
        } else {
            myName = str;
        }
    }

    /**
     * Simulation implements ObservableIfc. This allows deletion of all
     * observers attached to the simulation
     *
     */
    @Override
    public final void deleteObservers() {
        myReplicationExecutionProcess.deleteObservers();
    }

    /**
     * Simulation implements ObservableIfc. This allows deletion of an observer
     * attached to the simulation
     *
     * @param observer
     */
    @Override
    public final void deleteObserver(ObserverIfc observer) {
        myReplicationExecutionProcess.deleteObserver(observer);
    }

    /**
     * Indicated how many observers are attached
     *
     * @return
     */
    @Override
    public final int countObservers() {
        return myReplicationExecutionProcess.countObservers();
    }

    /**
     * Allows an observer to be added to the simulation. The observer observes
     * an IterativeProcess that manages the execution of the replications. Each
     * step in the IterativeProcess represents an entire replication.
     *
     * @param observer
     */
    @Override
    public final void addObserver(ObserverIfc observer) {
        myReplicationExecutionProcess.addObserver(observer);
    }

    @Override
    public boolean contains(ObserverIfc observer) {
        return myReplicationExecutionProcess.contains(observer);
    }

    /**
     * Returns true if additional replications need to be run
     *
     * @return
     */
    public final boolean hasNextReplication() {
        return myReplicationExecutionProcess.hasNext();
    }

    /**
     * Initializes the simulation in preparation for running
     *
     */
    @Override
    public final void initialize() {
        myReplicationExecutionProcess.initialize();
    }

    /**
     * Runs the next replication if there is one
     *
     */
    @Override
    public final void runNext() {
        myReplicationExecutionProcess.runNext();
    }

    /**
     * Runs all remaining replications
     *
     */
    @Override
    public final void run() {
        myReplicationExecutionProcess.run();
    }

    /**
     * Causes the simulation to end after the current replication is completed
     *
     * @param msg A message to indicate why the simulation was stopped
     */
    @Override
    public final void end(String msg) {
        myReplicationExecutionProcess.end(msg);
    }

    /**
     * Causes the simulation to end after the current replication is completed
     *
     */
    @Override
    public final void end() {
        myReplicationExecutionProcess.end();
    }

    /**
     * Checks if the replications were finished
     *
     * @return
     */
    @Override
    public final boolean isUnfinished() {
        return myReplicationExecutionProcess.isUnfinished();
    }

    /**
     * Checks if the simulation stopped because of real clock time
     *
     * @return
     */
    @Override
    public final boolean executionTimeExceeded() {
        return myReplicationExecutionProcess.executionTimeExceeded();
    }

    /**
     * Part of the IterativeProcessIfc. Checks if a step in the process is
     * completed. A step is a replication Checks if the state of the simulation
     * is that it just completed a replication
     *
     * @return
     */
    @Override
    public final boolean isStepCompleted() {
        return myReplicationExecutionProcess.isStepCompleted();
    }

    /**
     * Checks if the simulation is running. Running means that it is executing
     * replications
     *
     * @return
     */
    @Override
    public final boolean isRunning() {
        return myReplicationExecutionProcess.isRunning();
    }

    /**
     * Checks if the simulation has been initialized. If it is initialized, then
     * it is ready to run replications
     *
     * @return
     */
    @Override
    public final boolean isInitialized() {
        return myReplicationExecutionProcess.isInitialized();
    }

    /**
     * Checks to see if the simulation is in the ended state If it is ended, it
     * may be for a number of reasons
     *
     * @return
     */
    @Override
    public final boolean isEnded() {
        return myReplicationExecutionProcess.isEnded();
    }

    @Override
    public final boolean isCreated() {
        return myReplicationExecutionProcess.isCreated();
    }

    /**
     * Checks if the simulation has ended because it was stopped
     *
     * @return
     */
    @Override
    public final boolean stoppedByCondition() {
        return myReplicationExecutionProcess.stoppedByCondition();
    }

    /**
     * Checks if the simulation is done processing replications
     *
     * @return
     */
    @Override
    public final boolean isDone() {
        return myReplicationExecutionProcess.isDone();
    }

    /**
     * Checks if the simulation completed all of its replications
     *
     * @return
     */
    @Override
    public final boolean allStepsCompleted() {
        return myReplicationExecutionProcess.allStepsCompleted();
    }

    /**
     * Sets a real clock time for how long the entire simulation can last
     *
     * @param milliseconds
     */
    @Override
    public final void setMaximumExecutionTime(long milliseconds) {
        myReplicationExecutionProcess.setMaximumExecutionTime(milliseconds);
    }

    /**
     * Returns the real clock time in milliseconds for how long the simulation
     * is allowed to run
     *
     * @return
     */
    @Override
    public final long getMaximumAllowedExecutionTime() {
        return myReplicationExecutionProcess.getMaximumAllowedExecutionTime();
    }

    /**
     * The absolute time in milliseconds that the simulation ended
     *
     * @return
     */
    @Override
    public final long getEndExecutionTime() {
        return myReplicationExecutionProcess.getEndExecutionTime();
    }

    /**
     * The time in milliseconds between when the simulation was started and the
     * simulation ended
     *
     * @return
     */
    @Override
    public final long getElapsedExecutionTime() {
        return myReplicationExecutionProcess.getElapsedExecutionTime();
    }

    /**
     * The absolute time in milliseconds that the simulation was started
     *
     * @return
     */
    @Override
    public final long getBeginExecutionTime() {
        return myReplicationExecutionProcess.getBeginExecutionTime();
    }

    /**
     * The message supplied with stop()
     *
     * @return
     */
    @Override
    public final String getStoppingMessage() {
        return myReplicationExecutionProcess.getStoppingMessage();
    }

    /**
     * Turns on a timer and task that can be attached to the execution
     *
     * @param milliseconds
     * @param timerTask
     */
    @Override
    public final void turnOnTimer(long milliseconds, TimerTask timerTask) {
        myReplicationExecutionProcess.turnOnTimer(milliseconds, timerTask);
    }

    /**
     * Turns on a default timer and task to report on simulation progress
     *
     * @param milliseconds
     */
    @Override
    public final void turnOnTimer(long milliseconds) {
        myReplicationExecutionProcess.turnOnTimer(milliseconds);
    }

    /**
     * Turns on a default logging report with the provided name
     *
     * @param name
     */
    @Override
    public final void turnOnLogReport(String name) {
        myReplicationExecutionProcess.turnOnLogReport(name);
    }

    /**
     * Turns on a default logging report
     *
     */
    @Override
    public final void turnOnLogReport() {
        myReplicationExecutionProcess.turnOnLogReport();
    }

    /**
     * Turns of the default logging report
     *
     */
    @Override
    public final void turnOffLogReport() {
        myReplicationExecutionProcess.turnOffLogReport();
    }

    /**
     * For the IterativeProcessIfc. Returns the number of steps (replications)
     * completed
     *
     * @return
     */
    @Override
    public final long getNumberStepsCompleted() {
        return myReplicationExecutionProcess.getNumberStepsCompleted();
    }

    /**
     * Gets the IPLogReport that was attached to the simulation
     *
     * @return
     */
    @Override
    public final IPLogReport getLogReport() {
        return myReplicationExecutionProcess.getLogReport();
    }

    /**
     * Returns the current number of replications completed
     *
     * @return the number as a double
     */
    @Override
    public final int getCurrentReplicationNumber() {
        return (myExperiment.getCurrentReplicationNumber());
    }

    @Override
    public final boolean hasMoreReplications() {
        return myExperiment.hasMoreReplications();
    }

    /**
     * Returns the number of replications for the experiment
     *
     * @return
     */
    @Override
    public final int getNumberOfReplications() {
        return myExperiment.getNumberOfReplications();
    }

    @Override
    public final int getNumberOfStreamAdvancesPriorToRunning() {
        return myExperiment.getNumberOfStreamAdvancesPriorToRunning();
    }

    /**
     * If set to true then the streams will be reset to the start of there
     * stream prior to running the experiments. True facilitates the use of
     * common random numbers.
     *
     * @param b
     */
    public final void setResetStartStreamOption(boolean b) {
        myExperiment.setResetStartStreamOption(b);
    }

    /**
     * Sets the option to have the streams advance to the beginning of the next
     * substream after each replication
     *
     * @param b
     */
    public final void setAdvanceNextSubStreamOption(boolean b) {
        myExperiment.setAdvanceNextSubStreamOption(b);
    }

    /**
     * Sets whether or not the replication should be initialized before each
     * replication
     *
     * @param repInitOption
     */
    public final void setReplicationInitializationOption(boolean repInitOption) {
        myExperiment.setReplicationInitializationOption(repInitOption);
    }

    /**
     * Sets the number of replications to be executed and whether or not the
     * antithetic option is on. If the antithetic option is on then the number
     * of replications should be divisible by 2 so that antithetic pairs can be
     * formed.
     *
     * @param numReps
     * @param antitheticOption
     */
    public final void setNumberOfReplications(int numReps, boolean antitheticOption) {
        myExperiment.setNumberOfReplications(numReps, antitheticOption);
    }

    /**
     * Sets the number of replications to be executed. The antithetic option is
     * off
     *
     * @param numReps
     */
    public final void setNumberOfReplications(int numReps) {
        myExperiment.setNumberOfReplications(numReps);
    }

    /**
     * Sets in real clock time (milliseconds) the amount of time available for
     * each replication within the simulation. If the replication lasts longer
     * than the supplied time it will be stopped
     *
     * @param milliseconds
     */
    public final void setMaximumExecutionTimePerReplication(long milliseconds) {
        myExperiment.setMaximumExecutionTimePerReplication(milliseconds);
    }

    /**
     * Allows the length of the warm up period for each replication to be set
     *
     *
     * @param lengthOfWarmUp in simulation time
     */
    public final void setLengthOfWarmUp(double lengthOfWarmUp) {
        myExperiment.setLengthOfWarmUp(lengthOfWarmUp);
    }

    /**
     * Sets the length of the replications in simulation time.
     *
     * @param lengthOfReplication
     */
    public final void setLengthOfReplication(double lengthOfReplication) {
        myExperiment.setLengthOfReplication(lengthOfReplication);
    }

    @Override
    public final String getExperimentName() {
        return myExperiment.getExperimentName();
    }

    @Override
    public final long getExperimentId() {
        return myExperiment.getExperimentId();
    }

    /**
     * Returns whether or not the start stream will be reset prior to executing
     * the simulation
     *
     * @return
     */
    @Override
    public final boolean getResetStartStreamOption() {
        return myExperiment.getResetStartStreamOption();
    }

    /**
     * Returns how many times the random number streams will be advanced before
     * the simulation starts.
     *
     * @return
     */
    @Override
    public final boolean getAdvanceNextSubStreamOption() {
        return myExperiment.getAdvanceNextSubStreamOption();
    }

    /**
     * Returns whether or not replications will be initialized prior to running
     * each replication
     *
     * @return
     */
    @Override
    public final boolean getReplicationInitializationOption() {
        return myExperiment.getReplicationInitializationOption();
    }

    /**
     * Gets in real clock time (milliseconds) the amount of time available for
     * each replication within the simulation. If the replication lasts longer
     * than the supplied time it will be stopped
     *
     * @return
     */
    @Override
    public final long getMaximumAllowedExecutionTimePerReplication() {
        return myExperiment.getMaximumAllowedExecutionTimePerReplication();
    }

    /**
     * Provides the length of the warm up period for each replication
     *
     *
     * @return
     */
    @Override
    public final double getLengthOfWarmUp() {
        return myExperiment.getLengthOfWarmUp();
    }

    /**
     * Provides the length of each replication
     *
     *
     * @return
     */
    @Override
    public final double getLengthOfReplication() {
        return myExperiment.getLengthOfReplication();
    }

    /**
     * Indicates whether or not the antithetic streams have been turn on or off
     *
     * @return
     */
    @Override
    public final boolean getAntitheticOption() {
        return myExperiment.getAntitheticOption();
    }

    /**
     * Determines whether or not System.gc() is called after each replication
     *
     * @param flag
     */
    public final void setGarbageCollectAfterRepilicationFlag(boolean flag) {
        myExperiment.setGarbageCollectAfterRepilicationFlag(flag);
    }

    /**
     * Returns whether or not System.gc() is called after each replication
     *
     * @return
     */
    @Override
    public final boolean getGarbageCollectAfterRepilicationFlag() {
        return myExperiment.getGarbageCollectAfterRepilicationFlag();
    }

    /**
     *
     * @return true if the flag permits the message to be printed
     */
    public final boolean getRepLengthWarningMessageOption() {
        return myRepLengthWarningMsgOption;
    }

    /**
     * False turns off the message
     *
     * @param flag
     */
    public final void setRepLengthWarningMessageOption(boolean flag) {
        myRepLengthWarningMsgOption = flag;
    }

    /** Sets the name of the underlying experiment 
     * 
     * @param name the name to set
     */
    public final void setExperimentName(String name){
        myExperiment.setExperimentName(name);
    }
    
    /**
     * Set the simulation's experiment to the same attribute values as the
     * supplied experiment
     *
     * @param e
     */
    protected final void setExperiment(Experiment e) {
        myExperiment.setExperiment(e);
    }

    /**
     * Turns on a default tracing report for the Executive to trace event
     * execution to a file
     *
     * @param name
     */
    public final void turnOnDefaultEventTraceReport(String name) {
        myExecutive.turnOnDefaultEventTraceReport(name);
    }

    /**
     * Turns on a default tracing report for the Executive to trace event
     * execution to a file
     *
     *
     */
    public final void turnOnDefaultEventTraceReport() {
        myExecutive.turnOnDefaultEventTraceReport();
    }

    /**
     * Turns off a default tracing report for the Executive to trace event
     * execution to a file
     */
    public final void turnOffDefaultEventTraceReport() {
        myExecutive.turnOffDefaultEventTraceReport();
    }

    /**
     * Gets a reference to the default event tracing report. May be null if not
     * turned on.
     *
     * @return
     */
    public final ExecutiveTraceReport getDefaultExecutiveTraceReport() {
        return myExecutive.getDefaultExecutiveTraceReport();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Simulation Name: ");
        sb.append(getName());
        sb.append("\n");
        sb.append(myReplicationExecutionProcess);
        sb.append("\n");
        sb.append("Model Name: ");
        sb.append(getModel().getName());
        sb.append("\n");
        sb.append("\n");
        sb.append(getExperiment());
        sb.append("\n");
        sb.append("\n");
        sb.append(getExecutive());
        return sb.toString();
    }

    /**
     *  Constructs a SimulationReporter instance that uses
     *  this Simulation instance
     * @return  the SimulationReporter
     */
    public SimulationReporter makeSimulationReporter() {
        return new SimulationReporter(this);
    }

    /**
     * This method is automatically called at the start of the experiment
     * Sub-classes can inject behavior within here
     *
     */
    protected void beforeExperiment() {
    }

    /**
     * This method is automatically called at the end of the experiment
     * Sub-classes can inject behavior within here
     *
     */
    protected void afterExperiment() {
    }

    /**
     * This method is automatically called before each replication Sub-classes
     * can inject behavior within here
     *
     */
    protected void beforeReplication() {
    }

    /**
     * This method is automatically called after each replication Sub-classes
     * can inject behavior within here
     *
     */
    protected void afterReplication() {
    }

    @Override
    public final boolean isExecutionTimeExceeded() {
        return myReplicationExecutionProcess.isExecutionTimeExceeded();
    }

    @Override
    public final boolean getStoppingFlag() {
        return myReplicationExecutionProcess.getStoppingFlag();
    }

    @Override
    public final void stop() {
        myReplicationExecutionProcess.stop();
    }

    @Override
    public final void stop(String msg) {
        myReplicationExecutionProcess.stop(msg);
    }

    @Override
    public final boolean isRunningStep() {
        return myReplicationExecutionProcess.isRunningStep();
    }

    @Override
    public final boolean noStepsExecuted() {
        return myReplicationExecutionProcess.noStepsExecuted();
    }

    /**
     * This class implements the IterativeProcess behavior for the Simulation
     *
     */
    protected class ReplicationExecutionProcess extends IterativeProcess<Executive> {

        @Override
        protected final void initializeIterations() {
            super.initializeIterations();
            myExecutive.setTerminationWarningMessageOption(false);

            myExperiment.resetCurrentReplicationNumber();
            beforeExperiment();
            myModel.setUpExperiment();
            if (getRepLengthWarningMessageOption()) {
                if (Double.isInfinite(myExperiment.getLengthOfReplication())) {
                    if (getMaximumAllowedExecutionTimePerReplication() == 0) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Simulation: In initializeIterations()\n");
                        sb.append("The experiment has an infinite horizon.\n");
                        sb.append("There was no maximum real-clock execution time specified. \n");
                        sb.append("The user is responsible for ensuring that the Executive is stopped.\n");
                        JSL.LOGGER.warning(sb.toString());
                        System.out.flush();
                    }
                }
            }

        }

        @Override
        protected final void endIterations() {
            myModel.afterExperiment(myExperiment);
            afterExperiment();
            super.endIterations();
        }

        @Override
        protected boolean hasNext() {
            return myExperiment.hasMoreReplications();
        }

        @Override
        protected final Executive next() {
            if (!hasNext()) {
                return null;
            }

            return (myExecutive);
        }

        @Override
        protected final void runStep() {
            myCurrentStep = next();
            myExperiment.incrementCurrentReplicationNumber();
            long tpr = getMaximumAllowedExecutionTimePerReplication();
            if (tpr > 0) {
                myExecutive.setMaximumExecutionTime(tpr);
            }
            beforeReplication();
            myExecutive.initialize();
            myModel.setUpReplication();
            myExecutive.executeAllEvents();
            myModel.afterReplication(myExperiment);
            afterReplication();
            if (getGarbageCollectAfterRepilicationFlag()) {
                System.gc();
            }
        }
    }
}
