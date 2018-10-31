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

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

/**
 * @author Gavin Boshoff
 */
public class ShadowSystem {

    private static boolean stopped = false;

    public static void main(String... args) throws InterruptedException {
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

    public ShadowSystem() {}

    public int cores() {
        return Runtime.getRuntime().availableProcessors();
    }

    public int processorThreads() {
        return Runtime.getRuntime().availableProcessors();
    }
}