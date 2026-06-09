package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    public static final Random randomSeed = new Random();
    private static final int WIDTH = 70;
    private static final int HEIGHT = 60;

    public static void addHexagon(TETile[][] world, int x, int y, int s, TETile tile) {
        int height = s * 2;

        for (int row = 0; row < height; row++) {
            int width = rowWidth(s, row);
            int offset = rowOffset(s, row);

            for (int col = 0; col < width; col++) {
                if (x + offset + col >= 0 && x + offset + col < world.length
                        && y + row >= 0 && y + row < world[0].length) {
                    world[x + offset + col][y + row] = tile;
                }
            }
        }
    }

    private static int rowWidth(int s, int row) {
        if (row < s) {
            return s + row * 2;
        }
        else {
            return s + 2 * (2 * s - row - 1);
        }
    }

    private static int rowOffset(int s, int row) {
        if (row < s) {
            return -row;
        }
        else {
            return row - (2 * s - 1);
        }
    }

    public static void addHexColumn(TETile[][] world, int x, int y, int hexNum, int s) {
        for (int i = 0; i < hexNum; i++) {
            addHexagon(world, x, y + i * 2 * s, s, randomTile());
        }
    }

    private static TETile randomTile() {
        int choice = randomSeed.nextInt(5);

        if (choice == 0) {
            return Tileset.GRASS;
        } else if (choice == 1) {
            return Tileset.FLOWER;
        } else if (choice == 2) {
            return Tileset.SAND;
        } else if (choice == 3) {
            return Tileset.TREE;
        } else {
            return Tileset.MOUNTAIN;
        }
    }

    public static void drawWorld(TETile[][] world, int startX, int startY, int s) {
        int[] columnHeight = {3, 4, 5, 4, 3};
        int[] yOffsets = {0, -1, -2, -1, 0};

        for (int col = 0; col < 5; col++) {
            int x = startX + col * (2 * s - 1);
            int y = startY + yOffsets[col] * s;

            addHexColumn(world, x, y, columnHeight[col], s);
        }
    }

    public static void main(String args[]) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        drawWorld(world, 15, 15,5);
        ter.renderFrame(world);
    }
}
