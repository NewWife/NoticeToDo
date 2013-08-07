package com.example.noticetodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StubMemoActivity extends Activity
{
	static final String TAG = "NotificationToDoActivity";
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout);

		TextView textView = new TextView(this);
		textView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		textView.setText("メモのテストだよ！");
		layout.addView(textView);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		int color = bundle.getInt("Color");
		String content = bundle.getString("Content");
		String photoPath = bundle.getString("PhotoPath");
		String drawingPath = bundle.getString("DrawingPath");
		String voicePath = bundle.getString("VoicePath");
		Log.d(TAG, "Color : " + color);
		Log.d(TAG, "Content : " + content);
		Log.d(TAG, "PhotoPath : " + photoPath);
		Log.d(TAG, "drawingPath : " + drawingPath);
		Log.d(TAG, "VoicePath : " + voicePath);
	}
}
