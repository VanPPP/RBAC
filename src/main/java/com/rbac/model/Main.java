package com.rbac.model;

import com.rbac.model.*;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        // 1. Инициализируем менеджеры
        UserManager userManager = new UserManager();
        RoleManager roleManager = new RoleManager();
        AssignmentManager assignmentManager = new AssignmentManager();

        // Связываем RoleManager с AssignmentManager для проверки при удалении
        roleManager.setHasAssignmentsChecker(role ->
                !assignmentManager.findByRole(role).isEmpty()
        );

        System.out.println("=== RBAC System Demo ===\n");

        // 2. Создаем пользователя
        User ivan = User.create("ivan_dev", "Ivan Ivanov", "ivan@company.com");
        userManager.add(ivan);
        System.out.println("User created: " + ivan.format());

        // 3. Создаем роль и права
        Permission readDocs = new Permission("READ", "documents", "Can read docs");
        Permission deleteDocs = new Permission("DELETE", "documents", "Can delete docs");

        Role adminRole = new Role("Admin", "Full access to documents");
        adminRole.addPermission(readDocs);
        adminRole.addPermission(deleteDocs);

        roleManager.add(adminRole);
        System.out.println("Role created: " + adminRole.getName());

        // 4. Назначаем роль пользователю (Постоянная)
        AssignmentMetadata meta = AssignmentMetadata.now("SystemRoot", "Initial setup");
        PermanentAssignment assignment = new PermanentAssignment(ivan, adminRole, meta);
        assignmentManager.add(assignment);

        System.out.println("\n--- Access Check ---");

        // 5. Проверяем права
        checkAccess(assignmentManager, ivan, "READ", "documents");
        checkAccess(assignmentManager, ivan, "WRITE", "servers"); // Этого права нет

        // 6. Отзываем роль и проверяем снова
        System.out.println("\nRevoking Admin role...");
        assignmentManager.revokeAssignment(assignment.assignmentId());
        checkAccess(assignmentManager, ivan, "READ", "documents");
    }

    private static void checkAccess(AssignmentManager am, User u, String perm, String res) {
        boolean hasAccess = am.userHasPermission(u, perm, res);
        System.out.printf("Does %s have %s on %s? -> %s%n",
                u.username(), perm, res, hasAccess ? "YES" : "NO");
    }
}