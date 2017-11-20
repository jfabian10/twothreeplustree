
/**
 * 
 * @author johnmarshall, l3ogio22
 * @version Sep 25, 2016
 * 
 *          Internal Node implementation
 * 
 * 
 */
public class InternalNode extends TreeNode {

    private TreeNode left;
    private TreeNode center;
    private TreeNode right;

    /**
     * overloaded constructor
     * 
     * @param kv1
     *            first key value pair
     * @param kv2
     *            second key value pair
     */
    public InternalNode(KVPair kv1, KVPair kv2) {
        super(kv1, kv2);

    }

    /**
     * overloaded constructor
     * 
     * @param kv1
     *            first key value pair, second one is set to null
     */
    public InternalNode(KVPair kv1) {
        super(kv1);
    }

    /**
     * Defaul constructor
     */
    public InternalNode() {
        // empty constructor
    }

    /* Overridden abstract methods */

    @Override
    /**
     * returns false all the time
     */
    boolean isLeaf() {
        return false;
    }

    /* Public methods */
    /**
     * Get left function
     * 
     * @return returns the node to the left of the internal node
     */
    public TreeNode getLeft() {
        return left;
    }

    /**
     * Get center function
     * 
     * @return returns the node to the center of the internal node
     */
    public TreeNode getCenter() {
        return center;
    }

    /**
     * 
     * Get Right function
     * 
     * @return returns the node to the right of the internal node
     */
    public TreeNode getRight() {
        return right;
    }

    /**
     * Setter for the left node
     * 
     * @param newLeft
     *            new left node, can be null
     */
    public void setLeft(TreeNode newLeft) {
        left = newLeft;
    }

    /**
     * Setter for the center node
     * 
     * @param newCenter
     *            new center node, can be null
     */
    public void setCenter(TreeNode newCenter) {
        center = newCenter;
    }

    /**
     * Setter for the new right node
     * 
     * @param newRight
     *            new right node, can be null
     */
    public void setRight(TreeNode newRight) {
        right = newRight;
    }

    /**
     * true if both KV pairs are occupied
     * 
     * @return true if leaf is full, false else
     */
    public boolean isFull() {
        return kv1 != null && kv2 != null;
    }

    /**
     * Left pointer node should always be populated - assumption.
     * 
     * @return true if this internal nodes children are leaves, false else
     */
    public boolean isChildrenLeaves() {
        return left.isLeaf();
    }

    /**
     * Do not call on parent internal node. Call on the node you wish to get the
     * lowest kv pair of a subtree from
     * 
     * For example, if you want the new KV2 for yourself, call this function on
     * your center child.
     * 
     * @return a kv pair
     */
    public KVPair getMinimum() {

        if (isChildrenLeaves()) {
            return left.kv1;
        }
        else {
            InternalNode temp = (InternalNode) left;
            return temp.getMinimum();
        }

    }

    /**
     * Returns the KV's to their proper values based on children.
     * Handles the case where the node has underflowed as well.
     */
    public void resetKV() {

        if (isChildrenLeaves()) {

            if (center != null) {
                kv1 = center.kv1;
            }
            else {
                kv1 = null;
            }

            if (right != null) {
                kv2 = right.kv1;
            }
            else {
                kv2 = null;
            }
        }
        else {
            if (center != null) {
                InternalNode temp = (InternalNode) center;
                kv1 = temp.getMinimum();
            }
            else {
                kv1 = null;
            }

            if (right != null) {
                InternalNode temp = (InternalNode) right;
                kv2 = temp.getMinimum();
            }
            else {
                kv2 = null;
            }
        }

    }

}
