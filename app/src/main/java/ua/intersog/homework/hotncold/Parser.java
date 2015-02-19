package ua.intersog.homework.hotncold;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static final String LOG_TAG = "HnC: Parser";
    private HttpURLConnection urlConnection;
    private BufferedReader reader;

    public Parser(LatLng curPos, LatLng targetPoint) {
        Uri siteUri = new Uri.Builder()
                .scheme("http")
                .authority("garmin.com.ua")
                .appendPath("tools")
                .appendPath("calc.php")
                .appendQueryParameter("typ", "0")
                .appendQueryParameter("n1", String.valueOf(curPos.latitude))
                .appendQueryParameter("e1", String.valueOf(curPos.longitude))
                .appendQueryParameter("n2", String.valueOf(targetPoint.latitude))
                .appendQueryParameter("e2", String.valueOf(targetPoint.longitude))
                .build();
        try {
            urlConnection = (HttpURLConnection) new URL(siteUri.toString()).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double parse() {
        String response, line;
        StringBuilder buffer = new StringBuilder();
        try {
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            if (buffer.length() == 0) {
                return 361.0;
            } else
                response = buffer.toString();

            List<String> parsedStrings = new ArrayList<>();
            Matcher matcher = Pattern.compile(">\\d{1,3}\\.\\d+").matcher(response);
            while (matcher.find()){
                parsedStrings.add(matcher.group().substring(1));
            }
            return Double.valueOf(parsedStrings.get(1));

        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return 361.0;
    }
}
