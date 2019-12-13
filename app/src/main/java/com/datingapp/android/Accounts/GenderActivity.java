package com.datingapp.android.Accounts;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.datingapp.android.CodeClasses.Variables;
import com.datingapp.android.R;
import com.datingapp.android.common.Common;

public class GenderActivity extends AppCompatActivity {

    private RelativeLayout femaleBtn, maleBtn, nextBtn;

    private String selectedBtnTxt;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);
        Common.currentActivity = this;

        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
        selectedBtnTxt = sharedPreferences.getString(Variables.gender, "null");

        RelativeLayout backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        femaleBtn = findViewById(R.id.femaleBtn);
        femaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedBtnTxt = "female";
                femaleBtn.setBackgroundResource(R.drawable.female_btn_1);
                maleBtn.setBackgroundResource(R.drawable.male_btn_0);
                nextBtn.setBackgroundResource(R.drawable.next_btn_1);
            }
        });

        maleBtn = findViewById(R.id.maleBtn);
        maleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedBtnTxt = "male";
                maleBtn.setBackgroundResource(R.drawable.male_btn_1);
                femaleBtn.setBackgroundResource(R.drawable.female_btn_0);
                nextBtn.setBackgroundResource(R.drawable.next_btn_1);
            }
        });

        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedBtnTxt.equals("")) {
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString(Variables.gender, selectedBtnTxt);
                    editor.commit();
                    Intent intent = new Intent(Common.currentActivity, NameActivity.class);
                    startActivity(intent);
                }
            }
        });

        if (!selectedBtnTxt.equals("null")) {
            if (selectedBtnTxt.equals("male")) {
                maleBtn.setBackgroundResource(R.drawable.male_btn_1);
                femaleBtn.setBackgroundResource(R.drawable.female_btn_0);
                nextBtn.setBackgroundResource(R.drawable.next_btn_1);
            }else {
                femaleBtn.setBackgroundResource(R.drawable.female_btn_1);
                maleBtn.setBackgroundResource(R.drawable.male_btn_0);
                nextBtn.setBackgroundResource(R.drawable.next_btn_1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.currentActivity = this;
    }
}
