package com.histar.hslauncher;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

//TODO:replace List<AppsItemInfo> with AppsBaseAdapter

public class AllAppsActivity extends Activity {

    List<AppsItemInfo> list;    // 用来记录应用程序的信息
    GridView gridview;
    int selectedItemPosition = 0;       // 记录当前点击的item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.allapps_activity);

        list = new ArrayList<AppsItemInfo>();
        gridview = (GridView)findViewById(R.id.gridview);

        updateAllApps();

        // 点击应用图标时，做出响应
        gridview.setOnItemClickListener(new ClickListener());
    }

    @Override
    protected void onResume() {
        updateAllApps();
        super.onResume();
    }

    private void updateAllApps() {
        list.clear();
        PackageManager pManager = AllAppsActivity.this.getPackageManager();
        List<ResolveInfo> appList = getAllApps(AllAppsActivity.this);

        for (int i = 0; i < appList.size(); i ++) {
            ResolveInfo pinfo = appList.get(i);
            AppsItemInfo shareItem = new AppsItemInfo();
            // 设置图片
            shareItem.setIcon(pinfo.loadIcon(pManager));
            // 设置应用程序名字
            shareItem.setLabel(pinfo.loadLabel(pManager).toString());
            // 设置应用程序的包名
            shareItem.setPackageName(pinfo.activityInfo.packageName);

            list.add(shareItem);
        }
        // 设置gridview的Adapter
        gridview.setAdapter(new AppsBaseAdapter());

        if(selectedItemPosition >= 0)
            gridview.setSelection(selectedItemPosition);
    }

    private static List<ResolveInfo> getAllApps(Context context) {
        List<ResolveInfo> apps = new ArrayList<ResolveInfo>();
        PackageManager packageManager = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> tempAppList = packageManager.queryIntentActivities(mainIntent, 0);
        for(ResolveInfo info : tempAppList) {
            apps.add(info);
        }
        return apps;
    }

    private class AppsBaseAdapter extends BaseAdapter {
        LayoutInflater inflater = LayoutInflater.from(AllAppsActivity.this);

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                // 使用View的对象itemView与R.layout.item关联
                convertView = inflater.inflate(R.layout.app, null);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView
                        .findViewById(R.id.apps_image);
                holder.label = (TextView) convertView
                        .findViewById(R.id.apps_textview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.icon.setImageDrawable(list.get(position).getIcon());
            holder.label.setText(list.get(position).getLabel());

            return convertView;
        }
    }

    private class ViewHolder{
        private ImageView icon;
        private TextView label;
    }

    // 当用户点击应用程序图标时，将对这个类做出响应
    private class ClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            selectedItemPosition = arg2;
            Intent intent;
            intent = AllAppsActivity.this.getPackageManager().getLaunchIntentForPackage(list.get(arg2).getPackageName());
            startActivity(intent);
        }
    }

    // 自定义一个 AppsItemInfo 类，用来存储应用程序的相关信息
    private class AppsItemInfo {
        private Drawable icon; // 存放图片
        private String label; // 存放应用程序名
        private String packageName; // 存放应用程序包名

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }
    }
}