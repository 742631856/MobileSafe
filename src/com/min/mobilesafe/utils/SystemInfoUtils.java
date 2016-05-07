package com.min.mobilesafe.utils;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class SystemInfoUtils {

	/**
	 * 获取内存中正在运行的进程数量
	 * @param context
	 * @return 数量
	 */
	public static int getRunningProcessCount(Context context) {
		//PackageManager  包管理器，相当于Windows的程序管理器，静态的
		//ActivityManager 进程管理器，管理手机的活动信息，动态内容
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		return processes.size();
	}
	
	/**
	 * 获取可用内存
	 * @param context
	 * @return
	 */
	public static long getAvailMem(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	
	/**
	 * 获取总内存
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi")
	public static long getTotalMem(Context context) {//这个是高版本下的 写法， 低版本要自己到系统下去读
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.totalMem;
	}
}
