package com.noobs2d.businessappointer;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * Main activity with Tab menus.
 * 
 * @author MrUseL3tter
 */
@SuppressWarnings("deprecation")
public class BusinessAppointer extends TabActivity {

    private static final String CALENDAR = "CALENDAR";
    private static final String EVENTS = "EVENTS";
    private static final String MAP = "MAP";

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main_tabs);
	Settings.load();

	TabHost tabhost = getTabHost();

	// calendar tab
	TabSpec calendarTab = tabhost.newTabSpec(CALENDAR);
	calendarTab.setIndicator(CALENDAR);
	Intent calendarIntent = new Intent(getApplicationContext(), CalendarMenu.class);
	calendarTab.setContent(calendarIntent);

	// events tab
	TabSpec eventsTab = tabhost.newTabSpec(EVENTS);
	eventsTab.setIndicator(EVENTS);
	Intent eventsIntent = new Intent(getApplicationContext(), EventsMenu.class);
	eventsTab.setContent(eventsIntent);

	// map tab
	TabSpec mapTab = tabhost.newTabSpec(MAP);
	mapTab.setIndicator(MAP);
	Intent mapIntent = new Intent(getApplicationContext(), MapMenu.class);
	mapTab.setContent(mapIntent);

	tabhost.addTab(calendarTab);
	tabhost.addTab(eventsTab);
	tabhost.addTab(mapTab);
    }

}
