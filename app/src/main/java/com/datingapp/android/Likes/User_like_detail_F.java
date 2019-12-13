package com.datingapp.android.Likes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

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
import com.datingapp.android.CodeClasses.Functions;
import com.datingapp.android.CodeClasses.Variables;
import com.datingapp.android.Inbox.Match_Get_Set;
import com.datingapp.android.Main_Menu.MainMenuActivity;
import com.datingapp.android.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.datingapp.android.Matchs.Match_F;
import com.datingapp.android.R;
import com.datingapp.android.Users.Nearby_User_Get_Set;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User_like_detail_F extends RootFragment implements View.OnClickListener {


    View view;
    Context context;

    ImageButton move_downbtn;

    RelativeLayout username_layout;
    ScrollView scrollView;

    TextView username_txt, bottom_age,bottom_job_txt,bottom_location_text,bottom_about_txt;

    TextView bottom_report_txt;

    SliderLayout sliderLayout;

    PagerIndicator pagerIndicator;

    Nearby_User_Get_Set data_item;

    IOSDialog iosDialog;
    DatabaseReference rootref;

    public User_like_detail_F() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_like_user_detail, container, false);
        context = getContext();

        iosDialog = new IOSDialog.Builder(context)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();
        rootref= FirebaseDatabase.getInstance().getReference();

        Bundle bundle=getArguments();
        if(bundle!=null)
        {
            // get all the data of the user from privious fragment and show it here
            data_item= (Nearby_User_Get_Set) bundle.getSerializable("data");
        }

        scrollView = view.findViewById(R.id.scrollView);
        username_layout = view.findViewById(R.id.username_layout);

        move_downbtn = view.findViewById(R.id.move_downbtn);
        move_downbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ImageView unlikeBtn = view.findViewById(R.id.cross_btn);
        unlikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                updatedata_onLeftSwipe(data_item);
            }
        });

        ImageView likeBtn = view.findViewById(R.id.heart_btn);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                updatedata_onrightSwipe(data_item);
            }
        });



        sliderLayout = view.findViewById(R.id.slider);
        pagerIndicator=view.findViewById(R.id.custom_indicator);

        sliderLayout.setVisibility(View.INVISIBLE);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        YoYo.with(Techniques.BounceInDown)
//                .duration(800)
//                .playOn(move_downbtn);

        init_bottom_view();


        return view;

    }


    // this method will initialize all the views and set the data in that view
    public void init_bottom_view() {

        username_txt = view.findViewById(R.id.username_txt);
        username_txt.setText(data_item.getName());


        bottom_age = view.findViewById(R.id.bottom_age);


        if(!data_item.getBirthday().equals(""))
            bottom_age.setText(", "+data_item.getBirthday());


        bottom_job_txt=view.findViewById(R.id.bottom_job_txt);
        if(data_item.getJob_title().equals("") & !data_item.getCompany().equals("")){

            bottom_job_txt.setText(data_item.getJob_title());

        }else if(data_item.getCompany().equals("") && !data_item.getJob_title().equals("") ){

            bottom_job_txt.setText(data_item.getJob_title());

        }
        else if(data_item.getCompany().equals("") && data_item.getJob_title().equals("") ){
            view.findViewById(R.id.job_layout).setVisibility(View.GONE);
        }
        else {
            bottom_job_txt.setText(data_item.getJob_title()+" at "+data_item.getSchool());

        }


        bottom_location_text=view.findViewById(R.id.bottom_location_txt);
        bottom_location_text.setText(data_item.getLocation());

        bottom_about_txt=view.findViewById(R.id.bottom_about_txt);
        if(data_item.getAbout().equals("")){
            bottom_about_txt.setVisibility(View.GONE);
        }
        bottom_about_txt.setText(data_item.getAbout());


        bottom_report_txt=view.findViewById(R.id.bottom_report_txt);
        bottom_report_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Report_User_alert();
            }
        });
    }


    // when all the animation is done then we will place a data into the view
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation anim= MoveAnimation.create(MoveAnimation.UP, enter, 200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    // when animation is done then we will show the picture slide
                    // becuase animation in that case will show fulently

                    fill_data();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            return anim;

        } else {
            return MoveAnimation.create(MoveAnimation.DOWN, enter, 200);
        }
    }



    // this method will set and show the pictures slider
    public void fill_data(){
        sliderLayout.setCustomIndicator(pagerIndicator);
        sliderLayout.stopAutoCycle();
        ArrayList<String> file_maps=new ArrayList<>() ;
        file_maps=data_item.getImagesurl();
        for (int i = 0; i < file_maps.size(); i++) {

            if(!file_maps.get(i).equals("")){

                DefaultSliderView defaultSliderView = new DefaultSliderView(context);
                defaultSliderView
                        .image(file_maps.get(i))
                        .setScaleType(BaseSliderView.ScaleType.FitCenterCrop);
                defaultSliderView.bundle(new Bundle());
                sliderLayout.addSlider(defaultSliderView);

            }
        }
        sliderLayout.setVisibility(View.VISIBLE);

    }



    @Override
    public void onClick(View v) {
    }


    // this will show an alert will is show when we click to Report a User
    public void Report_User_alert(){
        final AlertDialog.Builder alert=new AlertDialog.Builder(context,R.style.DialogStyle);
        alert.setTitle("Report")
                .setMessage("Are you sure to Report this user?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Send_report();
                    }
                });

        alert.setCancelable(true);
        alert.show();
    }


    // below two method will get all the  new user that is nearby of us  and parse the data in dataset
    // in that case which has a name of Nearby get set
    private void Send_report() {
        iosDialog.show();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("my_id", MainMenuActivity.user_id);
            parameters.put("fb_id", data_item.getFb_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("resp",parameters.toString());

        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.flat_user, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);

                        JSONArray jsonArray=response.optJSONArray("msg");
                        Toast.makeText(context, ""+jsonArray.optJSONObject(0).optString("response"), Toast.LENGTH_SHORT).show();

                        iosDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                        iosDialog.dismiss();
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    // when we swipe left , right or reverse then this method is call and update the value in firebase database
    public void updatedata_onLeftSwipe(final Nearby_User_Get_Set item){

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("hh");
        final String formattedDate = df.format(c);

        rootref.child("Match").child(item.getFb_id()).child(MainMenuActivity.user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map mymap=new HashMap<>();
                mymap.put("match","false");
                mymap.put("type","dislike");
                mymap.put("status","0");
                mymap.put("time",formattedDate);
                mymap.put("name",item.getName());
                mymap.put("effect","true");


                Map othermap=new HashMap<>();
                othermap.put("match","false");
                othermap.put("type","dislike");
                othermap.put("status","0");
                othermap.put("time",formattedDate);
                othermap.put("name",MainMenuActivity.user_name);
                othermap.put("effect","false");
                if(dataSnapshot.exists()){
                    rootref.child("Match").child(MainMenuActivity.user_id+"/"+item.getFb_id()).updateChildren(mymap);
                }else {
                    rootref.child("Match").child(MainMenuActivity.user_id+"/"+item.getFb_id()).setValue(mymap);
                    rootref.child("Match").child(item.getFb_id()+"/"+MainMenuActivity.user_id).setValue(othermap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SendPushNotification(item.getFb_id(),MainMenuActivity.user_name+" has Dislike your Profile");

    }



    public void updatedata_onrightSwipe(final Nearby_User_Get_Set item){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("hh");
        final String formattedDate = df.format(c);

        Query query=rootref.child("Match").child(item.getFb_id()).child(MainMenuActivity.user_id);
        query.keepSynced(true);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map mymap=new HashMap<>();
                mymap.put("match","true");
                mymap.put("type","like");
                mymap.put("status","0");
                mymap.put("time",formattedDate);
                mymap.put("name",item.getName());
                mymap.put("effect","true");

                Map othermap=new HashMap<>();
                othermap.put("match","true");
                othermap.put("type","like");
                othermap.put("status","0");
                othermap.put("time",formattedDate);
                othermap.put("name",MainMenuActivity.user_name);
                othermap.put("effect","false");
                if(dataSnapshot.exists()){
                    rootref.child("Match").child(MainMenuActivity.user_id+"/"+item.getFb_id()).updateChildren(mymap);
                    rootref.child("Match").child(item.getFb_id()+"/"+MainMenuActivity.user_id).updateChildren(othermap);
                }else {
                    rootref.child("Match").child(MainMenuActivity.user_id+"/"+item.getFb_id()).setValue(mymap);
                    rootref.child("Match").child(item.getFb_id()+"/"+MainMenuActivity.user_id).setValue(othermap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SendPushNotification(item.getFb_id(),MainMenuActivity.user_name+" has Likes your Profile");

        Match_Get_Set match_get_set=new Match_Get_Set();
        match_get_set.setU_id(item.getFb_id());
        match_get_set.setUsername(item.getName());
        match_get_set.setPicture(item.getImagesurl().get(0));
        openMatch(match_get_set);

    }

    public void SendPushNotification(final String receverid, final String message){
        rootref.child("Users").child(receverid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String token=dataSnapshot.child("token").getValue().toString();
                    JSONObject notimap=new JSONObject();
                    try {
                        notimap.put("title",MainMenuActivity.user_name);
                        notimap.put("message",message);
                        notimap.put("icon", MainMenuActivity.user_pic);
                        notimap.put("tokon",token);
                        notimap.put("senderid",MainMenuActivity.user_id);
                        notimap.put("receiverid", receverid);
                        notimap.put("action_type", "Like_Dislike");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Functions.Call_api_Send_Notification(getActivity(),notimap);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // when a match is build between user then this method is call and open the view if match screen
    public void openMatch(Match_Get_Set item){
        Match_F match_f = new Match_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable("data",item);
        match_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, match_f).commit();

    }

}