package com.inledco.rs485.device;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.inledco.comman.AdvancedPreferenceUtil;
import com.inledco.comman.BaseFragment;
import com.inledco.comman.LogUtil;
import com.inledco.rs485.Constants;
import com.inledco.rs485.R;
import com.inledco.xlinkcloud.XlinkCloudManager;

import java.util.List;

import io.xlink.wifi.sdk.XDevice;

/**
 * A simple {@link Fragment} subclass.
 */
public class MasterFragment extends BaseFragment
{
    private RecyclerView master_rv_show;
    private ProgressBar master_progressbar;
    private Button master_btn_scan;

    private boolean mScanning;
    private Runnable mScanRunnable;
    private XlinkCloudManager.OnScanDeviceListener mScanDeviceListener;

    private Handler mHandler;

    private List< MasterDevice > mMasterDevices;
    private MasterDeviceAdapter mMasterDeviceAdapter;

    private AdvancedPreferenceUtil< MasterDevice > mAdvancedPreferenceUtil;

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.fragment_master, container, false );

        initView( view );
        initData();
        initEvent();
        return view;
    }

    @Override
    protected void initView ( View view )
    {
        master_rv_show = (RecyclerView) view.findViewById( R.id.master_rv_show );
        master_progressbar = (ProgressBar) view.findViewById( R.id.master_progressbar );
        master_btn_scan = (Button) view.findViewById( R.id.master_btn_scan );

        master_rv_show.setLayoutManager( new GridLayoutManager( getContext(), 3 ) );
    }

    @Override
    protected void initData ()
    {
        mAdvancedPreferenceUtil = new AdvancedPreferenceUtil<>();
        mMasterDevices = mAdvancedPreferenceUtil.getAll( getContext(), Constants.MASTER_DEVICE_FILE_NAME );
        for ( MasterDevice device : mMasterDevices )
        {
            XlinkCloudManager.getInstance().initDevice( device.getXDevice() );
        }
        mMasterDeviceAdapter = new MasterDeviceAdapter( mMasterDevices );
        master_rv_show.setAdapter( mMasterDeviceAdapter );
        mHandler = new Handler();
        mScanRunnable = new Runnable()
        {
            @Override
            public void run ()
            {
                master_progressbar.setVisibility( View.GONE );
                master_btn_scan.setText( R.string.scan_local_device );
                mScanning = false;
                mHandler.removeCallbacks( mScanRunnable );
            }
        };
        mScanDeviceListener = new XlinkCloudManager.OnScanDeviceListener()
        {
            @Override
            public void onScanDevice ( XDevice device )
            {
                mMasterDevices.add( new MasterDevice( device ) );
                mMasterDeviceAdapter.notifyItemInserted( mMasterDevices.size() - 1 );
                mAdvancedPreferenceUtil.setPrefer( getContext(),
                                                   Constants.MASTER_DEVICE_FILE_NAME,
                                                   mMasterDevices.get( mMasterDevices.size() - 1 ),
                                                   device.getMacAddress() );
            }

            @Override
            public void onScanFailure ( int error )
            {
                master_progressbar.setVisibility( View.GONE );
                master_btn_scan.setText( R.string.scan_local_device );
                mScanning = false;
                mHandler.removeCallbacks( mScanRunnable );
            }
        };
    }

    @Override
    protected void initEvent ()
    {
        master_btn_scan.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick ( View v )
            {
                if ( !mScanning )
                {
                    mScanning = true;
                    mMasterDevices.clear();
                    mMasterDeviceAdapter.notifyDataSetChanged();
                    master_progressbar.setVisibility( View.VISIBLE );
                    master_btn_scan.setText( R.string.scanning );
                    XlinkCloudManager.getInstance()
                                     .scanDeviceByProductId( Constants.PRODUCT_ID, mScanDeviceListener );
                    mHandler.postDelayed( mScanRunnable, 5000 );
                }
            }
        } );
    }

    public class MasterDeviceAdapter extends RecyclerView.Adapter< MasterDeviceAdapter.ViewHolder >
    {
        private List< MasterDevice > mDevices;

        public MasterDeviceAdapter ( List< MasterDevice > devices )
        {
            mDevices = devices;
        }

        @Override
        public ViewHolder onCreateViewHolder ( ViewGroup parent, int viewType )
        {
            ViewHolder holder = new ViewHolder( LayoutInflater.from( getContext() )
                                                              .inflate( R.layout.item_master, parent, false ) );
            return holder;
        }

        @Override
        public void onBindViewHolder ( ViewHolder holder, int position )
        {
            final MasterDevice device = mDevices.get( position );
            holder.item_master_icon.setImageResource( R.drawable.ic_master_device );
            holder.item_master_name.setText( device.getXDevice()
                                                   .getDeviceName() );
            holder.item_master_mac.setText( device.getXDevice()
                                                  .getMacAddress() );
            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick ( View v )
                {
                    LogUtil.e( TAG, "onClick: " );
                    XlinkCloudManager.getInstance().connectDevice( device.getXDevice(), new XlinkCloudManager.OnConnectListener() {
                        @Override
                        public void onConnectLocal ()
                        {
                            Intent intent = new Intent( getContext(), SlaveActivity.class );
                            intent.putExtra( "master_device", device );
                            startActivity( intent );
                        }

                        @Override
                        public void onConnectCloud ()
                        {
                            Intent intent = new Intent( getContext(), SlaveActivity.class );
                            intent.putExtra( "master_device", device );
                            startActivity( intent );
                        }

                        @Override
                        public void onConnectFailure ( int error )
                        {
                            Toast.makeText( getContext(), "连接失败,失败代码: " + error, Toast.LENGTH_SHORT )
                                 .show();
                        }
                    } );
                }
            } );
        }

        @Override
        public int getItemCount ()
        {
            return mDevices == null ? 0 : mDevices.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            private ImageView item_master_icon;
            private TextView item_master_name;
            private TextView item_master_mac;

            public ViewHolder ( View itemView )
            {
                super( itemView );
                item_master_icon = (ImageView) itemView.findViewById( R.id.item_master_icon );
                item_master_name = (TextView) itemView.findViewById( R.id.item_master_name );
                item_master_mac = (TextView) itemView.findViewById( R.id.item_master_mac );
            }
        }
    }
}
