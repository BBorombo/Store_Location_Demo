package com.borombo.demo.storelocatordemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

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

    Activity activity;

    private LocationManager locationManager;

    Location userLocation;

    public MyAsyncTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            userLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }else {
            userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        Log.d("UserLocation : ", userLocation.toString());

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
