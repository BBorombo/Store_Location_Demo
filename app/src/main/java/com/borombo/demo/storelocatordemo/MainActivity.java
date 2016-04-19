package com.borombo.demo.storelocatordemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String URL = "http://www.leon-de-bruxelles.fr/webservice/restaurant-service.php";
    ArrayList<Restaurant> listRestaurants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyAsyncTask task = new MyAsyncTask(this);
        task.execute(URL);
    }


}
