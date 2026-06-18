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

    public ArrayList<Room> getRooms() {
        return this.rooms;
    }

    public void initialize() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void generateRoom(int x, int y, int roomwidth, int roomheight) {
        boolean overlaps = false;
        Room newRoom = new Room(x, y, roomwidth, roomheight);

        if (!isRoomInsideTheWorld(newRoom)) {
            newRoom = null;
            return;
        }

        for (Room room : rooms) {
            if (newRoom.overlaps(room)) {
                newRoom = null;
                return;
            }
        }
        rooms.add(newRoom);
    }

    private boolean isRoomInsideTheWorld(Room room) {
        return room.getX() > 0 &&room.getY() > 0 && room.getX() + room.getWidth() <= width && room.getY() + room.getHeight() <= height;
    }

    public void generateRandomRooms(Random random, int worldWidth, int worldHeight) {
        int attempts = RandomUtils.uniform(random, 15, 21);

        for (int i = 0; i < attempts; i++) {
            int roomWidth = RandomUtils.uniform(random, 4, 10);
            int roomHeight = RandomUtils.uniform(random, 4, 8);
            int x = RandomUtils.uniform(random, 0, worldWidth);
            int y = RandomUtils.uniform(random, 0, worldHeight);

            generateRoom(x, y, roomWidth, roomHeight);
        }
    }

    public void drawRoom() {
        for (Room room : rooms) {
            for (int x = room.getX(); x < room.getX() + room.getWidth(); x++) {
                for (int y = room.getY(); y < room.getY() + room.getHeight(); y++) {
                    if (x == room.getX() || x == room.getX() + room.getWidth() - 1 || y == room.getY() || y == room.getY() + room.getHeight() - 1) {
                        world[x][y] = Tileset.WALL;
                    }
                    else {
                        world[x][y] = Tileset.FLOOR;
                    }
                }
            }
        }
    }

    public void connectRooms(TETile[][] world, Room room1, Room room2) {
        int x, y;
        if (RandomUtils.bernoulli(random)) {
            x = room1.centerX();
            y = room2.centerY();
        }
        else {
            x = room2.centerX();
            y = room1.centerY();
        }

        horizontalHallway(world, room1, room2, y);
        verticalHallway(world, room1, room2, x);
    }

    public void connectAllRooms(TETile[][] world, ArrayList<Room> rooms) {
        for (int i = 0; i < rooms.size() - 1; i++) {
            connectRooms(world, rooms.get(i), rooms.get(i + 1));
        }
    }

    private void horizontalHallway(TETile[][] world, Room room1, Room room2, int y) {
        int centerX1 = Math.min(room1.centerX(), room2.centerX());
        int centerX2 = Math.max(room1.centerX(), room2.centerX());

        for (int x = centerX1; x < centerX2; x++) {
            world[x][y] = Tileset.FLOOR;
        }
    }

    private void verticalHallway(TETile[][] world, Room room1, Room room2, int x) {
        int centerY1 = Math.min(room1.centerY(), room2.centerY());
        int centerY2 = Math.max(room1.centerY(), room2.centerY());

        for (int y = centerY1; y < centerY2; y++) {
            world[x][y] = Tileset.FLOOR;
        }
    }

    public void addWalls(TETile[][] world) {
        int width = world.length;
        int height = world[0].length;

        int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1};
        int[] dy = {0, 0, -1, 1, -1, 1, -1, 1};

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (world[i][j].equals(Tileset.NOTHING)) {
                    for (int p = 0; p < 8; p++) {
                        int nx = i + dx[p];
                        int ny = j + dy[p];
                        if (nx < 0 || nx >= width || ny < 0 || ny >= height) {
                            continue;
                        }

                        if (world[nx][ny].equals(Tileset.FLOOR)) {
                            world[i][j] = Tileset.WALL;
                            break;
                        }
                    }
                }
            }
        }
    }
}
