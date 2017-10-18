package com.inledco.esptouch;

import java.net.InetAddress;

/**
 * Created by liruya on 2017/9/16.
 */

public interface IEsptouchResult
{
    /**
     * check whether the esptouch task is executed suc
     *
     * @return whether the esptouch task is executed suc
     */
    boolean isSuc();

    /**
     * get the device's bssid
     *
     * @return the device's bssid
     */
    String getBssid();

    /**
     * check whether the esptouch task is cancelled by user
     *
     * @return whether the esptouch task is cancelled by user
     */
    boolean isCancelled();

    /**
     * get the ip address of the device
     *
     * @return the ip device of the device
     */
    InetAddress getInetAddress ();
}
