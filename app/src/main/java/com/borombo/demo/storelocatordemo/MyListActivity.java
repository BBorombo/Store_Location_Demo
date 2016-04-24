package com.borombo.demo.storelocatordemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * Created by Erwan on 19/04/2016.
 */
public class MyListActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public GoogleApiClient mGoogleApiClient;

    private Location userLocation;

    ArrayList<Restaurant> list;

    private final String EXTRA_NAME = "Restaurant";
    private boolean fromStorage;

    private SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        setGoogleApiClient();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        list = (ArrayList<Restaurant>) intent.getSerializableExtra("LIST");

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MyRecyclerViewAdapter(list);
        recyclerView.setAdapter(adapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fromStorage = getIntent().getBooleanExtra("GET_DATA", false);
        if (fromStorage){
            Snackbar snackBar = Snackbar.make(drawer, "Les données issue de votre appareil ne sont peut être pas à jour.", Snackbar.LENGTH_LONG)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
            snackBar.getView().setBackgroundColor(getColor(R.color.colorPrimary));
            snackBar.show();
        }

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
        ((MyRecyclerViewAdapter) adapter).setOnItemClickListener(new MyRecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(MyListActivity.this, FicheRestaurantActivity.class);
                intent.putExtra(EXTRA_NAME, list.get(position));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
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
        int id = item.getItemId();

        if (id == R.id.site_web) {
            String url = getString(R.string.website);
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(url) );
            startActivity(intent);
        } else if (id == R.id.mentions_legales) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Mentions Légales")
                    .setMessage(R.string.mentions_legales)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (id == R.id.partager) {
            startActivity(Intent.createChooser(getWebShareItent(), "Partager via"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRefresh() {
        if (ActivityCompat.checkSelfPermission(MyListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MyListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (userLocation != null){
            updateDistance();
        }
        adapter.notifyDataSetChanged();
        refresh.setRefreshing(false);
    }

    public void setGoogleApiClient(){
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public Intent getWebShareItent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.website));
        shareIntent.setType("text/plain");
        return shareIntent;
    }

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
