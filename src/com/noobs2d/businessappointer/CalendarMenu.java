package com.noobs2d.businessappointer;

import java.util.Calendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class CalendarMenu extends Activity {

    private static final int OPEN_SOURCE_LICENSE_PROMPT = 1;

    private static final int ADD_PLACE_MANUALLY_PROMPT = 2;
    private static final int ADD_CONTACTS_PROMPT = 3;
    private static final int GPS_NOT_AVAILABLE_PROMPT = 4;

    public class EventTriggerReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
	    Toast.makeText(getApplicationContext(), "THE EVENT HAS STARTED!", Toast.LENGTH_LONG).show();
	}

    }

    private CalendarView calendarView;
    private Button addEvent;

    private int year = Calendar.getInstance().get(Calendar.YEAR);
    private int month = Calendar.getInstance().get(Calendar.MONTH);
    private int date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.calendar);
	Settings.load();
	if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()) != ConnectionResult.SUCCESS)
	    showDialog(GPS_NOT_AVAILABLE_PROMPT);
	else {
	    calendarView = (CalendarView) findViewById(R.id.calendarView);
	    addEvent = (Button) findViewById(R.id.addEventButton);
	    //	addEvent.setEnabled(false);
	    addEvent.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    showDialog(ADD_PLACE_MANUALLY_PROMPT);
		}
	    });

	    calendarView.setOnDateChangeListener(new OnDateChangeListener() {

		@Override
		public void onSelectedDayChange(CalendarView arg0, int year, int month, int date) {
		    CalendarMenu.this.year = year;
		    CalendarMenu.this.month = month;
		    CalendarMenu.this.date = date;
		    addEvent.setEnabled(true);
		    //		long startMillis = 0;
		    //		long endMillis = 0;
		    //		Calendar beginTime = Calendar.getInstance();
		    //		beginTime.set(2012, 9, 14, 7, 30);
		    //		startMillis = beginTime.getTimeInMillis();
		    //		Calendar endTime = Calendar.getInstance();
		    //		endTime.set(2012, 9, 14, 8, 45);
		    //		endMillis = endTime.getTimeInMillis();

		    //		Intent intent = new Intent(Intent.ACTION_INSERT);
		    //		intent.setData(CalendarContract.Events.CONTENT_URI);
		    //		//		intent.setType("vnd.android.cursor.item/event");
		    //		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
		    //		intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
		    //		intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);// just included for completeness
		    //		intent.putExtra(Events.TITLE, "My Awesome Event");
		    //		intent.putExtra(Events.DESCRIPTION, "Heading out with friends to do something awesome.");
		    //		intent.putExtra(Events.EVENT_LOCATION, "Earth");
		    //		intent.putExtra(Events.RRULE, "FREQ=DAILY;COUNT=10");
		    //		intent.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
		    //		intent.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
		    //		intent.putExtra(Intent.EXTRA_EMAIL, "my.friend@example.com");
		    //		startActivity(intent);
		}

	    });

	    if (!Settings.licenseAccepted)
		showDialog(OPEN_SOURCE_LICENSE_PROMPT);
	}
    }

    @TargetApi(14)
    private void startEventIntent() {
	//	Intent intent = new Intent(Intent.ACTION_INSERT);
	//	intent.setData(CalendarContract.Events.CONTENT_URI);
	//	intent.setType("vnd.android.cursor.item/event");
	//	intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);// just included for completeness
	//	intent.putExtra(Events.TITLE, "My Awesome Event");
	//	intent.putExtra(Events.DESCRIPTION, "Heading out with friends to do something awesome.");
	//	intent.putExtra(Events.EVENT_LOCATION, "Earth");
	//	intent.putExtra(Events.RRULE, "FREQ=DAILY;COUNT=10");
	//	intent.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
	//	intent.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
	//	intent.putExtra(Intent.EXTRA_EMAIL, "my.friend@example.com");
	//	startActivityForResult(intent, 0);

	// Construct event details
	//	long startMillis = 0;
	//	long endMillis = 0;
	//	Calendar beginTime = Calendar.getInstance();
	//	beginTime.set(2013, 1, 4, 7, 30);
	//	startMillis = beginTime.getTimeInMillis();
	//	Calendar endTime = Calendar.getInstance();
	//	endTime.set(2013, 1, 14, 8, 45);
	//	endMillis = endTime.getTimeInMillis();
	//
	//	// Insert Event
	//	ContentResolver cr = getContentResolver();
	//	ContentValues values = new ContentValues();
	//	values.put(CalendarContract.Events.DTSTART, startMillis);
	//	values.put(CalendarContract.Events.DTEND, endMillis);
	//	values.put(CalendarContract.Events.TITLE, "Walk The Dog");
	//	values.put(CalendarContract.Events.DESCRIPTION, "My dog is bored, so we're going on a really long walk!");
	//	values.put(CalendarContract.Events.CALENDAR_ID, 0);
	//	values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
	//	Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
	//
	//	// Retrieve ID for new event
	//	String eventID = uri.getLastPathSegment();
	Intent intent = new Intent(Intent.ACTION_INSERT);
	intent.setData(CalendarContract.Events.CONTENT_URI);
	startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	//	Bundle extras = data.getExtras();
	//	System.out.println(extras.size());
	//	Set<String> ks = extras.keySet();
	//	Iterator<String> iterator = ks.iterator();
	//	while (iterator.hasNext())
	//	    Log.d("KEY", iterator.next());
	//	System.out.println("RETURNED FROM THE ABYSS!");
    }

    @Override
    protected Dialog onCreateDialog(int id) {
	Builder builder = new AlertDialog.Builder(this);
	String message = "";
	switch (id) {
	    case OPEN_SOURCE_LICENSE_PROMPT:
		message = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getApplicationContext());
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setTitle("License");
		builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			Settings.licenseAccepted = true;
			Settings.save();
		    }
		});
		builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			CalendarMenu.this.finish();
		    }
		});
		break;
	    case ADD_PLACE_MANUALLY_PROMPT:
		builder.setMessage(R.string.calendar_place_prompt);
		builder.setCancelable(true);
		builder.setTitle("Add Event Place");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			Intent intent = new Intent(CalendarMenu.this, MapV1Menu.class);
			Bundle bundle = new Bundle();
			bundle.putInt("YEAR", year);
			bundle.putInt("MONTH", month);
			bundle.putInt("DATE", date);
			intent.putExtras(bundle);
			startActivityForResult(intent, 0);
		    }
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			showDialog(ADD_CONTACTS_PROMPT);
		    }
		});
		break;
	    case ADD_CONTACTS_PROMPT:
		builder.setMessage(R.string.calendar_contact_prompt);
		builder.setCancelable(true);
		builder.setTitle("Add Contacts");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			Intent intent = new Intent(CalendarMenu.this, ContactsMenu.class);
			Bundle bundle = new Bundle();
			bundle.putInt("YEAR", year);
			bundle.putInt("MONTH", month);
			bundle.putInt("DATE", date);
			intent.putExtras(bundle);
			startActivityForResult(intent, 0);
		    }
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			startEventIntent();
		    }
		});
		break;
	    case GPS_NOT_AVAILABLE_PROMPT:
		message = "Google Play Services not available. Please download it from the Play Store to use the application.";
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setTitle("Service Unavailable");
		builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			CalendarMenu.this.finish();
		    }
		});
		break;
	    default:
		assert false;
	}
	AlertDialog dialog = builder.create();
	dialog.show();
	return super.onCreateDialog(id);
    }
}