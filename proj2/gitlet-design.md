# Gitlet Design Document

**Name**:

## Classes and Data Structures

### Class 1

#### Commit 
Represents a repository snapshot at a specific time.

1. message : String
2. timestamp : Date
3. parent : String
4. secondParent : String
5. fileMap : TreeMap<String, String>


### Class 2

#### Repository
Manages all version-control operations such as init, add, commit,
checkout and branch.

1. GITLET_DIR : File
2. BLOBS_DIR : File
3. COMMIT_DIR : File
4. BRANCH_DIR : File
5. HEAD_FILE : File



### Class 3


##### StagingArea
Stores the files which are about to add or remove.

1. addition : TreeMap<String, String>
2. removal : TreeMap<String, String>

## Algorithms

### init
1. If `.gitlet` already exists, print error and exit.
2. Create directories: `.gitlet/`, `commits/`, `blobs/`, `branches/`, `staging/`.
3. Create initial Commit: message="initial commit", timestamp=Date(0), parent=null, secondParent=null, fileMap=empty TreeMap.
4. Serialize the initial Commit, compute its SHA-1, save to `commits/<SHA-1>`.
5. Create `branches/master` file with content = initial commit's SHA-1.
6. Create `HEAD` file with content = "master".
7. Create empty StagingArea, serialize to `staging/staging_area`.

### add [filename]
1. If file does not exist in CWD, print error and exit.
2. Read file contents, compute blob SHA-1.
3. Deserialize current commit from `branches/<HEAD>`.
4. If file content is identical to current commit's fileMap entry, remove it from staging addition (if present) and return.
5. Save blob to `blobs/<SHA-1>` (overwrite if exists).
6. Deserialize StagingArea from `staging/staging_area`.
7. Add entry: addition.put(filename, blob SHA-1). Remove from removal if present.
8. Serialize StagingArea back to `staging/staging_area`.

### commit [message]
1. If message is blank, print error and exit.
2. Deserialize StagingArea. If both addition and removal are empty, print error and exit.
3. Deserialize current commit.
4. Copy current commit's fileMap to a new TreeMap.
5. For each entry in addition: put into new fileMap (overwrite or add).
6. For each entry in removal: remove from new fileMap.
7. Create new Commit: message, Date(), parent=current commit SHA-1, secondParent=null, fileMap=new fileMap.
8. Serialize new Commit, compute SHA-1, save to `commits/<SHA-1>`.
9. Read HEAD to get current branch name, update `branches/<branch>` with new commit SHA-1.
10. Clear StagingArea (both maps), serialize back.

### rm [filename]
1. Deserialize StagingArea and current commit.
2. If file is neither in staging addition nor in current commit's fileMap, print error and exit.
3. If file is in staging addition, remove it from addition.
4. If file is in current commit's fileMap, add to removal, and delete file from CWD.
5. Serialize StagingArea back.

### log
1. Read HEAD to get current branch name.
2. Read `branches/<branch>` to get current commit SHA-1.
3. Deserialize current commit.
4. Loop: print commit info (SHA-1, date, message). If merge commit, print Merge line. Move to parent. Stop when parent is null.

### global-log
1. List all files in `commits/` directory.
2. For each file, deserialize commit and print info. Order does not matter.

### find [message]
1. List all files in `commits/` directory.
2. For each file, deserialize commit. If message matches, print SHA-1.
3. If none found, print error.

### status
1. Read HEAD to get current branch name.
2. List all files in `branches/`, sort lexicographically, print with `*` on current branch.
3. Deserialize StagingArea.
4. Print addition keys (sorted) under "Staged Files".
5. Print removal keys (sorted) under "Removed Files".

### checkout
**checkout -- [filename]:**
1. Deserialize current commit.
2. If filename not in fileMap, print error and exit.
3. Read blob from `blobs/<fileMap.get(filename)>`, write to CWD.

**checkout [commit id] -- [filename]:**
1. Find commit: if id < 40 chars, search `commits/` for prefix match.
2. If not found, print error and exit.
3. If filename not in that commit's fileMap, print error and exit.
4. Read blob, write to CWD.

**checkout [branch name]:**
1. If branch does not exist in `branches/`, print error and exit.
2. If branch == current branch, print error and exit.
3. Deserialize target commit.
4. Check for untracked files in CWD that would be overwritten. If any, print error and exit.
5. Delete all files tracked by current commit.
6. Write all files from target commit's fileMap to CWD.
7. Update HEAD to new branch name.
8. Clear StagingArea.

### branch [branch name]
1. If branch already exists in `branches/`, print error and exit.
2. Read current commit SHA-1 from `branches/<HEAD>`.
3. Create new file `branches/<branch name>` with content = current commit SHA-1.

### rm-branch [branch name]
1. If branch does not exist in `branches/`, print error and exit.
2. If branch == current branch (HEAD), print error and exit.
3. Delete `branches/<branch name>`.

### reset [commit id]
1. Find commit by id (support abbreviated SHA-1).
2. If not found, print error and exit.
3. Check for untracked files in CWD that would be overwritten. If any, print error and exit.
4. Delete all files tracked by current commit.
5. Write all files from target commit's fileMap to CWD.
6. Update `branches/<HEAD>` to target commit SHA-1.
7. Clear StagingArea.

### merge [branch name]
1. If staging area is not empty, print error and exit.
2. If branch does not exist, print error and exit.
3. If branch == current branch, print error and exit.
4. Check for untracked files in CWD that would be overwritten. If any, print error and exit.
5. Find split point: collect all ancestors of current branch, then walk back from given branch until find first match.
6. If split point == given branch head, print "Given branch is an ancestor" and exit.
7. If split point == current branch head, fast-forward: checkout given branch, print message.
8. Otherwise, compare files across split point, current commit, and given commit (see table below).
9. Create merge commit with two parents (current + given).
10. If there were conflicts, print "Encountered a merge conflict."

#### Merge file comparison rules

| # | split | current | given        | action       |
|---|-------|---------|--------------|--------------|
| 1 | 有    | 没改    | 改了         | 用 given 的  |
| 2 | 有    | 改了    | 没改         | 保持 current |
| 3 | 有    | 改了    | 改了（一样） | 不动         |
| 4 | 没有  | 有      | 没有         | 保持 current |
| 5 | 没有  | 没有    | 有           | 用 given 的  |
| 6 | 有    | 没改    | 删了         | 删掉         |
| 7 | 有    | 删了    | 没改         | 保持删除     |
| 8 | -     | 不一样  | 不一样       | 冲突         |

## Persistence

.gitlet/         → GITLET_DIR
├── commits/     → COMMITS_DIR    （存序列化的 commit 对象, 文件名是完整 40 位 SHA-1）
├── blobs/       → BLOBS_DIR      （存文件内容快照, 文件名是完整 40 位 SHA-1）
├── branches/
│   ├── master          ← 文件内容是 master 分支指向的 commit SHA-1
│   └── other-branch    ← 文件内容是 other-branch 指向的 commit SHA-1
└── HEAD                ← 文件内容是当前分支名，比如 "master"
├── staging/     → STAGING_DIR    （暂存区, 序列化的 StagingArea 对象）


