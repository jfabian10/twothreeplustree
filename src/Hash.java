import java.nio.charset.StandardCharsets;

/**
 * Hash represents our client that will interact with the MemoryPool to its
 * information.
 * 
 *
 *
 * @author CS3114 staff
 * @version August 27, 2016
 */

public class Hash {

    private Handle[] handleArr;

    private byte[] buffer;

    // private String[] arr; /// for testing purposes this will be a
    private int numOfElements = 0;
    private int arrSize;

    /**
     * Create a new Hash object.
     * 
     * @param num
     *            size of table
     * 
     */
    public Hash(int num) {

        // arr = new String[num];
        handleArr = new Handle[num];

        arrSize = num;

    }

    /**
     * Compute the hash function. Uses the "sfold" method from the OpenDSA
     * module on hash functions
     *
     * @param s
     *            The string that we are hashing
     * @param m
     *            The size of the hash table
     * @return The home slot for that string
     */
    // Make this private in your project.
    // This is private for distributing hash function in a way that will
    // pass milestone 1 without change.

    public int h(String s, int m) {
        int intLength = s.length() / 4;
        long sum = 0;
        for (int j = 0; j < intLength; j++) {
            char[] c = s.substring(j * 4, (j * 4) + 4).toCharArray();
            long mult = 1;
            for (int k = 0; k < c.length; k++) {
                sum += c[k] * mult;
                mult *= 256;
            }
        }

        char[] c = s.substring(intLength * 4).toCharArray();
        long mult = 1;
        for (int k = 0; k < c.length; k++) {
            sum += c[k] * mult;
            mult *= 256;
        }

        return (int) (Math.abs(sum) % m);
    }

    /**
     * Removes handle from hash table by setting tombstone
     * 
     * @param num
     *            position within hash table to remove
     */
    public void remove(int num) {
        handleArr[num].setTombstone();
        numOfElements--;

    }

    /**
     * replaces the hash table
     * 
     * @param mp
     *            memory pool
     */
    public void replaceTableTwo(MemoryPool mp) {
        Handle[] bigger = new Handle[arrSize * 2];
        Handle[] temp = new Handle[arrSize];
        temp = handleArr;
        handleArr = bigger;

        //// rehashing

        int oldArrSize = arrSize;
        numOfElements = 0;
        arrSize *= 2;

        for (int i = 0; i < oldArrSize; i++) {
            if (temp[i] != null && !temp[i].isTombstone) {
                /// get bytes
                buffer = mp.getBytes(temp[i]);
                String byteToString = new String(buffer,
                        StandardCharsets.UTF_8);
                int newSlot = h(byteToString, arrSize);

                // bigger[newSlot] = temp[i];
                // numOfElements++;

                int probe = newSlot;
                int j = 1;
                while (getHandle(probe) != null) {

                    probe = (newSlot + (j * j)) % arrSize();
                    j++;
                }

                addHandle(temp[i], probe);
            }
        }

    }

    /**
     * See return value
     * 
     * @return a handle array
     */
    public Handle[] getTable() {
        return handleArr;
    }

    /**
     * Sets the table
     * 
     * @param tble
     *            handle array to pass in
     */
    // public void setTable(Handle[] tble) {
    // handleArr = tble;
    // }

    /**
     * @return numOfElements in table
     */
    public int numOfElts() {
        return numOfElements;
    }

    /**
     * @return size of table
     */
    public int arrSize() {
        return arrSize;
    }

    /**
     * Sets the array size
     * 
     * @param n
     *            Sets the array size to n
     */
    public void setSize(int n) {
        arrSize = n;
    }

    /**
     * Returns a handle at a specific position
     * 
     * @param i
     *            position of handle you want
     * @return handle at position i
     */
    public Handle getHandle(int i) {
        if (i >= arrSize || i < 0) {
            return null;
        }
        return handleArr[i];
    }

    /**
     * Adds a handle to a specific position
     * 
     * @param h
     *            handle to insert
     * @param num
     *            position to insert handle at
     */
    public void addHandle(Handle h, int num) {
        handleArr[num] = h;
        numOfElements++;
    }

}
