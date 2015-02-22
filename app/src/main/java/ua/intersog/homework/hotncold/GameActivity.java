package ua.intersog.homework.hotncold;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
<<<<<<< HEAD
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ProgressBar;
=======
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
>>>>>>> f9170efaf436ef961c4a3f966a4d268eea3d22d4
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;


public class GameActivity extends ActionBarActivity {

    public static final String LOG_TAG = "HnC: GameActivity";
<<<<<<< HEAD
    private TextView differenceTV;
    private TextView azimuthTV;
    private TextView compassTV;
    private TextView resultTV;

    private LatLng destLL;
    private LatLng myLL;
    private CompassBroadReceiver receiver;

    private ProgressBar mProgressBar;

    private int gameResult = 0;
    private final String VERY_HOT="Very hot";
    private final String HOT="Hot";
    private final String COLD="Cold";
    private final String VERY_COLD="Very cold";

=======
    public static final String KEY_AZIMUTH = "AZIMUTH";
    public static final String PROJECT_PKJ = "ua.intersog.homework.hotncold";

    private TextView azimuthTV;
    private TextView compassTV;
//    private TextView latitudeTV;
//    private TextView longitudeTV;
//    private TextView azimuthCurTV;
    private LatLng destLL;
    private LatLng myLL;
    private Intent gameService;
>>>>>>> f9170efaf436ef961c4a3f966a4d268eea3d22d4

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("mygame", "onCreate GameActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        destLL = getIntent().getParcelableExtra(MapActivity.EXTRA_LATLNG);
        myLL = getIntent().getParcelableExtra(MapActivity.EXTRA_MYLATLNG);
<<<<<<< HEAD

        differenceTV = (TextView) findViewById(R.id.difference);
        azimuthTV = (TextView) findViewById(R.id.azimuth);
        compassTV = (TextView) findViewById(R.id.compassValue);
        resultTV = (TextView) findViewById(R.id.resultTV);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setMax(100);
=======
        azimuthTV = (TextView) findViewById(R.id.azimuthTV);
        double azimuth = MyPoint.getAzimuth(destLL, myLL);
        azimuthTV.setText(Double.toString(azimuth));

        compassTV = (TextView) findViewById(R.id.compassTV);
//        latitudeTV = (TextView) findViewById(R.id.latitudeTV);
//        longitudeTV = (TextView) findViewById(R.id.longitudeTV);
//        azimuthCurTV = (TextView) findViewById(R.id.azimuthCurTV);

        ResultReceiver receiver = new ResultReceiver();
        registerReceiver(receiver, new IntentFilter(PROJECT_PKJ));

        gameService = new Intent(GameActivity.this, GameService.class);
        gameService.putExtra(KEY_AZIMUTH, azimuth);
        startService(gameService);

>>>>>>> f9170efaf436ef961c4a3f966a4d268eea3d22d4
    }


    @Override
    protected void onResume() {
        super.onResume();
        receiver = new CompassBroadReceiver();
        registerReceiver(receiver, new IntentFilter(SensorsService.ACTION_NEWDATA));
        Intent sensorsStart = new Intent(this, SensorsService.class);
        if (destLL != null)
            sensorsStart.putExtra(MapActivity.EXTRA_LATLNG, destLL);
        sensorsStart.putExtra(MapActivity.EXTRA_MYLATLNG, myLL);
        startService(sensorsStart);
    }

    @Override
    protected void onPause() {
        Intent sensorsStop = new Intent(this, SensorsService.class);
        stopService(sensorsStop);
        unregisterReceiver(receiver);
        super.onPause();
    }

    class CompassBroadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.d("mygame", "onReceive");

            float azimuth = intent.getFloatExtra(SensorsService.EXTRA_AZIMUTH, 361);
            float compass = intent.getFloatExtra(SensorsService.EXTRA_COMPASS, 361);
            float diff = (Math.abs(azimuth - compass) < 180) ? Math.abs(azimuth - compass) : 360 - Math.abs(azimuth - compass);
            int azimuthInt = (int) azimuth;
            int compassInt = (int) compass;
            int diffInt = (int) diff;
            azimuthTV.setText(Integer.toString(azimuthInt) + (char) 176);
            compassTV.setText(Integer.toString(compassInt) + (char) 176);
            differenceTV.setText(Integer.toString(diffInt) + (char) 176);
            setGameProgress(diffInt);

        }
    }

    private void setGameProgress(int diffInt) {
        if (diffInt < 20) {
            configProgressBar(R.drawable.vertical_hot,"#FF0033",VERY_HOT);
            gameResult = (int) (100 - diffInt * 0.8);

        } else if (diffInt <= 50) {
            configProgressBar(R.drawable.vertical_hot,"#FF3366",HOT);
            gameResult = 100 - diffInt;

        } else if (diffInt > 50 && diffInt < 100) {
            configProgressBar(R.drawable.vertical_cold,"#0099FF",COLD);
            gameResult = (int) (50 - diffInt * 0.15);

        } else if (diffInt >= 100) {
            configProgressBar(R.drawable.vertical_cold,"#0033FF",VERY_COLD);
            gameResult = (int) (50 - diffInt * 0.25);

        }
        mProgressBar.setProgress(gameResult);
    }

    private void configProgressBar(int type, String color, String text){
        mProgressBar.setProgressDrawable(getResources().getDrawable(type));
        resultTV.setTextColor(Color.parseColor(color));
        resultTV.setText(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    class ResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("mygame", "onReceive");
            double receivedCompass = intent.getDoubleExtra(GameService.KEY_COMPASS_AZIMUTH, 361.0);
            compassTV.setText(Double.toString(receivedCompass));

//            double latitude = intent.getDoubleExtra(GameService.KEY_LATITUDE, 0.0);
//            double longitude = intent.getDoubleExtra(GameService.KEY_LONGITUDE, 0.0);
//            latitudeTV.setText(Double.toString(latitude));
//            longitudeTV.setText(Double.toString(longitude));
//            destLL = new LatLng(latitude, longitude);
//            azimuthCurTV.setText(Double.toString(MyPoint.getAzimuth(destLL, myLL)));

        }
    }
}
