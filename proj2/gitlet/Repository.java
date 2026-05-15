package gitlet;

import java.io.File;
import java.io.Serializable;

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

        Commit initial = new Commit("initial commit", null, null);

        byte[] serialized = Utils.serialize(initial);
        String sha1 = Utils.sha1(serialized);
        File commitFile = Utils.join(COMMIT_DIR, sha1);
        Utils.writeContents(commitFile, serialized);

        File branchFile = Utils.join(BRANCH_DIR, "master");
        Utils.writeContents(branchFile, sha1);
        Utils.writeContents(HEAD_FILE, "master");

        StagingArea staged = new StagingArea();
        Utils.writeContents(Utils.join(STAGE_DIR, "staging_ara"), Utils.serialize(staged));
    }

    public static void add(File file) {
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String fileSha = Utils.sha1(Utils.readContentsAsString(file));
        Commit currentCommit = getCurrentCommit();
        Utils.writeContents(Utils.join(BLOBS_DIR, fileSha), Utils.readContents(file));
        StagingArea currentStaging = getCurrentStaging();

        if (currentCommit.getFileMap().containsKey(file) && currentCommit.getFileMap().get(file) == fileSha) {
            if (currentStaging.getAddition().containsKey(file)) {
                currentStaging.getAddition().remove(file);
            }
            return;
        }

        if (currentStaging.getRemoval().containsKey(file)) {
            currentStaging.getRemoval().remove(file);
        }

        currentStaging.getAddition().put(String.valueOf(file), fileSha);
        Utils.writeContents(Utils.join(STAGE_DIR, "staging_ara"), Utils.serialize(currentStaging));
    }

    public static Commit getCurrentCommit() {
        String branchName = Utils.readContentsAsString(HEAD_FILE);
        String commitSha = Utils.readContentsAsString(Utils.join(BRANCH_DIR, branchName));
        return Utils.readObject(Utils.join(COMMIT_DIR, commitSha), Commit.class);
    }

    public static StagingArea getCurrentStaging() {
        return Utils.readObject(Utils.join(STAGE_DIR, "staging_area"), StagingArea.class);
    }

    public static void commit(String message, String parent, String secondParent) {

    }

    public static void rm(File file) {

    }

    public static void log() {

    }

    public static void global_log() {

    }

    public static void find(String message) {

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
