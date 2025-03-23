import java.sql.SQLOutput;
import java.util.Scanner;

public class CommandLine {
    private TrashCanPool pool;
    private boolean isRunning;
    private Scanner scan;

    public CommandLine(Scanner scan) {
        this.scan = scan;
        this.pool = new TrashCanPool();
        this.isRunning = true;
        this.printMenu();
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void runCommand(String nextLine) {
        switch (nextLine) {
            case "1":
                pool.printCans();
                break;
            case "2":
                //add new can
                this.addNewTrashCan();
                break;
            case "3":
                this.pool.simulation();
                break;
            case "4":
                this.pool.removeTrashCan(scan.nextInt());
                scan.nextLine();
                break;
            case "5":
                this.isRunning = false;
                break;
            case "6":
                this.pool.clearTrashCans();
                break;
            default:
                System.out.println("Wrong command");
                break;
        }
        this.printMenu();
    }

    private void printMenu() {
        System.out.println("------------------------------------------------");
        System.out.println("Trashcan simulator - pick an operation by number");
        System.out.println("1 - list cans");
        System.out.println("2 - add new can");
        System.out.println("3 - run simulation");
        System.out.println("4 - remove can");
        System.out.println("5 - end program");
        System.out.println("6 - clear trash cans");
    }

    private void addNewTrashCan() {
        try {
            System.out.println("Enter description: ");
            String description = scan.nextLine();
            System.out.println("Enter max weight: ");
            int maxWeight = Integer.valueOf(scan.nextLine());
            System.out.println("Enter max volume: ");
            int maxVolume = Integer.valueOf(scan.nextLine());
            pool.addNewCan(0, maxVolume, 0, maxWeight, 0, 0, description);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid data");
            return;
        }
    }
}
