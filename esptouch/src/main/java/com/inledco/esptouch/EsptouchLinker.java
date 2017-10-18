package com.inledco.esptouch;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by liruya on 2017/9/16.
 */

public class EsptouchLinker extends AsyncTask< String, Void, List< IEsptouchResult > > implements IEsptouchListener
{
    private Context mContext;

    private IEsptouchTask mEsptouchTask;

    private IEsptouchLinkListener mEsptouchLinkListener;

    private Object mLock = new Object();

    public EsptouchLinker ( Context context )
    {
        mContext = context;
    }

    public void setEsptouchLinkListener ( IEsptouchLinkListener listener )
    {
        mEsptouchLinkListener = listener;
    }

    public void start( String ssid, String mac, String psw, boolean isSsidHiden, int taskResultCount )
    {
        String isSsidHidenStr = ( isSsidHiden ? "YES" : "NO" );
        String taskResultCountStr = taskResultCount + "";
        this.execute( ssid, mac, psw, isSsidHidenStr, taskResultCountStr );
    }

    public void stop()
    {
        if ( mEsptouchTask != null )
        {
            mEsptouchTask.interrupt();
        }
    }

    @Override
    protected List< IEsptouchResult > doInBackground ( String... params )
    {
        int taskResultCount = -1;
        synchronized ( mLock )
        {
            String ssid = params[0];
            String mac = params[1];
            String password = params[2];
            String isSsidHidenStr = params[3];
            String taskResultCountStr = params[4];
            boolean isSsidHiden = false;
            if( "YES".equals( isSsidHidenStr ) )
            {
                isSsidHiden = true;
            }
            taskResultCount = Integer.parseInt( taskResultCountStr );
            mEsptouchTask = new EsptouchTask( ssid, mac, password, isSsidHiden, mContext );
            mEsptouchTask.setEsptouchListener( this );
        }
        List< IEsptouchResult > results = mEsptouchTask.executeForResults( taskResultCount );
        return results;
    }

    @Override
    protected void onPostExecute ( List< IEsptouchResult > results )
    {
        if( mEsptouchLinkListener != null )
        {
            mEsptouchLinkListener.onCompleted( results );
        }

    }

    @Override
    public void onEsptouchResultAdded ( IEsptouchResult result )
    {
        if( mEsptouchLinkListener != null )
        {
            mEsptouchLinkListener.onLinked( result );
        }
    }
}
