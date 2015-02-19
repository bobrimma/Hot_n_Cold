package ua.intersog.homework.hotncold;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private Marker myMarker;
    private LatLng myLL;
    public static final String LOG_TAG = "HnC: MapActivity";
    public static final String EXTRA_LATLNG = "latLng";
    public static final String EXTRA_MYLATLNG = "myLatLng";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        assignMap();
    }

    @Override
    protected void onPause() {
        if (apiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
            apiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            apiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            apiClient.connect();
        }
    }

    private void assignMap() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    private void setUpMap() {
        Location myLoc = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        myLL = new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
        myMarker = mMap.addMarker(new MarkerOptions()
                .position(myLL)
                .title("Я тут")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_myloc)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLL, 14));
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(MapActivity.this)
                        .setMessage(R.string.target_set)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent startGame = new Intent(getApplicationContext(), GameActivity.class);
                                startGame.putExtra(EXTRA_LATLNG, latLng);
                                startGame.putExtra(EXTRA_MYLATLNG, myLL);
                                startGame.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(startGame);
                                finish();
                            }
                        });
                AlertDialog alert = adBuilder.create();
                alert.show();
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        while(!apiClient.isConnected()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LocationRequest request = new LocationRequest()
                .setInterval(3000)
                .setFastestInterval(2500)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
        setUpMap();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        myMarker.remove();
        myLL = new LatLng(location.getLatitude(), location.getLongitude());
        myMarker = mMap.addMarker(new MarkerOptions()
                .position(myLL)
                .title("Я тут")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_myloc)));
        Log.i(this.getLocalClassName(), "Location changed, new: " + myLL);
    }
}
