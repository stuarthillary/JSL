package misc;

import jsl.modeling.Simulation;
import jsl.modeling.Model;
import jsl.modeling.elements.queue.QObject;
import jsl.modeling.elements.queue.Queue;
import org.junit.*;
import static org.junit.Assert.*;

public class TestQ {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test1() {
        Simulation s = new Simulation();
       
        Queue q = new Queue(s.getModel());
        QObject r1 = q.enqueue();
        r1.setName("A");
        QObject r2 = q.enqueue();
        r2.setName("B");
        QObject r3 = q.enqueue();
        r3.setName("A");
        QObject r4 = q.enqueue();
        r4.setName("C");
        QObject r5 = q.enqueue();
        r5.setName("D");

        System.out.println("Before");
        for (QObject qo : q) {
            System.out.println(qo);
        }

        for (int i = 0; i < q.size(); i++) {
            QObject v = q.peekAt(i);
            if (v.getName().equals("A")) {
                q.remove(v);
            }
        }

        System.out.println("After");
        boolean t = true;
        int i = 1;
        for (QObject qo : q) {
            System.out.println(qo);
            if (i == 1 && !qo.getName().equals("B")) {
                t = false;
                break;
            }
            if (i == 2 && !qo.getName().equals("C")) {
                t = false;
                break;
            }
            if (i == 3 && !qo.getName().equals("D")) {
                t = false;
                break;
            }
            i++;
        }
        assertTrue(t);
    }

}
