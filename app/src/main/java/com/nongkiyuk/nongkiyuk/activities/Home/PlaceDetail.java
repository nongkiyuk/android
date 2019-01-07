package com.nongkiyuk.nongkiyuk.activities.Home;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nongkiyuk.nongkiyuk.R;
import com.nongkiyuk.nongkiyuk.activities.Favorite.Models.Place;
import com.nongkiyuk.nongkiyuk.activities.Home.Adapters.HomeAdapter;
import com.nongkiyuk.nongkiyuk.activities.Home.Adapters.ImageAdapter;

import java.io.Serializable;

public class PlaceDetail extends AppCompatActivity {

    TextView txtName;
    TextView txtDescription;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        Intent intent = getIntent();
        Place place = (Place) intent.getSerializableExtra("place");

        txtName = findViewById(R.id.name);
        txtDescription = findViewById(R.id.desc);

        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);

        ViewPager viewPager = findViewById(R.id.view_pager);
        ImageAdapter adapter = new ImageAdapter(this, place.getImageUrls());
        viewPager.setAdapter(adapter);

        dotscount = place.getImageUrls().length;
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++){
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        txtName.setText(place.getName());
        txtDescription.setText("Tayo");

    }
}
