package com.vinhdn.phonedetect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Created by vinh on 6/24/17.
 */

public class PhoneReceiver extends BroadcastReceiver {
    public static final String LISTEN_ENABLED = "ListenEnabled";
    public static final String FILE_DIRECTORY = "recordedCalls";
    private String phoneNumber = "";
    public static final int STATE_INCOMING_NUMBER = 0;
    public static final int STATE_CALL_START = 1;
    public static final int STATE_CALL_END = 2;

    public static final int MEDIA_MOUNTED = 0;
    public static final int MEDIA_MOUNTED_READ_ONLY = 1;
    public static final int NO_MEDIA = 2;

    @Override
    public void onReceive(Context context, Intent intent) {

        phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        String phoneCall = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        if (updateExternalStorageState() == MEDIA_MOUNTED) {
            if (phoneNumber == null) {
                if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    Intent myIntent = new Intent(context, CallRecordingService.class);
                    myIntent.putExtra("commandType", STATE_CALL_START);
                    Log.d("Intent", "STATE_CALL_START");
                    myIntent.putExtra("phoneNumber",  phoneNumber);
                    if(myIntent != null)
                        context.startService(myIntent);
                } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    Intent myIntent = new Intent(context, CallRecordingService.class);
                    myIntent.putExtra("commandType", STATE_CALL_END);
                    Log.d("Intent", "STATE_CALL_END");
                    if(myIntent != null)
                        context.startService(myIntent);
                } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    if (phoneNumber == null)
                        phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Intent myIntent = new Intent(context, CallRecordingService.class);
                    myIntent.putExtra("commandType", STATE_INCOMING_NUMBER);
                    Log.d("Intent", "EXTRA_INCOMING_NUMBER");
                    myIntent.putExtra("phoneNumber",  phoneNumber);
                    if(myIntent != null)
                        context.startService(myIntent);
                    if(phoneCall != null && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("Block", false))
                        disconnectPhoneItelephony(context);
                }
                Log.d("Phone = null", phoneCall + "");
            } else {
                Log.d("Intent", "STATE_CREATE_OUT_GOING_NUMBER");
                Log.d("Phone", phoneNumber + "");
            }


        }

    }

    public static int updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return MEDIA_MOUNTED;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return MEDIA_MOUNTED_READ_ONLY;
        } else {
            return NO_MEDIA;
        }
    }

    // Method to disconnect phone automatically and programmatically
    // Keep this method as it is
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void disconnectPhoneItelephony(Context context)
    {
        ITelephony telephonyService;
        TelephonyManager telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        try
        {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            telephonyService.endCall();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
