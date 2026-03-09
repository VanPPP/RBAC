package com.rbac.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class RoleManagerTest {
    private RoleManager roleManager;

    @BeforeEach
    void setUp() {
        roleManager = new RoleManager();
    }

    @Test
    @DisplayName("Add role and verify existence")
    void testAddAndFindRole() {
        Role role = new Role("Editor", "Can edit posts");
        roleManager.add(role);

        assertTrue(roleManager.exists("Editor"), "Role should exist in the manager");
        assertEquals(1, roleManager.count());
    }

    @Test
    @DisplayName("Fail to remove role if it has active assignments")
    void testRemoveAssignedRole() {
        Role role = new Role("Admin", "Root");
        roleManager.add(role);

        // Mocking assignment existence via Predicate
        roleManager.setHasAssignmentsChecker(r -> true);

        assertThrows(IllegalStateException.class, () -> {
            roleManager.remove(role);
        }, "Should not remove role if it is currently assigned");
    }

    @Test
    @DisplayName("Find roles by specific permission")
    void testFindRolesWithPermission() {
        Role role = new Role("Manager", "Desc");
        role.addPermission(new Permission("APPROVE", "orders", "Can approve"));
        roleManager.add(role);

        var found = roleManager.findRolesWithPermission("APPROVE", "orders");

        assertFalse(found.isEmpty(), "Found roles list should not be empty");
        assertEquals("Manager", found.get(0).getName());
    }
}