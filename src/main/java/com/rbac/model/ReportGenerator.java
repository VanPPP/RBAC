package com.rbac.model;

import com.rbac.model.*;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportGenerator {

    public static String generateUserReport(UserManager userManager, AssignmentManager assignmentManager) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== USER ROLE REPORT ===\n");
        sb.append(String.format("%-15s | %-30s\n", "Username", "Active Roles"));
        sb.append("-".repeat(48)).append("\n");

        String rows = userManager.findAll().parallelStream()
                .map(user -> {
                    String roles = assignmentManager.findAll().stream()
                            .filter(a -> a.user().equals(user) && a.isActive())
                            .map(a -> a.role().getName())
                            .collect(Collectors.joining(", "));
                    return String.format("%-15s | %s", user.username(), roles.isEmpty() ? "No roles" : roles);
                })
                .collect(Collectors.joining("\n"));

        sb.append(rows).append("\n");
        return sb.toString();
    }

    public static String generateRoleReport(RoleManager roleManager, AssignmentManager assignmentManager) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ROLE USAGE REPORT ===\n");
        sb.append(String.format("%-15s | %-10s\n", "Role Name", "Users Count"));
        sb.append("-".repeat(28)).append("\n");

        roleManager.findAll().forEach(role -> {
            long count = assignmentManager.findAll().stream()
                    .filter(a -> a.role().equals(role) && a.isActive())
                    .map(a -> a.user().username())
                    .distinct()
                    .count();

            sb.append(String.format("%-15s | %-10d\n", role.getName(), count));
        });
        return sb.toString();
    }

    public static String generatePermissionMatrix(UserManager userManager, AssignmentManager assignmentManager) {
        StringBuilder sb = new StringBuilder();

        Set<String> resources = assignmentManager.findAll().stream()
                .flatMap(a -> a.role().getPermissions().stream())
                .map(Permission::resource)
                .collect(Collectors.toCollection(TreeSet::new));

        sb.append("=== PERMISSION MATRIX (Resources) ===\n");
        sb.append(String.format("%-15s", "User \\ Res"));
        resources.forEach(res -> sb.append(String.format(" | %-10s", res)));
        sb.append("\n").append("-".repeat(15 + resources.size() * 13)).append("\n");

        String matrix = userManager.findAll().parallelStream()
                .map(user -> {
                    StringBuilder row = new StringBuilder(String.format("%-15s", user.username()));
                    Set<Permission> userPerms = assignmentManager.getUserPermissions(user);

                    resources.forEach(res -> {
                        boolean hasAccess = userPerms.stream().anyMatch(p -> p.resource().equals(res));
                        row.append(String.format(" | %-10s", hasAccess ? "[ X ]" : "[   ]"));
                    });
                    return row.toString();
                })
                .collect(Collectors.joining("\n"));

        sb.append(matrix).append("\n");
        return sb.toString();
    }

    public static void exportToFile(String report, String filename) {
        try (PrintWriter out = new PrintWriter(filename)) {
            out.println(report);
            System.out.println("Success: Report saved to " + filename);
        } catch (Exception e) {
            System.out.println("Error saving report: " + e.getMessage());
        }
    }
}