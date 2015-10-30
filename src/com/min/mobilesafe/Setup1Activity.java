package com.min.mobilesafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 手机防盗的向导界面
 * @author min
 *
 */
public class Setup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}
	
	/**
	 * 下一步
	 * @param view
	 */
	public void next(View view) {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		//这个方法要放在 startActivity或finish的后面
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showNext() {
		next(null);
	}

	@Override
	public void showPre() {}
}