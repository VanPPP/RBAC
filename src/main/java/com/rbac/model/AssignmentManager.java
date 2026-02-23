package com.rbac.model;

import java.util.*;
import java.util.stream.Collectors;

public class AssignmentManager implements Repository<RoleAssignment> {
    private final Map<String, RoleAssignment> assignments = new HashMap<>();

    @Override
    public void add(RoleAssignment assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment cannot be null");
        }
        if (assignments.containsKey(assignment.assignmentId())) {
            throw new IllegalArgumentException("Assignment ID already exists");
        }
        assignments.put(assignment.assignmentId(), assignment);
    }

    @Override
    public boolean remove(RoleAssignment assignment) {
        if (assignment == null) return false;
        return assignments.remove(assignment.assignmentId()) != null;
    }

    @Override
    public Optional<RoleAssignment> findById(String id) {
        return Optional.ofNullable(assignments.get(id));
    }

    @Override
    public List<RoleAssignment> findAll() {
        return new ArrayList<>(assignments.values());
    }

    @Override
    public int count() {
        return assignments.size();
    }

    @Override
    public void clear() {
        assignments.clear();
    }

    public List<RoleAssignment> findByUser(User user) {
        if (user == null) return Collections.emptyList();
        return assignments.values().stream()
                .filter(a -> a.user().equals(user))
                .collect(Collectors.toList());
    }

    public List<RoleAssignment> findByRole(Role role) {
        if (role == null) return Collections.emptyList();
        return assignments.values().stream()
                .filter(a -> a.role().equals(role))
                .collect(Collectors.toList());
    }

    public List<RoleAssignment> findByFilter(AssignmentFilter filter) {
        if (filter == null) return findAll();
        return assignments.values().stream()
                .filter(filter::test)
                .collect(Collectors.toList());
    }

    public List<RoleAssignment> findAll(AssignmentFilter filter, Comparator<RoleAssignment> sorter) {
        List<RoleAssignment> result = findByFilter(filter);

        if (sorter != null) {
            result.sort(sorter);
        } else {
            result.sort(Comparator.comparing(a -> a.metadata().assignedAt()));
        }

        return result;
    }

    public List<RoleAssignment> getActiveAssignments() {
        return assignments.values().stream()
                .filter(RoleAssignment::isActive)
                .collect(Collectors.toList());
    }

    public List<RoleAssignment> getExpiredAssignments() {
        return assignments.values().stream()
                .filter(a -> !a.isActive())
                .collect(Collectors.toList());
    }

    public boolean userHasRole(User user, Role role) {
        if (user == null || role == null) return false;
        return assignments.values().stream()
                .anyMatch(a -> a.user().equals(user) && a.role().equals(role));
    }

    public boolean userHasPermission(User user, String permissionName, String resource) {
        if (user == null || permissionName == null || resource == null) return false;
        return assignments.values().stream()
                .filter(a -> a.user().equals(user))
                .anyMatch(a -> a.role().hasPermission(permissionName, resource));
    }

    public Set<Permission> getUserPermissions(User user) {
        if (user == null) return Collections.emptySet();
        return assignments.values().stream()
                .filter(a -> a.user().equals(user))
                .flatMap(a -> a.role().getPermissions().stream())
                .collect(Collectors.toSet());
    }

    public void revokeAssignment(String assignmentId) {
        RoleAssignment assignment = assignments.get(assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment not found");
        }

        if (assignment instanceof PermanentAssignment pa) {
            pa.revoke();
        } else {
            throw new IllegalArgumentException("Only permanent assignments can be revoked");
        }
    }

    public void extendTemporaryAssignment(String assignmentId, String newExpirationDate) {
        RoleAssignment assignment = assignments.get(assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment not found");
        }

        if (assignment instanceof TemporaryAssignment ta) {
            ta.extend(newExpirationDate);
        } else {
            throw new IllegalArgumentException("Only temporary assignments can be extended");
        }
    }
}