package com.borombo.demo.storelocatordemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks{

    private final String URL = "http://www.leon-de-bruxelles.fr/webservice/restaurant-service.php";
    private GoogleApiClient mGoogleApiClient;

    // Constante permetant de déterminer le code correpondant à la requete d'utilisation de la localisation
    private final int ACCESS_LOCATION_PERMISSION = 233;

    // Variable permettant de savoir si on vient d'activer le GPS
    private boolean backFromSetting = false;

    // Variable vallant 0 si le GPS n'est pas activé
    private int gpsEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN);
        setGoogleApiClient();
    }

    /**
     * Fonction qui initialise le GoogleAPIClient
     */
    public void setGoogleApiClient(){
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    /**
     * On passe à l'AsyncTask qui récupère et traite le fichier JSON
     */
    public void startProcess() {
        MyAsyncTask task = new MyAsyncTask(this, mGoogleApiClient);
        task.execute(URL);
    }

    /**
     * Fonction qui propose à l'utilisateur d'activer son GPS
     */
    public void enableGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Activer le GPS")
                .setMessage(R.string.textGPSDialog)
                .setPositiveButton("Activer GPS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // On envoie l'utilisateur dans les paramètres pour qu'il active le GPS
                        Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        backFromSetting = true;
                        dialog.dismiss();
                        startActivity(onGPS);
                    }
                })
                .setNegativeButton("Nom merci", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // On continue, sans le GPS
                        dialog.dismiss();
                        startProcess();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Si on vient des Setting ('activation du GPS) on reconnecte le client Google
        if (backFromSetting){
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_LOCATION_PERMISSION) {
            // Si la permission d'acces à la position est acceptée
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // On vérifie si le GPS est activé, si il ne l'ai pas on demande pour l'activer, sinon on passe à l'AsyncTask
                if (gpsEnabled == 0){
                    enableGPS();
                }else {
                    startProcess();
                }
            } else {
                // Ici, on traite normalement le cas ou la permission d'accès à la position n'est pas accepté
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Si la permission pour acceder à la position n'est pas accordée, alors on la demande
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_PERMISSION);
        }else{
            // Sinon, on vérifie si le GPS est activé
            try {
                gpsEnabled = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) { e.printStackTrace(); }
            // Si il ne l'est pas, on demande pour l'activer, sinon, on passe à l'AsyncTask
            if (gpsEnabled == 0){
                enableGPS();
            }else {
                startProcess();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) { }
}
