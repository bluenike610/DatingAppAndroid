package com.datingapp.android.Accounts;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Toast;

import com.datingapp.android.Main_Menu.MainMenuActivity;
import com.datingapp.android.R;
import com.datingapp.android.CodeClasses.Variables;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Enable_location_F extends Fragment {


    View view;
    Context context;
    Button enable_location_btn;
    SharedPreferences sharedPreferences;

    IOSDialog iosDialog;

    public Enable_location_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_enable_location, container, false);
        context=getContext();


        iosDialog = new IOSDialog.Builder(context)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        sharedPreferences= context.getSharedPreferences(Variables.pref_name, MODE_PRIVATE);;

        enable_location_btn=view.findViewById(R.id.enable_location_btn);
        enable_location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationPermission();

            }
        });


        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation anim= MoveAnimation.create(MoveAnimation.LEFT, enter, 300);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {

                    // after all the animation done we will call  this mothod for get the location of user
                    GPSStatus();

                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            return anim;

        } else {
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, 300);
        }
    }



    private void getLocationPermission() {

        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case  123:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   GetCurrentlocation();
                } else {
                    Toast.makeText(context, "Please Grant permission", Toast.LENGTH_SHORT).show();
                }
                break;


        }

    }

    private FusedLocationProviderClient mFusedLocationClient;


    public void GPSStatus(){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!GpsStatus) {
            Toast.makeText(context, "On Location in High Accuracy", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),2);
        }
        else {
            GetCurrentlocation();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            GPSStatus();
        }
    }

    // main method used for get the location of user
    private void GetCurrentlocation() {
        iosDialog.show();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        // first check the location permission if not give then we will ask for permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            iosDialog.cancel();
            getLocationPermission();
            return;
        }

        // if the user gives the permission then this method will call and get the current location of user
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        iosDialog.cancel();
                        if (location != null) {

                            // save the location inlocal and move to main activity
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString(Variables.current_Lat,""+location.getLatitude());
                            editor.putString(Variables.current_Lon,""+location.getLongitude());
                            editor.commit();

                            GoToNext_Activty();

                        }else  {

                            if(sharedPreferences.getString(Variables.current_Lat,"").equals("") || sharedPreferences.getString(Variables.current_Lon,"").equals("") ){
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString(Variables.current_Lat,"33.738045");
                                editor.putString(Variables.current_Lon,"73.084488");
                                editor.commit();
                            }

                            GoToNext_Activty();
                        }
                    }
                });
    }


    public void GoToNext_Activty(){
        startActivity(new Intent(getActivity(), MainMenuActivity.class));
        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        getActivity().finishAffinity();
    }



}

