package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File BLOBS_DIR = join(GITLET_DIR,"blobs");

    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");

    public static final File BRANCH_DIR = join(GITLET_DIR, "branches");

    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    public static final File STAGE_DIR = join(GITLET_DIR, "staging");

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        GITLET_DIR.mkdirs();
        BLOBS_DIR.mkdirs();
        COMMIT_DIR.mkdirs();
        BRANCH_DIR.mkdirs();
        STAGE_DIR.mkdirs();

        Commit initial = new Commit("initial commit", null, null, new TreeMap<>());

        byte[] serialized = Utils.serialize(initial);
        String sha1 = Utils.sha1(serialized);
        File commitFile = Utils.join(COMMIT_DIR, sha1);
        Utils.writeContents(commitFile, serialized);

        File branchFile = Utils.join(BRANCH_DIR, "master");
        Utils.writeContents(branchFile, sha1);
        Utils.writeContents(HEAD_FILE, "master");

        StagingArea staged = new StagingArea();
        Utils.writeContents(Utils.join(STAGE_DIR, "staging_area"), Utils.serialize(staged));
    }

    public static void add(String filename) {
        File file = Utils.join(CWD, filename);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String fileSha = Utils.sha1(Utils.readContents(file));
        Commit currentCommit = getCurrentCommit();
        Utils.writeContents(Utils.join(BLOBS_DIR, fileSha), Utils.readContents(file));
        StagingArea currentStaging = getCurrentStaging();

        if (currentCommit.getFileMap().containsKey(filename) && currentCommit.getFileMap().get(filename).equals(fileSha)) {
            if (currentStaging.getAddition().containsKey(filename)) {
                currentStaging.getAddition().remove(filename);
            }
            return;
        }

        if (currentStaging.getRemoval().containsKey(filename)) {
            currentStaging.getRemoval().remove(filename);
        }

        currentStaging.getAddition().put(String.valueOf(filename), fileSha);
        Utils.writeContents(Utils.join(STAGE_DIR, "staging_area"), Utils.serialize(currentStaging));
    }

    private static String getCurrentSha() {
        String branchName = Utils.readContentsAsString(HEAD_FILE);
        return Utils.readContentsAsString(Utils.join(BRANCH_DIR, branchName));
    }

    private static Commit getCurrentCommit() {
        String commitSha = getCurrentSha();
        return Utils.readObject(Utils.join(COMMIT_DIR, commitSha), Commit.class);
    }

    private static StagingArea getCurrentStaging() {
        return Utils.readObject(Utils.join(STAGE_DIR, "staging_area"), StagingArea.class);
    }

    public static void commit(String message) {
        if (message == null) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        StagingArea currentStaging = getCurrentStaging();
        if (currentStaging.getAddition().isEmpty() && currentStaging.getRemoval().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        Commit currentCommit = getCurrentCommit();
        TreeMap<String, String> newFileMap = new TreeMap<>(currentCommit.getFileMap());

        for (String fileanme : currentStaging.getAddition().keySet()) {
            newFileMap.put(fileanme, currentStaging.getAddition().get(fileanme));
        }
        for (String filename : currentStaging.getRemoval().keySet()) {
            newFileMap.remove(filename);
        }

        Commit newCommit = new Commit(message, getCurrentSha(), null, newFileMap);
        byte[] serialized = Utils.serialize(newCommit);
        String newSha = Utils.sha1(serialized);
        Utils.writeContents(Utils.join(COMMIT_DIR, newSha), serialized);

        String branchName = Utils.readContentsAsString(HEAD_FILE);
        Utils.writeContents(Utils.join(BRANCH_DIR, branchName), newSha);

        currentStaging.getAddition().clear();
        currentStaging.getRemoval().clear();
        Utils.writeContents(Utils.join(STAGE_DIR, "staging_area"), Utils.serialize(currentStaging));
    }

    public static void rm(String filename) {
        File file = Utils.join(CWD, filename);
        Commit currentCommit = getCurrentCommit();
        StagingArea currentStaging = getCurrentStaging();

        if (!currentCommit.getFileMap().containsKey(filename) && !currentStaging.getAddition().containsKey(filename)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        if (currentStaging.getAddition().containsKey(filename)) {
            currentStaging.getAddition().remove(filename);
        }

        if (currentCommit.getFileMap().containsKey(filename)) {
            currentStaging.getRemoval().put(filename, currentCommit.getFileMap().get(filename));
            Utils.restrictedDelete(file);
        }
        Utils.writeContents(Utils.join(STAGE_DIR, "staging_area"), Utils.serialize(currentStaging));
    }

    private static void printCommit(Commit currentCommit) {
        System.out.println("===");
        System.out.println("commit " + Utils.sha1(Utils.serialize(currentCommit)));
        if (currentCommit.getSecondParent() != null) {
            String parent1 = currentCommit.getParent().substring(0, 7);
            String parent2 = currentCommit.getSecondParent().substring(0, 7);
            System.out.println("Merge: " + parent1 + " " + parent2);
        }
        Date timestamp = currentCommit.getTimestamp();
        String dateStr = String.format("%ta %tb %td %tT %tY %tz", timestamp, timestamp, timestamp, timestamp, timestamp, timestamp);
        System.out.println("Date: " + dateStr);
        System.out.println(currentCommit.getMessage());
        System.out.println();
    }

    public static void log() {
        String branchName = Utils.readContentsAsString(HEAD_FILE);
        Commit currentCommit = getCurrentCommit();

        while (currentCommit != null) {
            printCommit(currentCommit);

            if (currentCommit.getParent() == null) {break;}

            currentCommit = Utils.readObject(Utils.join(COMMIT_DIR, currentCommit.getParent()), Commit.class);
        }
    }

    public static void global_log() {
        List<String> commitFile = Utils.plainFilenamesIn(COMMIT_DIR);
        for (String sha : commitFile) {
            Commit c = Utils.readObject(Utils.join(COMMIT_DIR, sha), Commit.class);
            printCommit(c);
        }
    }

    public static void find(String message) {
        List<String> commitFiles = Utils.plainFilenamesIn(COMMIT_DIR);
        boolean found = false;

        for (String sha : commitFiles) {
            Commit c = Utils.readObject(Utils.join(COMMIT_DIR, sha), Commit.class);
            String commitMessage = c.getMessage();
            if (commitMessage.equals(message)) {
                System.out.println(sha);
                found = true;
            }
            }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {

    }

    public static void checkoutFile(String filename) {

    }

    public static void checkoutBranch(String branch) {

    }

    public static void checkoutFileFromCommit(String commitId, String filename) {

    }

    public static void branch(String branch) {

    }

    public static void rm_branch(String branch) {

    }

    public static void reset(String sha) {

    }

    public static void merge(String branch) {

    }


}
