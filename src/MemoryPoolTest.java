import student.TestCase;

// -------------------------------------------------------------------------
/**
 * Test the doubly linked list
 *
 * @author john9570
 * @version Sep 2, 2016
 */
public class MemoryPoolTest extends TestCase {

    private MemoryPool memPool;
    private byte[] helloStr = "hello".getBytes(); // SIZE OF 5
    private byte[] testStr = "Test".getBytes(); // SIZE OF 4
    private byte[] hiStr = "hi".getBytes(); // SIZE OF 2
    private byte[] thirty5 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".getBytes();
    private byte[] thirty = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".getBytes();
    private byte[] eighteen = "bbbbbbbbbbbbbbbbbb".getBytes(); // SIZE OF 18
    private byte[] one = "1".getBytes(); // SIZE OF 1

    private String superLong = "w8HIiZcFiFWpaGo5HSlym8i40WlMCYzt"
            + "0vEJ70jXaZvz0Fn50uz2mfiAD8hFJK3Af3ZVX511Dsb2kNE"
            + "WO9e1otqLhgSJ57wN1ZD65JJLAqZ7yEGvFhGp"
            + "C2JsXqImoDxHzXItN36OayB2NWMMCgzmLfBr"
            + "4YPc3mBPXL8RglgvTtAghOy"; // SIZE OF 175

    private byte[] superLongStr = superLong.getBytes();

    private Handle handle;

    /**
     * Sets up the tests that follow.
     */
    public void setUp() {
        memPool = new MemoryPool(32);
    }

    /**
     * Initialization test, dump and expand
     */
    public void testInit() {
        assertEquals(memPool.dump(), "(0,32)");
        memPool.expand();
        assertEquals(memPool.dump(), "(0,64)");
    }

    /**
     * Initial test. Only expand, dump, and insert work
     */
    public void testInsertOne() {
        assertEquals(memPool.dump(), "(0,32)");
        // System.out.println(memPool.dump());

        handle = memPool.insert(helloStr, 5);
        assertEquals(handle.offset, 0); // Should store this in position zero
        assertEquals(memPool.dump(), "(7,25)");
        // System.out.println(memPool.dump());

        // Test that expand works
        handle = memPool.insert(thirty5, 35);
        // System.out.println(handle.offset);
        // System.out.println(memPool.dump());
        assertEquals(handle.offset, 7);
        assertEquals(memPool.dump(), "(44,20)");

        // Test that the free blocks are used up
        handle = memPool.insert(eighteen, 18);
        // System.out.println(handle.offset);
        // System.out.println(memPool.dump());
        assertEquals(handle.offset, 44);
        assertEquals(memPool.dump(), "(64,0)"); // FB list is EMPTY

        handle = memPool.insert(thirty5, 35); // Needs to expand twice
        // System.out.println(handle.offset);
        // System.out.println(memPool.dump());

    }

    /**
     * This function tests adding stuff and retrieving it
     */
    public void testAddRetrieve() {
        assertEquals(memPool.dump(), "(0,32)");
        handle = memPool.insert(helloStr, 5);
        assertEquals(handle.offset, 0); // Should store this in position zero
        assertEquals(memPool.dump(), "(7,25)");

        /*
         * This piece tests adding to the beginning, then we remove that piece
         * so the free block list goes from (7,25), then since the beginning is
         * freed up it reverts to (0,32). End result.
         */
        byte[] holder;
        holder = memPool.getBytes(handle);
        String s = new String(holder);
        // System.out.println(s);
        assertEquals(s, "hello");
        // System.out.println(memPool.dump());
        memPool.remove(handle);
        // System.out.println(memPool.dump());

        /*
         * This test makes fb list look like: (0,7) -> (11,20) so there is a
         * used block in the middle. We then remove the middle block to ensure
         * the free block merges on both sides. End result should be (0,32)
         */
        Handle helloHandle = memPool.insert(helloStr, 5);
        assertEquals(helloHandle.offset, 0);
        Handle hiHandle = memPool.insert(hiStr, 2);
        assertEquals(hiHandle.offset, 7);
        // System.out.println(memPool.dump());

        // Remove the first handle
        memPool.remove(helloHandle);
        // System.out.println(memPool.dump());

        // Remove second handle, we should get (0,32)
        memPool.remove(hiHandle);
        // System.out.println(memPool.dump());

    }

    /**
     * tests the best fit insertion method
     */
    public void testBestFit() {
        memPool.size();
        assertEquals(memPool.dump(), "(0,32)");
        Handle handle1 = memPool.insert(helloStr, 5);
        assertEquals(handle1.offset, 0); // Should store this in position zero
        assertEquals(memPool.dump(), "(7,25)");

        Handle handle2 = memPool.insert(helloStr, 5);
        assertEquals(handle2.offset, 7);
        assertEquals(memPool.dump(), "(14,18)");

        Handle handle3 = memPool.insert(testStr, 4);
        assertEquals(handle3.offset, 14);
        assertEquals(memPool.dump(), "(20,12)");

        Handle handle4 = memPool.insert(helloStr, 5);
        assertEquals(handle4.offset, 20);
        assertEquals(memPool.dump(), "(27,5)");

        Handle handle5 = memPool.insert(testStr, 4);
        assertEquals(handle5.offset, 27);
        assertEquals(memPool.dump(), "(33,31)");

        Handle handle6 = memPool.insert(helloStr, 5);
        assertEquals(handle6.offset, 33);
        assertEquals(memPool.dump(), "(40,24)");

        memPool.remove(handle5);
        assertEquals(memPool.dump(), "(27,6) -> (40,24)");
        // System.out.println(memPool.dump());
        memPool.remove(handle2);
        // System.out.println(memPool.dump());
        assertEquals(memPool.dump(), "(7,7) -> (27,6) -> (40,24)");

        Handle handle7 = memPool.insert(hiStr, 2);
        assertEquals(handle7.offset, 27);
        // System.out.println(memPool.dump());
        assertEquals(memPool.dump(), "(7,7) -> (31,2) -> (40,24)");

        Handle handle8 = memPool.insert(testStr, 4);
        assertEquals(handle8.offset, 7);
        // System.out.println(memPool.dump());
        assertEquals(memPool.dump(), "(13,1) -> (31,2) -> (40,24)");

        memPool.remove(handle1);
        // System.out.println(memPool.dump());
        handle1 = memPool.insert(helloStr, 5);

        // Create a free block in the middle somewhere
        // This will hit the code that adds a free block
        // at an arbitrary spot, so increasing index order is preserved
        Handle handle9 = memPool.insert(helloStr, 5);
        assertEquals(handle9.offset, 40);
        assertEquals(memPool.dump(), "(13,1) -> (31,2) -> (47,17)");
        // ----
        Handle handle10 = memPool.insert(helloStr, 5);
        assertEquals(handle10.offset, 47);
        assertEquals(memPool.dump(), "(13,1) -> (31,2) -> (54,10)");
        // ----
        Handle handle11 = memPool.insert(helloStr, 5);
        assertEquals(handle11.offset, 54);
        // System.out.println(memPool.dump());
        assertEquals(memPool.dump(), "(13,1) -> (31,2) -> (61,3)");
        // ---
        memPool.remove(handle10);
        // System.out.println(memPool.dump());

        /*
         * This piece will fill up the very last free block but there are still
         * free blocks left. Then we call expand and force it to add a new free
         * block to the very end of the list.
         */
        Handle handle12 = memPool.insert(one, 1);
        assertEquals(handle12.offset, 61);
        memPool.expand();

        memPool.remove(handle12);

        handle10 = memPool.insert(helloStr, 5);
        assertEquals(handle10.offset, 47);
        assertEquals(memPool.dump(), "(13,1) -> (31,2) -> (61,35)");

        // System.out.println(memPool.dump());
        memPool.remove(handle9);
        // System.out.println(memPool.dump());
        memPool.remove(handle11);
        // System.out.println(memPool.dump());
        memPool.remove(handle10);
        // System.out.println(memPool.dump());
    }

    /**
     * Tests the free block list with empty / near empty conditions
     */
    public void testFreeBlockEmpty() {

        assertEquals(memPool.dump(), "(0,32)");
        Handle handle1 = memPool.insert(thirty, 30);
        assertEquals(handle1.offset, 0); // Should store this in position zero
        assertEquals(memPool.dump(), "(32,0)");

        memPool.remove(handle1);
        assertEquals(memPool.dump(), "(0,32)");
    }

    /**
     * Pool must expand a number of times to insert this monster
     */
    public void testLargeExpansion() {
        System.out.println("Super long inserting");
        Handle superLongHandle = memPool.insert(superLongStr, 175);
        System.out.println(memPool.dump());
        // System.out.println(superLongHandle.offset);

        byte[] holder;
        holder = memPool.getBytes(superLongHandle);
        memPool.remove(superLongHandle);
        String s = new String(holder);
        // System.out.println(s);
        assertEquals(s, superLong);
        System.out.println(memPool.dump());
    }

}
