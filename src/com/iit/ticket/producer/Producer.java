package com.iit.ticket.producer;

import com.iit.ticket.model.Ticket;
import com.iit.ticket.pool.TicketPool;
import com.iit.ticket.util.PoolEntity;
import com.iit.ticket.util.UtilMethods;

import java.util.Random;

public class Producer implements PoolEntity {

    private final TicketPool ticketPool;
    private final int id;
    private int rate;
    private volatile boolean running;
    Random random = new Random();

    public Producer(TicketPool ticketPool, int id) {
        this.ticketPool = ticketPool;
        this.id = id;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            while (running) {
                Ticket ticket = new Ticket(random.nextInt(1000), random.nextDouble(5000.00f));
                ticketPool.addTicket(ticket);
                UtilMethods.debug("Ticket Producer "+ id + " added a Ticket. No: " + ticket.getTicketId());
                if (rate > 0){
                    Thread.sleep(1000/rate);
                }else {
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().isInterrupted();
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public String getName() {
        return id+"";
    }

    @Override
    public void stop() {
        this.running = false;
    }
}
