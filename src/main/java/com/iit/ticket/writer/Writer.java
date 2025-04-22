package com.iit.ticket.writer;

import com.iit.ticket.model.Ticket;
import com.iit.ticket.pool.TicketPool;
import com.iit.ticket.util.PoolEntity;

import java.util.Random;

public class Writer implements PoolEntity {

    private final TicketPool ticketPool;
    private final int writerId;
    private final Random random = new Random();
    private final boolean running;
    private int rate;

    public Writer(TicketPool ticketPool, int writerId) {
        this.ticketPool = ticketPool;
        this.writerId = writerId;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            while (running) {

                Ticket ticket = ticketPool.purchaseTicket();
                if (ticket != null) {
                    // chnage the price
                    ticket.setTicketPrice(random.nextDouble());
                    ticketPool.addTicket(ticket);
                }

                if (rate > 0) {
                    Thread.sleep(1000 / rate);
                } else {
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Writer " + writerId + " interrupted.");
        }
    }

    @Override
    public int getRate() {
        return 0;
    }

    @Override
    public void setRate(int rate) {

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void stop() {

    }
}
