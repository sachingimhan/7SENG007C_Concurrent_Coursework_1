package com.iit.ticket.pool;

import com.iit.ticket.model.Ticket;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueTicketPool implements TicketPool {

    private final BlockingQueue<Ticket> queue;
    private final int maxNumberOfTickets;
    private int soldTickets;
    private int totalTickets;

    public BlockingQueueTicketPool(int maxNumberOfTickets) {
        this.maxNumberOfTickets = maxNumberOfTickets;
        queue = new LinkedBlockingQueue<>(maxNumberOfTickets);
    }

    @Override
    public void addTicket(Ticket ticket) {
        boolean offer = queue.offer(ticket);
        if (offer) totalTickets++;
    }

    @Override
    public Ticket purchaseTicket() {
        Ticket ticket = queue.poll();
        if (ticket != null) {
            soldTickets++;
        }
        return ticket;
    }

    @Override
    public synchronized int getAvailableTickets() {
        return queue.size();
    }

    @Override
    public synchronized int getSoldTickets() {
        return soldTickets;
    }

    @Override
    public synchronized int getTotalTickets() {
        return totalTickets;
    }
}
