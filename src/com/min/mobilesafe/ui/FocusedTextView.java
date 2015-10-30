package com.min.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义一个 他永远有焦点的TextView
 * @author min
 *
 */
public class FocusedTextView extends TextView {

	public FocusedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FocusedTextView(Context context) {
		super(context);
	}
	
	/**
	 * 没有焦点，这么做只是欺骗 android系统， 认为他永远有焦点
	 */
	@Override
	public boolean isFocused() {
		return true;	//返回true才能显示  文字的跑马灯效果
	}
}
