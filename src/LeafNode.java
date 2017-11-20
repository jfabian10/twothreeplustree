/**
 * 
 * @author johnmarshall
 * @version Sep 25, 2016
 * 
 *          Leaf Node implementation
 * 
 * 
 */
public class LeafNode extends TreeNode {

    private LeafNode next;
    private LeafNode prev;

    /**
     * overloaded constructor
     * 
     * @param kv1
     *            first kv pair
     * @param kv2
     *            second kv pair
     */
    public LeafNode(KVPair kv1, KVPair kv2) {
        super(kv1, kv2);
    }

    /**
     * overloaded constructor
     * 
     * @param kv1
     *            first kv pair, second kv2 is set to null
     */
    public LeafNode(KVPair kv1) {
        super(kv1);
    }

    /**
     * default constructor
     */
    public LeafNode() {
        // Empty Constructor
    }

    @Override
    /**
     * always returns true
     */
    boolean isLeaf() {
        return true;
    }

    /**
     * true if both KV pairs are occupied
     * 
     * @return true if leaf is full, false else
     */

    /**
     * Adds a new KVPair
     * 
     * @param newKV
     *            the new KV pair to add. This leaf shouldn't be full. If it is
     *            full this function will return false.
     * @return true if added, false else
     */
    boolean add(KVPair newKV) {

        if (isFull()) {
            return false;
        }

        if (kv1 == null && kv2 == null) { // nothing in leaf
            kv1 = newKV;
        }
        else if (kv1 != null && kv2 == null) { // kv1 full, kv2 empty
            // See which is bigger
            if (kv1.compareTo(newKV) == 1) {
                kv2 = kv1;
                kv1 = newKV;
            }
            else if (kv1.compareTo(newKV) == -1) {
                kv2 = newKV;
            }
        }
        else { // kv1 empty, kv2 full.
               // JM 10/5: Is this else statement necessary?
               // See which is bigger
            if (kv2.compareTo(newKV) == 1) {
                kv1 = newKV;
            }
            else if (kv2.compareTo(newKV) == -1) {
                kv1 = kv2;
                kv2 = newKV;
            }
        }

        return true;
    }

    /**
     * 
     * @return Returns the pointer to the next node
     */
    public LeafNode getNext() {
        return next;
    }

    /**
     * 
     * @return Returns the pointer to the previous node
     */
    public LeafNode getPrev() {
        return prev;
    }

    /**
     * 
     * @param nxt
     *            Sets next equal to nxt
     */
    public void setNext(LeafNode nxt) {
        next = nxt;
    }

    /**
     * 
     * @param prv
     *            Sets prev equal to prv
     */
    public void setPrev(LeafNode prv) {
        prev = prv;
    }

    /**
     * Use this function to remove a KVPair from the leaf node
     * 
     * @param badKV
     *            KV you wish to remove
     * @return True if removed, false else
     */
    boolean remove(KVPair badKV) {

        if (isEmpty()) {
            return false;
        }

        if (!hasKV(badKV)) {
            return false;
        }

        if (kv1 != null && kv2 != null) {

            if (kv1.compareTo(badKV) == 0) {
                kv1 = kv2;
                kv2 = null;
                return true;
            }
            else if (kv2.compareTo(badKV) == 0) {
                kv2 = null;
                return true;
            }
            else {
                return false;
            }

        }
        else { // kv1 is not null, kv2 is

            
            if (kv1 != null && kv1.compareTo(badKV) == 0) {
                kv1 = null;
                return true;
            }
            else {
                return false;
            }
        }
    }

}
