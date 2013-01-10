package com.noobs2d.businessappointer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

/**
 * Receiver for SMS alarm trigger.
 * 
 * @author MrUseL3tter
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {

    public SMSBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
	SmsManager smsManager = SmsManager.getDefault();
	for (int i = 0; i < intent.getIntExtra("CONTACTS_SIZE", 0); i++)
	    smsManager.sendTextMessage(intent.getStringExtra("CONTACT_" + i), null, intent.getStringExtra("MESSAGE"), null, null);
    }
}
