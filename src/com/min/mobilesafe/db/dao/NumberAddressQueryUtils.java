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
		if (number.matches("1[34568]\\d{9}$")) {
			Cursor cursor = database.rawQuery(
					"select location from data2 where id = (select outkey from data1 where id = ?)",
					new String[]{number.substring(0, 7)});
			while (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
			cursor.close();
		} else {
			switch (number.length()) {
			case 3:
				address = "报警电话";
				break;
			case 4:
				address = "模拟器";		
				break;
			case 5:
				address = "客服电话";
				break;
			case 7:
			case 8:
				address = "本地号码";
				break;
			default:
				if (number.length() > 10 && number.startsWith("0")) {//长途电话,010-7788120
					Cursor cursor = database.rawQuery(
							"select location from data2 where area = ?",
							new String[]{number.substring(1, 3)});//3位区号
					while (cursor.moveToNext()) {
						address = cursor.getString(0);
						address = address.substring(0, address.length() - 2);	//只需要地名，不要运营商名
					}
					cursor.close();
					
					cursor = database.rawQuery(
							"select location from data2 where area = ?",
							new String[]{number.substring(1, 4)});	//4位区号
					while (cursor.moveToNext()) {
						address = cursor.getString(0);
						address = address.substring(0, address.length() - 2);	//只需要地名，不要运营商名
					}
					cursor.close();
				}
				break;
			}
		}
		
		return address;
	}
}