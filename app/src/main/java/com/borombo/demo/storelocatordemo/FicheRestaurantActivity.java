package com.borombo.demo.storelocatordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by Erwan on 19/04/2016.
 */
public class FicheRestaurantActivity extends AppCompatActivity {

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

}
