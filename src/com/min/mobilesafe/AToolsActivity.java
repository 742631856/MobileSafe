package com.min.mobilesafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.min.mobilesafe.utils.SmsUtils;
import com.min.mobilesafe.utils.SmsUtils.SmsBackupCallBack;

/**
 * 高级工具界面
 * @author min
 *
 */
public class AToolsActivity extends Activity {
	
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}
	
	public void numberQuery(View view) {
		Intent intent = new Intent(this, NumberAddressQueryActivity.class);
		startActivity(intent);
	}
	
	public void smsBackup(View view) {
		pd = new ProgressDialog(this);
//		pd.setTitle("正在备份短信");
		pd.setMessage("正在备份短信");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					SmsUtils.smsBackup(getApplicationContext(), new SmsBackupCallBack() {
						
						@Override
						public void onBackup(int current) {
							pd.setProgress(current);
						}
						
						@Override
						public void beforeBackup(int total) {
							pd.setMax(total);
						}
					});
					
					runOnUiThread(new Runnable() {
						public void run() {
							//show()必须在主线程中
							Toast.makeText(getApplicationContext(), "备份完成", Toast.LENGTH_LONG).show();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getApplicationContext(), "备份失败", Toast.LENGTH_LONG).show();
						}
					});
				} finally {
					pd.dismiss();
				}
			}
		}).start();
	}
	
	public void smsRestore(View view) {
		
	}
}
