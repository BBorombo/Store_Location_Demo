package com.borombo.demo.storelocatordemo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

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
public class MyAsyncTask extends AsyncTask<String, Void, JSONObject> {

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
    private final String ESPACEENFANT= "espaceEnfant";
    private final String PHOTOURL = "photo";
    private final String INFOSSUP = "infosSupplementaires";
    private final String TELEPHONE = "Telephone";

    JSONObject jsonData;
    ArrayList<Restaurant> listRestaurants = new ArrayList<>();

    Activity activity;

    public MyAsyncTask(Activity activity){
        this.activity = activity;
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
}
