package com.min.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.min.mobilesafe.service.AddressService;
import com.min.mobilesafe.service.CallSmsSafeService;
import com.min.mobilesafe.ui.SettingClickView;
import com.min.mobilesafe.ui.SettingItemView;
import com.min.mobilesafe.utils.ServiceUtils;

/**
 * 设置界面
 * @author min
 *
 */
public class SettingActivity extends Activity {
	
	private SharedPreferences sp;
	
	//设置自动更新
	private SettingItemView sivUpdate;
	//设置显示来电归属地
	private SettingItemView sivAddress;
	//设置是否开启黑名单拦截
	private SettingItemView sivCallSmsSafe;
	//归属地显示框的风格切换按钮
	private SettingClickView scvChangeBg;
	//归属地显示框的风格
	private String items[] = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
	//开启服务的意图, 号码归属地显示服务意图
	private Intent addressIntent;
	//
	private Intent callSmsIntent;

//	@SuppressLint("CommitPrefEdits")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences(SPKeys.KEY_SP_NAME, MODE_PRIVATE);
		
		//-------------------是否自动更新------------------------------------------
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
		
		//-------------------号码归属地显示-------------------------------------------
		addressIntent = new Intent(SettingActivity.this, AddressService.class);
		sivAddress = (SettingItemView) findViewById(R.id.setting_item_show_address);
		sivAddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sivAddress.isChecked()) {
					sivAddress.setChecked(false);//这里要不要停止服务
//					boolean isRunning = ServiceUtils.isServiceRunning(SettingActivity.this, AddressService.class.getName());
//					if (isRunning) {
						stopService(addressIntent);
//					}
				} else {
					sivAddress.setChecked(true);
//					boolean isRunning = ServiceUtils.isServiceRunning(SettingActivity.this, AddressService.class.getName());
//					if (!isRunning) {
						startService(addressIntent);
//					}
				}
			}
		});
		
		//------------------归属地显示框的背景设置--------------------------------------
		scvChangeBg = (SettingClickView) findViewById(R.id.scv_change_bg);
		int swi = sp.getInt(SPKeys.KEY_ADDRESS_SHOW_BG_STYLE, 0);
		scvChangeBg.setDesc(items[swi]);
		scvChangeBg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int swi = sp.getInt(SPKeys.KEY_ADDRESS_SHOW_BG_STYLE, 0);
				new AlertDialog.Builder(SettingActivity.this)
				.setTitle("归属地提示框风格")
				.setSingleChoiceItems(items, swi, new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Editor edit = sp.edit();
						edit.putInt(SPKeys.KEY_ADDRESS_SHOW_BG_STYLE, which);
						edit.commit();
						scvChangeBg.setDesc(items[which]);
						dialog.dismiss();
					}
				})
				.setPositiveButton("取消", null)
				.show();
			}
		});
		
		//-------------------黑名单拦截设置----------------------------------------------
		callSmsIntent = new Intent(SettingActivity.this, CallSmsSafeService.class);
		sivCallSmsSafe = (SettingItemView) findViewById(R.id.setting_item_callsms_safe);
		sivCallSmsSafe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sivCallSmsSafe.isChecked()) {
					sivCallSmsSafe.setChecked(false);//这里要不要停止服务
//					boolean isRunning = ServiceUtils.isServiceRunning(SettingActivity.this, CallSmsSafeService.class.getName());
//					if (isRunning) {
						stopService(callSmsIntent);
//					}
				} else {
					sivCallSmsSafe.setChecked(true);
//					boolean isRunning = ServiceUtils.isServiceRunning(SettingActivity.this, CallSmsSafeService.class.getName());
//					if (!isRunning) {
						startService(callSmsIntent);//服务的启动永远只有一次
//					}
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//每次回到前台的时候都要检查服务是否在运行, 归属地显示服务
		boolean addressServiceIsRunning = ServiceUtils.isServiceRunning(this, AddressService.class.getName());
		if (addressServiceIsRunning) {
			sivAddress.setChecked(true);
		} else {
			sivAddress.setChecked(false);
		}
		
		//黑名单拦截服务是否开启
		boolean callSmsSafeServiceIsRunning = ServiceUtils.isServiceRunning(this, CallSmsSafeService.class.getName());
		if (callSmsSafeServiceIsRunning) {
			sivCallSmsSafe.setChecked(true);
		} else {
			sivCallSmsSafe.setChecked(false);
		}
	}
}