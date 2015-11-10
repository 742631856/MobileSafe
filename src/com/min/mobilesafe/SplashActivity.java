package com.min.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.min.mobilesafe.utils.StreamTools;

@SuppressLint("HandlerLeak")
public class SplashActivity extends Activity {
	
	public static final String TAG = "SplashActivity";

	private TextView tvVersion;
	private String apkurl;
	private String description;
	
	private TextView tvUpdateInfo;

	protected static final int ENTER_HOME = 0;
	protected static final int HAS_NEW_VERSION = 1;
	protected static final int NETWORK_ERROR = 2;
	protected static final int JSON_ERROR = 3;
	protected static final int IO_ERROR = 4;
	
	private Handler handler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ENTER_HOME:
				enterHome();
				break;
			case HAS_NEW_VERSION:
				//显示升级
				showUpdateDailog();
				break;
			case NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_LONG).show();
				enterHome();
				break;
			case JSON_ERROR:
				Toast.makeText(SplashActivity.this, "JSON解析异常", Toast.LENGTH_LONG).show();
				enterHome();
				break;
			case IO_ERROR:
				Toast.makeText(SplashActivity.this, "数据读取异常", Toast.LENGTH_LONG).show();
				enterHome();
				break;
			}
		};
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tvVersion = (TextView) findViewById(R.id.tv_splash_version);
		tvVersion.setText("版本号: " + getVersionName());
		
		tvUpdateInfo = (TextView) findViewById(R.id.tv_splash_update_info);
		
		boolean autoUpdate = getSharedPreferences(SPKeys.KEY_SP_NAME, MODE_PRIVATE)
				.getBoolean(SPKeys.KEY_AUTO_UPDATE, false);
		
		if (autoUpdate) {	//判断是否需要检查更新
			checkUpdate();
		} else {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					enterHome();
				}
			}, 2000);	//延迟2秒后执行
		}
		
		//给当前视图添加动画
		AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
		aa.setDuration(4000);
		/*TranslateAnimation tsa = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 1, 
				Animation.RELATIVE_TO_PARENT, 0, 
				Animation.RELATIVE_TO_PARENT, 0, 
				Animation.RELATIVE_TO_PARENT, 0);
		tsa.setDuration(4000);*/
		findViewById(R.id.rl_root_splash).startAnimation(aa);	//动画有效，可以作为“视图切换”动画
		
		copyDB();
	}
	
	/**
	 * 从assets文件夹下拷贝数据库到源码文件夹下, 在这之前要先将外部数据库文件拷贝到assets文件夹下
	 */
	private void copyDB() {
		File file = new File(getFilesDir(), "address.db");
		if (null != file && file.length() > 0) {	//已经拷贝过了
			return;
		}
		
		try {
			InputStream is = getAssets().open("address.db");
			FileOutputStream fos = new FileOutputStream(file);
			byte[] bt = new byte[1024];
			int len = 0;
			while ((len = is.read(bt)) != -1) {
				fos.write(bt, 0, len);
			}
			is.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示升级弹出框
	 */
	protected void showUpdateDailog() {
//		getApplicationContext() 不能用在Builder中, 在这里的两种context是有区别的
		AlertDialog.Builder builder = new Builder(this)
		.setTitle("升级提示")
		.setMessage(description)
		.setOnCancelListener(new OnCancelListener() {//点击返回或 AlertDialog 以外的地方监听
			
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				enterHome();
			}
		})
		.setPositiveButton("立刻升级", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//没有内存卡
					Toast.makeText(getApplicationContext(), "没有内存卡，请插入内存卡后试试", Toast.LENGTH_SHORT).show();
				} else {
					//下载文件, 用第三方库
					FinalHttp finalHttp = new FinalHttp();
					finalHttp.download(apkurl,
							Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobileSafe.apk",
							new AjaxCallBack<File>() {
								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {	//下载失败
									Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
								}
								
								@Override
								public void onLoading(long count, long current) {	//正在下载
									tvUpdateInfo.setVisibility(View.VISIBLE);	//可见
									int x = (int) (current * 100 / current);
									tvUpdateInfo.setText("已下载" + x + "%");
								}
								
								@Override
								public void onSuccess(File t) {	//下载成功, 安装apk
									Intent intent = new Intent(Intent.ACTION_VIEW); 
									intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive"); 
									startActivity(intent);
								}
							});
				}
			}
		})
		.setNegativeButton("下次再说", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterHome();
			}
		});
		builder.show();
	}

	/**
	 * 检查版本
	 */
	private void checkUpdate() {
		new Thread() {

			@Override
			public void run() {
				
				Message msg = handler.obtainMessage();
				long startTime = System.currentTimeMillis();	//程序开始时间
				try {
					URL url = new URL(getString(R.string.app_version));
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(4000);
					if (conn.getResponseCode() == 200) {
						String json = StreamTools.readFromStream(conn.getInputStream());
						Log.i("--->",json);
						JSONObject jobj = new JSONObject(json);
						//{"version":2.0, "description":"有新版本啦，快来下啊，下载送妹子啦", "appurl":"http://10.0.2.2:8080/sdfs.apk"}
						String version = jobj.getString("version");
						description = jobj.getString("description");
						apkurl = jobj.getString("apkurl");
						
						if (getVersionName().equals(version)) {
							msg.what = ENTER_HOME;
						} else {
							msg.what = HAS_NEW_VERSION;
						}
					}
				} catch (MalformedURLException e) {
					msg.what = HAS_NEW_VERSION;
					e.printStackTrace();
				} catch (IOException e) {
					msg.what = IO_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = JSON_ERROR;
					e.printStackTrace();
				} finally {
					long useTime = System.currentTimeMillis() - startTime;
					if (useTime < 2000) {	//在这个页面停留不到2秒钟
						try {
							Thread.sleep(2000 - useTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					msg.sendToTarget();
				}
			}
		}.start();
	}
	
	/**
	 * 得到此apk的版本号
	 * @return
	 */
	public String getVersionName() {
		//管理apk的
		PackageManager pm = getPackageManager();
		try {
			//获取app包信息中的版本信息
			return pm.getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 进入主界面
	 */
	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		
		finish();
	}
}