package com.borombo.demo.storelocatordemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Erwan on 19/04/2016.
 */
public class MyAsyncTask extends AsyncTask<String, Void, JSONObject> implements LocationListener {

    private final String ID = "id";
    private final String NOM = "nom";
    private final String ADRESSE = "adresse";
    private final String COMPLEMENTADRESSE = "complementAdresse";
    private final String CODEPOSTAL = "codePostal";
    private final String VILLE = "ville";
    private final String LATITUDE = "latitude";
    private final String LONGITUDE = "longitude";
    private final String HANDICAPE = "accesHandicape";
    private final String PARKING = "parking";
    private final String TERRASSE = "terrasse";
    private final String ESPACEENFANT = "espaceEnfant";
    private final String PHOTOURL = "photo";
    private final String INFOSSUP = "infosSupplementaires";
    private final String TELEPHONE = "Telephone";

    JSONObject jsonData;
    ArrayList<Restaurant> listRestaurants = new ArrayList<>();

    MainActivity activity;

    private LocationManager locationManager;

    Location userLocation;
    GoogleApiClient gApiClient;

    public MyAsyncTask(MainActivity activity, LocationManager locationManager) {
        this.activity = activity;
        this.locationManager = locationManager;
    }

    @Override
    protected void onPreExecute() {
        userLocation = activity.getmLastLocation();

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        }else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
//            userLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        }else{
//            userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        }

        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (locationGPS != null){
            Log.d("Location Manager GPS", "Lat : " + String.valueOf(locationGPS.getLatitude()));
            Log.d("Location Manager GPS", "Long : " + String.valueOf(locationGPS.getLongitude()));
        }
        if (locationNet != null){
            Log.d("Location Manager Net", "Lat : " + String.valueOf(locationNet.getLatitude()));
            Log.d("Location ManagerNet", "Long : " + String.valueOf(locationNet.getLongitude()));
        }
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        StringBuilder stringData = new StringBuilder();

        try{
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringData.append(line);
            }
            jsonData = new JSONObject(stringData.toString());
        }catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  jsonData;
    }

    @Override
    protected void onPostExecute(JSONObject s) {
        try {
            JSONArray restaurants = s.getJSONArray("restaurants");
            for (int i =0; i < restaurants.length(); i++){
                JSONObject c =restaurants.getJSONObject(i);
                Restaurant r = new Restaurant();
                r.setId(c.getInt(ID));
                r.setNom(c.getString(NOM));
                r.setAdresse(c.getString(ADRESSE));
                r.setComplementAdresse(c.getString(COMPLEMENTADRESSE));
                r.setCodePostal(c.getString(CODEPOSTAL));
                r.setVille(c.getString(VILLE));
                r.setLatitude(c.getDouble(LATITUDE));
                r.setLongitude(c.getDouble(LONGITUDE));
                r.setDistanceToUser(userLocation);
                if (c.getString(HANDICAPE).equals("1"))
                    r.setHandicape(true);
                if (c.getString(TERRASSE).equals("1"))
                    r.setTerrasse(true);
                if (c.getString(PARKING).equals("1"))
                    r.setParking(true);
                if (c.getString(ESPACEENFANT).equals("1"))
                    r.setEspaceEnfant(true);
                r.setPhotoUrl(c.getString(PHOTOURL));
                r.setInfosSup(c.getString(INFOSSUP));
                r.setTelephone(c.getString(TELEPHONE));

                listRestaurants.add(r);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(listRestaurants, new Comparator<Restaurant>() {
            public int compare(Restaurant res1, Restaurant res2) {
                return Float.valueOf(res1.getDistanceToUser()).compareTo(res2.getDistanceToUser());
            }
        });


        Intent i = new Intent(this.activity, MyListActivity.class);
        i.putExtra("LIST", listRestaurants);
        activity.startActivity(i);
        activity.finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.userLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
