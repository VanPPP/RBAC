package com.rbac.model;

import com.rbac.model.*;
import java.util.Set;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {

        // Пользователи
        System.out.println("user testing\n");

        User u1 = User.create("john_doe", "John Doe", "john@example.com");
        System.out.println("User created: " + u1.format());

        try {
            User.create(null, "Name", "a@b.co");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            User.create("username", "Name", null);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            User.create("user", "Name", "invalid-email");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\npermission testing\n");

        Permission readUsers = new Permission("READ", "users", "Can view user list");
        System.out.println("Permission format(): " + readUsers.format());

        System.out.println("\nrole testing\n");

        Set<Permission> perms = Set.of(
                new Permission("READ", "users", "Can view user list"),
                new Permission("WRITE", "users", "Can create and edit users"),
                new Permission("DELETE", "users", "Can delete users")
        );
        Role admin = new Role("Administrator", "Full system access", new HashSet<>(perms));
        System.out.println(admin.format());

        System.out.println("\nmetadata testing\n");

        AssignmentMetadata meta = AssignmentMetadata.now("admin", "New team member");
        System.out.println("AssignmentMetadata now():");
        System.out.println(meta.format());

        AssignmentMetadata fixedMeta = new AssignmentMetadata("admin", "2026-02-07T19:00:00Z", "Initial setup");
        System.out.println("\nAssignmentMetadata fixed:");
        System.out.println(fixedMeta.format());

        System.out.println("\npermanent assignment testing\n");

        PermanentAssignment permAssign = new PermanentAssignment(u1, admin, fixedMeta);
        System.out.println(permAssign.summary());

        System.out.println("\ntemporary assignment testing (FUTURE)\n");

        AssignmentMetadata tempMeta = new AssignmentMetadata("admin", "2026-02-01T10:00:00Z", "Temporary access");
        TemporaryAssignment tempFuture = new TemporaryAssignment(u1, admin, tempMeta, "2026-12-31", false);

        System.out.println("TemporaryAssignment (expires 2026-12-31)");
        System.out.println("summary(): " + tempFuture.summary());
        System.out.println("isExpired(): " + tempFuture.isExpired());
        System.out.println("isExpired(\"2026-06-01\"): " + tempFuture.isExpired("2026-06-01"));
        System.out.println("isExpired(\"2027-01-01\"): " + tempFuture.isExpired("2027-01-01"));
        System.out.println("isActive(): " + tempFuture.isActive());
        System.out.println("isAutoRenew(): " + tempFuture.isAutoRenew());
        System.out.println("getTimeRemaining(): " + tempFuture.getTimeRemaining());

        System.out.println("\nextend:");
        System.out.println("getExpiresAt: " + tempFuture.getExpiresAt());
        tempFuture.extend("2027-06-30");
        System.out.println("getExpiresAt after extend: " + tempFuture.getExpiresAt());
        System.out.println("getTimeRemaining after extend: " + tempFuture.getTimeRemaining());

        System.out.println("\nTemporary assignment (past)\n");

        TemporaryAssignment tempPast = new TemporaryAssignment(u1, admin, tempMeta, "2026-01-01", true);
        System.out.println("TemporaryAssignment (expired)");
        System.out.println("isExpired(): " + tempPast.isExpired());
        System.out.println("isActive(): " + tempPast.isActive());
        System.out.println("isAutoRenew(): " + tempPast.isAutoRenew());
        System.out.println("getTimeRemaining(): " + tempPast.getTimeRemaining());

        System.out.println("\ntesting user manager\n");

        UserManager userManager = new UserManager();

        System.out.println("Adding users:");
        userManager.add(u1);
        userManager.add(User.create("jane_smith", "Jane Smith", "jane@work.com"));
        userManager.add(User.create("bob_wilson", "Bob Wilson", "bob@gmail.com"));
        System.out.println("Total users: " + userManager.count());

        System.out.println("\nCheck exists:");
        System.out.println("john_doe exists? " + userManager.exists("john_doe"));
        System.out.println("unknown exists? " + userManager.exists("unknown"));

        System.out.println("\nFind by username:");
        userManager.findByUsername("jane_smith")
                .ifPresentOrElse(
                        user -> System.out.println("Found: " + user.format()),
                        () -> System.out.println("Not found")
                );

        System.out.println("\nFind by email:");
        userManager.findByEmail("bob@gmail.com")
                .ifPresent(user -> System.out.println("Found: " + user.format()));

        System.out.println("\nFilter by email domain '@gmail.com':");
        UserFilter gmailFilter = UserFilters.byEmailDomain("@gmail.com");
        userManager.findByFilter(gmailFilter)
                .forEach(user -> System.out.println(" - " + user.format()));

        System.out.println("\nFilter by username contains 'john':");
        UserFilter nameFilter = UserFilters.byUsernameContains("john");
        userManager.findByFilter(nameFilter)
                .forEach(user -> System.out.println(" - " + user.format()));

        System.out.println("\nFilter (username contains 'j' AND email domain '@work.com'):");
        UserFilter complex = UserFilters.byUsernameContains("j")
                .and(UserFilters.byEmailDomain("@work.com"));
        userManager.findByFilter(complex)
                .forEach(user -> System.out.println(" - " + user.format()));

        System.out.println("\nAll users sorted by username:");
        userManager.findAll(null, UserSorters.byUsername())
                .forEach(user -> System.out.println(" - " + user.format()));

        System.out.println("\nAll users sorted by email:");
        userManager.findAll(null, UserSorters.byEmail())
                .forEach(user -> System.out.println(" - " + user.format()));

        System.out.println("\nGmail users sorted by full name:");
        userManager.findAll(gmailFilter, UserSorters.byFullName())
                .forEach(user -> System.out.println(" - " + user.format()));

        System.out.println("\nUpdating user:");
        System.out.println("Before: " + userManager.findByUsername("bob_wilson").get().format());
        userManager.update("bob_wilson", "Robert Wilson", "robert@gmail.com");
        System.out.println("After: " + userManager.findByUsername("bob_wilson").get().format());

        System.out.println("\nRemoving user:");
        User toRemove = userManager.findByUsername("jane_smith").get();
        userManager.remove(toRemove);
        System.out.println("After removal, total users: " + userManager.count());

        System.out.println("\nError handling:");
        try {
            userManager.add(u1); // пытаемся добавить существующего
        } catch (IllegalArgumentException e) {
            System.out.println("Expected error: " + e.getMessage());
        }

        try {
            userManager.update("unknown", "New Name", "new@email.com");
        } catch (IllegalArgumentException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
    }
}