package com.nongkiyuk.nongkiyuk.activities.Favorite;


import android.app.ProgressDialog;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nongkiyuk.nongkiyuk.R;
import com.nongkiyuk.nongkiyuk.activities.Favorite.Adapters.FavoriteAdapter;
import com.nongkiyuk.nongkiyuk.activities.Favorite.Models.Place;
import com.nongkiyuk.nongkiyuk.activities.Login.LoginActivity;
import com.nongkiyuk.nongkiyuk.network.ApiInterface;
import com.nongkiyuk.nongkiyuk.utils.SQLiteHandler;
import com.nongkiyuk.nongkiyuk.utils.SharedPrefManager;
import com.nongkiyuk.nongkiyuk.utils.UtilsApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    ApiInterface mApiInterface;
    private SQLiteHandler db;
    ArrayList<Place> places = new ArrayList<Place>();
    private static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static RecyclerView mRecyclerView;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.favorite_rv);
        mLayoutManager = new GridLayoutManager(getContext(), 2);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new FavoriteAdapter(getContext(), places);
        mRecyclerView.setAdapter(mAdapter);

        mApiInterface = UtilsApi.getApiInterface();
        db = new SQLiteHandler(getContext());

        HashMap<String, String> user = db.getUserDetails();
        String access_token = user.get("access_token");
        String token_type = user.get("token_type");
        String token = token_type + " " + access_token;

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching...");
        progressDialog.show();

        mApiInterface.getFavoritePlaces(token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if(response.isSuccessful() && response.code() == 200){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());

                        String meta_total = jsonRESULTS.getJSONObject("meta").getString("total");
                        Log.d("Meta Total", meta_total);
                        if(meta_total.equals("0")){
                            String msg = jsonRESULTS.getJSONObject("data").getString("msg");
                            Log.d("TOAST", msg);
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                        }else{
                            places.clear();
                            JSONArray data = jsonRESULTS.getJSONArray("data");
                            for(int i = 0; i < data.length(); i++){
                                JSONObject place = data.getJSONObject(i);
                                JSONArray images = place.getJSONArray("images");
                                for(int j = 0; j < images.length(); j++) {
                                    JSONObject image = images.getJSONObject(j);
                                    Log.d("IMAGE " + place.getString("name"), image.getString("url"));
                                }
                                JSONObject cover = place.getJSONObject("cover");
                                places.add(new Place(place.getString("id"), place.getString("name"),
                                        place.getString("description"), place.getString("latitude"), place.getString("longitude"), cover.getString("url")));
                            }
                            mAdapter = new FavoriteAdapter(getContext(), places);
                            mRecyclerView.swapAdapter(mAdapter, true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Something went wrongs", Toast.LENGTH_LONG).show();
            }
        });




        return view;
    }

}
