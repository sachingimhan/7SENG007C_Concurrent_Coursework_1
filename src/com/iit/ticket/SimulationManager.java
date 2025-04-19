package com.iit.ticket;

import com.iit.ticket.consumer.Consumer;
import com.iit.ticket.pool.BlockingQueueTicketPool;
import com.iit.ticket.pool.ReentrantLockTicketPool;
import com.iit.ticket.pool.SynchronizedTicketPool;
import com.iit.ticket.pool.TicketPool;
import com.iit.ticket.producer.Producer;
import com.iit.ticket.reader.Reader;
import com.iit.ticket.writer.Writer;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimulationManager {

    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Thread> threads = new CopyOnWriteArrayList<>();
    private static final List<Producer> producers = new CopyOnWriteArrayList<>();
    private static final List<Consumer> consumers = new CopyOnWriteArrayList<>();
    private static final List<Reader> readers = new CopyOnWriteArrayList<>();
    private static final List<Writer> writers = new CopyOnWriteArrayList<>();
    private static final AtomicBoolean isRunning = new AtomicBoolean(true);
    private static TicketPool ticketPool;

    public static void main(String[] args) {
        System.out.println("Welcome to Ticket Pool Simulation System");
        System.out.println("----------------------------------------");
        System.out.println("Please select a pool type:");
        System.out.println("1. Synchronized Pool");
        System.out.println("2. ReentrantLock Pool");
        System.out.println("3. BlockingQueue Pool");

        initPoolType();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down simulation...");
            stopAllEntities();
        }));

    }

    public static void initPoolType() {
        System.out.print("Enter Pool Type (1-3): ");
        int poolType = scanner.nextInt();

        while (poolType < 1 || poolType > 3) {
            System.out.print("Invalid choice. Please enter a number between 1 and 3: ");
            poolType = scanner.nextInt();
        }

        System.out.print("Enter Pool Capacity: ");
        int poolCapacity = scanner.nextInt();

        while (poolCapacity <= 0) {
            System.out.print("Capacity must be greater than 0. Please enter a valid capacity: ");
            poolCapacity = scanner.nextInt();
        }

        switch (poolType) {
            case 1:
                ticketPool = new SynchronizedTicketPool(poolCapacity);
                System.out.println("Synchronized Pool initialized with capacity: " + poolCapacity);
                break;
            case 2:
                ticketPool = new ReentrantLockTicketPool(poolCapacity);
                System.out.println("ReentrantLock Pool initialized with capacity: " + poolCapacity);
                break;
            case 3:
                ticketPool = new BlockingQueueTicketPool(poolCapacity);
                System.out.println("BlockingQueue Pool initialized with capacity: " + poolCapacity);
                break;
        }

        showMenu();
    }

    public static void showMenu() {
        while (isRunning.get()) {
            System.out.println("\nTicket Pool System Menu");
            System.out.println("----------------------");
            System.out.println("1. Consumer Menu");
            System.out.println("2. Producer Menu");
            System.out.println("3. Reader Menu");
            System.out.println("4. Writer Menu");
            System.out.println("5. Show Ticket Pool Status");
            System.out.println("6. Exit");

            System.out.print("\nEnter your choice: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    consumerMenu();
                    break;
                case 2:
                    producerMenu();
                    break;
                case 3:
                    readerMenu();
                    break;
                case 4:
                    writerMenu();
                    break;
                case 5:
                    showPoolStatus();
                    break;
                case 6:
                    exit();
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private static void consumerMenu() {
        boolean backToMain = false;

        while (!backToMain) {
            System.out.println("\nConsumer Menu");
            System.out.println("------------");
            System.out.println("1. Add Consumer");
            System.out.println("2. Remove Consumer");
            System.out.println("3. List Consumers");
            System.out.println("4. Set Consumer Rate");
            System.out.println("5. Back to Main Menu");

            System.out.print("\nEnter your choice: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    addConsumer();
                    break;
                case 2:
                    removeConsumer();
                    break;
                case 3:
                    listConsumers();
                    break;
                case 4:
                    setConsumerRate();
                    break;
                case 5:
                    backToMain = true;
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private static void producerMenu() {
        boolean backToMain = false;

        while (!backToMain) {
            System.out.println("\nProducer Menu");
            System.out.println("------------");
            System.out.println("1. Add Producer");
            System.out.println("2. Remove Producer");
            System.out.println("3. List Producers");
            System.out.println("4. Set Producer Rate");
            System.out.println("5. Back to Main Menu");

            System.out.print("\nEnter your choice: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    addProducer();
                    break;
                case 2:
                    removeProducer();
                    break;
                case 3:
                    listProducers();
                    break;
                case 4:
                    setProducerRate();
                    break;
                case 5:
                    backToMain = true;
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private static void readerMenu() {
        boolean backToMain = false;

        while (!backToMain) {
            System.out.println("\nReader Menu");
            System.out.println("-----------");
            System.out.println("1. Add Reader");
            System.out.println("2. Remove Reader");
            System.out.println("3. List Readers");
            System.out.println("4. Set Reader Rate");
            System.out.println("5. Back to Main Menu");

            System.out.print("\nEnter your choice: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    addReader();
                    break;
                case 2:
                    removeReader();
                    break;
                case 3:
                    listReaders();
                    break;
                case 4:
                    setReaderRate();
                    break;
                case 5:
                    backToMain = true;
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private static void writerMenu() {
        boolean backToMain = false;

        while (!backToMain) {
            System.out.println("\nWriter Menu");
            System.out.println("-----------");
            System.out.println("1. Add Writer");
            System.out.println("2. Remove Writer");
            System.out.println("3. List Writers");
            System.out.println("4. Set Writer Rate");
            System.out.println("5. Back to Main Menu");

            System.out.print("\nEnter your choice: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    addWriter();
                    break;
                case 2:
                    removeWriter();
                    break;
                case 3:
                    listWriters();
                    break;
                case 4:
                    setWriterRate();
                    break;
                case 5:
                    backToMain = true;
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private static void addConsumer() {
        System.out.print("Enter consumer ID (number): ");
        int consumerId = scanner.nextInt();

        // Check if consumer with this ID already exists
        for (Consumer c : consumers) {
            if (Integer.parseInt(c.getName()) == consumerId) {
                System.out.println("Consumer with ID " + consumerId + " already exists!");
                return;
            }
        }

        System.out.print("Enter consumer rate (operations per second, 0 = default): ");
        int rate = scanner.nextInt();

        Consumer consumer = new Consumer(ticketPool, consumerId);
        consumer.setRate(rate);
        consumers.add(consumer);

        Thread thread = new Thread(consumer);
        threads.add(thread);
        thread.start();

        System.out.println("Consumer " + consumerId + " added successfully with rate: " + rate);
    }

    private static void removeConsumer() {
        if (consumers.isEmpty()) {
            System.out.println("No consumers to remove!");
            return;
        }

        System.out.println("Current Consumers:");
        listConsumers();

        System.out.print("Enter consumer ID to remove: ");
        int consumerId = scanner.nextInt();

        Consumer consumerToRemove = null;
        Thread threadToRemove = null;

        for (Consumer c : consumers) {
            if (Integer.parseInt(c.getName()) == consumerId) {
                consumerToRemove = c;
                break;
            }
        }

        if (consumerToRemove != null) {
            consumerToRemove.stop();
            consumers.remove(consumerToRemove);

            for (Thread t : threads) {
                if (t.getName().contains("Consumer-" + consumerId)) {
                    threadToRemove = t;
                    break;
                }
            }

            if (threadToRemove != null) {
                threadToRemove.interrupt();
                threads.remove(threadToRemove);
            }

            System.out.println("Consumer " + consumerId + " removed successfully.");
        } else {
            System.out.println("Consumer with ID " + consumerId + " not found!");
        }
    }

    private static void listConsumers() {
        if (consumers.isEmpty()) {
            System.out.println("No consumers available.");
            return;
        }

        System.out.println("Current Consumers:");
        System.out.println("ID\tState\tRate");
        System.out.println("--\t-----\t----");

        for (Consumer c : consumers) {
            System.out.println(c.getName() + "\tRunning\t" + c.getRate());
        }
    }

    private static void setConsumerRate() {
        if (consumers.isEmpty()) {
            System.out.println("No consumers available to set rate!");
            return;
        }

        System.out.println("Current Consumers:");
        listConsumers();

        System.out.print("Enter consumer ID to modify rate: ");
        int consumerId = scanner.nextInt();

        Consumer consumerToModify = null;

        for (Consumer c : consumers) {
            if (Integer.parseInt(c.getName()) == consumerId) {
                consumerToModify = c;
                break;
            }
        }

        if (consumerToModify != null) {
            System.out.print("Enter new rate (operations per second, 0 = default): ");
            int newRate = scanner.nextInt();
            consumerToModify.setRate(newRate);
            System.out.println("Consumer " + consumerId + " rate updated to " + newRate);
        } else {
            System.out.println("Consumer with ID " + consumerId + " not found!");
        }
    }

    private static void addProducer() {
        System.out.print("Enter producer ID (number): ");
        int producerId = scanner.nextInt();

        // Check if producer with this ID already exists
        for (Producer p : producers) {
            if (Integer.parseInt(p.getName()) == producerId) {
                System.out.println("Producer with ID " + producerId + " already exists!");
                return;
            }
        }

        System.out.print("Enter producer rate (operations per second, 0 = default): ");
        int rate = scanner.nextInt();

        Producer producer = new Producer(ticketPool, producerId);
        producer.setRate(rate);
        producers.add(producer);

        Thread thread = new Thread(producer, "Producer-" + producerId);
        threads.add(thread);
        thread.start();

        System.out.println("Producer " + producerId + " added successfully with rate: " + rate);
    }

    private static void removeProducer() {
        if (producers.isEmpty()) {
            System.out.println("No producers to remove!");
            return;
        }

        System.out.println("Current Producers:");
        listProducers();

        System.out.print("Enter producer ID to remove: ");
        int producerId = scanner.nextInt();

        Producer producerToRemove = null;
        Thread threadToRemove = null;

        for (Producer p : producers) {
            if (Integer.parseInt(p.getName()) == producerId) {
                producerToRemove = p;
                break;
            }
        }

        if (producerToRemove != null) {
            producerToRemove.stop();
            producers.remove(producerToRemove);

            for (Thread t : threads) {
                if (t.getName().equals("Producer-" + producerId)) {
                    threadToRemove = t;
                    break;
                }
            }

            if (threadToRemove != null) {
                threadToRemove.interrupt();
                threads.remove(threadToRemove);
            }

            System.out.println("Producer " + producerId + " removed successfully.");
        } else {
            System.out.println("Producer with ID " + producerId + " not found!");
        }
    }

    private static void listProducers() {
        if (producers.isEmpty()) {
            System.out.println("No producers available.");
            return;
        }

        System.out.println("Current Producers:");
        System.out.println("ID\tState\tRate");
        System.out.println("--\t-----\t----");

        for (Producer p : producers) {
            System.out.println(p.getName() + "\tRunning\t" + p.getRate());
        }
    }

    private static void setProducerRate() {
        if (producers.isEmpty()) {
            System.out.println("No producers available to set rate!");
            return;
        }

        System.out.println("Current Producers:");
        listProducers();

        System.out.print("Enter producer ID to modify rate: ");
        int producerId = scanner.nextInt();

        Producer producerToModify = null;

        for (Producer p : producers) {
            if (Integer.parseInt(p.getName()) == producerId) {
                producerToModify = p;
                break;
            }
        }

        if (producerToModify != null) {
            System.out.print("Enter new rate (operations per second, 0 = default): ");
            int newRate = scanner.nextInt();
            producerToModify.setRate(newRate);
            System.out.println("Producer " + producerId + " rate updated to " + newRate);
        } else {
            System.out.println("Producer with ID " + producerId + " not found!");
        }
    }

    private static void addReader() {
        System.out.print("Enter reader ID (number): ");
        int readerId = scanner.nextInt();

        // Check if reader with this ID already exists
        for (Reader r : readers) {
            if (Integer.parseInt(r.getName()) == readerId) {
                System.out.println("Reader with ID " + readerId + " already exists!");
                return;
            }
        }

        System.out.print("Enter reader rate (operations per second, 0 = default): ");
        int rate = scanner.nextInt();

        Reader reader = new Reader(ticketPool, readerId);
        reader.setRate(rate);
        readers.add(reader);

        Thread thread = new Thread(reader, "Reader-" + readerId);
        threads.add(thread);
        thread.start();

        System.out.println("Reader " + readerId + " added successfully with rate: " + rate);
    }

    private static void removeReader() {
        if (readers.isEmpty()) {
            System.out.println("No readers to remove!");
            return;
        }

        System.out.println("Current Readers:");
        listReaders();

        System.out.print("Enter reader ID to remove: ");
        int readerId = scanner.nextInt();

        Reader readerToRemove = null;
        Thread threadToRemove = null;

        for (Reader r : readers) {
            if (Integer.parseInt(r.getName()) == readerId) {
                readerToRemove = r;
                break;
            }
        }

        if (readerToRemove != null) {
            readerToRemove.stop();
            readers.remove(readerToRemove);

            for (Thread t : threads) {
                if (t.getName().equals("Reader-" + readerId)) {
                    threadToRemove = t;
                    break;
                }
            }

            if (threadToRemove != null) {
                threadToRemove.interrupt();
                threads.remove(threadToRemove);
            }

            System.out.println("Reader " + readerId + " removed successfully.");
        } else {
            System.out.println("Reader with ID " + readerId + " not found!");
        }
    }

    private static void listReaders() {
        if (readers.isEmpty()) {
            System.out.println("No readers available.");
            return;
        }

        System.out.println("Current Readers:");
        System.out.println("ID\tState\tRate");
        System.out.println("--\t-----\t----");

        for (Reader r : readers) {
            System.out.println(r.getName() + "\tRunning\t" + r.getRate());
        }
    }

    private static void setReaderRate() {
        if (readers.isEmpty()) {
            System.out.println("No readers available to set rate!");
            return;
        }

        System.out.println("Current Readers:");
        listReaders();

        System.out.print("Enter reader ID to modify rate: ");
        int readerId = scanner.nextInt();

        Reader readerToModify = null;

        for (Reader r : readers) {
            if (Integer.parseInt(r.getName()) == readerId) {
                readerToModify = r;
                break;
            }
        }

        if (readerToModify != null) {
            System.out.print("Enter new rate (operations per second, 0 = default): ");
            int newRate = scanner.nextInt();
            readerToModify.setRate(newRate);
            System.out.println("Reader " + readerId + " rate updated to " + newRate);
        } else {
            System.out.println("Reader with ID " + readerId + " not found!");
        }
    }

    private static void addWriter() {
        System.out.print("Enter writer ID (number): ");
        int writerId = scanner.nextInt();

        // Check if writer with this ID already exists
        for (Writer w : writers) {
            if (Integer.parseInt(w.getName()) == writerId) {
                System.out.println("Writer with ID " + writerId + " already exists!");
                return;
            }
        }

        System.out.print("Enter writer rate (operations per second, 0 = default): ");
        int rate = scanner.nextInt();

        Writer writer = new Writer(ticketPool, writerId);
        writer.setRate(rate);
        writers.add(writer);

        Thread thread = new Thread(writer, "Writer-" + writerId);
        threads.add(thread);
        thread.start();

        System.out.println("Writer " + writerId + " added successfully with rate: " + rate);
    }

    private static void removeWriter() {
        if (writers.isEmpty()) {
            System.out.println("No writers to remove!");
            return;
        }

        System.out.println("Current Writers:");
        listWriters();

        System.out.print("Enter writer ID to remove: ");
        int writerId = scanner.nextInt();

        Writer writerToRemove = null;
        Thread threadToRemove = null;

        for (Writer w : writers) {
            if (Integer.parseInt(w.getName()) == writerId) {
                writerToRemove = w;
                break;
            }
        }

        if (writerToRemove != null) {
            writerToRemove.stop();
            writers.remove(writerToRemove);

            for (Thread t : threads) {
                if (t.getName().equals("Writer-" + writerId)) {
                    threadToRemove = t;
                    break;
                }
            }

            if (threadToRemove != null) {
                threadToRemove.interrupt();
                threads.remove(threadToRemove);
            }

            System.out.println("Writer " + writerId + " removed successfully.");
        } else {
            System.out.println("Writer with ID " + writerId + " not found!");
        }
    }

    private static void listWriters() {
        if (writers.isEmpty()) {
            System.out.println("No writers available.");
            return;
        }

        System.out.println("Current Writers:");
        System.out.println("ID\tState\tRate");
        System.out.println("--\t-----\t----");

        for (Writer w : writers) {
            System.out.println(w.getName() + "\tRunning\t"+ w.getRate());
        }
    }

    private static void setWriterRate() {
        if (writers.isEmpty()) {
            System.out.println("No writers available to set rate!");
            return;
        }

        System.out.println("Current Writers:");
        listWriters();

        System.out.print("Enter writer ID to modify rate: ");
        int writerId = scanner.nextInt();

        Writer writerToModify = null;

        for (Writer w : writers) {
            if (Integer.parseInt(w.getName()) == writerId) {
                writerToModify = w;
                break;
            }
        }

        if (writerToModify != null) {
            System.out.print("Enter new rate (operations per second, 0 = default): ");
            int newRate = scanner.nextInt();
            writerToModify.setRate(newRate);
            System.out.println("Writer " + writerId + " rate updated to " + newRate);
        } else {
            System.out.println("Writer with ID " + writerId + " not found!");
        }
    }

    private static void showPoolStatus() {
        System.out.println("\nTicket Pool Status");
        System.out.println("-----------------");
        System.out.println("Available Tickets: " + ticketPool.getAvailableTickets());
        System.out.println("Sold Tickets: " + ticketPool.getSoldTickets());
        System.out.println("Total Tickets Created: " + ticketPool.getTotalTickets());
        System.out.println("\nActive Entities:");
        System.out.println("Producers: " + producers.size());
        System.out.println("Consumers: " + consumers.size());
        System.out.println("Readers: " + readers.size());
        System.out.println("Writers: " + writers.size());

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        scanner.nextLine();
    }

    private static void exit() {
        System.out.println("Exiting Ticket Pool System...");
        stopAllEntities();
        isRunning.set(false);
        System.out.println("Goodbye!");
    }

    private static void stopAllEntities() {
        // Stop all producers
        for (Producer p : producers) {
            p.stop();
        }

        // Stop all consumers
        for (Consumer c : consumers) {
            c.stop();
        }

        // Stop all readers
        for (Reader r : readers) {
            r.stop();
        }

        // Stop all writers
        for (Writer w : writers) {
            w.stop();
        }

        // Interrupt all threads
        for (Thread t : threads) {
            t.interrupt();
        }

        producers.clear();
        consumers.clear();
        readers.clear();
        threads.clear();
    }


}
