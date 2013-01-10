package com.noobs2d.businessappointer;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.noobs2d.businessappointer.routing.RouteOverlayCallback;

public class MapMenu extends MapActivity implements LocationListener, RouteOverlayCallback {

    private MapView mapView;
    private MyLocationOverlay currentLocationOverlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.map_menu);

	mapView = (MapView) findViewById(R.id.mapView203);
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

    }

    @Override
    public void onLocationChanged(Location location) {
	// TODO Auto-generated method stub
    }

    @Override
    public void onOverlayError() {
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

    @Override
    protected boolean isRouteDisplayed() {
	return false;
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
