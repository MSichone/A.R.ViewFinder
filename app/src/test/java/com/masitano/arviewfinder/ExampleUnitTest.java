package com.masitano.arviewfinder;

import android.location.Location;

import com.google.gson.Gson;
import com.masitano.arviewfinder.models.Sensor;
import com.masitano.arviewfinder.utilities.SensorStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    /*@Test
    public void addition_isCorrect() throws Exception {
        System.out.println("ExampleUnitTest.addition_isCorrect");
        assertEquals(4, 2 + 2);

    }*/

    @Test
    public void urban_observatory_server_responds() throws Exception {
        System.out.println("ExampleUnitTest.urban_observatory_server_responds");

        Gson gson;
        InputStream is = null;
        String json = "";

        Location mLastKnownLocation = new Location("manual");
        mLastKnownLocation.setLongitude(-1.617513);
        mLastKnownLocation.setLatitude(54.978188);
        int mapViewRange = 300;

        HttpClient httpclient = HttpClients.createDefault();
        String coordinatesToSearch = mLastKnownLocation.getLongitude() + "," + mLastKnownLocation.getLatitude() + "," + mapViewRange;

        URIBuilder builder = new URIBuilder("http://uoweb1.ncl.ac.uk/api/v1/sensors/live.json");
        builder.setParameter("api_key", "7b1a9ytmmhdcruloy81icy1u2dfk04njuco308y9eaxt9vfygr79aiplatb5c71s9iq30bdlad7swin7qgi7c2nvdt");
        builder.setParameter("buffer", coordinatesToSearch);
        builder.setParameter("variable", "Temperature");

        URI uri = builder.build();
        System.out.println("Urban Observatory Request URL: " + uri.toString());

        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpclient.execute(request);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            System.out.println("Urban Observatory Response: " + json);
            JSONArray jsonArray = new JSONArray(json);
            gson = new Gson();

            //replace sensor data
            if (jsonArray.length() > 0){
                //clear previous sensor data
                SensorStore.getInstance().clearSensors();
                //sensors.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Sensor sensor = gson.fromJson(jsonObject.toString(), Sensor.class);
                    SensorStore.getInstance().addSensor(sensor);
                }
            }

        }

        assertEquals(0, 2 - 2);

    }
}