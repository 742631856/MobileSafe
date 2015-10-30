package com.min.mobilesafe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.min.mobilesafe.ui.SettingItemView;

/**
 * 设置界面
 * @author min
 *
 */
public class SettingActivity extends Activity {
	
	private SharedPreferences sp;
	private SettingItemView sivUpdate;

	@SuppressLint("CommitPrefEdits")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences(SPKeys.KEY_SP_NAME, MODE_PRIVATE);
		boolean isAutoUpdate = sp.getBoolean(SPKeys.KEY_AUTO_UPDATE, false);
		
		sivUpdate = (SettingItemView) findViewById(R.id.setting_item_update);
		sivUpdate.setChecked(isAutoUpdate);
		
		sivUpdate.setOnClickListener(new OnClickListener() {
			
			Editor editor = sp.edit();
			
			@Override
			public void onClick(View v) {
				if (sivUpdate.isChecked()) {
					sivUpdate.setChecked(false);
					editor.putBoolean(SPKeys.KEY_AUTO_UPDATE, false);
				} else {
					sivUpdate.setChecked(true);
					editor.putBoolean(SPKeys.KEY_AUTO_UPDATE, true);
				}
				editor.commit();//一定要提交不然没法保存到本地
			}
		});
	}
}