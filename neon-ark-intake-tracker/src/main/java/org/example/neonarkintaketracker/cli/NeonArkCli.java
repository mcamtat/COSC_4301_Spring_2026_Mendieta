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
        System.out.println("-------------------------------------------------------------");

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

    }


    private void register() {

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
