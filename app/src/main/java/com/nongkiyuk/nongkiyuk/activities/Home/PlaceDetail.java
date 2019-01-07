package com.nongkiyuk.nongkiyuk.activities.Home;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.nongkiyuk.nongkiyuk.R;
import com.nongkiyuk.nongkiyuk.activities.Favorite.Models.Place;
import com.nongkiyuk.nongkiyuk.activities.Home.Adapters.HomeAdapter;

import java.io.Serializable;

public class PlaceDetail extends AppCompatActivity {

    TextView txtName;
    TextView txtDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        Intent intent = getIntent();
        Place place = (Place) intent.getSerializableExtra("place");

        txtName = findViewById(R.id.name);
        txtDescription = findViewById(R.id.desc);

        txtName.setText(place.getName());
        txtDescription.setText("Tayo");

    }
}
