import org.w3c.dom.ls.LSOutput;

import java.util.Scanner;
import java.util.List;

public class App {

    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);
        List<Warden> wardens = WardenService.readWardensFromFile("wardens.csv");

        runMainMenu(wardens, scanner);
    }

    public static void runMainMenu(List<Warden> wardens, Scanner scanner){

        boolean running = true;

        while(running){
            printMainMenu();

            String choice = scanner.nextLine();

            switch(choice){
                case "1":
                    addWarden(wardens, scanner);
                    break;
                case "2":
                    WardenService.displayWardens(wardens);
                    break;
                case "3":
                    simulateUpdate();
                    break;
                case "4":
                    simulateCertificationManagement();
                    break;
                case "5":
                    simulateDeactivate();
                    break;
                case "6":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    public static void printMainMenu(){
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
    }

    public static void addWarden(List<Warden> wardens, Scanner scanner){
        System.out.println("        ADD WARDEN");
        System.out.println("=============================");

        String firstName;
        while(true){
            System.out.println("Enter first name:");
            firstName = scanner.nextLine();

            if(!WardenService.checkNotBlank(firstName)){
                System.out.println("First name cannot be blank.");
            } else break;
        }

        System.out.print("Enter last name (optional): ");
        String lastName = scanner.nextLine();

        String identifier;
        while (true) {
            System.out.print("Enter identifier: ");
            identifier = scanner.nextLine();

            if (!WardenService.checkNotBlank(identifier)) {
                System.out.println("Identifier cannot be empty.");
            } else if (WardenService.checkDuplicate(wardens, identifier)) {
                System.out.println("Identifier already exists.");
            } else break;
        }

        System.out.print("Enter email (optional): ");
        String email = scanner.nextLine();

        String startDate;
        while (true) {
            System.out.print("Enter start date (YYYY-MM-DD): ");
            startDate = scanner.nextLine();

            if (!WardenService.checkValidDate(startDate)) {
                System.out.println("Invalid date format.");
            } else break;
        }

        String endDate;
        while (true) {
            System.out.print("Enter end date (optional, YYYY-MM-DD): ");
            endDate = scanner.nextLine();

            if (endDate.isEmpty()) {
                break; // allowed
            }

            if (!WardenService.checkValidDate(endDate)) {
                System.out.println("End date must be in YYYY-MM-DD format.");
            } else {
                break;
            }
        }

        String employmentId;
        while (true) {
            System.out.print("Enter employment ID: ");
            employmentId = scanner.nextLine();

            if (!employmentId.matches("\\d+")) {
                System.out.println("Employment ID must be a number.");
            } else {
                break;
            }
        }

        String roleId;
        while (true) {
            System.out.print("Enter role ID: ");
            roleId = scanner.nextLine();

            if (!roleId.matches("\\d+")) {
                System.out.println("Role ID must be a number.");
            } else {
                break;
            }
        }

        String clearanceId;
        while (true) {
            System.out.print("Enter clearance ID: ");
            clearanceId = scanner.nextLine();

            if (!clearanceId.matches("\\d+")) {
                System.out.println("Clearance ID must be a number.");
            } else {
                break;
            }
        }

        String identifierTypeId;
        while (true) {
            System.out.print("Enter Identifier Type ID: ");
            identifierTypeId = scanner.nextLine();

            if (!identifierTypeId.matches("\\d+")) {
                System.out.println("Identifier Type ID must be a number.");
            } else {
                break;
            }
        }

        System.out.println("\nWOULD SEND:  POST   /api/wardens");
        System.out.println("BRIEF DESCRIPTION: Create a new Warden record and add it to the roster.");
        System.out.println("PAYLOAD (JSON)");
        System.out.println("{");
        System.out.println("  \"firstName\": \"" + firstName + "\",");
        System.out.println("  \"lastName\": \"" + lastName + "\",");
        System.out.println("  \"identifierValue\": \"" + identifier + "\",");
        System.out.println("  \"email\": \"" + email + "\",");
        System.out.println("  \"startingDate\": \"" + startDate + "\",");
        System.out.println("  \"endDate\": \"" + endDate + "\",");
        System.out.println("  \"employmentId\": " + employmentId + ",");
        System.out.println("  \"roleId\": " + roleId + ",");
        System.out.println("  \"clearanceId\": " + clearanceId + ",");
        System.out.println("  \"identifierTypeId\": " + identifierTypeId);
        System.out.println("}");

        System.out.println("RESULT: SUCCESS (simulated)");
    }

    public static void simulateUpdate(){
        System.out.println("        UPDATE WARDEN");
        System.out.println("================================");

        System.out.println("Action: Update an existing warden's record.");

        System.out.println("\nInputs required: Warden ID, Field to update (last name, email, " +
                           "end date, role, clearance, employment status), New status");

        System.out.println("\nValidation check: Warden ID must exist, New values must " +
                           "follow their respective field's validation checks.");

        System.out.println("\nWOULD SEND:  PUT   /api/wardens/{id}");

        System.out.println("Brief Description: Update specific fields of an existing warden's record.");

        System.out.println("PAYLOAD (JSON)");
        System.out.println("{");
        System.out.println("  \"roleId\": <new value>,");
        System.out.println("  \"employmentId\": <new value>");
        System.out.println("}");
        System.out.println("\nRESULT: SUCCESS (simulated)");
        System.out.println("\nReturning to Main Menu...");
    }

    public static void simulateCertificationManagement(){
        System.out.println("        MANAGE CERTIFICATIONS");
        System.out.println("======================================");

        System.out.println("Action: Manage a specific warden's certifications.");

        System.out.println("\nInputs required: Warden ID, Certification action (Add, View, Mark expired, Remove), Date (optional)");

        System.out.println("\nValidation check: Warden ID must exist, Certification must exist, Date must be valid");

        System.out.println("\nWOULD SEND:  POST   /api/wardens/{id}/certifications");

        System.out.println("Brief Description: Create or manage a certification associated with an existing warden.");

        System.out.println("\nPAYLOAD (JSON):");
        System.out.println("{");
        System.out.println("  \"name\": \"Certification Name\",");
        System.out.println("  \"dateEarned\": \"YYYY-MM-DD\",");
        System.out.println("  \"expirationDate\": \"YYYY-MM-DD\"");
        System.out.println("}");
        System.out.println("\nRESULT: SUCCESS (simulated)");
        System.out.println("\nReturning to Main Menu...");
    }

    public static void simulateDeactivate(){
        System.out.println("        DEACTIVATE / TERMINATE WARDEN");
        System.out.println("==========================================");

        System.out.println("Action: Deactivate or terminate a warden.");

        System.out.println("\nInputs required: Warden ID, New employment status, End date (optional)");

        System.out.println("\nValidation check: Warden ID must exist, Employment status must exist, Date must be valid");

        System.out.println("\nWOULD SEND:  PATCH   /api/wardens/{id}/status");

        System.out.println("Brief Description: Update the employment status of a warden or mark them as terminated.");

        System.out.println("\nPAYLOAD (JSON):");
        System.out.println("{");
        System.out.println("  \"employmentId\": <new status>,");
        System.out.println("  \"endDate\": \"YYYY-MM-DD\"");
        System.out.println("}");
        System.out.println("\nRESULT: SUCCESS (simulated)");
        System.out.println("\nReturning to Main Menu...");
    }

}
