package VCS.DataStructures;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

public class Commit {
    private int commitNo;                   //Commit No of the commit represented by this object
    private String message = "";            //Message given when this commit was made
    private File[] diffFiles;               //All diff files that were generated when the commit was made

    public Commit(File commitDir) throws IOException {
        commitNo = Integer.parseInt(commitDir.getName().substring(6));

        FileInputStream fileInputStream = new FileInputStream(commitDir.getAbsolutePath() + "/message");
        int charsRead;
        byte[] buffer = new byte[1024];

        while (fileInputStream.available() > 0) {
            charsRead = fileInputStream.read(buffer);
            message = message.concat(new String(buffer, 0, charsRead));
        }

        FilenameFilter filenameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".diff");
            }
        };
        diffFiles = commitDir.listFiles(filenameFilter);

        fileInputStream.close();
    }

    //Returns the diff file for the file having name filename if it was committed during this commit.
    //Otherwise, returns null

    public File getDiffFile(String filename) {
        for (File file : diffFiles) {
            if (file.getName().equals(filename + ".diff")) {
                return file;
            }
        }
        return null;
    }

    public int getCommitNo() {
        return commitNo;
    }

    public String toString() {
        return "Commit " + commitNo + " : " + message;
    }
}
