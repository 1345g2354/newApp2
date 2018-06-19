package com.hangyjx.syygzapp.updateapp;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.Toast;

import com.hangyjx.syygzapp.R;
import com.hangyjx.syygzapp.SWYApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;



/**
 * Created by jian on 2016/6/6.
 * mabeijianxi@gmail.com
 */
public class DownloadService extends IntentService {

	public static final String APK_DOWNLOAD_URL = "url";
	public static final String APK_UPDATE_CONTENT = "updateMessage";

	private static final int BUFFER_SIZE = 10 * 1024; // 8k ~ 32K
	public static final String TAG = "DownloadService";
	private NotificationManager mNotifyManager;
	private Builder mBuilder;
	private String urlStr;
	public DownloadService() {
		super("DownloadService");
	}
	public static void startUploadImg(Context context, String path)
	{

	}
	@Override
	protected void onHandleIntent(Intent intent) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(SWYApplication.context,"开始下载", Toast.LENGTH_SHORT).show();

			}
		});
		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new Builder(this);

		String appName=getString(getApplicationInfo().labelRes);
		int icon=getApplicationInfo().icon;

		mBuilder.setContentTitle(appName).setSmallIcon(icon);
		urlStr = intent.getStringExtra(APK_DOWNLOAD_URL);
		InputStream in=null;
		FileOutputStream out = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(false);
			urlConnection.setConnectTimeout(10 * 1000);
			urlConnection.setReadTimeout(10 * 1000);
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("Charset", "UTF-8");
			urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

			urlConnection.connect();
			long bytetotal = urlConnection.getContentLength();
			long bytesum = 0;
			int byteread = 0;
			in = urlConnection.getInputStream();
			File dir = StorageUtils.getCacheDirectory(this);
			String apkName= urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length());
			File apkFile = new File(dir, apkName);
			out = new FileOutputStream(apkFile);
			byte[] buffer = new byte[BUFFER_SIZE];

			int oldProgress = 0;

			while ((byteread = in.read(buffer)) != -1) {
				bytesum += byteread;
				out.write(buffer, 0, byteread);

				int progress = (int) (bytesum * 100L / bytetotal);
				// 如果进度与之前进度相等，则不更新，如果更新太频繁，否则会造成界面卡顿
				if (progress != oldProgress) {
//					CommonUtils.sendUpdataBroadcast(this, BroadcastData.Status.IN_PROGRESS,TAG,null,null,progress,null);
					updateProgress(progress);
				}
				oldProgress = progress;
			}
			// 下载完成
//			CommonUtils.sendUpdataBroadcast(this, BroadcastData.Status.COMPLETED, TAG, null, null, 1, null);
			mBuilder.setContentText(getString(R.string.download_success)).setProgress(0, 0, false);

			final Intent installAPKIntent = new Intent(Intent.ACTION_VIEW);
			installAPKIntent.addCategory("android.intent.category.DEFAULT");
			installAPKIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//如果没有设置SDCard写权限，或者没有sdcard,apk文件保存在内存中，需要授予权限才能安装
			 String[] command = {"chmod","777",apkFile.toString()};
			 ProcessBuilder builder = new ProcessBuilder(command);
			 builder.start();
			SharedPreferencesUtil.saveIntData(this,"postion_secret",0);
			installAPKIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");

					startActivity(installAPKIntent);


			Notification noti = mBuilder.build();
			noti.flags = Notification.FLAG_AUTO_CANCEL;
			mNotifyManager.notify(0, noti);
//			SharedPreferencesUtil.saveStringData(this, GuideAct.SECOND_AD, "");

		} catch (Exception e) {
//			CommonUtils.sendUpdataBroadcast(this, BroadcastData.Status.ERROR, TAG, null, null, 0, null);

			if(!NetWorkStateUtils.isNetworkConnected(this)){
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(SWYApplication.context,"下载失败,请检查网络哦~！", Toast.LENGTH_SHORT).show();

					}
				});
				mBuilder.setContentText("下载失败，请检查网络，可点击重试！").setProgress(0, 0, false);
			}else {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(SWYApplication.context,"下载失败！", Toast.LENGTH_SHORT).show();

					}
				});
				mBuilder.setContentText("下载失败，可点击重试！").setProgress(0, 0, false);
			}
			Intent installAPKIntent = new Intent(getApplicationContext(),DownloadService.class);
			installAPKIntent.putExtra(DownloadService.APK_DOWNLOAD_URL, urlStr);
			PendingIntent pendingIntent = PendingIntent.getService(this, 0, installAPKIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			mBuilder.setContentIntent(pendingIntent);
			Notification noti = mBuilder.build();
			noti.flags = Notification.FLAG_AUTO_CANCEL;
			mNotifyManager.notify(0, noti);
			stopSelf();

		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressLint("StringFormatInvalid")
	private void updateProgress(int progress) {
		//"正在下载:" + progress + "%"
		mBuilder.setContentText(this.getString(R.string.download_progress, progress)).setProgress(100, progress, false);
		//setContentInent如果不设置在4.0+上没有问题，在4.0以下会报异常
		PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContentIntent(pendingintent);
		mNotifyManager.notify(0, mBuilder.build());
	}

}
