package org.lab.model.role;

import org.lab.model.*;
import org.lab.model.ids.BugId;

public non-sealed class DeveloperRole extends Role {

    public DeveloperRole(User user) {
        IO.println("Assigning DEVELOPER role");
        super(user);
    }

    public void startTicket(Ticket ticket) {
        ticket.start();
    }

    public void completeTicket(Ticket ticket) {
        ticket.complete();
    }

    public Bug createBug(Project project, String desc) {
        var bug = new Bug(
                new BugId(desc.hashCode()),
                project,
                desc
        );
        project.addBug(bug);
        return bug;
    }

    public Bug fixBug(Bug bug) {
        bug = bug.withStatus(BugStatus.FIXED);
        return bug;
    }
}
