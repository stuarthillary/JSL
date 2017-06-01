/*
 * Created on Nov 4, 2006
 *
 */
package jsl.utilities.rootfinding;

/**
 * @author rossetti
 *
 */
public class Interval {

    protected double myLower;

    protected double myUpper;

    /**
     * 
     * @param xLower
     * @param xUpper  
     */
    public Interval(double xLower, double xUpper) {
        setInterval(xLower, xUpper);
    }

    /** Sets the interval 
     *  Throws IllegalArgumentExceptons if the lower limit is &gt;= upper limit
     * 
     * @param xLower
     * @param xUpper
     */
    public final void setInterval(double xLower, double xUpper) {

        if (xLower >= xUpper) {
            throw new IllegalArgumentException("The lower limit must be < the upper limit");
        }

        myLower = xLower;
        myUpper = xUpper;
    }

    @Override
    public String toString() {
        return ("[" + myLower + ", " + myUpper + "]");
    }

    public final double getLowerLimit() {
        return myLower;
    }

    public final double getUpperLimit() {
        return myUpper;
    }

    public final boolean contains(double x) {
        return ((myLower <= x) && (x <= myUpper));
    }
}
