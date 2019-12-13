package com.datingapp.android.Main_Menu;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.datingapp.android.CodeClasses.Functions;
import com.datingapp.android.CodeClasses.Variables;
import com.datingapp.android.CodeClasses.VersionChecker;
import com.datingapp.android.R;
import com.datingapp.android.common.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainMenuActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private MainMenuFragment mainMenuFragment;
    long mBackPressed;


    public static SharedPreferences sharedPreferences;
    public static String user_id;
    public static String user_name;
    public static String user_pic;
    public static String birthday;
    public static String age;
    public static String token;


    DatabaseReference rootref;

    BillingProcessor billingProcessor;

    public static boolean purduct_purchase=false;


    public static String action_type="none";
    public static String receiverid="none";
    public static String title="none";
    public static String Receiver_pic="none";

    public  static  MainMenuActivity mainMenuActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Common.currentActivity = this;

        mainMenuActivity=this;
        // here we will make the static variable of user info which will user in different
        // places during using app
        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
        user_id = sharedPreferences.getString(Variables.uid, "null");
        user_name = sharedPreferences.getString(Variables.f_name, "") + " " +
                sharedPreferences.getString(Variables.l_name, "");
        birthday=sharedPreferences.getString(Variables.birth_day,"");
        age=sharedPreferences.getString(Variables.age,"");
        user_pic=sharedPreferences.getString(Variables.u_pic1,"null");
        token=sharedPreferences.getString(Variables.device_token,"null");
        rootref= FirebaseDatabase.getInstance().getReference();


        if(getIntent().hasExtra("action_type")){
            action_type=getIntent().getExtras().getString("action_type");
            receiverid=getIntent().getExtras().getString("receiverid");
            title=getIntent().getExtras().getString("title");
            Receiver_pic=getIntent().getExtras().getString("icon");
        }


        // we will get the user billing status in local if it not present inn local then we will intialize the billing

            // check user if subscript or not both status we will save
            billingProcessor = new BillingProcessor(this, Variables.licencekey, this);
            billingProcessor.initialize();


        if (savedInstanceState == null) {

            initScreen();

        } else {
            mainMenuFragment = (MainMenuFragment) getSupportFragmentManager().getFragments().get(0);
        }


        // get version of currently running app
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Variables.versionname=packageInfo.versionName;


    }


    private void initScreen() {
        mainMenuFragment = new MainMenuFragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mainMenuFragment)
                .commit();

        findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(MainMenuActivity.this);
            }
        });
    }

    // onstart we will save the latest token into the firebase
    @Override
    protected void onStart() {
        super.onStart();
        rootref.child("Users").child(user_id).child("token").setValue(token);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Check_version();

    }

    @Override
    public void onBackPressed() {
        if (!mainMenuFragment.onBackPressed()) {
            int count = this.getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                if (mBackPressed + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                    return;
                } else {
//                    Toast.makeText(getBaseContext(), "Tap Again To Exit", Toast.LENGTH_SHORT).show();
                    mBackPressed = System.currentTimeMillis();

                }
            } else {
                super.onBackPressed();
            }
        }

    }






    // below all method is belong to get the info that user is subscribe our app or not
    // keep in mind it is a listener so we will close the listener after checking in onBillingInitialized method
    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        sharedPreferences.edit().putBoolean(Variables.ispuduct_puchase,true).commit();
        purduct_purchase=true;
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {
        if(billingProcessor.loadOwnedPurchasesFromGoogle()){
            if(billingProcessor.isSubscribed(Variables.product_ID)){
                sharedPreferences.edit().putBoolean(Variables.ispuduct_puchase,true).commit();
                purduct_purchase=true;
                billingProcessor.release();
            }else {
                sharedPreferences.edit().putBoolean(Variables.ispuduct_puchase,false).commit();
                purduct_purchase=false;
            }
    }
}

    @Override
    protected void onDestroy() {

        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }


    // this method will get the version of app from play store and complate it with our present app version
    // and show the update message to update the application
    public void Check_version(){

        VersionChecker versionChecker = new VersionChecker(this);
        versionChecker.execute();


    }




}
