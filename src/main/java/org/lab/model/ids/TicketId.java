package org.lab.model.ids;

public value class TicketId {
    private int value;

    public TicketId(int value) {
        this.value = value;
    }

    public int id() { return value; }
}
