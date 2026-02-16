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
    }
}