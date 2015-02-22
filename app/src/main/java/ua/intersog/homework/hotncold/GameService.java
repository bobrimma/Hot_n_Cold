package ua.intersog.homework.hotncold;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

public class GameService extends IntentService implements com.google.android.gms.location.LocationListener, SensorEventListener {

    public static final String KEY_COMPASS_AZIMUTH = "COMPASS AZIMUTH";
    public static final String KEY_LATITUDE= "CURRENT LATITUDE";
    public static final String KEY_LONGITUDE= "CURRENT LONGITUDE";

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private float[] mGravity;
    private float[] mGeomagnetic;
    private float azimutCompass;
    private double azimuthInDegress;

    public GameService() {
        super("GameService");
        Log.d("mygame", "GameService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("mygame", "onHandleIntent");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.d("mygame", "onSensorChanged");
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
                azimutCompass = orientation[0];
                azimuthInDegress =(Math.toDegrees(azimutCompass) + 360) % 360;
                Log.d("mygame", "compass: " + azimuthInDegress);

            }
        }
        Intent data = new Intent(GameActivity.PROJECT_PKJ);
        data.putExtra(KEY_COMPASS_AZIMUTH, azimuthInDegress);
        sendBroadcast(data);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onCreate() {
        Log.d("mygame", "onCreate ");
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        Log.d("mygame", "onDestroy ");
        super.onDestroy();

    }
}
