package com.histar.hslauncher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SelectAppActivity extends Activity implements View.OnFocusChangeListener, View.OnClickListener {

    private static final int ICON_SIZE = 64;
    ArrayList<AppItem> mlstAllApp;
    LinearLayout scrollLayout;
    private String prePackageName;
    private int mCustomIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_app_activity);

        setTitle(R.string.select_app_title);

        Bundle bundle = getIntent().getExtras();
        prePackageName = bundle.getString("package_name");
        mCustomIndex = bundle.getInt("custom_index");

        mlstAllApp = new ArrayList<AppItem>();

        scrollLayout = (LinearLayout) findViewById(R.id.customItemLayout);

        getAllApp();
        updateItems();
    }

    @Override
    public void onClick(View v) {
        AppItem item = (AppItem) v;
        if(item == null)
            return;
        replaceCustomApp(item.getPackageName());
        Intent i = new Intent("custom_app_selected");
        sendBroadcast(i);
        finish();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            v.getBackground().setAlpha(255);
        } else {
            v.getBackground().setAlpha(0);
        }
    }

    private void getAllApp() {
        mlstAllApp.clear();
        PackageManager packageManager = this.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> tempAppList = packageManager.queryIntentActivities(mainIntent, 0);

        if(!prePackageName.equals("+")) {
            AppItem delItem = new AppItem(this, resizeDrawable(getResources().getDrawable(R.mipmap.del_custom_item)), getString(R.string.del_custom_item));
            delItem.setPackageName("-");
            mlstAllApp.add(delItem);
        }

        for(ResolveInfo info : tempAppList) {
            AppItem item = new AppItem(this, resizeDrawable(info.loadIcon(packageManager)), info.loadLabel(packageManager).toString());
            item.setPackageName(info.activityInfo.packageName);
            mlstAllApp.add(item);
        }
    }

    private void updateItems() {
        for (AppItem item : mlstAllApp) {
            scrollLayout.addView(item);
            item.setClickable(true);
            item.setFocusable(true);
            item.setFocusableInTouchMode(true);
            item.setOnClickListener(this);
            item.setOnFocusChangeListener(this);
        }

        if(mlstAllApp.size() > 0)
            mlstAllApp.get(0).requestFocus();
    }

    private Drawable resizeDrawable(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, ICON_SIZE, ICON_SIZE, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    private void replaceCustomApp(String strCurName) {
        Preferences p = new Preferences(this);
        String[] preCustomAppList = p.readCustomAppList();

        if(prePackageName.equals("+")) {
            String[] newCustomAppList = new String[preCustomAppList.length + 1];
            for (int i = 0; i < preCustomAppList.length; i ++) {
                newCustomAppList[i] = preCustomAppList[i];
            }
            newCustomAppList[preCustomAppList.length] = strCurName;
            p.writeCustomAppList(newCustomAppList);
        } else {
            if(strCurName.equals("-")) {
                String[] newCustomAppList = new String[preCustomAppList.length - 1];
                for (int i = 0; i < preCustomAppList.length; i ++) {
                    if(i < mCustomIndex - 1) {
                        newCustomAppList[i] = preCustomAppList[i];
                    } else if(i > mCustomIndex - 1) {
                        newCustomAppList[i - 1] = preCustomAppList[i];
                    }
                }
                p.writeCustomAppList(newCustomAppList);
            } else {
                String[] newCustomAppList = new String[preCustomAppList.length];
                for (int i = 0; i < preCustomAppList.length; i ++) {
                    if(mCustomIndex == i + 1) {
                        newCustomAppList[i] = strCurName;
                    } else {
                        newCustomAppList[i] = preCustomAppList[i];
                    }
                }
                p.writeCustomAppList(newCustomAppList);
            }
        }
    }

    private class AppItem extends LinearLayout {
        private ImageView image = null;
        private TextView label = null;
        private String mPackageName;

        public AppItem(Context context, Drawable icon, String text) {
            super(context);
            image = new ImageView(context);
            label = new TextView(context);
            setOrientation(LinearLayout.HORIZONTAL);

            label.setSingleLine(true);
            label.setText(text);
            label.setTextSize(24);
            setBackgroundColor(Color.BLUE);
            getBackground().setAlpha(0);
            image.setImageDrawable(icon);

            addView(image);
            addView(label);
        }

        void setPackageName(String strPackageName) {mPackageName = strPackageName;}

        String getPackageName() {return mPackageName;}
    }
}