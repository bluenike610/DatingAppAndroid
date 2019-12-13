package com.datingapp.android.Inbox;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class Message_F extends Fragment {

    View view;
    RecyclerView inbox_list;
    Context context;

    ArrayList<Inbox_Get_Set> inbox_arraylist;
    Inbox_Adapter inbox_adapter;
    DatabaseReference root_ref;

    ImageView noMessageview;

    Inbox_F parent;
    SwipeController swipeController = null;
    IOSDialog lodding_view;

    public Message_F(Inbox_F parent) {
        this.parent = parent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_message, container, false);
        context=getContext();

        inbox_list=view.findViewById(R.id.inboxlist);
        root_ref= FirebaseDatabase.getInstance().getReference();

        noMessageview = view.findViewById(R.id.no_message_view);

        // intialize the arraylist and and inboxlist
        inbox_arraylist=new ArrayList<>();

        inbox_list = (RecyclerView) view.findViewById(R.id.inboxlist);
        LinearLayoutManager layout = new LinearLayoutManager(context);
        inbox_list.setLayoutManager(layout);
        inbox_list.setHasFixedSize(false);
        inbox_adapter=new Inbox_Adapter(context, inbox_arraylist, new Inbox_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Inbox_Get_Set item) {
                parent.chatFragment(MainMenuActivity.user_id, item.getId(), item.getName(), item.getPicture(), false);
            }
        }, new Inbox_Adapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(Inbox_Get_Set item) {

            }
        });

        inbox_list.setAdapter(inbox_adapter);

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                Inbox_Get_Set item = inbox_arraylist.get(position-1);
                Call_api_to_Un_match_user(item);
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(inbox_list);

        inbox_list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

        // this is the black color loader that we see whan we click on save button
        lodding_view = new IOSDialog.Builder(context)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        return view;
    }

    // on start we will get the Inbox Message of user  which is show in bottom list of third tab
    ValueEventListener eventListener2;

    Query inbox_query;

    @Override
    public void onStart() {
        super.onStart();
        inbox_query=root_ref.child("Inbox").child(MainMenuActivity.user_id).orderByChild("date");
        eventListener2=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inbox_arraylist.clear();

                for (DataSnapshot ds:dataSnapshot.getChildren()) {
                    Inbox_Get_Set model = new Inbox_Get_Set();
                    model.setId(ds.getKey());
                    model.setName(ds.child("name").getValue().toString());
                    model.setMessage(ds.child("msg").getValue().toString());
                    model.setTimestamp(ds.child("date").getValue().toString());
                    model.setStatus(ds.child("status").getValue().toString());
                    model.setPicture(ds.child("pic").getValue().toString());
                    inbox_arraylist.add(model);
                }
                if (inbox_arraylist.size() > 0)
                    noMessageview.setVisibility(View.INVISIBLE);
                else
                    noMessageview.setVisibility(View.VISIBLE);
                Collections.reverse(inbox_arraylist);
                inbox_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        inbox_query.addValueEventListener(eventListener2);


    }

    // on stop we will remove the listener
    @Override
    public void onStop() {
        super.onStop();
        if(inbox_query!=null)
            inbox_query.removeEventListener(eventListener2);
    }

    // this will call the api to unmatch the user and api will remove all chat as well their inbox node
    public void Call_api_to_Un_match_user(final Inbox_Get_Set otherInfo) {
        lodding_view.show();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);
            parameters.put("other_id", otherInfo.getId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("resp",parameters.toString());

        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.unMatch, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("respo",respo);
                        lodding_view.cancel();
                        inbox_arraylist.remove(otherInfo);
                        inbox_adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        lodding_view.cancel();
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }


}
