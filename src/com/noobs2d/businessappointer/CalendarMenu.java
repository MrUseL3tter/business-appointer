package com.noobs2d.businessappointer;

import java.util.Calendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Tab menu where the user can add events.
 * 
 * @author MrUseL3tter
 */
public class CalendarMenu extends Activity {

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
	    showDialog(Dialogs.GPS_NOT_AVAILABLE_PROMPT);
	else {
	    calendarView = (CalendarView) findViewById(R.id.calendarView);
	    addEvent = (Button) findViewById(R.id.addEventButton);
	    addEvent.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    showDialog(Dialogs.ADD_PLACE_MANUALLY_PROMPT);
		}
	    });

	    calendarView.setOnDateChangeListener(new OnDateChangeListener() {

		@Override
		public void onSelectedDayChange(CalendarView arg0, int year, int month, int date) {
		    CalendarMenu.this.year = year;
		    CalendarMenu.this.month = month;
		    CalendarMenu.this.date = date;
		    addEvent.setEnabled(true);
		}
	    });

	    if (!Settings.licenseAccepted)
		showDialog(Dialogs.OPEN_SOURCE_LICENSE_PROMPT);
	}
    }

    @TargetApi(14)
    private void startEventIntent() {
	Intent intent = new Intent(Intent.ACTION_INSERT);
	intent.setData(CalendarContract.Events.CONTENT_URI);
	startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
	Builder builder = new AlertDialog.Builder(this);
	String message = "";
	switch (id) {
	    case Dialogs.OPEN_SOURCE_LICENSE_PROMPT:
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
	    case Dialogs.ADD_PLACE_MANUALLY_PROMPT:
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
			showDialog(Dialogs.ADD_CONTACTS_PROMPT);
		    }
		});
		break;
	    case Dialogs.ADD_CONTACTS_PROMPT:
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
	    case Dialogs.GPS_NOT_AVAILABLE_PROMPT:
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