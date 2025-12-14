package org.lab.model;

import org.lab.model.ids.TicketId;
import org.lab.model.role.DeveloperRole;

public final class Ticket {

    private final TicketId id;
    private final Project project;
    private final Milestone milestone;

    private DeveloperRole assignee;
    private TicketStatus status = TicketStatus.NEW;

    public Ticket(TicketId id, Project project, Milestone milestone) {
        this.id = id;
        this.project = project;
        this.milestone = milestone;
    }

    public DeveloperRole assignedDeveloper() {
        return assignee;
    }

    public void assignDeveloper(DeveloperRole dev) {
        this.assignee = dev;
        status = TicketStatus.ACCEPTED;
    }

    public void start() {
        status = TicketStatus.IN_PROGRESS;
    }

    public void complete() {
        status = TicketStatus.DONE;
    }

    public TicketStatus status() {
        return status;
    }

    public DeveloperRole assignee() {
        return assignee;
    }

    public TicketId id() {
        return id;
    }
}