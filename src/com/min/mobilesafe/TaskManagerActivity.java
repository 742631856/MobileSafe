package com.min.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.min.mobilesafe.domain.TaskInfo;
import com.min.mobilesafe.engine.TaskInfoProvider;
import com.min.mobilesafe.utils.SystemInfoUtils;

public class TaskManagerActivity extends Activity {
	
	private String packName;
	private String totalMem;	//总内存
	private long availMem;	//可用内存
	private int processCount;	//可用内存
	
	private SharedPreferences sp;
	private boolean isShowSysPro;	//是否显示系统进程
	
	private TextView tvProcCount;	//所有进程统计
	private TextView tvMemInfo;		//内存信息
	private TextView tvState;		//内存信息
	private View loadingView;		//加载视图
	private ListView lvProcInfo;	//进程列表
	
	private TaskInfoAdapter taskInfoAdapter;
	private List<TaskInfo> sysTaskInfos;	//系统任务
	private List<TaskInfo> userTaskInfos;	//用户任务

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		sp = getSharedPreferences(SPKeys.KEY_SP_NAME, MODE_PRIVATE);
		isShowSysPro = sp.getBoolean(SPKeys.KEY_SHOW_SYSTEM_PROCESS, false);
		
		packName = getPackageName();
		tvProcCount = (TextView) findViewById(R.id.tv_proc_count);
		tvMemInfo = (TextView) findViewById(R.id.tv_mem_info);
		tvState = (TextView) findViewById(R.id.tv_user_proc);
		loadingView = findViewById(R.id.ll_loading);
		lvProcInfo = (ListView) findViewById(R.id.lv_proc_info);
		
		setTitle();
		loadingView.setVisibility(View.VISIBLE);
		
		setListener();
//		loadProcInfo();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (sp.getBoolean(SPKeys.KEY_SHOW_SYSTEM_PROCESS, false) != isShowSysPro) {
			isShowSysPro = !isShowSysPro;
//			setTitle();
//			taskInfoAdapter.notifyDataSetChanged();
		}
		loadProcInfo();
	}

	private void setListener() {
		lvProcInfo.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (!isShowSysPro) {
					if (null != userTaskInfos) {
						tvState.setText("用户进程：" + userTaskInfos.size() + "个");
					}
					return;
				}
				if (null != sysTaskInfos && null != userTaskInfos) {
					if (firstVisibleItem < sysTaskInfos.size()) {
						tvState.setText("系统进程：" + sysTaskInfos.size() + "个");
					} else {
						tvState.setText("用户进程：" + userTaskInfos.size() + "个");
					}
				}
			}
		});
		lvProcInfo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskInfo taskInfo = null;
				if (0 == position || sysTaskInfos.size() + 1 == position) {
					return;
				} else if (position <= sysTaskInfos.size()) {
					taskInfo = sysTaskInfos.get(position - 1);
				} else {
					taskInfo = userTaskInfos.get(position - 1 - sysTaskInfos.size() - 1);
				}
				ViewHolder holder = (ViewHolder) view.getTag();
				if (packName.equals(taskInfo.packName)) {
					return;
				}
				taskInfo.isChecked = !taskInfo.isChecked;
				holder.cbState.setChecked(taskInfo.isChecked);
			}
		});
	}

	private void setTitle() {
		if (isShowSysPro) {
			if (null != sysTaskInfos && null != userTaskInfos) {
				processCount = sysTaskInfos.size() + userTaskInfos.size();
			}
		} else if (null != userTaskInfos) {
			processCount = userTaskInfos.size();
		}
		tvProcCount.setText("运行中的进程:" + processCount + "个");
		tvMemInfo.setText("可用/总共: " + Formatter.formatFileSize(this, availMem) + "/" + totalMem);
	}

	private void loadProcInfo() {
		new Thread() {
			public void run() {
				if (null != sysTaskInfos) {
					sysTaskInfos.clear();
					sysTaskInfos = null;
				}
				if (null != userTaskInfos) {
					userTaskInfos.clear();
					userTaskInfos = null;
				}
				
				totalMem = Formatter.formatFileSize(TaskManagerActivity.this, SystemInfoUtils.getTotalMem(TaskManagerActivity.this));
				availMem = SystemInfoUtils.getAvailMem(TaskManagerActivity.this);
				
				List<TaskInfo> taskInfos = TaskInfoProvider.getAppInfos(TaskManagerActivity.this);
				sysTaskInfos = new ArrayList<>();
				userTaskInfos = new ArrayList<>();
				for (TaskInfo taskInfo : taskInfos) {
					if (taskInfo.userTask) {
						userTaskInfos.add(taskInfo);
					} else {
						sysTaskInfos.add(taskInfo);
					}
				}
				
				if (isShowSysPro) {
					processCount = SystemInfoUtils.getRunningProcessCount(TaskManagerActivity.this);
				} else {
					processCount = userTaskInfos.size();
				}
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setTitle();
						loadingView.setVisibility(View.INVISIBLE);
						if (null == taskInfoAdapter) {
							taskInfoAdapter = new TaskInfoAdapter();
							lvProcInfo.setAdapter(taskInfoAdapter);
						} else {
							taskInfoAdapter.notifyDataSetChanged();
						}
					}
				});
			};
		}.start();
	}
	
	/**
	 * 全选
	 * @param view
	 */
	public void selectAll(View view) {
		if (null == taskInfoAdapter) {
			return;
		}
		for (TaskInfo info : sysTaskInfos) {
			info.isChecked = true;
		}
		for (TaskInfo info : userTaskInfos) {
			if (packName.equals(info.packName)) {
				continue;
			}
			info.isChecked = true;
		}
		taskInfoAdapter.notifyDataSetChanged();
	}
	/**
	 * 反选
	 * @param view
	 */
	public void selectRev(View view) {
		if (null == taskInfoAdapter) {
			return;
		}
		for (TaskInfo info : sysTaskInfos) {
			info.isChecked = !info.isChecked;
		}
		for (TaskInfo info : userTaskInfos) {
			if (packName.equals(info.packName)) {
				continue;
			}
			info.isChecked = !info.isChecked;
		}
		taskInfoAdapter.notifyDataSetChanged();
	}
	/**
	 * 清除
	 * @param view
	 */
	public void kill(View view) {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		long freeMem = 0;
		TaskInfo info = null;
		for (int i=userTaskInfos.size()-1; i>=0; --i) {
			info = userTaskInfos.get(i);
			if (info.isChecked) {
				am.killBackgroundProcesses(info.packName);
				userTaskInfos.remove(i);
				freeMem += info.memSize;
				--processCount;
			}
		}
		for (int i=sysTaskInfos.size()-1; i>=0; --i) {
			info = sysTaskInfos.get(i);
			if (info.isChecked) {
				am.killBackgroundProcesses(info.packName);
				sysTaskInfos.remove(i);
				freeMem += info.memSize;
				--processCount;
			}
		}
		availMem += freeMem;
		setTitle();
		taskInfoAdapter.notifyDataSetChanged();
		Toast.makeText(this, "共释放" + Formatter.formatFileSize(this, freeMem) + "内存", Toast.LENGTH_SHORT).show();
	}
	/**
	 * 设置
	 * @param view
	 */
	public void enterSetting(View view) {
		Intent intent = new Intent(this, TaskSettingActivity.class);
		startActivity(intent);
	}
	
	class TaskInfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (!isShowSysPro) {
				return userTaskInfos.size() + 1;
			}
			return sysTaskInfos.size() + userTaskInfos.size() + 2;
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
			
			if (isShowSysPro) {
				if (0 == position) {//分组1
					TextView tv = new TextView(TaskManagerActivity.this);
					tv.setBackgroundColor(Color.GRAY);
					tv.setText("系统进程：" + sysTaskInfos.size() + "个");
					return tv;
				} else if (sysTaskInfos.size() + 1 == position) {//分组2
					TextView tv = new TextView(TaskManagerActivity.this);
					tv.setBackgroundColor(Color.GRAY);
					tv.setText("用户进程：" + userTaskInfos.size() + "个");
					return tv;
				}
			} else {
				if (0 == position) {//分组1
					TextView tv = new TextView(TaskManagerActivity.this);
					tv.setBackgroundColor(Color.GRAY);
					tv.setText("用户进程：" + userTaskInfos.size() + "个");
					return tv;
				}
			}
			ViewHolder holder = null;
			if (null == convertView || convertView instanceof TextView) {//优化内存调用
				convertView = View.inflate(TaskManagerActivity.this, R.layout.task_manager_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.name = (TextView) convertView.findViewById(R.id.tv_task_name);
				holder.tvMemsize = (TextView) convertView.findViewById(R.id.tv_task_memsize);
				holder.cbState = (CheckBox) convertView.findViewById(R.id.cb_state);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			TaskInfo taskInfo = null;
			if (isShowSysPro) {
				if (position <= sysTaskInfos.size()) {
					taskInfo = sysTaskInfos.get(position - 1);
				} else {
					taskInfo = userTaskInfos.get(position - sysTaskInfos.size() - 2);
				}
			} else {
				taskInfo = userTaskInfos.get(position - 1);
			}
			
			holder.icon.setImageDrawable(taskInfo.icon);
			holder.name.setText(taskInfo.name);
			holder.tvMemsize.setText("内存占用:" + Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.memSize));
			holder.cbState.setChecked(taskInfo.isChecked);
			if (packName.equals(taskInfo.packName)) {
				holder.cbState.setVisibility(View.INVISIBLE);
			} else {
				holder.cbState.setVisibility(View.VISIBLE);
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
		TextView tvMemsize;
		CheckBox cbState;
	}
}
