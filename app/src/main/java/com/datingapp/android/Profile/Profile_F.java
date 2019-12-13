package com.datingapp.android.Profile;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.datingapp.android.Main_Menu.MainMenuActivity;
import com.datingapp.android.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.datingapp.android.Profile.EditProfile.EditProfile_F;
import com.datingapp.android.Profile.Profile_Details.Profile_Details_F;
import com.datingapp.android.R;
import com.datingapp.android.Settings.Setting_F;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile_F extends RootFragment {

    View view;
    Context context;

     public static ImageView profile_image;
      TextView user_name;
      public static TextView age;


    LinearLayout setting_layout,edit_profile_layout;



    public Profile_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_profile, container, false);
        context=getContext();


        edit_profile_layout=view.findViewById(R.id.edit_profile_layout);
        edit_profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editprofile();
            }
        });



        setting_layout=view.findViewById(R.id.setting_layout);
        setting_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting_profile();
            }
        });


        profile_image=view.findViewById(R.id.profile_image);
        user_name=view.findViewById(R.id.user_name);
        age=view.findViewById(R.id.age);


        // show the picture age and username of the user
        Picasso.with(context).load(MainMenuActivity.user_pic)
                .resize(200,200)
                .centerCrop()
                .into(profile_image);

        user_name.setText(MainMenuActivity.user_name);
        age.setText(" "+MainMenuActivity.birthday);


        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile_detail();
            }
        });



        return view;
    }


    // open the view of Edit profile where 6 pic is visible
        public void Profile_detail(){

        Profile_Details_F profile_details_f = new Profile_Details_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, profile_details_f).commit();

    }



    // open the view of Edit profile where 6 pic is visible
    public void Editprofile(){
        EditProfile_F editProfile_f = new EditProfile_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, editProfile_f).commit();
    }


    // open the view of Edit profile where 6 pic is visible
    public void Setting_profile(){
        Setting_F setting_f = new Setting_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, setting_f).commit();
    }





}
