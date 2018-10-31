/**
 * Created on 30 Oct 2018, 8:49:17 PM
 *
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 * Everyone is permitted to copy and distribute verbatim copies of this license
 * document, but changing it is not allowed.
 */
package com.shadowoflies.load.system;

import com.shadowoflies.load.cpu.ShadowMaxedCpuLoad;
import com.shadowoflies.load.cpu.ShadowSystemCpuLoad;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

/**
 * @author Gavin Boshoff
 */
public class ShadowSystem {

    private static boolean stopped = false;

    public static void main(String... args) throws InterruptedException {
        ShadowSystemCpuLoad sysCpuLoad;

        sysCpuLoad = new ShadowSystemCpuLoad(65D);
        System.out.println("Starting monitor...");
        monitor();

        System.out.println("Requesting load generation...");
        sysCpuLoad.laden();

        System.out.println("Give some time to allow the load to stabilize...");
        Thread.sleep(30000L);

        System.out.println("Waiting for 30 seconds to monitor load...");
        Thread.sleep(30000L);

        System.out.println("Requesting load generation stop...");
        sysCpuLoad.unladen();

        System.out.println("Done...");
    }

    private static void maxoutTest() throws InterruptedException {
        ShadowMaxedCpuLoad maxedCpuLoad;

        maxedCpuLoad = ShadowMaxedCpuLoad.getInstance();
        System.out.println("Starting monitor...");
        monitor();

        System.out.println("Requesting load generation...");
        maxedCpuLoad.laden();

        System.out.println("Waiting for 30 seconds to monitor load...");
        Thread.sleep(30000L);

        System.out.println("Requesting load generation stop...");
        maxedCpuLoad.unladen();

        System.out.println("Done...");
    }

    private static void simpleTest() throws InterruptedException {
        int availableProcessors = Runtime.getRuntime().availableProcessors();

        System.out.println("GBO - Available Processors : " + availableProcessors);

        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        availableProcessors = operatingSystemMXBean.getAvailableProcessors();
        System.out.println("GBO - Available Processors 2 : " + availableProcessors);

        Thread.sleep(1000L);

        System.out.println("GBO - System Load : " + operatingSystemMXBean.getSystemCpuLoad());

        monitor();
        kill();

        Thread.sleep(60000L);

//        System.out.println("GBO - System Load : " + operatingSystemMXBean.getSystemCpuLoad());
//
//        Thread.sleep(1000L);
//
//        System.out.println("GBO - System Load : " + operatingSystemMXBean.getSystemCpuLoad());

        stopped = true;

//        System.out.println("GBO - System Load : " + operatingSystemMXBean.getSystemCpuLoad());
        System.out.println("GBO - Done...");
    }

    private static void monitor() {
        new Thread(new Runnable() {

            private final OperatingSystemMXBean sysBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

            @Override
            public void run() {
                while (!stopped) {
                    try {
                        Thread.sleep(1000L);
                        System.out.println("GBO - System Load : " + sysBean.getSystemCpuLoad());
                        System.out.println("GBO - Process Load : " + sysBean.getProcessCpuLoad());
                    } catch (InterruptedException ex) {}
                }
            }
        }).start();
    }

    private static void kill() {
        new Thread(() -> {
            while (!stopped) {
//                try {
//                    Thread.sleep(0L, 5);
//                } catch (InterruptedException ex) {}
            }
        }).start();
    }

    private final OperatingSystemMXBean sysBean;

    public ShadowSystem() {
        this.sysBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    }

    public int cores() {
        return Runtime.getRuntime().availableProcessors();
    }

    public int processorThreads() {
        return Runtime.getRuntime().availableProcessors();
    }

    public double systemCpuLoad() {
        return this.sysBean.getSystemCpuLoad();
    }
}