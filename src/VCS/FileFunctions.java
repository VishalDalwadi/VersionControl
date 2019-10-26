package VCS;

import java.io.*;

public class FileFunctions {
    //If file not empty, then return lines of the file as an array of Strings
    public static String[] getFileLines(File file) {
        String fileContent = getFileContent(file);
        if (fileContent.equals("")) {
            return new String[0];
        }
        else {
            return fileContent.split("\n", -1);
        }
    }

    //Returns the whole file read into a String. (For small files)
    public static String getFileContent(File file) {
        String readString = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            byte[] buffer = new byte[1024];
            int bytesRead;

            while (bufferedInputStream.available() > 0) {
                bytesRead = bufferedInputStream.read(buffer);
                readString = readString.concat(new String(buffer, 0, bytesRead));
            }

            bufferedInputStream.close();
        } catch (IOException e) {}
        return readString;
    }

    //If contents of file1 and file2 are equal, then returns true, otherwise, false
    public static boolean areEqual(File file1, File file2) {
        String[] file1Lines = getFileLines(file1);
        String[] file2Lines = getFileLines(file2);

        if (file1Lines.length != file2Lines.length) {
            return false;
        }
        else {
            for (int i = 0; i < file1Lines.length; i++) {
                if (!file1Lines[i].equals(file2Lines[i])) {
                    return false;
                }
            }
        }

        return true;
    }

    //Recursively deletes a directory. This is implemented since delete() only works on empty directories.
    public static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file: files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                }
                else {
                    file.delete();
                }
            }
        }

        directory.delete();
    }
}
