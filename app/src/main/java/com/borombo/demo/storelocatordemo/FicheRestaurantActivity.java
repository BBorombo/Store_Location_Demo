package com.borombo.demo.storelocatordemo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Erwan on 19/04/2016.
 */
public class FicheRestaurantActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback{

    private final String EXTRA_NAME = "Restaurant";

    private Restaurant restaurant;

    private TextView name;
    private TextView adresse;
    private TextView ville;
    private TextView complementAdresse;

    private ImageView image;

    private TextView infosSup;
    private TextView telephone;

    private TextView distance;

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_restaurant);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        restaurant = (Restaurant) getIntent().getSerializableExtra(EXTRA_NAME);

        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();

        name = (TextView) findViewById(R.id.name);
        adresse = (TextView) findViewById(R.id.adresse);
        ville = (TextView) findViewById(R.id.ville);
        /*complementAdresse = (TextView) findViewById(R.id.name);
        infosSup = (TextView) findViewById(R.id.name);*/
        telephone = (TextView) findViewById(R.id.telephone);
        image = (ImageView) findViewById(R.id.image);
        distance = (TextView) findViewById(R.id.distance);

        name.append(restaurant.getNom());
        adresse.setText(restaurant.getAdresse());
        ville.setText(restaurant.getCodePostal() + " " + restaurant.getVille());
        telephone.append(restaurant.getTelephone());

        distance.append(restaurant.getDistanceToUser() +" "+ restaurant.getDistanceUnit());

        Glide.with(this).load(restaurant.getPhotoUrl()).into(image);

        /*
            Récupération des vues et ajout des informations spécifique au restaurant
         */

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.restaurant_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.partager:
                return true;
            case R.id.call:
                callRestaurant();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void callRestaurant(){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + restaurant.getTelephone()));
        try {
            startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
            Toast.makeText(getApplicationContext(),"yourActivity is not founded", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Marker"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
    }
}
