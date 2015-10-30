package com.min.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.min.mobilesafe.R;

public class SettingItemView extends RelativeLayout {
	
	public static final String NAMESPACE = "http://schemas.android.com/apk/res/com.min.mobilesafe";
	
	private TextView tvTitle;//
	private TextView tvDesc;
	private CheckBox cbState;
	//存放自定义属性
	private String descOn;
	private String descOff;
	
	private void initView(Context context) {
		View.inflate(context, R.layout.setting_list_item, this);	//this设置后，才能直接用下面的方法，否则应该用view.findViewById
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvDesc = (TextView) findViewById(R.id.tv_desc);
		cbState = (CheckBox) findViewById(R.id.cb_state);
	}

	public SettingItemView(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * 从布局文件中创建实例时,且没有样式的时，会调用这个构造方法
	 * @param context
	 * @param attrs
	 */
	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		
		//从布局文件中获取属性的值
		String title = attrs.getAttributeValue(NAMESPACE, "title");
		tvTitle.setText(title);
		
		descOn = attrs.getAttributeValue(NAMESPACE, "descOn");
		descOff = attrs.getAttributeValue(NAMESPACE, "descOff");
		
		tvDesc.setText(descOff);
	}

	/**
	 * 从布局文件中创建实例时,且有样式的时，会调用这个构造方法
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}
	public void setTitle(int resId) {
		tvTitle.setText(resId);
	}
	
	public boolean isChecked() {
		return cbState.isChecked();
	}
	
	public void setChecked(boolean checked) {
		cbState.setChecked(checked);
		if(checked) {
			tvDesc.setText(descOn);
		} else {
			tvDesc.setText(descOff);
		}
	}

	public void setDescOn(String descOn) {
		this.descOn = descOn;
	}

	public void setDescOff(String descOff) {
		this.descOff = descOff;
	}
}