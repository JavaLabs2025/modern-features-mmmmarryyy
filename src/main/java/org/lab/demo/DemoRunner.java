package org.lab.demo;

import org.lab.model.*;
import org.lab.model.ids.ProjectId;
import org.lab.model.ids.TicketId;
import org.lab.model.role.ManagerRole;
import org.lab.service.DashboardService;
import org.lab.model.role.DeveloperRole;
import org.lab.model.role.TesterRole;
import org.lab.service.UserService;

import java.time.LocalDate;

public class DemoRunner {
    public static void runDemo() throws Exception {
        UserService users = new UserService();

        var managerUser = users.register("Manager");
        var devUser = users.register("Developer");
        var testerUser = users.register("Tester");

        var manager = new ManagerRole(managerUser);
        var developer = new DeveloperRole(devUser);
        var tester = new TesterRole(testerUser);

        var project = new Project(
                new ProjectId(1),
                "Modern Java",
                manager,
                users
        );

        project.addDeveloper(manager, developer);
        project.addTester(manager, tester);

        var milestone = project.createMilestone(
                manager,
                new Milestone(
                        "MVP",
                        LocalDate.now(),
                        LocalDate.now().plusDays(30)
                )
        );

        project.activateMilestone(manager, milestone);

        var ticket = project.createTicket(
                manager,
                new Ticket(
                        new TicketId(100),
                        project,
                        milestone
                )
        );

        project.assignDeveloperToTicket(manager, ticket, developer);

        developer.startTicket(ticket);
        developer.completeTicket(ticket);

        var bug = tester.createBug(project, "UI glitch");
        var fixed = developer.fixBug(bug);
        var tested = tester.testBug(fixed);
        var closed = tester.closeBug(tested);

        IO.println("Bug final status: " + closed.status());

        var dashboard = new DashboardService();
        var snapshot = dashboard.snapshot(project);

        IO.println("Open tickets: " + snapshot.openTickets());
        IO.println("Open bugs: " + snapshot.openBugs());
    }
}

