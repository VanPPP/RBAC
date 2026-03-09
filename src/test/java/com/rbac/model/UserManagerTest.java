package com.rbac.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class UserManagerTest {
    private UserManager userManager;

    @BeforeEach
    void setUp() {
        userManager = new UserManager();
    }

    @Test
    @DisplayName("Successfully add and find user")
    void testAddAndFindUser() {
        User user = User.create("ivan_dev", "Ivan Ivanov", "ivan@mail.com");
        userManager.add(user);

        assertTrue(userManager.findById("ivan_dev").isPresent(), "User should be found by ID");
        assertEquals(1, userManager.count(), "User count should be 1");
    }

    @Test
    @DisplayName("Fail when adding user with existing username")
    void testDuplicateUser() {
        User user1 = User.create("tester", "Test 1", "test1@mail.com");
        User user2 = User.create("tester", "Test 2", "test2@mail.com");

        userManager.add(user1);

        assertThrows(IllegalArgumentException.class, () -> {
            userManager.add(user2);
        }, "Manager should prevent duplicate username entries");
    }

    @Test
    @DisplayName("Remove user from repository")
    void testRemoveUser() {
        User user = User.create("delete_me", "To Be Deleted", "del@mail.com");
        userManager.add(user);

        boolean removed = userManager.remove(user);

        assertTrue(removed, "remove method should return true");
        assertEquals(0, userManager.count(), "Repository should be empty after removal");
    }
}