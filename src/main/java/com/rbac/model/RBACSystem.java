package com.rbac.model;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RBACSystem {
    private final UserManager userManager;
    private final RoleManager roleManager;
    private final AssignmentManager assignmentManager;
    private String currentUser;

    private final ExecutorService backgroundExecutor = Executors.newFixedThreadPool(4);

    public RBACSystem() {
        this.userManager = new UserManager();
        this.roleManager = new RoleManager();
        this.assignmentManager = new AssignmentManager();

        this.roleManager.setHasAssignmentsChecker(role ->
                !assignmentManager.findByRole(role).isEmpty()
        );
    }

    public void executeAsync(Runnable task) {
        backgroundExecutor.execute(task);
    }

    public void shutdown() {
        backgroundExecutor.shutdown();
    }

    public UserManager getUserManager() { return userManager; }
    public RoleManager getRoleManager() { return roleManager; }
    public AssignmentManager getAssignmentManager() { return assignmentManager; }

    public void setCurrentUser(String username) { this.currentUser = username; }
    public String getCurrentUser() { return currentUser; }

    public void initialize() {
        Permission read = new Permission("READ", "system", "Read access");
        Permission write = new Permission("WRITE", "system", "Write access");
        Permission delete = new Permission("DELETE", "system", "Delete access");

        Role adminRole = new Role("Admin", "Full system access");
        adminRole.addPermission(read);
        adminRole.addPermission(write);
        adminRole.addPermission(delete);
        roleManager.add(adminRole);

        Role managerRole = new Role("Manager", "Management access");
        managerRole.addPermission(read);
        managerRole.addPermission(write);
        roleManager.add(managerRole);

        Role viewerRole = new Role("Viewer", "Read only access");
        viewerRole.addPermission(read);
        roleManager.add(viewerRole);

        User adminUser = User.create("admin", "System Administrator", "admin@system.com");
        userManager.add(adminUser);
        this.currentUser = "admin";

        AssignmentMetadata meta = AssignmentMetadata.now(currentUser, "System initialization");
        assignmentManager.add(new PermanentAssignment(adminUser, adminRole, meta));
    }

    public String generateStatistics() {
        int totalUsers = userManager.count();
        int totalRoles = roleManager.count();
        List<RoleAssignment> allAssignments = assignmentManager.findAll();

        long activeCount = allAssignments.stream()
                .filter(RoleAssignment::isActive)
                .count();

        long expiredCount = allAssignments.size() - activeCount;
        double avgRoles = totalUsers == 0 ? 0 : (double) allAssignments.size() / totalUsers;

        StringBuilder sb = new StringBuilder();
        sb.append("=== RBAC System Statistics ===\n");
        sb.append(String.format("Users: %d\n", totalUsers));
        sb.append(String.format("Roles: %d\n", totalRoles));
        sb.append(String.format("Assignments: %d (Active: %d, Expired: %d)\n",
                allAssignments.size(), (int)activeCount, (int)expiredCount));
        sb.append(String.format("Average roles per user: %.2f\n", avgRoles));

        return sb.toString();
    }
}