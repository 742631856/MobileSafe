package com.min.mobilesafe.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SmsUtils {
	
	/**
	 * 备份短信
	 * @param context 上下文
	 * @param callBack 备份过程中的回调接口
	 * @throws Exception
	 */
	public static void smsBackup(Context context, SmsBackupCallBack callBack) throws Exception {
		File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
		FileOutputStream fos = new FileOutputStream(file);
		XmlSerializer smsSerializer = Xml.newSerializer();
		smsSerializer.setOutput(fos, "UTF-8");
		smsSerializer.startDocument("UTF-8", true);
		smsSerializer.startTag(null, "smss");
		
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms");
		Cursor cursor = resolver.query(uri, new String[]{"body", "date", "type", "address"}, null, null, null);
		int max = cursor.getCount();
		//接口调用
		callBack.beforeBackup(max);//总条数
		//
		smsSerializer.attribute(null, "max", max+"");//短信的总条数
		//当前备份到第几条
		int current = 0;
		while (cursor.moveToNext()) {
			String body = cursor.getString(0);
			String date = cursor.getString(1);
			String type = cursor.getString(2);
			String address = cursor.getString(3);
			
			smsSerializer.startTag(null, "sms");
			
			smsSerializer.startTag(null, "body");
			smsSerializer.text(body);
			smsSerializer.endTag(null, "body");
			
			smsSerializer.startTag(null, "date");
			smsSerializer.text(date);
			smsSerializer.endTag(null, "date");
			
			smsSerializer.startTag(null, "type");
			smsSerializer.text(type);
			smsSerializer.endTag(null, "type");
			
			smsSerializer.startTag(null, "address");
			smsSerializer.text(address);
			smsSerializer.endTag(null, "address");
			
			smsSerializer.endTag(null, "sms");
			//
			current++;
			callBack.onBackup(current);
//			Thread.sleep(500);//测试用
		}
		cursor.close();
		
		smsSerializer.endTag(null, "smss");
		smsSerializer.endDocument();
	}
	
	public interface SmsBackupCallBack {
		/**
		 * 短信备份前调用
		 * @param total 短信的总数量
		 */
		void beforeBackup(int total);
		
		/**
		 * 正在备份时调用
		 * @param current 当前备份到第几条
		 */
		void onBackup(int current);
	}
	
	/**
	 * 恢复短信
	 * @param context 上下文
	 * @param flag 是否清除已有的短信
	 * @param callBack 恢复过程中回调接口
	 */
	public static void smsRestore(Context context, boolean flag, SmsRestoreCallback callBack) {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms");
		if (flag) {
			resolver.delete(uri, null, null);
		}
		
		ContentValues values = new ContentValues();
		values.put("body", "消息体");
		values.put("date", "");
		values.put("type", "1");
		values.put("address", "5556");
		resolver.insert(uri, values);//
		
		
	}
	
	public interface SmsRestoreCallback {
		
	}
}
