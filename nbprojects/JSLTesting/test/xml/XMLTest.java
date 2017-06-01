package xml;

import java.io.InputStream;
import java.io.Reader;
import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import jsl.modeling.Experiment;
import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.statistic.Statistic;
import nu.xom.*;

import org.junit.*;
import static org.junit.Assert.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rossetti
 */
public class XMLTest {

    @Before
    public void setUp() {
        System.out.println("XML Tests");
    }

    @Test
    public void testExperimentXML() {
        Experiment e1 = new Experiment();
        String s1 = toXML(e1);
        System.out.println(toXML(e1));
        Experiment e2 = makeExperiment(s1);
        String s2 = toXML(e2);
        assertTrue(s1.equals(s2));
    }

    /** Returns a string representation of the XML based on
     *  usage of XStream technology.  This is not a DOM or XOM
     *  document
     *
     * @param e 
     * @return
     */
    public static String toXML(Experiment e) {
        XStream xstream = new XStream();
        xstream.setMode(XStream.ID_REFERENCES);
        setExperimentXStreamAliases(xstream);
        return xstream.toXML(e);
    }

    /** Creates a new instance of Network from a string that
     *  was returned by String toXML()
     *
     * @param xml
     * @return
     */
    public static Experiment makeExperiment(String xml) {
        XStream xstream = new XStream();
        xstream.setMode(XStream.ID_REFERENCES);
        setExperimentXStreamAliases(xstream);
        return (Experiment) xstream.fromXML(xml);
    }

    /** Creates a new instance of Network from a Reader that
     *  has a xml string representation was returned by String toXML()
     *
     * @param xml
     * @return
     */
    public static Experiment makeExperiment(Reader xml) {
        XStream xstream = new XStream();
        xstream.setMode(XStream.ID_REFERENCES);
        setExperimentXStreamAliases(xstream);
        return (Experiment) xstream.fromXML(xml);
    }

    /** Creates a new instance of Network from an InputStream that
     *  has a xml string representation was returned by String toXML()
     *
     * @param xml
     * @return
     */
    public static Experiment makeExperiment(InputStream xml) {
        XStream xstream = new XStream();
        xstream.setMode(XStream.ID_REFERENCES);
        setExperimentXStreamAliases(xstream);
        return (Experiment) xstream.fromXML(xml);
    }

    public static void setExperimentXStreamAliases(XStream x) {
        x.alias("Experiment", Experiment.class);
        x.aliasField("name", Experiment.class, "myName");
        x.aliasField("id", Experiment.class, "myId");
        x.aliasField("numberOfReplications", Experiment.class, "myNumReps");
        x.aliasField("currendReplicationNumber", Experiment.class, "myCurRepNum");
        x.aliasField("lengthOfReplication", Experiment.class, "myLengthOfReplication");
        x.aliasField("lengthOfWarmUp", Experiment.class, "myLengthOfWarmUp");
        x.aliasField("replicationInitialization", Experiment.class, "myRepInitOption");
        x.aliasField("resetStartStream", Experiment.class, "myResetStartStreamOption");
        x.aliasField("advanceNextSubStream", Experiment.class, "myAdvNextSubStreamOption");
        x.aliasField("antitheticOption", Experiment.class, "myAntitheticOption");
        x.aliasField("numberOfSteamAdvancements", Experiment.class, "myAdvStreamNum");
        x.aliasField("maxAllowedTimePerReplication", Experiment.class, "myMaxAllowedExecutionTimePR");
        x.aliasField("garbageCollectAfterReplication", Experiment.class, "myGCAfterRepFlag");
    }

    //@Test
    public void testXstream1() {
        Statistic s = new Statistic("test xml stat");

        Normal n = new Normal();

        s.collect(n.getSample(100));

        System.out.println(s);

        XStream xstream = new XStream();

        String xml = xstream.toXML(s);

        System.out.println(xml);

        Statistic s1 = (Statistic) xstream.fromXML(xml);

        System.out.println(s1);
    }

    //@Test
    public void testXstream2() {

        RNStreamFactory f = RNStreamFactory.getDefault();

        System.out.println(f);

        XStream xstream = new XStream();

        String xml1 = xstream.toXML(f);

        System.out.println(xml1);

        RNStreamFactory f1 = (RNStreamFactory) xstream.fromXML(xml1);

        System.out.println(f1);
    }

    //@Test
    public void testJAXB1() throws Exception {
        Statistic s = new Statistic("jaxb test");

        Normal n = new Normal();

        s.collect(n.getSample(100));

        System.out.println(s);

        JAXBContext jc = JAXBContext.newInstance(Statistic.class);

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        JAXBElement<Statistic> jaxbElement = new JAXBElement<Statistic>(new QName("statistic"), Statistic.class, s);
        marshaller.marshal(jaxbElement, System.out);
    }

    //@Test
    public void testXOM1() {
        Statistic s = new Statistic("xom test");

        Normal n = new Normal();

        s.collect(n.getSample(100));

        System.out.println(s);

        Element root = new Element("statistic");
        Element name = new Element("name");
        Element avg = new Element("average");
        root.appendChild(name);
        name.appendChild(s.getName());
        root.appendChild(avg);
        avg.appendChild(Double.toString(s.getAverage()));

        Document doc = new Document(root);

        try {
            Serializer serializer = new Serializer(System.out, "ISO-8859-1");
            serializer.setIndent(4);
            serializer.setMaxLength(64);
            serializer.write(doc);
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }

    //@Test
    public void testXOMStatistic() {
        Statistic s = new Statistic("test xml stat");

        Normal n = new Normal();

        s.collect(n.getSample(100));

        System.out.println(s);

        Document doc = new Document(getXOMElement(s));

        try {
            Serializer serializer = new Serializer(System.out, "ISO-8859-1");
            serializer.setIndent(4);
            serializer.setMaxLength(64);
            serializer.write(doc);
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }

    //@Test
    public Element getXOMElement(Statistic s) {
        Element root = new Element("statistic");

        Element e1 = new Element("Name");
        root.appendChild(e1);
        e1.appendChild(s.getName());

        Element e2 = new Element("Count");
        root.appendChild(e2);
        e2.appendChild(Double.toString(s.getCount()));

        Element e3 = new Element("Average");
        root.appendChild(e3);
        e3.appendChild(Double.toString(s.getAverage()));

        Element e4 = new Element("StandardDeviation");
        root.appendChild(e4);
        e4.appendChild(Double.toString(s.getStandardDeviation()));

        Element e5 = new Element("StandardError");
        root.appendChild(e5);
        e5.appendChild(Double.toString(s.getStandardError()));

        Element e6 = new Element("HalfWidth");
        root.appendChild(e6);
        e6.appendChild(Double.toString(s.getHalfWidth()));

        Element e7 = new Element("ConfidenceLevel");
        root.appendChild(e7);
        e7.appendChild(Double.toString(s.getConfidenceLevel()));

        Element e8 = new Element("Minimum");
        root.appendChild(e8);
        e8.appendChild(Double.toString(s.getMin()));

        Element e9 = new Element("Maximum");
        root.appendChild(e9);
        e9.appendChild(Double.toString(s.getMax()));

        Element e10 = new Element("Sum");
        root.appendChild(e10);
        e10.appendChild(Double.toString(s.getSum()));

        Element e11 = new Element("Variance");
        root.appendChild(e11);
        e11.appendChild(Double.toString(s.getVariance()));

        Element e12 = new Element("WeightedAverage");
        root.appendChild(e12);
        e12.appendChild(Double.toString(s.getWeightedAverage()));

        Element e13 = new Element("WeightedSum");
        root.appendChild(e13);
        e13.appendChild(Double.toString(s.getWeightedSum()));

        Element e14 = new Element("SumOfWeights");
        root.appendChild(e14);
        e14.appendChild(Double.toString(s.getSumOfWeights()));

        Element e15 = new Element("WeightedSumOfSquares");
        root.appendChild(e15);
        e15.appendChild(Double.toString(s.getWeightedSumOfSquares()));

        Element e16 = new Element("DeviationSumOfSquares");
        root.appendChild(e16);
        e16.appendChild(Double.toString(s.getDeviationSumOfSquares()));

        Element e17 = new Element("LastValue");
        root.appendChild(e17);
        e17.appendChild(Double.toString(s.getLastValue()));

        Element e18 = new Element("LastWeight");
        root.appendChild(e18);
        e18.appendChild(Double.toString(s.getLastWeight()));

        Element e19 = new Element("Kurtosis");
        root.appendChild(e19);
        e19.appendChild(Double.toString(s.getKurtosis()));

        Element e20 = new Element("Skewness");
        root.appendChild(e20);
        e20.appendChild(Double.toString(s.getSkewness()));

        Element e21 = new Element("Lag1Covariance");
        root.appendChild(e21);
        e21.appendChild(Double.toString(s.getLag1Covariance()));

        Element e22 = new Element("Lag1Correlation");
        root.appendChild(e22);
        e22.appendChild(Double.toString(s.getLag1Correlation()));

        Element e23 = new Element("VonNeumannLag1TestStatistic");
        root.appendChild(e23);
        e23.appendChild(Double.toString(s.getVonNeumannLag1TestStatistic()));

        Element e24 = new Element("NumberMissing");
        root.appendChild(e24);
        e24.appendChild(Double.toString(s.getNumberMissing()));

        return root;
    }
}
