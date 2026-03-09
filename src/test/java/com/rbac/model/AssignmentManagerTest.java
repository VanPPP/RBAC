package com.rbac.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class AssignmentManagerTest {
    private AssignmentManager manager;
    private User testUser;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        manager = new AssignmentManager();
        testUser = User.create("tester", "Test User", "test@test.com");
        adminRole = new Role("Admin", "Admin role");
        adminRole.addPermission(new Permission("READ", "docs", "Read access"));
    }

    @Test
    @DisplayName("Grant access for active assignment")
    void testActiveAccess() {
        AssignmentMetadata meta = AssignmentMetadata.now("admin", "setup");
        PermanentAssignment pa = new PermanentAssignment(testUser, adminRole, meta);
        manager.add(pa);

        assertTrue(manager.userHasPermission(testUser, "READ", "docs"),
                "Access should be granted for active role");
    }

    @Test
    @DisplayName("Deny access after assignment is revoked")
    void testRevokedAccess() {
        AssignmentMetadata meta = AssignmentMetadata.now("admin", "setup");
        PermanentAssignment pa = new PermanentAssignment(testUser, adminRole, meta);
        manager.add(pa);

        manager.revokeAssignment(pa.assignmentId());

        assertFalse(manager.userHasPermission(testUser, "READ", "docs"),
                "Access should be denied after revocation");
    }

    @Test
    @DisplayName("Prevent duplicate active role assignments")
    void testDuplicateProtection() {
        AssignmentMetadata meta = AssignmentMetadata.now("admin", "setup");
        PermanentAssignment pa1 = new PermanentAssignment(testUser, adminRole, meta);
        manager.add(pa1);

        PermanentAssignment pa2 = new PermanentAssignment(testUser, adminRole, meta);

        assertThrows(IllegalStateException.class, () -> {
            manager.add(pa2);
        }, "Should throw exception when adding existing active role");
    }
}