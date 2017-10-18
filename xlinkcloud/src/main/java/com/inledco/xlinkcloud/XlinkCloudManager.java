package com.inledco.xlinkcloud;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.inledco.okhttp.OkHttpManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.bean.DataPoint;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;
import io.xlink.wifi.sdk.listener.ScanDeviceListener;
import io.xlink.wifi.sdk.listener.SendPipeListener;
import io.xlink.wifi.sdk.listener.SetDataPointListener;
import io.xlink.wifi.sdk.listener.SetDeviceAccessKeyListener;
import io.xlink.wifi.sdk.listener.XlinkNetListener;

/**
 * Created by liruya on 2017/9/18.
 */

public class XlinkCloudManager
{
    private static final String TAG = "XlinkCloudManager";

    private final String SOURCE_ANDROID = "2";

    private final String XLINK_HOST = "http://api2.xlink.cn";
    // url
    public final String REGISTER_URL = XLINK_HOST + "/v2/user_register";
    public final String LOGIN_URL = XLINK_HOST + "/v2/user_auth";
    public final String FORGET_URL = XLINK_HOST + "/v2/user/password/forgot";
    //.管理员（用户）获取所有设备分享请求列表
    public final String SHARE_LIST_URL = XLINK_HOST + "/v2/share/device/list";
    public final String GET_USERINFO_URL = XLINK_HOST + "/v2/user/{user_id}";
    //获取某个用户绑定的设备列表。
    public final String SUBSCRIBE_LIST_URL = XLINK_HOST + "/v2/user/{user_id}/subscribe/devices";
    //设备管理员分享设备给指定用户
    public final String SHARE_DEVICE_URL = XLINK_HOST + "/v2/share/device";
    //用户拒绝设备分享
    public final String DENY_SHARE_URL = XLINK_HOST + "/v2/share/device/deny";
    //用户确认设备分享
    public final String ACCEPCT_SHARE_URL = XLINK_HOST + "/v2/share/device/accept";
    //获取设备信息
    public final String GET_DEVICE_URL = XLINK_HOST + "/v2/product/{product_id}/device/{device_id}";
    //订阅设备（待定）
    public final String SUBSCRIBE_URL = XLINK_HOST + "/v2/user/{user_id}/subscribe";
    //修改用户信息
    public final String MODIFY_USER_URL = XLINK_HOST + "/v2/user/{user_id}";
    //重置密码
    public final String RESET_PASSWORD_URL = XLINK_HOST + "/v2/user/password/reset";
    //获取数据端点列表
    public final String GET_DATAPOINT_URL = XLINK_HOST + "/v2/product/{product_id}/datapoints";
    //取消订阅设备
    public final String UNSUBSCRIBE_URL = XLINK_HOST + "/v2/user/{user_id}/unsubscribe";
    //在云端注册设备
    public final String REGISTER_DEVICE_URL = XLINK_HOST + "/v2/user/{user_id}/register_device";

    //.管理员或用户删除这条分享记录
    public final String DELETE_SHARE_URL = XLINK_HOST + "/v2/share/device/delete/{invite_code}";

    //检查固件版本
    public final String CHECK_UPDATE_URL = "http://app.xlink.cn/v1/user/device/version";
    //固件升级
    public final String UPGRADE_URL = "http://app.xlink.cn/v1/user/device/upgrade";

    private static String company_id;

    private UserInfo mUserInfo;

    public static XlinkCloudManager getInstance ()
    {
        return Holder.INSTANCE;
    }

    public static String getCompany_id ()
    {
        return company_id;
    }

    public static void setCompany_id ( String company_id )
    {
        XlinkCloudManager.company_id = company_id;
    }

    public UserInfo getUserInfo ()
    {
        return mUserInfo;
    }

    public void setUserInfo ( UserInfo userInfo )
    {
        mUserInfo = userInfo;
    }

    public static void initialize ( @NonNull Context context, @NonNull String cid )
    {
        company_id = cid;
        XlinkAgent.init( context );
    }

    public static void initDevice ( XDevice xDevice )
    {
        XlinkAgent.getInstance().initDevice( xDevice );
    }

    /**
     * http 邮箱注册接口
     *
     * @param mail 用户 邮箱
     * @param pwd 密码
     */
    public void registerUserByMail ( String mail, String pwd, OkHttpManager.ResultCallback callback )
    {
        if ( TextUtils.isEmpty( company_id ) )
        {
            return;
        }
        Map< String, String > params = new HashMap<>();
        params.put( "email", mail );
        params.put( "corp_id", company_id );
        params.put( "password", pwd );
        params.put( "source", SOURCE_ANDROID );
        OkHttpManager.getInstance()
                     .post( REGISTER_URL, null, params, callback );
    }

    /**
     * http 邮箱登录接口
     *
     * @param mail 用户 邮箱
     * @param pwd 密码
     */
    public void login ( String mail, String pwd, OkHttpManager.ResultCallback callback )
    {
        if ( TextUtils.isEmpty( company_id ) )
        {
            return;
        }
        Map< String, String > params = new HashMap<>();
        params.put( "email", mail );
        params.put( "corp_id", company_id );
        params.put( "password", pwd );
        OkHttpManager.getInstance()
                     .post( LOGIN_URL, null, params, callback );
    }

    /**
     * http //.管理员（用户）获取所有设备分享请求列表
     */
    public void getShareList ( String access_token, OkHttpManager.ResultCallback callback )
    {
        Map< String, String > headrs = new HashMap<>();
        headrs.put( "Access-Token", access_token );
        OkHttpManager.getInstance()
                     .get( SHARE_LIST_URL, headrs, callback );
    }

    /**
     * 获取用户详细信息
     */
    public void getUserInfo ( int userId, String access_token, OkHttpManager.ResultCallback callback )
    {
        String url = GET_USERINFO_URL.replace( "{user_id}", userId + "" );
        Map< String, String > headrs = new HashMap<>();
        headrs.put( "Access-Token", access_token );
        OkHttpManager.getInstance()
                     .get( url, headrs, callback );
    }

    /**
     * .修改用户信息
     *
     * @param userId userId
     */
    public void modifyUser ( int userId, String nickname, String access_token, OkHttpManager.ResultCallback callback )
    {
        String url = MODIFY_USER_URL.replace( "{user_id}", userId + "" );
        Map< String, String > headrs = new HashMap<>();
        headrs.put( "Access-Token", access_token );
        Map< String, String > params = new HashMap<>();
        params.put( "nickname", nickname );
        OkHttpManager.getInstance()
                     .put( url, headrs, params, callback );
    }

    /**
     * http 忘记密码
     *
     * @param mail 用户 邮箱
     */
    public void forgetPassword ( String mail, OkHttpManager.ResultCallback callback )
    {
        if ( TextUtils.isEmpty( company_id ) )
        {
            return;
        }
        Map< String, String > params = new HashMap<>();
        params.put( "email", mail );
        params.put( "corp_id", company_id );
        OkHttpManager.getInstance()
                     .post( FORGET_URL, null, params, callback );
    }

    /**
     * .重置密码
     */
    public void resetPassword ( String newPasswd, String oldPasswd, String access_token, OkHttpManager.ResultCallback callback )
    {
        Map< String, String > headrs = new HashMap<>();
        headrs.put( "Access-Token", access_token );
        Map< String, String > params = new HashMap<>();
        params.put( "old_password", oldPasswd );
        params.put( "new_password", newPasswd );
        OkHttpManager.getInstance()
                     .put( RESET_PASSWORD_URL, headrs, params, callback );
    }

    /**
     * 获取数据端点信息
     */
    public void getDatapoints ( String pid, String access_token, OkHttpManager.ResultCallback callback )
    {
        String url = GET_DATAPOINT_URL.replace( "{product_id}", pid );
        Map< String, String > headrs = new HashMap<>();
        headrs.put( "Access-Token", access_token );
        OkHttpManager.getInstance()
                     .get( url, headrs, callback );
    }

    /**
     * 设备管理员分享设备给指定用户
     *
     * @param mail 用户 邮箱
     */
    public void shareDevice ( String mail, int deviceId, String access_token, OkHttpManager.ResultCallback callback )
    {
        Map< String, String > headrs = new HashMap<>();
        headrs.put( "Access-Token", access_token );
        Map< String, String > params = new HashMap<>();
        params.put( "user", mail );
        params.put( "expire", "7200" );
        params.put( "mode", "email" );
        params.put( "device_id", deviceId + "" );
        OkHttpManager.getInstance()
                     .post( SHARE_DEVICE_URL, headrs, params, callback );
    }

    /**
     * 用户接受设备分享
     *
     * @param inviteCode 分享ID
     */
    public void acceptShare ( String inviteCode, String access_token, OkHttpManager.ResultCallback callback )
    {
        Map< String, String > headrs = new HashMap<>();
        headrs.put( "Access-Token", access_token );
        Map< String, String > params = new HashMap<>();
        params.put( "invite_code", inviteCode );
        OkHttpManager.getInstance()
                     .post( ACCEPCT_SHARE_URL, headrs, params, callback );
    }

    /**
     * 用户拒绝设备分享
     *
     * @param inviteCode 分享ID
     */
    public void denyShare ( String inviteCode, String access_token, OkHttpManager.ResultCallback callback )
    {
        Map< String, String > headrs = new HashMap<>();
        headrs.put( "Access-Token", access_token );
        Map< String, String > params = new HashMap<>();
        params.put( "invite_code", inviteCode );
        OkHttpManager.getInstance()
                     .post( DENY_SHARE_URL, headrs, params, callback );
    }

    /**
     * 订阅设备
     *
     * @param userId userId
     */
    public void subscribe ( int userId, String productId, int deviceId, String access_token, OkHttpManager.ResultCallback callback )
    {
        String url = SUBSCRIBE_URL.replace( "{user_id}", userId + "" );
        Map< String, String > headrs = new HashMap<>();
        headrs.put( "Access-Token", access_token );
        Map< String, String > params = new HashMap<>();
        params.put( "product_id", productId );
        params.put( "device_id", deviceId + "" );
        OkHttpManager.getInstance()
                     .post( url, headrs, params, callback );
    }

    /**
     * 取消订阅设备
     *
     * @param userId userId
     */
    public void unsubscribe ( int userId, int deviceId, String access_token, OkHttpManager.ResultCallback callback )
    {
        String url = UNSUBSCRIBE_URL.replace( "{user_id}", userId + "" );
        Map< String, String > headrs = new HashMap<>();
        headrs.put( "Access-Token", access_token );
        Map< String, String > params = new HashMap<>();
        params.put( "device_id", deviceId + "" );
        OkHttpManager.getInstance()
                     .post( url, headrs, params, callback );
    }

    /**
     * 获取设备信息
     *
     * @param deviceId 设备ID
     */
    public void getDevice ( String productId, int deviceId, String access_token, OkHttpManager.ResultCallback callback )
    {
        String url = GET_DEVICE_URL.replace( "{device_id}", deviceId + "" )
                                   .replace( "{product_id}", productId );
        Map< String, String > headrs = new HashMap<>();
        headrs.put( "Access-Token", access_token );
        OkHttpManager.getInstance()
                     .get( url, headrs, callback );
    }

    /**
     * http //.获取某个用户绑定的设备列表。
     */
    public void getSubscribeList ( int userId, String access_token, OkHttpManager.ResultCallback callback )
    {
        String url = SUBSCRIBE_LIST_URL.replace( "{user_id}", userId + "" );
        Map< String, String > headrs = new HashMap<>();
        headrs.put( "Access-Token", access_token );
        OkHttpManager.getInstance()
                     .get( url, headrs, callback );
    }

    /**
     * 删除分享
     */
    public void deleteShare ( String inviteCode, String access_token, OkHttpManager.ResultCallback callback )
    {
        String url = DELETE_SHARE_URL.replace( "{invite_code}", inviteCode );
        Map< String, String > headrs = new HashMap<>();
        headrs.put( "Access-Token", access_token );
        OkHttpManager.getInstance()
                     .delete( url, headrs, null, callback );
    }

    public void registerDevice ( int userId, String productId, String access_token, String mac, OkHttpManager.ResultCallback callback )
    {
        String url = REGISTER_DEVICE_URL.replace( "{user_id}", userId + "" );
        Map< String, String > headrs = new HashMap<>();
        headrs.put( "Access-Token", access_token );
        Map< String, String > params = new HashMap<>();
        params.put( "product_id", productId );
        params.put( "mac", mac );
        OkHttpManager.getInstance()
                     .post( url, headrs, params, callback );
    }

    public void start()
    {
        XlinkAgent.getInstance().start();
    }

    public void stop()
    {
        XlinkAgent.getInstance().stop();
    }

    public void addXlinkListener( XlinkNetListener listener )
    {
        XlinkAgent.getInstance().addXlinkListener( listener );
    }

    public void removeXlinkListener( XlinkNetListener listener )
    {
        XlinkAgent.getInstance().removeListener( listener );
    }

    public void registerUserByMail ( String mail, String pwd, final OnRegisterListener listener )
    {
        registerUserByMail( mail, pwd, new OkHttpManager.ResultCallback< String, XlinkError >() {
            @Override
            public void onSuccess ( String response )
            {
                if ( listener != null )
                {
                    listener.onRegisterSuccess();
                }
            }

            @Override
            public void onError ( XlinkError error )
            {
                if ( listener != null )
                {
                    listener.onRegisterFailure( error );
                }
            }
        } );
    }

    public void login ( String mail, String pwd, final OnLoginListener listener )
    {
        login( mail, pwd, new OkHttpManager.ResultCallback< UserInfo, XlinkError >()
        {
            @Override
            public void onSuccess ( UserInfo response )
            {
                if ( listener != null )
                {
                    listener.onLoginSuccess( response );
                }
            }

            @Override
            public void onError ( XlinkError error )
            {
                if ( listener != null )
                {
                    listener.onLoginFailure( error );
                }
            }
        } );
    }

    public void scanDeviceByProductId ( String pid, final OnScanDeviceListener listener )
    {
        int ret = XlinkAgent.getInstance().scanDeviceByProductId( pid, new ScanDeviceListener() {
            @Override
            public void onGotDeviceByScan ( XDevice xDevice )
            {
                if ( listener != null )
                {
                    listener.onScanDevice( xDevice );
                }
            }
        } );
        if ( ret < 0 && listener != null )
        {
            listener.onScanFailure( ret );
        }
    }

    public void registerDevice ( int userId, String productId, String access_token, String mac, final OnRegisterDeviceListener listener )
    {
        registerDevice( userId, productId, access_token, mac, new OkHttpManager.ResultCallback< String, XlinkError >()
        {
            @Override
            public void onSuccess ( String response )
            {
                if ( listener != null )
                {
                    listener.onRegisterDeviceSuccess();
                }
            }

            @Override
            public void onError ( XlinkError error )
            {
                if ( listener != null )
                {
                    if ( error.getHttpError().getCode() == XlinkConstants.DEVICE_MAC_ADDRESS_EXISTS )
                    {
                        listener.onRegisterDeviceSuccess();
                    }
                    else
                    {
                        listener.onRegisterDeviceFailure( error.getHttpError().getCode() );
                    }
                }
            }
        } );
    }

    public void connectDevice ( XDevice device, final OnConnectListener listener )
    {
        int ret = XlinkAgent.getInstance().connectDevice( device, new ConnectDeviceListener() {
            @Override
            public void onConnectDevice ( XDevice xDevice, int i )
            {
                Log.e( TAG, "onConnectDevice0: " + i );
                if ( listener != null )
                {
                    switch ( i )
                    {
                        case XlinkCode.DEVICE_STATE_LOCAL_LINK:
                            listener.onConnectLocal();
                            break;

                        case XlinkCode.DEVICE_STATE_OUTER_LINK:
                            listener.onConnectCloud();
                            break;

                        default:
                            listener.onConnectFailure( i );
                            break;
                    }
                }
            }
        } );
        Log.e( TAG, "connectDevice1: " + ret  );
        if ( ret < 0 && listener != null )
        {
            listener.onConnectFailure( ret );
        }
    }

    public void setAccessKey ( XDevice device, int key, final OnSetAccessKeyListener listener )
    {
        int ret = XlinkAgent.getInstance().setDeviceAccessKey( device, key, new SetDeviceAccessKeyListener() {
            @Override
            public void onSetLocalDeviceAccessKey ( XDevice xDevice, int i, int i1 )
            {
                if ( listener != null )
                {
                    if ( i == XlinkCode.SUCCEED )
                    {
                        listener.onSetAccessKeySuccess();
                    }
                    else
                    {
                        listener.onSetAccessKeyFailure( i );
                    }
                }

            }
        } );
        if ( ret < 0 && listener != null )
        {
            listener.onSetAccessKeyFailure( ret );
        }
    }

    public void sendPipeData ( XDevice device, byte[] bytes, final OnSendPipeDataListener listener )
    {
        int ret = XlinkAgent.getInstance().sendPipeData( device, bytes, new SendPipeListener() {
            @Override
            public void onSendLocalPipeData ( XDevice xDevice, int i, int i1 )
            {
                if ( listener != null )
                {
                    if ( i == XlinkCode.SUCCEED )
                    {
                        listener.onSendPipeDataSuccess();
                    }
                    else
                    {
                        listener.onSendPipeDataFailure( i );
                    }
                }
            }
        } );
        if ( ret < 0 && listener != null )
        {
            listener.onSendPipeDataFailure( ret );
        }
    }

    public void setDataPoints ( XDevice device, List< DataPoint > datapoints, final OnSetDataPointListener listener )
    {
        int ret = XlinkAgent.getInstance().setDataPoint( device, datapoints, new SetDataPointListener() {
            @Override
            public void onSetDataPoint ( XDevice xDevice, int i, int i1 )
            {
                if ( listener != null )
                {
                    if ( i == XlinkCode.SUCCEED )
                    {
                        listener.onSetDataPointSuccess();
                    }
                    else
                    {
                        listener.onSetDataPointFailure( i );
                    }
                }
            }
        } );
        if ( ret < 0 && listener != null )
        {
            listener.onSetDataPointFailure( ret );
        }
    }

    public interface OnRegisterListener
    {
        void onRegisterSuccess();

        void onRegisterFailure( XlinkError error );
    }

    public interface OnLoginListener
    {
        void onLoginSuccess( UserInfo info );

        void onLoginFailure( XlinkError error );
    }

    public interface OnScanDeviceListener
    {
        void onScanDevice( XDevice device );

        void onScanFailure( int error );
    }

    public interface OnConnectListener
    {
        void onConnectLocal();

        void onConnectCloud();

        void onConnectFailure( int error );
    }

    public interface OnSetDataPointListener
    {
        void onSetDataPointSuccess();

        void onSetDataPointFailure( int error );
    }

    public interface OnSetAccessKeyListener
    {
        void onSetAccessKeySuccess();

        void onSetAccessKeyFailure( int error );
    }

    public interface OnRegisterDeviceListener
    {
        void onRegisterDeviceSuccess();

        void onRegisterDeviceFailure( int error );
    }

    public interface OnSendPipeDataListener
    {
        void onSendPipeDataSuccess();

        void onSendPipeDataFailure( int error );
    }

    public static class Holder
    {
        private static final XlinkCloudManager INSTANCE = new XlinkCloudManager();
    }
}
