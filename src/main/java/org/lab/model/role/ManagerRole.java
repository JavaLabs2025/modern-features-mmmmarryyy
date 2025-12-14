package org.lab.model.role;

import org.lab.model.User;

public final class ManagerRole extends Role {

    public ManagerRole(User user) {
        IO.println("Assigning MANAGER role");
        super(user);
    }
}
