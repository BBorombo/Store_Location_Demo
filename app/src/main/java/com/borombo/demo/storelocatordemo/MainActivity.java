package com.borombo.demo.storelocatordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final String URL = "http://www.leon-de-bruxelles.fr/webservice/restaurant-service.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyAsyncTask task = new MyAsyncTask(this);
        task.execute(URL);
    }


}
