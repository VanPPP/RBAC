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
                System.out.println(user.format());

                var userAssignments = system.getAssignmentManager().findAll().stream()
                        .filter(a -> a.user().username().equals(user.username()))
                        .toList();

                System.out.println("Roles: " + (userAssignments.isEmpty() ? "None" : ""));
                userAssignments.forEach(a -> System.out.println("  - " + a.role().getName()));

                var permissions = system.getAssignmentManager().getUserPermissions(user);
                System.out.println("Total Permissions: " + permissions.size());
                permissions.forEach(p -> System.out.println("  * " + p.name() + " on " + p.resource()));

            }, () -> System.out.println("Error: User not found."));
        });

        parser.registerCommand("user-update", "Update user information", (scanner, system) -> {
            System.out.print("Enter username to update: ");
            String username = scanner.next();

            if (system.getUserManager().exists(username)) {
                System.out.print("Enter new full name: ");
                scanner.nextLine();
                String newFullName = scanner.nextLine();

                System.out.print("Enter new email: ");
                String newEmail = scanner.next();

                try {
                    system.getUserManager().update(username, newFullName, newEmail);
                    System.out.println("Success: User information updated.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Update Error: " + e.getMessage());
                }
            } else {
                System.out.println("Error: User with username '" + username + "' not found.");
            }
        });

        parser.registerCommand("user-delete", "Remove user and their assignments", (scanner, system) -> {
            System.out.print("Enter username to delete: ");
            String username = scanner.next();

            system.getUserManager().findById(username).ifPresentOrElse(user -> {
                System.out.print("Are you sure you want to delete user '" + username + "'? (yes/no): ");
                String confirmation = scanner.next();

                if (confirmation.equalsIgnoreCase("yes")) {
                    system.getAssignmentManager().revokeAllForUser(user);

                    system.getUserManager().remove(user);
                    System.out.println("Success: User and all associated assignments removed.");
                } else {
                    System.out.println("Deletion cancelled.");
                }
            }, () -> System.out.println("Error: User not found."));
        });

        parser.registerCommand("user-search", "Search users by filters", (scanner, system) -> {
            System.out.println("\n--- Search Menu ---");
            System.out.println("1. By username (contains)");
            System.out.println("2. By email (exact)");
            System.out.println("3. By email domain (e.g., gmail.com)");
            System.out.println("4. By full name (contains)");
            System.out.print("Select filter (1-4): ");

            int choice = scanner.hasNextInt() ? scanner.nextInt() : 0;
            scanner.nextLine();

            if (choice < 1 || choice > 4) {
                System.out.println("Error: Invalid selection.");
                return;
            }

            System.out.print("Enter search term: ");
            String term = scanner.nextLine().trim();

            try {
                UserFilter filter = switch (choice) {
                    case 1 -> UserFilters.byUsernameContains(term);
                    case 2 -> UserFilters.byEmail(term);
                    case 3 -> UserFilters.byEmailDomain(term);
                    case 4 -> UserFilters.byFullNameContains(term);
                    default -> null;
                };

                var results = system.getUserManager().findByFilter(filter);

                if (results.isEmpty()) {
                    System.out.println("No users found.");
                } else {
                    System.out.println("\nSearch Results:");
                    results.forEach(u -> System.out.println(" - " + u.format()));
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Validation Error: " + e.getMessage());
            }
        });

        parser.registerCommand("role-list", "Show all roles", (scanner, system) -> {
            var roles = system.getRoleManager().findAll();

            if (roles.isEmpty()) {
                System.out.println("No roles found in the system.");
            } else {
                roles.sort(RoleSorters.byName());

                System.out.println("\n=== Role List ===");
                System.out.printf("%-20s | %-12s | %-36s%n", "Role Name", "Permissions", "ID");
                System.out.println("----------------------------------------------------------------------------");

                roles.forEach(role -> {
                    System.out.printf("%-20s | %-12d | %-36s%n",
                            role.getName(),
                            role.getPermissions().size(),
                            role.getId());
                });
            }
        });

        parser.registerCommand("role-create", "Create a new role with permissions", (scanner, system) -> {
            System.out.print("Enter role name: ");
            String name = scanner.next();

            System.out.print("Enter description: ");
            scanner.nextLine();
            String description = scanner.nextLine();

            try {
                Role newRole = new Role(name, description);
                system.getRoleManager().add(newRole);
                System.out.println("Success: Role '" + name + "' created with ID: " + newRole.getId());

                while (true) {
                    System.out.print("\nAdd a permission to this role? (y/n): ");
                    String choice = scanner.next().toLowerCase();

                    if (!choice.equals("y")) {
                        break;
                    }

                    System.out.print("Enter permission name (e.g., READ, WRITE): ");
                    String pName = scanner.next();

                    System.out.print("Enter resource (e.g., reports, system): ");
                    String pResource = scanner.next();

                    System.out.print("Enter permission description: ");
                    scanner.nextLine();
                    String pDesc = scanner.nextLine();

                    Permission permission = new Permission(pName, pResource, pDesc);
                    newRole.addPermission(permission);

                    System.out.println("Permission '" + pName + "' on '" + pResource + "' added to role.");
                }

                System.out.println("\nRole creation finished. Total permissions: " + newRole.getPermissions().size());

            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        });

        parser.registerCommand("role-view", "View role details and permissions", (scanner, system) -> {
            System.out.print("Enter role name: ");
            String name = scanner.next();

            system.getRoleManager().findByName(name).ifPresentOrElse(role -> {
                System.out.println("\n" + role.format());
            }, () -> System.out.println("Error: Role '" + name + "' not found."));
        });

        parser.registerCommand("role-delete", "Delete a role from system", (scanner, system) -> {
            System.out.print("Enter role name to delete: ");
            String name = scanner.next();

            system.getRoleManager().findByName(name).ifPresentOrElse(role -> {
                try {
                    boolean removed = system.getRoleManager().remove(role);
                    if (removed) {
                        System.out.println("Success: Role '" + name + "' deleted.");
                    } else {
                        System.out.println("Error: Could not delete role.");
                    }
                } catch (IllegalStateException e) {
                    System.out.println("Constraint Error: " + e.getMessage());
                }
            }, () -> System.out.println("Error: Role '" + name + "' not found."));
        });






    }
}