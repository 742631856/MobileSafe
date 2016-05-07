package com.min.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Debug.MemoryInfo;

import com.min.mobilesafe.R;
import com.min.mobilesafe.domain.TaskInfo;

public class TaskInfoProvider {

	/**
	 * 获取所有正在运行的进程信息
	 * @param context
	 * @return
	 */
	public static List<TaskInfo> getAppInfos(Context context) {
		ArrayList<TaskInfo> appInfos = new ArrayList<>();
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		//获取所有的信息
		List<RunningAppProcessInfo> processesInfos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo processInfo : processesInfos) {//packInfo 相当于一个manifest文件
			TaskInfo info = new TaskInfo();
			try {
				MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{processInfo.pid});
				info.memSize = memoryInfos[0].getTotalPrivateDirty() * 1024;
				info.packName = processInfo.processName;	//包名
				ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.processName, 0);
				info.icon = applicationInfo.loadIcon(pm);
				info.name = applicationInfo.loadLabel(pm).toString();//app名称
				info.userTask = (ApplicationInfo.FLAG_SYSTEM & applicationInfo.flags) == 0;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				info.name = processInfo.processName;
				info.icon = context.getResources().getDrawable(R.drawable.ic_default);
				info.userTask = false;
			}
			appInfos.add(info);
		}
		return appInfos;
	}
}
