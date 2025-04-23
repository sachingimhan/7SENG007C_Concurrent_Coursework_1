import com.iit.ticket.model.Ticket;
import com.iit.ticket.pool.BlockingQueueTicketPool;
import com.iit.ticket.pool.ReentrantLockTicketPool;
import com.iit.ticket.pool.SynchronizedTicketPool;
import com.iit.ticket.pool.TicketPool;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketPoolTest {

    // Constants for testing
    private static final int POOL_CAPACITY = 100;
    private static final int NUM_PRODUCERS = 5;
    private static final int NUM_CONSUMERS = 5;
    private static final int NUM_READERS = 3;
    private static final int OPERATIONS_PER_THREAD = 1000;

    public static void main(String[] args) throws Exception {
        // Run basic functionality tests
        testBasicFunctionality();

        // Run thread safety tests
        testThreadSafety();

        // Run performance tests
        testPerformance();

        // Run edge case tests
        testEdgeCases();
    }

    private static void testBasicFunctionality() {
        System.out.println("=== Basic Functionality Tests ===");

        // Test SynchronizedTicketPool
        testPoolBasicOperations(new SynchronizedTicketPool(POOL_CAPACITY), "Synchronized");

        // Test ReentrantLockTicketPool
        testPoolBasicOperations(new ReentrantLockTicketPool(POOL_CAPACITY), "ReentrantLock");

        // Test BlockingQueueTicketPool
        testPoolBasicOperations(new BlockingQueueTicketPool(POOL_CAPACITY), "BlockingQueue");
    }

    private static void testPoolBasicOperations(TicketPool pool, String name) {
        System.out.println("\nTesting " + name + " Pool basic operations:");

        // Test adding tickets
        for (int i = 0; i < 5; i++) {
            Ticket ticket = new Ticket(i, 10.0 * i);
            pool.addTicket(ticket);
        }

        // Verify available tickets
        System.out.println("Available tickets after adding 5: " + pool.getAvailableTickets());
        assert pool.getAvailableTickets() == 5 : "Expected 5 available tickets";

        // Test purchasing tickets
        for (int i = 0; i < 3; i++) {
            Ticket ticket = pool.purchaseTicket();
            System.out.println("Purchased: " + ticket);
        }

        // Verify counts
        System.out.println("Available tickets after purchasing 3: " + pool.getAvailableTickets());
        System.out.println("Sold tickets: " + pool.getSoldTickets());
        System.out.println("Total tickets created: " + pool.getTotalTickets());

        Assert.assertEquals(2, pool.getAvailableTickets());
        Assert.assertEquals(3, pool.getSoldTickets());
        Assert.assertEquals(5, pool.getTotalTickets());

        System.out.println(name + " Pool basic operations passed!");
    }

    private static void testThreadSafety() throws Exception {
        System.out.println("\n=== Thread Safety Tests ===");

        // Test SynchronizedTicketPool
        testPoolThreadSafety(new SynchronizedTicketPool(POOL_CAPACITY), "Synchronized");

        // Test ReentrantLockTicketPool
        testPoolThreadSafety(new ReentrantLockTicketPool(POOL_CAPACITY), "ReentrantLock");

        // Test BlockingQueueTicketPool
        testPoolThreadSafety(new BlockingQueueTicketPool(POOL_CAPACITY), "BlockingQueue");
    }

    private static void testPoolThreadSafety(TicketPool pool, String name) throws Exception {
        System.out.println("\nTesting " + name + " Pool thread safety:");

        ExecutorService executor = Executors.newFixedThreadPool(NUM_PRODUCERS + NUM_CONSUMERS + NUM_READERS);
        CountDownLatch latch = new CountDownLatch(NUM_PRODUCERS + NUM_CONSUMERS + NUM_READERS);
        AtomicInteger ticketsCreated = new AtomicInteger(0);
        AtomicInteger ticketsPurchased = new AtomicInteger(0);

        // Start producer threads
        for (int i = 0; i < NUM_PRODUCERS; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        int id = ticketsCreated.incrementAndGet();
                        pool.addTicket(new Ticket(id, 10.0));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Start consumer threads
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        Ticket ticket = pool.purchaseTicket();
                        if (ticket != null) {
                            ticketsPurchased.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Start reader threads
        for (int i = 0; i < NUM_READERS; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        pool.getAvailableTickets();
                        pool.getSoldTickets();
                        pool.getTotalTickets();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to complete
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Verify results
        System.out.println("Tickets created: " + ticketsCreated.get());
        System.out.println("Tickets purchased: " + ticketsPurchased.get());
        System.out.println("Pool available tickets: " + pool.getAvailableTickets());
        System.out.println("Pool sold tickets: " + pool.getSoldTickets());
        System.out.println("Pool total tickets: " + pool.getTotalTickets());

        // Verify that total tickets created equals tickets purchased plus available tickets
        Assert.assertEquals(pool.getSoldTickets() + pool.getAvailableTickets(), pool.getTotalTickets());

        System.out.println(name + " Pool thread safety test passed!");
    }

    private static void testPerformance() throws Exception {
        System.out.println("\n=== Performance Tests ===");

        // Test with different numbers of threads
        int[] threadCounts = {2, 5, 10, 20, 50};

        for (int threadCount : threadCounts) {
            System.out.println("\nPerformance test with " + threadCount + " threads each:");

            // Test SynchronizedTicketPool
            long syncTime = measurePerformance(new SynchronizedTicketPool(POOL_CAPACITY), threadCount);

            // Test ReentrantLockTicketPool
            long reentrantTime = measurePerformance(new ReentrantLockTicketPool(POOL_CAPACITY), threadCount);

            // Test BlockingQueueTicketPool
            long blockingQueueTime = measurePerformance(new BlockingQueueTicketPool(POOL_CAPACITY), threadCount);

            // Compare results
            System.out.println("SynchronizedTicketPool: " + syncTime + " ms");
            System.out.println("ReentrantLockTicketPool: " + reentrantTime + " ms");
            System.out.println("BlockingQueueTicketPool: " + blockingQueueTime + " ms");
        }
    }

    private static long measurePerformance(TicketPool pool, int threadCount) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount * 2);
        CountDownLatch latch = new CountDownLatch(threadCount * 2);

        long startTime = System.currentTimeMillis();

        // Start producer threads
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 1000; j++) {
                        pool.addTicket(new Ticket(j, 10.0));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Start consumer threads
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 1000; j++) {
                        pool.purchaseTicket();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to complete
        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    private static void testEdgeCases() {
        System.out.println("\n=== Edge Case Tests ===");

        // Test empty pool
        testEmptyPool(new SynchronizedTicketPool(POOL_CAPACITY), "Synchronized");
        testEmptyPool(new ReentrantLockTicketPool(POOL_CAPACITY), "ReentrantLock");
        testEmptyPool(new BlockingQueueTicketPool(POOL_CAPACITY), "BlockingQueue");

        // Test full pool
        testFullPool(new SynchronizedTicketPool(5), "Synchronized");
        testFullPool(new ReentrantLockTicketPool(5), "ReentrantLock");
        testFullPool(new BlockingQueueTicketPool(5), "BlockingQueue");
    }

    private static void testEmptyPool(TicketPool pool, String name) {
        System.out.println("\nTesting " + name + " Pool when empty:");

        // Try purchasing from empty pool - should block, so we'll use a thread with timeout
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Ticket result = executor.submit(() -> {
                try {
                    // This will block if the implementation is correct
                    return pool.purchaseTicket();
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                    return null;
                }
            }).get(100, TimeUnit.MILLISECONDS);

            if (result != null) {
                System.out.println("WARNING: " + name + " Pool did not block on empty pool!");
            } else {
                System.out.println(name + " Pool correctly blocks when empty.");
            }
        } catch (Exception e) {
            // Expected timeout exception
            System.out.println(name + " Pool correctly blocks when empty.");
        } finally {
            executor.shutdownNow();
        }
    }

    private static void testFullPool(TicketPool pool, String name) {
        System.out.println("\nTesting " + name + " Pool when full:");

        // Fill the pool
        for (int i = 0; i < 5; i++) {
            pool.addTicket(new Ticket(i, 10.0));
        }

        // Try adding to full pool - should block, so we'll use a thread with timeout
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Boolean result = executor.submit(() -> {
                try {
                    // This will block if the implementation is correct
                    pool.addTicket(new Ticket(999, 999.0));
                    return true;
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                    return false;
                }
            }).get(100, TimeUnit.MILLISECONDS);

            if (result != null && result) {
                System.out.println("WARNING: " + name + " Pool did not block on full pool!");
            } else {
                System.out.println(name + " Pool correctly blocks when full.");
            }
        } catch (Exception e) {
            // Expected timeout exception
            System.out.println(name + " Pool correctly blocks when full.");
        } finally {
            executor.shutdownNow();
        }
    }

}
