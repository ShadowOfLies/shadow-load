/**
 * Created on 2018/10/31, 08:48:58.
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
public enum LoadState {

    /**
     * Has not been started, or has been stopped. Can be started from this
     * state.
     */
    IDLE,
    /**
     * Has started and is running, generating configured load on the system.
     */
    RUNNING,
    /**
     * Busy starting. Can already be generating load, but not necessarily at
     * the rate configured.
     */
    STARTING,
    /**
     * In the process of stopping load generation. Depending on the cleanup
     * processes required, this could take time. Next expected state is
     * {@link #IDLE} or {@link #TERMINATED}.
     */
    STOPPING,
    /**
     * Has been stopped, or a best-effort attempt at stopping the load
     * generation. However, this state is indicative of a load generator that
     * cannot and should not be used further. Instead, it should be considered
     * safe to discard entirely
     */
    TERMINATED;
}
