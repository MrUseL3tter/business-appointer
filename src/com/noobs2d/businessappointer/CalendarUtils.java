package com.noobs2d.businessappointer;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.CalendarContract.Events;

/**
 * Utility class for various calendar ops
 * 
 * @author MrUseL3tter
 */
public class CalendarUtils {

    @TargetApi(14)
    public static int deleteEvent(ContentResolver contentResolver, String title) {
	Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, Long.parseLong(title));
	return contentResolver.delete(deleteUri, null, null);
    }
}
