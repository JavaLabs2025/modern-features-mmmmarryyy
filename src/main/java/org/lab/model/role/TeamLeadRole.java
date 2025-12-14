package org.lab.model.role;

import org.lab.model.Ticket;
import org.lab.model.TicketStatus;
import org.lab.model.User;

public final class TeamLeadRole extends Role {

    public TeamLeadRole(User user) {
        IO.println("Assigning TEAM LEAD role");
        super(user);
    }

    public void verifyTicket(Ticket ticket) {
        if (ticket.status() == TicketStatus.DONE) {
            IO.println("End working on ticket " + ticket.id());
        }
    }
}
