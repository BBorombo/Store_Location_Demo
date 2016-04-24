package com.borombo.demo.storelocatordemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * Created by Erwan on 19/04/2016.
 */
public class MyListActivity extends LateralMenuActivity implements GoogleApiClient.ConnectionCallbacks, SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private GoogleApiClient mGoogleApiClient;

    private Location userLocation;

    private ArrayList<Restaurant> list;
    // Variable qui permet de Savoir si la liste vient de la mémoire de l'appareil
    private boolean fromStorage;

    private SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Initialisation du GoogleAPIClient
        setGoogleApiClient();

        // Initialisation de la Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Récupération de la liste de restaurant
        Intent intent = getIntent();
        list = (ArrayList<Restaurant>) intent.getSerializableExtra(getString(R.string.list_tag));

        // Initialisation de la RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyRecyclerViewAdapter(list);
        recyclerView.setAdapter(adapter);

        // Initialisation du Menu latéral
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // On regarde si la liste provient du Fichier JSON sur internet, ou celui stocké dans la mémoire de l'appareil
        fromStorage = getIntent().getBooleanExtra(getString(R.string.getData_tag), false);
        // Si il provient de la mémoire, on informe l'utilisateur
        if (fromStorage){
            Snackbar snackBar = Snackbar.make(drawer, getString(R.string.snackbar), Snackbar.LENGTH_LONG)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
            snackBar.getView().setBackgroundColor(getColor(R.color.colorPrimary));
            snackBar.show();
        }
        // Initialisation du SwipteRefresh
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setOnRefreshListener(this);
        refresh.setColorSchemeColors(getColor(R.color.greenLeon));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Lorsqu'on clique sur un élément de la liste, on récupère le restaurant correspondant
        // et on l'affiche dans l'activité appropriée
        ((MyRecyclerViewAdapter) adapter).setOnItemClickListener(new MyRecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(MyListActivity.this, FicheRestaurantActivity.class);
                intent.putExtra(getString(R.string.restaurant_tag), list.get(position));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
    }



    @Override
    public void onRefresh() {
        if (ActivityCompat.checkSelfPermission(MyListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MyListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // On récupère la position de l'utilisateur
        userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // Si celle ci n'est pas nulle, on mets à jour la distance avec les restaurant
        if (userLocation != null){
            updateDistance();
        }
        adapter.notifyDataSetChanged();
        refresh.setRefreshing(false);
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
     * Fonction qui permet de récupérer l'intent pour partager l'adresse web
     * @return
     */
    public Intent getWebShareItent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.website));
        shareIntent.setType("text/plain");
        return shareIntent;
    }

    /**
     * Fonction qui permet de mettre à jour la distance des restaurant
     */
    public void updateDistance(){
        for (Restaurant r: list) {
            r.setDistanceToUser(userLocation);
        }
    }

    @Override
    public void onConnected(Bundle bundle) { }
    @Override
    public void onConnectionSuspended(int i) { }
}
