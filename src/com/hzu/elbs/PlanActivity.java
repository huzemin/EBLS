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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hzu.elbs.conf.ConfigInfo;
import com.hzu.elbs.net.ElbsHttpServer;
import com.hzu.elbs.utils.JsonUtils;
import com.hzu.elbs.utils.SharedPreferencesUtils;

public class PlanActivity extends Activity implements
		android.view.View.OnClickListener
{
	private ElbsHttpServer elbs;
	private ListView plview;
	private Button prebtn;
	private Button nextbtn;
	private ImageButton addplanbtn;
	private TextView nodata = null;
	private List<Map<String, String>> planlist;
	private LinearLayout layout;
	private  ProgressDialog mypDialog;
	// 用户权限和uid
	private String uid = "";
	private String role = "";
	private int page = 1;
	private int total = 0;
	private int totalpage = 0;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.plan);
		ElbsApplication.getInstance().addActivity(this);
/*		//初始化弹出进度对话框
		this.mypDialog=new ProgressDialog(this);
		//实例化
		mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		//设置进度条风格，风格为圆形，旋转的
		mypDialog.setTitle("计划");
		//设置ProgressDialog 标题
		mypDialog.setMessage("正在更新信息...");
		//设置ProgressDialog 的一个Button
		mypDialog.setIndeterminate(false);*/
		// 初始化服务器和参数
		this.elbs = new ElbsHttpServer(ConfigInfo.BASE_URL);
		this.role = SharedPreferencesUtils.getDataByName(getApplication(),
				ConfigInfo.SP_USER_NAME, "authority");
		this.uid = SharedPreferencesUtils.getDataByName(getApplication(),
				ConfigInfo.SP_USER_NAME, "uid");
		// find view
		this.prebtn = (Button) findViewById(R.id.prepage);
		this.nextbtn = (Button) findViewById(R.id.nextpage);
		this.layout = (LinearLayout) findViewById(R.id.pbl);
		this.plview = (ListView) findViewById(R.id.planlist);
		this.addplanbtn = (ImageButton) findViewById(R.id.addplan);
		this.nodata = (TextView) findViewById(R.id.nodata);
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e)
		{
			Toast.makeText(this, "Thread Error", Toast.LENGTH_LONG).show();
		}
		this.planlist = getPlanList(this.page);
		this.totalpage = this.setTotalpage(this.total, 8);
		if (this.totalpage == 1)
		{
			this.layout.setVisibility(View.GONE);
		} else
		{
			this.layout.setVisibility(View.VISIBLE);
			this.prebtn.setEnabled(false);
			this.nextbtn.setEnabled(true);
			this.prebtn.setOnClickListener(this);
			this.nextbtn.setOnClickListener(this);
		}
		if (planlist != null)
		{
			listViewSetAdapter(this.planlist);
		}

	}

	public void listViewSetAdapter(List<Map<String, String>> list)
	{
		SimpleAdapter adpater = new SimpleAdapter(this, this.planlist,
				R.layout.planitem, new String[] { "pusername", "username",
						"content", "isfinished", "posttime", "duration",
						"shared" }, new int[] { R.id.p_publisher,
						R.id.p_username, R.id.p_content, R.id.p_isfinish,
						R.id.p_posttime, R.id.p_duration, R.id.p_isshared });
		this.plview.setAdapter(adpater);
	}

	/*
	 * http://admin:8888/track/clientapi.jxp?action=showplan&uid=20120401225746909
	 * &role=ROLE_MASTER&page=1
	 */
	public List<Map<String, String>> getPlanList(int page)
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String p = new Integer(page).toString();
		System.out.println(role + uid);
		params.add(new BasicNameValuePair("action", "showplan"));
		params.add(new BasicNameValuePair("uid", this.uid));
		params.add(new BasicNameValuePair("role", this.role));
		params.add(new BasicNameValuePair("page", p));
		System.out.println("Post.....===========showplan");
		String str = elbs.requestByPost(params);
		System.out.println(str);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		JSONObject strjson = null;
		try
		{
			strjson = new JSONObject(str);
			if (!strjson.getBoolean("success"))
			{
				this.plview.setVisibility(View.GONE);
				this.nodata.setText(strjson.getString("msg"));
				this.nodata.setVisibility(View.VISIBLE);
				return null;
			}
			// json字符传中key为msg的值代表的是该用户不分页查询产生结果的总条数
			this.total = strjson.getInt("msg");
			JSONArray sdatas = strjson.getJSONArray("data");
			for (int i = 0; i < sdatas.length(); i++)
			{
				JSONObject st = sdatas.getJSONObject(i);
				Map<String, String> map = JsonUtils.toMap(st, null);
				// 获取publisher 的用户名
				String publisherid = map.get("publisher");
				List<NameValuePair> params2 = new ArrayList<NameValuePair>();
				params2.add(new BasicNameValuePair("action", "profile"));
				params2.add(new BasicNameValuePair("uid", publisherid));
				String profilestr = elbs.requestByPost(params2);
				JSONObject profilejson = new JSONObject(profilestr);
				String publisher_Username = profilejson.getJSONObject("info")
						.getString("username");
				map.put("pusername", publisher_Username);
				// 获取执行者 的用户名
				String uid = map.get("uid");
				List<NameValuePair> params3 = new ArrayList<NameValuePair>();
				params3.add(new BasicNameValuePair("action", "profile"));
				params3.add(new BasicNameValuePair("uid", uid));
				String profilestr1 = elbs.requestByPost(params3);
				JSONObject profile1json = new JSONObject(profilestr1);
				String username = profile1json.getJSONObject("info").getString(
						"username");
				map.put("username", username);
				list.add(map);
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return list;
	}

	public int setTotalpage(int total, int num)
	{
		int totalpage = 0;
		if (total % 10 == 0)
		{
			totalpage = total / 10;
		} else
		{
			totalpage = total / 10 + 1;
		}
		return totalpage;
	}

	@Override
	public void onClick(View v)
	{
 
		if (v.getId() == R.id.prepage)
		{
			this.page--;

			if (this.page < 1)
			{
				this.page = 1;
				return;
			}
			this.prebtn.setText("更新中...");
			this.prebtn.setEnabled(false);
			this.planlist = this.getPlanList(page);
			this.listViewSetAdapter(this.planlist);
			this.prebtn.setText("上一页");
			this.nextbtn.setEnabled(true);
			if(this.page == 1)
				this.prebtn.setEnabled(false);
			else
				this.prebtn.setEnabled(true);
 
		} else if (v.getId() == R.id.nextpage)
		{
			this.page++;
			if (this.page > totalpage)
			{
				return;
			}
			this.nextbtn.setText("更新中");
			this.nextbtn.setEnabled(false);
			this.planlist = this.getPlanList(page);
			this.listViewSetAdapter(this.planlist);
			this.nextbtn.setText("下一页");
			this.prebtn.setEnabled(true);
			if(this.page == totalpage)
				this.nextbtn.setEnabled(false);
			else
				this.nextbtn.setEnabled(true);
		}
 
	}

	// 返回按钮
	public void btnplanback(View view)
	{
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	// 添加计划
	public void addplan(View view)
	{
		if(this.role.equals("ROLE_MASTER"))
		{
			Intent intent = new Intent(this, AddPlanActivity.class);
			startActivity(intent);
		}else
		{
			new AlertDialog.Builder(this).setTitle("提示").setMessage("你无创建计划的权限！")
			.setNegativeButton("取消", null).create().show();
		}
	}

}
