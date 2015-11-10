package com.min.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 号码归属地查询工具
 * @author min
 *
 */
public class NumberAddressQueryUtils {
	//数据库的绝对路径  data/data/com.min.mobilesafe/files/xxx.db	//中间的是包名
	/**
	 * 查询号码的归属地
	 * @param number 要查询的号码
	 * @param databasePath 数据库的绝对路径
	 * @return 地址
	 */
	public static String queryNumber(String number, String databasePath) {
		String address = number;
		
		SQLiteDatabase database = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = database.rawQuery(
				"select location from data2 where id = (select outkey from data1 where id = ?)",
				new String[]{number.substring(0, 7)});
		while (cursor.moveToNext()) {
			address = cursor.getString(0);
		}
		cursor.close();
		
		return address;
	}
}