package com.min.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {
	
	private SharedPreferences sp;
	private TextView tvSafeNum;
	private ImageView ivLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences(SPKeys.KEY_SP_NAME, MODE_PRIVATE);
		boolean configed = sp.getBoolean(SPKeys.KEY_LOST_FIND_CONFIG, false);
		
		if (configed) {//是否进入过防丢向导界面
			setContentView(R.layout.activity_lost_find);			
		} else {//没有 就进入向导界面
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			finish();	//还要关闭当前界面
			return;
		}
		
		tvSafeNum = (TextView) findViewById(R.id.tv_safenum);
		ivLock = (ImageView) findViewById(R.id.iv_lock);
		
		tvSafeNum.setText(sp.getString(SPKeys.KEY_SAFE_PHONE, ""));
		
		if (sp.getBoolean(SPKeys.KEY_PROTECTING, false)) {//开启了防盗保护
			ivLock.setImageResource(R.drawable.lock);
		}
	}
	
	public void reEnterSetup(View view) {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();	//还要关闭当前界面
	}
}