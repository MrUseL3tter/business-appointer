package com.noobs2d.businessappointer;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Projection;
import com.noobs2d.businessappointer.routing.GoogleParser;
import com.noobs2d.businessappointer.routing.Parser;
import com.noobs2d.businessappointer.routing.Route;
import com.noobs2d.businessappointer.routing.RouteOverlay;
import com.noobs2d.businessappointer.routing.RouteOverlayCallback;

public class MapV1Menu extends MapActivity implements LocationListener, RouteOverlayCallback {

    private static final int BEGIN_PROMPT = 1;
    private static final int MARK_LOCATION_PROMPT = 2;
    private static final int SET_LOCATION_ERROR_PROMPT = 3;
    private static final int ADD_CONTACTS_PROMPT = 4;
    private static final int SHOW_SEARCH_RESULTS = 5;

    @SuppressLint("NewApi")
    private class DownloadWebDataTask extends AsyncTask<String, Void, Route> {

	@Override
	protected Route doInBackground(String... data) {
	    return directions(new GeoPoint(Integer.parseInt(data[0]), Integer.parseInt(data[1])), new GeoPoint(Integer.parseInt(data[2]), Integer.parseInt(data[3])));
	}

	@Override
	protected void onPostExecute(Route result) {
	    RouteOverlay routeOverlay = new RouteOverlay(MapV1Menu.this, result, Color.BLUE);
	    if (mapView.getOverlays().get(mapView.getOverlays().size() - 1) instanceof RouteOverlay)
		mapView.getOverlays().remove(mapView.getOverlays().size() - 1);
	    mapView.getOverlays().add(routeOverlay);

	    mapView.invalidate();
	    next.setEnabled(true);
	    super.onPostExecute(result);
	}
    }

    private class SearchMapsTask extends AsyncTask<String, Void, List<Address>> {

	private ProgressDialog progressDialog;

	@Override
	protected List<Address> doInBackground(String... query) {
	    try {
		return new Geocoder(getApplicationContext()).getFromLocationName(query[0], 5);
	    } catch (IOException e) {
		return null;
	    }
	}

	@Override
	protected void onPostExecute(List<Address> result) {
	    progressDialog.dismiss();
	    //	    searchView.clearFocus();
	    if (result.size() > 0)
		mapView.getController().setCenter(new GeoPoint((int) (result.get(0).getLatitude() * 1E6), (int) (result.get(0).getLongitude() * 1E6)));
	    super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
	    progressDialog = ProgressDialog.show(MapV1Menu.this, "", "Searching...");
	    super.onPreExecute();
	}

    }

    private ArrayAdapter<String> addressAdapter;
    private MapView mapView;
    private MyLocationOverlay currentLocationOverlay;
    //    private SearchView searchView;
    private Button next;
    private boolean isPotentialLongPress;
    private int markX;
    private int markY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
	handleLongPress(event);
	return super.dispatchTouchEvent(event);
    }

    /**
     * Loops for an amount of time while checking if the state of the isPotentialLongPress variable
     * has changed. If it has, this is regarded as if the longpress has been canceled. Else it is
     * regarded as a longpress.
     */
    public boolean isLongPressDetected() {
	isPotentialLongPress = true;
	try {
	    for (int i = 0; i < 50; i++) {
		Thread.sleep(10);
		if (!isPotentialLongPress)
		    return false;
	    }
	    return true;
	} catch (InterruptedException e) {
	    return false;
	} finally {
	    isPotentialLongPress = false;
	}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.mapv1);

	mapView = (MapView) findViewById(R.id.mapView);
	mapView.setClickable(true);

	currentLocationOverlay = new MyLocationOverlay(getApplicationContext(), mapView);
	currentLocationOverlay.enableMyLocation();
	currentLocationOverlay.getLastFix();
	currentLocationOverlay.enableCompass();
	currentLocationOverlay.runOnFirstFix(new Runnable() {

	    @Override
	    public void run() {
		int currentLatitude = Double.valueOf(currentLocationOverlay.getLastFix().getLatitude() * 1E6).intValue();
		int currentLongitude = Double.valueOf(currentLocationOverlay.getLastFix().getLongitude() * 1E6).intValue();
		mapView.getController().setCenter(new GeoPoint(currentLatitude, currentLongitude));
		mapView.setSatellite(true);
	    }
	});
	mapView.getOverlays().add(currentLocationOverlay);
	mapView.invalidate();

	//	searchView = (SearchView) findViewById(R.id.search);
	//	searchView.setOnQueryTextListener(new OnQueryTextListener() {
	//
	//	    @Override
	//	    public boolean onQueryTextChange(String newText) {
	//		// TODO Auto-generated method stub
	//		return false;
	//	    }
	//
	//	    @Override
	//	    public boolean onQueryTextSubmit(String query) {
	//		new SearchMapsTask().execute(query);
	//		return false;
	//	    }
	//	});

	next = (Button) findViewById(R.id.nextButton);
	next.setEnabled(false);
	next.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		showDialog(ADD_CONTACTS_PROMPT);
	    }
	});
	showDialog(BEGIN_PROMPT);
    }

    @Override
    public void onLocationChanged(Location location) {
	// TODO Auto-generated method stub
    }

    @Override
    public void onOverlayError() {
	showDialog(SET_LOCATION_ERROR_PROMPT);
    }

    @Override
    public void onProviderDisabled(String provider) {
	// TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
	// TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
	// TODO Auto-generated method stub
    }

    private Route directions(final GeoPoint start, final GeoPoint dest) {
	Parser parser;
	String jsonURL = "http://maps.google.com/maps/api/directions/json?";
	final StringBuffer sBuf = new StringBuffer(jsonURL);
	sBuf.append("origin=");
	sBuf.append(start.getLatitudeE6() / 1E6);
	sBuf.append(',');
	sBuf.append(start.getLongitudeE6() / 1E6);
	sBuf.append("&destination=");
	sBuf.append(dest.getLatitudeE6() / 1E6);
	sBuf.append(',');
	sBuf.append(dest.getLongitudeE6() / 1E6);
	sBuf.append("&sensor=true&mode=driving");
	parser = new GoogleParser(sBuf.toString());
	Route r = parser.parse();
	return r;
    }

    private void handleLongPress(final MotionEvent event) {
	if (event.getAction() == MotionEvent.ACTION_DOWN)
	    new Thread(new Runnable() {

		@Override
		public void run() {
		    Looper.prepare();
		    if (isLongPressDetected()) {
			showDialogOnUiThread();
			markX = (int) event.getX();
			markY = (int) event.getY();
		    }
		}
	    }).start();
	else if (event.getAction() == MotionEvent.ACTION_MOVE) {
	    /*
	     * Only MotionEvent.ACTION_MOVE could potentially be regarded as part of a longpress, as
	     * this event is trigged by the finger moving slightly on the device screen. Any other
	     * events causes us to cancel this events status as a potential longpress.
	     */
	    if (event.getHistorySize() < 1)
		return; // First call, no history

	    // Get difference in position since previous move event
	    float diffX = event.getX() - event.getHistoricalX(event.getHistorySize() - 1);
	    float diffY = event.getY() - event.getHistoricalY(event.getHistorySize() - 1);

	    /*
	     * If position has moved substatially, this is not a long press but probably a drag
	     * action
	     */
	    if (Math.abs(diffX) > 0.5f || Math.abs(diffY) > 0.5f)
		isPotentialLongPress = false;
	} else
	    // This motion is something else, and thus not part of a longpress
	    isPotentialLongPress = false;
    }

    private void setDestinationPath(int targetLat, int targetLon) {
	String[] data = new String[4];
	int originLat = Double.valueOf(currentLocationOverlay.getLastFix().getLatitude() * 1E6).intValue();
	int originLon = Double.valueOf(currentLocationOverlay.getLastFix().getLongitude() * 1E6).intValue();
	data[0] = "" + originLat; // start LATITUDE
	data[1] = "" + originLon; // start LONGITUDE
	data[2] = "" + targetLat;
	data[3] = "" + targetLon;
	new DownloadWebDataTask().execute(data);
    }

    private void showDialogOnUiThread() {
	runOnUiThread(new Runnable() {

	    @Override
	    public void run() {
		showDialog(MARK_LOCATION_PROMPT);
	    }
	});
    }

    @Override
    protected boolean isRouteDisplayed() {
	return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
	Builder builder = new AlertDialog.Builder(this);
	switch (id) {
	    case BEGIN_PROMPT:
		builder.setMessage(R.string.mapv1_begin_prompt);
		builder.setCancelable(false);
		builder.setTitle("Reminder");
		builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    }
		});
		break;
	    case MARK_LOCATION_PROMPT:
		Projection p = mapView.getProjection();
		final GeoPoint geoPoint = p.fromPixels(markX, markY);
		final int targetLat = geoPoint.getLatitudeE6();
		final int targetLon = geoPoint.getLongitudeE6();
		builder.setMessage("Mark this point as the location?");
		builder.setCancelable(true);
		builder.setTitle("Confirm");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			setDestinationPath(targetLat, targetLon);
			getIntent().putExtra(Events.EVENT_LOCATION, targetLat + ", " + targetLon);
		    }
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    }
		});
		break;
	    case SET_LOCATION_ERROR_PROMPT:
		builder.setMessage(R.string.mapv1_set_location_error_prompt);
		builder.setCancelable(false);
		builder.setTitle("Error");
		builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
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
			Intent intent = new Intent(getApplicationContext(), ContactsMenu.class);
			intent.putExtras(getIntent());
			startActivity(intent);
			MapV1Menu.this.finish();
		    }
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

		    @TargetApi(14)
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			Intent intent = new Intent(Intent.ACTION_INSERT);
			intent.setData(CalendarContract.Events.CONTENT_URI);
			intent.putExtras(getIntent());
			startActivity(intent);
			MapV1Menu.this.finish();
		    }
		});
		break;
	    case SHOW_SEARCH_RESULTS:
		//		View view = getLayoutInflater().inflate(R.layout.list_layout, null);
		//		ListView list = (ListView) view.findViewById(R.id.list);
		//		if (list != null) {
		//		    list.setAdapter(addressAdapter);
		//		    builder.setView(view);
		//		}
		//		builder.setAdapter(addressAdapter, new Dialog.OnClickListener() {
		//
		//		    @Override
		//		    public void onClick(DialogInterface dialog, int which) {
		//			// TODO Auto-generated method stub.0
		//
		//		    }
		//		});
		break;
	    default:
		assert false;
	}
	AlertDialog dialog = builder.create();
	dialog.show();
	return super.onCreateDialog(id);
    }

    @Override
    protected void onPause() {
	currentLocationOverlay.disableMyLocation();
	super.onPause();
    }

    @Override
    protected void onResume() {
	currentLocationOverlay.enableMyLocation();
	super.onResume();
    }
}
