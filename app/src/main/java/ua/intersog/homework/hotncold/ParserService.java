package ua.intersog.homework.hotncold;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;


public class ParserService extends IntentService {

    public static final String LOG_TAG = "HnC: ParserService";
    public static final String PREF_AZIMUTH = "azimuth";
    public static final String NAME_SHARED_PREF = "HnC";
    public static final String ACTION_READY = "ua.intersog.homework.hotncold.azimuthIsReady";

    public ParserService() {
        super("ParserService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            LatLng myLatLng = intent.getParcelableExtra(MapActivity.EXTRA_MYLATLNG);
            LatLng latLng = intent.getParcelableExtra(MapActivity.EXTRA_LATLNG);
            Parser parser = new Parser(myLatLng, latLng);
            double azimuth = parser.parse();
            getSharedPreferences(NAME_SHARED_PREF, MODE_PRIVATE)
                    .edit()
                    .putString(PREF_AZIMUTH, String.valueOf(azimuth))
                    .commit();
            Intent azimuthReady = new Intent(ACTION_READY);
            azimuthReady.putExtra(PREF_AZIMUTH, azimuth);
            sendBroadcast(azimuthReady);
        }
    }
}