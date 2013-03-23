package com.hzu.elbs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.hzu.elbs.conf.ConfigInfo;
import com.hzu.elbs.net.ElbsHttpServer;
import com.hzu.elbs.utils.JsonUtils;
import com.hzu.elbs.utils.SharedPreferencesUtils;

public class SignInActivity extends Activity implements OnItemClickListener
{

	private ListView signinlistview;
	private Button backBtn;
	private ImageButton signinBtn;
	private TextView nodata;
	private ElbsHttpServer elbs;
	private MySimpleAdapter slist;
	private List<Map<String, String>> list;
	/*
	 * 百度定位
	 */
	BMapManager mbMapMan = null;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListenner();
	public LocationData locData = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.signin);
		ElbsApplication.getInstance().addActivity(this);
		// 寻找组件
		this.nodata = (TextView) findViewById(R.id.nosignindata);
		this.signinlistview = (ListView) findViewById(R.id.signinlist);
		this.backBtn = (Button) findViewById(R.id.back_btn);
		this.backBtn.setOnClickListener(new BtnOnClickListener());
		this.signinBtn = (ImageButton) findViewById(R.id.signin_btn);
		this.signinBtn.setOnClickListener(new BtnOnClickListener());
		// 初始化HttpClient
		this.elbs = new ElbsHttpServer(ConfigInfo.BASE_URL);
		list = getSigninList();
		this.slist = getmyAdapter(list);
		this.signinlistview.setAdapter(slist);
		this.signinlistview.setOnItemClickListener(this);
		/*
		 * 百度定位
		 */
		mbMapMan = new BMapManager(getApplication());
		mbMapMan.init("AE935717A0C4771071B41A1F40A0AC3BA4290D77", null);
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		this.locData = new LocationData();
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setProdName("elbs");
		option.setPriority(LocationClientOption.GpsFirst);
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);// 禁止启用缓存定位
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted())
		{
			mLocationClient.requestLocation();
		} else
			Log.d("LocSDK3", "locClient is null or not started");
	}

	public MySimpleAdapter getmyAdapter(List<Map<String, String>> list)
	{
		MySimpleAdapter slist = new MySimpleAdapter(this, list,
				R.layout.signinlistitem, new String[] { "imgpath", "content",
						"signintime", "username", "uid", "location" },
				new int[] { R.id.headimage, R.id.signincontent,
						R.id.signintime, R.id.addplanuser, R.id.uid,
						R.id.location });
		return slist;
	}

	class BtnOnClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			// 返回按钮的单击动作
			if (v.getId() == R.id.back_btn)
			{
				Intent intent = new Intent();
				intent.setClass(SignInActivity.this, MainActivity.class);
				SignInActivity.this.startActivity(intent);
			} else if (v.getId() == R.id.signin_btn) // 签到图像按钮的单击动作
			{
				LayoutInflater factory = LayoutInflater
						.from(SignInActivity.this);
				final View signincontent = factory.inflate(
						R.layout.signincontext, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SignInActivity.this);
				builder.setTitle("签到：");
				builder.setView(signincontent);
				builder.setPositiveButton("签到",
						new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog,
									int which)
							{
								EditText content = (EditText) signincontent
										.findViewById(R.id.s_content);
								String content_text = content.getText() == null ? "No Content"
										: content.getText().toString().trim();
								double latitude = locData.latitude;

								double longitude = locData.longitude;
								String location = "" + latitude + ", "
										+ longitude;
								if (content_text == null
										|| content_text.equals(""))
								{
									content_text = "No Content";
								}
								String uid = SharedPreferencesUtils
										.getDataByName(SignInActivity.this,
												ConfigInfo.SP_USER_NAME, "uid");
								List<NameValuePair> params = new ArrayList<NameValuePair>();
								params.add(new BasicNameValuePair("action",
										"signin"));
								System.out.println(uid);
								params.add(new BasicNameValuePair("uid", uid));
								params.add(new BasicNameValuePair("content",
										content_text));

								params.add(new BasicNameValuePair("location",
										location));

								String responces = elbs.requestByPost(params);
								System.out.println(responces);
								try
								{
									JSONObject json = new JSONObject(responces);
									String msg = json.getString("msg");
									SignInActivity.this.show(msg);
									show("正在刷新...");
									nodata.setVisibility(View.GONE);
									signinlistview.setVisibility(View.VISIBLE);
									// 设置ListView重新刷新
									list = getSigninList();
									MySimpleAdapter adapter = getmyAdapter(list);
									signinlistview.setAdapter(adapter);
									adapter.notifyDataSetChanged();
								} catch (JSONException e)
								{
									e.printStackTrace();
								}
							}
						});
				builder.setNegativeButton("取消", null);
				builder.create().show();
			}
		}

	}

	public void show(String msg)
	{
		Toast.makeText(SignInActivity.this, msg, Toast.LENGTH_LONG).show();
	}

	// 请求签到信息
	public List<Map<String, String>> getSigninList()
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "showallsignin"));
		System.out.println("Post.....===========");
		String str = elbs.requestByPost(params);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		JSONObject strjson = null;
		try
		{
			strjson = new JSONObject(str);
			JSONArray signindatas = strjson.getJSONArray("data");
			if (signindatas.length() == 0)
			{
				this.nodata.setVisibility(View.VISIBLE);
				this.signinlistview.setVisibility(View.GONE);
			}
			for (int i = 0; i < signindatas.length(); i++)
			{
				JSONObject sgin = signindatas.getJSONObject(i);
				Map<String, String> map = JsonUtils.toMap(sgin, null);
				list.add(map);
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return list;
	}

	class MySimpleAdapter extends SimpleAdapter
	{
		int count = 0;
		private List<Map<String, String>> mItemList;

		public MySimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to)
		{
			super(context, data, resource, from, to);
			mItemList = (List<Map<String, String>>) data;
			if (data == null)
			{
				count = 0;
			} else
			{
				count = mItemList.size();
			}
		}

		public int getCount()
		{
			return mItemList.size();
		}

		public Object getItem(int pos)
		{
			return pos;
		}

		public long getItemId(int pos)
		{
			System.out.println(pos);
			return pos;
		}

		@Override
		public void setViewImage(ImageView v, String value)
		{
			Bitmap bitmap = null;/* WebBitImage.getHttpBitmap(value); */
			Drawable defaultImage = null;
			if (bitmap == null)
			{
				defaultImage = getResources().getDrawable(R.drawable.xiaohei);
				v.setImageDrawable(defaultImage);
			} else
			{
				((ImageView) v).setImageBitmap(bitmap);
			}

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			Log.e("Test", position + "");
			Map<String, String> map = this.mItemList.get(position);
			View view = super.getView(position, convertView, parent);
			ImageView headimage = (ImageView) view.findViewById(R.id.headimage);

			TextView uid = (TextView) view.findViewById(R.id.uid);
			TextView username = (TextView) view.findViewById(R.id.addplanuser);
			TextView signincontent = (TextView) view
					.findViewById(R.id.signincontent);
			TextView location = (TextView) view.findViewById(R.id.location);
			TextView signintime = (TextView) view.findViewById(R.id.signintime);
			// 设置头像
			setViewImage(headimage, null);
			uid.setText(map.get("uid"));
			username.setText(map.get("username"));
			signincontent.setText(map.get("content"));
			signintime.setText(map.get("signintime"));
			location.setText(map.get("location"));
			return view;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		this.mLocationClient.stop();
		Map<String,String> signin = list.get(arg2);
		String location = signin.get("location");
		Intent intent = new Intent(this, BaiduMapActivity.class);
		intent.putExtra("location", location);
		startActivity(intent);
	}

	@Override
	public void onDestroy()
	{
		if (mLocationClient != null && mLocationClient.isStarted())
		{
			mLocationClient.stop();
			mLocationClient = null;
		}
		super.onDestroy();
	}

	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener
	{
		@Override
		public void onReceiveLocation(BDLocation location)
		{
			if (location == null)
				return;

			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			Log.d("loctest",
					String.format("before: lat: %f lon: %f",
							location.getLatitude(), location.getLongitude()));
		}

		public void onReceivePoi(BDLocation poiLocation)
		{
			if (poiLocation == null)
			{
				return;
			}
		}
	}
}
