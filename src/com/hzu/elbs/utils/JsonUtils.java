package com.hzu.elbs.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils
{
	/**
	 * 
	 * @param json 
	 * @param key Ϊ����json����Ϊ{k:value,k:value,..}�ĸ�ʽ����Ϊ����"{key:{k:value,k:value,..},..}" û����Ƕ�µĶ���
	 * @return
	 * @throws JSONException
	 */
	public static Map<String,String> toMap(JSONObject json,String key) throws JSONException
	{
		Map<String,String> map = new HashMap<String,String>();
		if(key != null && !key.equals(""))
		{
			 json= json.getJSONObject(key);
		}
		Iterator<String> keyset = json.keys();
		while(keyset.hasNext())
		{
			String k = keyset.next();
			map.put(k, json.getString(k));
		}
		return map;
	}
}
