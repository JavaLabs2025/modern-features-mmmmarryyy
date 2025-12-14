package org.lab.model.role;

import org.lab.model.*;
import org.lab.model.ids.BugId;

public final class TesterRole extends Role {

    public TesterRole(User user) {
        IO.println("Assigning TESTER role");
        super(user);
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

    public Bug testBug(Bug bug) {
        return bug.withStatus(BugStatus.TESTED);
    }

    public Bug closeBug(Bug bug) {
        return bug.withStatus(BugStatus.CLOSED);
    }
}
