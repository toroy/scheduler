package com.clubfactory.platform.scheduler.core.utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.clubfactory.platform.common.util.Assert;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttp3Utils {

	private static final MediaType JSON_MEDIA_TYPE  = MediaType.parse("application/json; charset=utf-8");
	// 读取超时，单位秒
	private static int READ_TIMEOUT = 15; 
	// 连接超时，单位秒
	private static int CONNECT_TIMEOUT = 15;
	
	private static OkHttpClient client;
	
	static {
		client = new OkHttpClient();
	}
	
	public static <T> T postObject(String url, String json, Class<T> responseType) {
		String resultString = post(url, json);
		return JSON.parseObject(resultString, responseType);
	}
	
	public static String post(String url, String json) {
		return post(url, json, READ_TIMEOUT, CONNECT_TIMEOUT);
	}
	
	public static  <T> T getObject(String url, Class<T> responseType) {
		String resultString = get(url);
		return JSON.parseObject(resultString, responseType);
	}
	
	public static String get(String url) {
		return get(url,READ_TIMEOUT, CONNECT_TIMEOUT);
	}

	public static String post(String url, String json, int readTimeOut, int connectTimeOut) {
		Assert.notBlank(url, "url");
		Assert.notBlank(json, "json");
		
		RequestBody body = RequestBody.create(JSON_MEDIA_TYPE  , json);
		Request request = new Request.Builder().url(url).post(body).build();
		return doExecute(request, readTimeOut, connectTimeOut);
	}
	
	public static String get(String url, int readTimeOut, int connectTimeOut) {
		Assert.notBlank(url, "url");
		Request request = new Request.Builder().url(url).get().build();
		return doExecute(request, readTimeOut, connectTimeOut);
	}
	
	public  static String doExecute(Request request,  int readTimeOut, int connectTimeOut) {
		Response response = null;
		try {
			response = client
					.newBuilder()
					.connectTimeout(connectTimeOut, TimeUnit.SECONDS)
					.readTimeout(readTimeOut, TimeUnit.SECONDS)
					.build()
					.newCall(request).execute();
			return response.body().string();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (response != null) {
				response.close();
			}
		}
		
	}
}
