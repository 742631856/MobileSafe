package com.min.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.min.mobilesafe.db.BlackNumberDBOpenHelper;
import com.min.mobilesafe.domain.BlackNumberInfo;

public class BlackNumberDao {

	private BlackNumberDBOpenHelper helper;
	/**
	 * 
	 * @param context
	 */
	public BlackNumberDao(Context context) {
		helper = new BlackNumberDBOpenHelper(context);
	}
	
	public void add(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
	}
	
	public void delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("blacknumber", "number = ?", new String[]{number});
	}
	
	public void update(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		db.update("blacknumber", values, "number = ?", new String[]{number});
	}
	
	public boolean find(String number) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("blacknumber", new String[]{"number"}, "number = ?", new String[]{number}, null, null, null);
		if (cursor.moveToNext()) {
			cursor.close();
			db.close();
			return true;
		}
		cursor.close();
		db.close();
		return false;
	}
	
	public List<BlackNumberInfo> findAll() {
		List<BlackNumberInfo> list = new ArrayList<>();//jdk7
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number, mode from blacknumber", null);
		BlackNumberInfo info = null;
		while (cursor.moveToNext()) {
			String num = cursor.getString(0);
			String mode = cursor.getString(1);
			info = new BlackNumberInfo(num, mode);
			list.add(info);
		}
		cursor.close();
		db.close();
		return list;
	}
}
