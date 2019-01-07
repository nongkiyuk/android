package com.nongkiyuk.nongkiyuk.activities.Account;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nongkiyuk.nongkiyuk.R;
import com.nongkiyuk.nongkiyuk.activities.Login.LoginActivity;
import com.nongkiyuk.nongkiyuk.utils.SQLiteHandler;
import com.nongkiyuk.nongkiyuk.utils.SharedPrefManager;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private static final String TAG = "AccountFragment";

    @BindView(R.id.btn_logout)
    Button _logoutButton;

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

        ButterKnife.bind(this, rootView);
        sharedPrefManager = new SharedPrefManager(rootView.getContext());
        // SQLite database handler
        db = new SQLiteHandler(rootView.getContext());

        // Fetching user
        HashMap<String, String> user = db.getUserDetails();
        String access_token = user.get("access_token");

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

        return rootView;
    }

}
