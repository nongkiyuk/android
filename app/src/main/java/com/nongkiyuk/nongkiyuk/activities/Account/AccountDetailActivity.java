package com.nongkiyuk.nongkiyuk.activities.Account;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nongkiyuk.nongkiyuk.R;
import com.nongkiyuk.nongkiyuk.activities.SignUp.SignupActivity;
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

public class AccountDetailActivity extends AppCompatActivity {


    private static final String TAG = AccountDetailActivity.class.getSimpleName();
    private static FragmentManager fragmentManager;

    SharedPrefManager sharedPrefManager;
    Context mContext;
    ApiInterface mApiInterface;
    private SQLiteHandler db;

    @BindView(R.id.toolbarId) Toolbar toolbar;
    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_username) EditText _usernameText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_save) Button _saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        ButterKnife.bind(this);
        mContext = this;
        mApiInterface = UtilsApi.getApiInterface(); // init UtilsApi
        sharedPrefManager = new SharedPrefManager(this);
        fragmentManager = getSupportFragmentManager();

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Fetching user
        HashMap<String, String> user = db.getUserDetails();
        final String access_token = user.get("access_token");
        final String token_type = user.get("token_type");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");

        setFormProfile(token_type, access_token);

        _saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile(token_type, access_token);
            }
        });

    }

    private void setFormProfile(String token_type, String access_token) {
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

                                // passing to view
                                _emailText.setText(email);
                                _usernameText.setText(username);
                                _nameText.setText(name);

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void saveProfile(String token_type, String access_token) {
        Log.d(TAG, "Save Profile");

        if (!validate()) {
            return;
        }

        _saveButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(AccountDetailActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Simpan Profile...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String username = _usernameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String token = token_type + " " + access_token;

        mApiInterface.saveProfileRequest(token, name, email, username, password)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        progressDialog.dismiss();
                        if (response.isSuccessful()) {
                            Log.d(TAG, "CODE : " + response.code());
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                Log.d(TAG, "MESSAGE : " + String.valueOf(jsonObject));
                                _saveButton.setEnabled(true);
                                fragmentManager.beginTransaction().replace(R.id.fl_container, new AccountFragment()).commit();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d(TAG, "CODE : " + response.code());
                            _saveButton.setEnabled(true);
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getJSONObject("data").getString("msg");
                                Log.i(TAG, "ERROR: " + msg);
                                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        _saveButton.setEnabled(true);
                        Log.e("debug", "onFailure: ERROR > " + t.getMessage());
                        Toast.makeText(mContext, "Koneksi Internet Bermasalah", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String username = _usernameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (username.isEmpty()) {
            _usernameText.setError("Enter Valid Username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError(null);
        } else {
            if (password.length() < 6) {
                _passwordText.setError("Minimal 6 karakter");
                valid = false;
            }
        }

        return valid;
    }
}
