package com.hzu.elbs;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class ElbsApplication extends Application
{
	private static  ElbsApplication instance;
	private static List<Activity> activitylist = new ArrayList<Activity>();
	private ElbsApplication(){}
	public static ElbsApplication getInstance()
	{
		if(null == instance)
		{
			instance = new ElbsApplication();
		}
		return instance;
	}
	public static void addActivity(Activity activity)
	{
		activitylist.add(activity);
	}
	public static void exit()
	{
		for(Activity activity: activitylist)
		{
			activity.finish();
		}
	}
}
