// BankSimulation.java
//
// CSC --- Concurrency Bottleneck Diagnosis, Level 1
// -------------------------------------------------
// This program pretends to simulate multiple ATM withdrawals against a shared bank account.
// It is intentionally buggy for teaching purposes.
//
// Known (intentional) problems for students to find and explain:
// 1. Race condition on shared balance (multiple threads read/modify/write without sync)
// 2. Possible negative balance (business logic failure)
// 3. Inconsistent final balance between runs
// 4. Inefficient CPU usage (spin loop pretending to be "work")
// 5. Manual Thread management instead of a safer executor model
//
// Your job (student) is to:
//  - Run the program several times and observe different outputs.
//  - Explain WHY the balance is inconsistent.
//  - Propose at least two code-level fixes.

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class BankSimulation {

    // Shared mutable state with zero protection. 🔥
    static class BankAccount {
        private int balance; // cents would be smarter, but we're keeping it simple

        public BankAccount(int startingBalance) {
            this.balance = startingBalance;
        }

        // Intentionally UNSAFE.
        // Multiple threads can call this at the same time.
        public void withdraw(int amount, String who) {
            // Simulate "processing time" with a dumb busy loop (CPU burn, not sleep)
            fakeWork(); 
            synchronized(this){
            // Check balance first
            if (balance >= amount) {
                // Another thread might change balance RIGHT HERE before we subtract.
                int oldBalance = balance;

                // // More fake "processing time"
                // fakeWork(); //unnecessary and widens the window for 2 threads to withdraw from the same balance

                int newBalance = oldBalance - amount;
                balance = newBalance;

                System.out.println(
                    who + " withdrew $" + amount + " | old balance = " + oldBalance + " -> new balance = " + newBalance);
                } else {
                // Sometimes this triggers even when money "should" be there.
                System.out.println(who + " tried to withdraw $" + amount +" but INSUFFICIENT FUNDS. (current balance = " + balance + ")");
            }
        }
    }
        public int getBalance() {
            return balance;
        }

        // Pretend this is "complex fraud detection"
        // but it's actually just wasting CPU cycles.
        private void fakeWork() {
            for (int i = 0; i < 100000; i++) {
                // pointlessly burn CPU
                double x = Math.sqrt(i * 123.456); // value unused
            }
        }
    }

    // Represents an ATM / user hitting the shared account.
    static class WithdrawTask implements Runnable {
        private final BankAccount account;
        private final String userName;
        private final int amountPerWithdrawal;
        private final int times;

        public WithdrawTask(BankAccount account,
                            String userName,
                            int amountPerWithdrawal,
                            int times) {
            this.account = account;
            this.userName = userName;
            this.amountPerWithdrawal = amountPerWithdrawal;
            this.times = times;
        }

        @Override
        public void run() {
            for (int i = 0; i < times; i++) {

                // NOTE: No synchronization here.
                // Threads will stomp each other.
                account.withdraw(amountPerWithdrawal, userName);

                // "Random" pause logic that is actually NOT random and NOT robust.
                // Also: Thread.sleep() is swallowed without handling.
                try {
                    // Small sleep to reshuffle timing.
                    // This is not enough to FIX the bug. It just makes the output look chaotic.
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // bad pattern: silently ignore
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BankAccount shared = new BankAccount(1000); // start $1000

        ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        //ExecutorService that creates a fixed pool of threads based on available processors
        // We’ll spin up several "people" hitting the same account.
        List<WithdrawTask> threads = List.of(

        new WithdrawTask(shared, "Alice", 50, 10),
        new WithdrawTask(shared, "Bob", 50, 10),
        new WithdrawTask(shared, "Charlie", 50, 10),
        new WithdrawTask(shared, "Diana", 50, 10),

        // Bonus chaos thread that hammers faster, more times.
        new WithdrawTask(shared, "ATM-Kiosk", 20, 40));

        System.out.println("=== Starting transactions with balance = $" + shared.getBalance() + " ===");

        List<Future<?>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // Start all threads
        // for (Thread t : threads) {
        //     t.start();
        // }
        for (Runnable t : threads) {
        futures.add(ex.submit(t));
        }
        ex.shutdown(); //when no more tasks, the shutdown starts

        // Wait for all threads to finish (join)
        // for (Thread t : threads) {
        //     try {
        //         t.join();
        //     } catch (InterruptedException e) {
        //         // again: swallowing interruption, not great practice
        //     }
        // }
         for (Future<?> f : futures) {
        try {
            f.get(); // will throw ExecutionException
        } catch (ExecutionException e) {
             // again: swallowing interruption, not great practice
        }
    }
        if (!ex.awaitTermination(30, TimeUnit.SECONDS)) {
        System.err.println("Timed out waiting for tasks to finish, forcing shutdown.");
        ex.shutdownNow();
        }
        long endTime = System.currentTimeMillis();

        System.out.println("\n=== All transactions finished. ===");
        System.out.println("Expected balance (theoretically) should never go below $0.");
        System.out.println("Actual FINAL balance reported by program = $" + shared.getBalance());
        System.out.println("Total runtime ms: " + (endTime - startTime));

        // NOTE TO STUDENTS:
        //  - Run this program several times.
        //  - You will often see different final balances.
        //  - Sometimes the balance will even go NEGATIVE or "skip" values.
        //
        // Explain WHY.
        //
        // In your report:
        //  1. What's the core concurrency bug called?
        //  2. Where does it actually happen in code?
        //  3. Under what timing conditions does it show up?
        //  4. Propose at least TWO fixes.
        //
        // Suggested directions for fixes:
        //  - Add 'synchronized' / locks around withdraw()
        //  - Use thread-safe classes / AtomicInteger
        //  - Use an ExecutorService instead of manually new Thread(...)
        //  - Remove / redesign fakeWork() to stop wasting CPU
        //
        // Also include your AI Reflection:
        //  - Did you ask an AI to help debug?
        //  - What did it get right/wrong?
        //  - Did you trust it blindly or verify?
    }
}
