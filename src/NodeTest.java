
import student.TestCase;

/**
 * Test the doubly linked list
 *
 * @author john9570
 * @version Sep 2, 2016
 */
public class NodeTest extends TestCase {

    private LeafNode leafN;
    private InternalNode intN;

    private KVPair[] kvPairs;

    /**
     * Sets up the tests that follow.
     */
    public void setUp() {

        leafN = new LeafNode();
        intN = new InternalNode();

        Handle[] handles;

        handles = new Handle[18];
        for (int i = 0; i < 18; i++) {
            handles[i] = new Handle(i);
        }

        kvPairs = new KVPair[15];
        for (int i = 0; i < 15; i++) {
            kvPairs[i] = new KVPair(handles[i], handles[i]);
        }

    }

    /**
     * Tests that the constructors work properly
     */
    public void testConstruction() {

        leafN = new LeafNode(kvPairs[1]);
        leafN = new LeafNode(kvPairs[2], kvPairs[3]);

        assertEquals(leafN.kv1, kvPairs[2]);
        assertEquals(leafN.kv2, kvPairs[3]);

        intN = new InternalNode(kvPairs[1]);
        intN = new InternalNode(kvPairs[2], kvPairs[3]);

        assertEquals(intN.kv1, kvPairs[2]);
        assertEquals(intN.kv2, kvPairs[3]);

    }

    /**
     * Tests the the isEmpty() and isFull() functions work properly
     */
    public void testEmpty() {
        assertTrue(leafN.isEmpty());
        assertFalse(leafN.isFull());
        assertFalse(intN.isFull());

        intN = new InternalNode(kvPairs[1]);
        leafN = new LeafNode(kvPairs[1]);

        assertFalse(leafN.isEmpty());
        assertFalse(leafN.isFull());
        assertFalse(intN.isFull());

        intN = new InternalNode(kvPairs[2], kvPairs[3]);
        assertTrue(leafN.add(kvPairs[2]));

        assertFalse(leafN.isEmpty());
        assertTrue(leafN.isFull());
        assertTrue(intN.isFull());

        assertFalse(leafN.add(kvPairs[4]));
    }

    /**
     * Tests the printing functions
     */
    public void testPrint() {
        TreeNode treeN;
        treeN = new LeafNode();
        treeN = (TreeNode) treeN;
        assertEquals(treeN.print(), "");

        treeN.kv1 = kvPairs[1];
        assertEquals(treeN.print(), kvPairs[1].toString());

    }

    /**
     * Tests using the leaf's add function
     */
    public void testLeafAdd() {

        leafN.add(kvPairs[2]);

        leafN.add(kvPairs[1]);

        leafN = new LeafNode();

        leafN.add(kvPairs[2]);
        assertNull(leafN.kv2);
        leafN.add(kvPairs[3]);

        leafN.kv1 = null;
        leafN.add(kvPairs[4]);
        leafN.kv1 = null;
        assertFalse(leafN.isEmpty());
        leafN.add(kvPairs[0]);

    }

    /**
     * Tests using the leaf's remove function
     */
    public void testLeafRemove() {
        leafN.add(kvPairs[2]);
        leafN.add(kvPairs[1]);
        leafN.remove(kvPairs[7]);

        leafN.remove(kvPairs[2]);
        leafN.remove(kvPairs[1]);
        assertNull(leafN.kv1);
        assertNull(leafN.kv2);

        leafN.add(kvPairs[2]);
        leafN.remove(kvPairs[1]);
        leafN.remove(kvPairs[2]);
        assertNull(leafN.kv1);
        leafN.remove(kvPairs[2]);
        leafN.kv2 = kvPairs[3];
        leafN.remove(kvPairs[3]);
        leafN.kv1 = kvPairs[3];
        leafN.kv2 = kvPairs[2];
        leafN.remove(kvPairs[10]);
        
        
        
    }

}
