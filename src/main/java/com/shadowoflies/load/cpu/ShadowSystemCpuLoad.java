/**
 * Created on 30 Oct 2018, 8:48:21 PM
 *
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 * Everyone is permitted to copy and distribute verbatim copies of this license
 * document, but changing it is not allowed.
 */
package com.shadowoflies.load.cpu;

import com.shadowoflies.load.LoadState;
import com.shadowoflies.load.LoadWorker;
import com.shadowoflies.load.system.ShadowSystem;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Makes a best effort attempt to generate and maintain load on the system as
 * per the load specification provided.
 * <p>
 * Due to some restrictions and inconsistencies, the load experienced by the
 * generator might not be exactly what is perceived on the system. This could
 * result in unexpected behavior. This means that it is important to not
 * consider the load generated as the source of truth for the actual load
 * experienced.
 *
 * @author Gavin Boshoff
 */
public class ShadowSystemCpuLoad extends LoadWorker {

    private final BusyThreadManager busyManager;

    public ShadowSystemCpuLoad(double load) {
        super(load);
        this.busyManager = new BusyThreadManager(load / 100D);
    }

    @Override
    public void laden() {
        if (getState() == LoadState.STARTING
                || getState() == LoadState.RUNNING
                || getState() == LoadState.STOPPING) {
            throw new IllegalStateException("Process is already running. "
                    + "Use another load generator if this is not enough.");
        }

        setState(LoadState.STARTING);
        this.busyManager.beBusy();
    }

    @Override
    public void unladen() {
        if (getState() != LoadState.RUNNING) {
            throw new IllegalStateException("Process is not in a RUNNING state. "
                    + "Either wait for it to finish starting or stopping.");
        }

        this.busyManager.dontBeBusy();
        setState(LoadState.IDLE);
    }

    private class BusyThreadManager {

        private final Set<DoubleLoopThread> threads;
        private final ExecutorService executor;
        private final double expectedLoad;
        private final int threadCount;

        private SleeperThreadLoadMonitor loadMonitor;

        private BusyThreadManager(double expectedLoad) {
            this.threadCount = findThreadCount();
            this.executor = Executors.newFixedThreadPool(this.threadCount);
            this.threads = new HashSet<>();

            this.expectedLoad = expectedLoad;
        }

        private int findThreadCount() {
            ShadowSystem system;
            int busyThreads;

            system = new ShadowSystem();

            // We get the number of processor threads
//            busyThreads = system.processorThreads();
            return system.processorThreads();

//            // Now we need to overload it by adding waiting threads
//            return (int) (busyThreads * 2);
        }

        private void beBusy() {
            for (int i = 0; i < this.threadCount; i++) {
                this.threads.add(new DoubleLoopThread());
            }

            for (DoubleLoopThread thread : this.threads) {
                this.executor.execute(thread);
            }

            ShadowSystemCpuLoad.this.setState(LoadState.RUNNING);

            this.loadMonitor = new SleeperThreadLoadMonitor(this.expectedLoad);
            new Thread(loadMonitor).start();
        }

        private void dontBeBusy() {
            ShadowSystemCpuLoad.this.setState(LoadState.STOPPING);

            for (DoubleLoopThread thread : this.threads) {
                thread.stop();
            }

            while (isBusy()) {
                // We only check every 1/100th of a second
                try { Thread.sleep(10L); } catch (InterruptedException ex) {}
            }
        }

        private boolean isBusy() {
            for (DoubleLoopThread thread : this.threads) {
                if (!thread.isDisposable()) {
                    return true;
                }
            }

            return false;
        }
    }

    private class SleeperThreadLoadMonitor implements Runnable {

        private final double increaseBarrier;
        private final double decreaseBarrier;
        private final ShadowSystem system;

        private SleeperThreadLoadMonitor(double expectedLoad) {
            this.system = new ShadowSystem();

            this.increaseBarrier = calculateIncreaseBarrier(expectedLoad);
            this.decreaseBarrier = calculateDecreaseBarrier(expectedLoad);
        }

        private double calculateDecreaseBarrier(double expectedLoad) {
            if (expectedLoad > 0.95D) {
                // TODO: WARN - Suggest using max-out generator
                // Use value that will never be reached
                return 1.1D;
            } else if (expectedLoad > 0.9D) {
                // We are going to try keep things below 100%
                return 0.95D;
            } else {
                return expectedLoad + 0.1D;
            }
        }

        private double calculateIncreaseBarrier(double expectedLoad) {
            if (expectedLoad < 0.05D) {
                // TODO: WARN - Probably won't work
                // Use value that will never be reached
                return -0.12; // -1 returned when value is not available
            } else if (expectedLoad < 0.1D) {
                // We are going to try keep it just over 'doing nothing'
                return 0.05D;
            } else {
                return expectedLoad - 0.1D;
            }
        }

        @Override
        public void run() {
            double currentLoad;

            System.out.println("Increase Barrier : " + increaseBarrier);
            System.out.println("Decrease Barrier : " + decreaseBarrier);

            while (ShadowSystemCpuLoad.this.getState() == LoadState.RUNNING) {
                currentLoad = averageLoad();

                try {
                    if (currentLoad < increaseBarrier) {
                        if (currentLoad < (increaseBarrier - 0.1D)) {
                            DoubleLoopThread.outerLoopCount -= 1_000_000;
                        } else {
                            DoubleLoopThread.outerLoopCount -= 1_000;
                        }

                        Thread.sleep(5L);
                    } else if (currentLoad > decreaseBarrier) {
                        if (currentLoad > (decreaseBarrier + 0.1)) {
                            DoubleLoopThread.outerLoopCount += 1_000_000;
                        } else {
                            DoubleLoopThread.outerLoopCount += 1_000;
                        }

                        Thread.sleep(5L);
                    } else {
                        Thread.sleep(100L); // We seem to be in the correct range
                    }
                } catch (InterruptedException ie) {}
            }

            System.out.println("XXXXXXXXXXXXXXX");
            System.out.println("Sleep Nanos at the end : " + DoubleLoopThread.outerLoopCount);
            System.out.println("XXXXXXXXXXXXXXX");
        }

        private double averageLoad() {
            double averageLoad = 0D;
            double load;

            for (int i = 0; i < 5; i++) {
                load = system.systemCpuLoad();
                if (load < 0D) {
                    i--;
                } else {
                    averageLoad = averageLoad + load;
                }
            }

            return (averageLoad / 5);
        }
    }

    private static class DoubleLoopThread implements Runnable {

        private static final int LARGE = 1_000_000;
        private static final int SMALL = 1_000;

        private static volatile int outerLoopCount;

        private static void incLarge() {
            if (outerLoopCount > (Integer.MAX_VALUE - LARGE)) {
                outerLoopCount = Integer.MAX_VALUE;
            } else {
                outerLoopCount += LARGE;
            }
        }

        private static void incSmall() {

        }

        private static void decLarge() {

        }

        private static void decSmall() {

        }

        private volatile boolean stop;

        private boolean disposable;

        private DoubleLoopThread() {
            this.outerLoopCount = Integer.MAX_VALUE / 2;
            this.stop = false;
        }

        @Override
        public void run() {
            this.disposable = false;

            while (!stop) {
                for (int i = 0; i < outerLoopCount; i++) {
                    for (int j = 0; j < Integer.MAX_VALUE; j++) {
                        // Do nothing
                    }
                }
                try { Thread.sleep(0L, 500_000); }
                catch (InterruptedException ex) {}
            }

            this.disposable = true;
        }

        private void stop() {
            this.stop = true;
        }

        private boolean isDisposable() {
            return disposable;
        }
    }
}