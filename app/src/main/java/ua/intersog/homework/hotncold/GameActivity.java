package ua.intersog.homework.hotncold;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class GameActivity extends ActionBarActivity {

    public static final String LOG_TAG = "HnC: GameActivity";
    private SensorManager sensorManager;
    private TextView azimuthTV;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        double azimuth;
        if ((azimuth = intent.getDoubleExtra(ParserService.PREF_AZIMUTH, 361.0)) != 361.0) {
            azimuthTV.setText(Double.toString(azimuth));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        azimuthTV = (TextView) findViewById(R.id.azimuthTV);
        BrReceiver receiver = new BrReceiver();
        Intent parseIntent = new Intent(this, ParserService.class);
        registerReceiver(receiver, new IntentFilter(ParserService.ACTION_READY));
        parseIntent.putExtra(MapActivity.EXTRA_LATLNG, getIntent().getParcelableExtra(MapActivity.EXTRA_LATLNG));
        parseIntent.putExtra(MapActivity.EXTRA_MYLATLNG, getIntent().getParcelableExtra(MapActivity.EXTRA_MYLATLNG));
        startService(parseIntent);
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

class BrReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        double azimuth = intent.getDoubleExtra(ParserService.PREF_AZIMUTH, 361.0);
        Intent showAzi = new Intent(context, GameActivity.class);
        showAzi.putExtra(ParserService.PREF_AZIMUTH, azimuth);
        showAzi.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(showAzi);
    }
}
