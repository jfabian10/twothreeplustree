import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 
 * MemoryPool will serve as our virtual memory, it stores bytes and uses the
 * LinkedList to refer to WHERE it has free space to store bytes.
 * 
 * @author l3ogio22, john9570
 * @version Aug 26, 2016
 */

public class MemoryPool {

    /*
     * Fields
     */
    private byte[] byteArr;

    private int trackLength;
    // private ByteBuffer b;

    // private byte[] litteBuffer; // used to communicate back to the client
    private byte[] trackLengthBuff = new byte[2];

    private int totalSize;
    private int initialSize;

    private final int trackOffset = 2;

    /*
     * Operators
     */
    private DoublyLinkedList<FreeBlock> freeBlockList;

    // private ByteBuffer pool;

    /*
     * Constructors
     */

    /**
     * @param size
     *            how big our byte array is
     * 
     */
    public MemoryPool(int size) {

        initialSize = size;
        totalSize = size;
        byteArr = new byte[size];

        // Initialize our freeblock list
        freeBlockList = new DoublyLinkedList<FreeBlock>();
        FreeBlock fb = new FreeBlock(0, size);
        freeBlockList.add(fb);

    }

    /*
     * Public Methods
     */

    /**
     * Inserts a record and returns its position handle.
     * 
     * When this is called we know the hash has been searched And the string
     * isnt in the hash table so we need to store it and return a handle
     * 
     * CASES: Search FB list, find a spot -Completely take up the open spot,
     * remove node from fb list -Dont take up the entire spot, need to shrink fb
     * list node
     * 
     * 
     * Search FB list, don't find a spot -Need to expand by adding blocksize to
     * pool and fb list -Once this is done, try again.
     * 
     * 
     * @param space
     *            contains record to be inserted
     * @param size
     *            length of record
     * @return Handle to the start position of the index of inserted record
     */
    public Handle insert(byte[] space, int size) {

        trackLength = Array.getLength(space); // alternatively we could use size
        byte[] tlBytes = ByteBuffer.allocate(4).putInt(trackLength).array();

        // trackLengthBuff is of size 2, holds beginnning
        System.arraycopy(tlBytes, 2, trackLengthBuff, 0, 2);

        // Search the freeblock list for a position to put the new record
        boolean tryFirst = true; // Flags first pass of while loop
        boolean inserted = false; // Flags insert complete
        boolean insertNow = false; // Triggers insert
        boolean freeBlockFound = false;
        int bestFreeBlockIndex = 0; // Index of bestFreeBlock

        FreeBlock currentFreeBlock = new FreeBlock(0, 0); // Initially zero
        FreeBlock bestFreeBlock = new FreeBlock(0, 0);
        Handle handle = new Handle(0);

        while (!inserted) {

            // If there is no space, we need to expand.
            if (freeBlockList.isEmpty()) {
                expand();
            }

            // if (tryFirst) {
            currentFreeBlock = freeBlockList.first();
            // }

            // Use best fit rule
            while (freeBlockList.hasNext()) {

                currentFreeBlock = freeBlockList.next();
                if (tryFirst) {
                    currentFreeBlock = freeBlockList.first();
                    tryFirst = false;
                }

                if (currentFreeBlock.length >= trackLength + 2) {
                    if (!freeBlockFound) {
                        bestFreeBlock = currentFreeBlock;
                        bestFreeBlockIndex = freeBlockList.index;
                        freeBlockFound = true;
                    }
                    else if (currentFreeBlock.length < bestFreeBlock.length) {
                        bestFreeBlock = currentFreeBlock;
                        bestFreeBlockIndex = freeBlockList.index;
                    }
                }
            }

            // Success
            if (freeBlockFound) {
                insertNow = true;
            }
            // Success on one element only
            else if (freeBlockList.first().length >= trackLength + 2) {
                bestFreeBlock = freeBlockList.first();
                bestFreeBlockIndex = freeBlockList.index;
                insertNow = true;
            }
            else { // have to expand
                expand();
                tryFirst = true; // Try from the start after expansion
            }

            if (insertNow) { // SUCCESS
                inserted = true;

                /*
                 * BYTE ARRAY LOGIC
                 */
                int insertPosition = bestFreeBlock.index;
                // System.arraycopy(src, srcPos, dest, destPos, length);
                System.arraycopy(trackLengthBuff, 0, byteArr, insertPosition,
                        2);
                System.arraycopy(space, 0, byteArr, insertPosition + 2,
                        trackLength);

                /*
                 * FREEBLOCK handleR LOGIC remove the free block b/c we fully
                 * used it
                 */
                // Move free block list curr to correct index
                freeBlockList.first();
                while (freeBlockList.index != bestFreeBlockIndex) {
                    freeBlockList.next();
                }

                if (bestFreeBlock.length == trackLength + 2) {
                    freeBlockList.remove();
                }
                else {
                    // Modify the free block
                    bestFreeBlock.length -= trackLength + 2;
                    bestFreeBlock.index += trackLength + 2;
                    freeBlockList.set(bestFreeBlock);
                }

                handle.setOffset(insertPosition);

            }

        } // EOW !inserted

        return handle;

    }

    /**
     * Removes handle from memory pool and merges the adjacent free blocks.
     * 
     * @param handleR
     *            to free
     * 
     */
    public void remove(Handle handleR) {

        int startingPoint = handleR.getOffset();
        byte[] tlBytes = ByteBuffer.allocate(4).putInt(0).array();

        // Copy track length bytes from byteArry to tlBytes
        System.arraycopy(byteArr, startingPoint, tlBytes, 2, 2);

        // Convert tlBytes to integer
        trackLength = ByteBuffer.wrap(tlBytes).getInt();

        for (int i = 0; i < trackLength + trackOffset; i++) {
            byteArr[startingPoint + i] = 0; // Null it out
        }

        // Handle the freeblock list.
        // We are either going to add a freeblock,
        // Or extend a freeblock in the FB list.
        // I think i need to iterate over all the freeblocks here
        // and check for index + length == startingPoint
        FreeBlock fb = new FreeBlock(startingPoint, trackLength + trackOffset);

        boolean liberated = false;
        boolean tryFirst = true;

        if (freeBlockList.isEmpty()) {
            liberated = true;
            freeBlockList.add(fb); // SUCCESS
        }

        FreeBlock currentFreeBlock;

        while (!liberated) {

            if (tryFirst) {
                currentFreeBlock = freeBlockList.first();
                tryFirst = false;
            }
            else if (freeBlockList.hasNext()) {
                currentFreeBlock = freeBlockList.next();
            }
            else { // Ran out of blocks to check, so add a new one.
                   // Adds a new free block in order of ascending index.
                currentFreeBlock = freeBlockList.first();

                // Special case, need to insert block in first spot
                if (currentFreeBlock.index > fb.index) {
                    FreeBlock temp = new FreeBlock(currentFreeBlock.index,
                            currentFreeBlock.length);
                    freeBlockList.set(fb);
                    freeBlockList.add(temp);
                }
                else {
                    while ((currentFreeBlock.index < fb.index)
                            && freeBlockList.hasNext()) {
                        currentFreeBlock = freeBlockList.next();
                    }
                    freeBlockList.add(fb);
                }
                liberated = true;
                break; // Leave while loop
            }

            /*
             * This segment of code checks each of the free blocks after
             * iterating to the next one above.
             */
            // Look ahead free
            if (currentFreeBlock.index == startingPoint + trackLength
                    + trackOffset) { // SUCCESS
                liberated = true;
                currentFreeBlock.index -= (trackLength + trackOffset);
                currentFreeBlock.length += trackLength + trackOffset;
                freeBlockList.set(currentFreeBlock);

            }
            // Look behind free
            else if (startingPoint == currentFreeBlock.index
                    + currentFreeBlock.length) {
                currentFreeBlock.length += trackLength + trackOffset;
                freeBlockList.set(currentFreeBlock);
                liberated = true;
            }

            /*
             * Finally, we need to verify that if we liberated, the blocks dont
             * need to doubly merge. This happens when you free a block in the
             * middle of two other free blocks.
             * 
             */
            if (liberated) { // We inserted our free block already

                currentFreeBlock = freeBlockList.first();
                while (freeBlockList.hasNext()) {
                    FreeBlock nextBlock = freeBlockList.next();

                    // Need to merge if true
                    if (nextBlock.index == currentFreeBlock.index
                            + currentFreeBlock.length) {

                        // System.out.println(dump());
                        // System.out.println(currentFreeBlock.print());
                        currentFreeBlock.length += nextBlock.length;
                        // System.out.println("Next is: " + nextBlock.print());
                        freeBlockList.set(currentFreeBlock);
                        freeBlockList.remove(); // get rid of nextBlock.

                        // Reset and try again.
                        currentFreeBlock = freeBlockList.first();

                    }

                    // increment current free block since next will increment
                    currentFreeBlock = nextBlock;

                }

            }

        }

        sortFreeBlockList(); // The order is ascending index, guaranteed.
    }

    /**
     * 
     * @param h
     *            The input handle with offset to look at
     * @return the number of bytes actually copied
     */
    public byte[] getBytes(Handle h) {

        byte[] littleBuffer;
        int startingPoint = h.getOffset();

        System.arraycopy(byteArr, startingPoint, trackLengthBuff, 0, 2);

        ByteBuffer wrapped = ByteBuffer.wrap(trackLengthBuff);
        short num = wrapped.getShort();

        littleBuffer = new byte[num];

        for (int i = 0; i < num; i++) {

            /*
             * if(i >= byteArr.length){ throw new
             * Exception("Array index error. " + "The array is " +
             * byteArr.length + "and you tried to access position: " + i); }
             */

            littleBuffer[i] = byteArr[startingPoint + trackOffset];
            startingPoint++;
        }

        return littleBuffer;
    }

    /**
     * Dumps a printout of the freeblock list
     * 
     * @return string of fb list. If list is empty then return is (totalSize, 0)
     */
    public String dump() {
        if (!freeBlockList.isEmpty()) {

            String toPrint = freeBlockList.first().print();

            while (freeBlockList.hasNext()) {
                toPrint += " -> ";
                toPrint += freeBlockList.next().print();
            }

            return toPrint;

        }
        else {
            String toPrint = "(" + totalSize + "," + "0)";
            return toPrint;
        }

    }

    /**
     * Prints the size of memPool to std out
     */
    public void size() {
        // Should be the same as totalSize
        System.out.println("The size of the memory pools byte array is: "
                + Array.getLength(byteArr));
    }

    /**
     * Whenever the pool does not have sufficient space to store the next
     * request, it will be replaced by a new array that adds an additional
     * {blocksize} bytes. Data from old array will be copied over to new array.
     * The freeblock list will be updated accordingly and (it will store the
     * handle.)!
     */
    public void expand() {

        /**
         * Handle the expansion of the FreeBlock List
         */
        if (freeBlockList.isEmpty()) {
            FreeBlock newBlock = new FreeBlock(totalSize, initialSize);
            freeBlockList.add(newBlock);
        }
        else {
            FreeBlock lastBlock = freeBlockList.last();
            int lastIndex = lastBlock.index;
            int lastLength = lastBlock.length;
            /*
             * If this is true then we just need to EXTEND the last freeblock If
             * this is false then the last freeblock isn't at the end of the
             * array so in that case we add a new freeblock to the end.
             */
            if ((lastLength + lastIndex) == totalSize) {
                lastBlock.length = lastLength + initialSize;
                freeBlockList.set(lastBlock);
            }
            else {
                /*
                 * Create a new fb. Since we are adding to the very end,
                 * totalSize is the index, and its length is equal to that of
                 * initial size.
                 */
                FreeBlock fb;
                fb = new FreeBlock(totalSize, initialSize);
                freeBlockList.last();
                freeBlockList.add(fb);
            }
        }

        /**
         * Handle the expansion of the byte array itself
         */
        // initialSize = size;
        // totalSize = size;
        // byteArr = new byte[size];
        byte[] tempByteArray = byteArr;

        byteArr = new byte[totalSize + initialSize];

        for (int i = 0; i < totalSize; i++) {
            byteArr[i] = tempByteArray[i];
        }

        totalSize += initialSize;

        System.out.println(
                "Memory pool expanded to be " + totalSize + " bytes.");

    }

    /**
     * Sorts the free block list according to increasing index.
     * 
     */
    public void sortFreeBlockList() {

        FreeBlock[] freeBlockArray = new FreeBlock[freeBlockList.size()];

        if (freeBlockList.size() <= 2) {
            return;
        }

        freeBlockArray[0] = freeBlockList.first();
        int iterator = 1;
        while (freeBlockList.hasNext()) {
            freeBlockArray[iterator] = freeBlockList.next();
            iterator++;
        }

        Arrays.sort(freeBlockArray, new Comparator<FreeBlock>() {
            public int compare(FreeBlock fb1, FreeBlock fb2) {
                if (fb1.index > fb2.index) {
                    return 1;
                }
                // else if (fb1.index < fb2.index) {
                else {
                    return -1;
                }
                // else {
                // return 0;
                // }
            }
        });

        DoublyLinkedList<FreeBlock> newFreeBlockList = 
                new DoublyLinkedList<FreeBlock>();

        iterator = 0;
        for (iterator = 0; iterator < freeBlockArray.length; iterator++) {
            newFreeBlockList.add(freeBlockArray[iterator]);
        }

        freeBlockList = newFreeBlockList;

    }
}
