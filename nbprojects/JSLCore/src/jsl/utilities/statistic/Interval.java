/*
 * Created on Nov 4, 2006
 *
 */
package jsl.utilities.statistic;

/** Used to represent confidence intervals.  Intervals between two real
 *  numbers where the lower limit must be less than or equal to the upper limit.
 * 
 * @author rossetti
 *
 */
public class Interval {

    protected double myLower;

    protected double myUpper;

    /**
     * 
     * @param xLower the lower limit
     * @param xUpper  the upper limit
     */
    public Interval(double xLower, double xUpper) {
        setInterval(xLower, xUpper);
    }

    /** Sets the interval 
     *  Throws IllegalArgumentExceptons if the lower limit is &gt;= upper limit
     * 
     * @param xLower the lower limit
     * @param xUpper the upper limit
     */
    public final void setInterval(double xLower, double xUpper) {

        if (xLower > xUpper) {
            throw new IllegalArgumentException("The lower limit must be <= the upper limit");
        }

        myLower = xLower;
        myUpper = xUpper;
    }

    /** A new instance with the same interval settings.
     * 
     * @return 
     */
    public final Interval newInstance(){
        return new Interval(this.getLowerLimit(), this.getUpperLimit());
    }
    
    @Override
    public String toString() {
        return ("[" + myLower + ", " + myUpper + "]");
    }

    /**
     * 
     * @return the lower limit of the interval
     */
    public final double getLowerLimit() {
        return myLower;
    }

    /**
     * 
     * @return The upper limit of the interval
     */
    public final double getUpperLimit() {
        return myUpper;
    }
    
    /** The width of the interval
     * 
     * @return 
     */
    public final double getWidth(){
        return myUpper - myLower;
    }
    
    /** Half of the width of the interval
     * 
     * @return 
     */
    public final double getHalfWidth(){
        return getWidth()/2.0;
    }

    /**
     * 
     * @param x the value to check
     * @return true if x is in the interval (includes end points)
     */
    public final boolean contains(double x) {
        return ((myLower <= x) && (x <= myUpper));
    }
    
    /** Checks if the supplied interval is contained within
     *  this interval
     * 
     * @param interval
     * @return true only if both lower and upper limits of supplied interval 
     *  are within this interval
     */
    public final boolean contains(Interval interval){
        if (interval == null){
            return false;
        }
        return contains(interval.getLowerLimit()) && contains(interval.getUpperLimit());
    }
}
