import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        CommandLine line = new CommandLine(scan);
        while(line.isRunning()) {
            line.runCommand(scan.nextLine());
        }
    }
}