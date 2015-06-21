package com.vruc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.ruchelper.HttpClientRestore;
import com.ruchelper.LoginActivity;

import android.os.Message;
import android.util.Log;

public class LoginVruc extends Thread {
	private String url_token = "https://uc.tiup.cn/account/login?client_id=uc.tiup.cn&redirect_uri=%2Foauth%2Fauthorize%3Fclient_id%3Duc.tiup.cn%26redirect_uri%3Dhttp%253A%252F%252Fv.ruc.edu.cn%252Fsso%252Flogin%253Fredirect_uri%253D%25252FUser%2526school_code%253Druc%2526theme%253Dschools%26response_type%3Dcode%26school_code%3Druc%26scope%3Dall%26sso%3Dtrue%26state%3DZ08-C1r99RVtBwYVkEm12XxfhvVlW680YDKwfAaPU1DG-_mx%26theme%3Dschools&response_type=code&school_code=ruc&scope=all&sso=true&state=Z08-C1r99RVtBwYVkEm12XxfhvVlW680YDKwfAaPU1DG-_mx&theme=schools";
	private String url_login = "https://uc.tiup.cn/account/login";
	private String url_redirect = "https://uc.tiup.cn/oauth/authorize?client_id=uc.tiup.cn&amp;redirect_uri=http%3A%2F%2Fv.ruc.edu.cn%2Fsso%2Flogin%3Fredirect_uri%3D%252FUser%26school_code%3Druc%26theme%3Dschools&amp;response_type=code&amp;school_code=ruc&amp;scope=all&amp;sso=true&amp;state=vhfL6zHS_J4cH88Z_A-ZHl7RIUxmPrZEnffvYdtsCf7tWpz7&amp;theme=schools";
	private final HttpClient httpclient = HttpClientRestore.getHttpClient();
	private String userName = null;
	private String password = null;
	private HttpGet httpget= null;
	private HttpPost httpPost= null;
	boolean ok=false;
	public boolean getOK(){
		return ok;
	}
	public LoginVruc(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}
	//先获取csrf_token值，该值用于登陆发送的一个字段
	public String getToken() throws ClientProtocolException, IOException {
		this.httpget = new HttpGet(url_token);
		httpclient.execute(httpget);
		httpget.abort();
		List<Cookie> cookies = ((AbstractHttpClient) httpclient)
				.getCookieStore().getCookies();

		if (!cookies.isEmpty()) {
			for (int i = 0; i < cookies.size(); i++) {
				if (cookies.get(i).getName().equals("csrf_token"))
					return cookies.get(i).getValue();
			}
		}
		return null;
	}
	
	//登录操作
	public void login(String csrf_token) throws ClientProtocolException,
			IOException {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("csrf_token", csrf_token));
		nvps.add(new BasicNameValuePair("school_code", "ruc"));
		nvps.add(new BasicNameValuePair("username", this.userName));
		nvps.add(new BasicNameValuePair("password", this.password));
		nvps.add(new BasicNameValuePair("remember_me", "true"));
		this.httpPost = new HttpPost(url_login);
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		httpclient.execute(httpPost);
		httpPost.abort();
	}
	//登陆后进行重定向
	public void redirect() throws ClientProtocolException, IOException {
		this.httpget = new HttpGet(url_redirect);
		httpclient.execute(httpget);
		httpget.abort();
	}

	@Override
	public void run() {
		try {
			//Log.v("test", "startLogin");
			this.login(getToken()); //登陆
			this.redirect();		//重定向
			List<Cookie> cookies = ((AbstractHttpClient) httpclient)
					.getCookieStore().getCookies();
			
			Message msg = new Message();
			msg.what=0;
			if (!cookies.isEmpty()) {//遍历cookies
				for (int i = 0; i < cookies.size(); i++) {
					Cookie cur = cookies.get(i);
					if(cur.getName().equals("access_token")){//根据cookies中是否有名为access_token判断是否登录成功
						msg.what=1;
						ok=true;
					}
					Log.v("test", cur.getName() + " " + cur.getValue());
				}
			}
			if(LoginActivity.myHandler!=null)	LoginActivity.myHandler.sendMessage(msg);
		} catch (IOException e) {
			e.printStackTrace();
			Message msg = new Message();
			msg.what = 0;
			if(LoginActivity.myHandler!=null) LoginActivity.myHandler.sendMessage(msg);
		}finally{
			if(this.httpget!=null)
				this.httpget.abort();
			if(this.httpPost!=null)
				this.httpPost.abort();
		}
	}
}
