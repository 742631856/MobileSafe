package com.min.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	
	/**
	 * MD5加密
	 * @param password
	 * @return
	 */
	public static String md5Password(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");//用java自带的工具
			byte[] result = digest.digest(password.getBytes());
			//把每一个 byte 与 0xff 做与运算
			StringBuilder sb = new StringBuilder();
			for (byte b : result) {
				int num = b & 0xff;	//和0xff 做与运算是 标准的md5，和0xfff 或其他的做 叫加盐
/*				String str = Integer.toHexString(num);
				if (str.length() == 1) {	//只有一位，在前面添0
					sb.append("0");
				}
				sb.append(str);*///被下面的代替
				sb.append(String.format("%02x", num));
			}
			
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
}
