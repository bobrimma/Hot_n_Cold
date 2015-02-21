package ua.intersog.homework.hotncold;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class SensorsService extends Service
        implements
        SensorEventListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    public static final String ACTION_NEWDATA = "ua.intersog.homework.hotncold.new_sensor_data";
    public static final String EXTRA_COMPASS = "compassValue";
    public static final String EXTRA_AZIMUTH = "azimuthValue";
    private SensorManager mSensorManager;
    private GoogleApiClient apiClient;
    private float[] mGravity;
    private float[] mGeomagnetic;
    private LatLng destLL;
    private LatLng myLL;
    private Sensor accelerometer;
    private Sensor magnetometer;

//  Service lifecycle section

    public SensorsService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
        apiClient.connect();
        destLL = intent.getParcelableExtra(MapActivity.EXTRA_LATLNG);
        myLL = intent.getParcelableExtra(MapActivity.EXTRA_MYLATLNG);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//  Service lifecycle section ended
//
//  Sensors section

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimutCompass = orientation[0];
                double azimuthInDegrees = (Math.toDegrees(azimutCompass) + 360) % 360;
                Intent newData = new Intent(ACTION_NEWDATA);
                newData.putExtra(EXTRA_COMPASS, azimuthInDegrees);
                newData.putExtra(EXTRA_AZIMUTH, MyPoint.getAzimuth(myLL, destLL));
                sendBroadcast(newData);
                Log.d("mygame", "compass: " + azimuthInDegrees);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

//  Sensors section ended
//
//  Google API Client section

    @Override
    public void onConnected(Bundle bundle) {
        while (!apiClient.isConnected()) {
            try {
                Thread.sleep(100);// это очень грязный хак, но иначе иногда вылазит "GoogleApiClient isn't yet ready"
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LocationRequest request = new LocationRequest()
                .setInterval(3000)
                .setFastestInterval(2500)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onLocationChanged(Location location) {
        myLL = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("mygame", "Location changed, the new one is " + location);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

//  Google API Client section ended
}
