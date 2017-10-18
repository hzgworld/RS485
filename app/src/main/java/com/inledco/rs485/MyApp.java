package com.inledco.rs485;

import android.app.Application;

import com.inledco.comman.LogUtil;
import com.inledco.xlinkcloud.XlinkCloudManager;

import java.util.Arrays;
import java.util.List;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.bean.DataPoint;
import io.xlink.wifi.sdk.bean.EventNotify;
import io.xlink.wifi.sdk.listener.XlinkNetListener;

/**
 * Created by liruya on 2017/9/18.
 */

public class MyApp extends Application implements XlinkNetListener
{
    private static final String TAG = "MyApp";

    @Override
    public void onCreate ()
    {
        super.onCreate();
        XlinkCloudManager.initialize( this, Constants.COMPANY_ID );
        XlinkCloudManager.getInstance().addXlinkListener( this );
        XlinkCloudManager.getInstance().start();
    }

    @Override
    public void onStart ( int i )
    {
        LogUtil.d( TAG, "onStart: " + i );
    }

    @Override
    public void onLogin ( int i )
    {
        LogUtil.d( TAG, "onLogin: " + i );
    }

    @Override
    public void onLocalDisconnect ( int i )
    {
        LogUtil.d( TAG, "onLocalDisconnect: " + i );
    }

    @Override
    public void onDisconnect ( int i )
    {
        LogUtil.d( TAG, "onDisconnect: " + i );
    }

    @Override
    public void onRecvPipeData ( short i, XDevice xDevice, byte[] bytes )
    {
        LogUtil.d( TAG, "onRecvPipeData: " + i + "\t\t" + Arrays.toString( bytes ) );
    }

    @Override
    public void onRecvPipeSyncData ( short i, XDevice xDevice, byte[] bytes )
    {
        LogUtil.d( TAG, "onRecvPipeData: " + i + "\t\t" + Arrays.toString( bytes ) );
    }

    @Override
    public void onDeviceStateChanged ( XDevice xDevice, int i )
    {
        LogUtil.d( TAG, "onDeviceStateChanged: " + i );
    }

    @Override
    public void onDataPointUpdate ( XDevice xDevice, List< DataPoint > list, int i )
    {
        LogUtil.d( TAG, "onDataPointUpdate: " + i );
    }

    @Override
    public void onEventNotify ( EventNotify eventNotify )
    {
        LogUtil.d( TAG, "onEventNotify: " + eventNotify.toString() );
    }
}
