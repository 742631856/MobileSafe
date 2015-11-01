package com.min.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.min.mobilesafe.R;
import com.min.mobilesafe.SPKeys;
import com.min.mobilesafe.service.GPSService;

/**
 * 
 * @author mao
 * 
 * 监听短信需要权限
 */
public class SMSReceiver extends BroadcastReceiver {
	
	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences(SPKeys.KEY_SP_NAME, Context.MODE_PRIVATE);
		boolean protecting = sp.getBoolean(SPKeys.KEY_PROTECTING, false);
		if (!protecting) {	//没有开启防盗
			return;
		}
		String safePhone = sp.getString(SPKeys.KEY_SAFE_PHONE, "");
		//1、获取短信协议内容
		Object object[] = (Object[]) intent.getExtras().get("pdus");//短信的协议
		//2、获取每条短信
		for (Object obj : object) {
			SmsMessage sms = SmsMessage.createFromPdu((byte[])obj);	//获取短信
			String phone = sms.getOriginatingAddress(); //发送人的号码
			if (!phone.contains(safePhone)) {	//不是安全号码发送过来的, 模拟器中这个不好判断
				continue;	//立刻进入下一次循环
			}
			
			String msg = sms.getMessageBody();	//短信内容
			if ("#*location*#".equals(msg)) {	//jdk1.7以后可以用switch case
				//定位
				Log.i("--->", "SMSReceiver : 定位");
				Intent inte = new Intent(context, GPSService.class);
				context.startService(inte);
				String location = sp.getString(SPKeys.KEY_LOCATION, null);
				if (TextUtils.isEmpty(location)) {
					SmsManager.getDefault().sendTextMessage(phone, null, "locationing...", null, null);
				} else {
					SmsManager.getDefault().sendTextMessage(phone, null, location, null, null);
				}
				//终止广播向下传递
				abortBroadcast();
				
			} else if ("#*alarm*#".equals(msg)) {	//报警,播放报警音乐
				Log.i("--->", "SMSReceiver : 报警");
				MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
				mediaPlayer.setLooping(false);
				mediaPlayer.setVolume(1.0f, 1.0f);
				mediaPlayer.start();
				abortBroadcast();
				
			} else if ("#*wipedata*#".equals(msg)) {	//销毁数据
				Log.i("--->", "SMSReceiver : 销毁数据");
				DevicePolicyManager dpm = getDPM(context);
				if (null != dpm) {
//					dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);//销毁内存卡数据
					dpm.wipeData(0);//恢复出厂设置
				}
				abortBroadcast();
				
			} else if ("#*lockscreen*#".equals(msg)) {	//锁屏
				Log.i("--->", "SMSReceiver : 锁屏");
				DevicePolicyManager dpm = getDPM(context);
				if (null != dpm) {
					dpm.resetPassword("123", 0);
//					dpm.resetPassword("", 0);//清除锁屏密码
					dpm.lockNow();//锁屏
				}
				abortBroadcast();
			}
		}
	}
	
	private DevicePolicyManager getDPM(Context context) {
		DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName componentName = new ComponentName(context, AdminReceiver.class);
		if (dpm.isAdminActive(componentName)) {//检查管理员是不是被激活了
			return null;
		}
		return dpm;
	}
}