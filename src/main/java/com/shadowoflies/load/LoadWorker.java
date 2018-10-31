/**
 * Created on 30 Oct 2018, 8:26:03 PM
 *
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 * Everyone is permitted to copy and distribute verbatim copies of this license
 * document, but changing it is not allowed.
 */
package com.shadowoflies.load;

/**
 * @author Gavin Boshoff
 */
public abstract class LoadWorker implements Ladening {

    private final double load;

    private LoadState state;

    public LoadWorker(double load) {
        this.state = LoadState.IDLE;
        this.load = load;
    }

    public double getLoad() {
        return load;
    }

    protected void setState(LoadState state) {
        this.state = state;
    }

    @Override
    public LoadState getState() {
        return this.state;
    }
}