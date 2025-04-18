package com.iit.ticket;

import com.iit.ticket.consumer.Consumer;
import com.iit.ticket.pool.BlockingQueueTicketPool;
import com.iit.ticket.pool.ReentrantLockTicketPool;
import com.iit.ticket.pool.SynchronizedTicketPool;
import com.iit.ticket.pool.TicketPool;
import com.iit.ticket.producer.Producer;
import com.iit.ticket.reader.Reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimulationManager {

    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Thread> threads = new ArrayList<>();
    private static final List<Producer> producers = new ArrayList<>();
    private static final List<Consumer> consumers = new ArrayList<>();
    private static final List<Reader> readers = new ArrayList<>();
    private static TicketPool ticketPool;
    private static AtomicBoolean isRunning;


    public static void main(String[] args) {


    }

    public static void initPoolType() {

        System.out.print("Enter Pool Type: ");
        int poolType = scanner.nextInt();

        System.out.print("Enter Pool Capacity: ");
        int poolCapacity = scanner.nextInt();

        switch (poolType) {
            case 1:
                ticketPool = new SynchronizedTicketPool(poolCapacity);
                break;
            case 2:
                ticketPool = new ReentrantLockTicketPool(poolCapacity);
                break;
            case 3:
                ticketPool = new BlockingQueueTicketPool(poolCapacity);
        }

        showMenu();

    }

    public static void showMenu(){
        System.out.println("Ticket Pool System....");
        System.out.println("1. Consumer Menu");
        System.out.println("2. Producer Menu");
        System.out.println("3. Reader Menu");
        System.out.println("4. Show Ticket Pool Status");
        System.out.println("5. Exit");

        System.out.print("Enter your choice: ");
        int option = scanner.nextInt();


    }

}
