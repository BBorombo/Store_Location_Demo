package com.borombo.demo.storelocatordemo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

/**
 * Created by Erwan on 19/04/2016.
 */
public class FicheRestaurantActivity extends LateralMenuActivity implements OnMapReadyCallback{

    private Restaurant restaurant;

    private TextView name;
    private TextView adresse;
    private TextView ville;

    private ImageView image;

    private WebView infosSup;
    private TextView telephone;

    private ImageView parking;
    private ImageView handicape;
    private ImageView terrasse;
    private ImageView enfants;

    ShareActionProvider shareAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        // On récupère le restaurant à afficher
        restaurant = (Restaurant) getIntent().getSerializableExtra(getString(R.string.restaurant_tag));

        // Initialisation de la ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(restaurant.getNom());
        setSupportActionBar(toolbar);

        // Initialisation du menu latéral
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initialisation du fragment qui contient la Map
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Récupération des vues qui vont contenir les infromation
        name = (TextView) findViewById(R.id.name);
        adresse = (TextView) findViewById(R.id.adresse);
        ville = (TextView) findViewById(R.id.ville);
        telephone = (TextView) findViewById(R.id.telephone);
        infosSup = (WebView) findViewById(R.id.infos);

        image = (ImageView) findViewById(R.id.image);

        parking = (ImageView) findViewById(R.id.parking);
        handicape = (ImageView) findViewById(R.id.handicape);
        terrasse = (ImageView) findViewById(R.id.terrasse);
        enfants = (ImageView) findViewById(R.id.enfants);

        // Ajout des informations dans les champs
        name.append(restaurant.getNom());
        adresse.setText(restaurant.getAdresse());
        ville.setText(restaurant.getCodePostal() + " " + restaurant.getVille());
        telephone.append(restaurant.getTelephone());
        // J'agrandi le texte de la webview afin qu'il soit uniforme avec le reste
        restaurant.setInfosSup(restaurant.getInfosSup().replace("11px", "18px"));
        infosSup.loadData(restaurant.getInfosSup(), "text/html", null);

        // Conditions permettant de sélectionner les icones à afficher
        if (!restaurant.isParking()){ parking.setVisibility(View.INVISIBLE); }
        if (!restaurant.isHandicape()){ handicape.setVisibility(View.INVISIBLE); }
        if (!restaurant.isTerrasse()){ terrasse.setVisibility(View.INVISIBLE); }
        if (!restaurant.isEspaceEnfant()){ enfants.setVisibility(View.INVISIBLE); }

        // Téléchargement et affichage de l'image du restaurant
        Picasso.with(this).load(restaurant.getPhotoUrl()).into(image);

        // Initialisation du bouton permettant de lancer l'itinéraire
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchItinerary();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.restaurant_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // Clique sur "Partager"
            case R.id.partager:
                shareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
                shareAction.setShareIntent(getInfoShareItent());
                return true;
            // CLique sur "Appeler"
            case R.id.call:
                callRestaurant();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Fonction qui permet de lancer l'itinéraire jusqu'au restaurant dans google Maps
     */
    public void launchItinerary(){
        // On remplace les ',' par des '.' pour que l'Uri envoyé soit reconnu
        String latitude = String.valueOf(restaurant.getLatitude()).replace(',','.');
        String longitude = String.valueOf(restaurant.getLongitude()).replace(',','.');
        Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%s, %s", latitude, longitude));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage(getString(R.string.googlePackage));
        startActivity(mapIntent);
    }

    /**
     * Fonction qui permet de récupérer l'intent pour partager les infos du restaurant
     * @return
     */
    public Intent getInfoShareItent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, String.format("%s %s", getString(R.string.leon), restaurant.getNom()));
        // On formate le texte à partager avec les informations
        String text = String.format("%s %s \n%s \n%s %s \n%s%s",
                getString(R.string.leon), restaurant.getNom(), restaurant.getAdresse(),
                restaurant.getCodePostal(), restaurant.getVille(), getString(R.string.tel), restaurant.getTelephone());
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.setType("text/plain");
        return shareIntent;
    }

    /**
     * Fonction qui permet d'appller le numéro du restaurant
     */
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
