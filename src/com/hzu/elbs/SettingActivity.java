package com.hzu.elbs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SettingActivity extends Activity implements OnItemClickListener
{
	private String[] items  = {"版本信息","退出"};
	private ListView list = null;
	public void onCreate(Bundle savedInstanceState	)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting);
		ElbsApplication.getInstance().addActivity(this);
		list = (ListView)findViewById(R.id.settinglist);
		ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{ 
		if(arg2 == 0)
		{
			new AlertDialog.Builder(this).setTitle("版本信息")
			.setMessage("基于android 的Elbs v0.1版本")
			.setNegativeButton("取消", null)
			.create().show();
		}else
		{
			new AlertDialog.Builder(this).setTitle("退出提示")
			.setMessage("你确定要退出应用程序吗")
			.setPositiveButton("退出", new DialogInterface.OnClickListener()
			{
				@SuppressWarnings("static-access")
				@Override
				public void onClick(DialogInterface arg0, int arg1)
				{
					ElbsApplication.getInstance().exit();
				}
			})
			.setNegativeButton("取消", null)
			.create().show();
		}
	}
	//返回按钮控件的单击事件
	public void btnback(View view)
	{
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
	}
}
