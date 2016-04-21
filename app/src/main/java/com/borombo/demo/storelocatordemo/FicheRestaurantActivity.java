package com.borombo.demo.storelocatordemo;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by Erwan on 19/04/2016.
 */
public class FicheRestaurantActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

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
}
