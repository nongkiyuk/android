package com.nongkiyuk.nongkiyuk.activities.Account;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nongkiyuk.nongkiyuk.R;
import com.nongkiyuk.nongkiyuk.activities.Login.LoginActivity;
import com.nongkiyuk.nongkiyuk.network.ApiInterface;
import com.nongkiyuk.nongkiyuk.utils.SQLiteHandler;
import com.nongkiyuk.nongkiyuk.utils.SharedPrefManager;
import com.nongkiyuk.nongkiyuk.utils.UtilsApi;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private static final String TAG = "AccountFragment";

    @BindView(R.id.tv_name)
    TextView _nameView;
    @BindView(R.id.tv_username)
    TextView _usernameView;
    @BindView(R.id.tv_email)
    TextView _emailView;
    @BindView(R.id.iv_profile)
    ImageView _pictureImage;
    @BindView(R.id.btn_edit)
    Button _editButton;
    @BindView(R.id.btn_logout)
    Button _logoutButton;

    Context mContext;
    ApiInterface mApiInterface;
    SharedPrefManager sharedPrefManager;
    private SQLiteHandler db;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        ButterKnife.bind(this, rootView);
        mContext = rootView.getContext();
        mApiInterface = UtilsApi.getApiInterface();
        sharedPrefManager = new SharedPrefManager(rootView.getContext());
        // SQLite database handler
        db = new SQLiteHandler(rootView.getContext());

        // Fetching user
        HashMap<String, String> user = db.getUserDetails();
        String access_token = user.get("access_token");
        String token_type = user.get("token_type");

        // set user profile
        setUserProfile(token_type, access_token);

        _logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Light_Dialog);
                builder.setMessage("Apakah kamu yakin ingin keluar ?")
                        .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "OK DITEKAN");

                                sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_LOGGED_IN, false);
                                // delete user
                                db.deleteUsers();

                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                                ((Activity) getActivity()).overridePendingTransition(0,0);
                            }
                        })
                        .setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "TIDAK DITEKAN");
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        _editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.fl_container, new AccountDetailFragment())
//                        .commit();
                Intent intent = new Intent(getActivity(), AccountDetailActivity.class);
                startActivity(intent);
                ((Activity) getActivity()).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        return rootView;
    }

    private void setUserProfile(String token_type, String access_token) {
        mApiInterface.profileRequest(token_type + " " + access_token)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d(TAG, "RESPONSE CODE " + response.code());
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                String email = jsonObject.getJSONObject("data").getString("email");
                                String username = jsonObject.getJSONObject("data").getString("username");
                                String name = jsonObject.getJSONObject("data").getString("name");
                                String picture = jsonObject.getJSONObject("data").getString("picture");

                                // passing to view
                                _emailView.setText(email);
                                _nameView.setText(name);
                                _usernameView.setText(username);
                                Picasso.get()
                                        .load(picture)
                                        .resize(100, 100)
                                        .centerCrop()
                                        .placeholder(R.drawable.profile_pic)
                                        .error(R.color.accent)
                                        .into(_pictureImage);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug", "onFailure: ERROR > " + t.getMessage());
                    }
                });
    }

}
