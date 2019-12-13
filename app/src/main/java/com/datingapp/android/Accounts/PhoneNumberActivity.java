package com.datingapp.android.Accounts;

import androidx.annotation.RequiresApi;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.datingapp.android.R;
import com.datingapp.android.common.Common;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PhoneNumberActivity extends AppCompatActivity implements TextWatcher {

    private EditText phoneNumTxt;
    private RelativeLayout nextBtn;
    MaterialDialog process_dialog;

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth fbAuth;

    private Boolean isClickable = false;
    private String country_code = "+81";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        Common.currentActivity = this;

        fbAuth = FirebaseAuth.getInstance();

        RelativeLayout backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final ImageView countryFlag = findViewById(R.id.countryFlag);

        LinearLayout countryLayout = findViewById(R.id.flagLayout);
        countryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        // Implement your code here
                        country_code = dialCode;
                        countryFlag.setImageDrawable(getResources().getDrawable(flagDrawableResID));
                        picker.dismiss();
                    }
                });
                picker.show(getSupportFragmentManager(), "Select Country");
            }
        });

        phoneNumTxt = findViewById(R.id.phoneText);
        phoneNumTxt.addTextChangedListener(this);

        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClickable) {
                    try {
                        if (!isConnected()) {
                            //  Toast.makeText(LoginActivity.this, "Network problem", Toast.LENGTH_SHORT).show();
                            Snackbar.make(view, "Sorry there was a problem with your network", Snackbar.LENGTH_LONG)
                                    .setAction("Check Internet", null).show();
                        }
                        else {
                            sendCode();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.currentActivity = this;
    }

    public boolean isConnected() throws InterruptedException, IOException
    {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void sendCode() {

        String phoneNumber = country_code + phoneNumTxt.getText().toString().replace(" ", "").substring(1);
        setUpVerificatonCallbacks();


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);

        //Set processing indicators
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
//                        signInWithPhoneAuthCredential(credential);
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
                        Intent intent = new Intent(Common.currentActivity, SMSCodeActivity.class);
                        String phoneNumber = country_code + phoneNumTxt.getText().toString().replace(" ", "").substring(1);
                        intent.putExtra("phone_num", phoneNumber);
                        intent.putExtra("verification_id", phoneVerificationId);
                        intent.putExtra("resend_token", resendToken);
                        startActivity(intent);
                    }
                };
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    private String oldTxt = "";

    @Override
    public void afterTextChanged(Editable editable) {
        phoneNumTxt.setSelection(editable.toString().length());
        String originTxt = editable.toString();
        if (!originTxt.equals(oldTxt)) {
            originTxt = originTxt.replace(" ", "");
            if (originTxt.length() == 11) {
                isClickable = true;
                nextBtn.setBackgroundResource(R.drawable.next_btn_1);
            }else {
                isClickable = false;
                nextBtn.setBackgroundResource(R.drawable.next_btn_0);
            }
            String newTxt = originTxt;
            if (originTxt.length() > 3) {
                newTxt = originTxt.substring(0, 3) + " " + originTxt.substring(3);
            }
            if (newTxt.length() > 8) {
                newTxt = newTxt.substring(0, 8) + " " + newTxt.substring(8);
            }
            oldTxt = newTxt;
            phoneNumTxt.setText(newTxt);
        }
    }
}
