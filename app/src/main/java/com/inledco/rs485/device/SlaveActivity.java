package com.inledco.rs485.device;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inledco.comman.AdvancedPreferenceUtil;
import com.inledco.comman.BaseActivity;
import com.inledco.rs485.R;

import java.util.ArrayList;

import static com.inledco.rs485.Constants.SLAVE_DEVICE_FILE_NAME;

public class SlaveActivity extends BaseActivity
{
    private ViewPager slave_vp_show;
    private ArrayList<Fragment> mFragmentArrayList;
    private DetailAdapter mAdapter;

    private SwipeRefreshLayout slave_refresh_show;
    private RecyclerView slave_rv_show;

    private AdvancedPreferenceUtil mAdvancedPreferenceUtil;

    private ArrayList< BaseSlave > mBaseSlaves;
    private SlaveAdapter mSlaveAdapter;

    private MasterDevice mMasterDevice;
    private ModbusProtocol mProtocol;
    private ModbusProtocol.OnDeviceRegisterChangedListener mRegisterChangedListener;
    private Thread mRefreshThread;

    private CountDownTimer mTimeoutTimer;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_slave );

        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onDestroy ()
    {
        super.onDestroy();
        mRefreshThread.interrupt();
        mRefreshThread = null;
        if ( mProtocol != null )
        {
            mProtocol.stop();
        }
    }

    @Override
    protected void initView ()
    {
        slave_vp_show = (ViewPager) findViewById( R.id.slave_vp_show );
        slave_refresh_show = (SwipeRefreshLayout) findViewById( R.id.slave_refresh_show );
        slave_rv_show = (RecyclerView) findViewById( R.id.slave_rv_show );
        slave_rv_show.setLayoutManager( new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false ) );
        slave_rv_show.addItemDecoration( new DividerItemDecoration( this, OrientationHelper.VERTICAL ) );
    }

    @Override
    protected void initData ()
    {
        Intent intent = getIntent();
        if ( intent != null )
        {
            mMasterDevice = (MasterDevice) intent.getSerializableExtra( "master_device" );
        }

        if ( slave_vp_show != null )
        {
            mFragmentArrayList = new ArrayList<>();
            mAdapter = new DetailAdapter( getSupportFragmentManager(), mFragmentArrayList );
            slave_vp_show.setAdapter( mAdapter );
        }

        mProgressDialog = new ProgressDialog( this );
        mTimeoutTimer = new CountDownTimer( 2000, 2000 ) {
            @Override
            public void onTick ( long millisUntilFinished )
            {

            }

            @Override
            public void onFinish ()
            {
                mProgressDialog.dismiss();
            }
        };

        mAdvancedPreferenceUtil = new AdvancedPreferenceUtil<>();
        mBaseSlaves = mAdvancedPreferenceUtil.getAll( this, SLAVE_DEVICE_FILE_NAME );

        mRegisterChangedListener = new ModbusProtocol.OnDeviceRegisterChangedListener() {
            @Override
            public void onDeviceRegisterChanged ( byte addr, DeviceRegister deviceRegister )
            {
                for ( int i = 0; i < mBaseSlaves.size(); i++ )
                {
                    if ( mBaseSlaves.get( i ).getAddress() == addr )
                    {
                        if ( mBaseSlaves.get( i ).getDeviceId() != deviceRegister.getInput( 0x01 ) )
                        {
                            mBaseSlaves.get( i )
                                       .setDeviceId( deviceRegister.getInput( 0x01 ) );
                            mSlaveAdapter.notifyItemChanged( i );
                        }
                        return;
                    }
                }
                mBaseSlaves.add( new BaseSlave( addr, deviceRegister.getInput( 0x01 ), "test" ) );
                mSlaveAdapter.notifyItemInserted( mBaseSlaves.size() - 1 );
            }

            @Override
            public void onReadAll ( byte addr, DeviceRegister deviceRegister )
            {
                mTimeoutTimer.cancel();
                mProgressDialog.dismiss();
                mFragmentArrayList.add( SlaveDetailFragment.newInstance( mMasterDevice, addr, deviceRegister ) );
                mFragmentArrayList.add( SlaveTimerFragment.newInstance( mMasterDevice, addr, deviceRegister ) );
                mAdapter.notifyDataSetChanged();
            }
        };

        mSlaveAdapter = new SlaveAdapter( mBaseSlaves );
        slave_rv_show.setAdapter( mSlaveAdapter );

        refreshSlaveDevices();
    }

    @Override
    protected void initEvent ()
    {
        slave_refresh_show.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh ()
            {
                refreshSlaveDevices();
            }
        } );
    }

    private void refreshSlaveDevices ()
    {
        if ( mMasterDevice != null && mMasterDevice.getXDevice() != null )
        {
            mProtocol = new ModbusProtocol();
            mProtocol.start();
            mProtocol.setOnDeviceRegisterChangedListener( mRegisterChangedListener );
            mBaseSlaves.clear();
            mSlaveAdapter.setEnable( false );
            mSlaveAdapter.notifyDataSetChanged();
            mRefreshThread = new Thread( new Runnable() {
                @Override
                public void run ()
                {
                    byte idx = 1;
                    while ( idx < 32 )
                    {
                        mProtocol.send( mMasterDevice.getXDevice(), mProtocol.checkDeviceOnline( idx ) );
                        idx++;
                    }
                    mSlaveAdapter.setEnable( true );
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run ()
                        {
                            mSlaveAdapter.notifyDataSetChanged();
                            slave_refresh_show.setRefreshing( false );
                        }
                    } );
                }
            } );
            slave_refresh_show.setRefreshing( true );
            mRefreshThread.start();
        }
        else
        {
            slave_refresh_show.setRefreshing( false );
        }
    }

    public class SlaveAdapter extends RecyclerView.Adapter< SlaveAdapter.SlaveViewHolder >
    {
        private ArrayList< BaseSlave > mSlaveDevices;
        private boolean mEnable;

        public SlaveAdapter ( ArrayList< BaseSlave > slaveDevices )
        {
            mSlaveDevices = slaveDevices;
        }

        public boolean isEnable ()
        {
            return mEnable;
        }

        public void setEnable ( boolean enable )
        {
            mEnable = enable;
        }

        @Override
        public SlaveViewHolder onCreateViewHolder ( ViewGroup parent, int viewType )
        {
            SlaveViewHolder holder = new SlaveViewHolder( LayoutInflater.from( SlaveActivity.this )
                                                                        .inflate( R.layout.item_slave, parent, false ) );
            return holder;
        }

        @Override
        public void onBindViewHolder ( SlaveViewHolder holder, int position )
        {
            final BaseSlave device = mSlaveDevices.get( position );
            holder.tv_addr.setText( device.getAddress() + "" );
            holder.tv_name.setText( device.getName() );
            holder.tv_type.setText( device.getDeviceId() + "" );
            holder.itemView.setEnabled( mEnable );
            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick ( View view )
                {
                    if ( slave_vp_show != null )
                    {
                        mFragmentArrayList.clear();
                        mAdapter.notifyDataSetChanged();
                        mProgressDialog.show();
                        mTimeoutTimer.start();
                        mProtocol.sendAsync( mMasterDevice.getXDevice(), mProtocol.readAll( device.getAddress() ) );
                    }
                }
            } );
        }

        @Override
        public int getItemCount ()
        {
            return mSlaveDevices == null ? 0 : mSlaveDevices.size();
        }

        public class SlaveViewHolder extends RecyclerView.ViewHolder
        {
            private ImageView iv_icon;
            private TextView tv_addr;
            private TextView tv_name;
            private TextView tv_type;

            public SlaveViewHolder ( View itemView )
            {
                super( itemView );
                iv_icon = (ImageView) itemView.findViewById( R.id.item_slave_icon );
                tv_addr = (TextView) itemView.findViewById( R.id.item_slave_addr );
                tv_name = (TextView) itemView.findViewById( R.id.item_slave_name );
                tv_type = (TextView) itemView.findViewById( R.id.item_slave_type );
            }
        }
    }

    public class DetailAdapter extends FragmentStatePagerAdapter
    {
        private ArrayList<Fragment> mFragments;

        public DetailAdapter ( FragmentManager fm, ArrayList< Fragment > fragments )
        {
            super( fm );
            mFragments = fragments;
        }

        @Override
        public Fragment getItem ( int position )
        {
            return ( mFragments != null && mFragments.size() > 0 ) ? mFragments.get( position ) : null;
        }

        @Override
        public int getCount ()
        {
            return mFragments == null ? 0 : mFragments.size();
        }
    }
}
