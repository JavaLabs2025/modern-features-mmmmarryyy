package org.lab.model;

import org.lab.model.ids.ProjectId;
import org.lab.model.role.*;
import org.lab.service.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Project {

    private final ProjectId id;
    private final String name;

    private final ManagerRole manager;
    private TeamLeadRole teamLead;

    private final Set<DeveloperRole> developers = new HashSet<>();
    private final Set<TesterRole> testers = new HashSet<>();

    private final List<Milestone> milestones = new ArrayList<>();
    Milestone activeMilestone;

    private final List<Bug> bugs = new ArrayList<>();

    private final UserService userService;

    public Project(ProjectId id, String name, ManagerRole manager, UserService userService) {
        this.id = id;
        this.name = name;
        this.manager = manager;
        this.userService = userService;
        userService.addToProject(manager.user().id(), this);
    }

    public void assignTeamLead(Role actor, TeamLeadRole lead) {
        switch (actor) {
            case ManagerRole m -> {
                this.teamLead = lead;
                userService.addToProject(lead.user().id(), this);
                IO.println("Manager add new teamLead to project " + id.id());
            }
            default -> throw new IllegalStateException("Only manager can assign team lead");
        }
    }

    public void addDeveloper(Role actor, DeveloperRole dev) {
        switch (actor) {
            case ManagerRole m -> {
                developers.add(dev);
                userService.addToProject(dev.user().id(), this);
                IO.println("Manager add new developer to project " + id.id());
            }
            default -> throw new IllegalStateException("Only manager can add developer");
        }
    }

    public void addTester(Role actor, TesterRole tester) {
        switch (actor) {
            case ManagerRole m -> {
                testers.add(tester);
                userService.addToProject(tester.user().id(), this);
                IO.println("Manager add new tester to project " + id.id());
            }
            default -> throw new IllegalStateException("Only manager can add tester");
        }
    }

    public Milestone createMilestone(Role actor, Milestone milestone) {
        switch (actor) {
            case ManagerRole m -> {
                milestones.add(milestone);
                IO.println("Manager add new milestone to project " + id.id());
                return milestone;
            }
            default -> throw new IllegalStateException("Only manager can create milestone");
        }
    }

    public void activateMilestone(Role actor, Milestone milestone) {
        switch (actor) {
            case ManagerRole m -> {
                if (activeMilestone != null) {
                    throw new IllegalStateException("Only one active milestone allowed");
                }
                milestone.activate();
                activeMilestone = milestone;
                IO.println("Manager change activateMilestone for project " + id.id());
            }
            default -> throw new IllegalStateException("Only manager can activate milestone");
        }
    }

    public void closeMilestone(Role actor, Milestone milestone) {
        switch (actor) {
            case ManagerRole _ -> {
                milestone.close();
                activeMilestone = null;
            }
            default -> throw new IllegalStateException("Only manager can close milestone");
        }
    }

    public Ticket createTicket(Role actor, Ticket ticket) {
        switch (actor) {
            case ManagerRole _, TeamLeadRole _ -> {
                activeMilestone.addTicket(ticket);
                IO.println("New ticket was added for active milestone in project " + id.id());
                return ticket;
            }
            default -> throw new IllegalStateException("No rights to create ticket");
        }
    }

    public void assignDeveloperToTicket(Role actor, Ticket ticket, DeveloperRole dev) {
        switch (actor) {
            case ManagerRole _, TeamLeadRole _ -> {
                ticket.assignDeveloper(dev);
                IO.println("Ticket was assigned to developer " + dev.user().name() + " in project " + id.id());
            }
            default -> throw new IllegalStateException("No rights");
        }
    }

    public void addBug(Bug bug) {
        bugs.add(bug);
    }

    public List<Bug> bugs() {
        return List.copyOf(bugs);
    }

    public List<Milestone> milestones() {
        return List.copyOf(milestones);
    }
}
