package com.inledco.rs485.device;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.inledco.comman.BaseFragment;
import com.inledco.rs485.R;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SlaveTimerFragment extends BaseFragment
{
    private RecyclerView slave_timer_rv;
    private ImageButton slave_timer_add;

    private byte mAddress;
    private MasterDevice mMasterDevice;
    private SlaveDevice mSlaveDevice;
    private ModbusProtocol mProtocol;
    private TimerAdapter mTimerAdapter;

    public static SlaveTimerFragment newInstance ( MasterDevice masterDevice, byte addr, DeviceRegister deviceRegister )
    {
        SlaveTimerFragment frag = new SlaveTimerFragment();
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
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.fragment_slave_timer, container, false );

        initView( view );
        initData();
        initEvent();
        return view;
    }

    @Override
    protected void initView ( View view )
    {
        slave_timer_rv = (RecyclerView) view.findViewById( R.id.slave_timer_rv );
        slave_timer_add = (ImageButton) view.findViewById( R.id.slave_timer_add );
        slave_timer_rv.setLayoutManager( new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, false ) );
        slave_timer_rv.addItemDecoration( new DividerItemDecoration( getContext(), DividerItemDecoration.VERTICAL ) );
    }

    @Override
    protected void initData ()
    {
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
            mTimerAdapter = new TimerAdapter( mSlaveDevice.getTimers() );
            slave_timer_rv.setAdapter( mTimerAdapter );
        }
        mProtocol = new ModbusProtocol();
        mProtocol.setOnDeviceRegisterChangedListener( new ModbusProtocol.OnDeviceRegisterChangedListener() {
            @Override
            public void onDeviceRegisterChanged ( byte addr, DeviceRegister deviceRegister )
            {
                if ( addr == mAddress )
                {
                    mSlaveDevice = new SlaveDevice( deviceRegister );
                    mTimerAdapter = new TimerAdapter( mSlaveDevice.getTimers() );
                    slave_timer_rv.setAdapter( mTimerAdapter );
                }
            }

            @Override
            public void onReadAll ( byte addr, DeviceRegister deviceRegister )
            {
                if ( addr == mAddress )
                {
                    mSlaveDevice = new SlaveDevice( deviceRegister );
                    mTimerAdapter = new TimerAdapter( mSlaveDevice.getTimers() );
                    slave_timer_rv.setAdapter( mTimerAdapter );
                }
            }
        } );
    }

    @Override
    protected void initEvent ()
    {
        slave_timer_add.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick ( View view )
            {
                Timer tmr = new Timer( 0 );
                int hour = Calendar.getInstance().get( Calendar.HOUR );
                int min = Calendar.getInstance().get( Calendar.MINUTE );
                tmr.setTimer( (short) ( hour * 60 + min ) );
                showTimerDialog( -1, tmr );
            }
        } );
    }

    private void showTimerDialog ( final int regIdx, final Timer tmr )
    {
        final BottomSheetDialog dialog = new BottomSheetDialog( getContext() );
        View view = LayoutInflater.from( getContext() ).inflate( R.layout.dialog_timer, null );
        final TimePicker tp_show = (TimePicker) view.findViewById( R.id.dialog_timer_time );
        RadioGroup rg_show = (RadioGroup) view.findViewById( R.id.dialog_timer_rg );
        final CheckedTextView ctv_sun = (CheckedTextView) view.findViewById( R.id.dialog_timer_sun );
        final CheckedTextView ctv_mon = (CheckedTextView) view.findViewById( R.id.dialog_timer_mon );
        final CheckedTextView ctv_tue = (CheckedTextView) view.findViewById( R.id.dialog_timer_tue );
        final CheckedTextView ctv_wed = (CheckedTextView) view.findViewById( R.id.dialog_timer_wed );
        final CheckedTextView ctv_thu = (CheckedTextView) view.findViewById( R.id.dialog_timer_thu );
        final CheckedTextView ctv_fri = (CheckedTextView) view.findViewById( R.id.dialog_timer_fri );
        final CheckedTextView ctv_sat = (CheckedTextView) view.findViewById( R.id.dialog_timer_sat );
        final Switch sw_enable = (Switch) view.findViewById( R.id.dialog_timer_enable );
        ImageButton ib_delete = (ImageButton) view.findViewById( R.id.dialog_timer_delete );
        ImageButton ib_undo = (ImageButton) view.findViewById( R.id.dialog_timer_undo );
        ImageButton ib_check = (ImageButton) view.findViewById( R.id.dialog_timer_check );
        tp_show.setIs24HourView( true );
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
        {
            tp_show.setHour( tmr.getTimer()/60 );
            tp_show.setMinute( tmr.getTimer()%60 );
        }
        else
        {
            tp_show.setCurrentHour( tmr.getTimer()/60 );
            tp_show.setCurrentMinute( tmr.getTimer()%60 );
        }
        rg_show.check( tmr.isOn() ? R.id.dialog_timer_on : R.id.dialog_timer_off );
        ctv_sun.setChecked( tmr.isSun() ? true : false );
        ctv_mon.setChecked( tmr.isMon() ? true : false );
        ctv_tue.setChecked( tmr.isTue() ? true : false );
        ctv_wed.setChecked( tmr.isWed() ? true : false );
        ctv_thu.setChecked( tmr.isThu() ? true : false );
        ctv_fri.setChecked( tmr.isFri() ? true : false );
        ctv_sat.setChecked( tmr.isSat() ? true : false );
        sw_enable.setChecked( tmr.isEnable() );
        ib_delete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick ( View v )
            {
                if ( regIdx >= 0 && regIdx < mSlaveDevice.getTimers().length )
                {
                    int start = 16 + regIdx * 2;
                    byte[] bytes = new byte[]{ mAddress, ModbusProtocol.CMD_WRITE_HOLD_MULTI,
                                               (byte) ( start >> 8), (byte) ( start & 0xFF),
                                               0x00, 0x02, 0x04,
                                               (byte) 0xFF, (byte) 0xFF,
                                               (byte) 0xFF, (byte) 0xFF };
                    mProtocol.sendAsync( mMasterDevice.getXDevice(), bytes );
                }
                else
                {
                    dialog.dismiss();
                }
            }
        } );
        ib_undo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick ( View v )
            {
                dialog.dismiss();
            }
        } );
        ib_check.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick ( View v )
            {
                byte byte1 = 0;
                byte byte2 = 0;
                boolean[] week = new boolean[]{ ctv_sun.isChecked(),
                                                ctv_mon.isChecked(),
                                                ctv_tue.isChecked(),
                                                ctv_wed.isChecked(),
                                                ctv_thu.isChecked(),
                                                ctv_fri.isChecked(),
                                                ctv_sat.isChecked(),
                                                sw_enable.isChecked() };
                for ( int i = 0; i < week.length; i++ )
                {
                    if ( week[i] )
                    {
                        byte2 |= (1<<i);
                    }
                }
                int t;
                if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M )
                {
                    t = tp_show.getHour() * 60 + tp_show.getMinute();
                }
                else
                {
                    t = tp_show.getCurrentHour() * 60 + tp_show.getCurrentMinute();
                }
                byte byte3 = (byte) ( sw_enable.isChecked() ? ( 0x80 | ( t >> 8 ) ) : ( t >> 8 ) );
                byte byte4 = (byte) ( t & 0xFF);
                int start = -1;
                if ( regIdx >= 0 && regIdx < mSlaveDevice.getTimers().length )
                {
                    start = 16 + regIdx * 2;
                }
                else
                {
                    for ( int i = 0; i < mSlaveDevice.getTimers().length; i++ )
                    {
                        if ( !mSlaveDevice.getTimers()[i].isValid() )
                        {
                            start = 16 + i * 2;
                            break;
                        }
                    }
                }
                if ( start < 0 )
                {
                    dialog.dismiss();
                    Toast.makeText( getContext(), "Timers must be less than " + mSlaveDevice.getTimers().length, Toast.LENGTH_SHORT )
                         .show();
                    return;
                }
                byte[] bytes = new byte[]{ mAddress, ModbusProtocol.CMD_WRITE_HOLD_MULTI,
                                           (byte) ( start >> 8), (byte) ( start & 0xFF),
                                           0x00, 0x02, 0x04,
                                           (byte) 0xFF, (byte) 0xFF,
                                           (byte) 0xFF, (byte) 0xFF };
                mProtocol.sendAsync( mMasterDevice.getXDevice(), bytes );
                dialog.dismiss();
            }
        } );
        dialog.setContentView( view );
        dialog.setCanceledOnTouchOutside( false );
        dialog.show();
    }


    public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.TimerViewHolder>
    {
        private Timer[] mTimers;

        public TimerAdapter ( Timer[] timers )
        {
            mTimers = timers;
        }

        @Override
        public TimerViewHolder onCreateViewHolder ( ViewGroup parent, int viewType )
        {
            TimerViewHolder holder = new TimerViewHolder( LayoutInflater.from( getContext() )
                                                                        .inflate( R.layout.item_timer, parent, false ) );
            return holder;
        }

        @Override
        public void onBindViewHolder ( TimerViewHolder holder, int position )
        {
            Timer tmr = mTimers[position];
            if ( !tmr.isValid() )
            {
                holder.itemView.setVisibility( View.GONE );
                return;
            }
            DecimalFormat df = new DecimalFormat( "00" );
            holder.tv_chn.setText( "CH" + tmr.getIndex() );
            holder.tv_tmr.setText( df.format( tmr.getTimer()/60 ) + " : " + df.format( tmr.getTimer()%60 ) );
            holder.tv_action.setText( tmr.isOn() ? R.string.turn_on : R.string.turn_off );
            StringBuffer sb = new StringBuffer();
            if ( tmr.isSun() )
            {
                sb.append( R.string.week_sun ).append( "  " );
            }
            if ( tmr.isMon() )
            {
                sb.append( R.string.week_mon ).append( "  " );
            }
            if ( tmr.isTue() )
            {
                sb.append( R.string.week_tue ).append( "  " );
            }
            if ( tmr.isWed() )
            {
                sb.append( R.string.week_wed ).append( "  " );
            }
            if ( tmr.isThu() )
            {
                sb.append( R.string.week_thu ).append( "  " );
            }
            if ( tmr.isFri() )
            {
                sb.append( R.string.week_fri ).append( "  " );
            }
            if ( tmr.isSat() )
            {
                sb.append( R.string.week_sat ).append( "  " );
            }
            holder.tv_week.setText( sb );
            holder.sw_enable.setChecked( tmr.isEnable() );
            holder.sw_enable.setClickable( false );
        }

        @Override
        public int getItemCount ()
        {
            return mTimers == null ? 0 : mTimers.length;
        }

        public class TimerViewHolder extends RecyclerView.ViewHolder
        {
            private TextView tv_chn;
            private TextView tv_tmr;
            private Switch sw_enable;
            private TextView tv_action;
            private TextView tv_week;

            public TimerViewHolder ( View itemView )
            {
                super( itemView );
                tv_chn = (TextView) itemView.findViewById( R.id.item_timer_chn );
                tv_tmr = (TextView) itemView.findViewById( R.id.item_timer_time );
                sw_enable = (Switch) itemView.findViewById( R.id.item_timer_enable );
                tv_action = (TextView) itemView.findViewById( R.id.item_timer_action );
                tv_week = (TextView) itemView.findViewById( R.id.item_timer_week );
            }
        }
    }
}
