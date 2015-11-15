package com.min.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.min.mobilesafe.db.dao.NumberAddressQueryUtils;

public class AddressService extends Service {
	
	private String databasePath = null;
	private TelephonyManager tm;
	private MyPhoneStateListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		databasePath = getFilesDir().getAbsolutePath() + "/address.db";
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener(); 
		//设置来电监听
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);//需要读取手机状态的权限
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//取消监听
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		tm = null;
	}
	
	private class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {//第一个参数是状态 ，第二个参数是来电号码
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:	//响铃的时候
				String address = NumberAddressQueryUtils.queryNumber(incomingNumber, databasePath);
				Toast.makeText(AddressService.this, address, Toast.LENGTH_LONG).show();
				break;

			default:
				break;
			}
		}
	}
}