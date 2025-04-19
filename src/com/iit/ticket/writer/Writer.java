package com.iit.ticket.writer;

import com.iit.ticket.model.Ticket;
import com.iit.ticket.pool.TicketPool;
import com.iit.ticket.util.PoolEntity;
import com.iit.ticket.util.UtilMethods;

import java.util.Random;

public class Writer implements PoolEntity {

    private final TicketPool ticketPool;
    private final int writerId;
    private int rate;
    private volatile boolean running;
    private final Random random = new Random();

    public Writer(TicketPool ticketPool, int writerId) {
        this.ticketPool = ticketPool;
        this.writerId = writerId;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            while (running) {
                // System.out.println("Updating ticket pool.......");
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
