package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class GameState {
    private TETile[][] world;
    private Avatar avatar;

    public GameState(TETile[][] world, Avatar avatar) {
        this.world = world;
        this.avatar = avatar;
    }

    public void moveAvatar(int dx, int dy) {
        int oldX = avatar.getAvatarX();
        int oldY = avatar.getAvatarY();

        int newX = oldX + dx;
        int newY = oldY + dy;

        if (world[newX][newY].equals(Tileset.FLOOR) && isRange(newX, newY)) {
            world[oldX][oldY] = Tileset.FLOOR;
            world[newX][newY] = Tileset.AVATAR;
            avatar.move(newX, newY);
        }
    }

    private boolean isRange(int x, int y) {
        return x >= 0 && y >= 0 && x < world.length && y < world[0].length;
    }
}
