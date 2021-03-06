package com.min.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.min.mobilesafe.db.dao.BlackNumberDao;
import com.min.mobilesafe.domain.BlackNumberInfo;

public class CallSmsSafeActivity extends Activity {
	
	private ListView listView;
	private BlackNumberDao blackNumDao;
	private List<BlackNumberInfo> infos;
	private MyListAdapter adapter;
	
	private int offset;	//已经了多少个数据了
	private int len;	//每次加载多少个

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		
		listView = (ListView) findViewById(R.id.lv_number);
		
		listView.setOnScrollListener(new OnScrollListener() {
			
			//滚动状态发生变化的时候
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
					case OnScrollListener.SCROLL_STATE_IDLE:			//空闲状态
						if (view.getLastVisiblePosition() == offset-1) {
							loadData();
//							Log.i("--->", "--->加载新的");
						}
						break;
					case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:	//手指触摸滚动状态
						break;
					case OnScrollListener.SCROLL_STATE_FLING:			//惯性滑动状态
						break;
					default:
						break;
				}
			}
			
			//正在滚动的时候
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if ((firstVisibleItem + visibleItemCount) == offset) {
//					loadData();
					Log.i("--->", "--->加载新的");
				}
//				Log.i("--->", "--->第一个可见的" + firstVisibleItem);
//				Log.i("--->", "--->一共可见的" + visibleItemCount);
//				Log.i("--->", "--->最后可见的" + view.getLastVisiblePosition());
			}
		});
		
		adapter = new MyListAdapter();
		blackNumDao = new BlackNumberDao(this);
		offset = 0;
		len = 20;
		loadData();
		
		//添加黑名单按钮
		Button btnAdd = (Button) findViewById(R.id.btn_add);
		btnAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addBlackNumber(v);
			}
		});
	}
	
	public void loadData() {
		new Thread() {
			@Override
			public void run() {
				if (null == infos) {
					infos = blackNumDao.findPart(offset, len);
					offset = infos.size();
					runOnUiThread(new Runnable() {
						public void run() {
							listView.setAdapter(adapter);
						}
					});
				} else {
					infos.addAll(blackNumDao.findPart(offset, len));
					if (infos.size() > offset) {
						offset = infos.size();
						runOnUiThread(new Runnable() {
							public void run() {
								adapter.notifyDataSetChanged();
							}
						});
					}
				}
			};
		}.start();
	}
	
	public void addBlackNumber(View view) {
		//这里的上下文不能用getApplicationContext(),会崩溃
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		//这里的上下文不能用getApplicationContext()，会出现加载的视图主题有问题
		final View alertView = View.inflate(this, R.layout.dialog_add_blacknumber, null);
		Button btnOK = (Button) alertView.findViewById(R.id.btn_ok);
		//确定添加
		btnOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				EditText et = (EditText) alertView.findViewById(R.id.et_phone);
				String num = et.getEditableText().toString();
				if (num.length() <= 0) {
					Toast.makeText(CallSmsSafeActivity.this, "请输入电话号码", Toast.LENGTH_LONG).show();
					return;
				}
				CheckBox cbPhone = (CheckBox) alertView.findViewById(R.id.cb_phone);
				CheckBox cbSms = (CheckBox) alertView.findViewById(R.id.cb_sms);
				BlackNumberDao blackNumberDao = new BlackNumberDao(CallSmsSafeActivity.this);
				if (cbPhone.isChecked() && cbSms.isChecked()) {
					blackNumberDao.add(num, "3");
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo(num, "3");
					infos.add(0, blackNumberInfo);
				} else if (cbSms.isChecked()) {
					blackNumberDao.add(num, "2");
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo(num, "2");
					infos.add(0, blackNumberInfo);
				} else if (cbPhone.isChecked()) {
					blackNumberDao.add(num, "1");
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo(num, "1");
					infos.add(0, blackNumberInfo);
				} else {
					Toast.makeText(CallSmsSafeActivity.this, "请选择拦截类型", Toast.LENGTH_LONG).show();
					return;
				}
				adapter.notifyDataSetChanged();//通知listView数据发生变化了
				dialog.dismiss();
			}
		});
		Button btnCancel = (Button) alertView.findViewById(R.id.btn_cancel);
		//取消添加
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		
		dialog.setView(alertView, 0, 0, 0, 0);
		dialog.show();
	}
	
	class MyListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if (null == convertView) {//优化内存调用
				convertView = View.inflate(CallSmsSafeActivity.this, R.layout.list_item_callsms, null);
				TextView tvNumber = (TextView) convertView.findViewById(R.id.tv_number);
				TextView tvMode = (TextView) convertView.findViewById(R.id.tv_mode);
				//把找到的子实例保存起来
				holder = new ViewHolder();
				holder.tvNumber = tvNumber;
				holder.tvMode = tvMode;
				ImageButton btn = (ImageButton) convertView.findViewById(R.id.btn_delete);
				holder.btnDelete = btn;
				convertView.setTag(holder);//保存在tag中
				
				//item上的删除按钮
				btn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						final int posi = (int) v.getTag();
						new AlertDialog.Builder(CallSmsSafeActivity.this)
						.setTitle("提示")
						.setMessage("你要删除吗?")
						.setPositiveButton("确定", new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								BlackNumberInfo blackNumberInfo = infos.remove(posi);
								BlackNumberDao blackNumberDao = new BlackNumberDao(CallSmsSafeActivity.this);
								blackNumberDao.delete(blackNumberInfo.getNumber());
								adapter.notifyDataSetChanged();//list重新加载数据
								dialog.dismiss();
							}
						})
						.setNegativeButton("取消", null)
						.show();
						
					}
				});
			} else {
				holder = (ViewHolder) convertView.getTag();//复用的时候直接取出来
			}
			
			BlackNumberInfo info = infos.get(position);
			holder.tvNumber.setText(info.getNumber());
			holder.tvMode.setText(info.getModeString());
			holder.btnDelete.setTag(position);
			
			return convertView;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
																																																																																																	return 0;
		}
	}
	
	/**
	 * View的容器
	 * @author min
	 *
	 */
	static class ViewHolder {
		TextView tvNumber;
		TextView tvMode;
		ImageButton btnDelete;
	}
}
