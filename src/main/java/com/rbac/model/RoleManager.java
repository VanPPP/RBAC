package com.rbac.model;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

public class RoleManager implements Repository<Role> {
    private final Map<String, Role> rolesById = new ConcurrentHashMap<>();
    private final Map<String, Role> rolesByName = new ConcurrentHashMap<>();

    private volatile Predicate<Role> hasAssignments = null;

    public void setHasAssignmentsChecker(Predicate<Role> checker) {
        this.hasAssignments = checker;
    }

    @Override
    public synchronized void add(Role role) {
        if (role == null) throw new IllegalArgumentException("Role cannot be null");
        if (rolesById.containsKey(role.getId()) || rolesByName.containsKey(role.getName())) {
            throw new IllegalArgumentException("Role already exists");
        }

        rolesById.put(role.getId(), role);
        rolesByName.put(role.getName(), role);
    }

    @Override
    public synchronized boolean remove(Role role) {
        if (role == null) return false;
        if (hasAssignments != null && hasAssignments.test(role)) {
            throw new IllegalStateException("Cannot remove role that is assigned to users");
        }

        rolesByName.remove(role.getName());
        return rolesById.remove(role.getId()) != null;
    }

    @Override
    public Optional<Role> findById(String id) {
        return Optional.ofNullable(rolesById.get(id));
    }

    @Override
    public List<Role> findAll() {
        return new ArrayList<>(rolesById.values());
    }

    @Override
    public int count() {
        return rolesById.size();
    }

    @Override
    public void clear() {
        rolesById.clear();
        rolesByName.clear();
    }

    public Optional<Role> findByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(rolesByName.get(name));
    }

    public List<Role> findByFilter(RoleFilter filter) {
        if (filter == null) return findAll();

        return rolesById.values().stream()
                .filter(filter::test)
                .collect(Collectors.toList());
    }

    public List<Role> findAll(RoleFilter filter, Comparator<Role> sorter) {
        List<Role> result = findByFilter(filter);

        if (sorter != null) {
            result.sort(sorter);
        } else {
            result.sort(Comparator.comparing(Role::getName));
        }

        return result;
    }

    public boolean exists(String name) {
        return name != null && rolesByName.containsKey(name);
    }

    public void addPermissionToRole(String roleName, Permission permission) {
        Role role = rolesByName.get(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Role not found: " + roleName);
        }
        if (permission == null) {
            throw new IllegalArgumentException("Permission cannot be null");
        }
        role.addPermission(permission);
    }

    public void removePermissionFromRole(String roleName, Permission permission) {
        Role role = rolesByName.get(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Role not found: " + roleName);
        }
        if (permission == null) {
            throw new IllegalArgumentException("Permission cannot be null");
        }
        role.removePermission(permission);
    }

    public List<Role> findRolesWithPermission(String permissionName, String resource) {
        if (permissionName == null || resource == null) {
            return Collections.emptyList();
        }

        return rolesById.values().stream()
                .filter(role -> role.hasPermission(permissionName, resource))
                .collect(Collectors.toList());
    }
}