import student.TestCase;

/**
 * Tests the World class.
 * 
 * 
 * @author l3oGi_000
 * @version Sep 11, 2016
 */
public class WorldTest extends TestCase {

    private World world;

    /**
     * Inits db
     */
    public void setUp() {
        world = new World(10, 32);

    }

    /**
     * Testing of insert artists function
     */
    public void testInsertArtist() {
        assertTrue(world.insertArtist("Drake"));
        assertTrue(world.insertSong("Too Good"));

        assertTrue(world.insertArtist("Logic"));
        assertTrue(world.insertSong("Alright"));

        assertTrue(world.artistExistsInPool("Drake"));
        assertFalse(world.artistExistsInPool("Jesus"));
        assertTrue(world.songExistsInPool("Too Good"));
        assertFalse(world.songExistsInPool("TooGood"));

        assertTrue(world.insertSong("Song1"));
        assertTrue(world.insertSong("Song3"));
        assertTrue(world.insertSong("Song4"));

        assertTrue(world.insertSong("Hi It's Me"));

        assertTrue(world.insertArtist("Kanye"));
        assertTrue(world.insertArtist("G-Eazy"));
        assertTrue(world.insertArtist("Eminem"));

        assertTrue(world.insertArtist("Madonna"));
        assertFalse(world.insertArtist("Madonna"));
        assertFalse(world.insertArtist("Madonna"));
    }

    /**
     * Testing with input that is more complex
     */
    public void testComplicatedInsert() {
        world.insert("The Chainsmokers", "Closer");
        world.insert("The Chainsmokers", "Closer");
        world.insert("The Chainsmokers", "Closer2");
        world.insert("The Chainsmokers2", "Closer");
        world.insert("Major Lazer", "Cold Water");
        world.insert("Sia", "Cheap Thrills");
        world.insert("21 Pilots", "Heathens");
        world.insert("Kiiara", "Gold");
        world.insert("Adele", "Send My Love"); /// adds sixth element
        assertEquals(7, world.getArtistHash().numOfElts()); /// fix artist
        assertEquals(7, world.getSongHash().numOfElts());
        world.printArtists();
        world.printSongs();

        assertEquals(20, world.getSongHash().arrSize());
        assertEquals(20, world.getArtistHash().arrSize());

        assertTrue(world.songExistsInPool("Closer")); //// found in the memory
                                                      //// pool
        assertTrue(world.songExistsInPool("Heathens"));

        //////// first expansion successful

        world.insert("Drake", "Too Good");
        world.insert("Wiz", "UOENO");
        world.insert("Charlie Puth", "We Don't Talk Anymore");
        world.insert("Flume", "Never Be Like You");

        world.printArtists();
        world.printSongs();

        world.insert("DJ Khaled", "For Free"); /// adding 11th track
        world.printArtists();
        world.printSongs();

        assertEquals(40, world.getSongHash().arrSize());
        assertEquals(40, world.getArtistHash().arrSize());
        assertEquals(12, world.getArtistHash().numOfElts());
        assertEquals(12, world.getSongHash().numOfElts());

        ///// second expansion success

        world.insert("12track", "111");
        world.insert("13track", "222");
        world.insert("14track", "333");
        world.insert("15track", "444");
        world.insert("16track", "555");
        world.insert("17track", "696");
        world.insert("18track", "777");
        world.insert("19track", "888");
        world.insert("20track", "999");
        world.insert("21track", "1000"); /// added 21s track

        assertEquals(22, world.getArtistHash().numOfElts());
        assertEquals(22, world.getSongHash().numOfElts());
        assertEquals(80, world.getSongHash().arrSize());
        world.printSongs();
        world.printArtists();

    }

    /**
     * Testing of probe boundaries (artist)
     */
    public void testInsertToProbe() {
        assertTrue(world.insertArtist("hi"));
        assertTrue(world.insertArtist("her"));
        assertTrue(world.insertArtist("his"));
        world.removeArtist("her");
        assertFalse(world.artistExistsInPool("her"));
        assertFalse(world.insertArtist("his"));
        assertEquals(-1, world.getArtistSlot("her"));
        assertEquals(8, world.getArtistSlot("his"));

        assertTrue(world.insertSong("hi"));
        assertTrue(world.insertSong("her"));
        assertTrue(world.insertSong("his"));
        world.removeSong("her");
        assertFalse(world.songExistsInPool("her"));
        assertFalse(world.insertSong("his"));
        assertEquals(-1, world.getSongSlot("her"));
        assertEquals(8, world.getSongSlot("his"));

    }

    /**
     * Testing of song probe boundaries
     */
    public void testInsertSongProbe() {
        assertTrue(world.insertSong("hi"));
        assertTrue(world.insertSong("her"));
        assertTrue(world.insertSong("his"));
        world.printSongs();
        world.removeSong("her");
        assertFalse(world.songExistsInPool("her"));

        world.insertSong("her");
        assertTrue(world.songExistsInPool("her"));
        assertFalse(world.insertSong("his"));
        assertEquals(-1, world.getSongSlot("ttt"));
        assertEquals(8, world.getSongSlot("his"));

    }

    /**
     * Testing of artist functions
     */
    public void testArtistExistInPool() {
        assertTrue(world.insertArtist("hi"));
        assertTrue(world.insertArtist("her"));
        assertTrue(world.insertArtist("his"));
        assertTrue(world.insertArtist("hez"));

        world.printArtists();

        assertEquals(2, world.getArtistSlot("hez"));
        assertEquals(5, world.getArtistSlot("her"));
        assertEquals(8, world.getArtistSlot("his"));

        world.removeArtist("hez");
        world.printArtists();
        assertEquals(-1, world.getArtistSlot("hez"));
        assertFalse(world.artistExistsInPool("hed"));
    }

}
