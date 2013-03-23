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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hzu.elbs.conf.ConfigInfo;
import com.hzu.elbs.net.ElbsHttpServer;
import com.hzu.elbs.utils.JsonUtils;
import com.hzu.elbs.utils.SharedPreferencesUtils;

public class EmployeActivity extends Activity implements OnItemClickListener
{
	private ListView elist;
	private ElbsHttpServer elbs;
	private String root; // 员工的领导
	private String role; // 角色
	
	private List members;
	private List membersId;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.employee);
		ElbsApplication.getInstance().addActivity(this);
		this.elbs = new ElbsHttpServer(ConfigInfo.BASE_URL);
		this.role = SharedPreferencesUtils.getDataByName(getApplication(),
				ConfigInfo.SP_USER_NAME, "authority");
		if(this.role.equals("ROLE_MASTER"))
		{
			this.root = SharedPreferencesUtils.getDataByName(getApplication(),
					ConfigInfo.SP_USER_NAME, "username");
		}else{
			this.root = SharedPreferencesUtils.getDataByName(getApplication(),
					ConfigInfo.SP_USER_NAME, "root");
		}
		System.out.println(this.root);
		this.elist = (ListView)findViewById(R.id.elist);
		
		List<Map<String, String>> memberlist = getListMembers();
		Iterator<Map<String, String>> iterator = memberlist.iterator();
		this.members = new ArrayList();
		this.membersId = new ArrayList();
		while (iterator.hasNext())
		{
			Map<String, String> member = iterator.next();
			this.members.add(member.get("username"));
			this.membersId.add(member.get("uid"));
		}
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, members);
		this.elist.setAdapter(adapter);
		this.elist.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		String uid = (String) this.membersId.get(arg2);
		System.out.println(uid + "---------------------uid");
		Intent intent = new Intent(this, DetailEmployActivity.class);
		intent.putExtra("uid", uid);
		startActivity(intent);
	}
	public List<Map<String, String>> getListMembers()
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("action", "listmembers"));
		params.add(new BasicNameValuePair("username", this.root));
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
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return list;
	}
	

	public void eback(View view)
	{
		Intent intent = new Intent(this, MainActivity.class);
		this.startActivity(intent);
	}



}
