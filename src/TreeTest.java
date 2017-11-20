
import student.TestCase;

/**
 * Test the tree insert functionality
 *
 * @author john9570
 * @version Oct 4, 2016
 */
public class TreeTest extends TestCase {

    private Tree tree;
    private KVPair[] kvPairs;

    /**
     * Sets up the tests that follow.
     */
    public void setUp() {

        Handle[] handles;

        handles = new Handle[18];
        for (int i = 0; i < 18; i++) {
            handles[i] = new Handle(i);
        }

        kvPairs = new KVPair[15];
        for (int i = 0; i < 15; i++) {
            // kvPairs[i] = new KVPair(handles[i*2], handles[ (i*2) +1 ]);
            kvPairs[i] = new KVPair(handles[i], handles[i]);
        }

        tree = new Tree();
    }

    /**
     * Tests the path finding function
     */
    public void testTreePathFinder() {

        // Half full
        InternalNode intOne = new InternalNode(kvPairs[1]); // (2 3)
        assertEquals(tree.helper.pathFinderHandles(intOne, kvPairs[2]), 0);
        assertEquals(tree.helper.pathFinderHandles(intOne, kvPairs[1]), 0);
        assertEquals(tree.helper.pathFinderHandles(intOne, kvPairs[0]), -1);

        // Full internal node
        intOne.kv2 = kvPairs[3];

        assertEquals(tree.helper.pathFinderHandles(intOne, kvPairs[4]), 1);
        assertEquals(tree.helper.pathFinderHandles(intOne, kvPairs[3]), 1);
        assertEquals(tree.helper.pathFinderHandles(intOne, kvPairs[0]), -1);
        assertEquals(tree.helper.pathFinderHandles(intOne, kvPairs[2]), 0);
        assertEquals(tree.helper.pathFinderHandles(intOne, kvPairs[1]), 0);

    }

    /**
     * This function tests the split leaf function by examining all of its nodes
     * after a split
     */
    public void testTreeSplitLeaf() {

        LeafNode leaf1 = new LeafNode(kvPairs[0], kvPairs[1]);
        InternalNode firstSplit = tree.splitLeaf(leaf1, kvPairs[2]);

        assertEquals(firstSplit.kv1.compareTo(kvPairs[1]), 0);
        assertNull(firstSplit.kv2);

        // Check the left leaf node
        LeafNode newLeaf = (LeafNode) firstSplit.getLeft();
        assertEquals(firstSplit.getLeft().kv1.toString(), "0 0");
        assertNull(newLeaf.kv2);

        // Check the right leaf node
        LeafNode newLeaf2 = (LeafNode) firstSplit.getCenter();
        assertEquals(newLeaf2.kv1.toString(), "1 1");
        assertEquals(newLeaf2.kv2.toString(), "2 2");

        // Check the next pointer
        LeafNode centerLeaf = newLeaf.getNext();
        assertEquals(centerLeaf.kv1.toString(), "1 1");
        assertEquals(centerLeaf.kv2.toString(), "2 2");

        // Check prev pointer
        LeafNode leftLeaf = centerLeaf.getPrev();
        assertEquals(leftLeaf.kv1.toString(), "0 0");
        assertNull(leftLeaf.kv2);

    }

    /**
     * Test the tree's insert function LEVEL 1: Inserts until a split, then
     * inserts one more value
     * 
     * For this test I'm basing the values off of those found on this site:
     * http://lti.cs.vt.edu/NewKA/OpenDSA/AV/Development/TTPlusTree.html Ex. if
     * the site inserts 54, I insert 5. If it inserts 43, I insert 4. Then I go
     * and check the order of this tree with the one on the site.
     */
    public void testTreeInsertL1() {
        TreeNode myRt = new LeafNode(); // empty

        myRt = tree.insertHelp(myRt, kvPairs[5]);
        myRt = tree.insertHelp(myRt, kvPairs[4]); // Full leaf node

        assertEquals(myRt.kv1.compareTo(kvPairs[4]), 0);
        assertEquals(myRt.kv2.compareTo(kvPairs[5]), 0);

        myRt = tree.insertHelp(myRt, kvPairs[3]); // Splits here
        assertFalse(myRt.isLeaf());
        assertEquals(myRt.kv1.compareTo(kvPairs[4]), 0);
        assertNull(myRt.kv2);

        // Test the now split node:
        InternalNode myInternal = (InternalNode) myRt;
        // Tests:
        assertEquals(myInternal.getLeft().kv1.compareTo(kvPairs[3]), 0);
        assertNull(myInternal.getLeft().kv2);
        assertEquals(myInternal.getCenter().kv1.compareTo(kvPairs[4]), 0);
        assertEquals(myInternal.getCenter().kv2.compareTo(kvPairs[5]), 0);

        // Add one more to complete level one.
        myRt = tree.insertHelp(myRt, kvPairs[2]); // All leaves are full

        // Test all children:
        myInternal = (InternalNode) myRt;
        assertEquals(myInternal.getLeft().kv1.compareTo(kvPairs[2]), 0);
        assertEquals(myInternal.getLeft().kv2.compareTo(kvPairs[3]), 0);
        assertEquals(myInternal.getCenter().kv1.compareTo(kvPairs[4]), 0);
        assertEquals(myInternal.getCenter().kv2.compareTo(kvPairs[5]), 0);
    }

    /**
     * Test the tree's insert function LEVEL 1, advanced: Repeat of level1 with
     * addition of one more split. The second split causes the merge of the two
     * nodes
     * 
     * For this test I'm basing the values off of those found on this site:
     * http://lti.cs.vt.edu/NewKA/OpenDSA/AV/Development/TTPlusTree.html Ex. if
     * the site inserts 54, I insert 5. If it inserts 43, I insert 4. Then I go
     * and check the order of this tree with the one on the site.
     */
    public void testTreeInsertL1A() {

        TreeNode myRt = new LeafNode(); // empty

        myRt = tree.insertHelp(myRt, kvPairs[5]);
        myRt = tree.insertHelp(myRt, kvPairs[4]); // Full leaf node

        myRt = tree.insertHelp(myRt, kvPairs[3]); // Splits here

        // Add one more to complete level one.
        myRt = tree.insertHelp(myRt, kvPairs[2]); // Both leaves are full

        // ABOVE THIS LINE IS LEVEL 1.
        // Continuation of level 1:
        myRt = tree.insertHelp(myRt, kvPairs[7]);

        // Test the now split node:
        InternalNode myInternal = (InternalNode) myRt;
        assertEquals(myInternal.kv1.compareTo(kvPairs[4]), 0);
        assertEquals(myInternal.kv2.compareTo(kvPairs[5]), 0);

        // Tests:
        assertEquals(myInternal.getLeft().kv1.compareTo(kvPairs[2]), 0);
        assertEquals(myInternal.getLeft().kv2.compareTo(kvPairs[3]), 0);
        assertEquals(myInternal.getCenter().kv1.compareTo(kvPairs[4]), 0);
        assertNull(myInternal.getCenter().kv2);
        assertEquals(myInternal.getRight().kv1.compareTo(kvPairs[5]), 0);
        assertEquals(myInternal.getRight().kv2.compareTo(kvPairs[7]), 0);

        // Checkout the leaf links
        LeafNode linkTesterLeaf = (LeafNode) myInternal.getLeft();
        assertEquals(linkTesterLeaf.getNext(),
                (LeafNode) myInternal.getCenter());
        linkTesterLeaf = linkTesterLeaf.getNext();
        assertEquals(linkTesterLeaf.getNext(),
                (LeafNode) myInternal.getRight());

        // ----------------------------------------------------------------

        // Next lets try the same thing, but split the other way (to the left)
        myRt = new LeafNode(); // empty
        myRt = tree.insertHelp(myRt, kvPairs[5]);
        myRt = tree.insertHelp(myRt, kvPairs[4]); // Full leaf node

        myRt = tree.insertHelp(myRt, kvPairs[3]); // Splits here

        // Add one more to complete level one.
        myRt = tree.insertHelp(myRt, kvPairs[2]); // Both leaves are full

        // ABOVE THIS LINE IS LEVEL 1.
        // Continuation of level 1:
        myRt = tree.insertHelp(myRt, kvPairs[1]); // causes one more split,
                                                  // filling root internal node

        // Test the now split node:
        myInternal = (InternalNode) myRt;
        assertEquals(myInternal.kv1.compareTo(kvPairs[2]), 0);
        assertEquals(myInternal.kv2.compareTo(kvPairs[4]), 0);

        // Tests:
        assertEquals(myInternal.getLeft().kv1.compareTo(kvPairs[1]), 0);
        assertNull(myInternal.getLeft().kv2);
        assertEquals(myInternal.getCenter().kv1.compareTo(kvPairs[2]), 0);
        assertEquals(myInternal.getCenter().kv2.compareTo(kvPairs[3]), 0);
        assertEquals(myInternal.getRight().kv1.compareTo(kvPairs[4]), 0);
        assertEquals(myInternal.getRight().kv2.compareTo(kvPairs[5]), 0);

        // Checkout the leaf links
        linkTesterLeaf = (LeafNode) myInternal.getLeft();
        assertEquals(linkTesterLeaf.getNext(),
                (LeafNode) myInternal.getCenter());
        linkTesterLeaf = linkTesterLeaf.getNext();
        assertEquals(linkTesterLeaf.getNext(),
                (LeafNode) myInternal.getRight());

    }

    /**
     * Test the tree's insert function LEVEL 2
     * 
     * See test comments for details
     * 
     */
    public void testTreeInsertL2C() {

        /*
         * CENTER TEST - you can see the center leaf splits when we insert 7 For
         * this test the final tree will look like this: insert order: 3, 6, 9
         */
        TreeNode myRt = new LeafNode(); // empty
        myRt = tree.insertHelp(myRt, kvPairs[3]);
        myRt = tree.insertHelp(myRt, kvPairs[6]); // Full leaf node
        myRt = tree.insertHelp(myRt, kvPairs[9]); // Splits here

        // Add one more to complete level one.
        myRt = tree.insertHelp(myRt, kvPairs[12]); // Both leaves are full

        myRt = tree.insertHelp(myRt, kvPairs[8]);
        myRt = tree.insertHelp(myRt, kvPairs[7]); // Causes CENTER split

        /*
         * Test the nodes for their proper values
         */
        InternalNode root = (InternalNode) myRt;
        InternalNode rootLeft = (InternalNode) root.getLeft();
        InternalNode rootCenter = (InternalNode) root.getCenter();

        assertEquals(root.kv1, kvPairs[7]);
        assertEquals(rootLeft.kv1, kvPairs[6]);
        assertEquals(rootCenter.kv1, kvPairs[9]);
        assertEquals(rootLeft.getLeft().kv1, kvPairs[3]);
        assertEquals(rootLeft.getCenter().kv1, kvPairs[6]);
        assertEquals(rootCenter.getLeft().kv1, kvPairs[7]);
        assertEquals(rootCenter.getLeft().kv2, kvPairs[8]);
        assertEquals(rootCenter.getCenter().kv1, kvPairs[9]);
        assertEquals(rootCenter.getCenter().kv2, kvPairs[12]);

        // For the next part, get the LEFTMOST node
        LeafNode aLeaf = (LeafNode) rootLeft.getLeft();

        /*
         * Test the linked list 3|x, 6|x 7|8, 9|12
         */
        assertEquals(aLeaf.kv1, kvPairs[3]);
        assertNull(aLeaf.kv2);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[6]);
        assertNull(aLeaf.kv2);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[7]);
        assertEquals(aLeaf.kv2, kvPairs[8]);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[9]);
        assertEquals(aLeaf.kv2, kvPairs[12]);
    }

    /**
     * Test the tree's insert function LEVEL 2
     * 
     * See test comments for details
     * 
     */
    public void testTreeInsertL2R() {

        /*
         * RIGHT TEST - you can see the right leaf split For this test the final
         * tree will look like this: insert order: 5, 4, 3, 2, 7, 6 Copies this
         * example, drop the second digit though:
         * http://lti.cs.vt.edu/NewKA/OpenDSA/AV/Development/TTPlusTree.html
         * 
         */
        TreeNode myRt = new LeafNode(); // empty
        myRt = tree.insertHelp(myRt, kvPairs[5]);
        myRt = tree.insertHelp(myRt, kvPairs[4]); // Full leaf node
        myRt = tree.insertHelp(myRt, kvPairs[3]); // Splits here

        // Add one more to complete level one.
        myRt = tree.insertHelp(myRt, kvPairs[2]);

        myRt = tree.insertHelp(myRt, kvPairs[7]);
        myRt = tree.insertHelp(myRt, kvPairs[6]); // Causes RIGHT split

        /*
         * Test the nodes for their proper values
         */
        InternalNode root = (InternalNode) myRt;
        InternalNode rootLeft = (InternalNode) root.getLeft();
        InternalNode rootCenter = (InternalNode) root.getCenter();

        assertEquals(root.kv1, kvPairs[5]);
        assertEquals(rootLeft.kv1, kvPairs[4]);
        assertEquals(rootCenter.kv1, kvPairs[6]);

        assertEquals(rootLeft.getLeft().kv1, kvPairs[2]);
        assertEquals(rootLeft.getLeft().kv2, kvPairs[3]);
        assertEquals(rootLeft.getCenter().kv1, kvPairs[4]);
        assertNull(rootLeft.getCenter().kv2);
        assertEquals(rootCenter.getLeft().kv1, kvPairs[5]);
        assertNull(rootCenter.getLeft().kv2);
        assertEquals(rootCenter.getCenter().kv1, kvPairs[6]);
        assertEquals(rootCenter.getCenter().kv2, kvPairs[7]);

        // For the next part, get the LEFTMOST node
        LeafNode aLeaf = (LeafNode) rootLeft.getLeft();

        /*
         * Test the linked list
         */
        assertEquals(aLeaf.kv1, kvPairs[2]);
        assertEquals(aLeaf.kv2, kvPairs[3]);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[4]);
        assertNull(aLeaf.kv2);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[5]);
        assertNull(aLeaf.kv2);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[6]);
        assertEquals(aLeaf.kv2, kvPairs[7]);

        tree.print();
        tree.printHelp(myRt, 0);

    }

    /**
     * Test the tree's insert function LEVEL 2
     * 
     * See test comments for details
     * 
     */
    public void testTreeInsertL2L() {

        /*
         * LEFT TEST - you can see the left leaf split For this test the final
         * tree will look like this: insert order: 4, 5, 6, 7, 2, 1
         */
        TreeNode myRt = new LeafNode(); // empty
        myRt = tree.insertHelp(myRt, kvPairs[4]);
        myRt = tree.insertHelp(myRt, kvPairs[5]); // Full leaf node
        myRt = tree.insertHelp(myRt, kvPairs[6]); // Splits here

        // Add one more to complete level one.
        myRt = tree.insertHelp(myRt, kvPairs[7]);

        myRt = tree.insertHelp(myRt, kvPairs[2]);
        myRt = tree.insertHelp(myRt, kvPairs[1]); // Causes LEFT split

        /*
         * Test the nodes for their proper values
         */
        InternalNode root = (InternalNode) myRt;
        InternalNode rootLeft = (InternalNode) root.getLeft();
        InternalNode rootCenter = (InternalNode) root.getCenter();

        assertEquals(root.kv1, kvPairs[5]);
        assertEquals(rootLeft.kv1, kvPairs[2]);
        assertEquals(rootCenter.kv1, kvPairs[6]);

        assertEquals(rootLeft.getLeft().kv1, kvPairs[1]);
        assertNull(rootLeft.getLeft().kv2);
        assertEquals(rootLeft.getCenter().kv1, kvPairs[2]);
        assertEquals(rootLeft.getCenter().kv2, kvPairs[4]);
        assertEquals(rootCenter.getLeft().kv1, kvPairs[5]);
        assertNull(rootCenter.getLeft().kv2);
        assertEquals(rootCenter.getCenter().kv1, kvPairs[6]);
        assertEquals(rootCenter.getCenter().kv2, kvPairs[7]);

        // For the next part, get the LEFTMOST node
        LeafNode aLeaf = (LeafNode) rootLeft.getLeft();

        /*
         * Test the linked list
         */
        assertEquals(aLeaf.kv1, kvPairs[1]);
        assertNull(aLeaf.kv2);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[2]);
        assertEquals(aLeaf.kv2, kvPairs[4]);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[5]);
        assertNull(aLeaf.kv2);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[6]);
        assertEquals(aLeaf.kv2, kvPairs[7]);

    }

    /**
     * Test the tree's insert function LEVEL 2 ADVANCED
     * 
     * See test comments for details
     * 
     */
    public void testTreeInsertL2LA() {

        TreeNode myRt = new LeafNode(); // empty
        myRt = tree.insertHelp(myRt, kvPairs[6]);
        myRt = tree.insertHelp(myRt, kvPairs[8]); // Full leaf node
        myRt = tree.insertHelp(myRt, kvPairs[10]); // Splits here

        // Add one more to complete level one.
        myRt = tree.insertHelp(myRt, kvPairs[12]);

        myRt = tree.insertHelp(myRt, kvPairs[5]);
        myRt = tree.insertHelp(myRt, kvPairs[4]); // Causes LEFT split
        myRt = tree.insertHelp(myRt, kvPairs[3]);
        myRt = tree.insertHelp(myRt, kvPairs[2]); // Causes advanced split,
                                                  // fills bottom internal

        // Even more advanced split! Split up to root!
        myRt = tree.insertHelp(myRt, kvPairs[1]); // Good up to here! Meaning
                                                  // this line completes and is
                                                  // GOOD.
        myRt = tree.insertHelp(myRt, kvPairs[0]);

        /*
         * Test the nodes for their proper values AFTER (insert 0):
         * 
         * 3|8 l c r 1|x 5|x 10|x l c r l c l c 0|x 1|2 x 3|4 5|6 8|x 10|12
         */
        InternalNode root = (InternalNode) myRt;
        InternalNode rootLeft = (InternalNode) root.getLeft();
        InternalNode rootCenter = (InternalNode) root.getCenter();
        InternalNode rootRight = (InternalNode) root.getRight();

        assertEquals(root.kv1, kvPairs[3]);
        assertEquals(root.kv2, kvPairs[8]);
        assertEquals(rootLeft.kv1, kvPairs[1]);
        assertNull(rootLeft.kv2);
        assertEquals(rootCenter.kv1, kvPairs[5]);
        assertNull(rootCenter.kv2);
        assertEquals(rootRight.kv1, kvPairs[10]);
        assertNull(rootRight.kv2);

        assertEquals(rootLeft.getLeft().kv1, kvPairs[0]);
        assertNull(rootLeft.getLeft().kv2);
        assertEquals(rootLeft.getCenter().kv1, kvPairs[1]);
        assertEquals(rootLeft.getCenter().kv2, kvPairs[2]);
        assertNull(rootLeft.getRight());
        assertEquals(rootCenter.getLeft().kv1, kvPairs[3]);
        assertEquals(rootCenter.getLeft().kv2, kvPairs[4]);
        assertEquals(rootCenter.getCenter().kv1, kvPairs[5]);
        assertEquals(rootCenter.getCenter().kv2, kvPairs[6]);
        assertNull(rootCenter.getRight());

        assertEquals(rootRight.getLeft().kv1, kvPairs[8]);
        assertNull(rootRight.getLeft().kv2);
        assertEquals(rootRight.getCenter().kv1, kvPairs[10]);
        assertEquals(rootRight.getCenter().kv2, kvPairs[12]);
        assertNull(rootRight.getRight());

        // For the next part, get the LEFTMOST node
        LeafNode aLeaf = (LeafNode) rootLeft.getLeft();

        /*
         * Test the linked list
         * 
         * * AFTER (insert 0):
         * 
         * 3|8 l c r 1|x 5|x 10|x l c r l c l c 0|x 1|2 x 3|4 5|6 8|x 10|12
         */
        assertEquals(aLeaf.kv1, kvPairs[0]);
        assertNull(aLeaf.kv2);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[1]);
        assertEquals(aLeaf.kv2, kvPairs[2]);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[3]);
        assertEquals(aLeaf.kv2, kvPairs[4]);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[5]);
        assertEquals(aLeaf.kv2, kvPairs[6]);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[8]);
        assertNull(aLeaf.kv2);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[10]);
        assertEquals(aLeaf.kv2, kvPairs[12]);
    }

    /**
     * Test the tree's insert function LEVEL 3
     * 
     * See test comments for details
     * 
     */
    public void testTreeInsertL3() {

        /*
         * RIGHT TEST - you can see the right leaf split Tree will also be 4
         * levels deep For this test the final tree will look like this: insert
         * order: 6, 8, 10, 12, 5, 4, 3, 2, 1, 0, 13, 14
         * 
         * BEFORE: 3|8 l c r 1|x 5|x 10|12 l c r l c r l c 0|x 1|2 x 3|4 5|6 8|x
         * 10|x 12|13
         * 
         * 
         * AFTER (insert 14):
         * 
         * Use this website, and plug in the insert order.
         * http://www.cs.usfca.edu/~galles/visualization/BPlusTree.html
         * 
         */
        TreeNode myRt = new LeafNode(); // empty
        myRt = tree.insertHelp(myRt, kvPairs[6]);
        myRt = tree.insertHelp(myRt, kvPairs[8]); // Full leaf node
        myRt = tree.insertHelp(myRt, kvPairs[10]); // Splits here

        // Add one more to complete level one.
        myRt = tree.insertHelp(myRt, kvPairs[12]);

        myRt = tree.insertHelp(myRt, kvPairs[5]);
        myRt = tree.insertHelp(myRt, kvPairs[4]); // Causes LEFT split
        myRt = tree.insertHelp(myRt, kvPairs[3]);
        myRt = tree.insertHelp(myRt, kvPairs[2]); // Causes advanced split,
                                                  // fills bottom internal

        // Even more advanced split! Split up to root!
        myRt = tree.insertHelp(myRt, kvPairs[1]); // Good up to here! Meaning
                                                  // this line completes and is
                                                  // GOOD.
        myRt = tree.insertHelp(myRt, kvPairs[0]);
        myRt = tree.insertHelp(myRt, kvPairs[13]);
        myRt = tree.insertHelp(myRt, kvPairs[14]);

        /*
         * Test the nodes for their proper values
         */
        InternalNode root = (InternalNode) myRt;
        InternalNode rootLeft = (InternalNode) root.getLeft();
        InternalNode rootCenter = (InternalNode) root.getCenter();

        assertEquals(root.kv1, kvPairs[8]);
        assertNull(root.kv2);
        assertEquals(rootLeft.kv1, kvPairs[3]);
        assertNull(rootLeft.kv2);
        assertEquals(rootCenter.kv1, kvPairs[12]);
        assertNull(rootCenter.kv2);

        assertEquals(rootLeft.getLeft().kv1, kvPairs[1]);
        assertNull(rootLeft.getLeft().kv2);
        assertEquals(rootLeft.getCenter().kv1, kvPairs[5]);
        assertEquals(rootLeft.getCenter().kv2, null);
        assertNull(rootLeft.getRight());
        assertEquals(rootCenter.getLeft().kv1, kvPairs[10]);
        assertEquals(rootCenter.getLeft().kv2, null);
        assertEquals(rootCenter.getCenter().kv1, kvPairs[13]);
        assertNull(rootCenter.getCenter().kv2);
        assertNull(rootCenter.getRight());

        InternalNode one = (InternalNode) rootLeft.getLeft();
        InternalNode two = (InternalNode) rootLeft.getCenter();
        InternalNode three = (InternalNode) rootCenter.getLeft();
        InternalNode four = (InternalNode) rootCenter.getCenter();

        assertEquals(one.getLeft().kv1, kvPairs[0]);
        assertEquals(one.getLeft().kv2, null);
        assertEquals(one.getCenter().kv1, kvPairs[1]);
        assertEquals(one.getCenter().kv2, kvPairs[2]);

        assertEquals(two.getLeft().kv1, kvPairs[3]);
        assertEquals(two.getLeft().kv2, kvPairs[4]);
        assertEquals(two.getCenter().kv1, kvPairs[5]);
        assertEquals(two.getCenter().kv2, kvPairs[6]);

        assertEquals(three.getLeft().kv1, kvPairs[8]);
        assertEquals(three.getLeft().kv2, null);
        assertEquals(three.getCenter().kv1, kvPairs[10]);
        assertEquals(three.getCenter().kv2, null);

        assertEquals(four.getLeft().kv1, kvPairs[12]);
        assertEquals(four.getLeft().kv2, null);
        assertEquals(four.getCenter().kv1, kvPairs[13]);
        assertEquals(four.getCenter().kv2, kvPairs[14]);

        // For the next part, get the LEFTMOST node
        LeafNode aLeaf = (LeafNode) one.getLeft();

        /*
         * Test the linked list
         */
        assertEquals(aLeaf.kv1, kvPairs[0]);
        assertEquals(aLeaf.kv2, null);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[1]);
        assertEquals(aLeaf.kv2, kvPairs[2]);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[3]);
        assertEquals(aLeaf.kv2, kvPairs[4]);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[5]);
        assertEquals(aLeaf.kv2, kvPairs[6]);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[8]);
        assertEquals(aLeaf.kv2, null);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[10]);
        assertEquals(aLeaf.kv2, null);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[12]);
        assertEquals(aLeaf.kv2, null);
        aLeaf = aLeaf.getNext();

        assertEquals(aLeaf.kv1, kvPairs[13]);
        assertEquals(aLeaf.kv2, kvPairs[14]);

    }

}
