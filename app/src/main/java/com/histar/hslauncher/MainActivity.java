package com.histar.hslauncher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final int CLOCK = 3;
    private static final int CLOCK_CYCLE = 30000;
    private TextView mClockText = null;
    private ImageView mNetView = null;

    private int wifiStatus[] = {R.mipmap.wifi0,
            R.mipmap.wifi1,
            R.mipmap.wifi2,
            R.mipmap.wifi3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mClockText = (TextView)findViewById(R.id.clockText);
        mNetView = (ImageView)findViewById(R.id.netView);

        updateUsb(this);

        IntentFilter netFilter = new IntentFilter();
        netFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mainReceiver, netFilter);

        IntentFilter storageFilter = new IntentFilter();
        storageFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        storageFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        storageFilter.addDataScheme("file");
        registerReceiver(mainReceiver, storageFilter);

        Timer showTimeTimer = new Timer();
        showTimeTimer.schedule(new TimerTask() {
            public void run() {
                showTime();
            }
        }, 2000, CLOCK_CYCLE);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CLOCK:
                    mClockText.setText(msg.obj.toString());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mainReceiver != null){
            unregisterReceiver(mainReceiver);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(KeyEvent.KEYCODE_BACK == keyCode)
            return false ;
        return super.onKeyDown(keyCode, event);
    }

    private void showTime() {
        boolean b24Format = android.text.format.DateFormat.is24HourFormat(getApplicationContext());
        Calendar mCalendar = Calendar.getInstance();
        //mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String currentZone = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
        mCalendar.setTimeZone(TimeZone.getTimeZone(currentZone));
        //String strYear = String.valueOf(mCalendar.get(Calendar.YEAR));
        //String strMonth = String.format("%02d", mCalendar.get(Calendar.MONTH) + 1);
        //String strDay = String.format("%02d", mCalendar.get(Calendar.DAY_OF_MONTH));
        int nHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        String strAmPm = (nHour < 12) ? getString(R.string.am) : getString(R.string.pm);
        if(!b24Format && nHour > 12)
            nHour -= 12;
        String strHour = String.format("%02d", nHour);
        String strMinute = String.format("%02d", mCalendar.get(Calendar.MINUTE));
        //String strSecond = String.format("%02d", mCalendar.get(Calendar.SECOND));
        //String strWeek = String.valueOf(mCalendar.get(Calendar.DAY_OF_WEEK) - 1);
        String clockText = strHour + ":" + strMinute;

        if(!b24Format) {
            clockText += " " + strAmPm;
        }

        Message msg = MainActivity.this.mHandler.obtainMessage(MainActivity.CLOCK, clockText);
        MainActivity.this.mHandler.sendMessage(msg);
    }

    /*private int getStringFromWeek(int index) {
        int strWeek = 1;
        switch (index) {
            case 1:
                strWeek = R.string.monday;
                break;
            case 2:
                strWeek = R.string.tuesday;
                break;
            case 3:
                strWeek = R.string.wednesday;
                break;
            case 4:
                strWeek = R.string.thursday;
                break;
            case 5:
                strWeek = R.string.friday;
                break;
            case 6:
                strWeek = R.string.saturday;
                break;
            case 7:
                strWeek = R.string.sunday;
                break;
        }
        return strWeek;
    }*/

    private void updateUsbCount(int i) {
        TextView tvUsb = (TextView)findViewById(R.id.usbText);
        tvUsb.setText(i+"");
    }

    private void updateUsbView(boolean on) {
        ImageView ivUsb = (ImageView)findViewById(R.id.usbView);
        ivUsb.setVisibility(on ? View.VISIBLE : View.GONE);
        TextView tvUsb = (TextView)findViewById(R.id.usbText);
        tvUsb.setVisibility(on ? View.VISIBLE : View.GONE);
    }

    private void updateUsb(Context context) {
        int i = StorageUtil.getVolumePaths(context).size();
        updateUsbView(i!=0);
        updateUsbCount(i);
    }

    private BroadcastReceiver mainReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Preferences p = new Preferences(MainActivity.this);
            Log.i(TAG, "BroadcastReceiver : " + action);
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                NetworkInfo netInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if(netInfo.isConnected()) {
                    p.setInternetStatus(1);
                } else {
                    p.setInternetStatus(0);
                }
                switch (netInfo.getType()) {
                    case ConnectivityManager.TYPE_ETHERNET:
                        Log.i(TAG, "TYPE_ETHERNET isConnected = " + netInfo.isConnected());
                        mNetView.setBackgroundResource(netInfo.isConnected() ? R.mipmap.ethernet1 : R.mipmap.ethernet0);
                        break;
                    case ConnectivityManager.TYPE_WIFI:
                        Log.i(TAG, "TYPE_WIFI isConnected = " + netInfo.isConnected());
                        mNetView.setBackgroundResource(netInfo.isConnected() ? wifiStatus[getWifiRssi()] : R.mipmap.wifi0);
                        break;
                    default:
                }
            }else if(action.equals("android.intent.action.MEDIA_MOUNTED")){
                updateUsb(context);
            }else if(action.equals("android.intent.action.MEDIA_UNMOUNTED")){
                updateUsb(context);
            }
        }
    };

    private int getWifiRssi() {
        int ret;
        WifiManager wifi_service = (WifiManager)getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifi_service.getConnectionInfo();
        int level = wifiInfo.getRssi();
        if (level <= 0 && level >= -50) {
            ret = 3;
        } else if (level < -50 && level >= -75) {
            ret = 2;
        } else if (level < -75 && level >= -100) {
            ret = 1;
        } else {
            ret = 0;
        }
        return ret;
    }
}