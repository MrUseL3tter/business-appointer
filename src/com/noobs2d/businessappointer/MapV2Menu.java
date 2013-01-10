package com.noobs2d.businessappointer;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Map menu using the new version of android gmaps.
 * 
 * @author MrUseL3tter
 */
public class MapV2Menu extends FragmentActivity {

    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;
    private LatLng point;

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
	mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
	mMap.setIndoorEnabled(true);
	mMap.setMyLocationEnabled(true);
	mMap.setTrafficEnabled(true);
	mMap.getUiSettings().setCompassEnabled(true);
	mMap.setOnMapLongClickListener(new OnMapLongClickListener() {

	    @Override
	    public void onMapLongClick(LatLng point) {
		MapV2Menu.this.point = point;
		showDialog(Dialogs.SET_DESTINATION_PROMPT);
	    }
	});
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this Activity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the Activity may not have been
     * completely destroyed during this process (it is likely that it would only be stopped or
     * paused), {@link #onCreate(Bundle)} may not be called again so we should call this method in
     * {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
	// Do a null check to confirm that we have not already instantiated the map.
	if (mMap == null) {
	    // Try to obtain the map from the SupportMapFragment.
	    mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	    // Check if we were successful in obtaining the map.
	    if (mMap != null)
		setUpMap();
	}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.mapv2);
	setUpMapIfNeeded();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
	Builder builder = new AlertDialog.Builder(this);
	String message = "";
	switch (id) {
	    case Dialogs.SET_DESTINATION_PROMPT:
		message = "Set this point (" + point.latitude + ", " + point.longitude + ") as the destination?";
		break;
	    default:
		assert false;
	}
	builder.setMessage(message);
	builder.setCancelable(false);
	builder.setTitle("Set Destination");
	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	    }
	});
	builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {

	    }
	});
	AlertDialog dialog = builder.create();
	dialog.show();
	return super.onCreateDialog(id);
    }

    @Override
    protected void onResume() {
	super.onResume();
	setUpMapIfNeeded();
    }
}
