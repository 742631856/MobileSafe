package com.min.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.min.mobilesafe.SPKeys;

public class GPSService extends Service {
	
	private LocationManager lm;
	private MyLocationListener listener;
	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		sp = getSharedPreferences(SPKeys.KEY_SP_NAME, MODE_PRIVATE);
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(false);							//肯定不许收费啊
		criteria.setAccuracy(Criteria.ACCURACY_FINE);			//精确度要高
		criteria.setSpeedRequired(false);						//速度
		criteria.setPowerRequirement(Criteria.POWER_LOW);		//低电量
		criteria.setAltitudeRequired(false);					//不要海拔
		criteria.setBearingRequired(false);						//不要方向
		String provider = lm.getBestProvider(criteria, true);	//获取最好的定位服务者
		
		listener = new MyLocationListener();
		lm.requestLocationUpdates(provider, 0, 50, listener);	//0秒表示默认为1分钟，
	}
	
	@Override
	public void onDestroy() {
		lm.removeUpdates(listener);
		listener = null;
		lm = null;
		sp = null;
		super.onDestroy();
	}
	
	class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			String lat = "la: " + location.getLatitude() + "\n";		//纬度
			String lon = "lo: " + location.getLongitude() + "\n";	//经度
			String meter = "a: " + location.getAccuracy();		//精确度多少米
			//最好是转换成中国的火星坐标后再保存
			
			Editor edit = sp.edit();
			edit.putString(SPKeys.KEY_LOCATION, lat + lon + meter);
			edit.commit();
			edit = null;
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
		
	}
}
