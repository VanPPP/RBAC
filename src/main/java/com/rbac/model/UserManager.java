package com.rbac.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserManager implements Repository<User> {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public void add(User user) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");

        User validated = User.create(user.username(), user.fullName(), user.email());
        if (users.putIfAbsent(validated.username(), validated) != null) {
            throw new IllegalArgumentException("User with username '" + user.username() + "' already exists");
        }
    }

    @Override
    public boolean remove(User user) {
        if (user == null) return false;
        return users.remove(user.username(), user);
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public int count() {
        return users.size();
    }

    @Override
    public void clear() {
        users.clear();
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return users.values().stream()
                .filter(user -> user.email().equalsIgnoreCase(email))
                .findFirst();
    }

    public List<User> findByFilter(UserFilter filter) {
        if (filter == null) return findAll();
        return users.values().stream()
                .filter(filter::test)
                .collect(Collectors.toList());
    }

    public List<User> findAll(UserFilter filter, Comparator<User> sorter) {
        List<User> result = findByFilter(filter);

        if (sorter != null) {
            result.sort(sorter);
        } else {
            result.sort(Comparator.comparing(User::username));
        }

        return result;
    }

    public boolean exists(String username) {
        return users.containsKey(username);
    }

    public void update(String username, String newFullName, String newEmail) {
        // Метод computeIfPresent гарантирует атомарное обновление
        users.computeIfPresent(username, (k, v) -> User.create(username, newFullName, newEmail));
        if (!users.containsKey(username)) {
            throw new IllegalArgumentException("User with username '" + username + "' not found");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserManager that = (UserManager) o;
        return Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users);
    }
}