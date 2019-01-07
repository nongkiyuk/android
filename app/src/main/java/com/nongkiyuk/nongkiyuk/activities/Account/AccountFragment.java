package com.nongkiyuk.nongkiyuk.activities.Account;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Toast;

import com.nongkiyuk.nongkiyuk.R;
import com.nongkiyuk.nongkiyuk.activities.Login.LoginActivity;
import com.nongkiyuk.nongkiyuk.activities.Main.MainActivity;
import com.nongkiyuk.nongkiyuk.network.ApiInterface;
import com.nongkiyuk.nongkiyuk.utils.SQLiteHandler;
import com.nongkiyuk.nongkiyuk.utils.SharedPrefManager;
import com.nongkiyuk.nongkiyuk.utils.UtilsApi;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private static final String TAG = "AccountFragment";
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private static final int REQUEST_STORAGE = 112;
    private String to_token = null;

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
    @BindView(R.id.btn_language)
    Button _languageButton;

    Context mContext;
    ApiInterface mApiInterface;
    SharedPrefManager sharedPrefManager;
    private SQLiteHandler db;
    private Uri uri;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        ButterKnife.bind(this, rootView);
        mContext = rootView.getContext();
        mApiInterface = UtilsApi.getApiInterface();
        sharedPrefManager = new SharedPrefManager(rootView.getContext());
        // SQLite database handler
        db = new SQLiteHandler(rootView.getContext());

        // Fetching user
        HashMap<String, String> user = db.getUserDetails();
        final String access_token = user.get("access_token");
        final String token_type = user.get("token_type");
        final String token = token_type + " " + access_token;
        to_token = token;

        // set user profile
        setUserProfile(token_type, access_token);

        _logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Light_Dialog);
                builder.setMessage(R.string.content_area_logout)
                        .setPositiveButton(R.string.label_yes, new DialogInterface.OnClickListener() {
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
                        .setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
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
                Intent intent = new Intent(getActivity(), AccountDetailActivity.class);
                startActivity(intent);
                ((Activity) getActivity()).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _pictureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE};
                    if (!hasPermissions(mContext, PERMISSIONS)) {
                        ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST_STORAGE);
                    } else {
                        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        openGalleryIntent.setType("image/*");
                        startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
                    }
                } else {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, REQUEST_GALLERY_CODE);
                }
            }
        });

        _languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, REQUEST_GALLERY_CODE);
                } else {
                    Toast.makeText(mContext, "The app was not allowed to write to your storage.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Token " + to_token);

            uri = data.getData();
            String filePath = getRealPathFromURIPath(uri, getActivity());
            File file = new File(filePath);

            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("picture", file.getName(), mFile);

            mApiInterface.uploadPictureProfileRequest(to_token, fileToUpload)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d(TAG, "CODE : " + response.code());
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("debug", "onFailure: ERROR > " + t.getMessage());
                            Toast.makeText(mContext, "Koneksi Internet Bermasalah", Toast.LENGTH_SHORT).show();
                        }
                    });

            _pictureImage.setImageURI(uri);
        }
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
}
