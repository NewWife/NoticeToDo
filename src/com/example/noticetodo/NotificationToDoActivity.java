package com.example.noticetodo;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Random;

import jp.android.poro.todo.database.DBAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * ToDoの通知を管理するドライバクラス
 * 後でマージする時に置き換えを行う
 */
public class NotificationToDoActivity extends Activity
implements OnClickListener
{
	/**
	 *  IDとタイプを紐付けして保存するクラス
	 */
	private class ServiceKeyPair
	{
		public int id;
		public String type;

		public ServiceKeyPair(int id, String type)
		{
			this.id = id;
			this.type = type;
		}
	}

	static final String TAG = "NotificationToDoActivity";
	static final int REQUEST_CODE_MAIN_ACTIVITY = 0;

	private ArrayList<Integer> listID;
	private ArrayList<ServiceKeyPair> servicePairList;
	private DBAdapter dbAdapter;

	Button startServiceButton;
	Button exitServiceButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_todo_main);

		startServiceButton = (Button)findViewById(R.id.start_service);
		startServiceButton.setOnClickListener(this);

		// サービスの起動・終了ボタン
		exitServiceButton = (Button)findViewById(R.id.exit_service);
		exitServiceButton.setOnClickListener(this);

		servicePairList = new ArrayList<ServiceKeyPair>();
		listID = new ArrayList<Integer>();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		dbAdapter.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * AlarmManagerでサービス（ToDoNotificationの表示を行う）を起動する
	 */
	public void addAlarmManager(ServiceKeyPair keypair, int millitime)
	{
		Log.d(TAG, "scheduleService()");
		Context context = getBaseContext();

		// Intentの設定
		Intent intent = new Intent(context, SetToDoNotification.class);
		intent.putExtra("Title", keypair.type);
		intent.putExtra("SubTitle", keypair.type);
		intent.putExtra("NotificationMessage", keypair.type);
		intent.putExtra("ID", keypair.id);

		// ここでタイプ分けを行い、AlarmManagerに複数のサービスを登録できるようにする
		intent.setType(keypair.type);

		// PendingIntentでWrapしてalermManagerにセットする
		PendingIntent pendingIntent = PendingIntent.getService(context, keypair.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + millitime, pendingIntent);
	}

	/**
	 * 登録されたAlarmManagerによるサービス機能をキャンセルする
	 */
	public void cancelAlarmManager(ServiceKeyPair keypair)
	{
		Context context = getBaseContext();
		Intent intent = new Intent(context, SetToDoNotification.class);
		intent.setType(keypair.type);
		PendingIntent pendingIntent = PendingIntent.getService(context, keypair.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		if(startServiceButton == v)
		{
			// [DEBUG]
			// 複数登録をするために、IDとTypeを紐付けする
			// IDの登録できる上限は1000以上としているが、後で変更可能
			//  @ 新妻
			int ID = -1;
			for(int i = 1; i <= 1000; ++i)
			{
				// 登録されていないIDならば
				if(!listID.contains(i))
				{
					ID = i;
					listID.add(ID);
					break;
				}
			}
			// [DEBUG]
			// 本来ならToDoに対応するTypeが呼ばれるはずだが、デバックの為に適当に生成
			// もしくは主キーのIDを文字列としてTypeに指定することで、
			// PendingIntentで分けて実行することが出来る
			String type = getRandomString(10);

			// キーペアをリストに登録する
			ServiceKeyPair keypair = new ServiceKeyPair(ID, type);
			servicePairList.add(keypair);

			final int millitime = 1000;

			// 登録上限に達していなければAlermManagerにサービスの起動を登録する
			if(ID != -1)
				addAlarmManager(keypair, millitime);
		}
		else if(exitServiceButton == v)
		{
			// 全てのAlarmManagerをキャンセルする
			while(listID.size() > 0)
			{
				int ID = listID.remove(0);
				ServiceKeyPair keypair = getKeyPairFromID(ID);

				// 対応するサービス（ToDo）を削除する
				cancelAlarmManager(keypair);
				// 対応するkeyPairをリストから削除する
				servicePairList.remove(keypair);
				// ToDoノーティフィケーションの削除
				//destroyNotification();
			}
		}
	}

	/**
	 * IDから対象のserviceKeyPairの値を取得する
	 * ※ 処理重い O(n)。もっといい方法があるはず
	 */
	private ServiceKeyPair getKeyPairFromID(int ID)
	{
		ServiceKeyPair ret = null;
		for(int i = 0, n = servicePairList.size(); i < n; ++i)
		{
			ServiceKeyPair skp = servicePairList.get(i);
			if(skp.id == ID)
			{
				ret = skp;
				break;
			}
		}
		return ret;
	}

	/** [DEBUG]
	 *  指定された文字数だけランダムで文字を生成し、文字列として返す
	 */
	private static String getRandomString(int cnt) {
		final String chars ="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		long seed = System.currentTimeMillis() + Runtime.getRuntime().freeMemory();
		Log.d(TAG, Long.toString(seed));
		Random rnd = new Random(seed);
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < cnt; ++i){
			int val=rnd.nextInt(chars.length());
			buf.append(chars.charAt(val));
		}
		return buf.toString();
	}
}

