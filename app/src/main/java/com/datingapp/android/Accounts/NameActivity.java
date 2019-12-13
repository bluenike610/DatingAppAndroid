package com.datingapp.android.Accounts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.datingapp.android.CodeClasses.Variables;
import com.datingapp.android.R;
import com.datingapp.android.common.Common;

public class NameActivity extends AppCompatActivity {

    private RelativeLayout nextBtn;
    private Boolean isClickable = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        Common.currentActivity = this;

        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
        String name = sharedPreferences.getString(Variables.f_name, "null");
        final EditText nameTxt = findViewById(R.id.nameTxt);
        RelativeLayout backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClickable) {
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString(Variables.f_name, nameTxt.getText().toString());
                    editor.commit();
                    Intent intent = new Intent(Common.currentActivity, BirthdayActivity.class);
                    startActivity(intent);
                }
            }
        });

        nameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0) {
                    isClickable = true;
                    nextBtn.setBackgroundResource(R.drawable.next_btn_1);
                }else {
                    isClickable = false;
                    nextBtn.setBackgroundResource(R.drawable.next_btn_0);
                }
            }
        });
        if (!name.equals("null")) {
            nameTxt.setText(name);
            isClickable = true;
            nextBtn.setBackgroundResource(R.drawable.next_btn_1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.currentActivity = this;
    }
}
