package com.borombo.demo.storelocatordemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    private final String ESPACEENFANT = "espaceEnfant";
    private final String PHOTOURL = "photo";
    private final String INFOSSUP = "infosSupplementaires";
    private final String TELEPHONE = "Telephone";

    private final String FILE_NAME = "listData";

    private boolean fromStorage = false;

    private  JSONObject jsonData;
    private  ArrayList<Restaurant> listRestaurants = new ArrayList<>();

    private  Activity activity;

    private Location userLocation;
    private GoogleApiClient gApiClient;

    public MyAsyncTask(Activity activity, GoogleApiClient gApiClient) {
        this.activity = activity;
        this.gApiClient = gApiClient;
    }

    @Override
    protected void onPreExecute() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // On récupère la position de l'utilisateur
        userLocation = LocationServices.FusedLocationApi.getLastLocation(gApiClient);
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        StringBuilder stringData = new StringBuilder();
        InputStream inputStream = null;
        // Si il y a une connexion internet
        if (hasActiveInternetConnection()){
            try {
                // On télécharge le fichier
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                inputStream = connection.getInputStream();
                // On l'écrit/remplace dans la mémoire
                writeFile(inputStream);
                // Pour une raison inconnue, après l'écriture, il m'est impossible de lire l'inputStream
                // alors qu'hors connexion, cela fonctionne, je récupère donc le fichier que je viens d'écrire
                inputStream = getFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Si il n'y a pas de connexion internet
        }else {
            // On récupère le fichier depuis la mémoire
            fromStorage = true;
            inputStream = getFile();
        }
        try{
            // On traite le flux afin d'obtenir une chaine de caractères contenant les données
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringData.append(line);
            }
            // On transforme ces données en objet JSON qui est traité par la suite
            jsonData = new JSONObject(stringData.toString());
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonData;

    }

    /**
     * Fonction permettant de récupérer le fichier dans la mémoire
     * @return Le flux du fichier récupéré
     */
    public InputStream getFile(){
        // On récupère le fichier par son nom
        File file = new File(activity.getExternalFilesDir(null), FILE_NAME);
        InputStream input = null;
        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return input;
    }

    /**
     * Fonction permettant d'écrire le fichier dans la mémoire
     * @param input Le flux du fichier JSON
     */
    public void writeFile(InputStream input){
        // On créer le fichier
        File file = new File(activity.getExternalFilesDir(null), FILE_NAME);
        try {
            OutputStream output = new FileOutputStream(file);
            byte data[] = new byte[4096];
            int count;
            // On recopie le flux dans le fichier
            while ((count = input.read(data)) != -1) {
                // Si l'écriture est interompue, on ferme le flux
                if (isCancelled()) {
                    input.close();
                    return;
                }
                output.write(data, 0, count);
            }
        }catch (java.io.IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(JSONObject s) {
        try {
            // On récupère la liste de restaurant
            JSONArray restaurants = s.getJSONArray("restaurants");
            // Pour chaque restaurant, on créer un objet correspondant, et on récupère les infos
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
                // On ajoute le restaurant à laliste
                listRestaurants.add(r);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // On tri la liste par ordre croissant en fonction de la distance de ceux ci avec l'utilisateur
        Collections.sort(listRestaurants, new Comparator<Restaurant>() {
            public int compare(Restaurant res1, Restaurant res2) {
                return Float.valueOf(res1.getDistanceToUser()).compareTo(res2.getDistanceToUser());
            }
        });
        // On passe à l'activité suivante en passant la liste, et le boolean permettant de savoir d'ou vient la liste
        Intent i = new Intent(this.activity, MyListActivity.class);
        i.putExtra(activity.getString(R.string.list_tag), listRestaurants);
        i.putExtra(activity.getString(R.string.getData_tag),fromStorage);
        activity.startActivity(i);
        activity.finish();
    }

    /**
     * Fonction permettant de savoir si l'appareil est connecté à un réseau
     * @return
     */
    public boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null);
    }

    /**
     * Fonction permettant de savoir si l'appareil est connecté à Internet
     * @return
     */
    public boolean hasActiveInternetConnection() {
        boolean res = false;
        // Si l'appareil est connecté à un réseau
        if (isNetworkAvailable()) {
            // On teste avec un serveur de Google afin d'être sur que le réseau est connecté à internet,
            // et n'est pas juste local
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                res =  (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);
            } catch (IOException e) {
                Log.e("Check Connection", "Error checking internet connection", e);
            }
        }
        return res;
    }
}
