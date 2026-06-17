package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.util.ArrayList;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    private TETile[][] world;
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        input = input.toUpperCase();

        int start = input.indexOf('N') + 1;
        int end = input.indexOf('S');

        String seedText = input.substring(start, end);
        long seed = Long.parseLong(seedText);

        Random random = new Random(seed);

        ter.initialize(WIDTH, HEIGHT);

        world = new TETile[WIDTH][HEIGHT];
        ArrayList<Room> rooms = new ArrayList<>();
        WorldGenerator generator = new WorldGenerator(world, WIDTH, HEIGHT, random, rooms);

        generator.initialize();
        generator.generateRandomRooms(random, WIDTH, HEIGHT);
        generator.drawRoom();
        generator.connectAllRooms(world, generator.getRooms());
        generator.addWalls(world);

        ter.renderFrame(world);

        return world;
    }

    @Override
    public String toString() {
        return TETile.toString(world);
    }
}
