package VCS;

import VCS.Exceptions.FileException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter \"help\" for help and \"exit\" to exit.\n");
        String command;

        while (true) {
            System.out.print(">>> ");
            command = scanner.next();

            if (command.equals("help")) {
                VCSFunctions.printHelp();
            } else if (command.equals("exit")) {
                break;
            } else if (command.equals("init")) {
                VCSFunctions.init();
            } else if (command.equals("track")) {
                String filenames = scanner.nextLine();
                try {
                    VCS.VCSFunctions.track(filenames.trim().split("[\\s]+"));
                } catch (FileException | IOException e) {
                    System.out.println(e.getMessage());
                }
            } else if (command.equals("commit")) {
                String filenames = scanner.nextLine();
                System.out.print("Commit Message : ");
                String message = scanner.nextLine();
                VCSFunctions.commit(filenames.trim().split("[\\s]+"), message);
            } else if (command.equals("logs")) {
                String filename = scanner.next();
                VCSFunctions.logs(filename);
            } else if (command.equals("checkout")) {
                int commitNo = scanner.nextInt();
                VCSFunctions.checkout(commitNo);
            } else{
                System.out.println("Invalid command : " + command);
                System.out.println("Here's a list of valid commands\n");
                VCSFunctions.printHelp();
            }

            System.out.println();
        }
    }
}
