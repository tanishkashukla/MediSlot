package com.medislot.model;

/**
 * Priority Level for Appointment and Waiting-List Management.
 * 
 * Note: Per project requirements, Urgent priority provides higher
 * administrative priority for scheduling and waiting list slot offers,
 * and does not override existing confirmed bookings or act as clinical emergency triage.
 */
public enum PriorityLevel {
    URGENT(1, "Urgent Priority", "Higher scheduling queue precedence"),
    NORMAL(2, "Normal Priority", "Standard scheduling precedence");

    private final int rank;
    private final String title;
    private final String description;

    PriorityLevel(int rank, String title, String description) {
        this.rank = rank;
        this.title = title;
        this.description = description;
    }

    public int getRank() {
        return rank;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
