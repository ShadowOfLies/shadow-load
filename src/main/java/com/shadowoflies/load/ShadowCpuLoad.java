/**
 * Created on 30 Oct 2018, 8:48:21 PM
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