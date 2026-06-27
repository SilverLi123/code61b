package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Engine {
    private TETile[][] world;
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    private String inputHistory;
    private GameState game;
    private boolean waitingForQ = false;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        GameUI drawer = new GameUI(WIDTH, HEIGHT);
        drawer.drawMainMenu();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());

                if (c == 'N') {
                    String seedtext = readSeedInput(drawer);
                    long seed = Long.parseLong(seedtext);

                    game = startNewGame(seed);
                    inputHistory = 'N' + seedtext + 'S';

                    ter.renderFrame(world);
                    playGameloop(ter);
                    break;
                }
                else if (c == 'L') {
                    String pretext = loadGame();

                    if (pretext.isEmpty()) {
                        System.exit(0);
                    }

                    interactWithInputString(pretext);
                    ter.renderFrame(world);
                    playGameloop(ter);

                    break;
                }
                else if (c == 'Q') {
                    saveGame();
                    break;
                }
            }
        }
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
        String preInput = "";
        String fullinput = "";
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        if (input.charAt(0) == 'L') {
            preInput = loadGame();
        }

        if (input.charAt(0) == 'L') {
            fullinput = preInput + input.substring(1);
        }
        else {
            fullinput = preInput + input;
        }

        int start = fullinput.indexOf('N') + 1;
        int end = fullinput.indexOf('S');

        String seedText = fullinput.substring(start, end);
        long seed = Long.parseLong(seedText);
        inputHistory = fullinput.substring(0, end + 1);

        game = startNewGame(seed);

        for (int i = end + 1; i < fullinput.length(); i++) {
            char c = fullinput.charAt(i);

            boolean shouldQuit = handleGameKey(c);
            ter.renderFrame(world);

            if (shouldQuit) {
                break;
            }
        }

        return world;
    }

    @Override
    public String toString() {
        return TETile.toString(world);
    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.interactWithInputString("N12345Swaswadawsawsd");
    }

    private boolean handleGameKey(char c) {
        c = Character.toUpperCase(c);

        if (waitingForQ) {
            waitingForQ = false;

            if (c == 'Q') {
                saveGame();
                return true;
            }

            return false;
        }

        if (c == ':') {
            waitingForQ = true;
            return false;
        }

        if (c == 'W') {
            game.moveAvatar(0, 1);
            inputHistory += c;
        } else if (c == 'S') {
            game.moveAvatar(0, -1);
            inputHistory += c;
        } else if (c == 'A') {
            game.moveAvatar(-1, 0);
            inputHistory += c;
        } else if (c == 'D') {
            game.moveAvatar(1, 0);
            inputHistory += c;
        }

        return false;
    }

    public void saveGame() {
        try {
            FileWriter writer = new FileWriter("savefile.txt");
            writer.write(inputHistory);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadGame() {
        try {
            File file = new File("savefile.txt");
            Scanner scanner = new Scanner(file);
            String saveInput = "";

            if (scanner.hasNextLine()) {
                saveInput = scanner.nextLine();
            }

            scanner.close();
            return saveInput;
        }
        catch (FileNotFoundException e) {
            return "";
        }
    }

    public GameState startNewGame(long seed) {
        Random random = new Random(seed);

        world = new TETile[WIDTH][HEIGHT];
        ArrayList<Room> rooms = new ArrayList<>();
        WorldGenerator generator = new WorldGenerator(world, WIDTH, HEIGHT, random, rooms);

        generator.initialize();
        generator.generateRandomRooms(random, WIDTH, HEIGHT);
        generator.drawRoom();
        generator.connectAllRooms(world, generator.getRooms());
        generator.addWalls(world);
        Avatar avatar = generator.placeAvatar(rooms, world);

        GameState game = new GameState(world, avatar);

        return game;
    }

    private String readSeedInput(GameUI drawer) {
        String seedText = "";
        drawer.drawSeedInput(seedText);

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());

                if (Character.isDigit(c)) {
                    seedText += c;
                    drawer.drawSeedInput(seedText);
                } else if (c == 'S' && seedText.length() > 0) {
                    return seedText;
                }
            }

            StdDraw.pause(20);
        }
    }

    private void playGameloop(TERenderer ter) {
        while (true) {
            ter.renderFrame(world);

            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                boolean shouldQuit = handleGameKey(c);

                if (shouldQuit) {
                    System.exit(0);
                }

            }

            StdDraw.pause(20);
        }
    }
}
