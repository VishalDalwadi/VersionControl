package VCS;

import VCS.DataStructures.LinkedList;
import VCS.Exceptions.UnderflowException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Regenerate {
    public static void regenerate(File originalFile, File diffFile, File directory) {
        Scanner scanner;

        String[] diffFileLines = FileFunctions.getFileLines(diffFile);          //Lines in Diff File
        String[] originalFileLines = FileFunctions.getFileLines(originalFile);  //Lines in Original Dile

        LinkedList<String> updatedFileLines = new LinkedList<>(originalFileLines);
        int index1, index2, index3 = 0;
        char tempChar;

        try {
            for (int i = 0; i < diffFileLines.length - 1; i++) {
                scanner = new Scanner(diffFileLines[i]);
                tempChar = scanner.next().charAt(0);
                index1 = scanner.nextInt();
                scanner.next();
                index2 = scanner.nextInt();
                scanner.next();
                index3 = scanner.nextInt();

                if (tempChar == '-') {      //Lines that follow were removed from original file
                    for (int j = index1; j <= index2 ; j++) {
                        i++;
                        updatedFileLines.remove(diffFileLines[i]);
                    }
                }
                else if (tempChar == '+') {     //Lines that follow were added to the original file
                    for (int j = index2; j <= index3; j++) {
                        i++;
                        updatedFileLines.add(diffFileLines[i], j);
                    }
                }
            }
        }
        catch (UnderflowException e) {
            e.printStackTrace();
        }

        try {
            if (!directory.getName().endsWith("/temp")) {
                String[] tmp = originalFile.getName().split("/");
                originalFile = new File(directory, tmp[tmp.length - 1]);
            }

            FileOutputStream fileOutputStream = new FileOutputStream(originalFile);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            LinkedList<String>.Iterator iterator = updatedFileLines.iterator();
            String line;

            while (iterator.hasNext()) {
                line = iterator.next();
                if (line.equals("")) {
                    if (!iterator.hasNext()) {
                        break;
                    }
                    bufferedOutputStream.write("\n".getBytes());
                }
                else {
                    bufferedOutputStream.write((line + "\n").getBytes());
                }
                bufferedOutputStream.flush();
            }

            bufferedOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
