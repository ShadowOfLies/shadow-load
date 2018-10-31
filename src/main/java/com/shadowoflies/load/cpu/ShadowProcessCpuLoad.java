/**
 * Created on 2018/10/31, 08:33:30.
 *
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 * Everyone is permitted to copy and distribute verbatim copies of this license
 * document, but changing it is not allowed.
 */
package com.shadowoflies.load.cpu;

import com.shadowoflies.load.LoadWorker;

/**
 * Makes a best effort attempt to generate and maintain load on the current
 * process to the load specification provided.
 * <p>
 * It is important to note that the load on the system could mean that the load
 * generated is much less than expected. This will be the case where another
 * (higher priority) process is taking up more processing time and generating
 * enough load to max out the processors.</p>
 * <p>
 * Using multiple instances of these load generators could work against one
 * another while attempting to maintain the load for the process.</p>
 *
 * @author Gavin Boshoff
 */
public class ShadowProcessCpuLoad extends LoadWorker {

    public ShadowProcessCpuLoad(double load) {
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
