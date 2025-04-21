import com.iit.ticket.model.Ticket;
import com.iit.ticket.pool.BlockingQueueTicketPool;
import com.iit.ticket.pool.ReentrantLockTicketPool;
import com.iit.ticket.pool.SynchronizedTicketPool;
import com.iit.ticket.pool.TicketPool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DeadlockStressTest {

    private static final int POOL_CAPACITY = 10;
    private static final int NUM_PRODUCER_THREADS = 30;
    private static final int NUM_CONSUMER_THREADS = 30;
    private static final int TEST_DURATION_SECONDS = 20;

    public static void main(String[] args) throws Exception {
        System.out.println("=== Deadlock Stress Test ===");
        System.out.println("Testing with " + NUM_PRODUCER_THREADS + " producer threads and " +
                NUM_CONSUMER_THREADS + " consumer threads for " + TEST_DURATION_SECONDS + " seconds");

        // Test SynchronizedTicketPool
        boolean syncSuccess = testDeadlockResistance(new SynchronizedTicketPool(POOL_CAPACITY), "Synchronized");

        // Test ReentrantLockTicketPool
        boolean reentrantSuccess = testDeadlockResistance(new ReentrantLockTicketPool(POOL_CAPACITY), "ReentrantLock");

        // Test BlockingQueueTicketPool
        boolean blockingQueueSuccess = testDeadlockResistance(new BlockingQueueTicketPool(POOL_CAPACITY), "BlockingQueue");

        // Print summary
        System.out.println("\n=== Summary ===");
        System.out.println("SynchronizedTicketPool: " + (syncSuccess ? "PASSED" : "FAILED"));
        System.out.println("ReentrantLockTicketPool: " + (reentrantSuccess ? "PASSED" : "FAILED"));
        System.out.println("BlockingQueueTicketPool: " + (blockingQueueSuccess ? "PASSED" : "FAILED"));
    }

    private static boolean testDeadlockResistance(TicketPool pool, String name) throws Exception {
        System.out.println("\nTesting " + name + " Pool for deadlock resistance:");

        ExecutorService producerExecutor = Executors.newFixedThreadPool(NUM_PRODUCER_THREADS);
        ExecutorService consumerExecutor = Executors.newFixedThreadPool(NUM_CONSUMER_THREADS);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch producerLatch = new CountDownLatch(NUM_PRODUCER_THREADS);
        CountDownLatch consumerLatch = new CountDownLatch(NUM_CONSUMER_THREADS);

        AtomicInteger producedTickets = new AtomicInteger(0);
        AtomicInteger consumedTickets = new AtomicInteger(0);

        // Start producer threads
        for (int i = 0; i < NUM_PRODUCER_THREADS; i++) {
            final int producerId = i;
            producerExecutor.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to start at once

                    long endTime = System.currentTimeMillis() + TEST_DURATION_SECONDS * 1000;
                    while (System.currentTimeMillis() < endTime) {
                        try {
                            Ticket ticket = new Ticket(producerId * 10000 + producedTickets.get(), 10.0);
                            pool.addTicket(ticket);
                            producedTickets.incrementAndGet();

                            // Sleep a tiny amount to increase contention
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    producerLatch.countDown();
                }
            });
        }

        // Start consumer threads
        for (int i = 0; i < NUM_CONSUMER_THREADS; i++) {
            consumerExecutor.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to start at once

                    long endTime = System.currentTimeMillis() + TEST_DURATION_SECONDS * 1000;
                    while (System.currentTimeMillis() < endTime) {
                        try {
                            Ticket ticket = pool.purchaseTicket();
                            if (ticket != null) {
                                consumedTickets.incrementAndGet();
                            }

                            // Sleep a tiny amount to increase contention
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    consumerLatch.countDown();
                }
            });
        }

        // Start the test
        startLatch.countDown();

        // Wait for completion or timeout
        boolean producersFinished = producerLatch.await(TEST_DURATION_SECONDS + 5, TimeUnit.SECONDS);
        boolean consumersFinished = consumerLatch.await(TEST_DURATION_SECONDS + 5, TimeUnit.SECONDS);

        // Shutdown executors
        producerExecutor.shutdownNow();
        consumerExecutor.shutdownNow();

        boolean success = producersFinished && consumersFinished;

        System.out.println("Test completed:");
        System.out.println("  - Producers completed successfully: " + producersFinished);
        System.out.println("  - Consumers completed successfully: " + consumersFinished);
        System.out.println("  - Tickets produced: " + producedTickets.get());
        System.out.println("  - Tickets consumed: " + consumedTickets.get());
        System.out.println("  - Final pool state:");
        System.out.println("    * Available tickets: " + pool.getAvailableTickets());
        System.out.println("    * Sold tickets: " + pool.getSoldTickets());
        System.out.println("    * Total tickets: " + pool.getTotalTickets());

        // Verify data consistency
        boolean consistent = (pool.getSoldTickets() + pool.getAvailableTickets() == pool.getTotalTickets());
        System.out.println("  - Data consistency check: " + (consistent ? "PASSED" : "FAILED"));

        return success && consistent;
    }

}
