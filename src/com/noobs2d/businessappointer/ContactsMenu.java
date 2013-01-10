package com.noobs2d.businessappointer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

/**
 * Menu where the user can send an SMS to selected contacts under a scheduled alarm.
 * 
 * @author MrUseL3tter
 */
public class ContactsMenu extends Activity {

    private List<String> numbers = new ArrayList<String>();
    private EditText contactNames;
    private EditText messageBody;
    private Button addContacts;
    private Button confirm;

    /** changes '+63' into '0' */
    private String formatPhoneNumber(String number) {
	if (number.charAt(0) == '+')
	    number = number.replaceAll("\\+63", "0");
	return number;
    }

    @TargetApi(14)
    private void startEventIntent() {
	Intent intent = new Intent(Intent.ACTION_INSERT);
	intent.setData(CalendarContract.Events.CONTENT_URI);
	intent.putExtras(getIntent());
	startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (resultCode == RESULT_OK)
	    switch (requestCode) {
		case 1:
		    Uri result = data.getData();
		    String[] projection = { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER };
		    String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";
		    Cursor cursor = managedQuery(result, projection, selection, null, null);
		    while (cursor.moveToNext()) {
			String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

			Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
			while (phones.moveToNext()) {
			    int phoneType = phones.getInt(phones.getColumnIndex(Phone.TYPE));
			    if (phoneType == Phone.TYPE_MOBILE) {
				String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
				phoneNumber = phoneNumber.replaceAll("\\s", "");
				contactNames.setText(contactNames.getText().toString() + phoneNumber + "; ");
				numbers.add(formatPhoneNumber(phoneNumber));
				confirm.setEnabled(true);
				break;
			    }
			}
		    }
		    break;
	    }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.contact);

	contactNames = (EditText) findViewById(R.id.contactNames);
	contactNames.setEnabled(false);

	messageBody = (EditText) findViewById(R.id.messageBody);

	addContacts = (Button) findViewById(R.id.contactAdd);
	addContacts.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, 1);
	    }
	});

	confirm = (Button) findViewById(R.id.messageSend);
	confirm.setEnabled(false);
	confirm.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker1);
		TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker1);

		Calendar calendar = Calendar.getInstance();
		calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
		Intent intent = new Intent(getApplicationContext(), SMSBroadcastReceiver.class);
		intent.putExtra("CONTACTS_SIZE", numbers.size());
		for (int i = 0; i < numbers.size(); i++)
		    intent.putExtra("CONTACT_" + i, numbers.get(i));
		intent.putExtra("MESSAGE", messageBody.getText().toString());

		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 31324231, intent, 0);//Service(getApplicationContext(), 0, myIntent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

		startEventIntent();
		ContactsMenu.this.finish();
	    }
	});
    }
}
