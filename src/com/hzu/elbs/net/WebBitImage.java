package com.hzu.elbs.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class WebBitImage
{
	/*
	 * 这个是通过HttpConnection来获取图片
	 */
	public static Bitmap getHttpBitmap(String url)
	{
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try
		{
			// Log.d(TAG, url);
			myFileUrl = new URL(url);
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		try
		{
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setConnectTimeout(0);
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return bitmap;
	}
	public static Bitmap getBitmap(String url)
	{
		 /*
		  * 判断url是否是图片地址
		  */
		 return null;
	}
}