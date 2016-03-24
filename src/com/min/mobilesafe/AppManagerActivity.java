package com.min.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.min.mobilesafe.domain.AppInfo;
import com.min.mobilesafe.engine.AppInfoProvider;

public class AppManagerActivity extends Activity {

	private TextView tvRom;
	private TextView tvSDCard;
	private View ll_loading;
	private ListView lvAppInfo;
	
	private List<AppInfo> userAppInfos;
	private List<AppInfo> sysAppInfos;
	
	/**
	 * listview的状态
	 */
	private TextView tvState;
	/**
	 * 弹出框显示的类容
	 */
//	private TextView tvPop = null;
	private PopupWindow popWin;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		
		ll_loading = findViewById(R.id.ll_loading);
		ll_loading.setVisibility(View.VISIBLE);
		lvAppInfo = (ListView) findViewById(R.id.lv_app_info);
		tvState = (TextView) findViewById(R.id.tv_state);
		
		tvRom = (TextView) findViewById(R.id.tv_avial_rom);
		tvSDCard = (TextView) findViewById(R.id.tv_avial_sdcard);
		
		long dataSize = getAviableSpace(Environment.getDataDirectory().getAbsolutePath());//系统盘
		long sdSzie = getAviableSpace(Environment.getExternalStorageDirectory().getAbsolutePath());//
		
		tvRom.setText("内存可用: " + Formatter.formatFileSize(this, dataSize));
		tvSDCard.setText("SD卡可用:  " + Formatter.formatFileSize(this, sdSzie));
		
		getAppInfos();
		lvAppInfo.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (null != sysAppInfos && null != userAppInfos) {
					if (firstVisibleItem < sysAppInfos.size()) {
						tvState.setText("系统程序：" + sysAppInfos.size() + "个");
					} else {
						tvState.setText("应用程序：" + userAppInfos.size() + "个");
					}
					
					if (null != popWin && popWin.isShowing()) {//弹出窗体还在显示的时候
						popWin.dismiss();
					}
				}
			}
		});
		
		lvAppInfo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				/*AppInfo appInfo = null;
				if (0 == position || sysAppInfos.size() + 1 == position) {
					return;
				} else if (position <= sysAppInfos.size()) {
					appInfo = sysAppInfos.get(position - 1);
				} else {
					appInfo = userAppInfos.get(position - 1 - sysAppInfos.size() - 1);
				}*/
				dismissPopWindow();	//去掉以前的
				
//				tvPop.setText(appInfo.packName);
				int location[] = new int[2] ;
				view.getLocationInWindow(location);//获取点击视图的坐标
				popWin.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, location[0], location[1]);
			}
		});
	}
	
	private void dismissPopWindow() {
		if (null == popWin) {
			View view = View.inflate(this, R.layout.popup_app_item, null);
			//弹出窗口，包裹类容容
			popWin = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		popWin.dismiss();
	}
	
	private void getAppInfos() {
		new Thread() {
			public void run() {
				List<AppInfo> appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				userAppInfos = new ArrayList<>();
				sysAppInfos = new ArrayList<>();
				
				for (AppInfo info : appInfos) {
					if (info.userApp) {
						userAppInfos.add(info);
					} else {
						sysAppInfos.add(info);
					}
				}
				appInfos.clear();
				appInfos = null;
				
				runOnUiThread(new Runnable() {
					public void run() {
						lvAppInfo.setAdapter(new AppAdapter());
						ll_loading.setVisibility(View.INVISIBLE);
					}
				});
			};
		}.start();
	}
	
	private long getAviableSpace(String path) {
		StatFs statfs = new StatFs(path);
		long blocks = statfs.getAvailableBlocks();
		int size = statfs.getBlockSize();
		return blocks * size;
	}
	
	@Override
	protected void onDestroy() {
		if (null != popWin && popWin.isShowing()) {	//不这样做会出现 窗体内存泄露
			dismissPopWindow();
//			tvPop = null;
			popWin = null;
		}
		super.onDestroy();
	}
	
	class AppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return sysAppInfos.size() + userAppInfos.size() + 2;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (0 == position) {
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("系统程序：" + sysAppInfos.size() + "个");
				return tv;
			} else if (sysAppInfos.size() + 1 == position) {
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("应用程序：" + userAppInfos.size() + "个");
				return tv;
			}
			ViewHolder holder = null;
			if (null == convertView || convertView instanceof TextView) {//优化内存调用
				convertView = View.inflate(AppManagerActivity.this, R.layout.app_manager_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.name = (TextView) convertView.findViewById(R.id.tv_app_name);
				holder.tvIsRom = (TextView) convertView.findViewById(R.id.tv_app_inrom);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			AppInfo appInfo = null;
			if (position <= sysAppInfos.size()) {
				appInfo = sysAppInfos.get(position - 1);
			} else {
				appInfo = userAppInfos.get(position - sysAppInfos.size() - 2);
			}
			
			holder.icon.setImageDrawable(appInfo.icon);
			holder.name.setText(appInfo.name);
			if (appInfo.inRom) {
				holder.tvIsRom.setText("手机内存");
			} else {
				holder.tvIsRom.setText("外部存储");
			}
			return convertView;
		}
	}
	
	/**
	 * View的容器
	 * @author min
	 *
	 */
	static class ViewHolder {
		ImageView icon;
		TextView name;
		TextView tvIsRom;
	}
}
