package edu.gvsu.cis.convcalc.webservice;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class WeatherService extends IntentService {
    private static final String TAG = "WeatherService";
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_WEATHER_AT = "edu.gvsu.cis.webservice.action.WEATHER_AT";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?units=imperial&";
    public static final String BROADCAST_WEATHER = "edu.gvsu.cis.webservice.action.BROADCAST";
    private static final String EXTRA_KEY = "edu.gvsu.cis.webservice.extra.KEY";
    private static final String EXTRA_LAT = "edu.gvsu.cis.webservice.extra.LAT";
    private static final String EXTRA_LNG = "edu.gvsu.cis.webservice.extra.LNG";

    public WeatherService() {
        super("WeatherService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startGetWeather(Context context, String lat, String lng, String key) {
        Intent intent = new Intent(context, WeatherService.class);
        intent.setAction(ACTION_WEATHER_AT);
        intent.putExtra(EXTRA_LAT, lat);
        intent.putExtra(EXTRA_LNG, lng);
        intent.putExtra(EXTRA_KEY, key);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_WEATHER_AT.equals(action)) {
                final String key = intent.getStringExtra(EXTRA_KEY);
                final String lat = intent.getStringExtra(EXTRA_LAT);
                final String lng = intent.getStringExtra(EXTRA_LNG);
                fetchWeatherData(key, lat, lng);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void fetchWeatherData(String key, String lat, String lon) {
        try {
//            URL url = new URL(BASE_URL + String.format("lat=%s&lon=%s&appid=%s", lat, lon, key));
                URL url = new URL(BASE_URL+"lat=" + lat + "&lon=" + lon + "&appid=" + key);
//            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?units=imperial&lat=42.963686&lon=-85.888595&appid=51a006874b9214a7475ef8cc6be7a2a2");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000 /* milliseconds */);
            conn.setConnectTimeout(10000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int len;
                while ((len = bis.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }
                JSONObject data = new JSONObject(new String(baos.toByteArray()));
                JSONObject main = data.getJSONObject("main");
                JSONArray weather = data.getJSONArray("weather");

                // TODO: extract the values you need out of main and weather...
                Intent result = new Intent(BROADCAST_WEATHER);

                JSONObject w = weather.getJSONObject(0);
                Double temp = main.getDouble("temp");
                String summary = w.getString("description");
                String icon = "img" + w.getString("icon");

                // TODO: use putExtra to add the extracted values to your broadcast.

                result.putExtra("WEATHER", summary);
                result.putExtra("KEY", key);
                result.putExtra("SUMMARY", summary);
                result.putExtra("ICON", icon);
                result.putExtra("TEMPERATURE", temp);
                LocalBroadcastManager.getInstance(this).sendBroadcast(result);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
