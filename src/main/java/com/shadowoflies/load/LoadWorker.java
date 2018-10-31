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
 * @version 1
 * @author Gavin Boshoff
 */
public abstract class LoadWorker implements Ladening {

    private final double load;

    public LoadWorker(double load) {
        this.load = load;
    }
}