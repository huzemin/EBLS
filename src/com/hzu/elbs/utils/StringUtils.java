package com.hzu.elbs.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils
{
	public void toJson(String json)
	{
		
	}
	public Date strToDate(String str)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = new Date();
		String str1 = df.format(d);
		Date md = null;
		try
		{
			md = df.parse(str);
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		return md;
	}
	public Date strToDate(String str, String pattern)
	{
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		Date d = new Date();
		String str1 = df.format(d);
		Date md = null;
		try
		{
			md = df.parse(str);
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		return md;
	}
}
