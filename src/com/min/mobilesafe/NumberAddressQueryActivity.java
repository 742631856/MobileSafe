package com.min.mobilesafe;

import com.min.mobilesafe.db.dao.NumberAddressQueryUtils;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberAddressQueryActivity extends Activity {
	
	private EditText etPhone = null;
	private TextView tvResult = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address_query);
		
		etPhone = (EditText) findViewById(R.id.et_phone);
		tvResult = (TextView) findViewById(R.id.tv_result);
	}
	
	public void query(View view) {
		String phone = etPhone.getText().toString();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "请输入要查询的号码", Toast.LENGTH_LONG).show();
		} else {
			tvResult.setText(NumberAddressQueryUtils.queryNumber(phone, getFilesDir().getAbsolutePath() + "/address.db"));
			Log.i("--->", "--->查询" + phone);
		}
	}
}
