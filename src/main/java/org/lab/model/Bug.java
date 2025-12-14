package org.lab.model;

import org.lab.model.ids.BugId;

public class Bug {
    private final BugId id;
    private final Project project;
    private final String description;
    private BugStatus status;

    public Bug(BugId id, Project project, String description) {
        this.id = id;
        this.project = project;
        this.description = description;
        this.status = BugStatus.NEW;
    }

    public Bug withStatus(BugStatus newStatus) {
        this.status = newStatus;
        return this;
    }

    public BugStatus status() {
        return status;
    }
}
