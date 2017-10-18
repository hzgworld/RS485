package com.inledco.rs485.device;

import java.io.Serializable;

import io.xlink.wifi.sdk.XDevice;

/**
 * Created by liruya on 2017/9/20.
 */

public class MasterDevice implements Serializable
{
    private static final long serialVersionUID = -1110622979905108909L;

    private XDevice mXDevice;

    public MasterDevice ( XDevice XDevice )
    {
        mXDevice = XDevice;
    }

    public XDevice getXDevice ()
    {
        return mXDevice;
    }

    public void setXDevice ( XDevice XDevice )
    {
        mXDevice = XDevice;
    }
}
