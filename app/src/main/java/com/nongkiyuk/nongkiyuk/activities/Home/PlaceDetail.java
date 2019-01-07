package com.nongkiyuk.nongkiyuk.activities.Home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.nongkiyuk.nongkiyuk.R;

public class PlaceDetail extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        /*ViewPager mViewPager = (ViewPager) findViewById(R.id.picture);
        ImageSlider adapterView = new ImageSlider(this);
        mViewPager.setAdapter(adapterView);*/

        Intent intent = getIntent();

        int i = intent.getExtras().getInt("id");
        HomeFragment.MyAdapter myAdapter = new HomeFragment.MyAdapter(this);

        ImageView picture = (ImageView) findViewById(R.id.coverImg);
        TextView name = (TextView) findViewById(R.id.name);
        TextView desc = (TextView) findViewById(R.id.desc);
        picture.setImageResource(myAdapter.items.get(i).drawableId);
        name.setText(HomeFragment.MyAdapter.items.get(i).name);
        desc.setText(HomeFragment.MyAdapter.items.get(i).desc);
    }
}
