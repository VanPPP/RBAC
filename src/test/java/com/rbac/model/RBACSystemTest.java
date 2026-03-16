package com.rbac.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class RBACSystemTest {
    private RBACSystem system;
    private User testUser;
    private Role adminRole;
    private Permission editPerm;

    @BeforeEach
    void setUp() {
        system = new RBACSystem();
        system.initialize();

        testUser = User.create("editor", "Editor User", "editor@test.com");
        system.getUserManager().add(testUser);

        editPerm = new Permission("EDIT", "articles", "Can edit articles");
        adminRole = system.getRoleManager().findByName("Admin").orElseThrow();
    }

    @Test
    @DisplayName("Проверка получения прав пользователя через активное назначение")
    void testGetUserPermissions() {
        AssignmentMetadata meta = AssignmentMetadata.now("admin", "Test assignment");
        RoleAssignment assignment = new PermanentAssignment(testUser, adminRole, meta);
        system.getAssignmentManager().add(assignment);

        Set<Permission> permissions = system.getAssignmentManager().getUserPermissions(testUser);

        assertFalse(permissions.isEmpty(), "Список прав не должен быть пустым");
        assertTrue(system.getAssignmentManager().userHasPermission(testUser, "READ", "system"),
                "Пользователь должен иметь право READ на ресурс system"); //
    }

    @Test
    @DisplayName("Права не должны быть доступны, если роль отозвана (INACTIVE)")
    void testPermissionsAfterRevoke() {
        AssignmentMetadata meta = AssignmentMetadata.now("admin", "To be revoked");
        String assignmentId = "test-id-123";
        RoleAssignment assignment = new PermanentAssignment(testUser, adminRole, meta);
        system.getAssignmentManager().add(assignment);

        system.getAssignmentManager().revokeAssignment(assignment.assignmentId());

        Set<Permission> permissions = system.getAssignmentManager().getUserPermissions(testUser);
        assertTrue(permissions.isEmpty(), "После отзыва роли прав быть не должно");
    }

    @Test
    @DisplayName("Запрет на дублирование активных назначений")
    void testDuplicateAssignmentFails() {
        AssignmentMetadata meta = AssignmentMetadata.now("admin", "First");
        RoleAssignment assignment1 = new PermanentAssignment(testUser, adminRole, meta);
        system.getAssignmentManager().add(assignment1);

        RoleAssignment assignment2 = new PermanentAssignment(testUser, adminRole, meta);

        assertThrows(IllegalStateException.class, () -> {
            system.getAssignmentManager().add(assignment2);
        }, "Система не должна позволять дублировать активную роль для одного юзера");
    }

    @Test
    @DisplayName("Проверка генерации корректной статистики")
    void testGenerateStatistics() {
        String stats = system.generateStatistics();

        assertNotNull(stats);
        assertTrue(stats.contains("Users:"), "Статистика должна содержать количество пользователей");
        assertTrue(stats.contains("Roles:"), "Статистика должна содержать количество ролей");
    }
}