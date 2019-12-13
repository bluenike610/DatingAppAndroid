package com.datingapp.android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.datingapp.android.Accounts.Enable_location_F;
import com.datingapp.android.Accounts.LoginActivity;
import com.datingapp.android.CodeClasses.Variables;
import com.datingapp.android.Main_Menu.MainMenuActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class Splash_A extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_);

        sharedPreferences=getSharedPreferences(Variables.pref_name,MODE_PRIVATE);

            // here we check the user is already login or not
            new Handler().postDelayed(new Runnable() {
                public void run() {

                    if (sharedPreferences.getBoolean(Variables.islogin, false)) {
                        // if user is already login then we get the current location of user
                        if(getIntent().hasExtra("action_type")){
                            Intent intent= new Intent(Splash_A.this, MainMenuActivity.class);
                            String action_type=getIntent().getExtras().getString("action_type");
                            String receiverid=getIntent().getExtras().getString("senderid");
                            String title=getIntent().getExtras().getString("title");
                            String icon=getIntent().getExtras().getString("icon");

                            intent.putExtra("icon",icon);
                            intent.putExtra("action_type",action_type);
                            intent.putExtra("receiverid",receiverid);
                            intent.putExtra("title",title);


                            startActivity(intent);
                            finish();
                        }
                        else
                        GPSStatus();

                    } else {

                        // else we will move the user to login screen
                        startActivity(new Intent(Splash_A.this, LoginActivity.class));
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        finish();

                    }
                }
            }, 2000);



    }



    // get the Gps status to check that either a mobile gps is on or off
    public void GPSStatus(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!GpsStatus)
        {
            // if gps is not on then we will go to the setting screen to on the gps
            Toast.makeText(this, "On Location in High Accuracy", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),2);

        }else {

            // if on then get the location of the user and save the location into the local database

            GetCurrentlocation();
        }
    }


    // if the Gps is successfully on then we will on the again check the Gps status
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            GPSStatus();
        }
    }



    private FusedLocationProviderClient mFusedLocationClient;
    private void GetCurrentlocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           // here if user did not give the permission of location then we move user to enable location screen
            enable_location();
            return;
        }

        // else we will get the location and save it in local and move to main Screen
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
//                        if (location != null) {
//
//                            // if we successfully get the location of the user then we will save the locatio into
//                            //locally and go to the Main view
//                            SharedPreferences.Editor editor=sharedPreferences.edit();
//                            editor.putString(Variables.current_Lat,""+location.getLatitude());
//                            editor.putString(Variables.current_Lon,""+location.getLongitude());
//                            editor.commit();
//                            startActivity(new Intent(Splash_A.this, MainMenuActivity.class));
//                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//                            finish();
//                        }
//                        else  {
                            // else we will use the basic location

//                            if(sharedPreferences.getString(Variables.current_Lat,"").equals("") || sharedPreferences.getString(Variables.current_Lon,"").equals("") ){
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString(Variables.current_Lat,"33.738045");
                            editor.putString(Variables.current_Lon,"73.084488");
                            editor.commit();
//                            }
                            startActivity(new Intent(Splash_A.this, MainMenuActivity.class));
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                            finish();
//                        }
                    }
                });
    }



    // if user does not permitt the app to get the location then we will go to the enable location screen to enable the location permission
    private void enable_location() {
        Enable_location_F enable_location_f = new Enable_location_F();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left,R.anim.in_from_left,R.anim.out_to_right);
        getSupportFragmentManager().popBackStackImmediate();
//        transaction.replace(R.id.Login_A, enable_location_f).addToBackStack(null).commit();

    }


}
