package com.noobs2d.businessappointer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Tab menu showing the list of events depending on the selected calendar.
 * 
 * @author MrUseL3tter
 */
public class EventsMenu extends ListActivity {

    /** the long-pressed event to be deleted */
    private String selectedEventTitle;

    /** for tracking of event IDs on the ListView */
    private String[] eventIDs;

    private void getCalendarsList() {
	Spinner spinner = (Spinner) findViewById(R.id.calendarsSpinner);

	final String[] fields = { CalendarContract.Calendars.NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars.CALENDAR_COLOR, CalendarContract.Calendars.VISIBLE };
	final Uri calendarURI = Uri.parse("content://com.android.calendar/calendars");

	// Fetch a list of all calendars sync'd with the device and their display names
	Cursor cursor = getContentResolver().query(calendarURI, fields, null, null, null);

	Set<String> calendars = new HashSet<String>();
	try {
	    if (cursor.getCount() > 0)
		while (cursor.moveToNext()) {
		    String displayName = cursor.getString(1);
		    calendars.add(displayName);
		}
	    cursor.close();
	} catch (AssertionError ex) {
	}

	String[] calendarArray = new String[calendars.size()];
	Iterator<String> iterator = calendars.iterator();

	int i = 0;
	while (iterator.hasNext()) {
	    calendarArray[i] = iterator.next().toString();
	    iterator.remove();
	    i++;
	}

	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, calendarArray);
	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	spinner.setAdapter(dataAdapter);
	spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

	    @Override
	    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		getEventsByCalendar((String) parent.getAdapter().getItem(position));
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> arg0) {
	    }
	});
	getEventsByCalendar(calendarArray[0]);
    }

    @TargetApi(14)
    private void getEventsByCalendar(String calendarDisplayName) {
	System.out.println("Getting events list for " + calendarDisplayName + "...");
	String[] projection = { Events._ID, Events.TITLE };
	String selection = Calendars.CALENDAR_DISPLAY_NAME + " = " + DatabaseUtils.sqlEscapeString(calendarDisplayName);
	Cursor cursor = getApplicationContext().getContentResolver().query(Events.CONTENT_URI, projection, selection, null, null);

	cursor.moveToFirst();
	if (cursor.getCount() > 0) {
	    String[] eventsList = new String[cursor.getCount() - 1];
	    eventIDs = new String[cursor.getCount() - 1];
	    int i = 0;
	    while (cursor.moveToNext()) {
		eventsList[i] = !cursor.getString(1).equals("") ? cursor.getString(1) : "Untitled Event";
		eventIDs[i] = cursor.getString(0);
		i++;
	    }
	    cursor.close();
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_layout, R.id.listText);
	    adapter.addAll(eventsList);
	    setListAdapter(adapter);
	    getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		    selectedEventTitle = eventIDs[position];
		    showDialog(Dialogs.CONFIRM_DELETE_EVENT);
		    return false;
		}

	    });
	}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.events);
	getCalendarsList();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
	Builder builder = new AlertDialog.Builder(this);
	String message = "";
	switch (id) {
	    case Dialogs.CONFIRM_DELETE_EVENT:
		message = getResources().getString(R.string.events_menu_delete_prompt);
		builder.setMessage(message);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			CalendarUtils.deleteEvent(getContentResolver(), selectedEventTitle);
			getCalendarsList();
		    }
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    }
		});
	}
	AlertDialog dialog = builder.create();
	dialog.show();
	return super.onCreateDialog(id);
    }

}
