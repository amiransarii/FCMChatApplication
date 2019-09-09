package com.example.fcmchatapplication.util;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SharedPreferenceUtil {
    private Context context;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SharedPreferenceUtil(Context context)
    {
        this.context =context;
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        editor= sharedPreferences.edit();

    }
    public boolean loadPrefBoolean(String s)
    {
        return sharedPreferences.getBoolean(s,false);
    }

    public Integer loadPrefInt(String s)
    {

        int i= sharedPreferences.getInt(s,-1);
        if(i==-1)
        {  return 0;
            // return null;
        }
        else {
            return Integer.valueOf(i);
        }
    }

    public Long loadPrefLong(String s)
    {
        long l= sharedPreferences.getLong(s,-1L);
        if(l==-1L)
        {
            return null;
        }
        else {
            return Long.valueOf(l);
        }
    }

    public String loadPrefString(String s)
    {
        return sharedPreferences.getString(s,null);
    }

    public void removePref(String s)
    {
        editor.remove(s);
        editor.commit();
    }

    public void savePrefBoolean(String s,boolean flag)
    {
        editor.putBoolean(s,flag);
        editor.commit();
    }

    public void savePrefInt(String s,Integer integer)
    {
        if(integer.intValue()==-1)
        {
            throw new IllegalArgumentException("the integer value can not be -1");
        }
        else {
            editor.putInt(s,integer.intValue());
            editor.commit();
            return;
        }
    }

    public void savePrefLong(String s,Long longl)
    {
        if(longl.longValue()==-1L)
        {
            throw new IllegalArgumentException("the long value can not be  -1");
        }
        else {
            editor.putLong(s,longl.longValue());
            editor.commit();
            return;
        }
    }

    public void savePrefString(String s,String s1)
    {
        editor.putString(s,s1);
        editor.commit();
    }
}



