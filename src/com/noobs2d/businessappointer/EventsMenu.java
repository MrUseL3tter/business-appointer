package com.noobs2d.businessappointer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
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

    @TargetApi(14)
    private void setListViewData(String calendarDisplayName) {
	Cursor events = CalendarUtils.getEventsByCalendarDisplayName(getApplicationContext(), calendarDisplayName);
	events.moveToFirst();
	if (events.getCount() > 0) {
	    String[] eventTitles = new String[events.getCount() - 1];
	    String[] eventStartTimes = new String[events.getCount() - 1];
	    String[] eventEndTimes = new String[events.getCount() - 1];
	    eventIDs = new String[events.getCount() - 1];
	    int i = 0;
	    while (events.moveToNext()) {
		eventIDs[i] = events.getString(0);
		eventTitles[i] = !events.getString(1).equals("") ? events.getString(1) : "Untitled Event";
		eventStartTimes[i] = events.getString(2);
		eventEndTimes[i] = events.getString(3);
		i++;
	    }
	    events.close();
	    ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
	    for (int j = 0; j < eventStartTimes.length && j < eventEndTimes.length; j++) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Long.parseLong(eventStartTimes[j]));
		String schedule = CalendarUtils.getCalendarString(calendar) + " - ";
		calendar.setTimeInMillis(Long.parseLong(eventStartTimes[j]));
		schedule += CalendarUtils.getCalendarString(calendar);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Events.TITLE, eventTitles[j]);
		map.put("SCHEDULE", schedule);
		list.add(map);
	    }

	    String[] from = { Events.TITLE, "SCHEDULE" };
	    int[] to = { R.id.listText, R.id.listText2 };

	    SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), list, R.layout.list_layout, from, to);
	    setListAdapter(simpleAdapter);

	    //	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_layout, R.id.listText);
	    //	    adapter.addAll(eventTitles);
	    //	    setListAdapter(adapter);
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

    private void setSpinnerData() {
	Spinner spinner = (Spinner) findViewById(R.id.calendarsSpinner);

	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, CalendarUtils.getCalendarsList(getContentResolver()));
	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	spinner.setAdapter(dataAdapter);
	spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

	    @Override
	    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		setListViewData((String) parent.getAdapter().getItem(position));
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> arg0) {
	    }
	});
	setListViewData(CalendarUtils.getCalendarsList(getContentResolver())[0]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.events);
	setSpinnerData();
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
			setSpinnerData();
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
