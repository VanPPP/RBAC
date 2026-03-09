package com.rbac.model;

import java.util.Scanner;

public class CommandRegistry {
    public static void registerAll(CommandParser parser) {

        parser.registerCommand("user-list", "Show all users", (scanner, system) -> {
            var users = system.getUserManager().findAll();
            if (users.isEmpty()) {
                System.out.println("No users found.");
            } else {
                System.out.println("\n=== User List ===");
                System.out.printf("%-15s | %-20s | %-25s%n", "Username", "Full Name", "Email");
                System.out.println("------------------------------------------------------------");
                users.forEach(u -> System.out.printf("%-15s | %-20s | %-25s%n",
                        u.username(), u.fullName(), u.email()));
            }
        });

        parser.registerCommand("user-create", "Create a new user", (scanner, system) -> {
            System.out.print("Enter username: ");
            String username = scanner.next();

            System.out.print("Enter full name: ");
            scanner.nextLine();
            String fullName = scanner.nextLine();

            System.out.print("Enter email: ");
            String email = scanner.next();

            try {
                User newUser = User.create(username, fullName, email);
                system.getUserManager().add(newUser);
                System.out.println("Success: User created successfully.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        });

        parser.registerCommand("user-view", "View user details and permissions", (scanner, system) -> {
            System.out.print("Enter username: ");
            String username = scanner.next();

            system.getUserManager().findById(username).ifPresentOrElse(user -> {
                System.out.println("\n--- User Profile ---");
                System.out.println("Username:  " + user.username());
                System.out.println("Full Name: " + user.fullName());
                System.out.println("Email:     " + user.email());

                var assignments = system.getAssignmentManager().findByUser(user);
                System.out.println("Roles:     " + (assignments.isEmpty() ? "None" : ""));
                assignments.forEach(a -> System.out.println("  - " + a.role().getName() +
                        (a.isActive() ? " [ACTIVE]" : " [INACTIVE]")));

                var permissions = system.getAssignmentManager().getUserPermissions(user);
                System.out.println("Permissions: " + (permissions.isEmpty() ? "None" : ""));
                permissions.forEach(p -> System.out.println("  * " + p.name() + " on " + p.resource()));

            }, () -> System.out.println("Error: User not found."));
        });
    }
}