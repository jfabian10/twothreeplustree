
/**
 * 
 * @author johnmarshall
 * @version Sep 25, 2016
 * 
 *          Abstract TreeNode implementation
 * 
 * 
 */
public abstract class TreeNode {

    /**
     * Public Key value pair number 1
     */
    public KVPair kv1;

    /**
     * Public key value pair number 2
     */
    public KVPair kv2;

    /**
     * Overloaded constructor
     * 
     * @param kv1
     *            sets this.kv1 to kv1
     * @param kv2
     *            sets this.kv2 to kv2
     */
    public TreeNode(KVPair kv1, KVPair kv2) {
        this.kv1 = kv1;
        this.kv2 = kv2;

    }

    /**
     * Overloaded constructor
     * 
     * @param kv1
     *            Sets this.kv1 to kv1, kv2 is null
     */
    public TreeNode(KVPair kv1) {
        this.kv1 = kv1;
    }

    /**
     * Default constructor, both kv pairs are null
     */
    public TreeNode() {
        // do nothing
    }

    /**
     * abstract is leaf? function
     * 
     * @return True is subclass is leaf false if its an internal node
     */
    abstract boolean isLeaf();

    /**
     * Prints kv pair contents
     * 
     * @return Returns a formatted string of contents
     */
    public String print() {

        String returnString = "";

        if (kv1 != null) {
            returnString += kv1.toString();
        }

        if (kv2 != null) {
            returnString += " " + kv2.toString();
        }

        return returnString;
    }

    /**
     * 
     * @param testKV
     *            KVPair you are testing for
     * @return True if this leaf contains the KVPair testKV
     */
    public boolean hasKV(KVPair testKV) {
        boolean found = false;

        if (kv1 != null) {
            found = kv1.compareTo(testKV) == 0;
        }

        if (kv2 != null) {
            found = found || kv2.compareTo(testKV) == 0;
        }

        return found;
    }

    /**
     * 
     * @return If the LeafNode is empty, returns true. Else false
     */
    public boolean isEmpty() {
        return (kv1 == null && kv2 == null);
    }

    /**
     * Use to determine if node is full or not
     * 
     * @return True if both kv pairs are used, false else
     */
    public boolean isFull() {
        return (kv1 != null && kv2 != null);
    }

}
