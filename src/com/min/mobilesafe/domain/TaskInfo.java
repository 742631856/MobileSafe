package com.min.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * app信息实体类
 * @author min
 *
 */
public class TaskInfo {
	public Drawable icon;
	public String name;
	public String packName;	//包名
	public long memSize;	//占用内存
	public boolean userTask;	//用户app还是系统app
	
	public boolean isChecked;	//是否被选中了
	
	@Override
	public String toString() {
		return "TaskInfo [icon=" + icon + ", name=" + name + ", packName="
				+ packName + ", memSize=" + memSize + ", userTask=" + userTask
				+ "]";
	}
}
