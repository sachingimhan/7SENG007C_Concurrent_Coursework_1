package com.iit.ticket.reader;

import com.iit.ticket.pool.TicketPool;
import com.iit.ticket.util.PoolEntity;
import com.iit.ticket.util.UtilMethods;

public class Reader implements PoolEntity {

    private final TicketPool ticketPool;
    private final int readerId;
    private int rate;
    private volatile boolean running;

    public Reader(TicketPool ticketPool, int readerId) {
        this.ticketPool = ticketPool;
        this.readerId = readerId;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            while (running) {
                StringBuilder builder = new StringBuilder();
                builder.append("Reader ")
                        .append(readerId)
                        .append(" - Total Number of Tickets ")
                        .append(ticketPool.getTotalTickets())
                        .append("\n")
                        .append("Reader ")
                        .append(readerId)
                        .append(" - Total Number of Available Tickets ")
                        .append(ticketPool.getAvailableTickets())
                        .append("\n")
                        .append("Reader ")
                        .append(readerId)
                        .append(" - Total Number of Sold Tickets ")
                        .append(ticketPool.getSoldTickets());
                UtilMethods.debug(builder.toString());
                if (rate>0){
                    Thread.sleep(1000 / rate);
                }else {
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Reader " + readerId + " interrupted.");
        }
    }

    @Override
    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public String getName() {
        return readerId+"";
    }

    @Override
    public void stop() {
        this.running = false;
    }
}
