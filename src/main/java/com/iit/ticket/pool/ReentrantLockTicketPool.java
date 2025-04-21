package com.iit.ticket.pool;

import com.iit.ticket.model.Ticket;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTicketPool implements TicketPool {

    private final Queue<Ticket> queue = new LinkedList<>();
    private final ReentrantLock lock = new ReentrantLock();
    Condition queueEmpty = lock.newCondition();
    Condition queueFull = lock.newCondition();
    private int maxNumberOfTickets = 0;
    private int soldTickets;
    private int totalTickets;

    public ReentrantLockTicketPool(int maxNumberOfTickets) {
        this.maxNumberOfTickets = maxNumberOfTickets;
    }

    @Override
    public void addTicket(Ticket ticket) {
        try {
            lock.lock();
            while (queue.size() == maxNumberOfTickets) {
                queueFull.await();
            }
            boolean offer = queue.offer(ticket);
            if (offer) {
                totalTickets++;
                queueEmpty.signalAll();
            }
        } catch (InterruptedException e) {
            System.out.println("interrupted (addTicket)");
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Ticket purchaseTicket() {
        try {
            lock.lock();
            while (queue.isEmpty()) {
                queueEmpty.await();
            }
            Ticket ticket = queue.poll();
            if (ticket != null) {
                soldTickets++;
                queueFull.signalAll();
            }
            return ticket;
        } catch (InterruptedException e) {
            System.out.println("interrupted (purchaseTicket)");
        } finally {
            lock.unlock();
        }
        return null;
    }

    @Override
    public int getAvailableTickets() {
        try {
            lock.lock();
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getSoldTickets() {
        try {
            lock.lock();
            return soldTickets;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getTotalTickets() {
        try {
            lock.lock();
            return totalTickets;
        } finally {
            lock.unlock();
        }
    }
}
