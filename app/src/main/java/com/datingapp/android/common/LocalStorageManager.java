package com.datingapp.android.common;


import android.content.SharedPreferences;

import com.datingapp.android.R;

/**
 *
 * アプリ内のデータの保存を管理するクラス
 *
 */

public class LocalStorageManager {

    /**
     *
     * LoginStatusを保存する
     *
     * @param info Status
     *
     */
    public void saveLoginInfo(String info) {
        SharedPreferences sharedPreferences = Common.myApp.getSharedPreferences(String.valueOf(R.string.app_name), Common.myApp.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login_info", info);
        editor.commit();
    }

    /**
     *
     * LoginStatusを返す
     *
     * @return NSString Status
     *
     */
    public String getLoginStatus() {
        SharedPreferences sharedPreferences = Common.myApp.getSharedPreferences(String.valueOf(R.string.app_name), Common.myApp.MODE_PRIVATE);
        String syncState = sharedPreferences.getString("login_info", null);
        return syncState;
    }

    /**
     *
     * SettingStatusを保存する
     *
     * @param info Status
     *
     */
    public void saveTokenInfo(String info) {
        SharedPreferences sharedPreferences = Common.myApp.getSharedPreferences(String.valueOf(R.string.app_name), Common.myApp.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token_info", info);
        editor.commit();
    }

    /**
     *
     * SettingStatusを返す
     *
     * @return NSString Status
     *
     */
    public String getTokenStatus() {
        SharedPreferences sharedPreferences = Common.myApp.getSharedPreferences(String.valueOf(R.string.app_name), Common.myApp.MODE_PRIVATE);
        String syncState = sharedPreferences.getString("setting_info", null);
        return syncState;
    }

    /**
     *
     * SettingStatusを保存する
     *
     * @param info Status
     *
     */
    public void saveSettingInfo(String info) {
        SharedPreferences sharedPreferences = Common.myApp.getSharedPreferences(String.valueOf(R.string.app_name), Common.myApp.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("setting_info", info);
        editor.commit();
    }

    /**
     *
     * SettingStatusを返す
     *
     * @return NSString Status
     *
     */
    public String getSettingStatus() {
        SharedPreferences sharedPreferences = Common.myApp.getSharedPreferences(String.valueOf(R.string.app_name), Common.myApp.MODE_PRIVATE);
        String syncState = sharedPreferences.getString("setting_info", null);
        return syncState;
    }
}

