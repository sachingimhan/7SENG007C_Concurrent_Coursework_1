package com.iit.ticket.pool;

import com.iit.ticket.model.Ticket;

import java.util.LinkedList;
import java.util.Queue;

public class SynchronizedTicketPool implements TicketPool {

    private final Queue<Ticket> queue = new LinkedList<>();
    private int maxNumberOfTickets = 0;
    private int soldTickets;
    private int totalTickets;

    public SynchronizedTicketPool(int maxNumberOfTickets) {
        this.maxNumberOfTickets = maxNumberOfTickets;
    }

    @Override
    public synchronized void addTicket(Ticket ticket) {
        while (queue.size() >= maxNumberOfTickets) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        boolean offer = queue.offer(ticket);
        if (offer) {
            totalTickets++;
            notifyAll();
        }
    }

    @Override
    public synchronized Ticket purchaseTicket() {
        while (queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Ticket ticket = queue.poll();
        if (ticket != null) {
            soldTickets++;
            notifyAll();
        }
        return ticket;
    }

    @Override
    public synchronized int getAvailableTickets() {
        return queue.size();
    }

    @Override
    public synchronized int getSoldTickets() {
        return this.soldTickets;
    }

    @Override
    public synchronized int getTotalTickets() {
        return this.totalTickets;
    }
}
