package com.datingapp.android.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.datingapp.android.R;
import com.datingapp.android.myapplication.MyApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Commonクラス
 */

public class Common {
    //　共通

    /** Commonクラスのオブジェクト */
    public static Common cm = new Common();

    /** 現在のActivityを保存するオブジェクト */
    public static Activity currentActivity = null;

    /** MyApplicationを保存するオブジェクト */
    public static MyApplication myApp;

    public static String fcmToken = null;
    public static String roomId = "";
    public static String roomName = "";

    /**
     *
     * アラートを表示する。
     * */
    public void showAlertDlg(String title, String msg, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(currentActivity);
        alert.setTitle(title);
        alert.setMessage(msg);
        alert.setPositiveButton(currentActivity.getResources().getString(R.string.OK), positiveListener);
        if (negativeListener != null) alert.setNegativeButton(currentActivity.getResources().getString(R.string.Cancel), negativeListener);
        alert.create();
        alert.show();
    }

    /**
     *
     * 必要な許可を確認する
     *
     * @param permissionsToRequest 必要な許可のリスト
     *
     * @return ArrayList 可能な許容のリスト
     * */
    public ArrayList checkPermissions(ArrayList<String> permissionsToRequest) {
        ArrayList<String> permissionsRejected = new ArrayList();
        for (String perms : permissionsToRequest) {
            if (!hasPermission(perms)) {
                permissionsRejected.add(perms);
            }
        }
        return permissionsRejected;
    }

    /**
     *
     * 許可の状態をを判断する
     *
     * @return boolean 許可の状態 true / false
     * */
    public boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (currentActivity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    public boolean hasInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    /**
     *
     * Androidのバージョンを判断する
     *
     * @return boolean Android6.0より低いバージョンの場合 false / true
     * */
    public boolean canAskPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return true;
        else return false;
    }

    public void startSound(String filename) {
        AssetFileDescriptor afd = null;
        try {
            afd = currentActivity.getResources().getAssets().openFd(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaPlayer player = new MediaPlayer();
        try {
            assert afd != null;
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();
    }
}
