package org.example.neonarkintaketracker.cli;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import java.util.List;
import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.neonarkintaketracker.dto.CreatureResponse;
import org.example.neonarkintaketracker.dto.UserResponse;

import java.util.Scanner;
import java.net.*;
import java.io.*;


/**
 * Command-line interface (CLI) for interacting with the backend of the Neon Ark system.
 *
 * Provides a menu-driven interface that maps directly to backend API endpoints.
 * Handles user input, performs HTTP requests, and displays formatted results.
 */
public class NeonArkCli {

    private final Scanner scanner = new Scanner(System.in);
    private final String API_BASE = "http://localhost:8080/api";

    public static void main(String[] args) {
        new NeonArkCli().start();
    }


    /**
     * Starts the CLI application loop.
     *
     * Displays the menu, accepts user input, and routes
     * user choices to the appropriate methods.
     */
    private void start() {

        boolean running = true;

        while(running){

            printMenu();
            System.out.println("Select an option: ");
            String choice = scanner.nextLine();

            switch(choice){
                case "1":
                    listAll();             // GET /api/creatures
                    break;
                case "2":
                    viewById();            // GET /api/creatures/{id}
                    break;
                case "3":
                    register();            // POST /api/creatures
                    break;
                case "4":
                    rename();              // PUT /api/creatures/{id}/name
                    break;
                case "5":
                    remove();              // DELETE /api/creatures/{id} (confirm)
                    break;
                case "6":
                    viewObservations();    // GET /api/creatures/{id}/observations
                    break;
                case "7":
                    findByFeedingTime();   // GET /api/feedings?time={HH:MM}
                    break;
                case "8":
                    viewUsers();           // GET /api/admin/users
                    break;
                case "0":
                    running = !confirmExit();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
            }
        }


    /**
     * Retrieves and displays all active creatures.
     *
     * API Route: GET /api/creatures
     */
    private void listAll() {

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/creatures"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            int status = response.statusCode();
            System.out.println("\nStatus: " + status);

            if (status == 200) {
                List<CreatureResponse> creatures = parseJson(response.body());
                displayCreatureTable(creatures);
            } else {
                System.out.println("Error: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }


    /**
     * Parses a JSON array response into a list of CreatureResponse objects.
     *
     * @param json raw JSON response
     * @return list of creatures
     */
    private List<CreatureResponse> parseJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();


            return Arrays.asList(
                    mapper.readValue(json, CreatureResponse[].class)
            );

        } catch (Exception e) {
            System.out.println("Failed to parse response.");
            e.printStackTrace();
            return List.of();
        }
    }


    /**
     * Displays a formatted table of creatures.
     *
     * @param creatures list of creatures
     */
    private void displayCreatureTable(List<CreatureResponse> creatures) {

        if (creatures.isEmpty()) {
            System.out.println("No creatures found.");
            return;
        }

        String format = "%-5s %-20s %-25s %-15s %-10s %-15s\n";

        System.out.printf(format, "ID", "Name", "Habitat", "Species", "Danger", "Condition");
        System.out.println("-----------------------------------------------------------------------------------------");

        for (CreatureResponse creature : creatures) {
            System.out.printf(format,
                    creature.id(),
                    creature.name(),
                    creature.habitatName(),
                    creature.species(),
                    creature.dangerLevel(),
                    creature.condition()
            );
        }
    }


    /**
     * Retrieves and displays a single creature by ID.
     *
     * API Route: GET /api/creatures/{id}
     */
    private void viewById() {

        Long id = null;

        while (id == null) {
            System.out.print("Enter creature ID: ");
            String input = scanner.nextLine();

            try {
                id = Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric ID.");
            }
        }

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/creatures/" + id))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            int status = response.statusCode();
            System.out.println("\nStatus: " + status);

            if (status == 200) {
                CreatureResponse creature = parseSingle(response.body());
                displaySingleCreature(creature);

            } else if (status == 404) {
                System.out.println("Creature not found.");

            } else {
                System.out.println("Error: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }


    /**
     * Parses a JSON object into a CreatureResponse.
     *
     * @param json raw JSON response
     * @return parsed creature
     */
    private CreatureResponse parseSingle(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, CreatureResponse.class);

        } catch (Exception e) {
            System.out.println("Failed to parse response.");
            return null;
        }
    }


    /**
     * Displays details for a single creature.
     *
     * @param creature creature to display
     */
    private void displaySingleCreature(CreatureResponse creature) {

        if (creature == null) {
            System.out.println("No data to display.");
            return;
        }

        System.out.println("\n===== CREATURE DETAILS =====");
        System.out.println("ID: " + creature.id());
        System.out.println("Name: " + creature.name());
        System.out.println("Habitat: " + creature.habitatName());
        System.out.println("Species: " + creature.species());
        System.out.println("Danger Level: " + creature.dangerLevel());
        System.out.println("Condition: " + creature.condition());
        System.out.println("Notes: " + (creature.notes() != null ? creature.notes() : ""));
        System.out.println("============================\n");
    }


    /**
     * Collects user input and registers a new creature.
     *
     * API Route: POST /api/creatures
     */
    private void register() {

        String name;
        while (true) {
            System.out.print("Enter name: ");
            name = scanner.nextLine();
            if (!name.isBlank()) break;
            System.out.println("Name cannot be blank.");
        }

        String species;
        while (true) {
            System.out.print("Enter species: ");
            species = scanner.nextLine();
            if (!species.isBlank()) break;
            System.out.println("Species cannot be blank.");
        }

        String danger;
        while (true) {
            System.out.print("Enter danger level (LOW, MEDIUM, HIGH): ");
            danger = scanner.nextLine().toUpperCase();

            if (danger.equals("LOW") || danger.equals("MEDIUM") || danger.equals("HIGH")) {
                break;
            }

            System.out.println("Invalid danger level. Must be LOW, MEDIUM, or HIGH.");
        }

        String condition;
        while (true) {
            System.out.print("Enter condition (STABLE, QUARANTINED, CRITICAL): ");
            condition = scanner.nextLine().toUpperCase();

            if (condition.equals("STABLE") ||
                    condition.equals("QUARANTINED") ||
                    condition.equals("CRITICAL")) {
                break;
            }

            System.out.println("Invalid condition. Must be STABLE, QUARANTINED, or CRITICAL.");
        }

        Long habitatId;
        while (true) {
            System.out.print("Enter habitat ID: ");
            String input = scanner.nextLine();

            try {
                habitatId = Long.parseLong(input);


                if (habitatId < 1 || habitatId > 10) {
                    System.out.println("Invalid habitat ID. Must be between 1 and 10.");
                    continue;
                }

                break;

            } catch (NumberFormatException e) {
                System.out.println("Habitat ID must be a number.");
            }
        }


        try {
            HttpClient client = HttpClient.newHttpClient();

            String json = String.format(
                    "{\"name\":\"%s\",\"species\":\"%s\",\"dangerLevel\":\"%s\",\"condition\":\"%s\",\"habitatId\":%d}",
                    name, species, danger, condition, habitatId
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/creatures"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            int status = response.statusCode();
            System.out.println("\nStatus: " + status);

            if (status == 201) {
                CreatureResponse created = parseSingle(response.body());
                System.out.println("Creature created successfully!");
                displaySingleCreature(created);

            } else if (status == 409) {
                System.out.println("Creature already exists.");

            } else if (status == 400) {
                String body = response.body();

                if (body != null && body.toLowerCase().contains("habitat")) {
                    System.out.println("Invalid habitat ID. That habitat does not exist.");
                } else {
                    System.out.println("Invalid input. Check your data.");
                }

            } else {
                System.out.println("Error: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }


    /**
     * Renames an existing creature.
     *
     * API Route: PUT /api/creatures/{id}/name
     */
    private void rename() {

        Long id = null;

        while (id == null) {
            System.out.print("Enter creature ID to rename: ");
            String input = scanner.nextLine();

            try {
                id = Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric ID.");
            }
        }

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest getReq = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/creatures/" + id))
                    .GET()
                    .build();

            HttpResponse<String> getRes = client.send(
                    getReq,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (getRes.statusCode() == 404) {
                System.out.println("Creature not found.");
                return;
            }

            if (getRes.statusCode() != 200) {
                System.out.println("Error: " + getRes.body());
                return;
            }

            CreatureResponse current = parseSingle(getRes.body());
            String oldName = current.name();

            String newName;

            while (true) {
                System.out.print("Enter new name: ");
                newName = scanner.nextLine();

                if (oldName.equalsIgnoreCase(newName)) {
                    System.out.println("New name is the same as current name. No changes needed.");
                    return;
                }

                if (!newName.isBlank()){
                    break;
                }

                System.out.println("Name cannot be blank.");
            }

            System.out.print("Confirm rename '" + oldName + "' -> '" + newName + "'? (y/n): ");
            String confirm = scanner.nextLine();

            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("Rename cancelled.");
                return;
            }

            String json = String.format("{\"name\":\"%s\"}", newName);

            HttpRequest putReq = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/creatures/" + id + "/name"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> putRes = client.send(
                    putReq,
                    HttpResponse.BodyHandlers.ofString()
            );

            int status = putRes.statusCode();
            System.out.println("\nStatus: " + status);

            if (status == 200) {
                CreatureResponse updated = parseSingle(putRes.body());

                System.out.println("Rename successful!");
                System.out.println("Old name: " + oldName);
                System.out.println("New name: " + updated.name());

            } else if (status == 400) {
                System.out.println("Invalid name. Please try again.");

            } else if (status == 404) {
                System.out.println("Creature not found.");

            } else if (status == 409) {
                System.out.println("A creature with that name already exists.");

            } else {
                System.out.println("Error: " + putRes.body());
            }

        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }


    /**
     * Removes a creature after confirmation (soft delete).
     *
     * API Route: DELETE /api/creatures/{id}
     */
    private void remove() {

        Long id = null;

        while (id == null) {
            System.out.print("Enter creature ID to remove: ");
            String input = scanner.nextLine();

            try {
                id = Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric ID.");
            }
        }

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest getReq = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/creatures/" + id))
                    .GET()
                    .build();

            HttpResponse<String> getRes = client.send(
                    getReq,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (getRes.statusCode() == 404) {
                System.out.println("Creature not found.");
                return;
            }

            if (getRes.statusCode() != 200) {
                System.out.println("Error: " + getRes.body());
                return;
            }

            CreatureResponse creature = parseSingle(getRes.body());
            String name = creature.name();

            System.out.print("Confirm removal of '" + name + "'? (y/n): ");
            String confirm = scanner.nextLine();

            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("Removal cancelled.");
                return;
            }

            HttpRequest deleteReq = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/creatures/" + id))
                    .DELETE()
                    .build();

            HttpResponse<String> deleteRes = client.send(
                    deleteReq,
                    HttpResponse.BodyHandlers.ofString()
            );

            int status = deleteRes.statusCode();
            System.out.println("\nStatus: " + status);

            if (status == 200) {
                System.out.println("Creature '" + name + "' removed successfully.");

            } else if (status == 404) {
                System.out.println("Creature not found.");

            } else if (status == 409) {
                System.out.println("Cannot remove creature: active feeding schedule exists.");

            } else {
                System.out.println("Error: " + deleteRes.body());
            }

        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }


    /**
     * Retrieves and displays observations for a creature.
     *
     * API Route: GET /api/creatures/{id}/observations
     */
    private void viewObservations() {

        Long id = null;

        while (id == null) {
            System.out.print("Enter creature ID: ");
            String input = scanner.nextLine();

            try {
                id = Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric ID.");
            }
        }

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/creatures/" + id + "/observations"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            int status = response.statusCode();
            System.out.println("\nStatus: " + status);

            if (status == 404) {
                System.out.println("Creature not found.");
                return;
            }

            if (status != 200) {
                System.out.println("Error: " + response.body());
                return;
            }

            // 🔥 Parse response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());

            System.out.println("\n===== CREATURE DETAILS =====");
            System.out.println("ID: " + root.get("id").asLong());
            System.out.println("Name: " + root.get("name").asText());
            System.out.println("Habitat: " + root.get("habitatName").asText());
            System.out.println("============================");


            JsonNode observations = root.get("observations");

            if (observations == null || observations.isEmpty()) {
                System.out.println("No observations found for this creature.");
                return;
            }

            System.out.println("\n===== OBSERVATIONS =====");

            String format = "%-5s %-20s %-30s %-25s\n";
            System.out.printf(format, "ID", "Author", "Note", "Observed At");
            System.out.println("-------------------------------------------------------------------------------");

            for (JsonNode obs : observations) {
                System.out.printf(format,
                        obs.get("id").asLong(),
                        obs.get("author").asText(),
                        obs.get("note").asText(),
                        obs.get("observedAt").asText()
                );
            }

        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }


    /**
     * Retrieves creatures that need feeding at a specific time (HH:MM format).
     *
     * API Route: GET /api/feedings?time={HH:MM}
     */
    private void findByFeedingTime() {

        String time;

        // 🔹 Validate HH:MM format
        while (true) {
            System.out.print("Enter feeding time (HH:MM): ");
            time = scanner.nextLine();

            if (time.matches("^\\d{2}:\\d{2}$")) {
                break;
            }

            System.out.println("Invalid format. Use HH:MM (e.g., 12:00).");
        }

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/feedings?time=" + time))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            int status = response.statusCode();
            System.out.println("\nStatus: " + status);

            if (status == 400) {
                System.out.println("Invalid time format.");
                return;
            }

            if (status != 200) {
                System.out.println("Error: " + response.body());
                return;
            }

            // 🔥 Parse list
            ObjectMapper mapper = new ObjectMapper();
            List<CreatureResponse> creatures = Arrays.asList(
                    mapper.readValue(response.body(), CreatureResponse[].class)
            );

            if (creatures.isEmpty()) {
                System.out.println("No creatures need feeding at this time.");
                return;
            }

            // 🔥 Display table
            String format = "%-5s %-20s %-20s %-15s %-10s\n";

            System.out.printf(format, "ID", "Name", "Habitat", "Species", "Danger");
            System.out.println("--------------------------------------------------------------------------");

            for (CreatureResponse c : creatures) {
                System.out.printf(format,
                        c.id(),
                        c.name(),
                        c.habitatName(),
                        c.species(),
                        c.dangerLevel()
                );
            }

        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }


    /**
     * Retrieves and displays all system users (admin only).
     *
     * API Route: GET /api/admin/users
     */
    private void viewUsers() {

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/admin/users"))
                    .header("role", "ADMIN")         // Change to staff to test validation
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            int status = response.statusCode();
            System.out.println("\nStatus: " + status);

            if (status == 401) {
                System.out.println("Unauthorized: you must be logged in.");
                return;
            }

            if (status == 403) {
                System.out.println("Access denied: admin privileges required.");
                return;
            }

            if (status != 200) {
                System.out.println("Error: " + response.body());
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            List<UserResponse> users = Arrays.asList(
                    mapper.readValue(response.body(), UserResponse[].class)
            );

            if (users.isEmpty()) {
                System.out.println("No users found.");
                return;
            }

            String format = "%-20s %-25s %-15s %-10s\n";

            System.out.printf(format, "Full Name", "Email", "Phone", "Role");
            System.out.println("--------------------------------------------------------------------------");

            for (UserResponse user : users) {
                System.out.printf(format,
                        user.fullName(),
                        user.email(),
                        user.phone(),
                        user.role()
                );
            }

        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }


    /**
     * Prompts the user to confirm exit.
     *
     * @return true if user confirms exit, otherwise false
     */
    private boolean confirmExit() {

        while (true) {
            System.out.print("Are you sure you want to exit? (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y")) {
                System.out.println("Exiting system... Goodbye!");
                return true;
            }

            if (input.equals("n")) {
                return false;
            }

            System.out.println("Invalid input. Please enter 'y' or 'n'.");
        }
    }


    /**
     * Displays the main menu.
     */
    private static void printMenu() {
        System.out.println("\n=====================================");
        System.out.println("       NEON ARK CLI SYSTEM");
        System.out.println("=====================================");
        System.out.println("1. List all creatures");
        System.out.println("2. View creature by ID");
        System.out.println("3. Register new creature");
        System.out.println("4. Rename creature");
        System.out.println("5. Remove creature");
        System.out.println("6. View creature observations/notes");
        System.out.println("7. Find creatures by feeding time");
        System.out.println("\n--- Admin Only ---");
        System.out.println("8. View all system users");
        System.out.println("\n0. Exit");
        System.out.println("-------------------------------------");
    }


}
