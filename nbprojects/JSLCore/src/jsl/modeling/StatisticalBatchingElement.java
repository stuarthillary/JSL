/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling;

import java.util.ArrayList;
import java.util.List;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.ResponseVariableBatchingElement;
import jsl.modeling.elements.variable.TWBatchingElement;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.observers.variable.BatchStatisticObserver;
import jsl.utilities.reporting.StatisticReporter;
import jsl.utilities.statistic.BatchStatistic;
import jsl.utilities.statistic.StatisticAccessorIfc;

/**
 * When added to a Model, this class will cause batch statistics to be collected
 * for ResponseVariables and TimeWeighted variables. It uses the
 * TWBatchingElement and the ResponseVariableBatchingElement to perform this
 * functionality.
 *
 *
 * @author rossetti
 */
public class StatisticalBatchingElement extends ModelElement {

    private final TWBatchingElement myTWBatcher;

    private final ResponseVariableBatchingElement myRVBatcher;

    /**
     * Creates a StatisticalBatchingElement using the default discretizing
     * interval defined in TWBatchingElement
     *
     * @param model
     */
    public StatisticalBatchingElement(Model model) {
        this(model, 0.0, null);
    }

    /**
     * Creates a StatisticalBatchingElement
     *
     * @param model
     * @param batchInterval the discretizing interval for TimeWeighted variables
     */
    public StatisticalBatchingElement(Model model, double batchInterval) {
        this(model, batchInterval, null);
    }

    /**
     * Creates a StatisticalBatchingElement
     *
     * @param model
     * @param batchInterval the discretizing interval for TimeWeighted variables
     * @param name
     */
    public StatisticalBatchingElement(Model model, double batchInterval, String name) {
        super(model, name);
        myTWBatcher = new TWBatchingElement(this, batchInterval);
        myRVBatcher = new ResponseVariableBatchingElement(this);
    }

    /**
     * Look up the BatchStatisticObserver for the ResponseVariable
     *
     * @param key the ResponseVariable to look up
     * @return the BatchStatisticObserver
     */
    public final BatchStatisticObserver getBatchStatisticObserver(ResponseVariable key) {
        if (key instanceof TimeWeighted) {
            return myTWBatcher.getTWBatchStatisticObserver((TimeWeighted) key);
        } else {
            return myRVBatcher.getBatchStatisticObserver(key);
        }
    }

    /**
     * Removes the supplied ResponseVariable variable from the batching
     *
     * @param r the ResponseVariable to be removed
     */
    public final void remove(ResponseVariable r) {
        if (r instanceof TimeWeighted) {
            myTWBatcher.remove((TimeWeighted) r);
        } else {
            myRVBatcher.remove(r);
        }
    }

    /**
     * Removes all previously added ResponseVariable from the batching
     *
     */
    public final void removeAll() {
        myTWBatcher.removeAll();
        myRVBatcher.removeAll();
    }

    /**
     * Returns a statistical summary BatchStatistic on the ResponseVariable
     * variable across the observed batches This returns a copy of the summary
     * statistics.
     *
     * @param r the ResponseVariable to look up
     * @return the returned BatchStatistic
     */
    public final BatchStatistic getBatchStatistic(ResponseVariable r) {
        if (r instanceof TimeWeighted) {
            return myTWBatcher.getBatchStatistic((TimeWeighted) r);
        } else {
            return myRVBatcher.getBatchStatistic(r);
        }
    }

    /**
     * Returns a list of summary statistics on all ResponseVariable variables
     * The list is a copy of originals.
     *
     * @return the filled up list
     */
    public final List<BatchStatistic> getAllBatchStatisitcs() {
        List<BatchStatistic> list = myTWBatcher.getAllBatchStatisitcs();
        list.addAll(myRVBatcher.getAllBatchStatisitcs());
        return list;
    }

    /**
     * Returns a list of the batch statistics in the form of
     * StatisticAccessorIfc
     *
     * @return
     */
    public final List<StatisticAccessorIfc> getAllStatistics() {
        List<StatisticAccessorIfc> list = new ArrayList();
        List<BatchStatistic> allBatchStatisitcs = getAllBatchStatisitcs();
        list.addAll(allBatchStatisitcs);
        return list;
    }

    /**
     * Returns a StatisticReporter for reporting the statistics across the
     * batches.
     *
     * @return
     */
    public final StatisticReporter getStatisticReporter() {
        StatisticReporter sr = new StatisticReporter(getAllStatistics());
        sr.setReportTitle("Batch Summary Report");
        return sr;
    }

    @Override
    protected void beforeExperiment() {
        removeAll();
        // now add all appropriate responses to the batching
        Model m = getModel();
        List<ResponseVariable> list = m.getResponseVariables();
        for (ResponseVariable r : list) {
            if (r instanceof TimeWeighted) {
                myTWBatcher.add((TimeWeighted) r);
            } else {
                myRVBatcher.add(r);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(myTWBatcher.toString());
        sb.append(myRVBatcher.toString());
        return sb.toString();
    }
}
