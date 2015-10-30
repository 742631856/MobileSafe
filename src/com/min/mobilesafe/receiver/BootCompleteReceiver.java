package com.min.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.min.mobilesafe.SPKeys;

/**
 * 手机启动完成后
 * @author min
 *
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences(SPKeys.KEY_SP_NAME, Context.MODE_PRIVATE);
		//对于sp.get方法千万不要取错数据类型，getBoolean只能取bool的，getInt只能取int的，不然会抛出异常而崩溃
		if (!sp.getBoolean(SPKeys.KEY_PROTECTING, false)) {//没有开启防盗功能
			return;
		}
		
		tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		
		//比较两个序列号
		String saveSim = sp.getString(SPKeys.KEY_SIM, "");
		String realSim = tm.getSimSerialNumber();
		if (!saveSim.equals(realSim)) {
			Log.i("--->", "BootCompleteReceiver : sim卡变更");
//			Toast.makeText(context, "sim卡变更", Toast.LENGTH_LONG).show();
			//发送短信, 需要添加权限, 貌似在模拟器上发送中文短信会有乱码问题
			SmsManager.getDefault().sendTextMessage(sp.getString(SPKeys.KEY_SAFE_PHONE, ""), null, "sim changed", null, null);
		}
	}
}