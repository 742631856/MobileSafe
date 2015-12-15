package com.min.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

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
	
	//自定义Toast的参数
	private WindowManager.LayoutParams params;
	/**
	 * 自定义土司，模仿Toast
	 * @param address
	 */
	private void myToast(String address) {
		view = View.inflate(this, R.layout.address_show, null);
		view.setOnClickListener(new OnClickListener() {
			long[] mHits = new long[2];//保存点击的时间, 这个长度是表示完成点击的次数,
			@Override
			public void onClick(View v) {
				//-------------这段代码非常巧妙的验证了需要的点击事件----------------------------
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				mHits[mHits.length-1] = SystemClock.uptimeMillis();//记录cpu已启动的时间,这个时间系统是无法修改的
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					//完成了双击
			        params.x = (wm.getDefaultDisplay().getWidth() - view.getWidth())/2;//距窗体左边100个像素
			        params.y = (wm.getDefaultDisplay().getHeight() - view.getHeight())/2;
			        wm.updateViewLayout(view, params);//更新视图
//			        view.layout(l, t, r, b);
			        Editor edit = sp.edit();
					edit.putInt(SPKeys.KEY_ADDRESS_SHOW_X, params.x);
					edit.putInt(SPKeys.KEY_ADDRESS_SHOW_Y, params.y);
					edit.commit();
				}
				//----------------------------------------------------------------
				Log.i("--->", "--->点击了");//若下面的触摸事件返回true则收不到这个事件
//				Toast.makeText(AddressService.this, "点击了", Toast.LENGTH_LONG).show();
				
			}
		});
		view.setOnTouchListener(new OnTouchListener() {
			
			int startX = 0;
			int startY = 0;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN://
					startX = (int)event.getRawX();
					startY = (int)event.getRawY();
					Log.i("--->", "--->触摸按下");
					break;
				case MotionEvent.ACTION_MOVE:
					int nowX = (int)event.getRawX();
					int nowY = (int)event.getRawY();
					
					//把现在的位置和初始位置的变化距离给params
					params.x += (nowX - startX);//这次点击位置和上次点击位置的差
					params.y += (nowY - startY);
					//边界控制
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.x > (wm.getDefaultDisplay().getWidth() - view.getWidth())) {
						params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
					}
					if (params.y < 0) {
						params.y = 0;
					}
					if (params.y > (wm.getDefaultDisplay().getHeight() - view.getHeight())) {
						params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
					}
					wm.updateViewLayout(view, params);//更新视图
//					view.layout(l, t, r, b);//这个也可以更新视图
					//改变初始位置
					startX = nowX;
					startY = nowY;
					Log.i("--->", "--->触摸移动");
					break;
				case MotionEvent.ACTION_UP:
					Editor edit = sp.edit();
					edit.putInt(SPKeys.KEY_ADDRESS_SHOW_X, params.x);
					edit.putInt(SPKeys.KEY_ADDRESS_SHOW_Y, params.y);
					edit.commit();
					Log.i("--->", "--->抬起");
					break;
				}
				return false;//返回false表示这个事件还要继续向下传递，返回true表示事件到此为止
			}
		});
		
		//获取保存的图片的风格下标
		int index = sp.getInt(SPKeys.KEY_ADDRESS_SHOW_BG_STYLE, 0);
		view.setBackgroundResource(imageRes[index]);
		TextView tv = (TextView) view.findViewById(R.id.tv);
		tv.setText(address);
		
		//参数
		params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;//半透明
//        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        //具有电话优先级的窗体类型,这个是要权限的:SYSTEM_ALERT_WINDOW
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;	//窗体不能成为焦点
        //设置相对位置，这个不设置，下面的x、y设置是无效的
        params.gravity = Gravity.TOP + Gravity.LEFT;
        params.x = sp.getInt(SPKeys.KEY_ADDRESS_SHOW_X,  (wm.getDefaultDisplay().getWidth() - view.getWidth())/2);//距窗体左边100个像素
        params.y = sp.getInt(SPKeys.KEY_ADDRESS_SHOW_Y, (wm.getDefaultDisplay().getHeight() - view.getHeight())/2);//距窗体上边150个像素
        
        wm.addView(view, params);
	}
	
	/**
	 * 去电监听者, 显示去电地址, 动态注册, 不在manifest中注册
	 * 需要权限
	 * @author min
	 *
	 */
	class OutCallReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			//得到拨打的电话号码
			String phone = getResultData();
//			Toast.makeText(context, NumberAddressQueryUtils.queryNumber(phone, databasePath), Toast.LENGTH_LONG).show();
			String address = NumberAddressQueryUtils.queryNumber(phone, databasePath);
			myToast(address);
		}
	}
}