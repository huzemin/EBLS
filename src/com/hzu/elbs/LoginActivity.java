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
	 * R.layout.main�ؼ�
	 */
	private EditText username;
	private EditText password;
	private Button loginbtn;
	private Button cancelbtn;
	private ImageButton quit;
	/*
	 * ����ʹ�ñ���
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
		// ȥ��������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		ElbsApplication.getInstance().addActivity(this);
		// �������
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		loginbtn = (Button) findViewById(R.id.login);
		cancelbtn = (Button) findViewById(R.id.cancel);
		quit = (ImageButton) findViewById(R.id.quit);
		//������
		CheckNetworkState();
		// �û���¼
		loginbtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				String user = username.getText().toString().trim();
				String pass = password.getText().toString().trim();
				if (user.equals("") && pass.equals(""))
				{
					Toast.makeText(LoginActivity.this, "�û��������벻��Ϊ��",
							Toast.LENGTH_LONG).show();
					return;
				}
				try
				{
					String params = "action=userlogin&username="
							+ URLEncoder.encode(user, "utf-8") + "&password="
							+ URLEncoder.encode(pass, "utf-8");
					userLogin(params);
					//�жϵ�¼״̬��������ӳ
					if (LoginActivity.this.loginState)
					{
						LoginActivity.this.show("��¼�ɹ�->" + LoginActivity.this.loginLog);
						//�����û���Ϣ
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
						// ��¼ʧ����ʾ�Ի���������ʾ
						/*Toast.makeText(MainActivity.this,
								"��¼ʧ��->" + MainActivity.this.loginLog,
								Toast.LENGTH_LONG).show();*/
						new AlertDialog.Builder(LoginActivity.this)
								.setTitle("��¼ʧ��")
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

		// �˳�����->�Ƚ���ȷ��
		quit.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(
						LoginActivity.this);
				builder.setTitle("��ʾ");
				builder.setMessage("ȷ��Ҫ�˳�������");
				builder.setPositiveButton(R.string.sure,
						new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog,
									int which)
							{
								// �����˳�
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
	 * �û���¼ ����url:
	 * "http://localhost:8800/track/clientapi.jxp?action=userlogin&username=admin&password=admin"
	 * 
	 * @param String
	 *            params ��baseurl�ϲ���һ��Ĳ����ַ���
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
			// �����û��Ƿ��¼�ɹ�
			this.loginState = this.loginjson.getBoolean("success");
			// �����û��ĵ�¼״̬
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
	 * �������״̬��������ӳ
	 * 
	 */
	public void CheckNetworkState()
	{
		Log.i("network", "network is checking...");
		boolean flag = false;
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// ��������״̬ �ֻ���GPRS
		State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		// ���3G��wifi��2G������״̬�����ӵģ����˳���������ʾ��ʾ��Ϣ�����������ý���
		if (mobile == State.CONNECTED || mobile == State.CONNECTING)
		{
			show("�������");
			Log.i("network", "Mobile:network is available");
			return;
		}
		if (wifi == State.CONNECTED || wifi == State.CONNECTING)
		{
			show("�������");
			Log.i("network", "WIFI:network is available");
			return;
		}
		showTips();
	}

	private void showTips()
	{
		// �����Ի���
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("û�п�������");
		builder.setMessage("��ǰ���粻���ã��Ƿ��������磿");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// ���û���������ӣ�������������ý���
				startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener()
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
	 * Test Baidu GPRS localtion server ��ȡ�û���ǰ��λ�á�
	 */
}
