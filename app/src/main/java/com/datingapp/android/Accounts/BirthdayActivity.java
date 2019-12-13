package com.datingapp.android.Accounts;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.datingapp.android.CodeClasses.Variables;
import com.datingapp.android.R;
import com.datingapp.android.common.Common;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import static com.datingapp.android.common.Common.cm;

public class BirthdayActivity extends AppCompatActivity {


    private OtpView yearTxt, monthTxt, dayTxt;
    private RelativeLayout nextBtn;

    private String yearValue = "";
    private String monthValue = "";
    private String dayValue = "";

    private Boolean isClickable = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday);
        Common.currentActivity = this;

        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
        String birthday = sharedPreferences.getString(Variables.birth_day, "null");
        RelativeLayout backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        yearTxt = findViewById(R.id.yearTxt);
        yearTxt.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override public void onOtpCompleted(String otp) {
                if (otp.length() == 4) {
                    yearValue = otp;
                    checkValue();
                }
            }
        });

        monthTxt = findViewById(R.id.monthTxt);
        monthTxt.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override public void onOtpCompleted(String otp) {
                if (otp.length() == 2) {
                    monthValue = otp;
                    checkValue();
                }
            }
        });

        dayTxt = findViewById(R.id.dayTxt);
        dayTxt.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override public void onOtpCompleted(String otp) {
                if (otp.length() == 2) {
                    dayValue = otp;
                    checkValue();
                }
            }
        });


        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClickable) {
                    int year = Integer.valueOf(yearValue);
                    if (year < 1900 || year > 2010) {
                        cm.showAlertDlg(getResources().getString(R.string.input_err_title),
                                getResources().getString(R.string.input_err_msg), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                yearTxt.setText("");
                            }
                        }, null);
                        return;
                    }
                    int month = Integer.valueOf(monthValue);
                    if (month <= 0 || month > 12) {
                        cm.showAlertDlg(getResources().getString(R.string.input_err_title),
                                getResources().getString(R.string.input_err_msg), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                monthTxt.setText("");
                            }
                        }, null);
                        return;
                    }
                    int day = Integer.valueOf(dayValue);
                    if (day <= 0 || day > 31) {
                        cm.showAlertDlg(getResources().getString(R.string.input_err_title),
                                getResources().getString(R.string.input_err_msg), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dayTxt.setText("");
                            }
                        }, null);
                        return;
                    }

                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    String birthday = monthValue + "/" + dayValue + "/" + yearValue;
                    editor.putString(Variables.birth_day, birthday);
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                    startActivity(intent);

                }
            }
        });

        if (!birthday.equals("null")) {
            String[] birthArr = birthday.split("/");
//            if (birthArr.length > 0) {
//                yearValue = birthArr[2];
//                yearTxt.setText(yearValue);
//                monthValue = birthArr[0];
//                monthTxt.setText(monthValue);
//                dayValue = birthArr[1];
//                dayTxt.setText(dayValue);
//                checkValue();
//            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.currentActivity = this;
    }

    private void checkValue() {
        if (yearValue.length() == 4 &&
        monthValue.length() == 2 &&
        dayValue.length() == 2) {
            isClickable = true;
            nextBtn.setBackgroundResource(R.drawable.next_btn_1);
        }else {
            isClickable = false;
            nextBtn.setBackgroundResource(R.drawable.next_btn_0);
        }
    }
}
