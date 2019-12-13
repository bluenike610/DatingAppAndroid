package com.datingapp.android.InAppSubscription;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.datingapp.android.CodeClasses.Variables;
import com.datingapp.android.Likes.Likes_F;
import com.datingapp.android.Main_Menu.MainMenuActivity;
import com.datingapp.android.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.datingapp.android.R;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;
import static com.datingapp.android.Main_Menu.MainMenuFragment.likeNumTxt;
import static com.datingapp.android.Main_Menu.MainMenuFragment.likeTabImg;

public class InApp_Subscription_A extends RootFragment implements BillingProcessor.IBillingHandler, View.OnClickListener {

    BillingProcessor bp;
    public IOSDialog iosDialog;
    SharedPreferences sharedPreferences;
    View view;
    Context context;
    Button purchase_btn;

    TextView Goback;

    ImageView inappIntroImgBtn;
    TextView inappIntroTxt;
    ImageView inappIntroIndicate;
    RelativeLayout inappItem1, inappItem2, inappItem3;
    TextView inappItemTxt1, inappItemTxt11, inappItemTxt12;
    TextView inappItemTxt2, inappItemTxt21, inappItemTxt22;
    TextView inappItemTxt3, inappItemTxt31, inappItemTxt32;

    private int selectedInappIntroIndex = 0;
    private int selectedInappItemIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate
        // the layout for this fragment
        view= inflater.inflate(R.layout.activity_in_app_subscription, container, false);
        context=getContext();

        // get the sharepreference
        sharedPreferences=context.getSharedPreferences(Variables.pref_name,MODE_PRIVATE);

        purchase_btn=view.findViewById(R.id.purchase_btn);
        purchase_btn.setOnClickListener(this);


        Goback=view.findViewById(R.id.Goback);
        Goback.setOnClickListener(this);

        inappIntroImgBtn = view.findViewById(R.id.inappIntroImg);
        inappIntroImgBtn.setOnClickListener(this);

        inappIntroTxt = view.findViewById(R.id.inappIntroTxt);
        inappIntroIndicate = view.findViewById(R.id.inappIndicate);

        inappItem1 = view.findViewById(R.id.inappItem1);
        inappItem1.setOnClickListener(this);
        inappItem1.setBackground(getResources().getDrawable(R.drawable.inapp_item_back));
        inappItemTxt1 = view.findViewById(R.id.monthTxt1);
        inappItemTxt1.setTextColor(getResources().getColor(R.color.photo_alert));
        inappItemTxt11 = view.findViewById(R.id.monthTxt11);
        inappItemTxt11.setTextColor(getResources().getColor(R.color.photo_alert));
        inappItemTxt12 = view.findViewById(R.id.monthTxt12);
        inappItemTxt12.setTextColor(getResources().getColor(R.color.photo_alert));

        inappItem2 = view.findViewById(R.id.inappItem2);
        inappItem2.setOnClickListener(this);
        inappItem2.setBackgroundColor(getResources().getColor(R.color.gray));
        inappItemTxt2 = view.findViewById(R.id.monthTxt2);
        inappItemTxt2.setTextColor(getResources().getColor(R.color.black));
        inappItemTxt21 = view.findViewById(R.id.monthTxt21);
        inappItemTxt21.setTextColor(getResources().getColor(R.color.black));
        inappItemTxt22 = view.findViewById(R.id.monthTxt22);
        inappItemTxt22.setTextColor(getResources().getColor(R.color.black));

        inappItem3 = view.findViewById(R.id.inappItem3);
        inappItem3.setOnClickListener(this);
        inappItem3.setBackgroundColor(getResources().getColor(R.color.gray));
        inappItemTxt3 = view.findViewById(R.id.monthTxt3);
        inappItemTxt3.setTextColor(getResources().getColor(R.color.black));
        inappItemTxt31 = view.findViewById(R.id.monthTxt31);
        inappItemTxt31.setTextColor(getResources().getColor(R.color.black));
        inappItemTxt32 = view.findViewById(R.id.monthTxt32);
        inappItemTxt32.setTextColor(getResources().getColor(R.color.black));

        return view;
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.purchase_btn:
//                Puchase_item();
                MainMenuActivity.purduct_purchase=true;
                Likes_F.likeAdapter.notifyDataSetChanged();
                likeTabImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_tab));
                likeNumTxt.setTextColor(context.getResources().getColor(R.color.purchased_like));
                getActivity().onBackPressed();
                break;
            case R.id.Goback:
                Goback();
                break;
            case R.id.inappIntroImg:
                if (selectedInappIntroIndex == 0) {
                    selectedInappIntroIndex = 1;
                    inappIntroImgBtn.setImageDrawable(getResources().getDrawable(R.drawable.inapp_intro_item2));
                    inappIntroIndicate.setImageDrawable(getResources().getDrawable(R.drawable.inapp_intro_indi2));
                    inappIntroTxt.setText(getResources().getString(R.string.inapp_intro_2));
                }else if (selectedInappIntroIndex == 1) {
                    selectedInappIntroIndex = 2;
                    inappIntroImgBtn.setImageDrawable(getResources().getDrawable(R.drawable.inapp_intro_item3));
                    inappIntroIndicate.setImageDrawable(getResources().getDrawable(R.drawable.inapp_intro_indi3));
                    inappIntroTxt.setText(getResources().getString(R.string.inapp_intro_3));
                }else if (selectedInappIntroIndex == 2) {
                    selectedInappIntroIndex = 0;
                    inappIntroImgBtn.setImageDrawable(getResources().getDrawable(R.drawable.inapp_intro_item1));
                    inappIntroIndicate.setImageDrawable(getResources().getDrawable(R.drawable.inapp_intro_indi_1));
                    inappIntroTxt.setText(getResources().getString(R.string.inapp_intro_1));
                }
                break;
            case R.id.inappItem1:
                selectedInappItemIndex = 0;
                inappItem1.setBackground(getResources().getDrawable(R.drawable.inapp_item_back));
                inappItemTxt1.setTextColor(getResources().getColor(R.color.photo_alert));
                inappItemTxt11.setTextColor(getResources().getColor(R.color.photo_alert));
                inappItemTxt12.setTextColor(getResources().getColor(R.color.photo_alert));
                inappItem2.setBackgroundColor(getResources().getColor(R.color.gray));
                inappItemTxt2.setTextColor(getResources().getColor(R.color.black));
                inappItemTxt21.setTextColor(getResources().getColor(R.color.black));
                inappItemTxt22.setTextColor(getResources().getColor(R.color.black));
                inappItem3.setBackgroundColor(getResources().getColor(R.color.gray));
                inappItemTxt3.setTextColor(getResources().getColor(R.color.black));
                inappItemTxt31.setTextColor(getResources().getColor(R.color.black));
                inappItemTxt32.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.inappItem2:
                selectedInappItemIndex = 1;
                inappItem1.setBackgroundColor(getResources().getColor(R.color.gray));
                inappItemTxt1.setTextColor(getResources().getColor(R.color.black));
                inappItemTxt11.setTextColor(getResources().getColor(R.color.black));
                inappItemTxt12.setTextColor(getResources().getColor(R.color.black));
                inappItem2.setBackground(getResources().getDrawable(R.drawable.inapp_item_back));
                inappItemTxt2.setTextColor(getResources().getColor(R.color.photo_alert));
                inappItemTxt21.setTextColor(getResources().getColor(R.color.photo_alert));
                inappItemTxt22.setTextColor(getResources().getColor(R.color.photo_alert));
                inappItem3.setBackgroundColor(getResources().getColor(R.color.gray));
                inappItemTxt3.setTextColor(getResources().getColor(R.color.black));
                inappItemTxt31.setTextColor(getResources().getColor(R.color.black));
                inappItemTxt32.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.inappItem3:
                selectedInappItemIndex = 2;
                inappItem1.setBackgroundColor(getResources().getColor(R.color.gray));
                inappItemTxt1.setTextColor(getResources().getColor(R.color.black));
                inappItemTxt11.setTextColor(getResources().getColor(R.color.black));
                inappItemTxt12.setTextColor(getResources().getColor(R.color.black));
                inappItem2.setBackgroundColor(getResources().getColor(R.color.gray));
                inappItemTxt2.setTextColor(getResources().getColor(R.color.black));
                inappItemTxt21.setTextColor(getResources().getColor(R.color.black));
                inappItemTxt22.setTextColor(getResources().getColor(R.color.black));
                inappItem3.setBackground(getResources().getDrawable(R.drawable.inapp_item_back));
                inappItemTxt3.setTextColor(getResources().getColor(R.color.photo_alert));
                inappItemTxt31.setTextColor(getResources().getColor(R.color.photo_alert));
                inappItemTxt32.setTextColor(getResources().getColor(R.color.photo_alert));
                break;
        }
    }


    public void initlize_billing(){

        // intialize the billing process
        iosDialog = new IOSDialog.Builder(context)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

//        iosDialog.show();


        bp = new BillingProcessor(context, Variables.licencekey, this);
        bp.initialize();


    }


    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

        // if the user is subcripbe successfully then we will store the data in local and also call the api
        sharedPreferences.edit().putBoolean(Variables.ispuduct_puchase,true).commit();
        MainMenuActivity.purduct_purchase=true;
        Call_Api_For_update_purchase();

    }


    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {
        // on billing intialize we will get the data from google
        if(bp.loadOwnedPurchasesFromGoogle()){

            // check user is already subscribe or not
        if(bp.isSubscribed(Variables.product_ID)){
            // if already subscribe then we will change the static variable and goback
            MainMenuActivity.purduct_purchase=true;
            iosDialog.cancel();
            Call_Api_For_update_purchase();
        }
        else {

            iosDialog.cancel();
        }
        }
    }



    // when we click the continue btn this method will call
    public void Puchase_item() {
        boolean isAvailable = BillingProcessor.isIabServiceAvailable(getActivity());
        if(isAvailable)
        bp.subscribe(getActivity(),Variables.product_ID);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("responce", "onActivity Result Code : " + resultCode);
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation anim= MoveAnimation.create(MoveAnimation.UP, enter, 300);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    initlize_billing();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            return anim;

        } else {
            return MoveAnimation.create(MoveAnimation.DOWN, enter, 300);
        }
    }


    // on destory we will release the billing process
    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }


    public void Goback() {
        getActivity().onBackPressed();
    }


    // when user subscribe the app then this method will call that will store the status of user
    // into the database
    private void Call_Api_For_update_purchase() {
        iosDialog.show();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.update_purchase_Status, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        String respo=response.toString();
                        Log.d("responce",respo);
                        iosDialog.cancel();
                        Goback();


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
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
