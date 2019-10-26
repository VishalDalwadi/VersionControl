package VCS;

import VCS.Exceptions.DiffException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Diff {
    public static void diff(File fileVersion1, File fileVersion2, File commitDir) throws DiffException {
        if (fileVersion1.length() != 0 && fileVersion2.length() != 0 && FileFunctions.areEqual(fileVersion1, fileVersion2)) {
            throw new DiffException("Both files have same content");
        }

        File file = new File(commitDir, fileVersion2.getName() + ".diff");

        try {
            String[] file1Lines = FileFunctions.getFileLines(fileVersion1);
            String[] file2Lines = FileFunctions.getFileLines(fileVersion2);

            int start = 0;
            int end1 = file1Lines.length - 1;
            int end2 = file2Lines.length - 1;

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            String writeString = "";
            byte[] buffer;

            //Removing common lines from beginning
            while (start <= end1 && start <= end2 && file1Lines[start].equals(file2Lines[start])) {
                start++;
            }

            if (start == file1Lines.length) {       //The only change made to the file is adding lines at the end
                writeString = "+ " + file1Lines.length + " : " + start + " , " + end2;

                for (int i = start; i <= end2; i++) {
                    writeString = writeString.concat("\n" + file2Lines[i]);
                }

                buffer = writeString.getBytes();
                bufferedOutputStream.write(buffer);
                bufferedOutputStream.flush();
            } else if (start == file2Lines.length) {    //The only change made to the file is removing lines from the end
                writeString = "- " + start + " , " + end1 + " : " + file2Lines.length;

                for (int i = start; i <= end1; i++) {
                    writeString = writeString.concat("\n" + file1Lines[i]);
                }

                buffer = writeString.getBytes();
                bufferedOutputStream.write(buffer);
                bufferedOutputStream.flush();
            } else {
                //Removing common lines from the end
                while (end1 >= start && end2 >= start && file1Lines[end1].equals(file2Lines[end2])) {
                    end1--;
                    end2--;
                }
                if (end1 < start) {     //Only new lines we added to the original file in between. No change in existing lines
                    writeString = "+ " + end1 + " : " + start + " , " + end2;

                    for (int i = start; i <= end2; i++) {
                        writeString = writeString.concat("\n" + file2Lines[i]);
                    }

                    buffer = writeString.getBytes();
                    bufferedOutputStream.write(buffer);
                    bufferedOutputStream.flush();
                } else if (end2 < start) {      //Only lines removed from between in the original file. No change in other lines
                    writeString = "- " + start + " , " + end1 + " : " + end2;

                    for (int i = start; i <= end1; i++) {
                        writeString = writeString.concat("\n" + file1Lines[i]);
                    }

                    buffer = writeString.getBytes();
                    bufferedOutputStream.write(buffer);
                    bufferedOutputStream.flush();
                } else {    //Lines were edited
                    int i, j, k;
                    for (i = start; i <= end1; i++) {
                        String str = file1Lines[i];     //For each line in file1
                        for (j = start; j <= end2; j++) {   //Search it in file2
                            if (str.equals(file2Lines[j])) {    //If found
                                if (j == start) {   //Check if it is at start
                                    start++;        //If at start, then no new lines were added
                                    break;
                                } else {    //If not at start, then new lines were added
                                    writeString = "+ " + i + " : " + start + " , " + (j - 1);

                                    for (k = start; k < j; k++) {
                                        writeString = writeString.concat("\n" + file2Lines[k]);
                                    }

                                    writeString = writeString.concat("\n");

                                    buffer = writeString.getBytes();
                                    bufferedOutputStream.write(buffer);
                                    bufferedOutputStream.flush();

                                    start = j + 1;
                                    break;
                                }
                            }
                        }
                        if (j == end2 + 1) {   //If we reached end of file2, but line not found then it was removed
                            writeString = "- " + i + " , " + i + " : " + start + "\n" + str + "\n";

                            buffer = writeString.getBytes();
                            bufferedOutputStream.write(buffer);
                            bufferedOutputStream.flush();
                        }
                    }
                    if (start <= end2) {        //If we reached the end of file1 but not of file2, then remaining lines were added
                        writeString = "+ " + (end1 + 1) + " : " + start + " , " + end2;
                        for (int l = start; l <= end2; l++) {
                            writeString = writeString.concat("\n" + file2Lines[l]);
                        }

                        writeString = writeString.concat("\n");

                        buffer = writeString.getBytes();
                        bufferedOutputStream.write(buffer);
                        bufferedOutputStream.flush();
                    } else if (start == end2 && start <= end1) {
                        //If we reached the end of file2 but not of file1, then remaining lines were removed

                        writeString = "- " + end1 + " , " + (end2 + 1) + " : " + start;
                        for (int l = start; l <= end1; l++) {
                            writeString = writeString.concat("\n" + file1Lines[l]);
                        }

                        writeString = writeString.concat("\n");

                        buffer = writeString.getBytes();
                        bufferedOutputStream.write(buffer);
                        bufferedOutputStream.flush();
                    }
                }
            }

            bufferedOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
