package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
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
            if (currentStaging.getRemoval().containsKey(filename)) {
                currentStaging.getRemoval().remove(filename);
            }
            Utils.writeContents(Utils.join(STAGE_DIR, "staging_area"), Utils.serialize(currentStaging));
            return;
        }

        if (currentStaging.getRemoval().containsKey(filename)) {
            currentStaging.getRemoval().remove(filename);
        }

        currentStaging.getAddition().put(filename, fileSha);
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
        if (message == null || message.isEmpty()) {
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

        for (String filename : currentStaging.getAddition().keySet()) {
            newFileMap.put(filename, currentStaging.getAddition().get(filename));
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
        String branchName = Utils.readContentsAsString(HEAD_FILE);
        List<String> branches = Utils.plainFilenamesIn(BRANCH_DIR);
        System.out.println("=== Branches ===");
        for (String branch : branches) {
            if (branch.equals(branchName)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();

        StagingArea currrentStaging = getCurrentStaging();

        System.out.println("=== Staged Files ===");
        for (String filename : currrentStaging.getAddition().keySet()) {
            System.out.println(filename);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String filename : currrentStaging.getRemoval().keySet()) {
            System.out.println(filename);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public static void checkoutFile(String filename) {
        Commit currentCommit = getCurrentCommit();
        if (!currentCommit.getFileMap().containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String blobSha = currentCommit.getFileMap().get(filename);
        byte[] content = Utils.readContents(Utils.join(BLOBS_DIR, blobSha));
        Utils.writeContents(Utils.join(CWD, filename), content);
    }

    private static void checkUntrackedFiles(Commit targetCommit) {
        Commit currentCommit = getCurrentCommit();
        List<String> cwdFiles = Utils.plainFilenamesIn(CWD);
        for (String filename : cwdFiles) {
            boolean isTracked = currentCommit.getFileMap().containsKey(filename);
            boolean isInTarget = targetCommit.getFileMap().containsKey(filename);
            if (!isTracked && isInTarget) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    private static void writeContentToCWD(Commit targetCommit) {
        for (String filename : targetCommit.getFileMap().keySet()) {
            String blobSha = targetCommit.getFileMap().get(filename);
            byte[] content = Utils.readContents(Utils.join(BLOBS_DIR, blobSha));
            Utils.writeContents(Utils.join(CWD, filename), content);
        }
    }

    private static void clearStagingArea() {
        StagingArea staging = getCurrentStaging();
        staging.getAddition().clear();
        staging.getRemoval().clear();
        Utils.writeContents(Utils.join(STAGE_DIR, "staging_area"), Utils.serialize(staging));
    }

    public static void checkoutBranch(String branch) {
        File branchFile = Utils.join(BRANCH_DIR, branch);
        if (!branchFile.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (Utils.readContentsAsString(HEAD_FILE).equals(branch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        String targetSha = Utils.readContentsAsString(branchFile);
        Commit targetCommit = Utils.readObject(Utils.join(COMMIT_DIR, targetSha), Commit.class);
        Commit currentCommit = getCurrentCommit();
        checkUntrackedFiles(targetCommit);

        for (String filename : currentCommit.getFileMap().keySet()) {
            if (! targetCommit.getFileMap().containsKey(filename)) {
                Utils.restrictedDelete(filename);
            }
        }

        writeContentToCWD(targetCommit);
        Utils.writeContents(HEAD_FILE, branch);
        clearStagingArea();
    }

    private static Commit findCommit(String commitId) {
        List<String> commitFile = Utils.plainFilenamesIn(COMMIT_DIR);
        boolean commit_exist = false;
        Commit targetCommit = null;

        if (commitId.length() < 40) {
            for (String sha : commitFile) {
                if (sha.startsWith(commitId)) {
                    commitId = sha;
                    break;
                }
            }
        }

        for (String commit : commitFile) {
            if (commit.equals(commitId)) {
                targetCommit = Utils.readObject(Utils.join(COMMIT_DIR, commit), Commit.class);
                commit_exist = true;
                break;
            }
        }
        return targetCommit;
    }

    public static void checkoutFileFromCommit(String commitId, String filename) {
        Commit targetCommit = findCommit(commitId);

        if (targetCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        if (!targetCommit.getFileMap().containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        String blobSha = targetCommit.getFileMap().get(filename);
        byte[] content = Utils.readContents(Utils.join(BLOBS_DIR, blobSha));
        Utils.writeContents(Utils.join(CWD, filename), content);
    }

    private static boolean branchExists(String branch) {
        return Utils.join(BRANCH_DIR, branch).exists();
    }

    public static void branch(String branch) {
        if (branchExists(branch)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }

        String currentSha = Utils.readContentsAsString(Utils.join(BRANCH_DIR, Utils.readContentsAsString(HEAD_FILE)));
        Utils.writeContents(Utils.join(BRANCH_DIR, branch), currentSha);
    }

    public static void rm_branch(String branch) {
        if (!branchExists(branch)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (Utils.readContentsAsString(HEAD_FILE).equals(branch)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }

        Utils.join(BRANCH_DIR, branch).delete();
    }

    public static void reset(String commitId) {
        Commit targetCommit = findCommit(commitId);
        if (targetCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        checkUntrackedFiles(targetCommit);
        Commit currentCommit = getCurrentCommit();
        List<String> cwdFile = Utils.plainFilenamesIn(CWD);

        for (String filename : cwdFile) {
            if (currentCommit.getFileMap().containsKey(filename)) {
                Utils.restrictedDelete(filename);
            }
        }
        writeContentToCWD(targetCommit);
        String branchName = Utils.readContentsAsString(HEAD_FILE);
        byte[] resetSerialized = Utils.serialize(targetCommit);
        String fullSha = Utils.sha1(resetSerialized);
        Utils.writeContents(Utils.join(BRANCH_DIR, branchName), fullSha);
        clearStagingArea();
    }

    public static void merge(String branch) {
        StagingArea currentStaging = getCurrentStaging();
        if (!currentStaging.getAddition().isEmpty() || !currentStaging.getRemoval().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }

        if (!branchExists(branch)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        String currentBranch = Utils.readContentsAsString(HEAD_FILE);
        if (currentBranch.equals(branch)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        String givenBranchSha = Utils.readContentsAsString(Utils.join(BRANCH_DIR, branch));
        Commit mergedCommit = Utils.readObject(Utils.join(COMMIT_DIR, givenBranchSha), Commit.class);
        checkUntrackedFiles(mergedCommit);

        String splitSha = FindSplitPoint(branch);
        Commit splitCommit = Utils.readObject(Utils.join(COMMIT_DIR, splitSha), Commit.class);
        boolean hasConflict = false;

        if (givenBranchSha.equals(splitSha)) {
            System.out.println("Given branch is an ancestor.");
            System.exit(0);
        }
        else if (splitSha.equals(getCurrentSha())) {
            checkoutBranch(branch);
            System.out.println("Current branch fast-forwarded.");
        }
        else {
            Commit currentCommit = getCurrentCommit();
            Set<String> allFiles = new TreeSet<>();
            allFiles.addAll(splitCommit.getFileMap().keySet());
            allFiles.addAll(currentCommit.getFileMap().keySet());
            allFiles.addAll(mergedCommit.getFileMap().keySet());

            for (String filename : allFiles) {
                String currentBlob = currentCommit.getFileMap().get(filename);
                String givenBlob = mergedCommit.getFileMap().get(filename);
                String splitBlob = splitCommit.getFileMap().get(filename);
                boolean currentModified = (splitBlob == null) ? (currentBlob != null) : !splitBlob.equals(currentBlob);
                boolean givenModified = (splitBlob == null) ? (givenBlob != null) : !splitBlob.equals(givenBlob);
                boolean bothExist = currentBlob != null && givenBlob != null;
                boolean sameContent = bothExist && currentBlob.equals(givenBlob);
                boolean bothRemoved = currentBlob == null && givenBlob == null;

                if (!currentModified && givenModified && givenBlob != null) {
                    byte[] content = Utils.readContents(Utils.join(BLOBS_DIR, givenBlob));
                    Utils.writeContents(Utils.join(CWD, filename), content);
                    currentStaging.getAddition().put(filename, givenBlob);
                }
                else if (currentModified && !givenModified) {
                    continue;
                }
                else if (sameContent || bothRemoved) {
                    continue;
                }
                else if (!currentModified && givenModified && givenBlob == null) {
                    Utils.restrictedDelete(filename);
                    currentStaging.getRemoval().put(filename, splitBlob);
                }
                else if (currentBlob == null && !givenModified) {
                    continue;
                }
                else {
                    String currentContent = (currentBlob != null)
                            ? Utils.readContentsAsString(Utils.join(BLOBS_DIR, currentBlob)) : "";
                    String givenContent = (givenBlob != null)
                            ? Utils.readContentsAsString(Utils.join(BLOBS_DIR, givenBlob)) : "";
                    String conflict = "<<<<<<< HEAD\n" + currentContent + "=======\n" + givenContent + ">>>>>>>\n";
                    Utils.writeContents(Utils.join(CWD, filename), conflict);
                    String conflictSha = Utils.sha1(conflict);
                    Utils.writeContents(Utils.join(BLOBS_DIR, conflictSha), conflict);
                    currentStaging.getAddition().put(filename, Utils.sha1(conflict));
                    hasConflict = true;
                }
            }

            TreeMap<String, String> newFileMap = new TreeMap<>(currentCommit.getFileMap());
            for (String filename : currentStaging.getAddition().keySet()) {
                newFileMap.put(filename, currentStaging.getAddition().get(filename));
            }
            for (String filename : currentStaging.getRemoval().keySet()) {
                newFileMap.remove(filename);
            }

            String mergeMessage = "Merged " + branch + " into " + Utils.readContentsAsString(HEAD_FILE) + ".";
            Commit mergeCommit = new Commit(mergeMessage, getCurrentSha(), givenBranchSha, newFileMap);
            byte[] serialized = Utils.serialize(mergeCommit);
            String mergeSha = Utils.sha1(serialized);
            Utils.writeContents(Utils.join(COMMIT_DIR, mergeSha), serialized);

            String branchName = Utils.readContentsAsString(HEAD_FILE);
            Utils.writeContents(Utils.join(BRANCH_DIR, branchName), mergeSha);
            clearStagingArea();

            if (hasConflict) {
                System.out.println("Encountered a merge conflict.");
            }
        }

    }

    private static String FindSplitPoint(String branch) {
        Set<String> currentAncestors = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(getCurrentSha());
        while (!queue.isEmpty()) {
            String sha = queue.remove();
            if (sha == null || currentAncestors.contains(sha)) continue;
            currentAncestors.add(sha);
            Commit c = Utils.readObject(Utils.join(COMMIT_DIR, sha), Commit.class);
            queue.add(c.getParent());
            if (c.getSecondParent() != null) {
                queue.add(c.getSecondParent());
            }
        }

        queue.clear();
        queue.add(Utils.readContentsAsString(Utils.join(BRANCH_DIR, branch)));
        while (!queue.isEmpty()) {
            String sha = queue.remove();
            if (sha == null) continue;
            if (currentAncestors.contains(sha)) return sha;
            Commit c = Utils.readObject(Utils.join(COMMIT_DIR, sha), Commit.class);
            queue.add(c.getParent());
            if (c.getSecondParent() != null) {
                queue.add(c.getSecondParent());
            }
        }
        return null;
    }
}
