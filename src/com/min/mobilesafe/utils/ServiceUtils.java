package com.min.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

/**
 * 服务工具类
 * @author min
 *
 */
public class ServiceUtils {
	
	/**
	 * 判断服务是否启动
	 * @param context
	 * @param serviceName 服务名称
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String serviceName) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取正在运行的服务，有多少返回多少，不超过100，超过100的部分不管他
		List<RunningServiceInfo> services = am.getRunningServices(100);
		int size = services.size();
		for (int i = 0; i < size; i++) {
			if (services.get(i).service.getClassName().equals(serviceName)) {
				return true;
			}
		}
		return false;
	}
}