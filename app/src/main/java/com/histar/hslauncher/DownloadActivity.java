package com.histar.hslauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadActivity extends Activity implements OnClickListener {

    private static final int MSG_BEGIN_DOWNLOAD = 0;
    private static final int MSG_DOWNLOADING = 1;
    private static final int MSG_END_DOWNLOAD = 2;
    private static final int MSG_INSTALLED = 3;
    private static final int MSG_REBOOT = 99;
    private static final int MSG_CANCEL = 9;
    private String mSrcPath;
    private String mDstPath;
    private ProgressBar mProgressBar = null;
    private TextView progressValueText = null;
    private Button downloadCancel = null;
    private float mProgressValue;
    private int nTotalFileLength;
    private int nDownedFileLength;
    private int nDownloadIndex = 0;
    private boolean isCancel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_activity);
        setTitle(R.string.kodi_download);

        mProgressBar = (ProgressBar) findViewById(R.id.downloadProgressBar);
        progressValueText = (TextView) findViewById(R.id.progressValueText);
        downloadCancel = (Button) findViewById(R.id.bt_cancel);
        downloadCancel.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        mSrcPath = bundle.getString("src_path");
        mDstPath = bundle.getString("dst_path");
        Log.i("xxx", mSrcPath + "," + mDstPath);

        Thread thread = new Thread() {
            public void run() {
                try {
                    HttpDownloader downloader = new HttpDownloader();
                    int result = downloader.downFile(mSrcPath, mDstPath);

                    //mSrcPath = "http://ota.openhourlab.com/ota/gecko/apk/mxffmpeg.apk";
                    mSrcPath = "http://192.168.8.250/ota/mxffmpeg.apk";
                    mDstPath = "/sdcard/mxffmpeg.apk";
                    downloader.downFile(mSrcPath, mDstPath);
                    System.out.println(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case MSG_BEGIN_DOWNLOAD:
                        if (nDownloadIndex == 0)
                            setTitle(R.string.kodi_download);
                        else if (nDownloadIndex == 1)
                            setTitle(R.string.MXPlayer_download);
                        mProgressBar.setMax(nTotalFileLength);
                        break;
                    case MSG_REBOOT:
                        KodiSocketClient socket1 = new KodiSocketClient();
                        socket1.writeMess("system reboot");
                        break;
                    case MSG_CANCEL:
                        DownloadActivity.this.finish();
                        break;
                    case MSG_DOWNLOADING:
                        mProgressBar.setProgress(nDownedFileLength);
                        mProgressValue = (float) nDownedFileLength * 100 / nTotalFileLength;
                        if (mProgressValue > 100)
                            mProgressValue = 100;
                        break;
                    case MSG_INSTALLED:
                        if (MainPage.isKodiInstalled()) {
                            try {
                                //DownloadActivity.this.setVisible(false);
                                Log.i("xxx", "reboot dialog show");
                                AlertDialog mAlert = new AlertDialog.Builder(DownloadActivity.this).setTitle(R.string.reboot_notice1).setMessage(R.string.reboot_notice2).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Preferences p = new Preferences(getApplicationContext());
                                        p.setKodiState(2);
                                        Message message = new Message();
                                        message.what = MSG_REBOOT;
                                        handler.sendMessage(message);
                                        //
                                        //dialog.cancel();
                                        //DownloadActivity.this.finish();
                                    }
                                }).show();

                                mAlert.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                        if (keyCode == KeyEvent.KEYCODE_BACK
                                                && event.getRepeatCount() == 0) {
                                            dialog.cancel();
                                            DownloadActivity.this.finish();
                                        }
                                        return false;
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(DownloadActivity.this, R.string.install_failed, Toast.LENGTH_LONG).show();
                            Preferences p = new Preferences(DownloadActivity.this);
                            p.setKodiState(0);
                            DownloadActivity.this.finish();
                        }
                        break;
                    case MSG_END_DOWNLOAD:
                        nDownloadIndex++;
                        if (nDownloadIndex == 2) {
                            Preferences p = new Preferences(getApplicationContext());
                            p.setKodiState(1);
                            KodiSocketClient socket = new KodiSocketClient();
                            socket.writeMess("system busybox mount -o remount rw /system && cp /sdcard/mxffmpeg.apk /system/app/ && cp /sdcard/kodi.apk /data/app/");
                            final ProgressDialog progressDialog = ProgressDialog.show(DownloadActivity.this, getString(R.string.reboot_notice3), getString(R.string.reboot_notice4), true, false);
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(12000);
                                        progressDialog.dismiss();
                                        Log.i("xxx", "progressDialog.dismiss");
                                        //DownloadActivity.this.finish();

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Message message = new Message();
                                    message.what = MSG_INSTALLED;
                                    handler.sendMessage(message);
                                }
                            }.start();
                        } else {
                            nTotalFileLength = 100;
                            mProgressValue = 0;
                            nDownedFileLength = 0;
                        }
                        break;
                    default:
                        break;
                }
                progressValueText.setText(String.format("%.2f", mProgressValue) + " %");
            }
        }
    };


    @Override
    public void onClick(View v) {
        isCancel = true;
    }


    private class HttpDownloader {

        private URL url = null;
        private int FILESIZE = 4 * 1024;

        public int downFile(String srcPath, String dstPath) {
            InputStream inputStream = null;
            isCancel = false;
            try {
                File file = new File(dstPath);

                if (file.exists()) {
                    file.delete();
                }
                inputStream = getInputStreamFromURL(srcPath);
                OutputStream output = null;
                try {
                    file.createNewFile();
                    output = new FileOutputStream(file);
                    byte[] buffer = new byte[FILESIZE];
                    int length;
                    while ((length = (inputStream.read(buffer))) > 0) {
                        output.write(buffer, 0, length);
                        if (isCancel) {
                            Message message = new Message();
                            message.what = MSG_CANCEL;
                            handler.sendMessage(message);
                            return 0;
                        }
                        nDownedFileLength += length;
                        Message message = new Message();
                        message.what = MSG_DOWNLOADING;
                        handler.sendMessage(message);
                    }
                    output.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Thread.sleep(1000);
                Message message = new Message();
                message.what = MSG_END_DOWNLOAD;
                handler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }

        public InputStream getInputStreamFromURL(String urlStr) {
            HttpURLConnection urlConn = null;
            InputStream inputStream = null;
            try {
                url = new URL(urlStr);
                urlConn = (HttpURLConnection) url.openConnection();
                inputStream = urlConn.getInputStream();

                Message message = new Message();
                message.what = MSG_BEGIN_DOWNLOAD;
                nTotalFileLength = urlConn.getContentLength();
                handler.sendMessage(message);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return inputStream;
        }
    }
}