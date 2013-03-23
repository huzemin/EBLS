package com.hzu.elbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

/*
 * 功能主页
 */
public class MainActivity extends Activity implements OnItemClickListener
{
	private int[] images = { R.drawable.signin, R.drawable.plan,
			R.drawable.statement, R.drawable.employees, R.drawable.profile,
			R.drawable.setting,R.drawable.storm};
	private String[] images_title = { "签到", "计划", "公告", "员工信息", "用户信息", "设置","定位" };
	private GridView grid = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		ElbsApplication.getInstance().addActivity(this);
		grid = (GridView) findViewById(R.id.func_main);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < 6; ++i)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", images[i]);
			map.put("title", images_title[i]);
			list.add(map);
		}
		System.out.println(list.toString());
		SimpleAdapter sadapter = new SimpleAdapter(this, list,
				R.layout.mainlist, new String[] { "image", "title" },
				new int[] { R.id.func_image, R.id.func_title });
		grid.setAdapter(sadapter);
		grid.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		Intent intent = new Intent();
		switch (arg2)
		{
		case 0:

			intent.setClass(MainActivity.this, SignInActivity.class);
			this.startActivity(intent);
			break;
		case 1:
			intent.setClass(MainActivity.this, PlanActivity.class);
			this.startActivity(intent);
			break;
		case 2:
			intent.setClass(MainActivity.this, StatementActivity.class);
			this.startActivity(intent);
			break;
		case 3:
			intent.setClass(MainActivity.this, EmployeActivity.class);
			this.startActivity(intent);
			break;
		case 4:
			intent = new Intent();
			intent.setClass(MainActivity.this, PersonalInfoActivity.class);
			this.startActivity(intent);
			break;
		case 5:
			intent = new Intent();
			intent.setClass(MainActivity.this, SettingActivity.class);
			this.startActivity(intent);
			break;
		}
	}

}
