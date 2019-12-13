package com.datingapp.android.Accounts;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.datingapp.android.CodeClasses.Variables;
import com.datingapp.android.R;
import com.datingapp.android.common.Common;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static com.datingapp.android.common.Common.cm;

public class LocationActivity extends AppCompatActivity {

    MaterialDialog process_dialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Common.currentActivity = this;

        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
        RelativeLayout backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RelativeLayout nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(cm.currentActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(cm.currentActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // here if user did not give the permission of location then we move user to enable location screen
                    getLocationPermission();
                    return;
                }else {
                    Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.currentActivity = this;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getLocationPermission() {

        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                123);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case  123:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GetCurrentlocation();
                } else {
                }
                break;
        }

    }

    private FusedLocationProviderClient mFusedLocationClient;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void GetCurrentlocation() {
        //Set processing indicators
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title(R.string.location_title)
                .content(R.string.sending_code_msg)
                .progress(true, 0);

        process_dialog = builder.build();
        process_dialog.show();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // first check the location permission if not give then we will ask for permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            process_dialog.dismiss();
            getLocationPermission();
            return;
        }

        // if the user gives the permission then this method will call and get the current location of user
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(cm.currentActivity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        process_dialog.dismiss();
                        if (location != null) {

                            // save the location inlocal and move to main activity
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString(Variables.current_Lat,""+location.getLatitude());
                            editor.putString(Variables.current_Lon,""+location.getLongitude());
                            editor.commit();

                        }else  {

                            if(sharedPreferences.getString(Variables.current_Lat,"").equals("") || sharedPreferences.getString(Variables.current_Lon,"").equals("") ){
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString(Variables.current_Lat,"33.738045");
                                editor.putString(Variables.current_Lon,"73.084488");
                                editor.commit();
                            }

                        }
                        Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                        startActivity(intent);

                    }
                });
    }
}
