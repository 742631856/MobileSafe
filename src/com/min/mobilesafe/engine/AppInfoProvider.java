package com.min.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.min.mobilesafe.domain.AppInfo;

public class AppInfoProvider {

	/**
	 * 获取所有已安装的app信息
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		ArrayList<AppInfo> appInfos = new ArrayList<>();
		PackageManager pm = context.getPackageManager();
		//获取所有的安装包信息
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		for (PackageInfo packInfo : packInfos) {//packInfo 相当于一个manifest文件
			AppInfo info = new AppInfo();
			info.icon = packInfo.applicationInfo.loadIcon(pm);
			info.name = packInfo.applicationInfo.loadLabel(pm).toString();//app名称
			info.packName = packInfo.packageName;	//包名
			
			info.userApp = (ApplicationInfo.FLAG_SYSTEM & packInfo.applicationInfo.flags) == 0;
			info.inRom = ((ApplicationInfo.FLAG_EXTERNAL_STORAGE & packInfo.applicationInfo.flags) == 0);
			
			appInfos.add(info);
		}
		return appInfos;
	}
}
