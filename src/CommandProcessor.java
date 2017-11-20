import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

/**
 * 
 * In a nutshell, there are three commands that the database and memory pool
 * will need to process and handle accordingly.
 * 
 * @author l3ogio22
 * @version Aug 26, 2016
 */

public class CommandProcessor {

    /*
     * Fields
     */
    private World world;

    /*
     * Constructors
     */

    // public CommandProcessor(MemoryPool pool, Hash tables) {
    //
    // memPool = pool;
    // htable = tables;
    //
    // }

    /**
     * @param args
     *            arguments
     * 
     */
    public CommandProcessor(String[] args) {
        int hashSize = Integer.parseInt(args[0]);
        int blockSize = Integer.parseInt(args[1]);
        world = new World(hashSize, blockSize);

    }

    // ~Public Methods ........................................................

    /**
     * @param filename
     *            source where it reads from
     * 
     * @throws IOException
     */
    public void readTextFile(String filename) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(filename, "r");
        String line = raf.readLine();
        while (line != null) {
            Scanner sc = new Scanner(line);
            String word = sc.next();

            //System.out.println("<INPUT>" + line);

            if (word.equals("insert")) {

                if (line.contains("<SEP>")) {
                    String substr = line.replace("insert", "");
                    String[] subpart = substr.split("<SEP>");
                    String artistTrimStr = subpart[0].trim();
                    String songTrimStr = subpart[1].trim();
                    world.insert(artistTrimStr, songTrimStr);

                }
                else {
                    System.out.println("Can't insert");
                }

            }

            else if (word.equals("remove")) {
                String removeStr = sc.next();

                if (removeStr.equals("artist")) {
                    String remArtist = line.replace("remove artist", "");
                    String clean = remArtist.trim();
                    world.removeArtistFull(clean);
                }
                else if (removeStr.equals("song")) {
                    String remSong = line.replace("remove song", "");
                    String cleanTwo = remSong.trim();
                    world.removeSongFull(cleanTwo);
                }
                else {
                    System.out.println("Can't remove object");
                }
            }

            else if (word.equals("delete")) {
                if (line.contains("<SEP>")) {
                    String substr = line.replace("delete", "");
                    String[] subpart = substr.split("<SEP>");
                    String artistTrimStr = subpart[0].trim();
                    String songTrimStr = subpart[1].trim();
                    world.deleteTree(artistTrimStr, songTrimStr);

                }
                else {
                    System.out.println("Can't delete");
                }
            }

            else if (word.equals("print")) {
                String printStr = sc.next();

                if (printStr.equals("artist")) {
                    world.printArtists();
                }
                else if (printStr.equals("song")) {
                    world.printSongs();
                }
                else if (printStr.equals("blocks")) {
                    world.printBlocks();
                }
                else if (printStr.equals("tree")) {
                    world.printTree();
                }
                else {
                    System.out.println("Can't print object");

                }
            }

            else if (word.equals("list")) {
                String printStr = sc.next();

                if (printStr.equals("artist")) {
                    String valueString = line.replace("list artist", "");
                    world.listArtist(valueString.trim());
                }
                else if (printStr.equals("song")) {
                    String valueString = line.replace("list song", "");
                    world.listSong(valueString.trim());
                }

            }

            else {
                System.out.println("Invalid line");

            }

            line = raf.readLine();

        }

    }

}
