package com.hzu.elbs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.hzu.elbs.conf.ConfigInfo;
import com.hzu.elbs.net.ElbsHttpServer;
import com.hzu.elbs.utils.JsonUtils;
import com.hzu.elbs.utils.SharedPreferencesUtils;

public class AddPlanActivity extends Activity
{
	private Spinner spinner;
	private EditText duratime;
	private EditText content;
	private RadioButton shared;
	private ElbsHttpServer elbs;
	private String uid = "";
	private String publisher_name = "";
	private List members = null;
	private String username = "";
	private String isshared = "false";

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addplan);
		ElbsApplication.getInstance().addActivity(this);
		this.uid = SharedPreferencesUtils.getDataByName(getApplication(),
				ConfigInfo.SP_USER_NAME, "uid");
		this.publisher_name = SharedPreferencesUtils.getDataByName(
				getApplication(), ConfigInfo.SP_USER_NAME, "username");
		this.elbs = new ElbsHttpServer(ConfigInfo.BASE_URL);
		this.spinner = (Spinner) findViewById(R.id.addplanuser);
		this.duratime = (EditText) findViewById(R.id.addplan_duration);
		this.content = (EditText) findViewById(R.id.addplan_content);
		this.shared = (RadioButton) findViewById(R.id.addplan_shared);
		List<Map<String, String>> list = getListMembers();
		Iterator<Map<String, String>> iterator = list.iterator();
		this.members = new ArrayList();
		int i = 0;
		while (iterator.hasNext())
		{
			Map<String, String> member = iterator.next();
			this.members.add(member.get("username"));
			i++;
		}

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, members);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3)
			{

				System.out.println("您选择的是：" + adapter.getItem(arg2));
				AddPlanActivity.this.username = adapter.getItem(arg2);
				arg0.setVisibility(View.VISIBLE);
			}

			public void onNothingSelected(AdapterView<?> arg0)
			{
				arg0.setVisibility(View.VISIBLE);
			}
		});

	}

	/**
	 * 获取该用户（领导）的下属名单
	 * http://admin:8080/track/clientapi.jxp?action=listMembers&username=root
	 */
	//
	public List<Map<String, String>> getListMembers()
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("action", "listmembers"));
		params.add(new BasicNameValuePair("username", this.publisher_name));
		System.out.println("Post.....===========listmembers");
		String str = elbs.requestByPost(params);
		System.out.println(str);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		JSONObject strjson = null;
		try
		{
			strjson = new JSONObject(str);
			if (!strjson.getBoolean("success"))
			{
				return null;
			}
			JSONArray sdatas = strjson.getJSONArray("data");
			for (int i = 0; i < sdatas.length(); i++)
			{
				JSONObject st = sdatas.getJSONObject(i);
				Map<String, String> map = JsonUtils.toMap(st, null);
				list.add(map);
				System.out.println(list);
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return list;
	}

	// 按钮的单击事件
	/**
	 * http://admin:8080/track/clientapi.jxp?action=addplan&uid=
	 * 20120401225746909&username=huzemin&content=今天要把程序调试好
	 * &duration=3小时&shared=true
	 * 
	 * @param view
	 */
	public void createplan(View view)
	{
		String content = this.content.getText().toString().trim();
		String duratime = this.duratime.getText().toString().trim();
		this.isshared = "" + this.shared.isChecked();
		if (content.equals("") || duratime.equals(""))
		{
			return;
		}
		duratime += "小时";
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("action", "addplan"));
		params.add(new BasicNameValuePair("uid", this.uid));
		params.add(new BasicNameValuePair("username", this.username));
		params.add(new BasicNameValuePair("content", content));
		params.add(new BasicNameValuePair("duration", duratime));
		params.add(new BasicNameValuePair("shared", this.isshared));
		System.out.println("Post.....===========listMembers");
		String str = elbs.requestByPost(params);
		JSONObject strjson = null;
		try
		{
			strjson = new JSONObject(str);
			if (strjson.getBoolean("success"))
			{
				Toast.makeText(this, strjson.getString("msg"), Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		reset();
	}
	public void reset()
	{
		this.content.setText("");
		this.shared.setChecked(false);
		this.duratime.setText("");
		this.shared.setChecked(false);
	}
	public void resetplan(View view)
	{
		reset();
	}
	public void addplanback(View view)
	{
		Intent intent = new Intent(this, PlanActivity.class);
		this.startActivity(intent);
	}
}
