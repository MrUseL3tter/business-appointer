package com.noobs2d.businessappointer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

public class SMSBroadcastReceiver extends BroadcastReceiver {

    public SMSBroadcastReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
	//	if (intent.getBooleanExtra("PUT", true))
	//	    System.out.println("THE FFFUUUer WAS TRANSFERED!");
	//	else
	//	    System.out.println("NOT TRANSFERREEEEEEEED");
	System.out.println("SENDING: " + intent.getStringExtra("MESSAGE"));
	SmsManager smsManager = SmsManager.getDefault();
	for (int i = 0; i < intent.getIntExtra("CONTACTS_SIZE", 0); i++)
	    smsManager.sendTextMessage(intent.getStringExtra("CONTACT_" + i), null, intent.getStringExtra("MESSAGE"), null, null);
    }
}
