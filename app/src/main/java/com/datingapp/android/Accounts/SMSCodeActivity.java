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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.datingapp.android.CodeClasses.Variables;
import com.datingapp.android.Main_Menu.MainMenuActivity;
import com.datingapp.android.R;
import com.datingapp.android.common.Common;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import static com.datingapp.android.common.Common.cm;

public class SMSCodeActivity extends AppCompatActivity implements TextWatcher {

    private RelativeLayout nextBtn;
    private RelativeLayout resendBtn;
    private ImageView backIcon1, backIcon2, backIcon3, backIcon4, backIcon5, backIcon6;
    private EditText codeTxt;
    MaterialDialog process_dialog;

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private String phoneNum;
    private FirebaseAuth fbAuth;
    public IOSDialog iosDialog;
    SharedPreferences sharedPreferences;

    private Boolean isClickable = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smscode);
        Common.currentActivity = this;

        fbAuth = FirebaseAuth.getInstance();
        sharedPreferences=getSharedPreferences(Variables.pref_name,MODE_PRIVATE);

        RelativeLayout backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        backIcon1 = findViewById(R.id.backIcon1);
        backIcon2 = findViewById(R.id.backIcon2);
        backIcon3 = findViewById(R.id.backIcon3);
        backIcon4 = findViewById(R.id.backIcon4);
        backIcon5 = findViewById(R.id.backIcon5);
        backIcon6 = findViewById(R.id.backIcon6);

        codeTxt = findViewById(R.id.codeTxt);
        codeTxt.setLetterSpacing(0.87f);
        codeTxt.addTextChangedListener(this);

        phoneNum = getIntent().getStringExtra("phone_num");
        phoneVerificationId = getIntent().getStringExtra("verification_id");
        resendToken = (PhoneAuthProvider.ForceResendingToken) getIntent().getSerializableExtra("resend_token");

        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClickable) {
                    verifyCode();
                }
            }
        });

        resendBtn = findViewById(R.id.noSMSLayout);
        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendCode();
            }
        });

        iosDialog = new IOSDialog.Builder(this)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        setUpVerificatonCallbacks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.currentActivity = this;
    }

    private void verifyCode() {

        String code = codeTxt.getText().toString();

        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);

        //Set processing indicators
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title(R.string.verifying_code_title)
                .content(R.string.sending_code_msg)
                .progress(true, 0);

        process_dialog = builder.build();
        process_dialog.show();
    }

    private void resendCode() {

        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNum,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                resendToken);
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title(R.string.sending_code_title)
                .content(R.string.sending_code_msg)
                .progress(true, 0);

        process_dialog = builder.build();
        process_dialog.show();
    }


    private void setUpVerificatonCallbacks() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Toast.makeText(Common.currentActivity, "Invalid credential", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            Toast.makeText(Common.currentActivity, "Limit Reached Try Again In a few Hours", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(Common.currentActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        process_dialog.dismiss();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {

                        phoneVerificationId = verificationId;
                        resendToken = token;
                        process_dialog.dismiss();
                    }
                };
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            codeTxt.setText("");

                            Get_User_info();

                            process_dialog.dismiss();

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void Get_User_info() {
        iosDialog.show();
        final String phone_no = phoneNum.replace("+","");
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Variables.uid, phone_no);
        editor.commit();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", phone_no);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.getUserInfo, parameters, new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);

                        iosDialog.cancel();
                        try {
                            JSONObject jsonObject=new JSONObject(respo);
                            String code=jsonObject.optString("code");
                            if(code.equals("200")){

                                // if user is already logedin then we will save the user data and go to the enable location screen
                                JSONArray msg=jsonObject.getJSONArray("msg");
                                JSONObject userdata=msg.getJSONObject(0);

                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString(Variables.uid,phone_no);
                                editor.putString(Variables.f_name,userdata.optString("first_name"));
                                editor.putString(Variables.l_name,userdata.optString("last_name"));
                                editor.putString(Variables.birth_day,userdata.optString("birthday"));
                                editor.putString(Variables.age,userdata.optString("age"));
                                editor.putString(Variables.gender,userdata.optString("gender"));
                                editor.putString(Variables.about,userdata.optString("about_me"));
                                editor.putString(Variables.u_pic1,userdata.optString("image1"));
                                editor.putString(Variables.u_pic2,userdata.optString("image2"));
                                editor.putString(Variables.u_pic3,userdata.optString("image3"));
                                editor.putString(Variables.u_pic4,userdata.optString("image4"));
                                editor.putString(Variables.u_pic5,userdata.optString("image5"));
                                editor.putString(Variables.u_pic6,userdata.optString("image6"));
                                editor.putBoolean(Variables.islogin,true);
                                editor.commit();

                                locationPermission();

                            }else {
                                Intent intent = new Intent(Common.currentActivity, GenderActivity.class);
                                startActivity(intent);
                            }

                        }catch (JSONException e) {

                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                        iosDialog.cancel();
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rq.getCache().clear();

        rq.add(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void locationPermission() {
        if (ActivityCompat.checkSelfPermission(cm.currentActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(cm.currentActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // here if user did not give the permission of location then we move user to enable location screen
            getLocationPermission();
            return;
        }else {
            Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
            startActivity(intent);
        }
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
//                        if (location != null) {

//                            // save the location inlocal and move to main activity
//                            SharedPreferences.Editor editor=sharedPreferences.edit();
//                            editor.putString(Variables.current_Lat,""+location.getLatitude());
//                            editor.putString(Variables.current_Lon,""+location.getLongitude());
//                            editor.commit();
//
//                        }else  {

                            if(sharedPreferences.getString(Variables.current_Lat,"").equals("") || sharedPreferences.getString(Variables.current_Lon,"").equals("") ){
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString(Variables.current_Lat,"33.738045");
                                editor.putString(Variables.current_Lon,"73.084488");
                                editor.commit();
                            }

//                        }
                        Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        switch (editable.toString().length()) {
            case 0:
                backIcon1.setVisibility(View.VISIBLE);
                backIcon2.setVisibility(View.VISIBLE);
                backIcon3.setVisibility(View.VISIBLE);
                backIcon4.setVisibility(View.VISIBLE);
                backIcon5.setVisibility(View.VISIBLE);
                backIcon6.setVisibility(View.VISIBLE);
                break;
            case 1:
                backIcon1.setVisibility(View.INVISIBLE);
                backIcon2.setVisibility(View.VISIBLE);
                backIcon3.setVisibility(View.VISIBLE);
                backIcon4.setVisibility(View.VISIBLE);
                backIcon5.setVisibility(View.VISIBLE);
                backIcon6.setVisibility(View.VISIBLE);
                break;
            case 2:
                backIcon1.setVisibility(View.INVISIBLE);
                backIcon2.setVisibility(View.INVISIBLE);
                backIcon3.setVisibility(View.VISIBLE);
                backIcon4.setVisibility(View.VISIBLE);
                backIcon5.setVisibility(View.VISIBLE);
                backIcon6.setVisibility(View.VISIBLE);
                break;
            case 3:
                backIcon1.setVisibility(View.INVISIBLE);
                backIcon2.setVisibility(View.INVISIBLE);
                backIcon3.setVisibility(View.INVISIBLE);
                backIcon4.setVisibility(View.VISIBLE);
                backIcon5.setVisibility(View.VISIBLE);
                backIcon6.setVisibility(View.VISIBLE);
                break;
            case 4:
                backIcon1.setVisibility(View.INVISIBLE);
                backIcon2.setVisibility(View.INVISIBLE);
                backIcon3.setVisibility(View.INVISIBLE);
                backIcon4.setVisibility(View.INVISIBLE);
                backIcon5.setVisibility(View.VISIBLE);
                backIcon6.setVisibility(View.VISIBLE);
                break;
            case 5:
                backIcon1.setVisibility(View.INVISIBLE);
                backIcon2.setVisibility(View.INVISIBLE);
                backIcon3.setVisibility(View.INVISIBLE);
                backIcon4.setVisibility(View.INVISIBLE);
                backIcon5.setVisibility(View.INVISIBLE);
                backIcon6.setVisibility(View.VISIBLE);
                break;
            case 6:
                backIcon1.setVisibility(View.INVISIBLE);
                backIcon2.setVisibility(View.INVISIBLE);
                backIcon3.setVisibility(View.INVISIBLE);
                backIcon4.setVisibility(View.INVISIBLE);
                backIcon5.setVisibility(View.INVISIBLE);
                backIcon6.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
        if (editable.toString().length() == 6) {
            if (!isClickable) {
                isClickable = true;
                nextBtn.setBackgroundResource(R.drawable.next_btn_1);
            }
        }else {
            if (isClickable) {
                isClickable = false;
                nextBtn.setBackgroundResource(R.drawable.next_btn_0);
            }
        }
    }
}
