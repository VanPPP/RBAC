package com.rbac.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

class UserManagerTest {

    private UserManager manager;
    private User testUser;

    @BeforeEach
    void setUp() {
        manager = new UserManager();
        testUser = User.create("john_doe", "John Doe", "john@example.com");
    }

    @Test
    void addAndFind() {
        manager.add(testUser);

        assertEquals(1, manager.count());
        assertTrue(manager.exists("john_doe"));

        Optional<User> found = manager.findByUsername("john_doe");
        assertTrue(found.isPresent());
        assertEquals("john_doe", found.get().username());
    }

    @Test
    void addDuplicateThrows() {
        manager.add(testUser);

        User duplicate = User.create("john_doe", "John Doe", "john@example.com");

        assertThrows(IllegalArgumentException.class, () -> manager.add(duplicate));
    }

    @Test
    void removeUser() {
        manager.add(testUser);
        assertTrue(manager.remove(testUser));
        assertEquals(0, manager.count());
    }

    @Test
    void findByEmail() {
        manager.add(testUser);

        Optional<User> found = manager.findByEmail("john@example.com");
        assertTrue(found.isPresent());
        assertEquals("john_doe", found.get().username());
    }

    @Test
    void filterByUsername() {
        manager.add(testUser);
        manager.add(User.create("jane_smith", "Jane Smith", "jane@work.com"));

        UserFilter filter = UserFilters.byUsernameContains("john");
        List<User> result = manager.findByFilter(filter);

        assertEquals(1, result.size());
        assertEquals("john_doe", result.get(0).username());
    }

    @Test
    void filterByEmailDomain() {
        manager.add(testUser);
        manager.add(User.create("bob", "Bob", "bob@gmail.com"));

        UserFilter filter = UserFilters.byEmailDomain("@gmail.com");
        List<User> result = manager.findByFilter(filter);

        assertEquals(1, result.size());
        assertEquals("bob", result.get(0).username());
    }

    @Test
    void sortUsers() {
        manager.add(User.create("bob", "Bob", "bob@mail.com"));
        manager.add(User.create("alice", "Alice", "alice@mail.com"));

        List<User> sorted = manager.findAll(null, UserSorters.byUsername());

        assertEquals("alice", sorted.get(0).username());
        assertEquals("bob", sorted.get(1).username());
    }

    @Test
    void updateUser() {
        manager.add(testUser);

        manager.update("john_doe", "John Updated", "john.new@example.com");

        User updated = manager.findByUsername("john_doe").get();
        assertEquals("John Updated", updated.fullName());
        assertEquals("john.new@example.com", updated.email());
    }
}