package com.hzu.elbs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.hzu.elbs.conf.ConfigInfo;
import com.hzu.elbs.net.ElbsHttpServer;
import com.hzu.elbs.utils.JsonUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class DetailEmployActivity extends Activity
{
	private String uid;
	private ElbsHttpServer elbs;
	//
	private TextView username;
	private TextView nickname;
	private TextView email;
	private TextView sex;
	private TextView department;
	private TextView birthday;
	private TextView contact;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		ElbsApplication.getInstance().addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detailemploye);
		this.elbs = new ElbsHttpServer(ConfigInfo.BASE_URL);
		Intent intent = getIntent();
		this.uid = intent.getStringExtra("uid");
		System.out.println(uid + "---------------------uid");
		username = (TextView) findViewById(R.id.e_username);
		nickname = (TextView) findViewById(R.id.e_nickname);
		email = (TextView) findViewById(R.id.e_email);
		sex = (TextView) findViewById(R.id.e_sex);
		department = (TextView) findViewById(R.id.e_department);
		birthday = (TextView) findViewById(R.id.e_birthday);
		contact = (TextView) findViewById(R.id.e_contact);
		 Map<String,String> profile = getProfile();
		username.setText(profile.get("username"));
		nickname.setText(profile.get("nickname"));
		email.setText(profile.get("email"));
		sex.setText(profile.get("sex"));
		department.setText(profile.get("department"));
		birthday.setText(profile.get("birthday"));
		contact.setText(profile.get("contact"));
	}
	
	public Map<String,String> getProfile()
	{
		List<NameValuePair> params3 = new ArrayList<NameValuePair>();
		params3.add(new BasicNameValuePair("action", "profile"));
		params3.add(new BasicNameValuePair("uid", this.uid ));
		String profile = elbs.requestByPost(params3);
		System.out.println(profile);
		JSONObject pjson;
		Map<String,String> profilemap = null;
		try
		{
			pjson = new JSONObject(profile);
			profilemap = JsonUtils.toMap(pjson,"info"); 
		} catch (JSONException e)
		{

			e.printStackTrace();
		}
		return profilemap;
	}
	public void btnback(View view)
	{
		Intent intent = new Intent(this, EmployeActivity.class);
		startActivity(intent);
	}
}
