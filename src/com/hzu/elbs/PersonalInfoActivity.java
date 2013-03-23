package com.hzu.elbs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzu.elbs.conf.ConfigInfo;
import com.hzu.elbs.net.ElbsHttpServer;
import com.hzu.elbs.utils.JsonUtils;
import com.hzu.elbs.utils.SharedPreferencesUtils;
import com.hzu.elbs.utils.StringUtils;

public class PersonalInfoActivity extends Activity
{
	private ImageView headimage;
	private TextView info_uid;
	private EditText info_username, info_nickname, info_birthday, info_contact,
			info_mail, info_root, info_department;
	private Button updatebtn, resetbtn;
	private boolean info_edit = true;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personinfo);
		ElbsApplication.getInstance().addActivity(this);
		updatebtn = (Button) findViewById(R.id.info_update_btn);
		resetbtn = (Button) findViewById(R.id.info_reset_btn);
		headimage = (ImageView) findViewById(R.id.info_img);
		info_uid = (TextView) findViewById(R.id.info_uid);
		info_username = (EditText) findViewById(R.id.info_username);
		info_nickname = (EditText) findViewById(R.id.info_nickname);
		info_birthday = (EditText) findViewById(R.id.info_birthday);
		info_contact = (EditText) findViewById(R.id.info_contact);
		info_mail = (EditText) findViewById(R.id.info_mail);
		info_root = (EditText) findViewById(R.id.info_root);
		info_department = (EditText) findViewById(R.id.info_department);
		// 初始化数据
		info_init();
	}

	public void info_init()
	{
		Map<String, ?> allinfo = SharedPreferencesUtils.getData(
				getApplicationContext(), ConfigInfo.SP_USER_NAME,
				MODE_WORLD_READABLE);
		// 图像要异步从网络中读取，这里先统一用本地图片
		headimage.setImageDrawable(getResources().getDrawable(
				R.drawable.xiaohei));
		info_uid.setText((String) allinfo.get("uid"));
		info_username.setText((String) allinfo.get("username"));
		info_nickname.setText((String) allinfo.get("nickname"));
		info_birthday.setText((String) allinfo.get("birthday"));
		info_root.setText((String) allinfo.get("root"));
		info_contact.setText((String) allinfo.get("contact"));
		info_mail.setText((String) allinfo.get("email"));
		info_department.setText((String) allinfo.get("department"));
	}

	public void enableEditText(boolean enable)
	{
		info_nickname.setEnabled(enable);
		info_nickname.setFocusable(enable);
		info_birthday.setEnabled(enable);
		info_birthday.setFocusable(enable);
		info_contact.setEnabled(enable);
		info_contact.setFocusable(enable);
		info_mail.setEnabled(enable);
		info_mail.setFocusable(enable);
	}

	// 更新用户信息
	// http url: baseurl?action=updateuser?...
	public void update()  
	{
		//不知用不用判断是否为空
		 //用户信息
		String username = info_username.getText().toString();
		String nickname = info_nickname.getText().toString();
		String birthday = info_birthday.getText().toString();
		String contact = info_contact.getText().toString();
		String email = info_mail.getText().toString();
		//修改用户信息
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "updateuser"));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("nickname", nickname));
		params.add(new BasicNameValuePair("birthday", birthday));
		params.add(new BasicNameValuePair("contact", contact));
		params.add(new BasicNameValuePair("email", email));
		ElbsHttpServer elbs = new ElbsHttpServer(ConfigInfo.BASE_URL);
		String responces = elbs.requestByPost(params);
		System.out.println(responces);
		try
		{
			JSONObject json = new JSONObject(responces);
			boolean success = json.getBoolean("success");
			if(success)
			Toast.makeText(getApplication(), json.getString("msg"), Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getApplication(),"更新失败！" , Toast.LENGTH_SHORT).show();
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		 
		//更新SharedPerferences 的内容
		//http url = "http://localhost:8888/track/clientapi.jxp?action=profile&uid=20120330151433754"
	    params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "updateuser"));
		params.add(new BasicNameValuePair("username", username));
	    String  profile = elbs.requestByPost(params);
	    JSONObject json;
		try
		{
			json = new JSONObject(profile);
			if(json.getBoolean("success"))
				SharedPreferencesUtils.storeData(getApplication(), ConfigInfo.SP_USER_NAME, JsonUtils.toMap(json,"info"),MODE_PRIVATE);
			else
				Toast.makeText(getApplication(),"重载用户信息失败！！",Toast.LENGTH_LONG).show();
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    

		
	}

	// 返回和编辑按钮的单击事件
	public void infoeditbtnclick(View v)
	{
		if (info_edit)
		{
			updatebtn.setVisibility(View.VISIBLE);
			resetbtn.setVisibility(View.VISIBLE);
			enableEditText(info_edit);
			toastShow("修改个人信息");
			info_edit = false;
		} else
		{
			updatebtn.setVisibility(View.GONE);
			resetbtn.setVisibility(View.GONE);
			enableEditText(info_edit);
			info_edit = true;

			toastShow("关闭修改");
		}
	}

	public void infobackbtnclick(View v)
	{
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
	}

	// update，reset 按钮的单击事件
	public void updateclick(View v)
	{
		update();
		updatebtn.setVisibility(View.GONE);
		resetbtn.setVisibility(View.GONE);
		enableEditText(false);
		info_edit = true;
	}

	public void resetclick(View v)
	{
		info_init();
		toastShow("个人信息已经重置");
	}
	public void toastShow(String msg)
	{
		Toast toast = Toast.makeText(getApplication(), msg , Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.show();
	}
}
