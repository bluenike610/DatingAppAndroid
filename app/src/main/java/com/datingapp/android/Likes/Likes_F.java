package com.datingapp.android.Likes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.datingapp.android.Accounts.LoginActivity;
import com.datingapp.android.CodeClasses.Variables;
import com.datingapp.android.InAppSubscription.InApp_Subscription_A;
import com.datingapp.android.Main_Menu.MainMenuActivity;
import com.datingapp.android.Main_Menu.MainMenuFragment;
import com.datingapp.android.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.datingapp.android.R;
import com.datingapp.android.Users.Nearby_User_Get_Set;
import com.datingapp.android.Users.User_detail_F;
import com.datingapp.android.common.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.datingapp.android.common.Common.cm;
import static com.facebook.FacebookSdk.getApplicationContext;

public class Likes_F extends RootFragment {
    View view;
    Context context;

    RecyclerView likeList;
    TextView likeNumTxt;

    ArrayList<Nearby_User_Get_Set> likeArrayList;

    DatabaseReference root_ref;

    public static Likes_Adapter likeAdapter;

    boolean isview_created=false;
    boolean isApiCalling = false;

    public Likes_F() {
        // Required empty public constructor
    }
    public static Likes_F mInstance = new Likes_F();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_likes, container, false);
        context=getContext();

        root_ref= FirebaseDatabase.getInstance().getReference();


        likeList = view.findViewById(R.id.recyclerView);
        likeNumTxt = view.findViewById(R.id.likeNumTxt);

        // intialize the arraylist and and inboxlist
        likeArrayList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        likeList.setLayoutManager(gridLayoutManager);
        likeAdapter = new Likes_Adapter(context, likeArrayList, new Likes_Adapter.OnSelectedLikeItemListner() {
            @Override
            public void OnSelectedLikeItem(Nearby_User_Get_Set data) {
                if (!MainMenuActivity.purduct_purchase)
                    open_subscription_view();
                else
                    open_user_detail(data);
            }
        });
        likeList.setAdapter(likeAdapter); // set the Adapter to RecyclerView
        getLikesData();

        isview_created=true;

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }


    // whenever there is focus in the third tab we will get the match list
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            getLikesData();
        }
    }

    public void getLikesData() {
        if (isApiCalling)
            return;
        isApiCalling = true;

        String latlong="";
        if(MainMenuActivity.sharedPreferences.getBoolean(Variables.is_seleted_location_selected,false)){

            latlong=MainMenuActivity.sharedPreferences.getString(Variables.seleted_Lat,"33.738045")+", "+MainMenuActivity.sharedPreferences.getString(Variables.selected_Lon,"73.084488");
        }else {
            latlong=MainMenuActivity.sharedPreferences.getString(Variables.current_Lat,"33.738045")+", "+MainMenuActivity.sharedPreferences.getString(Variables.current_Lon,"73.084488");
        }

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);
            parameters.put("lat_long", latlong);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("resp",parameters.toString());

        RequestQueue rq = Volley.newRequestQueue(cm.currentActivity);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.likeMe, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        Parse_user_info(respo);
                        isApiCalling = false;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    String block="0";
    private void Parse_user_info(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                likeArrayList.clear();
                JSONArray msg=jsonObject.getJSONArray("msg");
                for (int i=0; i<msg.length();i++){
                    JSONObject userdata=msg.getJSONObject(i);
                    JSONObject username_obj=userdata.getJSONObject("action_profile_name");
                    Nearby_User_Get_Set item=new Nearby_User_Get_Set();
                    item.setFb_id(username_obj.optString("fb_id"));
                    item.setFirst_name(username_obj.optString("first_name"));
                    item.setLast_name(username_obj.optString("last_name"));
                    item.setName(username_obj.optString("first_name")+" "+username_obj.optString("last_name"));
                    item.setJob_title(username_obj.optString("job_title"));
                    item.setCompany(username_obj.optString("company"));
                    item.setSchool(username_obj.optString("school"));
                    item.setBirthday(username_obj.optString("birthday"));
                    item.setAbout(username_obj.optString("about_me"));
                    item.setLocation(username_obj.optString("distance"));
                    item.setGender(username_obj.optString("gender"));
                    item.setSwipe("like");

                    block=username_obj.optString("block");

                    ArrayList<String> images=new ArrayList<>();

                    images.add(username_obj.optString("image1"));

                    if(!username_obj.optString("image2").equals(""))
                        images.add(username_obj.optString("image2"));

                    if(!username_obj.optString("image3").equals(""))
                        images.add(username_obj.optString("image3"));

                    if(!username_obj.optString("image4").equals(""))
                        images.add(username_obj.optString("image4"));

                    if(!username_obj.optString("image5").equals(""))
                        images.add(username_obj.optString("image5"));

                    if(!username_obj.optString("image6").equals(""))
                        images.add(username_obj.optString("image6"));

                    item.setImagesurl(images);

                    likeArrayList.add(item);
                }
                likeAdapter.notifyDataSetChanged();
                likeNumTxt.setText(likeArrayList.size() + " " + cm.currentActivity.getResources().getString(R.string.like_num));
                MainMenuFragment.likeNumTxt.setText(likeArrayList.size()+"");
                if (MainMenuActivity.purduct_purchase)
                    MainMenuFragment.likeNumTxt.setTextColor(cm.currentActivity.getResources().getColor(R.color.purchased_like));
                else
                    MainMenuFragment.likeNumTxt.setTextColor(cm.currentActivity.getResources().getColor(R.color.no_purchased_like));

                if(block.equals("1")){
                    // on press logout btn we will chat the local value and move the user to login screen
                    MainMenuActivity.sharedPreferences.edit().putBoolean(Variables.islogin,false).commit();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                    getActivity().finish();

                }

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }


    }

    public void open_subscription_view(){

        InApp_Subscription_A inApp_subscription_a = new InApp_Subscription_A();

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.MainMenuFragment, inApp_subscription_a)
                .addToBackStack(null)
                .commit();

    }

    public void open_user_detail(Nearby_User_Get_Set item){

        User_like_detail_F user_detail_f = new User_like_detail_F();

        Bundle args = new Bundle();
        args.putSerializable("data",item);
        user_detail_f.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.MainMenuFragment, user_detail_f)
                .addToBackStack(null)
                .commit();

    }

}
