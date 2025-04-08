package com.clubfactory.platform.scheduler.core.utils;

import com.clubfactory.platform.scheduler.common.Constants;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * http utils
 */
public class HttpUtils {
	
	
	public static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

	/**
	 * get http request content
	 * @param url
	 * @return http response
	 */
	public static String get(String url){
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpGet httpget = new HttpGet(url);
		/** set timeout、request time、socket timeout */
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(Constants.HTTP_CONNECT_TIMEOUT)
				.setConnectionRequestTimeout(Constants.HTTP_CONNECTION_REQUEST_TIMEOUT)
				.setSocketTimeout(Constants.SOCKET_TIMEOUT)
				.setRedirectsEnabled(true)
				.build();
		httpget.setConfig(requestConfig);
		String responseContent = null;
		CloseableHttpResponse response = null;

		try {
			response = httpclient.execute(httpget);
			//check response status is 200
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					responseContent = EntityUtils.toString(entity, Constants.UTF_8);
				}else{
					logger.warn("http entity is null");
				}
			}else{
				logger.error("htt get:{} response status code is not 200!");
			}
		}catch (Exception e){
			logger.error(e.getMessage(),e);
		}finally {
			try {
				if (response != null) {
					EntityUtils.consume(response.getEntity());
					response.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}

			if (httpget != null && !httpget.isAborted()) {
				httpget.releaseConnection();
				httpget.abort();
			}

			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		return responseContent;
	}

}
