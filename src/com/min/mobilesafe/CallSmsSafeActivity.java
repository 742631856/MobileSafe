package com.min.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.min.mobilesafe.db.dao.BlackNumberDao;
import com.min.mobilesafe.domain.BlackNumberInfo;

public class CallSmsSafeActivity extends Activity {
	
	private ListView listView;
	private List<BlackNumberInfo> infos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		
		listView = (ListView) findViewById(R.id.lv_number);
		infos = new BlackNumberDao(this).findAll();
		
		listView.setAdapter(new MyListAdapter());
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
				convertView.setTag(holder);//保存在tag中
			} else {
				holder = (ViewHolder) convertView.getTag();//复用的时候直接取出来
			}
			
			BlackNumberInfo info = infos.get(position);
			holder.tvNumber.setText(info.getNumber());
			holder.tvMode.setText(info.getModeString());
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
	
	class ViewHolder {
		TextView tvNumber;
		TextView tvMode;
	}
}
