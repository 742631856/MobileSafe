package com.min.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.min.mobilesafe.receiver.AdminReceiver;
import com.min.mobilesafe.utils.MD5Utils;

/**
 * 主菜单界面
 * @author min
 *
 */
public class HomeActivity extends Activity {
	
	protected static final String TAG = "HomeActivity";

	private static final int REQUEST_CODE_ENABLE_ADMIN = 1101;

	private static String funs[] = {
		"手机防盗", "通讯卫士", "软件管理",
		"进程管理", "流量统计", "手机杀毒",
		"缓存清理", "高级工具", "设置中心"
	};
	
	private static int imgIds[] = {
		R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
		R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan,
		R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings
	};
	
	private SharedPreferences sp;
	private GridView gridView;	//功能列表
	private MyAdapter gridAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences(SPKeys.KEY_SP_NAME, MODE_PRIVATE);
		
		gridView = (GridView) findViewById(R.id.home_gv);
		
		gridAdapter = new MyAdapter();
		gridView.setAdapter(gridAdapter);
		
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = null;
				switch (position) {
				case 8:	//设置
					intent = new Intent(HomeActivity.this, SettingActivity.class);
					break;
				case 0:
					showLostFindDailog();
					break;
				}
				if (null != intent) {
					startActivity(intent);
				}
			}
		});
		
		addAdmin();
	}
	
	/**
	 * 手机防盗
	 */
	protected void showLostFindDailog() {
		if (isSetupPwd()) {
			showEnterPwdDailog();
		} else {
			showSetPwdDailog();
		}
	}


	private EditText etPwd;
	private EditText etPwd2;
	private Button btnOK;
	private Button btnCancel;
	private AlertDialog dialog;
	
	/**
	 * 显示设置密码界面
	 */
	private void showSetPwdDailog() {
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dailog_set_password, null);
		etPwd = (EditText) view.findViewById(R.id.dsp_et_pwd);
		etPwd2 = (EditText) view.findViewById(R.id.dsp_et_pwd2);
		btnOK = (Button) view.findViewById(R.id.dsp_btn_ok);
		btnCancel = (Button) view.findViewById(R.id.dsp_btn_cancel);
		
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String pwd = etPwd.getText().toString().trim();
				String pwd2 = etPwd2.getText().toString().trim();
				if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd2)) {
					Toast.makeText(HomeActivity.this, "密码为空", Toast.LENGTH_LONG).show();
					return;
				}
				
				if (!pwd.equals(pwd2)) {
					Toast.makeText(HomeActivity.this, "密码不一致", Toast.LENGTH_LONG).show();
					return;
				}
				
				pwd = MD5Utils.md5Password(pwd);//加密
				
				Editor editor = sp.edit();
				editor.putString(SPKeys.KEY_PASSWORD, pwd);
				editor.commit();
				Log.i(TAG, "---> + 密码已保存");
				dialog.dismiss();
				Log.i(TAG, "---> + 进入防盗界面");
				Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
				startActivity(intent);
			}
		});
		
		builder.setView(view);
		dialog = builder.show();
	}
	
	/**
	 * 显示输入密码弹出框
	 */
	private void showEnterPwdDailog() {
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dailog_enter_password, null);
		etPwd = (EditText) view.findViewById(R.id.et_pwd);
		btnOK = (Button) view.findViewById(R.id.btn_ok);
		btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String pwd = etPwd.getText().toString().trim();
				if (TextUtils.isEmpty(pwd)) {
					Toast.makeText(HomeActivity.this, "密码为空", Toast.LENGTH_LONG).show();
					return;
				}
				
				pwd = MD5Utils.md5Password(pwd);//加密
				
				String pwdInSp = sp.getString(SPKeys.KEY_PASSWORD, "");
				if (!pwd.equals(pwdInSp)) {
					Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_LONG).show();
					return;
				}
				dialog.dismiss();
				Log.i(TAG, "---> + 打开防丢界面");
				Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
				startActivity(intent);
			}
		});
		
//		builder.setView(view);
//		dialog = builder.show();
		//为了适配4.0以下的，用下面的方法
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);//不留边框
		dialog.show();
	}

	/**
	 * 是否设置过密码
	 * @return
	 */
	private boolean isSetupPwd() {
		String pwd = sp.getString(SPKeys.KEY_PASSWORD, "");
		return !TextUtils.isEmpty(pwd);
	}
	
	/**
	 * 添加管理员
	 */
	private void addAdmin() {
		ComponentName deviceAdmin = new ComponentName(this, AdminReceiver.class);
		DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		if (!dpm.isAdminActive(deviceAdmin)) {	//当MSDeviceAdminReceiver不是管理员的时候，就要去请求激活
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
	        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin);
	        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
	                getString(R.string.device_admin_description));
	        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
		}
		//在卸载这个app前要移除管理员，不然无法卸载
//		dpm.removeActiveAdmin(deviceAdmin);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		Log.i("--->", "--->" + resultCode + "--" + data);
		//resultCode：0、表示未激活，-1、表示激活
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return funs.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view = View.inflate(HomeActivity.this, R.layout.home_list_item, null);
			
			TextView tv = (TextView) view.findViewById(R.id.tv_item);
			tv.setText(funs[position]);
			
			ImageView iv = (ImageView) view.findViewById(R.id.iv_item);
			iv.setImageResource(imgIds[position]);
			
			return view;
		}
	}
}
