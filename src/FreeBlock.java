
/**
 * FreeBlock simply keeps track of the unused indexes in the byte array Memory
 * Pool.
 * 
 * 
 * 
 * @author johnmarshall
 * @version 1 Example of index / length suppose index = 0, len = 32, then (0,
 *          32) would mean position 0 to position 31 (32 total positions) is
 *          free
 */
public class FreeBlock {

    /**
     * refers to where in the array free space starts
     */
    public int index;

    /**
     * how much free space starting from index
     */
    public int length;

    /**
     * Constructor
     * 
     * @param i
     *            sets index
     * @param l
     *            sets length
     */
    public FreeBlock(int i, int l) {
        index = i;
        length = l;
    }

    /**
     * @return index and length of block
     */
    public String print() {
        return "(" + index + "," + length + ")";
    }

}
