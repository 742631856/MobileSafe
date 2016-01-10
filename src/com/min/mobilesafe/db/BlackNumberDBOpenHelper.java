package com.min.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 黑名单数据库工具
 * @author min
 *
 */
public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * 父类数抽象类，且没有无参的构造函数，所以必须显示的调用父类的构造函数
	 */
	public BlackNumberDBOpenHelper(Context context) {
		super(context, "blacknumber.db", null, 1);
	}

	//第一次打开数据库是会执行的
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table blacknumber(_id integer primary key autoincrement, number varchar(20), mode varchar(2))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
