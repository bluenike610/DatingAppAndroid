package com.datingapp.android.Inbox;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.datingapp.android.Chat.Chat_Activity;
import com.datingapp.android.CodeClasses.Functions;
import com.datingapp.android.CodeClasses.Variables;
import com.datingapp.android.Main_Menu.MainMenuActivity;
import com.datingapp.android.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.datingapp.android.R;
import com.datingapp.android.common.Common;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class Inbox_F extends RootFragment {


    View view;
    Context context;

    private final String[] PAGE_TITLES = new String[] {
            Common.currentActivity.getResources().getString(R.string.inbox_all_tab),
            Common.currentActivity.getResources().getString(R.string.inbox_match_tab)
    };

    private Fragment[] PAGES;


    private Message_F message_f;
    private Matches_F matches_f;

    private NonSwipeableViewPager mViewPager;

    public Inbox_F() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_inbox, container, false);
        context=getContext();

        message_f = new Message_F(this);
        matches_f = new Matches_F(this);

        PAGES = new Fragment[] {
                message_f,
                matches_f
        };
        mViewPager = (NonSwipeableViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new InboxPagerAdapter(getChildFragmentManager()));

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(context.getResources().getColor(R.color.photo_alert));
        tabLayout.setTabTextColors(context.getResources().getColor(R.color.gray), context.getResources().getColor(R.color.photo_alert));

        RelativeLayout backBtn = view.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    //open the chat fragment and on item click and pass your id and the other person id in which
    //you want to chat with them and this parameter is that is we move from match list or inbox list
    public void chatFragment(String senderid,String receiverid,String name,String picture,boolean is_match_exits){
        if (check_ReadStoragepermission()) {
            Chat_Activity chat_activity = new Chat_Activity();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            Bundle args = new Bundle();
            args.putString("Sender_Id",senderid);
            args.putString("Receiver_Id",receiverid);
            args.putString("picture",picture);
            args.putString("name",name);
            args.putBoolean("is_match_exits",is_match_exits);
            chat_activity.setArguments(args);
            transaction.addToBackStack(null);
            transaction.replace(R.id.MainMenuFragment, chat_activity).commit();
        }
    }



    //this method will check there is a storage permission given or not
    private boolean check_ReadStoragepermission(){
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Variables.permission_Read_data );
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    /* PagerAdapter for supplying the ViewPager with the pages (fragments) to display. */
    public class InboxPagerAdapter extends FragmentPagerAdapter {
        public InboxPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return PAGES[i];
        }

        @Override
        public int getCount() {
            return PAGES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return PAGE_TITLES[position];
        }
    }

}
