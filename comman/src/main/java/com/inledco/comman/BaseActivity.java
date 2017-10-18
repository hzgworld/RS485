package com.inledco.comman;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by liruya on 2017/9/18.
 */

public abstract class BaseActivity extends AppCompatActivity
{
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate ( @Nullable Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        LogUtil.d( TAG, "onCreate: " );
    }

    @Override
    protected void onStart ()
    {
        super.onStart();
        LogUtil.d( TAG, "onStart: " );
    }

    @Override
    protected void onResume ()
    {
        super.onResume();
        LogUtil.d( TAG, "onResume: " );
    }

    @Override
    protected void onRestart ()
    {
        super.onRestart();
        LogUtil.d( TAG, "onRestart: " );
    }

    @Override
    protected void onPause ()
    {
        super.onPause();
        LogUtil.d( TAG, "onPause: " );
    }

    @Override
    protected void onStop ()
    {
        super.onStop();
        LogUtil.d( TAG, "onStop: " );
    }

    @Override
    protected void onDestroy ()
    {
        super.onDestroy();
        LogUtil.d( TAG, "onDestroy: " );
    }

    @Override
    public void onConfigurationChanged ( Configuration newConfig )
    {
        super.onConfigurationChanged( newConfig );
        LogUtil.d( TAG, "onConfigurationChanged: " );
    }

    protected abstract void initView ();

    protected abstract void initData();

    protected abstract void initEvent();
}
