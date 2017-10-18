package com.inledco.rs485.smartlink;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inledco.comman.BaseFragment;
import com.inledco.esptouch.EsptouchLinker;
import com.inledco.esptouch.IEsptouchLinkListener;
import com.inledco.esptouch.IEsptouchResult;
import com.inledco.esptouch.util.EspWifiAdminSimple;
import com.inledco.rs485.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SmartlinkFragment extends BaseFragment
{
    private EditText smartlink_et_ssid;
    private EditText smartlink_et_psw;
    private Button smartlink_btn_start;

    private ProgressDialog mProgressDialog;

    private BroadcastReceiver mWifiStateChangedReceiver;

    private EspWifiAdminSimple mEspWifiAdminSimple;
    private IEsptouchLinkListener mEsptouchLinkListener;
    private EsptouchLinker mEsptouchLinker;

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.fragment_smartlink, container, false );

        initView( view );
        initData();
        initEvent();

        return view;
    }

    @Override
    public void onDestroyView ()
    {
        super.onDestroyView();
        if ( mWifiStateChangedReceiver != null )
        {
            getContext().unregisterReceiver( mWifiStateChangedReceiver );
        }
    }

    @Override
    protected void initView ( View view )
    {
        smartlink_et_ssid = (EditText) view.findViewById( R.id.smartlink_et_ssid );
        smartlink_et_psw = (EditText) view.findViewById( R.id.smartlink_et_psw );
        smartlink_btn_start = (Button) view.findViewById( R.id.smartlink_btn_start );
    }

    @Override
    protected void initData ()
    {
        mEspWifiAdminSimple = new EspWifiAdminSimple( getContext() );
        mEsptouchLinkListener = new IEsptouchLinkListener() {
            @Override
            public void onLinked ( final IEsptouchResult result )
            {
                getActivity().runOnUiThread( new Runnable() {
                    @Override
                    public void run ()
                    {
                        Toast.makeText( getContext(), result.getBssid() + "\t\t" +result.getInetAddress().getHostAddress(), Toast.LENGTH_LONG )
                             .show();
                    }
                } );
            }

            @Override
            public void onCompleted ( List< IEsptouchResult > results )
            {
                mEsptouchLinker.stop();
                mEsptouchLinker.setEsptouchLinkListener( null );
                mEsptouchLinker = null;
                if ( mProgressDialog.isShowing() )
                {
                    mProgressDialog.dismiss();
                }
            }
        };

        String ssid = mEspWifiAdminSimple.getWifiConnectedSsid();
        if ( TextUtils.isEmpty( ssid ) )
        {
            smartlink_et_ssid.setText( getString( R.string.no_wifi_connected ) );
        }
        else
        {
            smartlink_et_ssid.setText( ssid );
        }
        mWifiStateChangedReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive ( Context context, Intent intent )
            {
                ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService( Context.CONNECTIVITY_SERVICE );
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo( ConnectivityManager.TYPE_WIFI );
                if ( networkInfo != null && networkInfo.isConnected() )
                {
                    smartlink_et_ssid.setText( mEspWifiAdminSimple.getWifiConnectedSsid() );
                    smartlink_et_psw.requestFocus();
                    smartlink_btn_start.setEnabled( true );
                }
                else
                {
                    smartlink_et_ssid.setText( getString( R.string.no_wifi_connected ) );
                    smartlink_et_psw.setText( "" );
                    smartlink_btn_start.setEnabled( false );
                }
            }
        };
        getContext().registerReceiver( mWifiStateChangedReceiver, new IntentFilter( ConnectivityManager.CONNECTIVITY_ACTION ) );

        mProgressDialog = new ProgressDialog( getContext() );
        mProgressDialog.setCanceledOnTouchOutside( false );
        mProgressDialog.setTitle( R.string.smartlink );
        mProgressDialog.setMessage( getString( R.string.config_wifi_dev_to_router ) );
        mProgressDialog.setButton( ProgressDialog.BUTTON_NEGATIVE, getString( R.string.cancel ), new DialogInterface.OnClickListener() {
            @Override
            public void onClick ( DialogInterface dialog, int which )
            {

            }
        } );
        mProgressDialog.setOnDismissListener( new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss ( DialogInterface dialog )
            {
                if ( mEsptouchLinker != null )
                {
                    mEsptouchLinker.stop();
                    mEsptouchLinker.setEsptouchLinkListener( null );
                    mEsptouchLinker = null;
                }
            }
        } );
    }

    @Override
    protected void initEvent ()
    {
        smartlink_btn_start.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick ( View v )
            {
                String psw = smartlink_et_psw.getText().toString().trim();
                if ( TextUtils.isEmpty( psw ) )
                {
                    smartlink_et_psw.setError( getString( R.string.input_empty ) );
                    return;
                }
                mEsptouchLinker = new EsptouchLinker( getContext() );
                mEsptouchLinker.setEsptouchLinkListener( mEsptouchLinkListener );
                mProgressDialog.show();
                mEsptouchLinker.start( mEspWifiAdminSimple.getWifiConnectedSsid(),
                                       mEspWifiAdminSimple.getWifiConnectedBssid(),
                                       smartlink_et_psw.getText().toString().trim(),
                                       false,
                                       1 );
            }
        } );
    }
}
