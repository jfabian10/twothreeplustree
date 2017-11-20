
import student.TestCase;

/**
 * Test the tree Remove functionality
 *
 * @author john9570
 * @version Oct 8, 2016
 */
public class TreeRemoveTest extends TestCase {

    private Tree tree;
    private KVPair[] kvPairs;

    /**
     * Sets up the tests that follow.
     */
    public void setUp() {

        Handle[] handles;

        handles = new Handle[30];
        for (int i = 0; i < 30; i++) {
            handles[i] = new Handle(i);
        }

        kvPairs = new KVPair[25];
        for (int i = 0; i < 25; i++) {
            // kvPairs[i] = new KVPair(handles[i*2], handles[ (i*2) +1 ]);
            kvPairs[i] = new KVPair(handles[i], handles[i]);
        }

        tree = new Tree();
    }

    /**
     * Simple level 1 remove test. Focuses on any removes where there is 1
     * internal node parent, who has children that are leaves (depth = 2).
     */
    public void testRemoveL1() {

        TreeNode myRt = new LeafNode(); // empty
        TreeNode temp = new LeafNode(); // empty

        myRt = tree.insertHelp(myRt, kvPairs[3]);
        myRt = tree.insertHelp(myRt, kvPairs[5]);
        myRt = tree.insertHelp(myRt, kvPairs[7]); // Splits
        myRt = tree.insertHelp(myRt, kvPairs[9]); // Splits again

        temp = tree.insertHelp(temp, kvPairs[3]);
        temp = tree.insertHelp(temp, kvPairs[5]);
        temp = tree.insertHelp(temp, kvPairs[7]); // Splits
        temp = tree.insertHelp(temp, kvPairs[9]); // Splits again

        /*
         * Looks like: { 5, 7 } l c r [3] [5] [7, 9]
         * 
         * VERIFIED WITH DEBUGGER
         */

        // Try some removes
        myRt = tree.helper.removeHelp(myRt, kvPairs[5]);

        /*
         * Tree should now look like:
         * 
         * { 7, 9 } l c r [3] [7] [9]
         * 
         * VERIFIED below. This will be the last comment drawn tree for this
         * function
         */

        assertEquals(myRt.kv1.compareTo(kvPairs[7]), 0);
        assertEquals(myRt.kv2.compareTo(kvPairs[9]), 0);

        InternalNode rtInt = (InternalNode) myRt;
        assertEquals(rtInt.getLeft().kv1, kvPairs[3]);
        assertNull(rtInt.getLeft().kv2);
        assertEquals(rtInt.getCenter().kv1, kvPairs[7]);
        assertNull(rtInt.getCenter().kv2);
        assertEquals(rtInt.getRight().kv1, kvPairs[9]);
        assertNull(rtInt.getRight().kv2);

        // Remove 7 from above tree
        myRt = tree.helper.removeHelp(myRt, kvPairs[7]);
        assertEquals(myRt.kv1.compareTo(kvPairs[9]), 0);
        assertNull(myRt.kv2);

        rtInt = (InternalNode) myRt;
        assertEquals(rtInt.getLeft().kv1, kvPairs[3]);
        assertNull(rtInt.getLeft().kv2);
        assertEquals(rtInt.getCenter().kv1, kvPairs[9]);
        assertNull(rtInt.getCenter().kv2);
        assertNull(rtInt.getRight());

        // Remove 9 from a new tree
        myRt = tree.insertHelp(myRt, kvPairs[7]);
        myRt = tree.insertHelp(myRt, kvPairs[8]);
        myRt = tree.helper.removeHelp(myRt, kvPairs[9]);

        assertEquals(myRt.kv1.compareTo(kvPairs[7]), 0);
        assertEquals(myRt.kv2.compareTo(kvPairs[8]), 0);
        rtInt = (InternalNode) myRt;
        assertEquals(rtInt.getLeft().kv1, kvPairs[3]);
        assertNull(rtInt.getLeft().kv2);
        assertEquals(rtInt.getCenter().kv1, kvPairs[7]);
        assertNull(rtInt.getCenter().kv2);
        assertEquals(rtInt.getRight().kv1, kvPairs[8]);
        assertNull(rtInt.getRight().kv2);

        // Remove 3
        myRt = tree.helper.removeHelp(myRt, kvPairs[3]);

        assertEquals(myRt.kv1.compareTo(kvPairs[8]), 0);
        assertNull(myRt.kv2);
        rtInt = (InternalNode) myRt;
        assertEquals(rtInt.getLeft().kv1, kvPairs[7]);
        assertNull(rtInt.getLeft().kv2);
        assertEquals(rtInt.getCenter().kv1, kvPairs[8]);
        assertNull(rtInt.getCenter().kv2);
        assertNull(rtInt.getRight());

        // Remove 8
        myRt = tree.helper.removeHelp(myRt, kvPairs[8]);

        assertNull(myRt.kv1);
        assertNull(myRt.kv2);
        rtInt = (InternalNode) myRt;
        assertEquals(rtInt.getLeft().kv1, kvPairs[7]);
        assertNull(rtInt.getCenter());
        assertNull(rtInt.getRight());

        temp = tree.insertHelp(temp, kvPairs[6]);
        temp = tree.insertHelp(temp, kvPairs[4]);

        // Delete 2 from center
        temp = tree.helper.removeHelp(temp, kvPairs[5]);
        assertEquals(temp.kv1, kvPairs[6]);
        assertEquals(temp.kv2, kvPairs[7]);
        temp = tree.helper.removeHelp(temp, kvPairs[6]);

        assertEquals(temp.kv1.compareTo(kvPairs[4]), 0);
        assertEquals(temp.kv2.compareTo(kvPairs[7]), 0);
        rtInt = (InternalNode) temp;
        assertEquals(rtInt.getLeft().kv1, kvPairs[3]);
        assertNull(rtInt.getLeft().kv2);
        assertEquals(rtInt.getCenter().kv1, kvPairs[4]);
        assertNull(rtInt.getCenter().kv2);
        assertEquals(rtInt.getRight().kv1, kvPairs[7]);
        assertEquals(rtInt.getRight().kv2, kvPairs[9]);

        // Completely delete the left leaf
        temp = tree.helper.removeHelp(temp, kvPairs[3]);

        assertEquals(temp.kv1.compareTo(kvPairs[7]), 0);
        assertNull(temp.kv2);
        rtInt = (InternalNode) temp;
        assertEquals(rtInt.getLeft().kv1, kvPairs[4]);
        assertNull(rtInt.getLeft().kv2);
        assertEquals(rtInt.getCenter().kv1, kvPairs[7]);
        assertEquals(rtInt.getCenter().kv2, kvPairs[9]);
        assertNull(rtInt.getRight());

    }

    /**
     * Tree depth is going to be 3. Tests of remove will force borrow and merge
     * of internal nodes
     */
    public void testRemoveL2() {

        TreeNode myRt = new LeafNode(); // empty
        myRt = tree.insertHelp(myRt, kvPairs[6]);
        myRt = tree.insertHelp(myRt, kvPairs[8]); // Full leaf node
        myRt = tree.insertHelp(myRt, kvPairs[10]); // Splits here

        // Add one more to complete level one.
        myRt = tree.insertHelp(myRt, kvPairs[12]);

        myRt = tree.insertHelp(myRt, kvPairs[5]);
        myRt = tree.insertHelp(myRt, kvPairs[4]); // Causes LEFT split

        myRt = tree.helper.removeHelp(myRt, kvPairs[8]);

        // Verify borrow propagated to root
        assertEquals(myRt.kv1.compareTo(kvPairs[10]), 0);
        // VERIFIED IN DEBUGGER, GOOD TO HERE

        myRt = tree.helper.removeHelp(myRt, kvPairs[10]);
        InternalNode temp = (InternalNode) myRt;
        myRt = temp.getLeft();

        assertEquals(myRt.kv1.compareTo(kvPairs[5]), 0);
        assertEquals(myRt.kv2.compareTo(kvPairs[12]), 0);

        // Insert 15, 16, 17, 18
        myRt = tree.insertHelp(myRt, kvPairs[15]);
        myRt = tree.insertHelp(myRt, kvPairs[16]);
        myRt = tree.insertHelp(myRt, kvPairs[17]);
        myRt = tree.insertHelp(myRt, kvPairs[18]);

        // Remove 15, this should cause a left MERGE
        myRt = tree.helper.removeHelp(myRt, kvPairs[15]);

        assertEquals(myRt.kv1.compareTo(kvPairs[16]), 0);
        assertNull(myRt.kv2);

        InternalNode tempRt = (InternalNode) myRt;
        InternalNode left = (InternalNode) tempRt.getLeft();
        InternalNode center = (InternalNode) tempRt.getCenter();
        assertNull(tempRt.getRight());

        assertEquals(left.kv1.compareTo(kvPairs[5]), 0);
        assertEquals(left.kv2.compareTo(kvPairs[12]), 0);

        assertEquals(center.kv1.compareTo(kvPairs[17]), 0);
        assertNull(center.kv2);

        assertEquals(left.getLeft().kv1.compareTo(kvPairs[4]), 0);
        assertEquals(left.getCenter().kv1.compareTo(kvPairs[5]), 0);
        assertEquals(left.getCenter().kv2.compareTo(kvPairs[6]), 0);
        assertEquals(left.getRight().kv1.compareTo(kvPairs[12]), 0);
        assertNull(left.getLeft().kv2);
        assertNull(left.getRight().kv2);

        assertEquals(center.getLeft().kv1.compareTo(kvPairs[16]), 0);
        assertEquals(center.getCenter().kv1.compareTo(kvPairs[17]), 0);
        assertEquals(center.getCenter().kv2.compareTo(kvPairs[18]), 0);
        assertNull(center.getLeft().kv2);

    }

    /**
     * Tests the other helper function
     */
    public void testPathFinderHelp() {
        // Half full
        InternalNode intOne = new InternalNode(kvPairs[1]); // (2 3)
        assertEquals(tree.helper.pathFinder(intOne, kvPairs[2]), 0); // center
        assertEquals(tree.helper.pathFinder(intOne, kvPairs[1]), 0); // center
        assertEquals(tree.helper.pathFinder(intOne, kvPairs[0]), -1); // left

        // Full internal node
        intOne.kv2 = kvPairs[3];

        assertEquals(tree.helper.pathFinder(intOne, kvPairs[4]), 1); // right
        assertEquals(tree.helper.pathFinder(intOne, kvPairs[3]), 1); // right
        assertEquals(tree.helper.pathFinder(intOne, kvPairs[0]), -1); // left
        assertEquals(tree.helper.pathFinder(intOne, kvPairs[2]), 0); // center
        assertEquals(tree.helper.pathFinder(intOne, kvPairs[1]), 0); // center

    }

}