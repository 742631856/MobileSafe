package com.min.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.min.mobilesafe.domain.TaskInfo;
import com.min.mobilesafe.engine.TaskInfoProvider;

/**
 * 锁屏监听,清理进程
 * @author min
 *
 */
public class TaskCleanService extends Service {
	
	private ScreenOffReceiver receiver;
	private ActivityManager am;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		//锁屏的广播监听只能是用代码注册，不能写在manifest中，系统这么设计是为了节约资源
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		receiver = new ScreenOffReceiver();
		registerReceiver(receiver, filter);
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	class ScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			List<TaskInfo> taskInfos = TaskInfoProvider.getAppInfos(TaskCleanService.this);
			for (TaskInfo taskInfo : taskInfos) {
				am.killBackgroundProcesses(taskInfo.packName);
			}
		}
	}
}
