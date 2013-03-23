package com.hzu.elbs.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.hzu.elbs.conf.ConfigInfo;

public class SharedPreferencesUtils
{
	public static void storeData(Context content, String name,
			Map<String, String> data, int Mode)
	{

		SharedPreferences sp = content.getSharedPreferences(name,
				content.MODE_PRIVATE);
		Editor editor = sp.edit();
		Set<String> keyset = data.keySet();
		Iterator<String> i = keyset.iterator();
		while (i.hasNext())
		{
			String key = i.next();
			editor.putString(key,  data.get(key));
		}
		editor.commit();
	}

	public static Map<String, ?> getData(Context content, String name,
			int mode)
	{

		SharedPreferences sp = content.getSharedPreferences(name, mode);

		return sp.getAll();

	}
	public static String getDataByName(Context content, String name,String key)
	{

		SharedPreferences sp = content.getSharedPreferences(name, 0);
		String result = sp.getString(key, "ERROR_NOT_UID");
		return result;

	}
}

/** cut from MainActivity
 * �����û���Ϣ ����ȷ��¼���û���Ϣ�浽SharedPerference�У��Թ�����Ĳ��� �����û���uid,username, nickname
 * ,auth->role
 * 
 * @param �û���Ϣ��Map
 * @return void
 */
/**
 * public void StoreUserInfo(Map<String, Object> userInfo) { SharedPreferences
 * sp = getSharedPreferences( ConfigInfo.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
 * Editor editor = sp.edit(); Set<String> keyset = userInfo.keySet();
 * Iterator<String> i = keyset.iterator(); while(i.hasNext()) { String key =
 * i.next(); editor.putString(key, (String)userInfo.get(key)); }
 * editor.commit(); }
 */
