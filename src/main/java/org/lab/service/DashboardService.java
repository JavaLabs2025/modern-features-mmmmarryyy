package org.lab.service;

import org.lab.model.*;

import java.util.stream.Gatherers;

public final class DashboardService {

    public record Snapshot(int openTickets, int openBugs) {}

    public Snapshot snapshot(Project project) {

        int openTickets = project.milestones().stream()
                .flatMap(m -> m.tickets().stream())
                .gather(Gatherers.fold(
                        () -> 0,
                        (count, t) -> switch (t) {
                            case Ticket tt when tt.status() != TicketStatus.DONE -> count + 1;
                            case Ticket _ -> count;
                        }
                ))
                .findFirst()
                .orElse(0);

        int openBugs = project.bugs().stream()
                .gather(Gatherers.fold(
                        () -> 0,
                        (count, b) -> switch (b) {
                            case Bug bb when bb.status() != BugStatus.CLOSED -> count + 1;
                            case Bug _ -> count;
                        }
                ))
                .findFirst()
                .orElse(0);

        return new Snapshot(openTickets, openBugs);
    }
}
