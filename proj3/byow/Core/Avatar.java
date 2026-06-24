package byow.Core;

import java.util.Random;

public class Avatar {
    private int avatarX;
    private int avatarY;

    public Avatar(int x, int y) {
        this.avatarX = x;
        this.avatarY = y;
    }

    public void move(int x, int y) {
        this.avatarX = x;
        this.avatarY = y;
    }

    public int getAvatarX() {return this.avatarX;}

    public int getAvatarY() {return this.avatarY;}
}
