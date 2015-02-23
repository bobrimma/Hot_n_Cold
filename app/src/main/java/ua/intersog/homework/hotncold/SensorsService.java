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
    public static final String EXTRA_DIST = "distanceValue";

    private SensorManager mSensorManager;
    private GoogleApiClient apiClient;
    private LatLng destLL;
    private LatLng myLL;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
//  Service lifecycle section

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //   Log.d("mygame", "onStartCommand");

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
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // Log.d("mygame", "onDestroy");
        if (apiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
            apiClient.disconnect();
        }
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
        // Log.d("mygame", "onSensorChanged");
        if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == magnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
            Intent newData = new Intent(ACTION_NEWDATA);
            newData.putExtra(EXTRA_COMPASS, azimuthInDegress);
            newData.putExtra(EXTRA_AZIMUTH,(float)MyPoint.getAzimuth(myLL, destLL));
            newData.putExtra(EXTRA_DIST,(int)MyPoint.getDistance(myLL, destLL));
            sendBroadcast(newData);
            //    Log.d("mygame", "compass: " + azimuthInDegress);

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