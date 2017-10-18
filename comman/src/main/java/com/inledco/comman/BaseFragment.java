package com.inledco.comman;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liruya on 2017/9/18.
 */

public abstract class BaseFragment extends Fragment
{
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    public void onAttach ( Context context )
    {
        super.onAttach( context );
        LogUtil.d( TAG, "onAttach: " );
    }

    @Override
    public void onCreate ( @Nullable Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        LogUtil.d( TAG, "onCreate: " );
    }

    @Nullable
    @Override
    public View onCreateView ( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState )
    {
        LogUtil.d( TAG, "onCreateView: " );
        return super.onCreateView( inflater, container, savedInstanceState );
    }

    @Override
    public void onActivityCreated ( @Nullable Bundle savedInstanceState )
    {
        super.onActivityCreated( savedInstanceState );
        LogUtil.d( TAG, "onActivityCreated: " );
    }

    @Override
    public void onStart ()
    {
        super.onStart();
        LogUtil.d( TAG, "onStart: " );
    }

    @Override
    public void onResume ()
    {
        super.onResume();
        LogUtil.d( TAG, "onResume: " );
    }

    @Override
    public void onPause ()
    {
        super.onPause();
        LogUtil.d( TAG, "onPause: " );
    }

    @Override
    public void onStop ()
    {
        super.onStop();
        LogUtil.d( TAG, "onStop: " );
    }

    @Override
    public void onDestroyView ()
    {
        super.onDestroyView();
        LogUtil.d( TAG, "onDestroyView: " );
    }

    @Override
    public void onDestroy ()
    {
        super.onDestroy();
        LogUtil.d( TAG, "onDestroy: " );
    }

    @Override
    public void onDetach ()
    {
        super.onDetach();
        LogUtil.d( TAG, "onDetach: " );
    }

    protected abstract void initView ( View view );

    protected abstract void initData();

    protected abstract void initEvent();
}
