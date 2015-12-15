package com.min.mobilesafe.domain;

public class BlackNumberInfo {

	private String number;
	private String mode;
	
	public BlackNumberInfo(String number, String mode) {
		super();
		this.number = number;
		this.mode = mode;
	}
	
	public String getNumber() {
		return number;
	}
	public String getMode() {
		return mode;
	}
	
	public String getModeString() {
		if (mode.equals("1")) {
			return "拦截电话";
		} else if (mode.equals("2")) {
			return "拦截短信";
		} else {
			return "全部拦截";
		}
	}
}