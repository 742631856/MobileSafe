package com.min.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.min.mobilesafe.ui.SettingItemView;

/**
 * 手机防盗的向导界面
 * @author min
 *
 */
public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView siv;
	private TelephonyManager tm;	//手机管理，可以获取sim卡信息
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		siv = (SettingItemView) findViewById(R.id.setup2_siv_sim);
		
		String sim = sp.getString(SPKeys.KEY_SIM, null);
		if (!TextUtils.isEmpty(sim)) {
			siv.setChecked(true);
		}
		
		siv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (siv.isChecked()) {
					siv.setChecked(false);
					editor.putString(SPKeys.KEY_SIM, null);
				} else {
					siv.setChecked(true);
					editor.putString(SPKeys.KEY_SIM, tm.getSimSerialNumber());	//保存sim卡的唯一序列号, 需要权限，读取手机的状态权限
				}
				editor.commit();
			}
		});
	}
	
	public void pre(View view) {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	
	public void next(View view) {
		
		String sim = sp.getString(SPKeys.KEY_SIM, null);
		if (TextUtils.isEmpty(sim)) {
			Toast.makeText(this, "你还没有绑定SIM卡", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		//执行界面切换动画, 这个方法要放在 startActivity或finish的后面
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showNext() {
		next(null);
	}

	@Override
	public void showPre() {
		pre(null);
	}
}