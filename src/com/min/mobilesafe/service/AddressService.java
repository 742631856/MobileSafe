package com.min.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.min.mobilesafe.R;
import com.min.mobilesafe.SPKeys;
import com.min.mobilesafe.db.dao.NumberAddressQueryUtils;

public class AddressService extends Service {
	
	private String databasePath = null;
	private SharedPreferences sp;
	private TelephonyManager tm;
	private MyPhoneStateListener listener;
	private OutCallReceiver receiver;
	//窗体管理器
	private WindowManager wm;
	//
	private View view;
	//"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"
	private int imageRes[] = {
		R.drawable.call_locate_white,
		R.drawable.call_locate_orange,
		R.drawable.call_locate_blue,
		R.drawable.call_locate_gray,
		R.drawable.call_locate_green
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		databasePath = getFilesDir().getAbsolutePath() + "/address.db";
		sp = getSharedPreferences(SPKeys.KEY_SP_NAME, MODE_PRIVATE);
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener(); 
		//设置来电监听
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);//需要读取手机状态的权限
		
		//动态注册去电监听
		IntentFilter filter = new IntentFilter();//意图拦截
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);//去电事件
		receiver = new OutCallReceiver();
		registerReceiver(receiver, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//取消监听
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		tm = null;
		unregisterReceiver(receiver);
		receiver = null;
	}
	
	private class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {//第一个参数是状态 ，第二个参数是来电号码
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:	//响铃的时候
				String address = NumberAddressQueryUtils.queryNumber(incomingNumber, databasePath);
//				Toast.makeText(AddressService.this, address, Toast.LENGTH_LONG).show();
				myToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE:		//空闲状态：挂掉电话、拒接来电后
				if (null != view) {						//移除
					wm.removeView(view);
				}
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 自定义土司，模仿Toast
	 * @param address
	 */
	private void myToast(String address) {
		view = View.inflate(this, R.layout.address_show, null);
		//获取保存的图片的风格下标
		int index = sp.getInt(SPKeys.KEY_ADDRESS_SHOW_BG_STYLE, 0);
		view.setBackgroundResource(imageRes[index]);
		TextView tv = (TextView) view.findViewById(R.id.tv);
		tv.setText(address);
		
		//参数
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;//半透明
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        
        wm.addView(view, params);
	}
	
	/**
	 * 去电监听者, 显示去电地址, 动态注册, 不在manifest中注册, 需要权限
	 * @author min
	 *
	 */
	class OutCallReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			//得到拨打的电话号码
			String phone = getResultData();
			Toast.makeText(context, NumberAddressQueryUtils.queryNumber(phone, databasePath), Toast.LENGTH_LONG).show();
		}
	}
}