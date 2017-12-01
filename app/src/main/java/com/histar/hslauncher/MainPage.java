package com.histar.hslauncher;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;

public class MainPage extends RelativeLayout implements View.OnFocusChangeListener, View.OnClickListener {

    private static final String TAG = "MainPage";
    private static final int KODI_LENGTH_MIN = 39 * 1000 * 1000;
    private RelativeLayout[] items;

    public MainPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View parent = inflater.inflate(R.layout.main_page, this);
        initView(parent);
    }

    public static boolean isKodiInstalled() {
        File kodi = new File("/data/app/kodi.apk");
        if (kodi.exists() && kodi.length() > KODI_LENGTH_MIN) {
            Log.i("xxx", "Kodi.apk length = " + kodi.length());
            return true;
        } else {
            Log.i("xxx", "DELETE Kodi.apk length = " + kodi.length());
            kodi.delete();
            return false;
        }
    }

    private void initView(View parent) {
        items = new RelativeLayout[]{
                (RelativeLayout) parent.findViewById(R.id.rLayout_1),
                (RelativeLayout) parent.findViewById(R.id.rLayout_2),
                (RelativeLayout) parent.findViewById(R.id.rLayout_3),
                (RelativeLayout) parent.findViewById(R.id.rLayout_4),
                (RelativeLayout) parent.findViewById(R.id.rLayout_5),
                (RelativeLayout) parent.findViewById(R.id.rLayout_6),
                (RelativeLayout) parent.findViewById(R.id.rLayout_7)
        };

        for (RelativeLayout v : items) {
            v.setOnClickListener(this);
            v.getBackground().setAlpha(0);
            v.setOnFocusChangeListener(this);
        }
        items[0].requestFocus();
    }


    @Override
    public void onClick(View v) {
        String tag = v.getTag().toString().trim();
        try {
            if (tag.indexOf("|") == -1) {
                if (tag != null) {
                    Log.i(TAG, "package name : " + tag);
                    Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(tag);
                    if (intent != null)
                        getContext().startActivity(intent);
                }
            } else {
                String pkgs[] = tag.split("\\|");
                String packagename = pkgs[0];
                String activityname = pkgs[1];
                if (packagename.contains("kodi")) {
                    startKodi();
                } else {
                    Intent i = new Intent();
                    i.setClassName(packagename, activityname);
                    getContext().startActivity(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadKodi() {
        Preferences p = new Preferences(getContext());
        Log.i("xxx", "adasdadasd = " + p.getInternetStatus());
        if (p.getInternetStatus() == 0) {
            Toast.makeText(getContext(), R.string.internet_notice, Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(getContext(), DownloadActivity.class);
        Bundle bundle = new Bundle();
        //bundle.putString("src_path", "http://ota.openhourlab.com/ota/gecko/apk/kodi.apk");
        bundle.putString("src_path", "http://192.168.8.250/ota/kodi.apk");
        bundle.putString("dst_path", "/sdcard/kodi.apk");
        intent.putExtras(bundle);
        getContext().startActivity(intent);
    }

    private void startKodi() {
        Preferences pre = new Preferences(getContext());
        if (!isKodiInstalled()) {
            pre.setKodiState(0);
        }
        int status = pre.getKodiStatus();
        if (status == 0) {
            downloadKodi();
        } else if (status == 1) {
            new AlertDialog.Builder(getContext()).setTitle(R.string.reboot_notice1).setMessage(R.string.reboot_notice2).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Preferences pre = new Preferences(getContext());
                    pre.setKodiState(2);
                    KodiSocketClient socket1 = new KodiSocketClient();
                    socket1.writeMess("system reboot");
                    dialog.cancel();
                }
            }).show();
        } else if (status == 2) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("org.xbmc.kodi", "org.xbmc.kodi.Splash"));
            getContext().startActivity(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            v.bringToFront();
            v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            //v.setPadding(4, 4, 4, 4);
            //v.setBackgroundResource(R.drawable.border_img);
            v.getBackground().setAlpha(255);

        } else {
            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            //v.setPadding(0, 0, 0, 0);
            //v.setBackground(null);
            v.getBackground().setAlpha(0);
        }
    }
}
