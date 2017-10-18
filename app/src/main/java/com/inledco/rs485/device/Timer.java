package com.inledco.rs485.device;

/**
 * Created by liruya on 2017/9/21.
 */

public class Timer
{
    private byte mIndex;
    private boolean mEnable;
    private boolean mSun;
    private boolean mMon;
    private boolean mTue;
    private boolean mWed;
    private boolean mThu;
    private boolean mFri;
    private boolean mSat;
    private boolean mOn;
    private short mTimer;

    public Timer ( int i, int i1 )
    {
    }

    public Timer ( byte index, boolean enable, boolean sun, boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean on,
                   short tmr )
    {
        mIndex = index;
        mEnable = enable;
        mSun = sun;
        mMon = mon;
        mTue = tue;
        mWed = wed;
        mThu = thu;
        mFri = fri;
        mSat = sat;
        mOn = on;
        this.mTimer = tmr;
    }

    public Timer ( byte index, boolean enable, byte week, boolean on, short tmr )
    {
        mIndex = index;
        mEnable = enable;
        mSun = ( week & 0x01 ) == 0x01 ? true : false;
        mMon = ( week & 0x02 ) == 0x02 ? true : false;
        mTue = ( week & 0x04 ) == 0x04 ? true : false;
        mWed = ( week & 0x08 ) == 0x08 ? true : false;
        mThu = ( week & 0x10 ) == 0x10 ? true : false;
        mFri = ( week & 0x20 ) == 0x20 ? true : false;
        mSat = ( week & 0x40 ) == 0x40 ? true : false;
        mOn = on;
        mTimer = tmr;
    }

    public Timer ( short value1, short value2 )
    {
        mIndex = (byte) ( ( value1 & 0xFFFF) >> 8);
        mEnable = ( value1 & 0x80 ) == 0x0080 ? true : false;
        mSun = ( value1 & 0x01 ) == 0x01 ? true : false;
        mMon = ( value1 & 0x02 ) == 0x02 ? true : false;
        mTue = ( value1 & 0x04 ) == 0x04 ? true : false;
        mWed = ( value1 & 0x08 ) == 0x08 ? true : false;
        mThu = ( value1 & 0x10 ) == 0x10 ? true : false;
        mFri = ( value1 & 0x20 ) == 0x20 ? true : false;
        mSat = ( value1 & 0x40 ) == 0x40 ? true : false;
        mOn = ( value2 & 0x8000 ) == 0x8000 ? true : false;
        mTimer = (short) ( value2 & 0x7FFF);
    }

    public Timer ( int value )
    {
        this( (short) ( value >> 16), (short) ( value & 0xFFFF) );
    }

    public boolean isValid ()
    {
        return mTimer < 1440;
    }

    public byte getIndex ()
    {
        return mIndex;
    }

    public void setIndex ( byte index )
    {
        mIndex = index;
    }

    public boolean isSun ()
    {
        return mSun;
    }

    public void setSun ( boolean sun )
    {
        mSun = sun;
    }

    public boolean isMon ()
    {
        return mMon;
    }

    public void setMon ( boolean mon )
    {
        mMon = mon;
    }

    public boolean isTue ()
    {
        return mTue;
    }

    public void setTue ( boolean tue )
    {
        mTue = tue;
    }

    public boolean isWed ()
    {
        return mWed;
    }

    public void setWed ( boolean wed )
    {
        mWed = wed;
    }

    public boolean isThu ()
    {
        return mThu;
    }

    public void setThu ( boolean thu )
    {
        mThu = thu;
    }

    public boolean isFri ()
    {
        return mFri;
    }

    public void setFri ( boolean fri )
    {
        mFri = fri;
    }

    public boolean isSat ()
    {
        return mSat;
    }

    public void setSat ( boolean sat )
    {
        mSat = sat;
    }

    public boolean isOn ()
    {
        return mOn;
    }

    public void setOn ( boolean on )
    {
        mOn = on;
    }

    public boolean isEnable ()
    {
        return mEnable;
    }

    public void setEnable ( boolean enable )
    {
        mEnable = enable;
    }

    public int getTimer ()
    {
        return mTimer;
    }

    public void setTimer ( short timer )
    {
        mTimer = timer;
    }

    public int getValue1 ()
    {
        int result = 0;
        if ( mSun )
        {
            result |= 0x01;
        }
        if ( mMon )
        {
            result |= 0x02;
        }
        if ( mTue )
        {
            result |= 0x04;
        }
        if ( mWed )
        {
            result |= 0x08;
        }
        if ( mThu )
        {
            result |= 0x10;
        }
        if ( mFri )
        {
            result |= 0x20;
        }
        if ( mSat )
        {
            result |= 0x40;
        }
        if ( mEnable )
        {
            result |= 0x80;
        }
        result |= mIndex << 8;
        return result;
    }

    public int getValue2 ()
    {
        int result = 0;
        if ( mOn )
        {
            result |= 0x8000;
        }
        result |= ( mTimer >= 0 && mTimer < 1440 ) ? mTimer : 0x7FFF;
        return result;
    }

    public int getValue ()
    {
        return ( getValue1() & 0xFFFF ) << 16 | getValue2();
    }
}
