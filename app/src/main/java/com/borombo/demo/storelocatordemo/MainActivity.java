package com.borombo.demo.storelocatordemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks{

    private final String URL = "http://www.leon-de-bruxelles.fr/webservice/restaurant-service.php";
    public final int ACCESS_LOCATION_PERMISSION = 233;
    public GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    boolean backFromSetting = false;

    int gpsEnabled; // Vaut 0 si le GPS n'est pas activé

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

    public void setGoogleApiClient(){
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void startProcess() {
        Log.d("API Connected", String.valueOf(mGoogleApiClient.isConnected()));
        MyAsyncTask task = new MyAsyncTask(this, mGoogleApiClient);
        task.execute(URL);
    }

    public void enableGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Activer le GPS")
                .setMessage(R.string.textGPSDialog)
                .setPositiveButton("Activer GPS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        backFromSetting = true;
                        dialog.dismiss();
                        startActivity(onGPS);
                    }
                })
                .setNegativeButton("Nom merci", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                // Si le GPS n'est pas activé
                if (gpsEnabled == 0){
                    enableGPS();
                }else {
                    startProcess();
                }
            } else {
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_PERMISSION);
        }else{
            try {
                gpsEnabled = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) { e.printStackTrace(); }

            if (gpsEnabled == 0){
                enableGPS();
            }else {
                startProcess();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

}
