package com.inledco.rs485;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.inledco.comman.BaseActivity;
import com.inledco.comman.LogUtil;
import com.inledco.xlinkcloud.UserInfo;
import com.inledco.xlinkcloud.XlinkCloudManager;
import com.inledco.xlinkcloud.XlinkError;

public class LoginActivity extends BaseActivity
{
    private EditText login_et_email;
    private EditText login_et_psw;
    private Button login_btn_signup;
    private Button login_btn_signin;
    private TextView login_tv_forget;
    private TextView login_tv_skip;

    private XlinkCloudManager.OnRegisterListener registerListener;
    private XlinkCloudManager.OnLoginListener loginListener;
    private XlinkCloudManager.OnConnectListener connectListener;
    private XlinkCloudManager.OnRegisterDeviceListener registerDeviceListener;
    private XlinkCloudManager.OnScanDeviceListener scanDeviceListener;
    private XlinkCloudManager.OnSendPipeDataListener sendPipeDataListener;
    private XlinkCloudManager.OnSetAccessKeyListener setAccessKeyListener;
    private XlinkCloudManager.OnSetDataPointListener setDataPointListener;

    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        initView();
        initData();
        initEvent();
    }

    @Override
    protected void initView ()
    {
        login_et_email = (EditText) findViewById( R.id.login_et_email );
        login_et_psw = (EditText) findViewById( R.id.login_et_psw );
        login_btn_signup = (Button) findViewById( R.id.login_btn_signup );
        login_btn_signin = (Button) findViewById( R.id.login_btn_signin );
        login_tv_forget = (TextView) findViewById( R.id.login_tv_forget_psw );
        login_tv_skip = (TextView) findViewById( R.id.login_tv_skip );
    }

    @Override
    protected void initData ()
    {
        registerListener = new XlinkCloudManager.OnRegisterListener() {
            @Override
            public void onRegisterSuccess ()
            {
                login_btn_signin.performClick();
            }

            @Override
            public void onRegisterFailure ( XlinkError error )
            {
                LogUtil.d( TAG, "onRegisterFailure: " + error.toString() );
            }
        };
        loginListener = new XlinkCloudManager.OnLoginListener() {
            @Override
            public void onLoginSuccess ( UserInfo info )
            {
                LogUtil.d( TAG, "onLoginSuccess: " + info.toString() );
            }

            @Override
            public void onLoginFailure ( XlinkError error )
            {
                LogUtil.d( TAG, "onLoginFailure: " + error.toString() );
            }
        };
    }

    @Override
    protected void initEvent ()
    {
        login_btn_signup.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick ( View v )
            {
                String email = login_et_email.getText().toString();
                String psw = login_et_psw.getText().toString();
                if ( TextUtils.isEmpty( email ) )
                {
                    login_et_email.setError( getString( R.string.input_empty ) );
                    return;
                }
                if ( TextUtils.isEmpty( psw ) )
                {
                    login_et_psw.setError( getString( R.string.input_empty ) );
                    return;
                }
                if ( psw.length() < 6 )
                {
                    login_et_psw.setError( getString( R.string.psw_len_less_than_6 ) );
                    return;
                }
                XlinkCloudManager.getInstance().registerUserByMail( email, psw, registerListener );
            }
        } );

        login_btn_signin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick ( View v )
            {
                String email = login_et_email.getText().toString();
                String psw = login_et_psw.getText().toString();
                if ( TextUtils.isEmpty( email ) )
                {
                    login_et_email.setError( getString( R.string.input_empty ) );
                    return;
                }
                if ( TextUtils.isEmpty( psw ) )
                {
                    login_et_psw.setError( getString( R.string.input_empty ) );
                    return;
                }
                XlinkCloudManager.getInstance().login( email, psw, loginListener );
            }
        } );

        login_tv_forget.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick ( View v )
            {
                Intent intent = new Intent( LoginActivity.this, MainActivity.class );
                startActivity( intent );
            }
        } );
    }
}
