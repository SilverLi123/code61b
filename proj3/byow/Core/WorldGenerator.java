package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class WorldGenerator {
    private TETile[][] world;
    private int width;
    private int height;
    private Random random;
    private ArrayList<Room> rooms;

    public WorldGenerator(TETile[][] world, int width, int height, Random random, ArrayList<Room> rooms) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.random = random;
        this.rooms = rooms;
    }

    public void initialize() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void generateRoom(int x, int y, int width, int height) {
        boolean overlaps = false;
        Room newRoom = new Room(x, y, width, height);
        for (Room room : rooms) {
            if (newRoom.overlaps(room)) {
                newRoom = null;
                return;
            }
        }
        rooms.add(newRoom);
    }

    public void drawRoom() {
        for (Room room : rooms) {
            for (int x = room.getX(); x < room.getWidth(); x++) {
                for (int y = room.getY(); y < room.getHeight(); y++) {
                    if (x == room.getX() || x == room.getX() + width - 1 || y == room.getY() || y == room.getY() + height + 1) {
                        world[x][y] = Tileset.WALL;
                    }
                    else {
                        world[x][y] = Tileset.FLOOR;
                    }
                }
            }
        }
    }

    public void connectRooms() {

    }

    public void addWalls() {

    }
}
