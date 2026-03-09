package com.rbac.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandParser {
    private final Map<String, Command> commands = new HashMap<>();
    private final Map<String, String> commandDescriptions = new HashMap<>();

    public void registerCommand(String name, String description, Command command) {
        commands.put(name, command);
        commandDescriptions.put(name, description);
    }

    public void executeCommand(String commandName, Scanner scanner, RBACSystem system) {
        Command command = commands.get(commandName);
        if (command != null) {
            command.execute(scanner, system);
        } else {
            System.out.println("Unknown command: " + commandName + ". Type 'help' for assistance.");
        }
    }

    public void printHelp() {
        System.out.println("\n=== Available Commands ===");
        commandDescriptions.forEach((name, desc) ->
                System.out.printf("%-20s - %s%n", name, desc)
        );
    }

    public void parseAndExecute(String input, Scanner scanner, RBACSystem system) {
        if (input == null || input.trim().isEmpty()) return;

        String[] parts = input.trim().split("\\s+", 2);
        String commandName = parts[0].toLowerCase();

        executeCommand(commandName, scanner, system);
    }
}