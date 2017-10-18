package com.inledco.comman;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;

/**
 * Created by liruya on 2017/9/20.
 */

public class PreferenceUtil
{

    public static void setPrefer ( Context context, String preferName, String value, String key )
    {
        SharedPreferences prefer = context.getSharedPreferences( preferName, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = prefer.edit()
                                                .putString( key, value );
        SharedPreferencesCompat.EditorCompat.getInstance()
                                            .apply( editor );
    }

    public static void setPrefer ( Context context, String preferName, int value, String key )
    {
        SharedPreferences prefer = context.getSharedPreferences( preferName, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = prefer.edit()
                                                .putInt( key, value );
        SharedPreferencesCompat.EditorCompat.getInstance()
                                            .apply( editor );
    }

    public static void setPrefer ( Context context, String preferName, long value, String key )
    {
        SharedPreferences prefer = context.getSharedPreferences( preferName, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = prefer.edit()
                                                .putLong( key, value );
        SharedPreferencesCompat.EditorCompat.getInstance()
                                            .apply( editor );
    }

    public static void setPrefer ( Context context, String preferName, float value, String key )
    {
        SharedPreferences prefer = context.getSharedPreferences( preferName, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = prefer.edit()
                                                .putFloat( key, value );
        SharedPreferencesCompat.EditorCompat.getInstance()
                                            .apply( editor );
    }

    public static void setPrefer ( Context context, String preferName, boolean value, String key )
    {
        SharedPreferences prefer = context.getSharedPreferences( preferName, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = prefer.edit()
                                                .putBoolean( key, value );
        SharedPreferencesCompat.EditorCompat.getInstance()
                                            .apply( editor );
    }

    public static String getPrefer ( Context context, String preferName, String key, String defaultValue )
    {
        SharedPreferences prefer = context.getSharedPreferences( preferName, Context.MODE_PRIVATE );
        String value = prefer.getString( key, defaultValue );
        return value;
    }

    public static int getPrefer ( Context context, String preferName, String key, int defaultValue )
    {
        SharedPreferences prefer = context.getSharedPreferences( preferName, Context.MODE_PRIVATE );
        int value = prefer.getInt( key, defaultValue );
        return value;
    }

    public static long getPrefer ( Context context, String preferName, String key, long defaultValue )
    {
        SharedPreferences prefer = context.getSharedPreferences( preferName, Context.MODE_PRIVATE );
        long value = prefer.getLong( key, defaultValue );
        return value;
    }

    public static float getPrefer ( Context context, String preferName, String key, float defaultValue )
    {
        SharedPreferences prefer = context.getSharedPreferences( preferName, Context.MODE_PRIVATE );
        float value = prefer.getFloat( key, defaultValue );
        return value;
    }

    public static boolean getPrefer ( Context context, String preferName, String key, boolean defaultValue )
    {
        SharedPreferences prefer = context.getSharedPreferences( preferName, Context.MODE_PRIVATE );
        boolean value = prefer.getBoolean( key, defaultValue );
        return value;
    }

    /**
     * 删除文件中储存的对象
     *
     * @param context
     * @param preferName
     * @param key
     */
    public static void deleteObjectFromPrefer ( Context context, String preferName, String key )
    {
        SharedPreferences objectPrefer = context.getSharedPreferences( preferName, Context.MODE_PRIVATE );

        if ( objectPrefer.contains( key ) )
        {
            SharedPreferences.Editor editor = objectPrefer.edit()
                                                          .remove( key );
            SharedPreferencesCompat.EditorCompat.getInstance()
                                                .apply( editor );
        }
    }
}
