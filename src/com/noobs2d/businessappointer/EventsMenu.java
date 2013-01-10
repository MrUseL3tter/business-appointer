package com.noobs2d.businessappointer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class EventsMenu extends ListActivity {

    private class EventsAdapter extends ArrayAdapter<String> {

	private Context context;
	private final String[] values;

	public EventsAdapter(Context context, String[] values) {
	    super(context, android.R.id.text1);
	    this.context = context;
	    this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.list_layout, parent, false);
	    return rowView;
	}

    }

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
		    String name = cursor.getString(0);
		    String displayName = cursor.getString(1);
		    // This is actually a better pattern:
		    String color = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR));
		    Boolean selected = !cursor.getString(3).equals("0");
		    calendars.add(displayName);
		}
	    cursor.close();
	} catch (AssertionError ex) {
	    // TODO: log exception and bail
	}

	String[] calendarArray = new String[calendars.size()];
	Iterator iterator = calendars.iterator();

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
		// TODO Auto-generated method stub

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
		//		System.out.println("Calendar ID: " + cursor.getString(0));
		//		System.out.println("Title: " + cursor.getString(1));
		//		System.out.println("Description: " + cursor.getString(2));
		//		System.out.println("DTStart: " + cursor.getString(3));
		//		System.out.println("DTEnd: " + cursor.getString(4));
		//		System.out.println("Event Location: " + cursor.getString(5));
		eventsList[i] = !cursor.getString(1).equals("") ? cursor.getString(1) : "Untitled Event";
		//		System.out.println(cursor.getLong(0));
		eventIDs[i] = cursor.getString(0);
		i++;
	    }
	    cursor.close();
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_layout, R.id.listText);
	    //	EventsAdapter adapter = new EventsAdapter(getApplicationContext(), sampleValues);
	    adapter.addAll(eventsList);
	    setListAdapter(adapter);
	    getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		    //		Toast.makeText(getApplicationContext(), EventsMenu.this.getListAdapter().getItem(position).toString(), Toast.LENGTH_LONG).show();
		    selectedEventTitle = eventIDs[position];//EventsMenu.this.getListAdapter().getItem(position).toString();
		    showDialog(Dialogs.CONFIRM_DELETE_EVENT);
		    return false;
		}

	    });
	} else
	    System.out.println("FUUUUUUUUUUUUUUU");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
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
