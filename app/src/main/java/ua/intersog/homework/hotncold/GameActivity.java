package ua.intersog.homework.hotncold;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;


public class GameActivity extends ActionBarActivity {

    public static final String LOG_TAG = "HnC: GameActivity";
    private SensorManager sensorManager;
    private TextView azimuthTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        LatLng destLL = getIntent().getParcelableExtra(MapActivity.EXTRA_LATLNG);
        LatLng myLL = getIntent().getParcelableExtra(MapActivity.EXTRA_MYLATLNG);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        azimuthTV = (TextView) findViewById(R.id.azimuthTV);
        azimuthTV.setText(Double.toString(MyPoint.getAzimuth(destLL, myLL)));
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
}
