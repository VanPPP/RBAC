package com.rbac.model;

import java.util.*;
import java.util.stream.Collectors;

public class UserManager implements Repository<User> {
    private final Map<String, User> users = new HashMap<>();

    @Override
    public void add(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (users.containsKey(user.username())) {
            throw new IllegalArgumentException("User with username '" + user.username() + "' already exists");
        }
        // Переиспользуем валидацию из User.create()
        User validated = User.create(user.username(), user.fullName(), user.email());
        users.put(validated.username(), validated);
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
        if (!users.containsKey(username)) {
            throw new IllegalArgumentException("User with username '" + username + "' not found");
        }
        User validated = User.create(username, newFullName, newEmail);
        users.put(username, validated);
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