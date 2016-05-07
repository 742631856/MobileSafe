package com.min.mobilesafe;

import com.min.mobilesafe.service.TaskCleanService;
import com.min.mobilesafe.utils.ServiceUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TaskSettingActivity extends Activity {
	
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_setting);
		sp = getSharedPreferences(SPKeys.KEY_SP_NAME, MODE_PRIVATE);
		
		CheckBox cbpro = (CheckBox) findViewById(R.id.cb_show_sys_pro);
		CheckBox cbAuto = (CheckBox) findViewById(R.id.cb_auto_clean);
		
		cbpro.setChecked(sp.getBoolean(SPKeys.KEY_SHOW_SYSTEM_PROCESS, false));
		
		cbAuto.setChecked(ServiceUtils.isServiceRunning(this, TaskCleanService.class.getName()));
		
		cbpro.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				sp.edit().putBoolean(SPKeys.KEY_SHOW_SYSTEM_PROCESS, isChecked).commit();
			}
		});
		
		cbAuto.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent intent = new Intent(TaskSettingActivity.this, TaskCleanService.class);
				if (isChecked) {
					startService(intent);
				} else {
					stopService(intent);
				}
			}
		});
	}
}
