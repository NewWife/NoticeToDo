package com.example.noticetodo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import jp.android.poro.todo.database.*;

public class SetToDoNotification extends IntentService
{
	static final String TAG = "NotificationToDoActivity";

	private DBAdapter dpAdapter;

	public SetToDoNotification()
	{
		super(TAG);
		//Log.d(TAG, "Hello Service");
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		dpAdapter = new DBAdapter(this);
		dpAdapter.open();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		dpAdapter.close();
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Bundle bundle = intent.getExtras();
		String title = bundle.getString("Title");
		String subTitle = bundle.getString("SubTitle");
		String message = bundle.getString("NotificationMessage");
		int ID = bundle.getInt("ID");
		int color = 0;
		String content = null;
		String photoPath = null;
		String drawingPath = null;
		String voicePath = null;
		try
		{
			color = dpAdapter.readColor(ID);
			content = dpAdapter.readContent(ID);
			photoPath = dpAdapter.readPhotoPath(ID);
			drawingPath = dpAdapter.readPhotoPath(ID);
			voicePath = dpAdapter.readPhotoPath(ID);
		}
		catch (Exception e)
		{
			// エラー処理
			Log.d(TAG, "DBadapter catch Exception");
			color = 0;
			content = "Read Error";
			photoPath = "none";
			drawingPath = "none";
			voicePath = "none";
		}
		setToDoDetailNotification(ID, title, subTitle, message, color, content, photoPath, drawingPath, voicePath);
	}

	private void setToDoDetailNotification(int ID, String title, String subTitle, String message,
			int color, String content, String photoPath, String drawingPath, String voicePath)
	{
		// Intentの作成
		Intent intent = new Intent(SetToDoNotification.this, StubMemoActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("Color", color);
		intent.putExtra("Content", content);
		intent.putExtra("PhotoPath", photoPath);
		intent.putExtra("DrawingPath", drawingPath);
		intent.putExtra("VoicePath", voicePath);
		PendingIntent contentIntent = PendingIntent.getActivity(SetToDoNotification.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


		// NotificationBuilderを作成
		NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
		// Intentを埋め込む
		builder.setContentIntent(contentIntent);
		// ステータスバーに表示されるテキスト
		builder.setTicker(message);
		// アイコン
		builder.setSmallIcon(android.R.drawable.ic_menu_today);
		// Notificationを開いた時に表示されるタイトル
		builder.setContentTitle(title);
		// Notificationを開いた時に表示されるサブタイトル
		builder.setContentText(subTitle);
		// Notificationを開いた時に表示されるアイコン
		//builder.setLargeIcon(largeIcon);
		// 通知するタイミング
		builder.setWhen(System.currentTimeMillis());
		// 通知時の音・ライト
		builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);
		// タップするとキャンセル（消える）
		builder.setAutoCancel(true);

		// NotificationManagerを取得
		NotificationManager manager = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);
		// Notificationを作成して通知
		manager.notify(ID, builder.build());
	}

}
