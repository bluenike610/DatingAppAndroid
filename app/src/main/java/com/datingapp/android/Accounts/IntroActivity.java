package com.datingapp.android.Accounts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IntroActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    public IOSDialog iosDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Common.currentActivity = this;

        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
        String intro = sharedPreferences.getString(Variables.about, "null");
        RelativeLayout backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        iosDialog = new IOSDialog.Builder(this)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        final TextView limitNumTxt = findViewById(R.id.limitNumTxt);
        limitNumTxt.setText("500");

        final EditText introTxt = findViewById(R.id.introTxt);
        if (!intro.equals("null")) {
            introTxt.setText(intro);
        }
        introTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int currentNum = editable.toString().length();
                limitNumTxt.setText((500-currentNum) + "");
            }
        });

        RelativeLayout nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(Variables.about, introTxt.getText().toString());
                editor.commit();
                Call_Api_For_Signup();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.currentActivity = this;
    }

    // this method will store the info of user to  database
    private void Call_Api_For_Signup() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", sharedPreferences.getString(Variables.uid, "null"));
            parameters.put("first_name",sharedPreferences.getString(Variables.f_name, "null"));
            parameters.put("last_name", "");
            parameters.put("birthday", sharedPreferences.getString(Variables.birth_day, "null"));
            parameters.put("gender", sharedPreferences.getString(Variables.gender, "null"));
            parameters.put("about_me", sharedPreferences.getString(Variables.about, "null"));
            parameters.put("image1",sharedPreferences.getString(Variables.u_pic1, "null"));
            parameters.put("image2",sharedPreferences.getString(Variables.u_pic2, "null"));
            parameters.put("image3",sharedPreferences.getString(Variables.u_pic3, "null"));
            parameters.put("image4",sharedPreferences.getString(Variables.u_pic4, "null"));
            parameters.put("image5",sharedPreferences.getString(Variables.u_pic5, "null"));
            parameters.put("image6",sharedPreferences.getString(Variables.u_pic6, "null"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.SignUp, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);

                        iosDialog.cancel();
                        Parse_signup_data(respo);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    private void Parse_signup_data(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean(Variables.islogin,true);
                editor.commit();

                // after all things done we will move the user to enable location screen
                Intent intent = new Intent(Common.currentActivity, MainMenuActivity.class);
                startActivity(intent);
            }else {
                Toast.makeText(this, ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            iosDialog.cancel();
            e.printStackTrace();
        }

    }

}
