package com.datingapp.android.CodeClasses;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by AQEEL on 10/23/2018.
 */

public class Variables {

    public static String pref_name="pref_name";
    public static String f_name="f_name";
    public static String l_name="l_name";
    public static String birth_day="birth_day";
    public static String age="age";
    public static String gender="gender";
    public static String about="about_me";
    public static String job="job";
    public static String uid="uid";
    public static String u_pic1="u_pic1";
    public static String u_pic2="u_pic2";
    public static String u_pic3="u_pic3";
    public static String u_pic4="u_pic4";
    public static String u_pic5="u_pic5";
    public static String u_pic6="u_pic6";
    public static String islogin="islogin";

    public static String current_Lat ="current_Lat";
    public static String current_Lon ="current_Lon";

    public static String seleted_Lat ="seleted_Lat";
    public static String selected_Lon ="selected_Lon";
    public static String is_seleted_location_selected ="is_seleted_location_selected";

    public static String selected_location_string ="selected_location_string";


    public static String device_token="device_token";
    public static String ispuduct_puchase="ispuduct_puchase";

    public static String versionname="1.0";

    public static boolean is_reload_users =false;
    public static String show_me="show_me";
    public static String max_distance="max_distance";
    public static String max_age="max_age";
    public static String show_me_on_binder="show_me_on_tinder";


    public static int default_distance=10000;
    public static int default_age=60;



    public static int permission_camera_code=786;
    public static int permission_write_data=788;
    public static int permission_Read_data=789;
    public static int permission_Recording_audio=790;




    public static String Pic_firstpart="https://graph.facebook.com/";
    public static String Pic_secondpart="/picture?width=500&width=500";

    public static String Pic_firstpart_200="https://graph.facebook.com/";
    public static String Pic_secondpart_200="/picture?width=200&width=200";

    public static String gif_firstpart="https://media.giphy.com/media/";
    public static String gif_secondpart="/100w.gif";

    public static String gif_firstpart_chat="https://media.giphy.com/media/";
    public static String gif_secondpart_chat="/200w.gif";




  


    public static String gif_api_key1="giphy_api_key";

    // Bottom two variable Related with in App Subscription
    //First step get licencekey
    public static String licencekey="play_store_licence_key";

    //create the Product id or in app subcription id
    public static String product_ID="playstore_subscription_id";



    public static SimpleDateFormat df =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);

    public static SimpleDateFormat df2 =
            new SimpleDateFormat("dd-MM-yyyy HH:mmZZ", Locale.ENGLISH);




    public static int Select_image_from_gallry_code=3;

   
//    public static String domain="http://domain_url_here";
//    public static String domain="http://192.168.207.85/datingapp/api/index.php?p=";
    public static String domain="http://160.16.208.69/datingapp/api/index.php?p=";


    public static String SignUp=domain+"signup";

    public static String Edit_profile=domain+"edit_profile";

    public static String getUserInfo=domain+"getUserInfo";

    public static String uploadImages=domain+"uploadImages";

    public static String deleteImages=domain+"deleteImages";

    public static String userNearByMe=domain+"userNearByMe";

    public static String flat_user=domain+"flat_user";

    public static String likeMe=domain+"likeMe";

    public static String myMatch=domain+"myMatch";

    public static String firstchat=domain+"firstchat";

    public static String unMatch=domain+"unMatch";

    public static String show_or_hide_profile=domain+"show_or_hide_profile";

    public static String update_purchase_Status=domain+"update_purchase_Status";

    public static String deleteAccount=domain+"deleteAccount";

    public static String sendPushNotification=domain+"sendPushNotification";



}
