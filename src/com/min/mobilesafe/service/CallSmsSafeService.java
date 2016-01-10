package com.min.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.min.mobilesafe.db.dao.BlackNumberDao;

/**
 * 黑名单拦截服务
 * @author min
 *
 */
public class CallSmsSafeService extends Service {
	
	BlackNumberDao blackDao;
	private InnerSmsReciever smsReciever;//短信监听器
	private TelephonyManager tm;
	private PhoneListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("--->", "--->内置服务启动");
		blackDao = new BlackNumberDao(this);
		//短信监听
		smsReciever = new InnerSmsReciever();
		IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);//优先级高
		registerReceiver(smsReciever, filter);
		
		//来电监听
		listener = new PhoneListener();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(smsReciever);
		smsReciever = null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);//去掉监听
		listener = null;
		super.onDestroy();
	}

	//收到短信 广播接收者
	private class InnerSmsReciever extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			//1、获取短信协议内容
			Object object[] = (Object[]) intent.getExtras().get("pdus");//短信的协议
			for (Object obj : object) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[])obj);	//获取短信
				String phone = sms.getOriginatingAddress(); //发送人的号码
				String mode = blackDao.findMode(phone);
				//直接拦截
				if ("2".equals(mode) || "3".equals(mode)) {//需要拦截
					Log.i("--->", "拦截了黑名单");
					abortBroadcast();//停止广播
				}
				//关键字拦截
				String body = sms.getMessageBody();//短信类容
				if (body.contains("发票")) {//这里需要用到分词技术
					Log.i("--->", "拦截了关键字");
					abortBroadcast();//停止广播
				}
			}
		}
	}
	
	//电话状态变化 广播接收者
	private class PhoneListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			String mode = blackDao.findMode(incomingNumber);
			if ("1".equals(mode) || "3".equals(mode)) {
				Log.i("--->", "--->挂断电话...");
				Toast.makeText(CallSmsSafeService.this, "挂断电话...", Toast.LENGTH_SHORT).show();
//				tm.endCall();	//这个方法被隐藏到了，无法直接调用，放到了隐藏的ITelephony类中
				//
				endCall();
			}
		}
		
		private void endCall() {
			//需要权限CALL_PHONE
			try {
				//通过 ServiceManager 得到TELEPHONY_SERVICE的IBinder实例，但ServiceManager被系统隐藏了，只能通过反射的方法得到
				//主要是TelephonyManager 中的 ITelephony 实例的方法，而 ITelephony 是 aidl文件，可以看看 ITelephony 的注释
				/*	在TelephonyManager 能找到 ServiceManager.getService(Context.TELEPHONY_SERVICE)
					private ITelephony getITelephony() {
						return ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
					}
				 */
//				IBinder iBinder = ServiceManager.getService(TELEPHONY_SERVICE);
				Class<?> clas = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
				Method method = clas.getDeclaredMethod("getService", String.class);
				//得到的IBinder实例是ITelephony的代理类
				IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);//当方法是静态的时候，第一个参数为空
				//ITelephony是根据从系统中拷贝过来的ITelephony.aidl文件拷贝过来自动翻译生成的
				ITelephony.Stub.asInterface(iBinder).endCall();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
}
