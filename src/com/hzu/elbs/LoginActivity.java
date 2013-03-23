package com.hzu.elbs;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hzu.elbs.conf.ConfigInfo;
import com.hzu.elbs.conf.LogTag;
import com.hzu.elbs.net.ElbsHttpServer;
import com.hzu.elbs.utils.JsonUtils;
import com.hzu.elbs.utils.SharedPreferencesUtils;

public class LoginActivity extends Activity
{
	/*
	 * R.layout.main控件
	 */
	private EditText username;
	private EditText password;
	private Button loginbtn;
	private Button cancelbtn;
	private ImageButton quit;
	/*
	 * 程序使用变量
	 */
	private Boolean loginState = false;
	private String loginLog = "";
	private boolean bindservice = true;
	private JSONObject loginjson;
	/*private Map<String, String> userInfo;*/
	 

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// 去掉标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		ElbsApplication.getInstance().addActivity(this);
		// 查找组件
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		loginbtn = (Button) findViewById(R.id.login);
		cancelbtn = (Button) findViewById(R.id.cancel);
		quit = (ImageButton) findViewById(R.id.quit);
		//网络检查
		CheckNetworkState();
		// 用户登录
		loginbtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				String user = username.getText().toString().trim();
				String pass = password.getText().toString().trim();
				if (user.equals("") && pass.equals(""))
				{
					Toast.makeText(LoginActivity.this, "用户名和密码不能为空",
							Toast.LENGTH_LONG).show();
					return;
				}
				try
				{
					String params = "action=userlogin&username="
							+ URLEncoder.encode(user, "utf-8") + "&password="
							+ URLEncoder.encode(pass, "utf-8");
					userLogin(params);
					//判断登录状态并做出反映
					if (LoginActivity.this.loginState)
					{
						LoginActivity.this.show("登录成功->" + LoginActivity.this.loginLog);
						//保存用户信息
						try
						{
							JSONObject data = loginjson.getJSONObject("data");
							Map<String, String> userinfo = JsonUtils.toMap(data, "user");
							Map<String, String> authinfo = JsonUtils.toMap(data, "auth");
							userinfo.putAll(authinfo);
							SharedPreferencesUtils.storeData(LoginActivity.this, ConfigInfo.SP_USER_NAME, userinfo, MODE_PRIVATE);
							System.out.println(SharedPreferencesUtils.getData(LoginActivity.this, ConfigInfo.SP_USER_NAME, MODE_PRIVATE));
						} catch (JSONException e)
						{
							e.printStackTrace();
						}
						Intent mapintent = new Intent(LoginActivity.this,
								MainActivity.class);
						LoginActivity.this.startActivity(mapintent);
					} else
					{
						// 登录失败显示对话框做出提示
						/*Toast.makeText(MainActivity.this,
								"登录失败->" + MainActivity.this.loginLog,
								Toast.LENGTH_LONG).show();*/
						new AlertDialog.Builder(LoginActivity.this)
								.setTitle("登录失败")
								.setMessage(LoginActivity.this.loginLog)
								.setNegativeButton(R.string.cancel, null).create().show();
						
					}
				} catch (UnsupportedEncodingException e)
				{
					e.printStackTrace();
				}
			}

		});
		// bind server

		cancelbtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				LoginActivity.this.username.setText("");
				LoginActivity.this.password.setText("");
			}
		});

		// 退出程序->先进行确认
		quit.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(
						LoginActivity.this);
				builder.setTitle("提示");
				builder.setMessage("确认要退出程序吗？");
				builder.setPositiveButton(R.string.sure,
						new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog,
									int which)
							{
								// 程序退出
								LoginActivity.this.finish();
							}
						});
				builder.setNegativeButton(R.string.cancel, null);
				builder.create();
				builder.show();

			}
		});
	}

	/**
	 * 用户登录 请求url:
	 * "http://localhost:8800/track/clientapi.jxp?action=userlogin&username=admin&password=admin"
	 * 
	 * @param String
	 *            params 与baseurl合并在一起的参数字符串
	 * 
	 * @return void
	 */

	public void userLogin(String params)
	{
		ElbsHttpServer elbs = new ElbsHttpServer(ConfigInfo.BASE_URL);
		elbs.setParams(params);
		System.out.println(params);
		String sj = elbs.requestByGet();
		try
		{
			this.loginjson = new org.json.JSONObject(sj);
			Log.i(LogTag.JSON_LOG_INF, this.loginjson.toString());
			// 返回用户是否登录成功
			this.loginState = this.loginjson.getBoolean("success");
			// 返回用户的登录状态
			this.loginLog = this.loginjson.getString("msg");	 
		} catch (JSONException e)
		{
			e.printStackTrace();
		}

	}

	public void show(String msg)
	{
		Toast toast = Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.LEFT | Gravity.BOTTOM, 0, 0);
		toast.show();
	}

	/**
	 * 检查网络状态并做出反映
	 * 
	 */
	public void CheckNetworkState()
	{
		Log.i("network", "network is checking...");
		boolean flag = false;
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// 网络连接状态 手机的GPRS
		State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		// 如果3G、wifi、2G等网络状态是连接的，则退出，否则显示提示信息进入网络设置界面
		if (mobile == State.CONNECTED || mobile == State.CONNECTING)
		{
			show("网络可用");
			Log.i("network", "Mobile:network is available");
			return;
		}
		if (wifi == State.CONNECTED || wifi == State.CONNECTING)
		{
			show("网络可用");
			Log.i("network", "WIFI:network is available");
			return;
		}
		showTips();
	}

	private void showTips()
	{
		// 创建对话框
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("没有可用网络");
		builder.setMessage("当前网络不可用，是否设置网络？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// 如果没有网络连接，则进入网络设置界面
				startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
				LoginActivity.this.finish();
			}
		});
		builder.create();
		builder.show();

	}

	/*
	 * Test Baidu GPRS localtion server 获取用户当前的位置。
	 */
}
