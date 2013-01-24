package com.noobs2d.businessappointer;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

/**
 * Utility class for various calendar ops
 * 
 * @author MrUseL3tter
 */
public class CalendarUtils {

    /**
     * Delete an event with the provided ID.
     * 
     * @param contentResolver required to execute a query
     * @param id the ID of the event
     * @return rows deleted
     */
    @TargetApi(14)
    public static int deleteEvent(ContentResolver contentResolver, String id) {
	Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, Long.parseLong(id));
	return contentResolver.delete(deleteUri, null, null);
    }

    /**
     * Get the list of calendars sync'd with the phone as an array of Strings.
     * 
     * @param contentResolver required to execute a query
     * @return String array of the calendar names.
     */
    public static String[] getCalendarsList(ContentResolver contentResolver) {
	final String[] fields = { CalendarContract.Calendars.NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars.CALENDAR_COLOR, CalendarContract.Calendars.VISIBLE };
	final Uri calendarURI = Uri.parse("content://com.android.calendar/calendars");

	// Fetch a list of all calendars sync'd with the device and their display names
	Cursor cursor = contentResolver.query(calendarURI, fields, null, null, null);

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
	return calendarArray;
    }

    /**
     * Get a pre-formatted String based from the instance of Calendar passed.
     * 
     * @param calendar instance of Calendar with date set
     * @return pre-formatted Calendar date
     */
    public static String getCalendarString(Calendar calendar) {
	String aMPM = calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM";
	String hour = calendar.get(Calendar.HOUR) < 10 ? "0" + calendar.get(Calendar.HOUR) : "" + calendar.get(Calendar.HOUR);
	String minute = calendar.get(Calendar.MINUTE) < 10 ? "0" + calendar.get(Calendar.MINUTE) : "" + calendar.get(Calendar.MINUTE);
	return getMonthString(calendar.get(Calendar.MONTH)) + " " + calendar.get(Calendar.DAY_OF_MONTH) + ", " + calendar.get(Calendar.YEAR) + " " + hour + ":" + minute + " " + aMPM;
    }

    /**
     * Get all the events associated with the provided calendar display name sorted by chronological
     * order (recent first) with the following columns: Events.ID, Events.TITLE, Events.DTSTART,
     * Events.DTEND
     * 
     * @param context required in order to create a query
     * @param calendarDisplayName the name of the calendar to fetch events from
     * @return may be empty if the query returned no rows
     */
    @TargetApi(14)
    public static Cursor getEventsByCalendarDisplayName(Context context, String calendarDisplayName) {
	String[] projection = { Events._ID, Events.TITLE, Events.DTSTART, Events.DTEND };
	String selection = Calendars.CALENDAR_DISPLAY_NAME + " = " + DatabaseUtils.sqlEscapeString(calendarDisplayName);
	Cursor cursor = context.getContentResolver().query(Events.CONTENT_URI, projection, selection, null, Events.DTEND + " DESC");
	return cursor;
    }

    /**
     * return the string equivalent of the Calendar month constants.
     * 
     * @param month Calendar constant for the month
     * @return String equivalent
     */
    public static String getMonthString(int month) {
	switch (month) {
	    case Calendar.JANUARY:
		return "Jan.";
	    case Calendar.FEBRUARY:
		return "Feb.";
	    case Calendar.MARCH:
		return "Mar.";
	    case Calendar.APRIL:
		return "Apr.";
	    case Calendar.MAY:
		return "May";
	    case Calendar.JUNE:
		return "June";
	    case Calendar.JULY:
		return "July";
	    case Calendar.AUGUST:
		return "Aug.";
	    case Calendar.SEPTEMBER:
		return "Sept.";
	    case Calendar.OCTOBER:
		return "Oct.";
	    case Calendar.NOVEMBER:
		return "Nov.";
	    case Calendar.DECEMBER:
		return "Dec.";
	    case Calendar.UNDECIMBER:
		return "Undec.";
	    default:
		return "";
	}
    }
}
