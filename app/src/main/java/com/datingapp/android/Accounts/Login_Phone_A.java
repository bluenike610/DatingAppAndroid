package com.datingapp.android.Accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.datingapp.android.CodeClasses.Variables;
import com.datingapp.android.R;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class Login_Phone_A extends AppCompatActivity {

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth fbAuth;
    EditText phoneText,digit1,digit2,digit3,digit4,digit5,digit6;
    TextView countrytxt,countrycodetxt,sendtotxt;
    RelativeLayout select_country;
    ViewFlipper viewFlipper;
    String phoneNumber;
    public IOSDialog iosDialog;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);
        fbAuth = FirebaseAuth.getInstance();


        sharedPreferences=getSharedPreferences(Variables.pref_name,MODE_PRIVATE);

        phoneText=findViewById(R.id.phonetxt);

        select_country=findViewById(R.id.select_country);
        select_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Opencountry();
            }
        });

        countrytxt    =findViewById(R.id.countrytxt);
        countrycodetxt=findViewById(R.id.countrycodetxt);

        sendtotxt=findViewById(R.id.sendtotxt);

        viewFlipper=findViewById(R.id.viewfillper);

        codefill();

        iosDialog = new IOSDialog.Builder(this)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

    }

    //message code fill in edittext and change focus in android
    public void codefill(){
        digit1=findViewById(R.id.digitone);
        digit2=findViewById(R.id.digittwo);
        digit3=findViewById(R.id.digitthree);
        digit4=findViewById(R.id.digitfour);
        digit5=findViewById(R.id.digitfive);
        digit6=findViewById(R.id.digitsix);

        digit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(digit1.getText().toString().length()==0){
                    digit2.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        digit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(digit2.getText().toString().length()==0){
                    digit3.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        digit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(digit3.getText().toString().length()==0){
                    digit4.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        digit4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(digit4.getText().toString().length()==0){
                    digit5.requestFocus();
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        digit5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(digit5.getText().toString().length()==0){
                    digit6.requestFocus();
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    String country_iso_code="US";
    @SuppressLint("WrongConstant")
    public void Opencountry(){
        final CountryPicker picker = CountryPicker.newInstance("Select Country");
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                // Implement your code here
                countrytxt.setText(name);
                countrycodetxt.setText(dialCode);
                picker.dismiss();
                country_iso_code=code;
            }
        });
        picker.setStyle(R.style.countrypicker_style,R.style.countrypicker_style);
        picker.show(getSupportFragmentManager(), "Select Country");
    }

    public void Nextbtn(View view) {
        phoneNumber=countrycodetxt.getText().toString()+phoneText.getText().toString();
        Send_Number_tofirebase(phoneNumber);
    }


    public void Send_Number_tofirebase(String phoneNumber){
        setUpVerificatonCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);
    }

    private void setUpVerificatonCallbacks() {
        iosDialog.show();
        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {

                           iosDialog.cancel();
                            signInWithPhoneAuthCredential(credential);

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        iosDialog.cancel();
                        Log.d("responce",e.toString());
                        Toast.makeText(Login_Phone_A.this, "Enter Correct Number.", Toast.LENGTH_SHORT).show();
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {

                        iosDialog.cancel();

                        phoneVerificationId = verificationId;
                        resendToken = token;
                        sendtotxt.setText("Send to ( "+phoneNumber+" )");
                        iosDialog.cancel();
                        viewFlipper.setInAnimation(Login_Phone_A.this, R.anim.in_from_right);
                        viewFlipper.setOutAnimation(Login_Phone_A.this, R.anim.out_to_left);
                        viewFlipper.setDisplayedChild(1);

                    }
                };
    }


    public void verifyCode(View view) {
    String code=""+digit1.getText().toString()+digit2.getText().toString()+digit3.getText().toString()+digit4.getText().toString()+digit5.getText().toString()+digit6.getText().toString();
    if(!code.equals("")){
        iosDialog.show();
    PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }else {
        Toast.makeText(this, "Enter the Correct varification Code", Toast.LENGTH_SHORT).show();
    }

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // get the user info to know that user is already login or not
                            Get_User_info();

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                iosDialog.cancel();
                            }
                        }
                    }
                });
    }


    public void resendCode(View view) {

        String phoneNumber = phoneText.getText().toString();

        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                resendToken);
    }


    public void Goback_1(View view) {
        finish();
    }

    public void Goback(View view) {
        viewFlipper.setInAnimation(Login_Phone_A.this, R.anim.in_from_left);
        viewFlipper.setOutAnimation(Login_Phone_A.this, R.anim.out_to_right);
        viewFlipper.setDisplayedChild(0);
    }


    private void Get_User_info() {
        iosDialog.show();
        final String phone_no=phoneNumber.replace("+","");
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", phone_no);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.getUserInfo, parameters, new Response.Listener<JSONObject>() {
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
                                editor.putString(Variables.birth_day,userdata.optString("age"));
                                editor.putString(Variables.gender,userdata.optString("gender"));
                                editor.putString(Variables.u_pic1,userdata.optString("image1"));
                                editor.putBoolean(Variables.islogin,true);
                                editor.commit();

                                // after all things done we will move the user to enable location screen
                                enable_location();


                            }else {
                                // if user is first time login then we will get the usser picture and name
                                Intent intent=new Intent(Login_Phone_A.this, Get_User_Info_A.class);
                                intent.putExtra("id",phone_no);
                                startActivity(intent);
                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                finish();

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


    private void enable_location() {
        // will move the user for enable location screen
        Enable_location_F enable_location_f = new Enable_location_F();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left,R.anim.in_from_left,R.anim.out_to_right);
        getSupportFragmentManager().popBackStackImmediate();
        transaction.replace(R.id.Login_Phone_A, enable_location_f).addToBackStack(null).commit();
    }



}
