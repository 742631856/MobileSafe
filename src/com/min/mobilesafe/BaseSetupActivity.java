package com.min.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	
	protected static final String TAG = "BaseSetupActivity";
	//手势识别器
	private GestureDetector detector;
	protected SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sp = getSharedPreferences(SPKeys.KEY_SP_NAME, MODE_PRIVATE);
		detector = new GestureDetector(this, new SimpleOnGestureListener() {
			
			//当屏幕上有滑动时
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				
				if (Math.abs(velocityX) < 200) {	//滑动太慢
					Log.i(TAG, "--->滑动太慢" + velocityX);
					Toast.makeText(BaseSetupActivity.this, "滑动太慢," + velocityX, Toast.LENGTH_SHORT).show();
					return true;
				}
				
				if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {//不允许斜着滑
					Log.i(TAG, "--->不能斜着滑");
					Toast.makeText(BaseSetupActivity.this, "不能斜着滑", Toast.LENGTH_SHORT).show();
					return true;
				}
				
				float x = e1.getRawX() - e2.getRawX();
				Log.i(TAG, "--->" + x);
//				Toast.makeText(BaseSetupActivity.this, "宽度" + x, Toast.LENGTH_SHORT).show();
				
				if (x < -200) {	//从左向右滑动了
//					Log.i(TAG, "--->右滑");
//					Toast.makeText(BaseSetupActivity.this, "右滑", Toast.LENGTH_SHORT).show();
					showPre();
					return true;
				}
				
				if (x > 200) {	//从右向左滑动了
//					Log.i(TAG, "--->左滑");
//					Toast.makeText(BaseSetupActivity.this, "左滑", Toast.LENGTH_SHORT).show();
					showNext();
					return true;
				}
				
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//使用手势识别器
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	public abstract void showNext();
	public abstract void showPre();
}
