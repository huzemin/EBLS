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
		// ע�⣺��������setContentViewǰ��ʼ��BMapManager���󣬷���ᱨ��
		mbMapMan = new BMapManager(getApplication());
		mbMapMan.init("AE935717A0C4771071B41A1F40A0AC3BA4290D77", null);
		setContentView(R.layout.baidumap);
		mapView = (MapView) findViewById(R.id.bmapsView);
		// �����������õ����ſؼ�
		mapView.setBuiltInZoomControls(true);
		// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		MapController mapController = mapView.getController();
		// �ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
 		GeoPoint point =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));
		//GeoPoint point = new GeoPoint((int) (this.latitude * 1E6), (int) (this.longitude * 1E6));
		mapController.setCenter(point);// ���õ�ͼ���ĵ�
 		mapController.setZoom(12);// ���õ�ͼzoom����
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

	// �����¼���������������ͨ�������������Ȩ��֤�����
	class MyGeneralListener implements MKGeneralListener
	{

		@Override
		public void onGetNetworkState(int iError)
		{
			if (iError == MKEvent.ERROR_NETWORK_CONNECT)
			{
				Toast.makeText(BaiduMapActivity.this, "���������������",
						Toast.LENGTH_LONG).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA)
			{
				Toast.makeText(BaiduMapActivity.this.getApplicationContext(),
						"������ȷ�ļ���������", Toast.LENGTH_LONG).show();
			}
			// ...
		}

		@Override
		public void onGetPermissionState(int iError)
		{
			if (iError == MKEvent.ERROR_PERMISSION_DENIED)
			{
				// ��ȨKey����
				Toast.makeText(BaiduMapActivity.this,
						"���� DemoApplication.java�ļ�������ȷ����ȨKey��",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
