package com.elmclass.elmclass.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.elmclass.elmclass.activity.SignInActivity;

/**
 *
 * Created by kaininggu on 9/2/18.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = SignInActivity.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppManager.DEBUG) {
            Log.d(LOG_TAG, "SmsReceiver onReceive");
        }
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String messageSender = smsMessage.getOriginatingAddress();
                String messageBody = smsMessage.getMessageBody();
                if (AppManager.DEBUG) {
                    Log.d(LOG_TAG, "messageSender=" + messageSender + " messageBody=" + messageBody);
                }
            }
        }
    }
}
