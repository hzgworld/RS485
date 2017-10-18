package com.inledco.comman;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by liruya on 2017/9/21.
 */

public class AdvancedPreferenceUtil< T >
{
    /**
     * 储存对象到SharedPreference
     *
     * @param context
     * @param preferName 文件名
     * @param object 对象
     * @param key
     */
    public void setPrefer ( Context context, String preferName, T object, String key )
    {
        SharedPreferences objectPrefer = context.getSharedPreferences( preferName, Context.MODE_PRIVATE );
        if ( object == null )
        {
            SharedPreferences.Editor editor = objectPrefer.edit()
                                                          .remove( key );
            SharedPreferencesCompat.EditorCompat.getInstance()
                                                .apply( editor );
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try
        {
            oos = new ObjectOutputStream( baos );
            oos.writeObject( object );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        String objectStr = new String( Base64.encode( baos.toByteArray(), Base64.DEFAULT ) );
        try
        {
            baos.close();
            oos.close();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = objectPrefer.edit()
                                                      .putString( key, objectStr );
        SharedPreferencesCompat.EditorCompat.getInstance()
                                            .apply( editor );
    }

    /**
     * 从指定文件获取对象
     *
     * @param context
     * @param preferName 文件名
     * @param key
     * @return
     */
    public T getPrefer ( Context context, String preferName, String key )
    {
        SharedPreferences objectPrefer = context.getSharedPreferences( preferName, Context.MODE_PRIVATE );
        String objectStr = objectPrefer.getString( key, "" );
        if ( TextUtils.isEmpty( objectStr ) )
        {
            return null;
        }
        byte[] objBytes = Base64.decode( objectStr.getBytes(), Base64.DEFAULT );
        ByteArrayInputStream bais = new ByteArrayInputStream( objBytes );
        try
        {
            ObjectInputStream ois = new ObjectInputStream( bais );
            T object = (T) ois.readObject();
            bais.close();
            ois.close();
            return object;
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        catch ( ClassNotFoundException e )
        {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList< T > getAll ( Context context, String preferName )
    {
        SharedPreferences objectPrefer = context.getSharedPreferences( preferName, Context.MODE_PRIVATE );
        ArrayList< T > objects = new ArrayList<>();
        for ( String key : objectPrefer.getAll()
                                       .keySet() )
        {
            String objectStr = objectPrefer.getString( key, "" );
            if ( TextUtils.isEmpty( objectStr ) )
            {
                continue;
            }
            byte[] objBytes = Base64.decode( objectStr.getBytes(), Base64.DEFAULT );
            ByteArrayInputStream bais = new ByteArrayInputStream( objBytes );
            try
            {
                ObjectInputStream ois = new ObjectInputStream( bais );
                T object = (T) ois.readObject();
                bais.close();
                ois.close();
                objects.add( object );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            catch ( ClassNotFoundException e )
            {
                e.printStackTrace();
            }
        }
        return objects;
    }
}
