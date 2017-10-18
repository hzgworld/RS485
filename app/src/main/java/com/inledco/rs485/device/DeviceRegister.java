package com.inledco.rs485.device;

import java.io.Serializable;

/**
 * Created by liruya on 2017/9/21.
 */

public class DeviceRegister implements Serializable
{
    private static final long serialVersionUID = 7327804350444849232L;

    private boolean[] mCoils;
    private boolean[] mStatus;
    private short[] mHolds;
    private short[] mInputs;

    public DeviceRegister ()
    {

        mCoils = new boolean[8];
        mStatus = new boolean[8];
        mHolds = new short[32];
        mInputs = new short[16];
        for ( int i = 0; i < mCoils.length; i++ )
        {
            mCoils[i] = false;
        }
        for ( int i = 0; i < mStatus.length; i++ )
        {
            mStatus[i] = false;
        }
        for ( int i = 0; i < mHolds.length; i++ )
        {
            mHolds[i] = (short) 0xFFFF;
        }
        for ( int i = 0; i < mInputs.length; i++ )
        {
            mInputs[i] = (short) 0xFFFF;
        }
    }

    public DeviceRegister ( boolean[] coils, boolean[] status, short[] holds, short[] inputs )
    {
        mCoils = coils;
        mStatus = status;
        mHolds = holds;
        mInputs = inputs;
    }

    public boolean[] getCoils ()
    {
        return mCoils;
    }

    public void setCoils ( boolean[] coils )
    {
        mCoils = coils;
    }

    public boolean[] getStatus ()
    {
        return mStatus;
    }

    public void setStatus ( boolean[] status )
    {
        mStatus = status;
    }

    public short[] getHolds ()
    {
        return mHolds;
    }

    public void setHolds ( short[] holds )
    {
        mHolds = holds;
    }

    public short[] getInputs ()
    {
        return mInputs;
    }

    public void setInputs ( short[] inputs )
    {
        mInputs = inputs;
    }

    public void setCoil ( int idx, boolean b )
    {
        if ( idx < mCoils.length )
        {
            mCoils[idx] = b;
        }
    }

    public boolean getCoil ( int idx )
    {
        if ( idx < mCoils.length )
        {
            return mCoils[idx];
        }
        return false;
    }

    public void setStatus ( int idx, boolean b )
    {
        if ( idx < mStatus.length )
        {
            mStatus[idx] = b;
        }
    }

    public boolean getStatus ( int idx )
    {
        if ( idx < mStatus.length )
        {
            return mStatus[idx];
        }
        return false;
    }

    public void setHold ( int idx, short value )
    {
        if ( idx < mHolds.length )
        {
            mHolds[idx] = value;
        }
    }

    public short getHold ( int idx )
    {
        if ( idx < mHolds.length )
        {
            return mHolds[idx];
        }
        return -1;
    }

    public void setInput ( int idx, short value )
    {
        if ( idx < mInputs.length )
        {
            mInputs[idx] = value;
        }
    }

    public short getInput ( int idx )
    {
        if ( idx < mInputs.length )
        {
            return mInputs[idx];
        }
        return -1;
    }

    public static class Builder
    {
        public DeviceRegister build( byte[] bytes )
        {
            if ( bytes != null && bytes.length > 5 && bytes[1] == 0x00 && bytes.length == bytes[2] + 5 )
            {
                if ( CRC16Util.getCRC( bytes ) == 0 )
                {
                    DeviceRegister deviceRegister = new DeviceRegister();
                    int index = 3;
                    while( index < bytes.length - 2 )
                    {
                        int type = (bytes[index] & 0xFF ) >> 6;
                        int idx = bytes[index++] & 0x3F;
                        if ( type == 0 )
                        {
                            deviceRegister.setCoil( idx, bytes[index++] == 0x00 ? false : true );
                        }
                        else if ( type == 1 )
                        {
                            deviceRegister.setStatus( idx, bytes[index++] == 0x00 ? false : true );
                        }
                        else if ( type == 2 )
                        {
                            short value = (short) ( ( ( bytes[index++] << 8 ) & 0xFF ) | bytes[index++] );
                            deviceRegister.setHold( idx, value );
                        }
                        else if ( type == 3 )
                        {
                            short value = (short) ( ( ( bytes[index++] << 8 ) & 0xFF ) | bytes[index++] );
                            deviceRegister.setInput( idx, value );
                        }
                    }
                    return deviceRegister;
                }
            }
            return null;
        }
    }
}
