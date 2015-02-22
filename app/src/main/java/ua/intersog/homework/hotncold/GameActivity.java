
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        destLL = getIntent().getParcelableExtra(MapActivity.EXTRA_LATLNG);
        myLL = getIntent().getParcelableExtra(MapActivity.EXTRA_MYLATLNG);

        differenceTV = (TextView) findViewById(R.id.difference);
        azimuthTV = (TextView) findViewById(R.id.azimuth);
        compassTV = (TextView) findViewById(R.id.compassValue);
        resultTV = (TextView) findViewById(R.id.resultTV);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setMax(100);
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
}

