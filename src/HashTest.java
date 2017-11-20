import student.TestCase;

/**
 * Test the hash table
 *
 * @author john9570
 * @version Sep 2, 2016
 */
public class HashTest extends TestCase {

    private Hash table = new Hash(10);

    /**
     * Test that the hash wont return an invalid value
     */
    public void testBadValue() {

        assertNull(table.getHandle(-1));
        assertNull(table.getHandle(15));

    }

}