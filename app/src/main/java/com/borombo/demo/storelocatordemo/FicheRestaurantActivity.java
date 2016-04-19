package com.borombo.demo.storelocatordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Erwan on 19/04/2016.
 */
public class FicheRestaurantActivity extends AppCompatActivity {

    private final String EXTRA_NAME = "Restaurant";

    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_restaurant);

        restaurant = (Restaurant) getIntent().getSerializableExtra(EXTRA_NAME);

        /*
            Récupération des vues et ajout des informations spécifique au restaurant
         */

    }

}
