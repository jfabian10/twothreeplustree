import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * World will serve as
 * 
 * World will also instantiate the memory and pass it to the Hash class.
 * 
 * @author l3ogio22
 * @version 
 */
public class World {
    // ~ Fields
    private Hash artistH;
    private Hash songH;

    private byte[] buffer;
    private byte[] bufferTwo;

    private MemoryPool memPool;

    private Handle artistHandleLastInserted;
    private Handle songHandleLastInserted;

    private Tree tree = new Tree();

    // ~ Constructors

    /**
     * Initializes the the databases.
     * 
     * @param hashSize
     *            size of table
     * @param blockSize
     *            of mem pool
     */

    public World(int hashSize, int blockSize) {
        artistH = new Hash(hashSize);
        songH = new Hash(hashSize);
        memPool = new MemoryPool(blockSize);

    }

    // ~Public Methods ........................................................

    /**
     * @param str1
     *            artist
     * @param str2
     *            song
     */
    public void insert(String str1, String str2) {


        if (insertArtist(str1)) {
            System.out
                    .println("|" + str1 + "| is added to the artist database.");
        }
        else {
            System.out.println("|" + str1
                    + "| duplicates a record already in the artist database.");
        }

        if (insertSong(str2)) {
            System.out.println("|" + str2 + "| is added to the song database.");
        }
        else {
            System.out.println("|" + str2
                    + "| duplicates a record already in the song database.");
        }

        KVPair temp = new KVPair(artistHandleLastInserted,
                songHandleLastInserted);
        KVPair reverse = new KVPair(songHandleLastInserted,
                artistHandleLastInserted);

        if (!tree.exists(temp)) { // could check either
            tree.insert(temp);
            tree.insert(reverse);
            System.out.println("The KVPair (|" + str1 + "|,|" + str2 + "|),("
                    + artistHandleLastInserted.getOffset() + ","
                    + songHandleLastInserted.getOffset()
                    + ") is added to the tree.");
            System.out.println("The KVPair (|" + str2 + "|,|" + str1 + "|),("
                    + songHandleLastInserted.getOffset() + ","
                    + artistHandleLastInserted.getOffset()
                    + ") is added to the tree.");
        }
        else {
            System.out.println("The KVPair (|" + str1 + "|,|" + str2 + "|),("
                    + artistHandleLastInserted.getOffset() + ","
                    + songHandleLastInserted.getOffset()
                    + ") duplicates a record already in the tree.");
            System.out.println("The KVPair (|" + str2 + "|,|" + str1 + "|),("
                    + songHandleLastInserted.getOffset() + ","
                    + artistHandleLastInserted.getOffset()
                    + ") duplicates a record already in the tree.");
        }

    }

    /**
     * inserts artist, then song
     * 
     * @param str1
     *            String to insert
     * @return True if insert succeeded, false else
     */
    public boolean insertArtist(String str1) {
        Handle artistHandle;
        buffer = str1.getBytes();
        if (artistExistsInPool(str1)) {
            return false;
        }
        if (artistH.numOfElts() == artistH.arrSize() / 2) {
            artistH.replaceTableTwo(memPool);
            System.out.println("Artist hash table size doubled.");

        }

        int slot = artistH.h(str1, artistH.arrSize());
        int probe = slot;
        int i = 1;
        while (artistH.getHandle(probe) != null) {
            // We want to use this position since its "empty"
            if (artistH.getHandle(probe).isTombstone) {
                break;
            }
            probe = (slot + (i * i)) % artistH.arrSize();
            i++;
        }
        artistHandle = memPool.insert(buffer, buffer.length);
        artistH.addHandle(artistHandle, probe);
        artistHandleLastInserted = artistHandle;
        return true;

    }

    /**
     * Use to insert a song
     * 
     * @param str2
     *            String of song name to insert
     * @return True if inserted, false else
     */
    public boolean insertSong(String str2) {
        Handle songHandle;
        buffer = str2.getBytes();
        if (songExistsInPool(str2)) {
            return false;
        }
        if (songH.numOfElts() == songH.arrSize() / 2) {

            songH.replaceTableTwo(memPool);
            System.out.println("Song hash table size doubled.");

        }

        int slot = songH.h(str2, songH.arrSize());

        int probe = slot;
        int i = 1;
        while (songH.getHandle(probe) != null) {
            // We want to use this position since its "empty"
            if (songH.getHandle(probe).isTombstone) {
                break;
            }
            probe = (slot + (i * i)) % songH.arrSize();
            i++;
        }

        songHandle = memPool.insert(buffer, buffer.length);
        songH.addHandle(songHandle, probe);
        songHandleLastInserted = songHandle;
        return true;
    }

    /**
     * See return
     * 
     * @param str
     *            Artist name
     * @return True if already in pool, false else
     */
    public boolean artistExistsInPool(String str) {

        buffer = str.getBytes();

        int homeSlot = artistH.h(str, artistH.arrSize());
        int probe = homeSlot;
        int i = 1;
        while (artistH.getHandle(probe) != null) {

            // bufferTwo = memPool.getBytes(artistH.getHandle(probe));

            if (!artistH.getHandle(probe).isTombstone) {
                bufferTwo = memPool.getBytes(artistH.getHandle(probe));

                if (Arrays.equals(buffer, bufferTwo)) {
                    artistHandleLastInserted = artistH.getHandle(probe);
                    return true;
                }
            }

            probe = (homeSlot + (i * i)) % artistH.arrSize();
            i++;

        }
        return false;
    }

    /**
     * See return
     * 
     * @param str
     *            Song name
     * @return True if song is in pool, false else
     */
    public boolean songExistsInPool(String str) {
        buffer = str.getBytes();

        int homeSlot = songH.h(str, songH.arrSize());
        int probe = homeSlot;
        int i = 1;
        while (songH.getHandle(probe) != null && probe <= songH.arrSize() - 1) {

            // bufferTwo = new byte[str.length()];

            if (!songH.getHandle(probe).isTombstone) {
                bufferTwo = memPool.getBytes(songH.getHandle(probe));

                if (Arrays.equals(buffer, bufferTwo)) {
                    songHandleLastInserted = songH.getHandle(probe);
                    return true;
                }
            }
            probe = (homeSlot + (i * i)) % songH.arrSize();
            i++;
        }
        return false;
    }

    /**
     * See return
     * 
     * @param str
     *            Song name
     * @return position of slot
     */
    public int getSongSlot(String str) {
        int slot = -1;
        buffer = str.getBytes();

        int homeSlot = songH.h(str, songH.arrSize());
        int probe = homeSlot;
        int i = 1;
        while (songH.getHandle(probe) != null) {

            // bufferTwo = new byte[str.length()];

            if (!songH.getHandle(probe).isTombstone) {
                bufferTwo = memPool.getBytes(songH.getHandle(probe));

                if (Arrays.equals(buffer, bufferTwo)) {
                    slot = probe;
                }
            }
            probe = (homeSlot + (i * i)) % songH.arrSize();
            i++;
        }
        return slot;
    }

    /**
     * Handle of song if it exists
     * 
     * @param str
     *            Name of the song
     * @return Handle if it exists in mem pool / hash table, else null
     */
    public Handle getSongHandle(String str) {
        int slot = -1;
        buffer = str.getBytes();

        int homeSlot = songH.h(str, songH.arrSize());
        int probe = homeSlot;
        int i = 1;
        while (songH.getHandle(probe) != null) {

            // bufferTwo = new byte[str.length()];

            if (!songH.getHandle(probe).isTombstone) {
                bufferTwo = memPool.getBytes(songH.getHandle(probe));

                if (Arrays.equals(buffer, bufferTwo)) {
                    slot = probe;
                    return songH.getHandle(slot);
                }
            }
            probe = (homeSlot + (i * i)) % songH.arrSize();
            i++;
        }
        return null;
    }

    /**
     * See return
     * 
     * @param str
     *            name of artist
     * @return slot artist is located in
     */
    public int getArtistSlot(String str) {
        int slot = -1;
        byte[] localBuffer = str.getBytes();
        byte[] localBuffer2;

        int homeSlot = artistH.h(str, artistH.arrSize());
        int probe = homeSlot;
        int i = 1;
        while (artistH.getHandle(probe) != null) {

            // bufferTwo = memPool.getBytes(artistH.getHandle(probe));

            if (!artistH.getHandle(probe).isTombstone) {
                localBuffer2 = memPool.getBytes(artistH.getHandle(probe));

                if (Arrays.equals(localBuffer, localBuffer2)) {
                    slot = probe;
                }
            }

            probe = (homeSlot + (i * i)) % artistH.arrSize();
            i++;

        }
        return slot;
    }

    /**
     * Find an artist handle in the hash table / mem pool
     * 
     * @param str
     *            String of the artist name
     * @return Handle if it exists, else null
     */
    public Handle getArtistHandle(String str) {
        int slot = -1;
        byte[] localBuffer = str.getBytes();
        byte[] localBuffer2;

        int homeSlot = artistH.h(str, artistH.arrSize());
        int probe = homeSlot;
        int i = 1;
        while (artistH.getHandle(probe) != null) {

            // bufferTwo = memPool.getBytes(artistH.getHandle(probe));

            if (!artistH.getHandle(probe).isTombstone) {
                localBuffer2 = memPool.getBytes(artistH.getHandle(probe));

                if (Arrays.equals(localBuffer, localBuffer2)) {
                    slot = probe;
                    return artistH.getHandle(slot);
                }
            }

            probe = (homeSlot + (i * i)) % artistH.arrSize();
            i++;

        }
        return null;
    }

    /**
     * Removes the song COMPLETELY
     * 
     * @param str
     *            Name of song
     */
    public void removeSongFull(String str) {

        if (!songExistsInPool(str)) {
            System.out.println(
                    "|" + str + "| does not exist in the song database.");
        }
        else {
            int tombstoneSlot = getSongSlot(str);
            Handle handleRemoved = songH.getHandle(tombstoneSlot);
            List<Handle> artistsRemoved = new ArrayList<Handle>();
            // TODO add remove tree stuff
            KVPair one = new KVPair(handleRemoved, new Handle(0));

            List<KVPair> retList = tree.list(one, true);

            for (int i = 0; i < retList.size(); i++) {

                KVPair retPair1 = retList.get(i);
                KVPair retPair2 = new KVPair(retPair1.value(), retPair1.key());
                tree.remove(retPair1);
                tree.remove(retPair2);

                // HANDLE ARTIST----------------------------------
                buffer = memPool.getBytes(retPair1.value());
                String artistName = new String(buffer);
                int artistCheckSlot = getArtistSlot(artistName);
                Handle artistHandleCheck = artistH.getHandle(artistCheckSlot);
                artistsRemoved.add(artistHandleCheck);
                // -----------------------------------------------

                // Handle artist
                buffer = memPool.getBytes(retPair2.value());
                String songName = new String(buffer);

                System.out.println("The KVPair (|" + songName + "|,|"
                        + artistName + "|) is deleted from the tree.");
                System.out.println("The KVPair (|" + artistName + "|,|"
                        + songName + "|) is deleted from the tree.");

                KVPair tempSongKV = new KVPair(retPair1.key(), new Handle(0));
                List<KVPair> tempSongList = tree.list(tempSongKV, true);
                if (tempSongList.isEmpty()) {
                    System.out.println(
                            "|" + str + "| is deleted from the song database.");
                }

                KVPair tempArtistKV = new KVPair(artistHandleCheck,
                        new Handle(0));
                List<KVPair> tempArtistList = tree.list(tempArtistKV, true);
                if (tempArtistList.isEmpty()) {
                    removeArtist(artistName);
                }

            }

            memPool.remove(handleRemoved);
            songH.remove(tombstoneSlot);

           
        }
    }

    /**
     * Removes the artist
     * 
     * @param str
     *            Name of artist
     */
    public void removeArtist(String str) {

        if (!artistExistsInPool(str)) {
            System.out.println(
                    "|" + str + "| does not exist in the artist database.");
        }
        else {
            int tombstoneSlot = getArtistSlot(str);
            Handle handleRemoved = artistH.getHandle(tombstoneSlot);
            memPool.remove(handleRemoved);
            artistH.remove(tombstoneSlot);
            System.out.println(
                    "|" + str + "| is deleted from the artist database.");
        }
    }

    /**
     * Removes the artist COMPLETELY
     * 
     * @param str
     *            Name of artist
     */
    public void removeArtistFull(String str) {

        if (!artistExistsInPool(str)) {
            System.out.println(
                    "|" + str + "| does not exist in the artist database.");
        }
        else {
            int tombstoneSlot = getArtistSlot(str);
            Handle handleRemoved = artistH.getHandle(tombstoneSlot);
            List<Handle> songsRemoved = new ArrayList<Handle>();
            // TODO add remove tree stuff
            KVPair one = new KVPair(handleRemoved, new Handle(0));

            List<KVPair> retList = tree.list(one, true);

            for (int i = 0; i < retList.size(); i++) {

                KVPair retPair1 = retList.get(i);
                KVPair retPair2 = new KVPair(retPair1.value(), retPair1.key());
                tree.remove(retPair1);
                tree.remove(retPair2);

                // HANDLE SONG----------------------------------
                buffer = memPool.getBytes(retPair1.value());
                String songName = new String(buffer);
                int songCheckSlot = getSongSlot(songName);
                Handle songHandleCheck = songH.getHandle(songCheckSlot);
                songsRemoved.add(songHandleCheck);
                // ---------------------------------------------

                // Handle artist
                buffer = memPool.getBytes(retPair2.value());
                String artistName = new String(buffer);

                System.out.println("The KVPair (|" + artistName + "|,|"
                        + songName + "|) is deleted from the tree.");
                System.out.println("The KVPair (|" + songName + "|,|"
                        + artistName + "|) is deleted from the tree.");

                KVPair tempArtistKV = new KVPair(retPair1.key(), new Handle(0));
                List<KVPair> tempArtistList = tree.list(tempArtistKV, true);
                if (tempArtistList.isEmpty()) {
                    System.out.println("|" + str
                            + "| is deleted from the artist database.");
                }

                KVPair tempSongKV = new KVPair(songHandleCheck, new Handle(0));
                List<KVPair> tempSongList = tree.list(tempSongKV, true);
                if (tempSongList.isEmpty()) {
                    removeSong(songName);
                }

            }

            memPool.remove(handleRemoved);
            artistH.remove(tombstoneSlot);

            
        }
    }

    /**
     * Removes the song
     * 
     * @param str
     *            Name of song
     */
    public void removeSong(String str) {
        if (!songExistsInPool(str)) {
            System.out.println(
                    "|" + str + "| does not exist in the song database.");
        }
        else {
            int tombstoneSlot = getSongSlot(str);
            Handle handleRemoved = songH.getHandle(tombstoneSlot);
            memPool.remove(handleRemoved);
            songH.remove(tombstoneSlot);
            System.out.println(
                    "|" + str + "| is deleted from the song database.");
        }
    }

    /**
     * Prints artists to std out
     */
    public void printArtists() {

        for (int i = 0; i < artistH.arrSize(); i++) {
            if (artistH.getHandle(i) != null
                    && !artistH.getHandle(i).isTombstone)

            {

                ///// retrieve from pool and convert it string

                buffer = memPool.getBytes(artistH.getHandle(i));
                String s = new String(buffer);
                System.out.println("|" + s + "| " + i);

            }

        }
        System.out.println("total artists: " + artistH.numOfElts());
    }

    /**
     * Returns the artist hash table
     * 
     * @return The artist hash table
     */
    public Hash getArtistHash() {
        return artistH;
    }

    /**
     * Returns the song hash table
     * 
     * @return The song hash table
     */
    public Hash getSongHash() {
        return songH;
    }

    /**
     * Prints songs to std out
     */
    public void printSongs() {
        for (int i = 0; i < songH.arrSize(); i++) {
            if (songH.getHandle(i) != null && !songH.getHandle(i).isTombstone) {
                buffer = memPool.getBytes(songH.getHandle(i));
                String s = new String(buffer);
                System.out.println("|" + s + "| " + i);
            }

        }
        System.out.println("total songs: " + songH.numOfElts());

    }

    /**
     * prints memory block
     */
    public void printBlocks() {
        System.out.println(memPool.dump());
    }

    /**
     * 
     */
    public void printTree() {
        System.out.println("Printing 2-3 tree:");
        tree.print();
    }

    /**
     * Used to delete values from only the tree
     * @param artistTrimStr
     *      Artist string
     * @param songTrimStr
     *      Song string
     */
    public void deleteTree(String artistTrimStr, String songTrimStr) {

        // System.out.println("DEBUG: Artist trim string: " + artistTrimStr);
        // System.out.println("DEBUG: Song trim string: " + songTrimStr);

        Handle tempArtHandle = getArtistHandle(artistTrimStr);
        Handle tempSongHandle = getSongHandle(songTrimStr);

        if (tempArtHandle == null) {
            System.out.println("|" + artistTrimStr
                    + "| does not exist in the artist database.");
            return;
        }

        if (tempSongHandle == null) {
            System.out.println("|" + songTrimStr
                    + "| does not exist in the song database.");
            return;
        }

        KVPair one = new KVPair(tempArtHandle, tempSongHandle);
        KVPair two = new KVPair(tempSongHandle, tempArtHandle);

        tree.remove(one);
        tree.remove(two);

        System.out.println("The KVPair (|" + artistTrimStr + "|,|" + songTrimStr
                + "|) is deleted from the tree.");
        System.out.println("The KVPair (|" + songTrimStr + "|,|" + artistTrimStr
                + "|) is deleted from the tree.");

        // Figure out if it needs to be removed from the database
        // For artists, does that artist have anymore songs?
        one = new KVPair(tempArtHandle, new Handle(0));
        two = new KVPair(tempSongHandle, new Handle(0));

        List<KVPair> retList = tree.list(one, false);

        if (retList.isEmpty()) {
            removeArtist(artistTrimStr);
        }

        // For songs, does that song have anymore artists associated with it?
        // Use the list command for this
        retList = tree.list(two, false);

        if (retList.isEmpty()) {
            removeSong(songTrimStr);
        }

    }

    /**
     * Used to list all the songs of an artist
     * @param valueStr
     *      Name of the artist
     */
    public void listArtist(String valueStr) {
        Handle tempArtHandle = getArtistHandle(valueStr);

        if (tempArtHandle == null) {
            System.out.println("|" + valueStr
                    + "| does not exist in the artist database.");
            return;
        }

        KVPair one = new KVPair(tempArtHandle, new Handle(0));

        List<KVPair> retList = tree.list(one, true);

        for (int i = 0; i < retList.size(); i++) {
            KVPair retPair = retList.get(i);
            buffer = memPool.getBytes(retPair.value());
            String s = new String(buffer);
            System.out.println("|" + s + "|");
        }
    }

    /**
     * Used to list all the artists of a song
     * @param valueStr
     *      Name of the song
     */
    public void listSong(String valueStr) {
        Handle tempSongHandle = getSongHandle(valueStr);

        if (tempSongHandle == null) {
            System.out.println(
                    "|" + valueStr + "| does not exist in the song database.");
            return;
        }

        KVPair one = new KVPair(tempSongHandle, new Handle(0));

        List<KVPair> retList = tree.list(one, true);

        for (int i = 0; i < retList.size(); i++) {
            KVPair retPair = retList.get(i);
            buffer = memPool.getBytes(retPair.value());
            String s = new String(buffer);
            System.out.println("|" + s + "|");
        }
    }

}
