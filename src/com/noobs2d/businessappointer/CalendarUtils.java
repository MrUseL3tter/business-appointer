package com.noobs2d.businessappointer;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.CalendarContract.Events;

public class CalendarUtils {

    @TargetApi(14)
    public static void deleteEvent(ContentResolver contentResolver, String title) {

	Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, Long.parseLong(title));

	System.out.println("DELETING EVENT #" + title);
	int i = contentResolver.delete(deleteUri, null, null);
	//	int i = contentResolver.delete(Events.CONTENT_URI, Events._ID + " =? ", new String[] { DatabaseUtils.sqlEscapeString(title) });
	System.out.println("DELETED ROWS: " + i);
    }
}
