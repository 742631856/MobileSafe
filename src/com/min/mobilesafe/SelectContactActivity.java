package com.min.mobilesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectContactActivity extends Activity {
	
	private ListView lvContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		
		lvContact = (ListView) findViewById(R.id.lv);
		final List<Map<String, String>> list = getContactList();
		lvContact.setAdapter(new SimpleAdapter(this, list, R.layout.select_contact_item, new String[]{"name", "phone"}, new int[]{R.id.tv_name, R.id.tv_phone}));
		
		lvContact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				 String phone = list.get(position).get("phone");
				 Intent intent = new Intent();
				 intent.putExtra("phone", phone);
				 setResult(1001, intent);
				 finish();
			}
		});
	}

	//获取联系人
	private List<Map<String, String>> getContactList() {
		
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		
		//类容解析器
		ContentResolver resolver = getContentResolver();
		Uri uriRaw = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri uriData = Uri.parse("content://com.android.contacts/data");
		
		//先取得联系人的id
		Cursor cursor = resolver.query(uriRaw, new String[]{"contact_id"}, null, null, null);
		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			
			if (null != id) {
				//获取数据				
//				Cursor cursorData = resolver.query(uriData, new String[]{"data1", "mimetype_id", "mimetype"}, "contact_id=?", new String[]{id}, null);
				Cursor cursorData = resolver.query(uriData, new String[]{"data1", "mimetype"}, "contact_id=?", new String[]{id}, null);
				Map<String, String> map = new HashMap<String, String>();
				while (cursorData.moveToNext()) {
					String data1 = cursorData.getString(0);
					String type = cursorData.getString(1);
					if ("vnd.android.cursor.item/phone_v2".equals(type)) {	//取到的是电话号码
						map.put("phone", data1);
					} else if ("vnd.android.cursor.item/name".equals(type)) {	//取到的是名称
						map.put("name", data1);						
					}
				}
				list.add(map);
				cursorData.close();
			}
		}
		cursor.close();
		return list;
	}	
}