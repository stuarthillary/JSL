package jsl.utilities.random;

public interface SampleIfc {

    /**
     * Generates a random sample of the give size
     *
     * @param sampleSize the amount to fill
     * @return A array holding the sample
     */
    public double[] getSample(int sampleSize);

    /**
     * Fills the supplied array with a random sample
     *
     * @param values the array to fill
     */
    public void getSample(double[] values);

}
