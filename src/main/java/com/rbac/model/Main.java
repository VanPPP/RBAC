package com.rbac.model;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        RBACSystem system = new RBACSystem();
        system.initialize();

        CommandParser parser = new CommandParser();
        CommandRegistry.registerAll(parser);

        Scanner scanner = new Scanner(System.in);
        System.out.println("=== RBAC Control Panel Loaded ===");
        System.out.println("Type 'help' to see available commands or 'exit' to quit.");

        while (true) {
            System.out.print("\n> ");
            if (!scanner.hasNextLine()) break;

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting system...");
                break;
            }

            if (input.equalsIgnoreCase("help")) {
                parser.printHelp();
                continue;
            }

            parser.parseAndExecute(input, scanner, system);
        }

        scanner.close();
    }
}