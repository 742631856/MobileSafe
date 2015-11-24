package com.min.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.min.mobilesafe.R;

public class SettingClickView extends RelativeLayout {
	
	public static final String NAMESPACE = "http://schemas.android.com/apk/res/com.min.mobilesafe";
	
	private TextView tvTitle;//
	private TextView tvDesc;

	private void initView(Context context) {
		View.inflate(context, R.layout.setting_click_item, this);	//this设置后，才能直接用下面的方法，否则应该用view.findViewById
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvDesc = (TextView) findViewById(R.id.tv_desc);
	}

	public SettingClickView(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * 从布局文件中创建实例时,且没有样式的时，会调用这个构造方法
	 * @param context
	 * @param attrs
	 */
	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		
		//从布局文件中获取属性的值
		String title = attrs.getAttributeValue(NAMESPACE, "title");
		tvTitle.setText(title);
	}

	/**
	 * 从布局文件中创建实例时,且有样式的时，会调用这个构造方法
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}
	
	public void setTitle(int resId) {
		tvTitle.setText(resId);
	}

	public void setDesc(String desc) {
		tvDesc.setText(desc);
	}
	
	public void setDesc(int resId) {
		tvDesc.setText(resId);
	}
}