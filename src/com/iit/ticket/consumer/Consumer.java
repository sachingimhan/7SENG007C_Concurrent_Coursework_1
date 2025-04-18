package com.iit.ticket.consumer;

import com.iit.ticket.model.Ticket;
import com.iit.ticket.pool.TicketPool;
import com.iit.ticket.util.PoolEntity;
import com.iit.ticket.util.UtilMethods;

import java.util.Random;

public class Consumer implements PoolEntity {

    private final TicketPool ticketPool;
    private final int consumerId;
    private int rate;
    private volatile boolean running;

    public Consumer(TicketPool ticketPool, int consumerId) {
        this.ticketPool = ticketPool;
        this.consumerId = consumerId;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            while (this.running) {
                Ticket ticket = ticketPool.purchaseTicket();
                if (ticket != null) {
                    UtilMethods.debug("Consumer " + consumerId + " purchased: " + ticket);
                }
                if (rate > 0){
                    Thread.sleep(1000 / rate);
                }else {
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public String getName() {
        return consumerId+"";
    }

    @Override
    public void stop() {
        this.running = false;
    }
}
