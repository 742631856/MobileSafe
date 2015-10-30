package com.min.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 手机防盗的向导界面
 * @author min
 *
 */
public class Setup4Activity extends BaseSetupActivity {
	
	private CheckBox cbProtecting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		
		cbProtecting = (CheckBox) findViewById(R.id.cb_protecting);
		setProtectingChecked(sp.getBoolean(SPKeys.KEY_PROTECTING, false));
		cbProtecting.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setProtectingChecked(isChecked);
			}
		});
	}
	
	private void setProtectingChecked(boolean isChecked) {
		sp.edit().putBoolean(SPKeys.KEY_PROTECTING, isChecked).commit();
		if (isChecked) {
			cbProtecting.setText("防盗保护已开启");
			cbProtecting.setChecked(true);
		} else {
			cbProtecting.setChecked(false);
			cbProtecting.setText("防盗保护已关闭");
		}
	}
	
	public void pre(View view) {
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	
	public void next(View view) {
		Editor editor = sp.edit();
		editor.putBoolean(SPKeys.KEY_LOST_FIND_CONFIG, true);
		editor.commit();
		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);
		finish();
		//这个方法要放在 startActivity或finish的后面
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showNext() {
	}

	@Override
	public void showPre() {
		pre(null);
	}
}