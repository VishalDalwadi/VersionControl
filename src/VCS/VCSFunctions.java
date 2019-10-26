package VCS;

import VCS.DataStructures.Commit;
import VCS.DataStructures.LinkedList;
import VCS.Exceptions.DiffException;
import VCS.Exceptions.FileException;
import java.io.*;

public class VCSFunctions {
    private static final File repo = new File("");
    private static final File vcsDir = new File( ".vcs");
    private static final File commitDir = new File(vcsDir, "commits");
    private static final File metaDir = new File(vcsDir, "meta");
    private static final File trackFile = new File(metaDir, "track");
    private static final File tempDir = new File(vcsDir, "temp");
    private static LinkedList<Commit> commitList = new LinkedList<>();
    private static final File head = new File(metaDir, "HEAD");

    static void printHelp() {
        System.out.println("Initialize tracking of files:                    init");
        System.out.println("Add files to track:                              track file(s)");
        System.out.println("List files to which changes have been made:      status");
        System.out.println("Commit changes made to files:                    commit file(s)");
        System.out.println("Checkout a committed version of a file:          checkout commit_no");
        System.out.println("Print commit logs of a file:                     logs file");
        System.out.println("Print list of all commits/versions:              logs .");
        System.out.println("To exit:                                         exit");
        System.out.println("To see this help message:                        help");
        System.out.println("\nUse . in options requiring file(s) to include all file");
        System.out.println("Filenames or paths should not contain \"..\" in them. Although, use of \".\" is allowed.");
        System.out.println("commit_no is the same as version_no.");
    }

    static void init() {
        if (vcsDir.exists()) {
            System.out.println("VCS has already been initialized.");
        }
        else {
            vcsDir.mkdir();
            commitDir.mkdir();
            metaDir.mkdir();
            try {
                trackFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(".vcs directory created. Use \"track file(s)\" to start tracking changes to them.");
            System.out.println("See \"help\" for additional information.");
        }
    }

    static void track(String[] filenames) throws FileException, IOException {
        if (!vcsDir.exists()) {
            System.out.println(".vcs directory does not exist.");
            System.out.println("If this is a new repository, use \"init\" to initialize VCS");
            System.out.println("Use \"help\" for more information");
            return;
        }

        if (!trackFile.exists()) {
            trackFile.createNewFile();
        }

        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(trackFile, "rw");
            randomAccessFile.seek(randomAccessFile.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String filename : filenames) {
            if (filename.startsWith("./")) {
                filename = filename.substring(2);
            }
            if (filename.contains("..")) {
                throw new FileException("Filenames cannot contain \"..\"");
            }
            File file = new File(filename);
            if (file.exists()) {
                if (file.isDirectory()) {
                    throw new FileException(filename + " is a directory. Please specify a file to track");
                }
                else {
                    randomAccessFile.write((file.getAbsolutePath() + "\n").getBytes());
                }
            } else {
                throw new FileNotFoundException();
            }
        }
        randomAccessFile.close();
    }

    static boolean contains(File file, String[] filenames) {
        for (String filename : filenames) {
            if (file.getAbsolutePath().equals(filename)) {
                return true;
            }
        }
        return false;
    }

    static void commit(String[] filenames, String message) {
        if (!vcsDir.exists()) {
            System.out.println("\n.vcs directory does not exist.");
            System.out.println("If this is a new repository, use \"init\" to initialize VCS");
            System.out.println("Use \"help\" for more information");
            return;
        }

        for (String filename : filenames) {
            if (!contains(new File(filename), VCS.FileFunctions.getFileLines(trackFile))) {
                System.out.println("\n" + filename + " is not being tracked");
                System.out.println("Use \"track\" to start tracking this file");
                System.out.println("Use \"help\" for more information");
                return;
            }
        }

        if (!tempDir.exists()) {
            tempDir.mkdir();
        }

        int commitNo = commitDir.list().length + 1;
        File commitDirectory = new File(commitDir, "commit" + commitNo);
        commitDirectory.mkdir();

        if (commitList.isEmpty()) {
            createCommitList(commitNo - 1);
        }

        if (!commitList.isEmpty()) {
            for (String filename : filenames) {
                LinkedList<Commit>.Iterator iterator = commitList.iterator();
                String[] tmp = filename.split("/");
                Commit commit;

                while (iterator.hasNext()) {
                    commit = iterator.next();
                    File file = new File(tempDir, tmp[tmp.length - 1]);
                    File diffFile = commit.getDiffFile(tmp[tmp.length - 1]);

                    if (diffFile == null) {
                        continue;
                    }
                    else {
                        Regenerate.regenerate(file, diffFile, tempDir);
                    }
                }

                try {
                    Diff.diff(new File(tempDir, tmp[tmp.length - 1]), new File(repo, filename), commitDirectory);
                } catch (DiffException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            for (String filename : filenames) {
                String tmp[] = filename.split("/");
                File tempFile = new File(tempDir, tmp[tmp.length - 1] + "1");
                try {
                    Diff.diff(tempFile, new File(repo, filename), commitDirectory);
                } catch (DiffException e) {
                    System.out.println("\n" + e.getMessage());
                }
            }
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(commitDirectory, "message"));
            fileOutputStream.write(message.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();

            fileOutputStream = new FileOutputStream(head);
            fileOutputStream.write(String.valueOf(commitNo).getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            commitList.add(new Commit(commitDirectory));
        } catch (IOException e) {
            commitDirectory.delete();
            e.printStackTrace();
        }
        FileFunctions.deleteDirectory(tempDir);
    }

    static void createCommitList(int commitNo) {
        for (int i = 1; i <= commitNo; i++) {
            try {
                commitList.add(new Commit(new File(commitDir, "commit" + i)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void checkout(int commitNo) {
        if (!vcsDir.exists()) {
            System.out.println("\n.vcs directory does not exist.");
            System.out.println("If this is a new repository, use \"init\" to initialize VCS");
            System.out.println("Use \"help\" for more information");
            return;
        }

        int lastCommitNo = commitDir.list().length;

        if (commitNo > lastCommitNo) {
            System.out.println("Invalid version_no");
            return;
        }

        if (lastCommitNo == 0) {
            System.out.println("\nNo commits have been made so far.");
            System.out.println("Use \"commit\" to commit a version of a file / project");
            System.out.println("Use \"help\" for more information");
            return;
        }

        try {
            if (commitList.isEmpty()) {
                createCommitList(lastCommitNo);
            }

            for (String filename : FileFunctions.getFileLines(trackFile)) {
                if (!tempDir.exists()) {
                    tempDir.mkdir();
                }

                LinkedList<Commit>.Iterator iterator = commitList.iterator();
                String[] tmp = filename.split("/");
                Commit commit;
                File file = new File(tempDir, tmp[tmp.length - 1]);

                while (iterator.hasNext()) {
                    commit = iterator.next();
                    File diffFile = commit.getDiffFile(tmp[tmp.length - 1]);

                    if (commit.getCommitNo() <= commitNo) {
                        if (diffFile == null) {
                            continue;
                        } else {
                            if (commit.getCommitNo() == commitNo) {
                                Regenerate.regenerate(file, diffFile, repo);
                            }
                            else {
                                Regenerate.regenerate(file, diffFile, tempDir);
                            }
                        }
                    }
                    else {
                        break;
                    }
                }
                FileFunctions.deleteDirectory(tempDir);
            }

            FileOutputStream fileOutputStream = new FileOutputStream(head);
            fileOutputStream.write(String.valueOf(commitNo).getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void logs(String filename) {
        if (!vcsDir.exists()) {
            System.out.println("\n.vcs directory does not exist.");
            System.out.println("If this is a new repository, use \"init\" to initialize VCS");
            System.out.println("Use \"help\" for more information");
            return;
        }

        int commitNo = commitDir.list().length;

        if (commitNo == 0) {
            System.out.println("\nNo commits have been made so far.");
            System.out.println("Use \"commit\" to commit");
            System.out.println("See \"help\" for more information");
        }

        if (commitList.isEmpty()) {
            createCommitList(commitNo);
        }

        LinkedList<Commit>.Iterator iterator = commitList.iterator();
        Commit commit;

        if (filename.equals("")) {
            while (iterator.hasNext()) {
                commit = iterator.next();
                System.out.println(commit);
            }
        }
        else if (contains(new File(filename), FileFunctions.getFileLines(trackFile))){
            while (iterator.hasNext()) {
                commit = iterator.next();
                String[] tmp = filename.split("/");

                if (commit.getDiffFile(tmp[tmp.length - 1]) != null) {
                    System.out.println(commit);
                }
            }
        }
    }
}
