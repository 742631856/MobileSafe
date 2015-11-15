package com.min.mobilesafe;

import com.min.mobilesafe.db.dao.NumberAddressQueryUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberAddressQueryActivity extends Activity {
	
	private String databasePath = null;	//这个是数据库文件的路径,初始话不能写在这个后面,getFilesDir()会包空指针错误
	private EditText etPhone = null;
	private TextView tvResult = null;
	
	/**
	 * 震动服务
	 */
	private Vibrator vibrator = null;//要权限

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address_query);
		databasePath = getFilesDir().getAbsolutePath() + "/address.db";
		
		etPhone = (EditText) findViewById(R.id.et_phone);
		tvResult = (TextView) findViewById(R.id.tv_result);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		
		etPhone.addTextChangedListener(new TextWatcher() {
			
			//输入框发生了变化
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (null != s && s.length() >= 3) {
					tvResult.setText(NumberAddressQueryUtils.queryNumber(s.toString(), databasePath));
				} else {
					long pattern[] = new long[]{	//开关要成对出现
							300,	//开0.3秒
							1000,	//关1秒
							200,	//开
							1000,	//关
							100,	//开
							1000	//关
					};
					vibrator.vibrate(pattern, -1);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
	}
	
	public void query(View view) {
		String phone = etPhone.getText().toString();
		if (TextUtils.isEmpty(phone)) {
			Animation anim = AnimationUtils.loadAnimation(this, R.anim.shake);//抖动动画
			etPhone.startAnimation(anim);
			Toast.makeText(this, "请输入要查询的号码", Toast.LENGTH_LONG).show();
		} else {
			tvResult.setText(NumberAddressQueryUtils.queryNumber(phone, databasePath));
			Log.i("--->", "--->查询" + phone);
		}
	}
}