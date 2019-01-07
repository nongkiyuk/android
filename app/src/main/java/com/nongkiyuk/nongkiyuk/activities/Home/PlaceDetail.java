package com.nongkiyuk.nongkiyuk.activities.Home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.nongkiyuk.nongkiyuk.R;
import com.nongkiyuk.nongkiyuk.activities.Account.AccountFragment;
import com.nongkiyuk.nongkiyuk.activities.Favorite.FavoriteFragment;
import com.nongkiyuk.nongkiyuk.activities.Favorite.Models.Place;
import com.nongkiyuk.nongkiyuk.activities.Home.Adapters.HomeAdapter;
import com.nongkiyuk.nongkiyuk.activities.Home.Adapters.ImageAdapter;
import com.nongkiyuk.nongkiyuk.network.ApiInterface;
import com.nongkiyuk.nongkiyuk.utils.SQLiteHandler;
import com.nongkiyuk.nongkiyuk.utils.SharedPrefManager;
import com.nongkiyuk.nongkiyuk.utils.UtilsApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceDetail extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private ApiInterface mApiInterface;
    private SQLiteHandler db;
    private BottomNavigationView bottomNavigationView;
    private TextView txtName;
    private TextView txtAddress;
    private TextView txtDescription;
    private LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    private Place place;
    private String token;
    private Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        mApiInterface = UtilsApi.getApiInterface();
        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();
        String access_token = user.get("access_token");
        String token_type = user.get("token_type");
        token = token_type + " " + access_token;

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        menu = bottomNavigationView.getMenu();

        Intent intent = getIntent();
        place = (Place) intent.getSerializableExtra("place");

        txtName = findViewById(R.id.name);
        txtAddress = findViewById(R.id.address);
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
        txtAddress.setText(place.getAddress());
        txtDescription.setText(place.getDescription());

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_map:
                navigateToMap();
                break;
            case R.id.navigation_share:
                shareToSocial();
                break;
            case R.id.navigation_fav:
                loveIt();
                break;
        }
        return true;
    }

    private void navigateToMap()
    {
        String url = "google.navigation:q=" + place.getLatitude() + "," + place.getLongitude();
        Uri gmmIntentUri = Uri.parse(url);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void shareToSocial()
    {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBodyText = "Hai Saya Menggunakan NongkiYuk Untuk Mencari Lokasi Untuk Nongkrong , Yuk Nongkrong Bersama saya di  " + place.getName() + ", " + place.getAddress();
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Yuk Pakai NongkiYuk");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
    }

    private void loveIt()
    {
        mApiInterface.sendFavotire(token, place.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        String msg = jsonRESULTS.getJSONObject("data").getString("msg");
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_LONG).show();
            }
        });
    }
}
