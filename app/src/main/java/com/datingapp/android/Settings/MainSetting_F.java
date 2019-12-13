package com.datingapp.android.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datingapp.android.InAppSubscription.InApp_Subscription_A;
import com.datingapp.android.Main_Menu.MainMenuActivity;
import com.datingapp.android.Profile.EditProfile.EditProfile_F;
import com.datingapp.android.R;
import com.squareup.picasso.Picasso;

public class MainSetting_F extends Fragment implements View.OnClickListener {

    View view;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_main_setting, container, false);
        context=getContext();

        ImageView userImg = view.findViewById(R.id.user_image);
        Picasso.with(context).load(MainMenuActivity.user_pic)
                .resize(100,100)
                .placeholder(context.getDrawable(R.drawable.profile_image_placeholder))
                .into(userImg);
        userImg.setOnClickListener(this);

        TextView userName = view.findViewById(R.id.user_name);
        userName.setText(MainMenuActivity.user_name + "," + MainMenuActivity.age);

        TextView purchaseStateTxt = view.findViewById(R.id.purchase_state);
        if (MainMenuActivity.purduct_purchase) {
            purchaseStateTxt.setText(context.getResources().getString(R.string.unpurchase_state));
        }else {
            purchaseStateTxt.setText(context.getResources().getString(R.string.purchase_state));
        }

        RelativeLayout showLayout = view.findViewById(R.id.show_layout);
        showLayout.setOnClickListener(this);

        RelativeLayout filterLayout = view.findViewById(R.id.filter_layout);
        filterLayout.setOnClickListener(this);

        RelativeLayout notificationLayout = view.findViewById(R.id.notification_layout);
        notificationLayout.setOnClickListener(this);

        RelativeLayout privacyLayout = view.findViewById(R.id.privacy_layout);
        privacyLayout.setOnClickListener(this);

        RelativeLayout termsLayout = view.findViewById(R.id.terms_layout);
        termsLayout.setOnClickListener(this);

        RelativeLayout restoreLayout = view.findViewById(R.id.restore_layout);
        restoreLayout.setOnClickListener(this);

        RelativeLayout reportLayout = view.findViewById(R.id.restore_layout);
        reportLayout.setOnClickListener(this);

        RelativeLayout passportLayout = view.findViewById(R.id.passport_layout);
        passportLayout.setOnClickListener(this);

        RelativeLayout purchaseLayout = view.findViewById(R.id.purchase_layout);
        purchaseLayout.setOnClickListener(this);

        ImageView twitterShareBtn = view.findViewById(R.id.twitter_share);
        twitterShareBtn.setOnClickListener(this);

        TextView deleteAccountBtn = view.findViewById(R.id.delete_account_btn);
        deleteAccountBtn.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_image:
                EditProfile_F editProfile_f = new EditProfile_F();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
                transaction.addToBackStack(null);
                transaction.replace(R.id.MainMenuFragment, editProfile_f).commit();
                break;
            case R.id.purchase_layout:
                InApp_Subscription_A inApp_subscription_a = new InApp_Subscription_A();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.MainMenuFragment, inApp_subscription_a)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.show_layout:
                break;
            case R.id.filter_layout:
                break;
            case R.id.notification_layout:
                break;
            case R.id.privacy_layout:
                break;
            case R.id.terms_layout:
                break;
            case R.id.restore_layout:
                break;
            case R.id.report_layout:
                break;
            case R.id.passport_layout:
                break;
            case R.id.twitter_share:
                break;
            case R.id.delete_account_btn:
                break;
        }
    }
}
