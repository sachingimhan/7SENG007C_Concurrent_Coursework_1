package com.iit.ticket.model;

public class Ticket {

    private int ticketId;;
    private double ticketPrice;

    public Ticket() {
    }

    public Ticket(int ticketId, double ticketPrice) {
        this.ticketId = ticketId;
        this.ticketPrice = ticketPrice;
    }

    public int getTicketId() {
        return ticketId;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", ticketPrice=" + ticketPrice +
                '}';
    }
}
