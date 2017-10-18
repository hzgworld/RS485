package com.inledco.rs485.device;

import android.os.CountDownTimer;
import android.support.annotation.NonNull;

import com.inledco.comman.LogUtil;
import com.inledco.xlinkcloud.XlinkCloudManager;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.bean.DataPoint;
import io.xlink.wifi.sdk.bean.EventNotify;
import io.xlink.wifi.sdk.listener.XlinkNetListener;

/**
 * Created by liruya on 2017/9/29.
 */

public class ModbusProtocol
{
    public static final byte CMD_READ_ALL = 0x00;
    public static final byte CMD_READ_COIL = 0x01;
    public static final byte CMD_READ_STATUS = 0x02;
    public static final byte CMD_READ_HOLD = 0x03;
    public static final byte CMD_READ_INPUT = 0x04;
    public static final byte CMD_WRITE_COIL_SINGLE = 0x05;
    public static final byte CMD_WRITE_HOLD_SINGLE = 0x06;
    public static final byte CMD_WRITE_COIL_MULTI = 0x0F;
    public static final byte CMD_WRITE_HOLD_MULTI = 0x10;

    private byte[] mTxBytes;
    private Map< Byte, DeviceRegister > mDeviceRegisterMap;
    private boolean mSendLock;
    private int mSendInterval;
    private CountDownTimer mTimeoutTimer;
    private XlinkNetListener mXlinkNetListener;
    private OnDeviceRegisterChangedListener mRegisterChangedListener;

    public ModbusProtocol ()
    {
        this( 100 );
    }

    public ModbusProtocol ( int sendInterval )
    {
        if ( sendInterval < 80 || sendInterval > 4000 )
        {
            mSendInterval = 100;
        }
        else
        {
            mSendInterval = sendInterval;
        }
        mDeviceRegisterMap = new HashMap<>();
        mTimeoutTimer = new CountDownTimer( mSendInterval, mSendInterval ) {
            @Override
            public void onTick ( long millisUntilFinished )
            {

            }

            @Override
            public void onFinish ()
            {
                mSendLock = false;
            }
        };
    }

    private XlinkCloudManager.OnSendPipeDataListener mSendPipeDataListener = new XlinkCloudManager.OnSendPipeDataListener() {
        @Override
        public void onSendPipeDataSuccess ()
        {
            mTimeoutTimer.start();
        }

        @Override
        public void onSendPipeDataFailure ( int error )
        {
            mSendLock = false;
        }
    };

    public void start ()
    {
        mXlinkNetListener = new XlinkNetListener() {
            @Override
            public void onStart ( int i )
            {

            }

            @Override
            public void onLogin ( int i )
            {

            }

            @Override
            public void onLocalDisconnect ( int i )
            {

            }

            @Override
            public void onDisconnect ( int i )
            {

            }

            @Override
            public void onRecvPipeData ( short i, XDevice xDevice, byte[] bytes )
            {
                receive( bytes );
            }

            @Override
            public void onRecvPipeSyncData ( short i, XDevice xDevice, byte[] bytes )
            {
                receive( bytes );
            }

            @Override
            public void onDeviceStateChanged ( XDevice xDevice, int i )
            {

            }

            @Override
            public void onDataPointUpdate ( XDevice xDevice, List< DataPoint > list, int i )
            {

            }

            @Override
            public void onEventNotify ( EventNotify eventNotify )
            {

            }
        };
        XlinkCloudManager.getInstance().addXlinkListener( mXlinkNetListener );
    }

    public void stop ()
    {
        if ( mXlinkNetListener != null )
        {
            XlinkCloudManager.getInstance().removeXlinkListener( mXlinkNetListener );
        }
    }

    public void setOnDeviceRegisterChangedListener ( OnDeviceRegisterChangedListener listener )
    {
        mRegisterChangedListener = listener;
    }

    public void setRegisterCoil ( @NonNull DeviceRegister deviceRegister, int start, @NonNull boolean[] values )
    {
        for ( int i = start; i < start + values.length; i++ )
        {
            deviceRegister.setCoil( i, values[i - start] );
        }
    }

    public void setRegisterStatus ( @NonNull DeviceRegister deviceRegister, int start, @NonNull boolean[] values )
    {
        for ( int i = start; i < start + values.length; i++ )
        {
            deviceRegister.setStatus( i, values[i -start] );
        }
    }

    public void setRegisterHold ( @NonNull DeviceRegister deviceRegister, int start, @NonNull short[] values )
    {
        for ( int i = start; i < start + values.length; i++ )
        {
            deviceRegister.setHold( i, values[i - start] );
        }
    }

    public void setRegisterInput( @NonNull DeviceRegister deviceRegister, int start, @NonNull short[] values )
    {
        for ( int i = start; i < start + values.length; i++ )
        {
            deviceRegister.setInput( i, values[i - start] );
        }
    }

    private boolean[] bytesToBits ( byte[] bytes, int cnt )
    {
        boolean[] result = new boolean[cnt];
        for ( int i = 0; i < cnt; i++ )
        {
            int val = bytes[i>>3] & 0xFF;
            if ( ( val & ( 1 << ( i & 0x07 ) ) ) == 0x00 )
            {
                result[i] = false;
            }
            else
            {
                result[i] = true;
            }
        }
        return result;
    }

    private short[] bytesToWords ( byte[] bytes, int cnt )
    {
        short[] result = new short[cnt/2];
        for ( int i = 0; i < cnt; i += 2 )
        {
            result[i/2] = (short) ( ( bytes[i] & 0xFF ) << 8 | ( bytes[i + 1] & 0xFF ));
        }
        return result;
    }

    public synchronized void send ( @NonNull final XDevice xDevice, @NonNull final byte[] bytes )
    {
        mTxBytes = Arrays.copyOf( bytes, bytes.length + 2 );
        int crc = CRC16Util.getCRC( bytes );
        mTxBytes[mTxBytes.length-2] = (byte) ( crc & 0xFF );
        mTxBytes[mTxBytes.length-1] = (byte) ( crc >> 8 );
        while ( mSendLock )
        {
            try
            {
                Thread.sleep( 10 );
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }
        }
        mSendLock = true;
        XlinkCloudManager.getInstance().sendPipeData( xDevice, mTxBytes, mSendPipeDataListener );
    }

    public synchronized void sendAsync ( @NonNull final XDevice xDevice, @NonNull final byte[] bytes )
    {
        new Thread( new Runnable() {
            @Override
            public void run ()
            {
                send( xDevice, bytes );
            }
        } ).start();
    }

    public byte[] checkDeviceOnline ( byte addr )
    {
        byte[] bytes = new byte[]{ addr, CMD_READ_INPUT, 0x00, 0x01, 0x00, 0x03 };
        return bytes;
    }

    public byte[] readAll ( byte addr )
    {
        byte[] bytes = new byte[]{ addr, CMD_READ_ALL, 0x00, 0x00, 0x00, 0x00 };
        return bytes;
    }

    public byte[] syncTime ( byte addr )
    {
        short year = (short) Calendar.getInstance().get( Calendar.YEAR );
        byte month = (byte) Calendar.getInstance().get( Calendar.MONTH );
        byte day = (byte) Calendar.getInstance().get( Calendar.DAY_OF_MONTH );
        byte week = (byte) Calendar.getInstance().get( Calendar.DAY_OF_WEEK );
        byte hour = (byte) Calendar.getInstance().get( Calendar.HOUR_OF_DAY );
        byte minute = (byte) Calendar.getInstance().get( Calendar.MINUTE );
        byte second = (byte) Calendar.getInstance().get( Calendar.SECOND );

        byte[] bytes = new byte[]{ addr, CMD_WRITE_HOLD_MULTI, 0x00, 0x01, 0x00, 0x07, 0x0E,
                                   (byte) ( year >> 8), (byte) ( year & 0xFF), 0x00, month, 0x00,
                                   day, 0x00, week, 0x00, hour, 0x00, minute, 0x00, second };
        return bytes;
    }

    public byte[] syncAllTime ()
    {
        return syncTime( (byte) 0x00 );
    }

    public byte[] turnOn ( byte addr )
    {
        byte[] bytes = new byte[]{ addr, CMD_WRITE_COIL_SINGLE, 0x00, 0x00, (byte) 0xFF, 0x00 };
        return bytes;
    }

    public byte[] turnOff ( byte addr )
    {
        byte[] bytes = new byte[]{ addr, CMD_WRITE_COIL_SINGLE, 0x00, 0x00, 0x00, 0x00 };
        return bytes;
    }

    public byte[] turnAllOn ()
    {
        return turnOn( (byte) 0x00 );
    }

    public byte[] turnAllOff ()
    {
        return turnOff( (byte) 0x00 );
    }

    public void receive ( byte[] bytes )
    {
        LogUtil.e( "TAG", "receive: " + Arrays.toString( bytes ) );
        if ( mTxBytes != null && bytes != null && bytes.length >= 6 && mTxBytes[0] == bytes[0] )
        {
            if (  mTxBytes[1] == bytes[1] && CRC16Util.getCRC( bytes ) == 0x00 )
            {
                int len;
                int start;
                int cnt;
                byte[] src;
                if ( !mDeviceRegisterMap.containsKey( bytes[0] ) || mDeviceRegisterMap.get( bytes[0] ) == null )
                {
                    mDeviceRegisterMap.put( bytes[0], new DeviceRegister() );
                }
                switch ( bytes[1] )
                {
                    case CMD_READ_ALL:
                        mDeviceRegisterMap.put( bytes[0], new DeviceRegister.Builder().build( bytes ) );
                        if ( mRegisterChangedListener != null )
                        {
                            mRegisterChangedListener.onReadAll( bytes[0], mDeviceRegisterMap.get( bytes[0] ) );
                        }
                        break;
                    case CMD_READ_COIL:
                        len = ( mTxBytes[4] & 0xFF ) << 8 | ( mTxBytes[5] & 0xFF );
                        start = ( mTxBytes[2] & 0xFF ) << 8 | ( mTxBytes[3] & 0xFF );
                        cnt = ( len & 0x07 ) == 0 ? ( len >> 3 ) : ( ( len >> 3 ) + 1 );
                        if ( bytes[2] == cnt && bytes.length == bytes[2] + 5 )
                        {
                            src = Arrays.copyOfRange( bytes, 3, bytes.length - 2 );
                            setRegisterCoil( mDeviceRegisterMap.get( bytes[0] ), start, bytesToBits( src, cnt ) );
                            if ( mRegisterChangedListener != null )
                            {
                                mRegisterChangedListener.onDeviceRegisterChanged( bytes[0], mDeviceRegisterMap.get( bytes[0] ) );
                            }
                        }
                        break;
                    case CMD_READ_STATUS:
                        len = ( mTxBytes[4] & 0xFF ) << 8 | ( mTxBytes[5] & 0xFF );
                        start = ( mTxBytes[2] & 0xFF ) << 8 | ( mTxBytes[3] & 0xFF );
                        cnt = ( len & 0x07 ) == 0 ? ( len >> 3 ) : ( ( len >> 3 ) + 1 );
                        if ( bytes[2] == cnt && bytes.length == bytes[2] + 5 )
                        {
                            src = Arrays.copyOfRange( bytes, 3, bytes.length - 2 );
                            setRegisterStatus( mDeviceRegisterMap.get( bytes[0] ), start, bytesToBits( src, cnt ) );
                            if ( mRegisterChangedListener != null )
                            {
                                mRegisterChangedListener.onDeviceRegisterChanged( bytes[0], mDeviceRegisterMap.get( bytes[0] ) );
                            }
                        }
                        break;
                    case CMD_READ_HOLD:
                        len = ( mTxBytes[4] & 0xFF ) << 8 | ( mTxBytes[5] & 0xFF );
                        start = ( mTxBytes[2] & 0xFF ) << 8 | ( mTxBytes[3] & 0xFF );
                        cnt = len * 2;
                        if ( bytes[2] == cnt && bytes.length == bytes[2] + 5 )
                        {
                            src = Arrays.copyOfRange( bytes, 3, bytes.length - 2 );
                            setRegisterHold( mDeviceRegisterMap.get( bytes[0] ), start, bytesToWords( src, cnt ) );
                            if ( mRegisterChangedListener != null )
                            {
                                mRegisterChangedListener.onDeviceRegisterChanged( bytes[0], mDeviceRegisterMap.get( bytes[0] ) );
                            }
                        }
                        break;
                    case CMD_READ_INPUT:
                        len = ( mTxBytes[4] & 0xFF ) << 8 | ( mTxBytes[5] & 0xFF );
                        start = ( mTxBytes[2] & 0xFF ) << 8 | ( mTxBytes[3] & 0xFF );
                        cnt = len * 2;
                        if ( bytes[2] == cnt && bytes.length == bytes[2] + 5 )
                        {
                            src = Arrays.copyOfRange( bytes, 3, bytes.length - 2 );
                            setRegisterInput( mDeviceRegisterMap.get( bytes[0] ), start, bytesToWords( src, cnt ) );
                            if ( mRegisterChangedListener != null )
                            {
                                mRegisterChangedListener.onDeviceRegisterChanged( bytes[0], mDeviceRegisterMap.get( bytes[0] ) );
                            }
                        }
                        break;
                    case CMD_WRITE_COIL_SINGLE:
                        start = ( mTxBytes[2] & 0xFF ) << 8 | ( mTxBytes[3] & 0xFF );
                        if ( bytes[2] == mTxBytes[2] && bytes[3] == mTxBytes[3] && bytes[5] == 0x00 )
                        {
                            boolean b = false;
                            if ( bytes[4] == 0xFF )
                            {
                                b = true;
                            }
                            setRegisterCoil( mDeviceRegisterMap.get( bytes[0] ), start, new boolean[]{ b } );
                            if ( mRegisterChangedListener != null )
                            {
                                mRegisterChangedListener.onDeviceRegisterChanged( bytes[0], mDeviceRegisterMap.get( bytes[0] ) );
                            }
                        }
                        break;
                    case CMD_WRITE_HOLD_SINGLE:
                        start = ( mTxBytes[2] & 0xFF ) << 8 | ( mTxBytes[3] & 0xFF );
                        if ( bytes[2] == mTxBytes[2] && bytes[3] == mTxBytes[3] )
                        {
                            short val = (short) ( ( bytes[4] & 0xFF ) << 8 | ( bytes[5] & 0xFF ) );
                            setRegisterHold( mDeviceRegisterMap.get( bytes[0] ), start, new short[]{ val } );
                            if ( mRegisterChangedListener != null )
                            {
                                mRegisterChangedListener.onDeviceRegisterChanged( bytes[0], mDeviceRegisterMap.get( bytes[0] ) );
                            }
                        }
                        break;
                    case CMD_WRITE_COIL_MULTI:
                        if ( mTxBytes[2] == bytes[2] && mTxBytes[3] == bytes[3] && mTxBytes[4] == bytes[4] && mTxBytes[5] == bytes[5] )
                        {
                            start = ( mTxBytes[2] & 0xFF ) << 8 | ( mTxBytes[3] & 0xFF );
                            src = Arrays.copyOfRange( mTxBytes, 7, mTxBytes.length - 2 );
                            setRegisterCoil( mDeviceRegisterMap.get( bytes[0] ), start, bytesToBits( src, mTxBytes[6] ) );
                            if ( mRegisterChangedListener != null )
                            {
                                mRegisterChangedListener.onDeviceRegisterChanged( bytes[0], mDeviceRegisterMap.get( bytes[0] ) );
                            }
                        }
                        break;
                    case CMD_WRITE_HOLD_MULTI:
                        if ( mTxBytes[2] == bytes[2] && mTxBytes[3] == bytes[3] && mTxBytes[4] == bytes[4] && mTxBytes[5] == bytes[5] )
                        {
                            start = ( mTxBytes[2] & 0xFF ) << 8 | ( mTxBytes[3] & 0xFF );
                            src = Arrays.copyOfRange( mTxBytes, 7, mTxBytes.length - 2 );
                            setRegisterHold( mDeviceRegisterMap.get( bytes[0] ), start, bytesToWords( src, mTxBytes[6] ) );
                            if ( mRegisterChangedListener != null )
                            {
                                mRegisterChangedListener.onDeviceRegisterChanged( bytes[0], mDeviceRegisterMap.get( bytes[0] ) );
                            }
                        }
                        break;
                }
            }
            else if ( mTxBytes[1] == ( bytes[1] & 0x7F ) )
            {

            }
        }
    }

    public interface OnDeviceRegisterChangedListener
    {
        void onDeviceRegisterChanged ( byte addr, DeviceRegister deviceRegister );

        void onReadAll ( byte addr, DeviceRegister deviceRegister );
    }
}
