package com.iit.ticket.pool;

import com.iit.ticket.model.Ticket;

public interface TicketPool {

    void addTicket(Ticket ticket);
    Ticket purchaseTicket();
    int getAvailableTickets();
    int getSoldTickets();
    int getTotalTickets();

}
