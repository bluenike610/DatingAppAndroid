package com.datingapp.android.Main_Menu;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import com.datingapp.android.Likes.Likes_F;
import com.datingapp.android.Settings.MainSetting_F;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datingapp.android.Chat.Chat_Activity;
import com.datingapp.android.CodeClasses.Functions;
import com.datingapp.android.Inbox.Inbox_F;
import com.datingapp.android.Main_Menu.RelateToFragment_OnBack.OnBackPressListener;
import com.datingapp.android.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.datingapp.android.Profile.Profile_F;
import com.datingapp.android.R;
import com.datingapp.android.Users.Users_F;


public class MainMenuFragment extends RootFragment implements View.OnClickListener {

    protected TabLayout tabLayout;

    protected Custom_ViewPager pager;

    private ViewPagerAdapter adapter;
    Context context;

    public static TextView likeNumTxt;
    public static ImageView likeTabImg;

    public MainMenuFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        context=getContext();
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        pager = view.findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(2);
        pager.setPagingEnabled(false);
        view.setOnClickListener(this);

        RelativeLayout profileBtn = view.findViewById(R.id.profileBtn);
        profileBtn.setOnClickListener(this);

        RelativeLayout inboxBtn = view.findViewById(R.id.inboxBtn);
        inboxBtn.setOnClickListener(this);

        likeNumTxt = view.findViewById(R.id.likeNumTxt);

        return view;
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.profileBtn:
                showProfileFragment();
                break;
            case R.id.inboxBtn:
                showInboxFragment();
                break;
        }
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Note that we are passing childFragmentManager, not FragmentManager
        adapter = new ViewPagerAdapter(getResources(), getChildFragmentManager());
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        setupTabIcons();

    }



    public boolean onBackPressed() {
        // currently visible tab Fragment
        OnBackPressListener currentFragment = (OnBackPressListener) adapter.getRegisteredFragment(pager.getCurrentItem());

        if (currentFragment != null) {
            // lets see if the currentFragment or any of its childFragment can handle onBackPressed
            return currentFragment.onBackPressed();
        }

        // this Fragment couldn't handle the onBackPressed call
        return false;
    }


    private void setupTabIcons() {

        View view1 = LayoutInflater.from(context).inflate(R.layout.item_tablayout, null);
        ImageView imageView1= view1.findViewById(R.id.image);
        imageView1.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_tab));
        tabLayout.getTabAt(0).setCustomView(view1);

        View view2 = LayoutInflater.from(context).inflate(R.layout.item_tablayout, null);
        likeTabImg= view2.findViewById(R.id.image);
        likeTabImg.setScaleType(ImageView.ScaleType.CENTER);
        if (MainMenuActivity.purduct_purchase)
            likeTabImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_tab));
        else
            likeTabImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_tab_d));
        tabLayout.getTabAt(1).setCustomView(view2);


//        View view3 = LayoutInflater.from(context).inflate(R.layout.item_tablayout, null);
//        ImageView imageView3= view3.findViewById(R.id.image);
//        imageView3.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_gray));
//        tabLayout.getTabAt(2).setCustomView(view3);

//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
//
//
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                View v=tab.getCustomView();
//                Functions.hideSoftKeyboard(getActivity());
//                ImageView image=v.findViewById(R.id.image);
//                switch (tab.getPosition()){
//                    case 0:
//                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_color));
//                        break;
//                    case 1:
//                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_binder_color));
//                        break;
//
////                    case 2:
////                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_color));
////                        break;
//                }
//                tab.setCustomView(v);
//            }
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                View v=tab.getCustomView();
//                ImageView image=v.findViewById(R.id.image);
//
//                switch (tab.getPosition()){
//                    case 0:
//                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_gray));
//                        break;
//                    case 1:
//                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_binder_gray));
//                        break;
////                    case 2:
////                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_gray));
////                        break;
//                }
//                tab.setCustomView(v);
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//
//        });



//        if(MainMenuActivity.action_type.equals("message")){
//            TabLayout.Tab tab=tabLayout.getTabAt(2);
//            tab.select();
//            chatFragment();
//        }
//        else if(MainMenuActivity.action_type.equals("match")){
//            TabLayout.Tab tab=tabLayout.getTabAt(2);
//            tab.select();
//        }
//        else {
            TabLayout.Tab tab=tabLayout.getTabAt(0);
            tab.select();
//          }
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final Resources resources;

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


        public ViewPagerAdapter(final Resources resources, FragmentManager fm) {
            super(fm);
            this.resources = resources;
        }


        @Override
        public Fragment getItem(int position) {
            final Fragment result;
            switch (position) {
                case 0:
                    result = new Users_F();
                    break;
                case 1:
                    result = new Likes_F();
                    break;
                default:
                    result = null;
                    break;
            }

            return result;
        }

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }


        /**
         * Get the Fragment by position
         *
         * @param position tab position of the fragment
         * @return
         */
        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

    public void showChatFragment(){
        Chat_Activity chat_activity = new Chat_Activity();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        Bundle args = new Bundle();
        args.putString("Sender_Id",MainMenuActivity.user_id);
        args.putString("Receiver_Id",MainMenuActivity.receiverid);
        args.putString("name",MainMenuActivity.title);
        args.putString("picture",MainMenuActivity.Receiver_pic);
        args.putBoolean("is_match_exits",false);
        chat_activity.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, chat_activity).commit();
    }

    public void showProfileFragment(){
        MainSetting_F profile_activity = new MainSetting_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, profile_activity).commit();
    }

    public void showInboxFragment(){
        Inbox_F inbox_activity = new Inbox_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, inbox_activity).commit();
    }



}