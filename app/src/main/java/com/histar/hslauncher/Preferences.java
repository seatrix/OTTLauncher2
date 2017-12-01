package com.histar.hslauncher;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class Preferences extends ContextWrapper {

    private static final String Preferences_Name = "MainActivity";
    private static final String CUSTOM_APP_LIST = "CustomAppList";
    private static final String  KODI_STATE ="KodiState";
    private static final String  NET_STATE ="InternetState";
    private static final String PACKAGE_NAME_1 = "com.tv.clean";
    private SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;

    public Preferences(Context base) {
        super(base);
        mSharedPreferences = getSharedPreferences(Preferences_Name, Activity.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }
/*
    void writeCustomAppList(ArrayList<String> lstApp) {
        String strCustomApp = "";
        for (String strApp : lstApp) {
            strCustomApp += strApp;
            strCustomApp += " ";
        }
        mEditor.putString(CUSTOM_APP_LIST, strCustomApp);
        mEditor.commit();
    }
*/
    void writeCustomAppList(String[] lstApp) {
        String strCustomApp = "";
        for (String strApp : lstApp) {
            strCustomApp += strApp;
            strCustomApp += ",";
        }
        mEditor.putString(CUSTOM_APP_LIST, strCustomApp);
        mEditor.commit();
    }

    String[] readCustomAppListShow() {
        String strAppList = PACKAGE_NAME_1;
        strAppList += "," + mSharedPreferences.getString(CUSTOM_APP_LIST, "");
        strAppList += "+";
        //Log.i("xxx", strAppList);
        return strAppList.split(",");
    }

    String[] readCustomAppList() {
        String strAppList = mSharedPreferences.getString(CUSTOM_APP_LIST, "");
        if(strAppList == null || !strAppList.contains(","))
            return new String[0];
        else
            return strAppList.split(",");
    }

    int getKodiStatus()
    {
        return mSharedPreferences.getInt(KODI_STATE, 0);
    }

    void setKodiState(int status)
    {
        mEditor.putInt(KODI_STATE, status);
        mEditor.commit();
    }

    int getInternetStatus()
    {
        return mSharedPreferences.getInt(NET_STATE,0);
    }

    void setInternetStatus(int status)
    {
        mEditor.putInt(NET_STATE,status);
        mEditor.commit();
    }
/*
    ArrayList<String> readCustomAppList() {
        String strAppList = mSharedPreferences.getString(CUSTOM_APP_LIST, "");
        if (strAppList == null) {
            return new ArrayList<>();
        } else {
            String[] lstApp = strAppList.split(" ");
            ArrayList<String> lstRet = new ArrayList<>(lstApp.length);
            Collections.addAll(lstRet, lstApp);
            return lstRet;
        }
    }*/
}
