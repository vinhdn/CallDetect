package com.vinhdn.phonedetect;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by vinh on 6/25/17.
 */

public class App extends Application {

    private static App instance;

    private static SharedPreferences prefs;
    private static SharedPreferences.Editor edit;

    public static Context get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        edit = prefs.edit();
    }

    public static final boolean is(String key)                { return prefs.getBoolean(key, false); }
    //public static final boolean is(String key, boolean def)   { return prefs.getBoolean(key, def  ); }
    public static final String gets(String key)              { return prefs.getString (key, ""   ); }
    //public static final String gets(String key, String def)  { return prefs.getString (key, def  ); }
    public static final int    geti(String key)              { return prefs.getInt    (key, 0    ); }
    //public static final int    geti(String key, int def)     { return prefs.getInt    (key, def  ); }
    public static final long   getl(String key)              { return prefs.getLong   (key, 0l   ); }
    //public static final long   getl(String key, long def)    { return prefs.getLong   (key, def  ); }
    //public static final float  getf(String key)              { return prefs.getFloat  (key, 0    ); }
    //public static final float  getf(String key, float def)   { return prefs.getFloat  (key, def  ); }
    public static final int    getsi(String key)             { return Integer.parseInt(prefs.getString(key, "0")); }
    //public static final int    getsi(String key, String def) { return Integer.parseInt  (prefs.getString(key, def)); }
    //public static final long   getsl(String key)             { return Long   .parseLong (prefs.getString(key, "0")); }
    //public static final long   getsl(String key, String def) { return Long   .parseLong (prefs.getString(key, def)); }
    //public static final float  getsf(String key)             { return Float  .parseFloat(prefs.getString(key, "0")); }
    //public static final float  getsf(String key, String def) { return Float  .parseFloat(prefs.getString(key, def)); }
    public static final Object get(String key) {
        try { return geti(key); } catch(Exception ei) {
            try { return getl(key); } catch(Exception el) {
                try { return is(key);   } catch(Exception eb) {
                    try { return gets(key); } catch(Exception es) {}}}}
        return null;
    }

    public static final boolean has (String key) { return prefs.contains(key); }
    public static final App       del (String key) { edit.remove(key);          return instance; }
    public static final App       delc(String key) { edit.remove(key).commit(); return instance; }
}
