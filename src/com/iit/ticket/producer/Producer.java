package com.iit.ticket.producer;

import com.iit.ticket.model.Ticket;
import com.iit.ticket.pool.TicketPool;
import com.iit.ticket.util.PoolEntity;

import java.util.Random;

public class Producer implements PoolEntity {

    private final TicketPool ticketPool;
    private final int id;
    Random random = new Random();
    private int rate;
    private volatile boolean running;

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
//                UtilMethods.debug("Ticket Producer "+ id + " added a Ticket. No: " + ticket.getTicketId());
                if (rate > 0) {
                    Thread.sleep(1000 / rate);
                } else {
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Producer " + id + " thread interrupted");
        }

    }

    @Override
    public int getRate() {
        return this.rate;
    }

    @Override
    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public String getName() {
        return id + "";
    }

    @Override
    public void stop() {
        this.running = false;
    }
}
