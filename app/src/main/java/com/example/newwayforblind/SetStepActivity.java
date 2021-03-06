package com.example.newwayforblind;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SetStepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_step);

        if(savedInstanceState == null){
            MapFragment mapFragment = new MapFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mapFragment, mapFragment, "mapFragment")
                    .commit();
        }
    }
}
