package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class GameUI {
    private int width;
    private int height;

    public GameUI(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void drawMainMenu() {
        StdDraw.clear(Color.BLACK);

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(width / 2.0, height * 0.7, "CS61BYoW");

        StdDraw.setFont(new Font("Monaco", Font.PLAIN, 20));
        StdDraw.text(width / 2.0, height * 0.55, "New Game (N)");
        StdDraw.text(width / 2.0, height * 0.48, "Load Game (L)");
        StdDraw.text(width / 2.0, height * 0.41, "Quit (Q)");

        StdDraw.show();
    }

    public void drawSeedInput(String seedText) {
        StdDraw.clear(Color.BLACK);

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.PLAIN, 24));
        StdDraw.text(width / 2.0, height * 0.6, "Enter Seed:");
        StdDraw.text(width / 2.0, height * 0.5, seedText);
        StdDraw.text(width / 2.0, height * 0.4, "Press S to start");

        StdDraw.show();
    }
}
