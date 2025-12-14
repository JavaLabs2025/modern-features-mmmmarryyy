package org.lab.service;

import org.lab.model.Project;
import org.lab.model.User;
import org.lab.model.ids.UserId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UserService {

    private final List<User> users = new ArrayList<>();
    private final Map<UserId, List<Project>> memberships = new HashMap<>();

    public User register(String name) {
        var user = new User(new UserId(users.size()), name);
        users.add(user);
        memberships.put(user.id(), new ArrayList<>());
        return user;
    }

    public void addToProject(UserId userId, Project project) {
        memberships.get(userId).add(project);
    }

    public List<Project> projectsOf(UserId userId) {
        return List.copyOf(memberships.get(userId));
    }
}
