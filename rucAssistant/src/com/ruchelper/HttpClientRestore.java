package com.ruchelper;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class HttpClientRestore {
	private static final String CHARSET = HTTP.UTF_8;
	private static HttpClient httpclient = null;
	public static synchronized HttpClient getHttpClient() {
		if (httpclient == null) {	
			HttpParams params =new BasicHttpParams();
            // 设置一些基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params,CHARSET);
            HttpProtocolParams.setUseExpectContinue(params, true);
            HttpProtocolParams
                    .setUserAgent(
                            params,
                            "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
                                    +"AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
           
            //从连接池中取连接的超时时间
            ConnManagerParams.setTimeout(params, 1000);
            //连接超时
            HttpConnectionParams.setConnectionTimeout(params, 2000);
            //请求超时
            HttpConnectionParams.setSoTimeout(params, 4000);
            httpclient =new DefaultHttpClient(params);
		}
		return httpclient;
	}
	//httpclient关闭
	public static synchronized void colseHttpClient(){
		if(httpclient!=null)
			httpclient.getConnectionManager().shutdown(); 
		httpclient = null;
	}
}
