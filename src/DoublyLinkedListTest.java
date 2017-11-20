import java.util.NoSuchElementException;

import student.TestCase;

// -------------------------------------------------------------------------
/**
 * Test the doubly linked list
 *
 * @author john9570
 * @version Sep 2, 2016
 */

public class DoublyLinkedListTest extends TestCase {

    private DoublyLinkedList<FreeBlock> fbList;
    private FreeBlock fb1;
    private FreeBlock fb2;
    private FreeBlock fb3;

    /**
     * Sets up the tests that follow.
     */
    public void setUp() {
        fbList = new DoublyLinkedList<FreeBlock>();
        fb1 = new FreeBlock(0, 32);
        fb2 = new FreeBlock(32, 10);
        fb3 = new FreeBlock(50, 5);

    }

    /**
     * Test the add function, first function, and next functionality
     */
    public void testAdd() {
        fbList.add(fb1);
        fbList.add(fb2);
        fbList.add(fb3);

        assertEquals(fbList.first().print(), fb1.print()); // Tests the first()
                                                           // function
        assertEquals(fbList.next().print(), fb2.print());
        assertEquals(fbList.next().print(), fb3.print());
    }

    /**
     * Test the remove functionality, hasNext() - T/F, hasPrevious() - T/F, as
     * well as the prev() functionality
     */
    public void testRemove() {

        // Be in the state we left off in after add
        testAdd();

        assertFalse(fbList.hasNext());
        assertEquals(fbList.previous().print(), fb2.print());

        fbList.remove(); // Remove (32,10)

        assertTrue(fbList.hasPrev());
        assertEquals(fbList.previous().print(), fb1.print());

        assertFalse(fbList.hasPrev());
        assertTrue(fbList.hasNext());
        assertEquals(fbList.next().print(), fb3.print());

    }

    /**
     * Test beginning edge case
     */
    public void testBeginning() {

        assertTrue(fbList.isEmpty());
        assertEquals(0, fbList.size());
        fbList.add(fb1);
        fbList.add(fb2);

        fbList.first();
        assertTrue(fbList.hasNext());
    }

    /**
     * Tests the set() functionality, isEmpty(), and size()
     */
    public void testSet() {

        assertTrue(fbList.isEmpty());
        assertEquals(0, fbList.size());

        fbList.add(fb1);
        fbList.add(fb2);
        fbList.add(fb3);

        assertFalse(fbList.isEmpty());
        assertEquals(3, fbList.size());

        assertEquals(fbList.first().print(), fb1.print());
        assertEquals(fbList.next().print(), fb2.print());

        FreeBlock newfb = new FreeBlock(75, 75);

        fbList.set(newfb);

        assertEquals(fbList.first().print(), fb1.print());
        assertEquals(fbList.next().print(), newfb.print());
        assertEquals(fbList.next().print(), fb3.print());

    }

    /**
     * Tests an edge case resulting in index getting decremented after a remove
     */
    public void testEdge() {

        fbList.add(fb1);
        assertEquals(fbList.first().print(), fb1.print());
        fbList.remove();

        assertTrue(fbList.isEmpty());

        fbList.add(fb1);
        fbList.add(fb2);
        fbList.add(fb3);

        assertEquals(fbList.last().print(), fb3.print());

        fbList.remove();

        assertEquals(fbList.first().print(), fb1.print());
        assertEquals(fbList.next().print(), fb2.print());
        assertFalse(fbList.hasNext());

        assertEquals(fbList.last().print(), fb2.print());
        assertEquals(fbList.previous().print(), fb1.print());

        fb2.length += 15;

        fbList.last();
        fbList.set(fb2);

        /*
         * Check that when we access last, then do an add, the new last is what
         * we JUST added
         */
        assertEquals(fbList.last().print(), fb2.print());
        fbList.add(fb3);
        assertEquals(fbList.last().print(), fb3.print());

    }

    /**
     * Tests exceptions
     */
    public void testExceptions() {

        try {
            fbList.remove();
        }
        catch (IllegalStateException e) {
            assertNotNull(e);
        }

        try {
            fbList.set(fb1);
        }
        catch (IllegalStateException e) {
            assertNotNull(e);
        }

        try {
            fbList.previous();
            fbList.previous();
        }
        catch (NoSuchElementException e) {
            assertNotNull(e);
        }

        try {
            fbList.next();
            fbList.next();
        }
        catch (NoSuchElementException e) {
            assertNotNull(e);
        }

        try {
            fbList.first();
        }
        catch (NoSuchElementException e) {
            assertNotNull(e);
        }

    }

    /**
     * Assures that last() returns exception
     */
    public void testLastNull() {

        try {
            fbList.last();
        }
        catch (NoSuchElementException e) {
            assertNotNull(e);
        }
    }

}
