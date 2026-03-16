package com.rbac.model;

import java.util.List;
import java.util.Scanner;

public class ConsoleUtils {

    public static String promptString(Scanner scanner, String message, boolean required) {
        while (true) {
            System.out.print(message + (required ? " (required): " : ": "));
            String input = scanner.hasNextLine() ? scanner.nextLine().trim() : "";

            if (required && input.isEmpty()) {
                System.out.println("Error: This field is required.");
                continue;
            }
            return ValidationUtils.normalizeString(input);
        }
    }

    public static int promptInt(Scanner scanner, String message, int min, int max) {
        while (true) {
            System.out.printf("%s (%d-%d): ", message, min, max);
            String input = scanner.next();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("Error: Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number format.");
            }
        }
    }

    public static boolean promptYesNo(Scanner scanner, String message) {
        while (true) {
            System.out.print(message + " (yes/no): ");
            String input = scanner.next().toLowerCase();
            if (input.equals("yes") || input.equals("y")) return true;
            if (input.equals("no") || input.equals("n")) return false;
            System.out.println("Error: Please enter 'yes' or 'no'.");
        }
    }

    public static <T> T promptChoice(Scanner scanner, String message, List<T> options) {
        if (options == null || options.isEmpty()) return null;

        System.out.println("\n" + message);
        for (int i = 0; i < options.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, options.get(i).toString());
        }

        int choice = promptInt(scanner, "Select option", 1, options.size());
        return options.get(choice - 1);
    }
}