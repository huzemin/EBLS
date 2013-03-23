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
	 * @param key 为空则json必须为{k:value,k:value,..}的格式，不为空则"{key:{k:value,k:value,..},..}" 没有内嵌新的对象
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
