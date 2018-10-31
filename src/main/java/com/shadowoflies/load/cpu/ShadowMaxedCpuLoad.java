/**
 * Created on 2018/10/31, 08:31:06.
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
 * @author Gavin Boshoff
 */
public class ShadowMaxedCpuLoad extends LoadWorker {

    private static final double MAX_LOAD = 100D;

    public static ShadowMaxedCpuLoad getInstance() {
        return ShadowMaxedCpuLoadHolder.INSTANCE;
    }

    private final BusyThreadManager busyManager;

    private ShadowMaxedCpuLoad() {
        super(MAX_LOAD);
        this.busyManager = new BusyThreadManager();
    }

    @Override
    public synchronized void laden() {
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
    public synchronized void unladen() {
        if (getState() != LoadState.RUNNING) {
            throw new IllegalStateException("Process is not in a RUNNING state. "
                    + "Either wait for it to finish starting or stopping.");
        }

        this.busyManager.dontBeBusy();
        setState(LoadState.IDLE);
    }

    private static final class ShadowMaxedCpuLoadHolder {
        private static final ShadowMaxedCpuLoad INSTANCE = new ShadowMaxedCpuLoad();
        private ShadowMaxedCpuLoadHolder() {}
    }

    private class BusyThreadManager {

        private final Set<VeryBusyThread> threads;
        private final ExecutorService executor;
        private final int threadCount;

        private BusyThreadManager() {
            this.threadCount = findThreadCount();
            this.executor = Executors.newFixedThreadPool(this.threadCount);
            this.threads = new HashSet<>();
        }

        private int findThreadCount() {
            ShadowSystem system;
            int busyThreads;

            system = new ShadowSystem();

            // We get the number of processor threads
            busyThreads = system.processorThreads();

            // Now we need to overload it by adding waiting threads
            return (int) Math.ceil(busyThreads * 1.5);
        }

        private void beBusy() {
            for (int i = 0; i < this.threadCount; i++) {
                this.threads.add(new VeryBusyThread());
            }

            for (VeryBusyThread thread : this.threads) {
                this.executor.execute(thread);
            }

            ShadowMaxedCpuLoad.this.setState(LoadState.RUNNING);
        }

        private void dontBeBusy() {
            ShadowMaxedCpuLoad.this.setState(LoadState.STOPPING);

            for (VeryBusyThread thread : this.threads) {
                thread.stop();
            }

            while (isBusy()) {
                // We only check every 1/100th of a second
                try { Thread.sleep(10L); } catch (InterruptedException ex) {}
            }
        }

        private boolean isBusy() {
            for (VeryBusyThread thread : this.threads) {
                if (!thread.isDisposable()) {
                    return true;
                }
            }

            return false;
        }
    }

    private static class VeryBusyThread implements Runnable {

        private volatile boolean stop;

        private boolean disposable;

        private VeryBusyThread() {
            this.disposable = true;
            this.stop = false;
        }

        @Override
        public void run() {
            this.disposable = false;

            while (!this.stop) {
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    // Do nothing
                }
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
