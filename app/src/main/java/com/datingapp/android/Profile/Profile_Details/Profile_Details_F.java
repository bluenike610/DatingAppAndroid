package com.datingapp.android.Profile.Profile_Details;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.datingapp.android.CodeClasses.Variables;
import com.datingapp.android.Main_Menu.MainMenuActivity;
import com.datingapp.android.Profile.EditProfile.EditProfile_F;
import com.datingapp.android.R;
import com.datingapp.android.common.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile_Details_F extends Fragment {

    View view;
    Context context;


    ImageButton move_downbtn;

    RelativeLayout username_layout;
    ScrollView scrollView;


    SliderLayout sliderLayout;

    PagerIndicator pagerIndicator;



    ArrayList<String> images_list;



    TextView user_name_txt,user_jobtitle_txt,user_birthday_txt,user_aboutme_txt;



    LinearLayout job_layout,birthday_layout;

    String user_id;

    public Profile_Details_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_profile_details, container, false);
        context=getContext();


        scrollView = view.findViewById(R.id.scrollView);
        username_layout = view.findViewById(R.id.username_layout);

        move_downbtn = view.findViewById(R.id.move_downbtn);
        move_downbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();


            }
        });


        Bundle bundle = getArguments();
        if (bundle != null) {
            user_id = bundle.getString("user_id");

        }else {
            user_id= MainMenuActivity.user_id;
        }

        images_list=new ArrayList<>();
        sliderLayout = view.findViewById(R.id.slider);
        pagerIndicator=view.findViewById(R.id.custom_indicator);

        sliderLayout.setVisibility(View.INVISIBLE);


//        YoYo.with(Techniques.BounceInDown)
//                .duration(800)
//                .playOn(move_downbtn);




        user_name_txt=view.findViewById(R.id.user_name_txt);

        user_jobtitle_txt=view.findViewById(R.id.user_jobtitle_txt);

        user_birthday_txt=view.findViewById(R.id.user_birthday_txt);

        user_aboutme_txt=view.findViewById(R.id.user_about_txt);




        job_layout=view.findViewById(R.id.job_layout);
        birthday_layout=view.findViewById(R.id.birthday_layout);

        if (user_id.equals(MainMenuActivity.user_id)) {
            SharedPreferences sharedPreferences = Common.currentActivity.getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
            images_list.clear();
            images_list.add(sharedPreferences.getString(Variables.u_pic1, "null"));
            images_list.add(sharedPreferences.getString(Variables.u_pic2, "null"));
            images_list.add(sharedPreferences.getString(Variables.u_pic3, "null"));
            images_list.add(sharedPreferences.getString(Variables.u_pic4, "null"));
            images_list.add(sharedPreferences.getString(Variables.u_pic5, "null"));
            images_list.add(sharedPreferences.getString(Variables.u_pic6, "null"));

            user_name_txt.setText(MainMenuActivity.user_name+" ,"+MainMenuActivity.age);
            user_jobtitle_txt.setText(sharedPreferences.getString(Variables.job, ""));
            user_aboutme_txt.setText(sharedPreferences.getString(Variables.about, ""));
            birthday_layout.setVisibility(View.GONE);

            fill_data();
        }else {
            Get_User_info();
        }

        return view;
    }




    // below two method is used get the user pictures and about text from our server
    private void Get_User_info() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.getUserInfo, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        Parse_user_info(respo);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }


    public void Parse_user_info(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray msg=jsonObject.getJSONArray("msg");
                JSONObject userdata=msg.getJSONObject(0);

                images_list.clear();
                images_list.add(userdata.optString("image1"));
                images_list.add(userdata.optString("image2"));
                images_list.add(userdata.optString("image3"));
                images_list.add(userdata.optString("image4"));
                images_list.add(userdata.optString("image5"));
                images_list.add(userdata.optString("image6"));


                user_name_txt.setText(userdata.optString("first_name")+" "+userdata.optString("last_name")+" ,"+userdata.optString("age"));

                String user_jobtitle=userdata.optString("job_title");
                String user_company=userdata.optString("company");
                String user_birthday=userdata.optString("distance");

                if(user_jobtitle.equals("") & !user_company.equals("")){

                    user_jobtitle_txt.setText(user_company);

                }else if(user_company.equals("") && !user_jobtitle.equals("") ){

                    user_jobtitle_txt.setText(user_jobtitle);

                }
                else if(user_company.equals("") && user_jobtitle.equals("") ){

                    job_layout.setVisibility(View.GONE);
                }
                else {
                    user_jobtitle_txt.setText(user_jobtitle+" at "+user_company);

                }


                user_birthday_txt.setText(userdata.optString("birthday"));
                user_aboutme_txt.setText(userdata.optString("about_me"));


                if(user_birthday.equals("")){
                    birthday_layout.setVisibility(View.GONE);
                }


                fill_data();


            }
        } catch (JSONException e) {

            e.printStackTrace();
        }

    }


    // this method will set and show the pictures slider
    public void fill_data(){
        sliderLayout.setCustomIndicator(pagerIndicator);
        sliderLayout.stopAutoCycle();

        for (int i = 0; i < images_list.size(); i++) {

            if(!images_list.get(i).equals("")){
            DefaultSliderView defaultSliderView = new DefaultSliderView(context);
            defaultSliderView
                    .image(images_list.get(i))
                    .setScaleType(BaseSliderView.ScaleType.FitCenterCrop);
            defaultSliderView.bundle(new Bundle());
            sliderLayout.addSlider(defaultSliderView);
            }

        }
        sliderLayout.setVisibility(View.VISIBLE);

    }



    // open the view of Edit profile where 6 pic is visible
    public void Editprofile(){
        EditProfile_F editProfile_f = new EditProfile_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
       //transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, editProfile_f).commit();
    }


}
