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

public class ContactsMenu extends Activity {

    private List<String> numbers = new ArrayList<String>();
    private EditText contactNames;
    private EditText messageBody;
    private Button addContacts;
    private Button confirm;

    private String formatPhoneNumber(String number) {
	System.out.print("Replaced " + number + " with ");
	if (number.charAt(0) == '+')
	    number = number.replaceAll("\\+63", "0");
	//	System.out.println(number);
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
		    //		    String text = contactNames.getText().toString();
		    //		    if (text.length() > 0)
		    //			text += ", ";
		    //		    Cursor cursor = getContentResolver().query(Phone.CONTENT_URI, new String[] { Contacts._ID, Contacts.DISPLAY_NAME, Phone.NUMBER }, null, null, null);
		    //		    cursor.getString(0);
		    //		    contactNames.setText(cursor.getString(0));
		    Uri result = data.getData();
		    //		    System.out.println(result.toString());
		    String id = result.getLastPathSegment();

		    String[] projection = { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER };
		    String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";

		    Cursor cursor = managedQuery(result, projection, selection, null, null);
		    while (cursor.moveToNext()) {
			String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

			Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
			while (phones.moveToNext()) {
			    int phoneType = phones.getInt(phones.getColumnIndex(Phone.TYPE));
			    if (phoneType == Phone.TYPE_MOBILE) {
				String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
				phoneNumber = phoneNumber.replaceAll("\\s", "");
				contactNames.setText(contactNames.getText().toString() + phoneNumber + "; ");
				//				System.out.println(phoneNumber);
				numbers.add(formatPhoneNumber(phoneNumber));
				confirm.setEnabled(true);
				break;
			    }
			}
		    }

		    //		    Cursor cursor = getContentResolver().query(Contacts.CONTENT_URI, null, Contacts._ID, new String[] { id }, null);
		    //		    for (int i = 0; i < cursor.getColumnCount(); i++)
		    //			System.out.println(cursor.getColumnNames()[i]);
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
