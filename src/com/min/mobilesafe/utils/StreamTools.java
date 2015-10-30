package com.min.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamTools {
	
	public static String readFromStream(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bt = new byte[1024];
		int len = 0;
		while ((len = is.read(bt)) != -1) {
			baos.write(bt, 0, len);
		}
		//is.close();
		String result = baos.toString();
		baos.close();
		return result;
	}
}