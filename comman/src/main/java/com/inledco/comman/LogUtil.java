package com.inledco.comman;

import android.util.Log;

/**
 * Created by liruya on 2017/9/18.
 */

public class LogUtil
{
    private static boolean mDebugEnabled = true;

    public static boolean ismDebugEnabled ()
    {
        return mDebugEnabled;
    }

    public static void setmDebugEnabled ( boolean mDebugEnabled )
    {
        LogUtil.mDebugEnabled = mDebugEnabled;
    }

    public static void v(String tag, String msg)
    {
        if ( mDebugEnabled )
        {
            Log.v( tag, msg );
        }
    }

    public static void d(String tag, String msg)
    {
        if ( mDebugEnabled )
        {
            Log.d( tag, msg );
        }
    }

    public static void i(String tag, String msg)
    {
        if ( mDebugEnabled )
        {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg)
    {
        if ( mDebugEnabled )
        {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg)
    {
        if ( mDebugEnabled )
        {
            Log.e(tag, msg);
        }
    }
}
