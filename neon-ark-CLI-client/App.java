import java.util.Scanner;
import java.util.List;

public class App {

    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);
        List<Warden> wardens = WardenService.readWardensFromFile("wardens.csv");

        runMainMenu(wardens, scanner);
    }

    public static void runMainMenu(List<Warden> wardens, Scanner scanner){

        System.out.println("=========================================================");
        System.out.println("        NEON ARK — ADMIN WARDEN ONBOARDING CONSOLE");
        System.out.println("=========================================================");
        System.out.println("\n[ MAIN MENU ]");
        System.out.println("1. Add New Warden");
        System.out.println("2. View Wardens");
        System.out.println("3. Update Warden");
        System.out.println("4. Manage Certifications");
        System.out.println("5. Deactivate / Terminate Warden");
        System.out.println("6. Exit");

        boolean running = true;

        while(running){

        }
    }

}
