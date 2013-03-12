package com.teamapp.ui.team;

import java.util.List;
import java.util.Locale;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class GMapAct extends MapActivity {

	private MapView mapView;
	private List<Overlay> mapOverlays;

	private MapController mapController;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gmap);

		String location = getIntent().getStringExtra("location");

		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);

		mapController = mapView.getController();
		mapController.setZoom(14);

		mapOverlays = mapView.getOverlays();

		Drawable mMarker = getResources().getDrawable(R.drawable.gpin);
		mMarker.setBounds(0, 0, mMarker.getIntrinsicWidth(),
				mMarker.getIntrinsicHeight());

		MyItemizedOverlay itemizedOverlay = new MyItemizedOverlay(mMarker,
				mapView);

		showLocationOnMap(location, itemizedOverlay);
		mapOverlays.add(itemizedOverlay);
		mapView.invalidate();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void showLocationOnMap(String addr,
			MyItemizedOverlay itemizedOverlay) {
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try {
			Address address = geocoder.getFromLocationName(addr, 1).get(0);
			GeoPoint point = new GeoPoint((int) (address.getLatitude() * 1E6),
					(int) (address.getLongitude() * 1E6));
			OverlayItem overlayitem = new OverlayItem(point, addr, addr);
			itemizedOverlay.addOverlay(overlayitem);
			if (point != null) {
				mapController.animateTo(point);
			}

		} catch (Exception e) {
			// TODO: handle exception
			TeamAppAlerts.showToast(this, getString(R.string.location_not));
			finish();
		}
	}

}
