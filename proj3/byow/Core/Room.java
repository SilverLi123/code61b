package byow.Core;

public class Room {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int centerX() {
        return this.x + this.width / 2;
    }

    public int centerY() {
        return this.y + this.height / 2;
    }

    public boolean overlaps(Room room) {
        boolean separated = this.x + width <= room.x ||
                room.x + room.width <= this.x ||
                this.y + this.height <= room.y ||
                room.y + room.height <= this.y;

        return !separated;
    }
}
