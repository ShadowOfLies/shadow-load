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
package com.shadowoflies.load;

/**
 * @version 1
 * @author Gavin Boshoff
 */
public class ShadowCpuLoad extends LoadWorker {

    public ShadowCpuLoad(double load) {
        super(load);
    }

    @Override
    public void laden() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unladen() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}