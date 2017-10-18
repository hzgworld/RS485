package com.inledco.esptouch.task;

/**
 * Created by liruya on 2017/9/16.
 */

public interface IEsptouchGenerator
{
    /**
     * Get guide code by the format of byte[][]
     *
     * @return guide code by the format of byte[][]
     */
    byte[][] getGCBytes2();

    /**
     * Get data code by the format of byte[][]
     *
     * @return data code by the format of byte[][]
     */
    byte[][] getDCBytes2();
}
