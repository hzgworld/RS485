package com.inledco.rs485.device;

import java.io.Serializable;

/**
 * Created by liruya on 2017/9/21.
 */

public class BaseSlave implements Serializable
{
    private static final long serialVersionUID = 6481489218170004680L;

    private byte mAddress;
    private short mDeviceId;
    private String mName;

    public BaseSlave ( byte address, short deviceId, String name )
    {
        mAddress = address;
        mDeviceId = deviceId;
        mName = name;
    }

    public byte getAddress ()
    {
        return mAddress;
    }

    public void setAddress ( byte address )
    {
        mAddress = address;
    }

    public short getDeviceId ()
    {
        return mDeviceId;
    }

    public void setDeviceId ( short deviceId )
    {
        mDeviceId = deviceId;
    }

    public String getName ()
    {
        return mName;
    }

    public void setName ( String name )
    {
        mName = name;
    }
}
