package gitlet;

// TODO: any imports you need here

import org.knowm.xchart.internal.chartpart.AxisTickCalculator_;

import java.io.Serializable;
import java.util.Date;
import java.io.File;
import java.util.HashMap;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date timestamp;
    private String parent;
    private String secondParent;
    private TreeMap<String, String> fileMap;

    public Commit(String message, String parent, String secondParent) {
        this.message = message;
        this.parent = parent;
        this.secondParent = secondParent;
        this.fileMap = new TreeMap<>();

        if (parent == null) {
            this.timestamp = new Date(0);
        }
        else {
            this.timestamp = new Date();
        }
    }

    public String getMessage() { return this.message; }
    public Date getTimestamp() { return this.timestamp; }
    public String getParent() { return this.parent; }
    public String getSecondParent() { return this.secondParent; }
    public TreeMap<String, String> getFileMap() { return this.fileMap; }
}
