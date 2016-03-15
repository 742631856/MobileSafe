package com.min.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * app信息实体类
 * @author min
 *
 */
public class AppInfo {

	public Drawable icon;
	public String name;
	public String packName;	//包名
	public boolean inRom;		//是否装在系统盘
	public boolean userApp;	//用户app还是系统app
}
