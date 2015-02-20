package ua.intersog.homework.hotncold;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;


public class GameActivity extends ActionBarActivity {

    public static final String LOG_TAG = "HnC: GameActivity";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("mygame", "onCreate GameActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        destLL = getIntent().getParcelableExtra(MapActivity.EXTRA_LATLNG);
        myLL = getIntent().getParcelableExtra(MapActivity.EXTRA_MYLATLNG);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
