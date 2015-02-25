
package ua.intersog.homework.hotncold;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;


public class GameActivity extends ActionBarActivity {

    public static final String LOG_TAG = "HnC: GameActivity";

    private TextView resultTV;
    private TextView distanceTV;

    private LatLng destLL;
    private LatLng myLL;
    private CompassBroadReceiver receiver;
    private boolean recieverOn;

    private ProgressBar mProgressBar;

    private int gameResult = 0;
    private final String VERY_HOT = "Very hot: ";
    private final String HOT = "Hot: ";
    private final String COLD = "Cold: ";
    private final String VERY_COLD = "Very cold: ";
    private final String GAME_FINISHED = "Game finished. You've reached the point! ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        destLL = getIntent().getParcelableExtra(MapActivity.EXTRA_LATLNG);
        myLL = getIntent().getParcelableExtra(MapActivity.EXTRA_MYLATLNG);


        resultTV = (TextView) findViewById(R.id.resultTV);
        distanceTV = (TextView) findViewById(R.id.distanceTV);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setMax(180);
    }


    @Override
    protected void onResume() {
        super.onResume();
        receiver = new CompassBroadReceiver();
        registerReceiver(receiver, new IntentFilter(SensorsService.ACTION_NEWDATA));
        recieverOn = true;
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
        if (recieverOn) {
            unregisterReceiver(receiver);
            recieverOn=false;
        }
        super.onPause();
    }

    class CompassBroadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.d("mygame", "onReceive");

            float azimuth = intent.getFloatExtra(SensorsService.EXTRA_AZIMUTH, 361);
            float compass = intent.getFloatExtra(SensorsService.EXTRA_COMPASS, 361);
            int distance = intent.getIntExtra(SensorsService.EXTRA_DIST, -1);
            float diff = (Math.abs(azimuth - compass) < 180) ? Math.abs(azimuth - compass) : 360 - Math.abs(azimuth - compass);
            int diffInt = (int) diff;
            setGameProgress(diffInt, distance);


        }
    }

    private void setGameProgress(int diffInt, int distance) {
        gameResult = 180 - diffInt;
        if (gameResult > 178 && distance <= 1) {
            configProgressBar(R.drawable.vertical_very_hot, "#FF0000", GAME_FINISHED, gameResult);
            Intent sensorsStop = new Intent(this, SensorsService.class);
            stopService(sensorsStop);
            if (recieverOn) {
                unregisterReceiver(receiver);
                recieverOn=false;
            }
        } else if (gameResult > 150) {
            configProgressBar(R.drawable.vertical_very_hot, "#FF0000", VERY_HOT, gameResult);
        } else if (gameResult <= 150 && gameResult >= 100) {
            configProgressBar(R.drawable.vertical_hot, "#FF3366", HOT, gameResult);
        } else if (gameResult < 100 && gameResult > 50) {
            configProgressBar(R.drawable.vertical_cold, "#0033FF", COLD, gameResult);
        } else if (diffInt >= 50) {
            configProgressBar(R.drawable.vertical_very_cold, "#0099FF", VERY_COLD, gameResult);
        }
        mProgressBar.setProgress(gameResult);
        distanceTV.setText(Integer.toString(distance) + " metres");

    }

    private void configProgressBar(int type, String color, String text, int temperature) {
        mProgressBar.setProgressDrawable(getResources().getDrawable(type));
        resultTV.setTextColor(Color.parseColor(color));
        resultTV.setText(text + Integer.toString(temperature) + (char) 176);

    }
}

