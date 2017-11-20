import java.io.IOException;

import student.TestCase;

/**
 * @author {Your Name Here}
 * @version {Put Something Here}
 */
public class SearchTreeTest extends TestCase {
    /**
     * Sets up the tests that follow. In general, used for initialization.
     */
    SearchTree searchTree;

    /**
     * Setup
     */
    public void setUp() {
        // Nothing Here
    }

    /**
     * Tests main method with bad input
     * 
     * @throws IOException
     *             This is expected
     */
    public void testMainBadArguments() throws IOException {
        searchTree = new SearchTree();

        SearchTree.main(new String[] { "10", "45" });
        String output = systemOut().getHistory();
        assertEquals("Error\n", output);
        searchTree.main(new String[] { "10", "45", "test.txt" });
    }
}
