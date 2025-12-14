package org.lab.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class Milestone {

    private final String name;
    private final LocalDate start;
    private final LocalDate end;

    private MilestoneStatus status = MilestoneStatus.OPEN;
    private final List<Ticket> tickets = new ArrayList<>();

    public Milestone(String name, LocalDate start, LocalDate end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }

    public void activate() {
        status = MilestoneStatus.ACTIVE;
    }

    public void close() {
        boolean allDone = tickets.stream()
                .allMatch(t -> t.status() == TicketStatus.DONE);

        if (!allDone) {
            throw new IllegalStateException("Not all tickets are done");
        }

        status = MilestoneStatus.CLOSED;
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    public List<Ticket> tickets() {
        return List.copyOf(tickets);
    }

    public MilestoneStatus status() {
        return status;
    }
}