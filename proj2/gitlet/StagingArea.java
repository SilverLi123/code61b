package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

public class StagingArea implements Serializable {
    private TreeMap<String, String> addition;
    private TreeMap<String, String> removal;

    public StagingArea() {
        addition = new TreeMap<>();
        removal = new TreeMap<>();
    }

    public TreeMap<String, String> getAddition() {return this.addition;}
    public TreeMap<String, String> getRemoval() {return this.removal;}
}
