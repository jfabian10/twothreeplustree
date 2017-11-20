
/**
 * 
 * @author john9570, l3ogio22
 * @version Sep 25, 2016
 * 
 *          Our 2-3+ Tree implementation
 * 
 *          Notes:
 * 
 *          API Rough outline: Helper function template for the actual Tree
 *          class
 */
public class TreeHelper {

    /**
     * Set and Access this when using the actual remove function to determine if
     * a value has been removed or not
     */
    public boolean valueRemoved;

    /**
     * Helper function for the 'exists' tree function
     * 
     * @param curr
     *            Current node
     * @param searchKV
     *            KV you are searching for
     * @return True if KV is in tree, false else
     */
    public boolean existsHelper(TreeNode curr, KVPair searchKV) {

        if (curr.isLeaf()) {
            LeafNode temp = (LeafNode) curr;
            return temp.hasKV(searchKV);

        }
        else { // Internal Node

            InternalNode currInt = (InternalNode) curr;

            // Find path and let the call go through:
            int path = pathFinder(currInt, searchKV);

            if (path == -1) {
                return existsHelper(currInt.getLeft(), searchKV);
            }
            else if (path == 0) {
                return existsHelper(currInt.getCenter(), searchKV);
            }
            else {
                return existsHelper(currInt.getRight(), searchKV);
            }

        }

    }

    /**
     * PRIVATE helper function to remove values from tree
     * 
     * @param root
     *            the root of the tree or subtree
     * @param badKV
     *            the KV pair to remove
     * @return The subtree or resulting tree after remove
     * 
     *         NOTES:
     * 
     *         **(PROF)Parents do the borrowing for the leaf node
     * 
     *         **(PROF)After completing the deletion from the subtree, you just
     *         ask if the current record (at this internal node) equals the one
     *         that you just removed from its child. If it is, that means that
     *         you removed the smallest record from the subtree. Which means
     *         that you have to update that record. So, you simply ask the
     *         subtree what its current smallest record is.
     * 
     *         **(PROF)Borrow first from the left, then the right.
     * 
     *         **A node should only borrow from another node if they are
     *         siblings (i.e. they have the same parent).
     * 
     *         **(PROF) What I did (for my internal node remove method) is
     *         return myself to my parent, but with my fields changed in such a
     *         way that my parent can recognize that I have underflowed.
     *         Specifically, if my first key is null, that means that I have
     *         only one child left (in my left child field).
     * 
     * 
     * 
     * 
     * 
     */
    public TreeNode removeHelp(TreeNode root, KVPair badKV) {

        // BASECASE, we are at the leaf node
        if (root.isLeaf()) {
            LeafNode thisLeaf = (LeafNode) root;
            /*
             * Delete KVPair. The leaf's delete function takes care of there not
             * being a matching KVPair, and/or needing to move kv2 to kv1 if we
             * deleted kv1
             */
            valueRemoved = thisLeaf.remove(badKV);

            // Leaves don't handle merging, they just return themselves,
            // even if they are now empty.
            return thisLeaf;
        }
        // We are at an internal node
        else {

            InternalNode currentInternal = (InternalNode) root;

            // Find path and let the call go through:
            int path = pathFinder(currentInternal, badKV);

            TreeNode returnedValueNode;

            if (path == -1) {
                returnedValueNode = removeHelp(currentInternal.getLeft(),
                        badKV);
            }
            else if (path == 0) {
                returnedValueNode = removeHelp(currentInternal.getCenter(),
                        badKV);
            }
            else {
                returnedValueNode = removeHelp(currentInternal.getRight(),
                        badKV);
            }

            // Parent to leaves
            if (currentInternal.isChildrenLeaves()) {
                /*
                 * Cases.
                 */

                return removeInternalChildLeafHelper(currentInternal,
                        (LeafNode) returnedValueNode, badKV, path);

            }

            // Parent to other internal nodes
            else {
                /*
                 * Cases.
                 */

                // All returnedValueNode's will be InternalNodes.
                return removeSuperInternalHelper(currentInternal,
                        (InternalNode) returnedValueNode, badKV, path);

            }

            // NOTE: Need to always check out internal KV's for the KV that was
            // removed

        }

    }

    /**
     * 
     * Used with the INSERT method, this function finds the proper path for you
     * 
     * @param root
     *            Internal node you are currently at
     * @param newKV
     *            KV Pair you are trying to INSERT or REMOVE
     * @return -1-> Left, 0-> Center, 1->Right
     */
    public int pathFinder(InternalNode root, KVPair newKV) {

        if (root.isFull()) { // Look at KV1 and KV2

            if (newKV.compareTo(root.kv2) >= 0) {
                return 1; // Go right!
            }
            else if (newKV.compareTo(root.kv1) >= 0) {
                return 0; // Go center!
            }
            else {
                return -1; // Go left!
            }

        }
        else { // Only KV1 to look at
            if (newKV.compareTo(root.kv1) >= 0) {
                return 0; // Go center!
            }
            else {
                return -1; // Go left!
            }
        }
    }

    /**
     * 
     * Used with the list method, only looks at first handle of a KV pair
     * 
     * @param root
     *            Internal node you are currently at
     * @param newKV
     *            KV Pair you are trying to INSERT or REMOVE
     * @return -1-> Left, 0-> Center, 1->Right
     */
    public int pathFinderHandles(InternalNode root, KVPair newKV) {

        if (root.isFull()) { // Look at KV1 and KV2

            if (newKV.key().compareTo(root.kv2.key()) >= 0) {
                return 1; // Go right!
            }
            else if (newKV.key().compareTo(root.kv1.key()) >= 0) {
                return 0; // Go center!
            }
            else {
                return -1; // Go left!
            }

        }
        else { // Only KV1 to look at
            if (newKV.key().compareTo(root.kv1.key()) >= 0) {
                return 0; // Go center!
            }
            else {
                return -1; // Go left!
            }
        }
    }

    /**
     * PRIVATE helper function for remove
     * 
     * @param parent
     *            Your current internal node who has CHILDREN that are LEAVES
     * @param returnedValueNode
     *            The returned leaf node value that you got after doing a remove
     * @param badKV
     *            The KV you were told to remove
     * @param path
     *            The branch down parent (left, center, or right) that you took
     *            to do the remove
     * @return Your (parent) return value :)
     */
    public TreeNode removeInternalChildLeafHelper(InternalNode parent,
            LeafNode returnedValueNode, KVPair badKV, int path) {

        // Case 1, we get a leaf node back that is unchanged
        // OR it changed but still has a value in it
        if (returnedValueNode.kv1 != null) {

            if (path == -1) {
                parent.setLeft(returnedValueNode);
            }
            else if (path == 0) {
                parent.setCenter(returnedValueNode);
            }
            else {
                parent.setRight(returnedValueNode);
            }

            // Handle the parent kv pairs
            parent.resetKV();

        }
        else { // We got back an empty leaf node

            /**
             * HANDLE THE POINTERS:
             */
            if (returnedValueNode.getNext() != null) {
                returnedValueNode.getNext()
                        .setPrev(returnedValueNode.getPrev());
            }

            if (returnedValueNode.getPrev() != null) {
                returnedValueNode.getPrev()
                        .setNext(returnedValueNode.getNext());
            }

            // Case 2, we get an empty leaf node back AND
            // CAN borrow
            // we have other children to borrow from.
            // Basically try to solve the issue here
            // Check left first then right
            boolean borrowed = false;

            if (path == -1) { // left is empty
                // Can't borrow left, so try center only
                if (parent.getCenter() != null) {
                    LeafNode left = returnedValueNode;
                    LeafNode center = (LeafNode) parent.getCenter();

                    if (parent.getCenter().isFull()) { // borrow from center

                        borrowed = true;
                        left.kv1 = center.kv1;
                        left.kv2 = null;
                        center.kv1 = center.kv2;
                        center.kv2 = null;
                        parent.setLeft(left);
                        parent.setCenter(center);
                        // Right goes unchanged
                    }
                    else { // shuffle, couldn't borrow
                        parent.setLeft(parent.getCenter());
                        parent.setCenter(parent.getRight());
                        parent.setRight(null);
                    }

                    parent.resetKV();
                }

            }
            else if (path == 0) { // center is empty

                LeafNode left = (LeafNode) parent.getLeft();
                LeafNode center = returnedValueNode;

                if (parent.getLeft().isFull()) { // borrow from left

                    borrowed = true;
                    center.kv1 = left.kv2;
                    center.kv2 = null;
                    left.kv2 = null;
                    parent.setLeft(left);
                    parent.setCenter(center);
                }

                if (!borrowed) { // couldnt borrow left
                    if (parent.getRight() != null) {
                        LeafNode right = (LeafNode) parent.getRight();
                        if (parent.getRight().isFull()) { // borrow from right
                            borrowed = true;
                            center.kv1 = right.kv1;
                            center.kv2 = null;
                            right.kv1 = right.kv2;
                            right.kv2 = null;
                            parent.setCenter(center);
                            parent.setRight(right);
                        }
                    }
                }

                if (!borrowed) { // couldnt borrow from anyone, so shuffle
                    parent.setCenter(parent.getRight());
                    parent.setRight(null);
                }

                parent.resetKV();
            }
            else { // right is empty

                LeafNode right = returnedValueNode;
                LeafNode center = (LeafNode) parent.getCenter();

                if (parent.getCenter().isFull()) { // borrow from center
                    borrowed = true;
                    right.kv1 = center.kv2;
                    right.kv2 = null;
                    center.kv2 = null;
                    parent.setRight(right);
                    parent.setCenter(center);
                    // Left goes unchanged
                }
                else { // shuffle, couldn't borrow
                    parent.setRight(null); // right just goes null
                }

                parent.resetKV();

            }

            /*
             * Case 3, we can't borrow from anyone AND We underflowed, this
             * means that: We HAD 2 children, now we only have one Set our
             * remaining child to left node, null our kv pairs, and return
             */
            /*
             * if (!borrowed) {
             * 
             * parent.kv1 = null; parent.kv2 = null;
             * 
             * if (path == -1) { // left is empty if (parent.getCenter() !=
             * null) { parent.setLeft(parent.getCenter());
             * parent.setCenter(null); }
             * 
             * } else if (path == 0) { // center is empty, left is good
             * parent.setCenter(null); }
             * 
             * 
             * }
             * 
             */

        } // END: We got back an empty leaf node

        return parent;

    }

    /**
     * PRIVATE helper function for remove
     * 
     * @param parent
     *            current internal node
     * @param returnedValueNode
     *            value returned from lower internal node after call of remove
     *            help
     * @param badKV
     *            KV pair you are removing
     * @param path
     *            Path taken to reach lower internal node
     * @return Currect return value for current parent node
     */
    public TreeNode removeSuperInternalHelper(InternalNode parent,
            InternalNode returnedValueNode, KVPair badKV, int path) {

        // Case 1, our child internal node didn't underflow!
        if (returnedValueNode.kv1 != null) {

            if (path == -1) {
                parent.setLeft(returnedValueNode);
            }
            else if (path == 0) {
                parent.setCenter(returnedValueNode);
            }
            else {
                parent.setRight(returnedValueNode);
            }

            // Now we need to make sure our kv pairs are good:
            parent.resetKV();

            return parent;

        }

        // We got back an empty internal node.
        // CASE 1: We can borrow from our other children and
        // solve the issue here, or:
        // Case 2: we ourselves underflow?
        else {

            boolean borrowed = false;

            if (path == -1) { // left is empty internal node
                // Center is either full and can be borrowed, or not
                // so try to borrow first
                if (parent.getCenter().isFull()) {
                    borrowed = true;
                    InternalNode pCent = (InternalNode) parent.getCenter();

                    returnedValueNode.setCenter(pCent.getLeft());
                    pCent.setLeft(pCent.getCenter());
                    pCent.setCenter(pCent.getRight());
                    pCent.setRight(null);

                    returnedValueNode.resetKV();
                    pCent.resetKV();

                    parent.setLeft(returnedValueNode);
                    parent.setCenter(pCent);
                    parent.resetKV();
                }

            }
            else if (path == 0) { // center underflowed, hardest case
                // First try to borrow left, then try to borrow right

                if (parent.getLeft().isFull()) {
                    borrowed = true;
                    InternalNode pLeft = (InternalNode) parent.getLeft();

                    returnedValueNode.setCenter(returnedValueNode.getLeft());
                    returnedValueNode.setLeft(pLeft.getRight());
                    pLeft.setRight(null);

                    pLeft.resetKV();
                    returnedValueNode.resetKV();

                    parent.setLeft(pLeft);
                    parent.setCenter(returnedValueNode);
                    parent.resetKV();

                }
                else if (parent.getRight() != null) {
                    if (parent.getRight().isFull()) {
                        borrowed = true;
                        InternalNode pRight = (InternalNode) parent.getRight();

                        returnedValueNode.setCenter(pRight.getLeft());
                        pRight.setLeft(pRight.getCenter());
                        pRight.setCenter(pRight.getRight());
                        pRight.setRight(null);

                        pRight.resetKV();
                        returnedValueNode.resetKV();

                        parent.setCenter(returnedValueNode);
                        parent.setRight(pRight);
                        parent.resetKV();
                    }
                }

            }
            else { // right is underflowed
                   // Try to borrow from the center
                if (parent.getCenter().isFull()) {
                    borrowed = true;
                    InternalNode pCent = (InternalNode) parent.getCenter();

                    returnedValueNode.setCenter(returnedValueNode.getLeft());
                    returnedValueNode.setLeft(pCent.getRight());

                    pCent.setRight(null);

                    pCent.resetKV();
                    returnedValueNode.resetKV();

                    parent.setRight(returnedValueNode);
                    parent.setCenter(pCent);

                    parent.resetKV();
                }

            }

            if (!borrowed) {
                // Need to merge to the left or right.
                // At this point we know we can't borrow so a merge will be
                // possible

                if (path == -1) { // left (returnedValueNode) is empty internal
                                  // node, merge with center
                    InternalNode pCent = (InternalNode) parent.getCenter();

                    returnedValueNode.setCenter(pCent.getLeft());
                    returnedValueNode.setRight(pCent.getCenter());
                    returnedValueNode.resetKV();

                    parent.setLeft(returnedValueNode);
                    parent.setCenter(parent.getRight());
                    parent.setRight(null);

                    // Might have underflow
                    parent.resetKV(); // should handle making kv1 && 2 null if
                                      // underflow
                }
                else if (path == 0) { // center (returnedValueNode) is empty
                                      // internal node, merge with left
                    InternalNode pLeft = (InternalNode) parent.getLeft();

                    pLeft.setRight(returnedValueNode.getLeft()); // perform
                                                                 // merge
                    pLeft.resetKV();

                    // Update parent
                    parent.setLeft(pLeft);
                    parent.setCenter(parent.getRight());
                    parent.setRight(null);

                    // Might have underflow
                    parent.resetKV(); // should handle making kv1 && 2 null if
                                      // underflow
                }
                else { // right (returnedValueNode) is empty internal node,
                       // merge with center
                       // Cant have underflow in this case :D
                    InternalNode pCent = (InternalNode) parent.getCenter();

                    pCent.setRight(returnedValueNode.getLeft());
                    pCent.resetKV();

                    parent.setCenter(pCent);
                    parent.setRight(null);

                    parent.resetKV();
                }

            } // end of possible merge

            return parent;

        } // end of else
    } // end of function

}
