package com.hzu.elbs.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/*
 * ��HttpClient��Զ�̷����ȡ����-��json format��
 * elbs connection api 
 */
public class ElbsHttpServer
{
	public String baseurl;
	public HttpClient http;
	public String message = ""; // json or other
	public String params;
	// �������Ӻ�����ʱʱ��
	public final int REQUEST_TIMEOUT = 5000;
	public final int SO_TIMEOUT = 3000;

	public ElbsHttpServer(String baseurl)
	{
		this.baseurl = baseurl;
		this.params = "";
		this.http = this.getHttpClient();
	}

	// ��ʼ��HttpClient�������ó�ʱ
	public HttpClient getHttpClient()
	{
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}
	/**
	 *  ͨ��Post��ʽ��������
	 * @param paramsPairs
	 * @return
	 */
	public String requestByPost(List<NameValuePair> params) {
		HttpClient httpClient = getHttp();
		HttpPost httpRequest = new HttpPost(this.baseurl);
		String error = "";
		String strResult = "";
		try {
			/* ������������������� */
			httpRequest.setEntity(new UrlEncodedFormEntity(params,
					HTTP.UTF_8));
			/* �������󲢵ȴ���Ӧ */
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			/* ��״̬��Ϊ200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* ���������� */
				strResult = EntityUtils.toString(httpResponse.getEntity());

			} else {
				error = "doPostError:"
						+ httpResponse.getStatusLine().toString();
			}
		} catch (ClientProtocolException e) {
			error = e.getMessage().toString();
			e.printStackTrace();
		} catch (IOException e) {
			error = e.getMessage().toString();
			e.printStackTrace();
		} catch (Exception e) {
			error = e.getMessage().toString();
			e.printStackTrace();
		}
		if (error.equals("")) {
			return strResult;
		} else {
			return error;
		}
	}
	/*
	 * ͨ��HttpClient�������ݵĴ�ȡ
	 */
	//�����url+params��ʽΪ: "http://127.0.0.1:8800/track/article.jxp?action=showBlogs&_dc=1362665502350&start=0&limit=15&page=1"
	public String requestByGet()
	{
		HttpGet httpgets = new HttpGet(this.baseurl + "?" + this.params);
		httpgets.addHeader("Content-Type", "text/html;charset=UTF-8");
		HttpResponse response = null;
		//try-catch ��þ�����Activity���ж�
		try
		{
			 
			response = http.execute(httpgets);
			
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{

			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();
		if (entity != null)
		{
			InputStream instreams;
			try
			{
				instreams = entity.getContent();
				this.message = readStreamToStr(instreams);
				httpgets.abort();
			} catch (IllegalStateException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return this.message;
	}

	public byte[] readStream(InputStream in)
	{
		// BufferedReader buff = new BufferedReader(new InputStreamReader(in));
		int len;
		byte[] data = null;
		try
		{
			len = in.available();
			data = new byte[len];
			in.read(data);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			if(in != null)
			{
				try
				{
					in.close();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
		return data;
	}
	/*
	 * ��ȡ���紫����������ݲ�ת��Ϊ�ַ���
	 */
	public String readStreamToStr(InputStream in)  
	{
		BufferedReader buff = new BufferedReader(new InputStreamReader(in));
		String str = "";
		StringBuilder sb = new StringBuilder();
		try
		{
			while ((str = buff.readLine()) != null)
			{
				sb.append(str.toString());
			}
		} catch (IOException e)
		{
			if(buff != null)
			{
				try
				{
					buff.close();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
		return sb.toString();
	}

	public String getBaseurl()
	{
		return baseurl;
	}

	public void setBaseurl(String baseurl)
	{
		this.baseurl = baseurl;
	}

	public String getMessage()
	{
		return message;
	}

	public String getParams()
	{
		return params;
	}

	public HttpClient getHttp()
	{
		return http;
	}

	public void setHttp(HttpClient http)
	{
		this.http = http;
	}

	public void setParams(Map params)
	{
		String paramStr = "";

		Iterator iter = params.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			paramStr += paramStr = "&" + key + "=" + val;
		}

		if (!paramStr.equals(""))
		{
			paramStr = paramStr.replaceFirst("&", "?");
			this.baseurl += paramStr;
		}
		this.params = paramStr;
	}

	public void setParams(String params)
	{
		this.params = params;
	}
 
	

}
