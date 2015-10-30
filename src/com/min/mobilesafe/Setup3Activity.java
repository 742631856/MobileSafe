package com.min.mobilesafe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 手机防盗的向导界面
 * @author min
 *
 */
public class Setup3Activity extends BaseSetupActivity {
	
	private EditText etPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		//设置已经保存的号码
		etPhone = (EditText) findViewById(R.id.et_phone);
		etPhone.setText(sp.getString(SPKeys.KEY_SAFE_PHONE, ""));
	}
	
	public void pre(View view) {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	
	public void next(View view) {
		String phone = etPhone.getEditableText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "您还没有添加安全号码", Toast.LENGTH_SHORT).show();
			return;
		}
		//保存安全号码
		sp.edit().putString(SPKeys.KEY_SAFE_PHONE, phone).commit();
		
		Intent intent = new Intent(this, Setup4Activity.class);
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
	public void showPre() {
		pre(null);
	}
	
	public void selectContact(View view) {
		Intent intent = new Intent(this, SelectContactActivity.class);
		startActivityForResult(intent, 1001);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (null == data)
			return;
		
		String phone = data.getStringExtra("phone").replace("-", "");
		etPhone.setText(phone);
	}
}
