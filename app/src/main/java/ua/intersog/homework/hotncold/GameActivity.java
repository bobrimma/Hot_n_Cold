package ua.intersog.homework.hotncold;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;


public class GameActivity extends ActionBarActivity {

    public static final String LOG_TAG = "HnC: GameActivity";
    private TextView differenceTV;
    private TextView azimuthTV;
    private TextView compassTV;
    private LatLng destLL;
    private LatLng myLL;
    private CompassBroadReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        destLL = getIntent().getParcelableExtra(MapActivity.EXTRA_LATLNG);
        myLL = getIntent().getParcelableExtra(MapActivity.EXTRA_MYLATLNG);
        differenceTV = (TextView) findViewById(R.id.difference);
        azimuthTV = (TextView) findViewById(R.id.azimuth);
        compassTV = (TextView) findViewById(R.id.compassValue);
        differenceTV.setText(Double.toString(MyPoint.getAzimuth(myLL, destLL)));
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

    class CompassBroadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            double azimuth = intent.getDoubleExtra(SensorsService.EXTRA_AZIMUTH, 361);
            double compass = intent.getDoubleExtra(SensorsService.EXTRA_COMPASS, 361);
            double diff = (Math.abs(azimuth - compass) < 180) ? Math.abs(azimuth - compass) : 360 - Math.abs(azimuth - compass);
            azimuthTV.setText(Double.toString(azimuth));
            compassTV.setText(Double.toString(compass));
            differenceTV.setText(Double.toString(diff) + (char) 176);
        }
    }
}
