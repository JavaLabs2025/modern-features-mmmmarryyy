package org.lab.model.ids;

public value class BugId {
    private int value;

    public BugId(int value) {
        this.value = value;
    }

    public int id() { return value; }
}
