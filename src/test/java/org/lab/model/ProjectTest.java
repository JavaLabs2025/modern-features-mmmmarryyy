package org.lab.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lab.model.ids.BugId;
import org.lab.model.ids.ProjectId;
import org.lab.model.ids.TicketId;
import org.lab.model.ids.UserId;
import org.lab.model.role.*;
import org.lab.service.UserService;
import org.lab.service.DashboardService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectTest {

    private UserService userService;
    private ManagerRole managerRole;
    private Project project;

    @BeforeEach
    void setup() {
        userService = new UserService();
        var managerUser = userService.register("Alice");
        managerRole = new ManagerRole(managerUser);
        project = new Project(new ProjectId(1), "Test Project", managerRole, userService);
    }

    @Test
    void testAddDeveloperAndMembership() {
        var devUser = userService.register("Bob");
        var devRole = new DeveloperRole(devUser);

        project.addDeveloper(managerRole, devRole);

        assertTrue(project.milestones().isEmpty());
        assertTrue(userService.projectsOf(devUser.id()).contains(project));
    }

    @Test
    void testAddTeamLeadAndMembership() {
        var tlUser = userService.register("Charlie");
        var tlRole = new TeamLeadRole(tlUser);

        project.assignTeamLead(managerRole, tlRole);

        assertTrue(userService.projectsOf(tlUser.id()).contains(project));
    }

    @Test
    void testAddTesterMembership() {
        var testerUser = userService.register("Dana");
        var testerRole = new TesterRole(testerUser);
        project.addTester(managerRole, testerRole);

        assertTrue(userService.projectsOf(testerUser.id()).contains(project));
        assertThrows(IllegalStateException.class, () -> project.addTester(new DeveloperRole(testerUser), testerRole));
    }

    @Test
    void testCreateAndActivateMilestone() {
        var milestone1 = new Milestone("MVP", LocalDate.now(), LocalDate.now().plusDays(30));
        var milestone2 = new Milestone("Beta", LocalDate.now().plusDays(31), LocalDate.now().plusDays(60));

        project.createMilestone(managerRole, milestone1);
        project.activateMilestone(managerRole, milestone1);

        assertEquals(milestone1, project.activeMilestone);

        project.createMilestone(managerRole, milestone2);
        assertThrows(IllegalStateException.class, () -> project.activateMilestone(managerRole, milestone2));

        project.closeMilestone(managerRole, milestone1);
        project.activateMilestone(managerRole, milestone2);

        assertEquals(milestone2, project.activeMilestone);
    }

    @Test
    void testMilestoneCannotCloseIfTicketsOpen() {
        var milestone = new Milestone("MVP", LocalDate.now(), LocalDate.now().plusDays(30));
        project.createMilestone(managerRole, milestone);
        project.activateMilestone(managerRole, milestone);

        var devUser = userService.register("Dev");
        var devRole = new DeveloperRole(devUser);
        project.addDeveloper(managerRole, devRole);

        var ticket = new Ticket(new TicketId(1), project, milestone);
        project.createTicket(managerRole, ticket);
        ticket.assignDeveloper(devRole);

        assertThrows(IllegalStateException.class, milestone::close);
    }

    @Test
    void testCreateTicketAndAssignDeveloper() {
        var milestone = new Milestone("MVP", LocalDate.now(), LocalDate.now().plusDays(30));
        project.createMilestone(managerRole, milestone);
        project.activateMilestone(managerRole, milestone);

        var tlUser = userService.register("TeamLead");
        var tlRole = new TeamLeadRole(tlUser);
        project.assignTeamLead(managerRole, tlRole);

        var devUser = userService.register("Dev");
        var devRole = new DeveloperRole(devUser);
        project.addDeveloper(managerRole, devRole);

        var ticket = new Ticket(new TicketId(1), project, milestone);

        project.createTicket(managerRole, ticket);
        var ticket2 = new Ticket(new TicketId(2), project, milestone);
        project.createTicket(tlRole, ticket2);

        project.assignDeveloperToTicket(managerRole, ticket, devRole);
        project.assignDeveloperToTicket(tlRole, ticket2, devRole);

        assertEquals(devRole, ticket.assignedDeveloper());
        assertEquals(devRole, ticket2.assignedDeveloper());

        assertThrows(IllegalStateException.class, () -> project.createTicket(devRole, new Ticket(new TicketId(3), project, milestone)));
        assertThrows(IllegalStateException.class, () -> project.assignDeveloperToTicket(devRole, ticket, devRole));
    }

    @Test
    void testDeveloperCompletesTicket() {
        var milestone = new Milestone("MVP", LocalDate.now(), LocalDate.now().plusDays(30));
        project.createMilestone(managerRole, milestone);
        project.activateMilestone(managerRole, milestone);

        var devUser = userService.register("Dev");
        var devRole = new DeveloperRole(devUser);
        project.addDeveloper(managerRole, devRole);

        var ticket = new Ticket(new TicketId(1), project, milestone);
        project.createTicket(managerRole, ticket);
        project.assignDeveloperToTicket(managerRole, ticket, devRole);

        devRole.completeTicket(ticket);

        assertEquals(TicketStatus.DONE, ticket.status());
    }

    @Test
    void testBugCreationAndFixing() {
        var milestone = new Milestone("MVP", LocalDate.now(), LocalDate.now().plusDays(30));
        project.createMilestone(managerRole, milestone);
        project.activateMilestone(managerRole, milestone);

        var devUser = userService.register("Dev");
        var devRole = new DeveloperRole(devUser);
        project.addDeveloper(managerRole, devRole);

        var testerUser = userService.register("Tester");
        var testerRole = new TesterRole(testerUser);
        project.addTester(managerRole, testerRole);

        var bug1 = new Bug(new BugId(1), project, "Bug 1");
        var bug2 = new Bug(new BugId(2), project, "Bug 2");

        project.addBug(bug1);
        project.addBug(bug2);

        devRole.fixBug(bug1);
        assertEquals(BugStatus.FIXED, bug1.status());

        testerRole.testBug(bug1);
        assertEquals(BugStatus.TESTED, bug1.status());

        testerRole.closeBug(bug1);
        testerRole.closeBug(bug2);
        assertEquals(BugStatus.CLOSED, bug1.status());
        assertEquals(BugStatus.CLOSED, bug2.status());
    }

    @Test
    void testDashboardServiceGather() {
        var devUser = userService.register("Dev");
        var devRole = new DeveloperRole(devUser);
        project.addDeveloper(managerRole, devRole);

        var milestone = new Milestone("MVP", LocalDate.now(), LocalDate.now().plusDays(30));
        project.createMilestone(managerRole, milestone);
        project.activateMilestone(managerRole, milestone);

        var ticket = new Ticket(new TicketId(1), project, milestone);
        project.createTicket(managerRole, ticket);
        ticket.assignDeveloper(devRole);

        var bug = new Bug(new BugId(1), project, "Bug 1");
        project.addBug(bug);

        var dashboardService = new DashboardService();
        var snapshot = dashboardService.snapshot(project);

        assertEquals(1, snapshot.openTickets());
        assertEquals(1, snapshot.openBugs());
    }
}
