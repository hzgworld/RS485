package com.inledco.rs485.device;

/**
 * Created by liruya on 2017/9/21.
 */

public class SlaveDevice
{
    private short[] mBrights;
    private Timer[] mTimers;

    private boolean mOn;

    private short mDeviceId;
    private short mSoftwareVersion;
    private short mHardwareVersion;
    private short mIntensity;
    private short mTemperature;
    private short mHumidity;
    private short mCo2;

    public SlaveDevice ()
    {
        mBrights = new short[8];
        mTimers = new Timer[8];
        for ( int i = 0; i < mBrights.length; i++ )
        {
            mBrights[i] = (short) 0xFFFF;
        }
        for ( int i = 0; i < mTimers.length; i++ )
        {
            mTimers[i] = new Timer( 0xFFFF, 0xFFFF );
        }
        mDeviceId = (short) 0xFFFF;
        mSoftwareVersion = (short) 0xFFFF;
        mHardwareVersion = (short) 0xFFFF;
        mIntensity = (short) 0xFFFF;
        mTemperature = (short) 0xFFFF;
        mHumidity = (short) 0xFFFF;
        mCo2 = (short) 0xFFFF;
    }

    public SlaveDevice ( DeviceRegister deviceRegister )
    {
        this();
        if ( deviceRegister != null )
        {
            for ( int i = 0; i < mBrights.length; i++ )
            {
                mBrights[i] = deviceRegister.getHold( 8 + i );
            }
            for ( int i = 0; i < mTimers.length; i++ )
            {
                mTimers[i] = new Timer( deviceRegister.getHold( 16 + 2 * i ), deviceRegister.getHold( 17 + 2 * i ) );
            }
            mOn = deviceRegister.getCoil( 0 );
            mDeviceId = deviceRegister.getInput( 1 );
            mSoftwareVersion = deviceRegister.getInput( 2 );
            mHardwareVersion = deviceRegister.getInput( 3 );
            mIntensity = deviceRegister.getInput( 8 );
            mTemperature = deviceRegister.getInput( 9 );
            mHumidity = deviceRegister.getInput( 10 );
            mCo2 = deviceRegister.getInput( 11 );
        }
    }

    public short getBright ( int idx )
    {
        if ( idx < mBrights.length )
        {
            return mBrights[idx];
        }
        return -1;
    }

    public void setBright ( int idx, short value )
    {
        if ( idx < mBrights.length )
        {
            mBrights[idx] = value;
        }
    }

    public Timer getTimer ( int idx )
    {
        if ( idx < mTimers.length )
        {
            return mTimers[idx];
        }
        return new Timer( 0xFFFF, 0xFFFF );
    }

    public void setTimer ( int idx, Timer tmr )
    {
        if ( idx < mTimers.length )
        {
            mTimers[idx] = tmr;
        }
    }

    public short[] getBrights ()
    {
        return mBrights;
    }

    public void setBrights ( short[] brights )
    {
        mBrights = brights;
    }

    public Timer[] getTimers ()
    {
        return mTimers;
    }

    public void setTimers ( Timer[] timers )
    {
        mTimers = timers;
    }

    public boolean getOn ()
    {
        return mOn;
    }

    public void setOn ( boolean on )
    {
        mOn = on;
    }

    public short getDeviceId ()
    {
        return mDeviceId;
    }

    public void setDeviceId ( short deviceId )
    {
        mDeviceId = deviceId;
    }

    public short getSoftwareVersion ()
    {
        return mSoftwareVersion;
    }

    public void setSoftwareVersion ( short softwareVersion )
    {
        mSoftwareVersion = softwareVersion;
    }

    public short getHardwareVersion ()
    {
        return mHardwareVersion;
    }

    public void setHardwareVersion ( short hardwareVersion )
    {
        mHardwareVersion = hardwareVersion;
    }

    public short getIntensity ()
    {
        return mIntensity;
    }

    public void setIntensity ( short intensity )
    {
        mIntensity = intensity;
    }

    public short getTemperature ()
    {
        return mTemperature;
    }

    public void setTemperature ( short temperature )
    {
        mTemperature = temperature;
    }

    public short getHumidity ()
    {
        return mHumidity;
    }

    public void setHumidity ( short humidity )
    {
        mHumidity = humidity;
    }

    public short getCo2 ()
    {
        return mCo2;
    }

    public void setCo2 ( short co2 )
    {
        mCo2 = co2;
    }
}
