package org.example.neonarkintaketracker.cli;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import java.util.List;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.neonarkintaketracker.dto.CreatureResponse;

import java.util.Scanner;
import java.net.*;
import java.io.*;

public class NeonArkCli {

    private final Scanner scanner = new Scanner(System.in);
    private final String API_BASE = "http://localhost:8080/api";


    public static void main(String[] args) {
        new NeonArkCli().start();
    }


    private void start() {

        boolean running = true;

        while(running){

            printMenu();
            System.out.println("Select an option: ");
            String choice = scanner.nextLine();

            switch(choice){
                case "1":
                    listAll();             //  GET /api/creatures
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


    private void displayCreatureTable(List<CreatureResponse> creatures) {

        if (creatures.isEmpty()) {
            System.out.println("No creatures found.");
            return;
        }

        String format = "%-5s %-20s %-15s %-10s %-15s\n";

        System.out.printf(format, "ID", "Name", "Species", "Danger", "Condition");
        System.out.println("----------------------------------------------------------------");

        for (CreatureResponse creature : creatures) {
            System.out.printf(format,
                    creature.id(),
                    creature.name(),
                    creature.species(),
                    creature.dangerLevel(),
                    creature.condition()
            );
        }
    }


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


    private CreatureResponse parseSingle(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, CreatureResponse.class);

        } catch (Exception e) {
            System.out.println("Failed to parse response.");
            return null;
        }
    }

    private void displaySingleCreature(CreatureResponse creature) {

        if (creature == null) {
            System.out.println("No data to display.");
            return;
        }

        System.out.println("\n===== CREATURE DETAILS =====");
        System.out.println("ID: " + creature.id());
        System.out.println("Name: " + creature.name());
        System.out.println("Species: " + creature.species());
        System.out.println("Danger Level: " + creature.dangerLevel());
        System.out.println("Condition: " + creature.condition());
        System.out.println("Notes: " + (creature.notes() != null ? creature.notes() : ""));
        System.out.println("============================\n");
    }


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
                System.out.println("Conflict: Creature already exists.");

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


    private void rename() {

    }


    private void remove() {

    }


    private void viewObservations() {

    }


    private void findByFeedingTime() {

    }


    private void viewUsers() {

    }


    private boolean confirmExit() {
        return true;
    }


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
