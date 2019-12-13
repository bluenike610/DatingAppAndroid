package com.datingapp.android.Inbox;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Matches_F extends Fragment {
    View view;
    Context context;

    RecyclerView match_list;
    ArrayList<Match_Get_Set> matchs_users_list;

    DatabaseReference root_ref;

    Matches_Adapter matches_adapter;
    boolean isview_created=false;

    TextView matchNumTxt;
    ImageView noMessageview;

    Inbox_F parent;

    public Matches_F(Inbox_F parent) {
        this.parent = parent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_matches, container, false);
        context=getContext();

        root_ref= FirebaseDatabase.getInstance().getReference();

        noMessageview = view.findViewById(R.id.no_message_view);

        match_list=view.findViewById(R.id.match_list);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        match_list.setLayoutManager(layoutManager);
        match_list.setItemAnimator(new DefaultItemAnimator());

        matchNumTxt = view.findViewById(R.id.match_num);



        // intialize the arraylist and and upper Match list
        matchs_users_list=new ArrayList<>();

        matches_adapter=new Matches_Adapter(context, matchs_users_list, new Matches_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Match_Get_Set item) {
                // first check the storage permission then open the chat fragment
              parent.chatFragment(MainMenuActivity.user_id,item.getU_id(),item.getUsername(),item.getPicture(),true);

            }
        }, new Matches_Adapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(Match_Get_Set item) {

            }
        });


        match_list.setAdapter(matches_adapter);

        isview_created=true;
        get_match_data();

        return view;
    }

    // whenever there is focus in the third tab we will get the match list
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isview_created){
//            get_match_data();
        }
    }

    // below two method will get all the  new user that is nearby of us  and parse the data in dataset
    // in that case which has a name of Nearby get set
    public void get_match_data() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("resp",parameters.toString());

        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.myMatch, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        Parse_user_info(respo);
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


    public void Parse_user_info(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                matchs_users_list.clear();
                JSONArray msg=jsonObject.getJSONArray("msg");
                for (int i=0; i<msg.length();i++){
                    JSONObject userdata=msg.getJSONObject(i);
                    Match_Get_Set model = new Match_Get_Set();
                    model.setU_id(userdata.optString("effect_profile"));
                    JSONObject username_obj=userdata.getJSONObject("effect_profile_name");
                    model.setPicture(username_obj.optString("image1"));
                    model.setUsername(username_obj.optString("first_name")+" "+username_obj.optString("last_name"));
                    matchs_users_list.add(model);
                }
                if (matchs_users_list.size() > 0) {
                    noMessageview.setVisibility(View.INVISIBLE);
                }
                else {
                    noMessageview.setVisibility(View.VISIBLE);

                }
                matchNumTxt.setText(matchs_users_list.size() + context.getResources().getString(R.string.inbox_like_num));
                matches_adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }


    }
}
