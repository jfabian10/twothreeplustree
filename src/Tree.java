import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author john9570, l3ogio22
 * @version Sep 25, 2016
 * 
 *          Our 2-3+ Tree implementation
 * 
 *          Notes:
 * 
 *          API Rough outline:
 */
public class Tree {

    private TreeNode rt;

    /**
     * Private helper function accessor, do not use outside of class.
     */
    public TreeHelper helper = new TreeHelper();

    /**
     * Default constructor
     */
    public Tree() {
        rt = new LeafNode(); // empty initial leaf node
    }

    /**
     * Call this to insert a new KV pair. DOES NOT CHECK FOR DUPLICATES.
     * 
     * @param newKV
     *            New KV pair to insert into tree
     */
    public void insert(KVPair newKV) {
        rt = insertHelp(rt, newKV);
    }

    /**
     * Searches for initial KVPair, then continues using next pointer for N
     * steps
     * 
     * @param initial
     *            Initial KV to look for
     * @param useValues
     *            True if you want to use keys AND values for comparisons
     * @return Null if initial not found, or initial overshoots bottom of tree
     *         else it returns a kvpair
     */
    public List<KVPair> list(KVPair initial, boolean useValues) {
        return listHelper(rt, initial, useValues);
    }

    /**
     * See original funciton
     * 
     * @param initial
     *            Initial KV to look for
     * @param useValues
     *            True if you want to use keys AND values for comparisons
     * @param currentNode
     *            Root at start, changes over time
     * @return hopefully a KVPair (and not null)
     */
    public List<KVPair> listHelper(TreeNode currentNode, KVPair initial,
            boolean useValues) {

        List<KVPair> myList = new ArrayList<KVPair>();
        boolean done = false;

        while (!done) {

            if (currentNode.isLeaf()) {
                LeafNode curr = (LeafNode) currentNode;

                if (curr.kv1 != null) {

                    if (curr.kv1.key().compareTo(initial.key()) == 0) {
                        myList.add(curr.kv1);
                    }

                }
                else {
                    done = true;
                }

                if (curr.kv2 != null
                        && curr.kv2.compareTo(initial.key()) == 0) {
                    myList.add(curr.kv2);
                }

                currentNode = curr.getNext();
                if (currentNode == null) {
                    done = true;
                    break;
                }

            }
            else { // Internal Node

                InternalNode currInt = (InternalNode) currentNode;

                // Find path and let the call go through:
                int path;
                if (!useValues) {
                    path = helper.pathFinderHandles(currInt, initial);
                }
                else {
                    path = helper.pathFinder(currInt, initial);
                }

                if (path == -1) {
                    currentNode = currInt.getLeft();
                }
                else if (path == 0) {
                    currentNode = currInt.getCenter();
                }
                else {
                    currentNode = currInt.getRight();
                }

            }

        } // end of while

        return myList;

    }

    /**
     * Call this to delete the KVPair badKV from the tree
     * 
     * @param badKV
     *            KVPair you wish to delete
     * @return 
     *        true if successful, false else           
     */
    public boolean remove(KVPair badKV) {
        helper.valueRemoved = false;
        rt = helper.removeHelp(rt, badKV);

        if (rt.kv1 == null && !rt.isLeaf()) {
            InternalNode temp = (InternalNode) rt;
            rt = temp.getLeft();
        }
        return helper.valueRemoved;
    }

    /**
     * Find out if a KV pair exists in the tree
     * Implements recursive search
     * @param searchKV
     *      KV you are searching for
     * @return
     *      True if KV is found in tree, false else
     */
    public boolean exists(KVPair searchKV) {
        return helper.existsHelper(rt, searchKV);
    }

    /**
     * PRIVATE helper function to insert values into tree
     * 
     * @param root
     *            the root of the tree
     * @param newKV
     *            the new value to insert
     * @return the subtree or resulting tree after insert
     */
    public TreeNode insertHelp(TreeNode root, KVPair newKV) {
        // Take in a subtree, return a subtree
        // https://piazza.com/class/irxteir3cpthi?cid=289

        if (root == null) {
            return null; // RETURN A NEW LEAF?
        }

        if (root.isLeaf()) { // LEAF, see if we can insert root
            LeafNode leaf = (LeafNode) root;

            if (!leaf.isFull()) { // Leaf has space
                leaf.add(newKV);
                return leaf;
            }
            else { // FULL, need to split
                return splitLeaf(leaf, newKV);
            }
        }

        else { // Internal Node

            InternalNode thisInternal = (InternalNode) root;

            // First we need to see which path to follow
            // Path = -1, go left
            // Path = 0, go center
            // Path = 1, go right
            int path = helper.pathFinder(thisInternal, newKV);
            TreeNode returnedValueNode;

            if (path == -1) {
                returnedValueNode = insertHelp(thisInternal.getLeft(), newKV);
            }
            else if (path == 0) {
                returnedValueNode = insertHelp(thisInternal.getCenter(), newKV);
            }
            else {
                returnedValueNode = insertHelp(thisInternal.getRight(), newKV);
            }

            // Evaluate the returned value. Do we need to split??
            // CASE 1, my children are leaves.
            if (thisInternal.isChildrenLeaves()) {

                if (returnedValueNode.isLeaf()) { // No one split
                    // Good, re-assign the leaf
                    return internalReassignLeaf(thisInternal,
                            (LeafNode) returnedValueNode, path);
                }
                else { // Someone split and returned an internal node

                    // Here we need to either consume and merge with it
                    if (!thisInternal.isFull()) {
                        return internalMergeHelp(thisInternal,
                                (InternalNode) returnedValueNode);
                    }
                    // OR split ourselves because we are full!
                    else {
                        return splitInternalNode(thisInternal,
                                (InternalNode) returnedValueNode);
                    }

                }

            }
            // CASE 2, my children are other internal nodes
            else {
                // We know we are going to get an internal node back FOR SURE
                // So we need to either reassign it or split / merge.
                // Use helper function along with path
                return childrenInternalNodeHelper(thisInternal,
                        (InternalNode) returnedValueNode, path);
            }
        }

    }

    /**
     * PRIVATE helper function to help with insert when internal node has
     * children that are internal nodes, not leaves
     * 
     * @param parentNode
     *            the base node
     * @param newNode
     *            the node that WAS returned as a subtree
     * @param path
     *            Where the subtree or newNode is coming from (left, center,
     *            right)
     * @return the resulting tree/subtree with the addition of newNode
     */
    public InternalNode childrenInternalNodeHelper(InternalNode parentNode,
            InternalNode newNode, int path) {

        InternalNode currOld;

        if (path == -1) { // check out left node
            currOld = (InternalNode) parentNode.getLeft();

            if (currOld.kv1 == newNode.kv1 && currOld.kv2 == newNode.kv2) {
                parentNode.setLeft(newNode); // reassign and return
                return parentNode;
            }
            // Otherwise, they are different.

            if (newNode.isFull()) { // Filled up the child internal
                parentNode.setLeft(newNode);
                return parentNode;
            }
            else { // The child internal split meaning we need to consume it or
                   // split ourselves
                if (!parentNode.isFull()) { // If the current, parent node isnt
                                            // full, merge
                    return internalMergeHelp(parentNode, newNode);
                }
                else { // split the parent node and return it
                    return splitInternalNode(parentNode, newNode);
                }
            }

        }
        else if (path == 0) { // check out center

            currOld = (InternalNode) parentNode.getCenter();
            if (currOld.kv1 == newNode.kv1 && currOld.kv2 == newNode.kv2) {
                parentNode.setCenter(newNode); // reassign and return
                return parentNode;
            }
            // Otherwise, they are different.

            if (newNode.isFull()) { // Filled up the child internal
                parentNode.setCenter(newNode);
                return parentNode;
            }
            else { // The child internal split meaning we need to consume it or
                   // split ourselves
                if (!parentNode.isFull()) { // If the current, parent node isnt
                                            // full, merge
                    return internalMergeHelp(parentNode, newNode);
                }
                else { // split the parent node and return it
                    return splitInternalNode(parentNode, newNode);
                }
            }

        }
        else { // check out the right

            currOld = (InternalNode) parentNode.getRight();
            if (currOld.kv1 == newNode.kv1 && currOld.kv2 == newNode.kv2) {
                parentNode.setRight(newNode); // reassign and return
                return parentNode;
            }
            // Otherwise, they are different.

            if (newNode.isFull()) { // Filled up the child internal
                parentNode.setRight(newNode);
                return parentNode;
            }
            else { // The child internal split meaning we need to consume it or
                   // split ourselves
                if (!parentNode.isFull()) { // If the current, parent node isnt
                                            // full, merge
                    return internalMergeHelp(parentNode, newNode);
                }
                else { // split the parent node and return it
                    return splitInternalNode(parentNode, newNode);
                }
            }

        }

    }

    /**
     * Returns the new node resulting from the split of an internal node
     * 
     * @param oldNode
     *            Yourself (the FULL node), node must be full.
     * @param newNode
     *            The new node thats causing you to split
     * @return the new node resulting from the split of an internal node
     */
    public InternalNode splitInternalNode(InternalNode oldNode,
            InternalNode newNode) {

        InternalNode nodeToReturn = new InternalNode();

        /*
         * First, Figure out the order
         */
        if (newNode.kv1.compareTo(oldNode.kv2) == 1) { // new KV is largest
                                                       // value
            nodeToReturn.kv1 = oldNode.kv2;
            oldNode.kv2 = null;
            oldNode.setRight(null); // old losses its right child //New node
                                    // keeps its large KV value

            // Set the new nodes children
            nodeToReturn.setLeft(oldNode);
            nodeToReturn.setCenter(newNode);
        }
        else if (oldNode.kv1.compareTo(newNode.kv1) == -1
                && oldNode.kv2.compareTo(newNode.kv1) == 1) { // CENTER SPLIT
            // This is the HARDEST case

            // Old node operations
            oldNode.setCenter(newNode.getLeft());

            // New node operations
            newNode.setLeft(newNode.getCenter());
            newNode.setCenter(oldNode.getRight());

            newNode.setRight(null); // Just to be sure, should already be nulled
            oldNode.setRight(null); // Safe to null this now

            nodeToReturn.kv1 = newNode.kv1;

            newNode.kv1 = oldNode.kv2;
            oldNode.kv2 = null;

            nodeToReturn.setLeft(oldNode);
            nodeToReturn.setCenter(newNode);

        }
        else { // newNode is smallest

            oldNode.setLeft(oldNode.getCenter());
            oldNode.setCenter(oldNode.getRight());
            oldNode.setRight(null);

            nodeToReturn.kv1 = oldNode.kv1;
            oldNode.kv1 = oldNode.kv2;
            oldNode.kv2 = null;

            nodeToReturn.setLeft(newNode);
            nodeToReturn.setCenter(oldNode);
        }

        return nodeToReturn;

    }

    /**
     * Splits a leaf and returns a new internal node
     * 
     * @param oldLeaf
     *            The old leaf, must be full
     * @param newKV
     *            the new KV pair to insert into the "full" leaf
     * @return Internal node with proper order of leaves
     */
    public InternalNode splitLeaf(LeafNode oldLeaf, KVPair newKV) {
        // Guaranteed to always be passed a full, ordered leaf node

        /*
         * Create new leaf node, handle pointers at the end
         */
        LeafNode newLeaf = new LeafNode();

        /*
         * Figure out the order, there's only 3 cases
         */
        KVPair first;
        KVPair second;
        KVPair third;
        if (newKV.compareTo(oldLeaf.kv1) == -1) { // new KV is smallest value
            first = newKV;
            second = oldLeaf.kv1;
            third = oldLeaf.kv2;
        }
        else if (newKV.compareTo(oldLeaf.kv2) == 1) { // new KV is largest value
            first = oldLeaf.kv1;
            second = oldLeaf.kv2;
            third = newKV;
        }
        else { // new KV must be in between since we CANT have duplicates
            first = oldLeaf.kv1;
            second = newKV;
            third = oldLeaf.kv2;
        }

        // Set the KVPairs in the proper order
        oldLeaf.kv1 = first;
        oldLeaf.kv2 = null;
        newLeaf.kv1 = second;
        newLeaf.kv2 = third;

        /*
         * Handle pointers
         */
        if (oldLeaf.getNext() != null) {
            oldLeaf.getNext().setPrev(newLeaf);
        }
        newLeaf.setNext(oldLeaf.getNext());
        oldLeaf.setNext(newLeaf);
        newLeaf.setPrev(oldLeaf);

        /*
         * Create a new Internal Node
         */
        InternalNode newInternal = new InternalNode();
        newInternal.kv1 = second;
        newInternal.setLeft(oldLeaf);
        newInternal.setCenter(newLeaf);

        return newInternal;
    }

    /**
     * Prints a PREORDER traversal of the tree
     */
    public void print() {
        printHelp(rt, 0);
    }

    /**
     * PREORDER printout
     * 
     * @param root
     *            starting point
     * @param level
     *            current level, root is 0
     */
    public void printHelp(TreeNode root, int level) {
        if (root == null) {
            return;
        }

        if (root.isLeaf()) {
            LeafNode thisL = (LeafNode) root;
            if (thisL.isEmpty()) {
                return;
            }
        }

        // generates the tab
        String toReturn = new String(new char[2 * level]).replace("\0", " ");

        // First process YOURSELF.
        // Then if you are an internal node, process your left side then your
        // center, then your right side
        toReturn += root.print();
        System.out.println(toReturn);

        if (!root.isLeaf()) {
            InternalNode thisInt = (InternalNode) root;

            printHelp(thisInt.getLeft(), level + 1);
            printHelp(thisInt.getCenter(), level + 1);
            printHelp(thisInt.getRight(), level + 1);
        }

    }

    /**
     * Takes in two internal nodes, the primary and the new node. This function
     * assumes that the primary has space to merge the new node in
     * 
     * @param primary
     * 
     * @return merged nodes
     */
    private InternalNode internalMergeHelp(InternalNode primary,
            InternalNode newInt) {

        InternalNode nodeToReturn = new InternalNode();

        // Determine which is larger
        if (primary.kv1.compareTo(newInt.kv1) >= 0) { // The primary value is
                                                      // larger
            // this means our left LEAF child split
            nodeToReturn.kv1 = newInt.kv1;
            nodeToReturn.kv2 = primary.kv1;

            nodeToReturn.setLeft(newInt.getLeft());
            nodeToReturn.setCenter(newInt.getCenter());
            nodeToReturn.setRight(primary.getCenter());

        }
        else { // The primary value is smaller
            nodeToReturn.kv1 = primary.kv1;
            nodeToReturn.kv2 = newInt.kv1;

            nodeToReturn.setLeft(primary.getLeft()); // same
            nodeToReturn.setCenter(newInt.getLeft());
            nodeToReturn.setRight(newInt.getCenter());
        }

        return nodeToReturn;
    }

    /**
     * This is a helper function to reassign a leaf node
     * 
     * @return The internal node with the new leaf attached to position
     *         indicated by path
     */
    private InternalNode internalReassignLeaf(InternalNode curr,
            LeafNode newLeaf, int path) {

        if (path == -1) {
            curr.setLeft(newLeaf);
        }
        else if (path == 0) {
            curr.setCenter(newLeaf);
        }
        else {
            curr.setRight(newLeaf);
        }

        return curr;
    }

}
