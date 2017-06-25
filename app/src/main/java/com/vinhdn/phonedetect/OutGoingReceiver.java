package com.vinhdn.phonedetect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * Created by vinh on 6/25/17.
 */

public class OutGoingReceiver extends BroadcastReceiver{
    private static final String SEP  = ":\n     ";

    private static boolean confirmed = false;
    private static boolean anonym    = false;

    @Override
    public void onReceive(Context ctx, Intent i)
    {
        final String num = i.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        if(TextUtils.isEmpty(num)) return;
        setCallNum(num);
    }

    private static void setCallNum(String num)
    {
        PickupService.start();
    }


}
