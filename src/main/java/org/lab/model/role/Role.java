package org.lab.model.role;

import org.lab.model.User;

public sealed abstract class Role permits ManagerRole, TeamLeadRole, DeveloperRole, TesterRole {

    protected final User user;

    protected Role(User user) {
        this.user = user;
    }

    public User user() {
        return user;
    }
}