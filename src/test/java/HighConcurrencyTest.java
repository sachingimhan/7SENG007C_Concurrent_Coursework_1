import com.iit.ticket.model.Ticket;
import com.iit.ticket.pool.BlockingQueueTicketPool;
import com.iit.ticket.pool.ReentrantLockTicketPool;
import com.iit.ticket.pool.SynchronizedTicketPool;
import com.iit.ticket.pool.TicketPool;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class HighConcurrencyTest {

    private static final int POOL_CAPACITY = 500;
    private static final int NUM_THREADS = 100;
    private static final int TEST_DURATION_SECONDS = 10;

    public static void main(String[] args) throws Exception {
        System.out.println("=== High Concurrency Test ===");
        System.out.println("Testing with " + NUM_THREADS + " threads for " + TEST_DURATION_SECONDS + " seconds");

        // Test SynchronizedTicketPool
        TestResult syncResult = testHighConcurrency(new SynchronizedTicketPool(POOL_CAPACITY), "Synchronized");

        // Test ReentrantLockTicketPool
        TestResult reentrantResult = testHighConcurrency(new ReentrantLockTicketPool(POOL_CAPACITY), "ReentrantLock");

        // Test BlockingQueueTicketPool
        TestResult blockingQueueResult = testHighConcurrency(new BlockingQueueTicketPool(POOL_CAPACITY), "BlockingQueue");

        // Print summary
        System.out.println("\n=== Summary ===");
        System.out.println("SynchronizedTicketPool:");
        System.out.println("  - Throughput: " + syncResult.operationsPerSecond + " ops/sec");
        System.out.println("  - Tickets produced: " + syncResult.ticketsProduced);
        System.out.println("  - Tickets consumed: " + syncResult.ticketsConsumed);

        System.out.println("\nReentrantLockTicketPool:");
        System.out.println("  - Throughput: " + reentrantResult.operationsPerSecond + " ops/sec");
        System.out.println("  - Tickets produced: " + reentrantResult.ticketsProduced);
        System.out.println("  - Tickets consumed: " + reentrantResult.ticketsConsumed);

        System.out.println("\nBlockingQueueTicketPool:");
        System.out.println("  - Throughput: " + blockingQueueResult.operationsPerSecond + " ops/sec");
        System.out.println("  - Tickets produced: " + blockingQueueResult.ticketsProduced);
        System.out.println("  - Tickets consumed: " + blockingQueueResult.ticketsConsumed);
    }

    private static TestResult testHighConcurrency(TicketPool pool, String name) throws Exception {
        System.out.println("\nTesting " + name + " Pool under high concurrency:");

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        AtomicBoolean running = new AtomicBoolean(true);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(NUM_THREADS);

        AtomicInteger ticketsProduced = new AtomicInteger(0);
        AtomicInteger ticketsConsumed = new AtomicInteger(0);
        AtomicInteger readOperations = new AtomicInteger(0);

        Random random = new Random();

        // Start mixed workload threads (producers, consumers, readers)
        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    // Wait for all threads to start at the same time
                    startLatch.await();

                    while (running.get()) {
                        // Randomly decide what operation to do
                        int operation = random.nextInt(3);

                        switch (operation) {
                            case 0: // Produce
                                Ticket ticket = new Ticket(threadId * 10000 + random.nextInt(10000), random.nextDouble() * 100);
                                pool.addTicket(ticket);
                                ticketsProduced.incrementAndGet();
                                break;

                            case 1: // Consume
                                Ticket purchased = pool.purchaseTicket();
                                if (purchased != null) {
                                    ticketsConsumed.incrementAndGet();
                                }
                                break;

                            case 2: // Read
                                pool.getAvailableTickets();
                                pool.getSoldTickets();
                                pool.getTotalTickets();
                                readOperations.incrementAndGet();
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // Start the test
        long startTime = System.currentTimeMillis();
        startLatch.countDown();

        // Run for specified duration
        Thread.sleep(TEST_DURATION_SECONDS * 1000);

        // Stop the test
        running.set(false);
        endLatch.await(5, TimeUnit.SECONDS);
        executor.shutdownNow();

        long endTime = System.currentTimeMillis();
        long durationMs = endTime - startTime;

        // Calculate operations per second
        int totalOperations = ticketsProduced.get() + ticketsConsumed.get() + readOperations.get();
        double operationsPerSecond = totalOperations * 1000.0 / durationMs;

        System.out.println("Test completed:");
        System.out.println("  - Duration: " + durationMs + " ms");
        System.out.println("  - Tickets produced: " + ticketsProduced.get());
        System.out.println("  - Tickets consumed: " + ticketsConsumed.get());
        System.out.println("  - Read operations: " + readOperations.get());
        System.out.println("  - Total operations: " + totalOperations);
        System.out.println("  - Operations per second: " + String.format("%.2f", operationsPerSecond));

        // Check data consistency
        System.out.println("  - Final pool state:");
        System.out.println("    * Available tickets: " + pool.getAvailableTickets());
        System.out.println("    * Sold tickets: " + pool.getSoldTickets());
        System.out.println("    * Total tickets: " + pool.getTotalTickets());

        // Verify data consistency
        boolean consistent = (pool.getSoldTickets() + pool.getAvailableTickets() == pool.getTotalTickets());
        System.out.println("  - Data consistency check: " + (consistent ? "PASSED" : "FAILED"));

        return new TestResult(operationsPerSecond, ticketsProduced.get(), ticketsConsumed.get(), readOperations.get());
    }

    private static class TestResult {
        final double operationsPerSecond;
        final int ticketsProduced;
        final int ticketsConsumed;
        final int readOperations;

        TestResult(double operationsPerSecond, int ticketsProduced, int ticketsConsumed, int readOperations) {
            this.operationsPerSecond = operationsPerSecond;
            this.ticketsProduced = ticketsProduced;
            this.ticketsConsumed = ticketsConsumed;
            this.readOperations = readOperations;
        }
    }

}
