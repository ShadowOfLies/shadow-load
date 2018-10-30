/**
 * Created on 30 Oct 2018, 8:26:03 PM
 *
 * Copyright(c) 2018 ShadowOfLies. All Rights Reserved.
 * The code from this class and all associated code, with the exception of third
 * party library code, is the proprietary information of ShadowOfLies.
 */
package com.shadowoflies.load;

/**
 * @version 1
 * @author Gavin Boshoff
 */
public abstract class LoadWorker implements Ladening {

    private final double load;

    public LoadWorker(double load) {
        this.load = load;
    }
}