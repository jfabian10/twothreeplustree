/**
 * Handle class definition
 *
 * @author CS3114 Instructors and TAs
 * @version 9/15/2016
 */

public class Handle {
    /**
     * The position for the associated message in the memory pool
     */
    public int offset;

    /**
     * True when removing handle from hash table False when valid handle/string
     * exists in table
     */
    public boolean isTombstone = false;

    // ----------------------------------------------------------
    /**
     * Create a new Handle object.
     *
     * @param p
     *            Value for position
     */
    public Handle(int p) {
        offset = p;
    }

    // ----------------------------------------------------------
    /**
     * Overload compareTo
     *
     * @param it
     *            The handle being compared against
     * @return standard values of -1, 0, 1
     */
    public int compareTo(Handle it) {
        if (offset < it.pos()) {
            return -1;
        }
        else if (offset == it.pos()) {
            return 0;
        }
        else {
            return 1;
        }
    }

    // ----------------------------------------------------------
    /**
     * Getter for position
     *
     * @return The position
     */
    public int pos() {
        return offset;
    }

    // ----------------------------------------------------------
    /**
     * Overload toString
     *
     * @return A print string
     */
    public String toString() {
        return String.valueOf(offset);
    }

    /**
     * Sets the offset to input num's value
     * 
     * @param num
     *            sets the offset
     */
    public void setOffset(int num) {
        offset = num;
    }

    /**
     * 
     * @return the handles current offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Sets the tombStone for handle to true
     */
    public void setTombstone() {
        isTombstone = true;
    }

}
