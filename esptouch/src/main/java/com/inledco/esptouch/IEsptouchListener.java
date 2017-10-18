package com.inledco.esptouch;

/**
 * Created by liruya on 2017/9/16.
 */

public interface IEsptouchListener
{
    /**
     * when new esptouch result is added, the listener will call
     * onEsptouchResultAdded callback
     *
     * @param result
     *            the Esptouch result
     */
    void onEsptouchResultAdded(IEsptouchResult result);
}
