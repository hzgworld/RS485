package com.inledco.okhttp;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by liruya on 2017/9/18.
 */

public class OkHttpManager
{
    private static final String TAG = "OkHttpManager";

    public final MediaType JSON = MediaType.parse( "application/json; charset=utf-8" );

    public static final int PARAM_SUCCESS = 200;

    private OkHttpClient mOkHttpClient = new OkHttpClient();

    public static OkHttpManager getInstance ()
    {
        return Holder.INSTANCE;
    }

    public void get ( String url, Map< String, String > hdrs, ResultCallback callback )
    {
        Request request;
        if ( hdrs == null )
        {
            request = new Request.Builder().url( url )
                                           .build();
        }
        else
        {
            Headers headers = Headers.of( hdrs );
            request = new Request.Builder().url( url )
                                           .headers( headers )
                                           .build();
        }
        mOkHttpClient.newCall( request )
                     .enqueue( callback );
    }

    public void post ( String url, Map< String, String > hdrs, Map< String, String > params, ResultCallback callback )
    {
        String json = new JSONObject( params ).toString();
        RequestBody body = RequestBody.create( JSON, json );
        Request request;
        if ( hdrs == null )
        {
            request = new Request.Builder().url( url )
                                           .post( body )
                                           .build();
        }
        else
        {
            Headers headers = Headers.of( hdrs );
            request = new Request.Builder().url( url )
                                           .headers( headers )
                                           .post( body )
                                           .build();
        }
        mOkHttpClient.newCall( request )
                     .enqueue( callback );
    }

    public void put ( String url, Map< String, String > hdrs, Map< String, String > params, ResultCallback callback )
    {
        FormBody.Builder builder = new FormBody.Builder();
        for ( String key : params.keySet() )
        {
            builder.add( key, params.get( key ) );
        }
        RequestBody body = builder.build();
        Request request;
        if ( hdrs == null )
        {
            request = new Request.Builder().url( url )
                                           .put( body )
                                           .build();
        }
        else
        {
            Headers headers = Headers.of( hdrs );
            request = new Request.Builder().url( url )
                                           .headers( headers )
                                           .put( body )
                                           .build();
        }
        mOkHttpClient.newCall( request )
                     .enqueue( callback );
    }

    public void delete ( String url, Map< String, String > hdrs, Map< String, String > params, ResultCallback callback )
    {
        FormBody.Builder builder = new FormBody.Builder();
        for ( String key : params.keySet() )
        {
            builder.add( key, params.get( key ) );
        }
        RequestBody body = builder.build();
        Request request;
        if ( hdrs == null )
        {
            request = new Request.Builder().url( url )
                                           .delete( body )
                                           .build();
        }
        else
        {
            Headers headers = Headers.of( hdrs );
            request = new Request.Builder().url( url )
                                           .headers( headers )
                                           .delete( body )
                                           .build();
        }
        mOkHttpClient.newCall( request )
                     .enqueue( callback );
    }

    public abstract static class ResultCallback< T1, T2 > implements Callback
    {
        private Gson mGson;
        private Type mType;
        private Type mError;

        public ResultCallback ()
        {
            mGson = new Gson();
            Type superClass = getClass().getGenericSuperclass();
            if ( superClass instanceof ParameterizedType )
            {
                mType = ( (ParameterizedType) superClass ).getActualTypeArguments()[0];
                mError = ( (ParameterizedType) superClass ).getActualTypeArguments()[1];
            }
        }

        @Override
        public void onFailure ( Call call, IOException e )
        {

        }

        @Override
        public void onResponse ( Call call, Response response ) throws IOException
        {
            if ( response.code() == PARAM_SUCCESS )
            {
                if ( mType == String.class )
                {
                    onSuccess( (T1) response.body().string() );
                }
                else
                {
                    T1 result = mGson.fromJson( response.body()
                                                        .string(), mType );
                    onSuccess( result );
                }
            }
            else
            {
                if ( mError == String.class )
                {
                    onError( (T2) response.body().string() );
                }
                else
                {
                    T2 error = mGson.fromJson( response.body()
                                                       .string(), mError );
                    onError( error );
                }
            }
        }

        public abstract void onSuccess ( T1 response );

        public abstract void onError ( T2 error );
    }

    public static class Holder
    {
        private static final OkHttpManager INSTANCE = new OkHttpManager();
    }
}
