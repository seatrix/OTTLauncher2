package com.histar.hslauncher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class CustomBar extends LinearLayout implements View.OnFocusChangeListener, View.OnClickListener, View.OnKeyListener {

    private static final int CUSTOM_APP_NUM = 32;
    private Context mContext;
    private ImageView[] views;
    //private ArrayList<String> mCustomApp;
//    private LinearLayout mLayout;
    //private ArrayList<CustomAppInfo> lstCustomApp;
    int mFocusIndex = 0;
    Preferences mPre;
    private View[] mCustomItem;
    //private String[] mCustomPackage;
    private CustomAppInfo[] mlstCustomApp;

    public CustomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.custom_bar, this);
        mContext = context;

        mCustomItem = new View[] {
                (RelativeLayout) findViewById(R.id.item1),
                (RelativeLayout) findViewById(R.id.item2),
                (RelativeLayout) findViewById(R.id.item3),
                (RelativeLayout) findViewById(R.id.item4),
                (RelativeLayout) findViewById(R.id.item5),
                (RelativeLayout) findViewById(R.id.item6),
                (RelativeLayout) findViewById(R.id.item7),
                (RelativeLayout) findViewById(R.id.item8),
                (RelativeLayout) findViewById(R.id.item9),
                (RelativeLayout) findViewById(R.id.item10),
                (RelativeLayout) findViewById(R.id.item11),
                (RelativeLayout) findViewById(R.id.item12),
                (RelativeLayout) findViewById(R.id.item13),
                (RelativeLayout) findViewById(R.id.item14),
                (RelativeLayout) findViewById(R.id.item15),
                (RelativeLayout) findViewById(R.id.item16),
                (RelativeLayout) findViewById(R.id.item17),
                (RelativeLayout) findViewById(R.id.item18),
                (RelativeLayout) findViewById(R.id.item19),
                (RelativeLayout) findViewById(R.id.item20),
                (RelativeLayout) findViewById(R.id.item21),
                (RelativeLayout) findViewById(R.id.item22),
                (RelativeLayout) findViewById(R.id.item23),
                (RelativeLayout) findViewById(R.id.item24),
                (RelativeLayout) findViewById(R.id.item25),
                (RelativeLayout) findViewById(R.id.item26),
                (RelativeLayout) findViewById(R.id.item27),
                (RelativeLayout) findViewById(R.id.item28),
                (RelativeLayout) findViewById(R.id.item29),
                (RelativeLayout) findViewById(R.id.item30),
                (RelativeLayout) findViewById(R.id.item31),
                (RelativeLayout) findViewById(R.id.item32)
        };

        views = new ImageView[] {
                (ImageView) findViewById(R.id.custom_image1),
                (ImageView) findViewById(R.id.custom_image2),
                (ImageView) findViewById(R.id.custom_image3),
                (ImageView) findViewById(R.id.custom_image4),
                (ImageView) findViewById(R.id.custom_image5),
                (ImageView) findViewById(R.id.custom_image6),
                (ImageView) findViewById(R.id.custom_image7),
                (ImageView) findViewById(R.id.custom_image8),
                (ImageView) findViewById(R.id.custom_image9),
                (ImageView) findViewById(R.id.custom_image10),
                (ImageView) findViewById(R.id.custom_image11),
                (ImageView) findViewById(R.id.custom_image12),
                (ImageView) findViewById(R.id.custom_image13),
                (ImageView) findViewById(R.id.custom_image14),
                (ImageView) findViewById(R.id.custom_image15),
                (ImageView) findViewById(R.id.custom_image16),
                (ImageView) findViewById(R.id.custom_image17),
                (ImageView) findViewById(R.id.custom_image18),
                (ImageView) findViewById(R.id.custom_image19),
                (ImageView) findViewById(R.id.custom_image20),
                (ImageView) findViewById(R.id.custom_image21),
                (ImageView) findViewById(R.id.custom_image22),
                (ImageView) findViewById(R.id.custom_image23),
                (ImageView) findViewById(R.id.custom_image24),
                (ImageView) findViewById(R.id.custom_image25),
                (ImageView) findViewById(R.id.custom_image26),
                (ImageView) findViewById(R.id.custom_image27),
                (ImageView) findViewById(R.id.custom_image28),
                (ImageView) findViewById(R.id.custom_image29),
                (ImageView) findViewById(R.id.custom_image30),
                (ImageView) findViewById(R.id.custom_image31),
                (ImageView) findViewById(R.id.custom_image32)
        };

        for (View v : mCustomItem) {
            v.setOnClickListener(this);
            v.getBackground().setAlpha(0);
            v.setOnFocusChangeListener(this);
            v.setOnKeyListener(this);
        }

        mlstCustomApp = new CustomAppInfo[CUSTOM_APP_NUM];

        //test();
        mPre = new Preferences(mContext);
        updateViews();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("custom_app_selected");
        mContext.registerReceiver(mReceiver, intentFilter);
    }
/*
    private void test() {
        //////////////////////////////////begin test
        String[] aaa = new String[] {"com.android.camera2", "com.android.camera2"};

        Preferences p = new Preferences(mContext);
        p.writeCustomAppList(aaa);

        String[] bbb = p.readCustomAppList();
        for (String aBbb : bbb) {
            Log.i("xxx", aBbb);
        }
        ///////////////////////////////////end test
    }
*/
    @Override
    public void onClick(View v) {
        String packageName = "";
        for (int i = 0; i < CUSTOM_APP_NUM; i ++) {
            if(mCustomItem[i] == v) {
                packageName = mlstCustomApp[i].strPackageName;
                mFocusIndex = i;
                break;
            }
        }

        if(packageName == null || packageName.isEmpty())
            return;

        if (packageName.equals("+")) {
            Intent intent = new Intent(mContext, SelectAppActivity.class);
            /*Intent intent = new Intent();
            intent.setClass(mContext,SelectAppActivity.class);*/
            Bundle bundle = new Bundle();
            bundle.putString("package_name", packageName);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        } else if(packageName.equals("com.tv.clean"))
        {
            Intent intent = new Intent();
            ComponentName com = new ComponentName("com.tv.clean","com.tv.clean.HomeAct");
            intent.setComponent(com);
            //Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(intent != null)
                mContext.startActivity(intent);
        }else {
            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
            if(intent != null)
                mContext.startActivity(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            for (int i = 0; i < CUSTOM_APP_NUM; i ++) {
                if(mCustomItem[i] == v) {
                    mFocusIndex = i;
                }
            }

            v.bringToFront();
            v.getBackground().setAlpha(255);
            v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            //v.setPadding(3, 3, 3, 3);
            //v.setBackgroundResource(R.drawable.border_img);

            // Scroll
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            while (v.getX() < 0) {
                for (View vi : mCustomItem) {
                    vi.setX(vi.getX() + 185);
                }
            }
            while (v.getX() + 300 > width) {
                for (View vi : mCustomItem) {
                    vi.setX(vi.getX() - 185);
                }
            }
        } else {
            v.getBackground().setAlpha(0);
            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            //v.setPadding(0, 0, 0, 0);
            //v.setBackground(null);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("custom_app_selected")) {
                updateViews();
            }
        }
    };
/*
    private void readCustomAppInfo() {
        String[] lstPackageName = mPre.readCustomAppListShow();
        int length = (CUSTOM_APP_NUM < lstPackageName.length) ? CUSTOM_APP_NUM : lstPackageName.length;
        for(int i = 0; i < length; i ++) {
            mlstCustomApp[i] = getAppInfo(lstPackageName[i]);
            Log.i("xxx", "" + i + " " + mlstCustomApp[i].strPackageName);
        }
    }
*/
    private void readCustomAppInfo() {
        String[] lstPackageName = mPre.readCustomAppListShow();
        for (int i = 0; i < CUSTOM_APP_NUM; i ++) {
            if (i < lstPackageName.length) {
                mlstCustomApp[i] = getAppInfo(lstPackageName[i]);
            } else {
                mlstCustomApp[i] = new CustomAppInfo();
            }
        }
    }

    private CustomAppInfo getAppInfo(String strPackageName) {
        CustomAppInfo info = new CustomAppInfo();
        if(strPackageName.equals("+")) {
            info.strPackageName = strPackageName;
            //info.strPackageName = strPackageName;
        } else {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo packageinfo = null;
            try {
                packageinfo = pm.getPackageInfo(strPackageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (packageinfo == null) {
                return info;
            }
            info.strPackageName = strPackageName;
            //info.strClassName = packageinfo.applicationInfo.className;
            info.strAppName = packageinfo.applicationInfo.loadLabel(pm).toString();
            info.icon = packageinfo.applicationInfo.loadIcon(pm);
        }
        return info;
    }

    private void updateViews() {
        readCustomAppInfo();

        for (View v : mCustomItem) {
            v.setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < CUSTOM_APP_NUM; i ++) {
            if(i == 0) {
                mCustomItem[i].setVisibility(View.VISIBLE);
                continue;
            }
            if(mlstCustomApp[i] == null) {
                mCustomItem[i].setVisibility(View.INVISIBLE);
                break;
            }
            if(mlstCustomApp[i].strPackageName == null)
                break;
            if(mlstCustomApp[i].strPackageName.isEmpty()) {
                mCustomItem[i].setVisibility(View.INVISIBLE);
            } else {
                mCustomItem[i].setVisibility(View.VISIBLE);
                if (mlstCustomApp[i].strPackageName.equals("+")) {
                    views[i].setImageDrawable(mContext.getResources().getDrawable(R.mipmap.add));
                } else {
                    views[i].setImageDrawable(mlstCustomApp[i].icon);
                }
            }
        }
        mCustomItem[mFocusIndex].requestFocus();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN) {
            for(int i = 0; i < mCustomItem.length; i ++) {
                if(i != 0 && mCustomItem[i] == v) {
                    mFocusIndex = i;
                    String packageName = mlstCustomApp[i].strPackageName;
                    Intent intent = new Intent(mContext, SelectAppActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("package_name", packageName);
                    bundle.putInt("custom_index", i);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            }
        }
        return false;
    }

    private class CustomAppInfo {
        public String strPackageName;
        //public String strClassName;
        public String strAppName;
        public Drawable icon;
    }
}
