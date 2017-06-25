package com.vinhdn.phonedetect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by vinh on 6/24/17.
 */

public class InComingCall extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        PhoneListener phoneListener = new PhoneListener(context);

        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    private class PhoneListener extends PhoneStateListener {
        private Context context = null;

        public PhoneListener(Context context) {
            this.context = context;
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d("Telephone", String.format("Phone is ringing, number %s",
                            incomingNumber));

                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
//				if (incomingNumber == null || incomingNumber.equals(""))
//					break;
                    if(incomingNumber == null)
                        incomingNumber = "";
                    Log.d("Telephone", String.format(
                            "Phone call offhook, number %s", incomingNumber));
//                    Intent localIntent1 = new Intent(this.context,
//                            CallRecordingService.class);
//                    localIntent1.putExtra("android.intent.extra.PHONE_NUMBER", incomingNumber);
//                    localIntent1.putExtra("android.intent.extra.KEY_EVENT",
//                            "RecordCall");
//                    context.startService(localIntent1);
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d("Telephone",
                            String.format(
                                    "Phone call hung up, incoming number %s. STOPING recording service",
                                    incomingNumber));
//                    Intent localIntent2 = new Intent(this.context,
//                            CallRecordingService.class);
//                    this.context.stopService(localIntent2);

                    // Intent stopRecordingServiceIntent = new Intent(this.context,
                    // CallRecordingService.class);
                    // context.stopService(stopRecordingServiceIntent);

                    break;

                default:
                    break;
            }
        }
    }
}
