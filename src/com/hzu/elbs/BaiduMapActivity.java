package com.hzu.elbs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class BaiduMapActivity extends Activity
{
	BMapManager mbMapMan = null;
	MapView mapView = null;
	LocationData locData = null;
	private MyLocationOverlay myLocationOverlay; 
	private double latitude;
	private double longitude;
	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savaInstanceStates)
	{
		super.onCreate(savaInstanceStates);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		ElbsApplication.getInstance().addActivity(this);
		Intent intent = getIntent();
		String location = intent.getStringExtra("location");
		String[] lsplit = location.split(",");
		this.latitude =new Double(lsplit[0]).doubleValue();
		this.longitude =new Double(lsplit[1]).doubleValue();
		System.out.println(this.latitude + "----longitude ---" + longitude);
		// 注意：请在试用setContentView前初始化BMapManager对象，否则会报错
		mbMapMan = new BMapManager(getApplication());
		mbMapMan.init("AE935717A0C4771071B41A1F40A0AC3BA4290D77", null);
		setContentView(R.layout.baidumap);
		mapView = (MapView) findViewById(R.id.bmapsView);
		// 设置启用内置的缩放控件
		mapView.setBuiltInZoomControls(true);
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		MapController mapController = mapView.getController();
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
 		GeoPoint point =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));
		//GeoPoint point = new GeoPoint((int) (this.latitude * 1E6), (int) (this.longitude * 1E6));
		mapController.setCenter(point);// 设置地图中心点
 		mapController.setZoom(12);// 设置地图zoom级别
	/*	myLocationOverlay = new MyLocationOverlay(mapView);
		locData = new LocationData();
		this.locData.latitude = this.latitude;
		this.locData.longitude = this.longitude;
		locData.direction = 2.0f;
	    myLocationOverlay.setData(locData);
	    mapView.getOverlays().add(myLocationOverlay);
		mapView.refresh();
	 */
	}

	public void btnmapback(View view)
	{
		Intent intent = new Intent(this, SignInActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onDestroy()
	{
		mapView.destroy();
		if (mbMapMan != null)
		{
			mbMapMan.destroy();
			mbMapMan = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause()
	{
		mapView.onPause();
		if (mbMapMan != null)
		{
			mbMapMan.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		mapView.onResume();
		if (mbMapMan != null)
		{
			mbMapMan.start();
		}
		super.onResume();
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	class MyGeneralListener implements MKGeneralListener
	{

		@Override
		public void onGetNetworkState(int iError)
		{
			if (iError == MKEvent.ERROR_NETWORK_CONNECT)
			{
				Toast.makeText(BaiduMapActivity.this, "您的网络出错啦！",
						Toast.LENGTH_LONG).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA)
			{
				Toast.makeText(BaiduMapActivity.this.getApplicationContext(),
						"输入正确的检索条件！", Toast.LENGTH_LONG).show();
			}
			// ...
		}

		@Override
		public void onGetPermissionState(int iError)
		{
			if (iError == MKEvent.ERROR_PERMISSION_DENIED)
			{
				// 授权Key错误：
				Toast.makeText(BaiduMapActivity.this,
						"请在 DemoApplication.java文件输入正确的授权Key！",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
