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
package jsl.utilities.random.arta;

import jsl.utilities.random.distributions.Beta;
import jsl.utilities.random.distributions.Distribution;
import jsl.utilities.random.distributions.Lognormal;
import jsl.utilities.random.distributions.Normal;
import jsl.utilities.statistic.Statistic;
import jsl.utilities.statistic.StatisticAccessorIfc;
import jsl.utilities.statistic.StatisticXY;

/**
 * @author rossetti
 *
 */
public class BivariateNORTACorrelationEvaluator {

	/** Standard normal
	 * 
	 */
	protected Normal snd;
	
	/** The distribution from which we want the
	 *  correlated random variates
	 * 
	 */
	protected Distribution myD1;
	
	/** The distribution from which we want the
	 *  correlated random variates
	 * 
	 */
	protected Distribution myD2;

	/** The sample size used to estimate the actual
	 *  correlation within one replication.  The 
	 *  default is 1000
	 * 
	 */
	protected int mySampleSize = 1000;
	
	/** The number of replications of the provided sample size
	 *  that are used to estimate the actual correlation
	 *  The default is 10.
	 */
	protected int myNumReps = 10;
	
	/** Whether or not to use antithetic variates in
	 *  the replications uses to estimate the correlation
	 *  The default is false
	 * 
	 */
	protected boolean myAntitheticFlag = false;

	/** Used to estimate the statistics of the indicated sample size
	 * 
	 */
	protected StatisticXY statXY;
	
	/** Used to estimate the statistics across replications for the correlation
	 * 
	 */
	protected Statistic myCorrStat;
	
	/** The input correlation
	 * 
	 */protected double myCorr;
	 
	/** 
	 *  
	 * @param d1
	 * @param d2
	 * @param correlation
	 * @param sampleSize
	 * @param numReps
	 * @param antitheticFlag
	 */
	public BivariateNORTACorrelationEvaluator(Distribution d1, Distribution d2, double correlation,
			int sampleSize, int numReps, boolean antitheticFlag) {
		setFirstDistribution(d1);
		setSecondDistribution(d2);
		statXY = new StatisticXY();
		myCorrStat = new Statistic("Lag 1 across rep statistcs");
		setCorrelation(correlation);
		setSampleSize(sampleSize);
		setSampleSize(sampleSize);
		setNumberOfReplications(numReps);
		setAntitheticFlag(antitheticFlag);	
		snd = new Normal();
	}
	
	/**
	 * @return the distribution
	 */
	public final Distribution getFirstDistribution() {
		return myD1;
	}

	/**
	 * @param distribution the distribution to set
	 */
	public final void setFirstDistribution(Distribution distribution) {
		if (distribution == null)
			throw new IllegalArgumentException("The supplied distribution was null");
		
		myD1 = distribution;
	}

	/**
	 * @return the distribution
	 */
	public final Distribution getSecondDistribution() {
		return myD2;
	}

	/**
	 * @param distribution the distribution to set
	 */
	public final void setSecondDistribution(Distribution distribution) {
		if (distribution == null)
			throw new IllegalArgumentException("The supplied distribution was null");
		
		myD2 = distribution;
	}
	
	/** This is the correlation set within the NORTA process
	 * @return the correlation
	 */
	public final double getCorrelation() {
		return myCorr;
	}

	/** This is the correlation that will be used within the NORTA process
	 *  This is not the desired correlation or the actual resulting correlation
	 *  
	 * @param corr the correlation to set
	 */
	public final void setCorrelation(double corr) {
		if ( (corr <= -1) || (corr >= 1))
			throw new IllegalArgumentException("Phi must be (-1,1)");
		myCorr = corr;
		statXY.reset();
		myCorrStat.reset();

	}

	/** This is the sample size used within each replication to estimate
	 *  the actual correlation
	 *  
	 * @return the sample size
	 */
	public final int getSampleSize() {
		return mySampleSize;
	}

	/**
	 * @param sampleSize the sample size to set, must be &gt; 2
	 */
	public final void setSampleSize(int sampleSize) {
		if (sampleSize < 3)
			throw new IllegalArgumentException("The sample size must be > 2");
		
		mySampleSize = sampleSize;
	}
	
	/** The number of replications of the provided sample size
	 *  used to estimate the actual correlation
	 *  
	 * @return the number of replications
	 */
	public final int getNumberOfReplications() {
		return myNumReps;
	}

	/** The number of replications of the provided sample size
	 *  used to estimate the actual correlation
	 * @param numReps the number of replications
	 */
	public final void setNumberOfReplications(int numReps) {
		if (numReps < 1)
			throw new IllegalArgumentException("The number of replications must be >=1");
		myNumReps = numReps;
	}

	/** The antithetic flag can be use to turn on antithethic sampling
	 *  when estimating the correlation with multiple replications
	 *  
	 * @return the antitheticFlag
	 */
	public final boolean getAntitheticFlag() {
		return myAntitheticFlag;
	}

	/** The antithetic flag can be use to turn on antithethic sampling
	 *  when estimating the correlation with multiple replications. True
	 *  means that antithetic sampling will be used.  When this is set
	 *  the number of replications represents the number of antithetic
	 *  pairs to be sampled.
	 *  
	/**
	 * @param antitheticFlag the flag to set
	 */
	public final void setAntitheticFlag(boolean antitheticFlag) {
		myAntitheticFlag = antitheticFlag;
	}

	/** Estimates the correlation based on the provided number
	 *  of replications of the given sample size
	 * @return
	 */
	public final double estimateCorrelation(){
		return (estimateCorrelation(myNumReps, mySampleSize));
	}
	
	/** Estimates the correlation based on the provided number
	 *  of replications of the given sample size
	 * 
	 * @param numReps must be &gt;=1
	 * @return
	 */
	public final double estimateCorrelation(int numReps){
		return (estimateCorrelation(numReps, mySampleSize));
	}
	
	/** Estimates the correlation based on the provided number
	 *  of replications of the given sample size
	 * 
	 * @param numReps must be &gt;=1
	 * @param sampleSize must be &gt; 3
	 * @return
	 */
	public final double estimateCorrelation(int numReps, int sampleSize){
		if (numReps < 1)
			throw new IllegalArgumentException("The number of replications must be >=1");
		if (sampleSize < 3)
			throw new IllegalArgumentException("The sample size must be > 2");

		myCorrStat.reset();
		
		if (myAntitheticFlag){
			double xodd = 0.0;
			int n = numReps*2;
			for(int i=1;i<=n;i++){
				double erho = sampleCorrelation(sampleSize);				
				if ( (i%2) == 0){
					double x = (xodd + erho)/2.0;
					myCorrStat.collect(x);
					snd.setAntitheticOption(false);
				} else {
					xodd = erho;
					snd.setAntitheticOption(true);
				}
			}
			snd.setAntitheticOption(false);
		} else {
			for(int i=1;i<=numReps;i++){
				myCorrStat.collect(sampleCorrelation(sampleSize));
				//System.out.println("hw = " + myLag1Stat.getHalfWidth() + " i = " + i);
			}
		}
		
		return(myCorrStat.getAverage());
	}

	/** Estimates the correlation to the precision of the half-width bound
	 *  The maximum number of replications is set at 20*getNumberOfReplications()
	 *  The size of each sample for an individual replication is getSampleSize()
	 *  
	 * @param hwBound
	 * @return
	 */
	public final double estimateCorrelation(double hwBound){
		return estimateCorrelation(hwBound, mySampleSize, 100*getNumberOfReplications());
	}
	
	/** Estimates the correlation to the precision of the half-width bound
	 *  The maximum number of replications is set at 20*getNumberOfReplications()
	 * 
	 * @param hwBound
	 * @param sampleSize The size of each sample for an individual replication
	 * @return
	 */
	public final double estimateCorrelation(double hwBound, int sampleSize){
		return estimateCorrelation(hwBound, sampleSize, 100*getNumberOfReplications());
	}
	
	/** Estimates the correlation to the precision of the half-width bound or
	 *  until the specified number of replications has been met
	 * 
	 * 
	 * @param hwBound
	 * @param sampleSize The size of each sample for an individual replication
	 * @param numReps The maximum number of replications
	 * @return
	 */
	public final double estimateCorrelation(double hwBound, int sampleSize, int numReps){
       
        if (hwBound <=0)
            throw new IllegalArgumentException("Half-width bound must be > 0.");
 
		if (sampleSize < 3)
			throw new IllegalArgumentException("The sample size must be > 2");

		if (numReps < 1)
			throw new IllegalArgumentException("The number of replications must be >=1");

		myCorrStat.reset();

		double hw = Double.MAX_VALUE;
		
		if (myAntitheticFlag){			
			double xodd = 0.0;
			int k = numReps*2;
			for(int i=1;i<=k;i++){
				double erho = sampleCorrelation(sampleSize);				
				if ( (i%2) == 0){
					double x = (xodd + erho)/2.0;
					myCorrStat.collect(x);
					snd.setAntitheticOption(false);
					hw = myCorrStat.getHalfWidth();
					if (hw <= hwBound)
						break;
				} else {
					xodd = erho;
					snd.setAntitheticOption(true);
				}
			}
			snd.setAntitheticOption(false);
		} else {
			for(int i=1;i<=numReps;i++){
				myCorrStat.collect(sampleCorrelation(sampleSize));
				hw = myCorrStat.getHalfWidth();
				//System.out.println("hw = " + hw + " i = " + i);
				if (hw <= hwBound)
					break;
			}		
		}
		
		return(myCorrStat.getAverage());

	}

	/** After the correlation has been estimated, this method can
	 *  be used to get the statistics across the replications on
	 *  the correlation
	 * 
	 * @return
	 */
	public final StatisticAccessorIfc getCorrelationStatistics(){
		return myCorrStat;
	}
	
	/** Returns an estimate of the correlation based on a
	 *  sample of the provided size
	 * 
	 * @param sampleSize must be &gt; 2
	 * @return
	 */
	public final double sampleCorrelation(int sampleSize){
		if (sampleSize < 3)
			throw new IllegalArgumentException("The sample size must be > 2");
		statXY.reset();
		for(int i=1;i<=sampleSize;i++){
			double z1 = snd.getValue();
			double z2 = myCorr*z1 + Math.sqrt(1.0-myCorr*myCorr)*snd.getValue();
			double u1 = snd.cdf(z1);
			double u2 = snd.cdf(z2);
			double x = myD1.invCDF(u1);
			double y= myD2.invCDF(u2);
			statXY.collectXY(x, y);			
		}
		return statXY.getCorrelationXY();
	}
	
	/** After an individual sample for a replication has been generated
	 *  this method can provide the statistics on the sample
	 * 
	 * @return
	 */
	public final StatisticXY getSampleStatistics(){
		return statXY;
	}

	/** Returns a String representation
	 * @return A String with basic results
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("\nNumber of replications = " + myCorrStat.getCount() + "\n");
		sb.append("Sample size = " + statXY.getCount() + "\n");
		sb.append("Antithetic flag option = " + myAntitheticFlag + "\n");
		sb.append("Specified lag 1 correlation = " + getCorrelation() + "\n");
		sb.append("Correlation statistics based on last call to estimateCorrelation() \n \n");
		sb.append(myCorrStat);
		return(sb.toString());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Distribution xd = new Beta(1.5162, 2.1784);
		Distribution yd = new Lognormal(0.289, 0.336);

		double lag1 = 0.8;
		int n = 10000;
		int r = 10;
		boolean flag = false;
		
		BivariateNORTACorrelationEvaluator y = new BivariateNORTACorrelationEvaluator(xd, yd, lag1, n, r, flag);
		
		y.estimateCorrelation();
		
		System.out.println(y);
		
//		y.estimateCorrelation(0.001);
		
//		System.out.println(y);
	}

}
