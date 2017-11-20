
import java.util.NoSuchElementException;

/**
 * 
 * @author johnmarshall, l3ogio22
 * @version Aug 29, 2016
 * 
 *          Our doubly linked, non circular list will serve as our free block
 *          store tracker
 * 
 *          Note we use first fit so we should walk down the free block list
 *          from the first block and work our way over to the last
 * 
 *          API rough outline Memory manager will step through the list from the
 *          first to the last item looking at the payload which is how much free
 *          space there is at a particular location (index, length)
 *
 * @param <T>
 *            List data type, Example int, object, etc.
 */
public class DoublyLinkedList<T> {
    /*
     * Fields
     */
    private int n; // Number of elements in the list
    private Node firstNode; // Blank node to mark the beginning
    private Node lastNode; // Blank node to mark the ending
    private Node current; // To keep track of current node iteration (when
                          // iterating using next() and prev())

    /**
     * index field description: Keep track of where we are currently when
     * iterating using next() and prev()
     */
    public int index;

    // last node which is returned by prev() or next(), this is reset to null
    // upon addition or removal of Nodes
    private Node lastAccessed = null;

    /**
     * Default constructor
     */
    public DoublyLinkedList() {
        n = 0;
        index = 0;
        firstNode = new Node();
        lastNode = new Node();

        firstNode.next = lastNode;
        lastNode.prev = firstNode;

        firstNode.prev = null;
        lastNode.next = null;

        current = firstNode.next;
    }

    /*
     * T will later be an object of the FreeBlock class
     * 
     * Ex. T.index = 0, T.len = 32, (0, 32) would mean position 0 to position 31
     * (32 total positions) is free
     * 
     */
    private class Node {
        private T data;
        private Node next;
        private Node prev;
    }

    /**
     * Add element to doubly linked list in current position.
     * 
     * @param data
     *            object to be added to current position
     */
    public void add(T data) {

        /*
         * LEGACY Node newNode = new Node(); Node old_last_node = lastNode.prev;
         * 
         * newNode.data = data; newNode.next = lastNode; //point to the "empty"
         * sentinel as the next item newNode.prev = old_last_node; lastNode.prev
         * = newNode; old_last_node.next = newNode;
         */

        // This will always work even with first item
        Node currPrev = current.prev;
        Node newNode = new Node();
        Node curr = current;

        newNode.data = data;
        currPrev.next = newNode;
        newNode.next = curr;
        newNode.prev = currPrev;
        curr.prev = newNode;

        n += 1;
        index += 1;
        lastAccessed = null;

    }

    /**
     * True if list is empty, false else
     * 
     * @return See description
     */
    public boolean isEmpty() {
        return n == 0;
    }

    /**
     * @return Returns the number of items in the list
     */
    public int size() {
        return n;
    }

    /**
     * @return Returns true if there is another item, false else. Won't return
     *         true for sentinel
     */
    public boolean hasNext() {

        return index < n;
    }

    /**
     * @return Returns true if there is a previous item, false else. Won't
     *         return true for sentinel
     */
    public boolean hasPrev() {

        return index > 0;
    }

    /**
     * Returns data of what "current" is on top of then moves "current" forwards
     * in the list by one. Also sets lastAccessed to "current".
     * 
     * @return returns object of type T (from initialization)
     */
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // Call this function once to move off of sentinel front node
        if (!hasPrev()) {
            lastAccessed = current;
            current = current.next;
            index += 1;
        }

        lastAccessed = current;
        T data = current.data;
        current = current.next;
        index += 1;
        return data;
    }

    /**
     * First moves current backwards by 1, sets last accessed, then returns the
     * data "current" is on top of
     * 
     * @return returns object of type T (from initialization)
     */
    public T previous() {
        if (!hasPrev()) {
            throw new NoSuchElementException();
        }

        // Move backwards by 1 off of sentinel end node
        if (!hasNext()) {
            lastAccessed = current.prev;
            current = current.prev;
            index -= 1;
        }

        lastAccessed = current.prev;
        current = current.prev;
        T data = current.data;
        index -= 1;
        return data;
    }

    /**
     * Replace the data which was last returned by next or previous In order to
     * use this function, add() or remove() may NOT of been called last and
     * next() or previous() must have been called last.
     * 
     * @param newData
     *            is the data to replace the old data with
     */
    public void set(T newData) {
        if (lastAccessed == null) {
            throw new IllegalStateException();
        }
        lastAccessed.data = newData;
    }

    /**
     * Remove the last item that was returned from next() or prev() Can't be
     * called after a previous call to add() or remove() <-- which is this
     * function
     */
    public void remove() {
        if (lastAccessed == null) {
            throw new IllegalStateException();
        }
        Node x = lastAccessed.prev;
        Node y = lastAccessed.next;
        x.next = y;
        y.prev = x;
        n -= 1;

        if (lastAccessed == current) {
            current = y;
        }
        else {
            index -= 1;
        }
        lastAccessed = null;

    }

    /**
     * Returns the first element in the list and starts you counting from there.
     * 
     * Useful when you want to start over again with first fit method.
     * 
     * @return returns object of type T (from initialization)
     */
    public T first() {

        index = 0;
        current = firstNode.next;

        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        lastAccessed = current;
        T data = current.data;
        current = current.next;
        index += 1;
        return data;

    }

    /**
     * Returns the last element in the list
     * 
     * Moves pointer backwards by one
     * 
     * Useful when you want to view the last item
     * 
     * @return returns object of type T (from initialization)
     */
    public T last() {

        index = n;
        current = lastNode;

        if (!hasPrev()) {
            throw new NoSuchElementException();
        }
        lastAccessed = current.prev;
        T data = current.prev.data;

        return data;

    }

}
