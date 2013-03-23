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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.hzu.elbs.SignInActivity.MySimpleAdapter;
import com.hzu.elbs.conf.ConfigInfo;
import com.hzu.elbs.net.ElbsHttpServer;
import com.hzu.elbs.utils.JsonUtils;
import com.hzu.elbs.utils.SharedPreferencesUtils;

/**
 * ����ģ�� �û����ܲ�ѯ��ͬ�쵼��root)���µĹ��� ֻ�н�ɫΪROLE_MASTER���û�������ӹ���
 * 
 * @author huzemin
 * 
 */
public class StatementActivity extends Activity implements OnItemLongClickListener
{
	private ElbsHttpServer elbs;
	private List<Map<String, String>> list = null;
	private ListView listview;
	private String role; //�û�Ȩ��
	private String uid; // �û�id
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.statement);
		ElbsApplication.getInstance().addActivity(this);
		// find view
		this.listview = (ListView) findViewById(R.id.statementlist);
		this.elbs = new ElbsHttpServer(ConfigInfo.BASE_URL);
		this.role = SharedPreferencesUtils.getDataByName(getApplication(),
				ConfigInfo.SP_USER_NAME, "authority");
		this.uid = SharedPreferencesUtils.getDataByName(getApplication(),
				ConfigInfo.SP_USER_NAME, "uid");
		list = getStatementslist();
		if (list != null)
		{
			System.out.println("hello debug");
			showStatements(list);
			listview.setOnItemLongClickListener(this);
		} else
			Toast.makeText(this, "û������", Toast.LENGTH_LONG).show();

	}
	public void showStatements(List<Map<String,String>> list)
	{
		SimpleAdapter sadapter = new SimpleAdapter(this, list,
				R.layout.statementlistitem, new String[] { "sid",
						"publisher_Username", "posttime", "content" }, new int[] {
						R.id.sid, R.id.state_user, R.id.state_posttime,
						R.id.statementcontent });
		listview.setAdapter(sadapter);
	}
	public List<Map<String, String>> getStatementslist()
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		System.out.println(role + uid);
		params.add(new BasicNameValuePair("action", "listAnnoucements"));
		params.add(new BasicNameValuePair("uid", uid));
		params.add(new BasicNameValuePair("role", role));
		System.out.println("Post.....===========listAnnoucements");
		String str = elbs.requestByPost(params);
		System.out.println(str);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		JSONObject strjson = null;
		try
		{
			strjson = new JSONObject(str);
			if (!strjson.getBoolean("success"))
			{
				Toast toast = Toast.makeText(getApplication(),
						strjson.getString("msg"), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return null;
			}
			JSONArray sdatas = strjson.getJSONArray("data");
			for (int i = 0; i < sdatas.length(); i++)
			{
				JSONObject st = sdatas.getJSONObject(i);
				Map<String, String> map = JsonUtils.toMap(st, null);
				// ��ȡpublisher ���û���
				String publisherid = map.get("publisher");
				System.out.println(publisherid);
				List<NameValuePair> params2 = new ArrayList<NameValuePair>();
				params2.add(new BasicNameValuePair("action", "profile"));
				params2.add(new BasicNameValuePair("uid", publisherid));
				String profilestr = elbs.requestByPost(params2);
				System.out.println(profilestr);
				JSONObject profilejson = new JSONObject(profilestr);
				String publisher_Username = profilejson.getJSONObject("info")
						.getString("username");
				map.put("publisher_Username", publisher_Username);
				list.add(map);
				System.out.println(list);
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return list;
	}

	// ���ذ��������¼�
	public void btnback(View view)
	{
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	// ��ӹ���
	// ����ֻ�н�ɫΪROLE_MASTER��Ա���������
	public void btnaddStatement(View view)
	{
		LayoutInflater factory = LayoutInflater.from(this);
		//��ӹ�����ǩ���ĵ����Ի���Ĳ���һ��
		final View signincontent = factory
				.inflate(R.layout.signincontext, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				this);
		builder.setTitle("�������棺");
		builder.setView(signincontent);
		builder.setPositiveButton("����", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				EditText content = (EditText) signincontent
						.findViewById(R.id.s_content);
				String content_text = content.getText() == null ? "No Content"
						: content.getText().toString().trim();

				if (content_text == null || content_text.equals(""))
				{
					content_text = "No Statement";
				}
				String uid = SharedPreferencesUtils.getDataByName(
					getApplication(), ConfigInfo.SP_USER_NAME, "uid");
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("action", "postAnnoucement"));
				System.out.println(uid);
				params.add(new BasicNameValuePair("uid", StatementActivity.this.uid));
				params.add(new BasicNameValuePair("role", StatementActivity.this.role));
				params.add(new BasicNameValuePair("content", content_text));
				/*
				 * params.add(new BasicNameValuePair("location", content_text));
				 */
				String responces = elbs.requestByPost(params);
				System.out.println(responces);
				try
				{
					JSONObject json = new JSONObject(responces);
					String msg = json.getString("msg");
					if(json.getBoolean("success"))
					{
						Toast.makeText(getApplication(), "����ˢ��...", Toast.LENGTH_SHORT).show();
						// ����ListView����ˢ��
					    list = getStatementslist();
						showStatements(list);
					}
					Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show();
				} catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
		});
		builder.setNegativeButton("ȡ��", null);
		builder.create().show();
	}
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3)
	{
		if(this.role.equals("ROLE_MASTER"))
		{
			Map<String, String> statement = this.list.get(arg2);
			System.out.println(statement.get("sid"));
			new AlertDialog.Builder(this).setTitle("ɾ������").setMessage("��ȷ��Ҫɾ����������")
			.setPositiveButton("ɾ��", new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					Toast.makeText(getApplicationContext(), "ɾ��", Toast.LENGTH_LONG).show();
				}
			}).setNegativeButton("ȡ��", null).create().show();
		}else{
			Toast.makeText(getApplicationContext(), "��Ȩ��ִ��ɾ��", Toast.LENGTH_SHORT).show();
		}
		return false;
	}
}
