package com.inledco.rs485.device;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CheckableImageButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.inledco.comman.BaseFragment;
import com.inledco.rs485.R;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 */
public class SlaveDetailFragment extends BaseFragment
{
    private byte mAddress;
    private SlaveDevice mSlaveDevice;

    private TextView slave_detail_intensity;
    private TextView slave_detail_temperature;
    private TextView slave_detail_humidity;
    private TextView slave_detail_co2;
    private CheckableImageButton slave_detail_power;
    private RecyclerView slave_detail_rv;

    private BrightAdapter mBrightAdapter;

    private MasterDevice mMasterDevice;
    private ModbusProtocol mProtocol;

    public static SlaveDetailFragment newInstance ( MasterDevice masterDevice, byte addr, DeviceRegister deviceRegister )
    {
        SlaveDetailFragment frag = new SlaveDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable( "master_device", masterDevice );
        bundle.putByte( "addr", addr );
        bundle.putSerializable( "device_register", deviceRegister );
//        bundle.putBooleanArray( "coil", deviceRegister.getCoils() );
//        bundle.putBooleanArray( "status", deviceRegister.getStatus() );
//        bundle.putShortArray( "hold", deviceRegister.getHolds() );
//        bundle.putShortArray( "input", deviceRegister.getInputs() );
        frag.setArguments( bundle );

        return frag;
    }

    @Override
    public void onCreate ( @Nullable Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        Bundle bundle = getArguments();
        if ( bundle != null )
        {
            mMasterDevice = (MasterDevice) bundle.get( "master_device" );
            mAddress = bundle.getByte( "addr" );
            mSlaveDevice = new SlaveDevice( (DeviceRegister) bundle.getSerializable( "device_register" ) );
//            mSlaveDevice = new SlaveDevice( new DeviceRegister( bundle.getBooleanArray( "coil" ),
//                                                                bundle.getBooleanArray( "status" ),
//                                                                bundle.getShortArray( "hold" ),
//                                                                bundle.getShortArray( "input" ) ) );
        }
    }

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.fragment_slave_detail, container, false );

        initView( view );
        initData();
        initEvent();
        return view;
    }

    @Override
    protected void initView ( View view )
    {
        slave_detail_intensity = (TextView) view.findViewById( R.id.slave_detail_intensity );
        slave_detail_temperature = (TextView) view.findViewById( R.id.slave_detail_temperature );
        slave_detail_humidity = (TextView) view.findViewById( R.id.slave_detail_humidity );
        slave_detail_co2 = (TextView) view.findViewById( R.id.slave_detail_co2 );
        slave_detail_power = (CheckableImageButton) view.findViewById( R.id.slave_detail_power );
        slave_detail_rv = (RecyclerView) view.findViewById( R.id.slave_detail_rv );
        slave_detail_rv.setLayoutManager( new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, false ) );
    }

    @Override
    protected void initData ()
    {
        mBrightAdapter = new BrightAdapter( mSlaveDevice.getBrights() );
        slave_detail_rv.setAdapter( mBrightAdapter );
        mProtocol = new ModbusProtocol();
        mProtocol.setOnDeviceRegisterChangedListener( new ModbusProtocol.OnDeviceRegisterChangedListener() {
            @Override
            public void onDeviceRegisterChanged ( byte addr, DeviceRegister deviceRegister )
            {
                if ( addr == mAddress )
                {
                    mSlaveDevice = new SlaveDevice( deviceRegister );
                }
            }

            @Override
            public void onReadAll ( byte addr, DeviceRegister deviceRegister )
            {
                if ( addr == mAddress )
                {
                    mSlaveDevice = new SlaveDevice( deviceRegister );
                }
            }
        } );
        updateData();
    }

    @Override
    protected void initEvent ()
    {

    }

    private String getIntensity ( short value )
    {
        if ( value >= 0 && value <= 10000 )
        {
            return value + " Lux";
        }
        return "N/A";
    }

    private String getTemperature ( short value )
    {
        if ( value > 0 && value <= 1000 )
        {
            DecimalFormat df = new DecimalFormat( "00.0" );
            return df.format( ( (float) ( value - 250 ) )/10 ) + " ℃";
        }
        return "N/A";
    }

    private String getHumidity ( short value )
    {
        if ( value > 0 && value <= 1000 )
        {
            DecimalFormat df = new DecimalFormat( "00.0" );
            return df.format( ( (float)  value )/10 ) + " %";
        }
        return "N/A";
    }

    private String getCo2 ( short value )
    {
        if ( value > 0 && value <= 1000 )
        {
            DecimalFormat df = new DecimalFormat( "00.0" );
            return df.format( ( (float) value )/10 ) + " %";
        }
        return "N/A";
    }


    private void updateData ()
    {
        slave_detail_intensity.setText( getIntensity( mSlaveDevice.getIntensity() ) );
        slave_detail_temperature.setText( getTemperature( mSlaveDevice.getTemperature() ) );
        slave_detail_humidity.setText( getHumidity( mSlaveDevice.getHumidity() ) );
        slave_detail_co2.setText( getCo2( mSlaveDevice.getCo2() ) );
        slave_detail_power.setChecked( mSlaveDevice.getOn() );
        mBrightAdapter.notifyDataSetChanged();
    }

    private class BrightAdapter extends RecyclerView.Adapter<BrightAdapter.BrightViewHolder>
    {
        private short[] mBright;

        public BrightAdapter ( short[] bright )
        {
            mBright = bright;
        }

        @Override
        public BrightViewHolder onCreateViewHolder ( ViewGroup parent, int viewType )
        {
            BrightViewHolder holder = new BrightViewHolder( LayoutInflater.from( getContext() )
                                                                          .inflate( R.layout.item_bright, parent, false ) );
            return holder;
        }

        @Override
        public void onBindViewHolder ( BrightViewHolder holder, int position )
        {
            Log.e( TAG, "onBindViewHolder: " + position + "\t\t" + holder.getAdapterPosition() );
            if ( mBright[position] >= 0 && mBright[position] <= 1000 )
            {
                holder.tv_name.setText( "CH" + position );
                holder.tv_percent.setText( mBright[position]/10 + "%" );
                holder.sb_progress.setProgress( mBright[position]/10 );
            }
            else
            {
                holder.itemView.setVisibility( View.GONE );
            }
        }

        @Override
        public int getItemCount ()
        {
            return mBright == null ? 0 : mBright.length;
        }

        public class BrightViewHolder extends RecyclerView.ViewHolder
        {
            private TextView tv_name;
            private SeekBar sb_progress;
            private TextView tv_percent;

            public BrightViewHolder ( View itemView )
            {
                super( itemView );
                tv_name = (TextView) itemView.findViewById( R.id.item_bright_name );
                sb_progress = (SeekBar) itemView.findViewById( R.id.item_bright_progress );
                tv_percent = (TextView) itemView.findViewById( R.id.item_bright_percent );
            }
        }
    }
}
