package com.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.os.Bundle;
import android.os.Message;
import com.ruchelper.HttpClientRestore;
import com.ruchelper.LoginActivity;

public class LoginLibrary extends Thread {
	private final HttpClient httpclient = HttpClientRestore.getHttpClient();
	ArrayList<String> books = new ArrayList<String>();
	public ArrayList<String> getBooks() {
		return books;
	}

	private String url = "http://202.112.118.30/uhtbin/cgisirsi/0/人大图书馆/0/29/491/X/3";
	private String userName = null;
	private String password = null;
	private HttpGet httpget = null;
	private HttpPost httpPost = null;
	boolean ok=false;
	public boolean getOK(){
		return ok;
	}

	public LoginLibrary(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	private String getPostUrl() throws ClientProtocolException, IOException {
		this.httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		BufferedReader br = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent(), "UTF-8"));
		String line = "";
		StringBuffer html = new StringBuffer();
		while ((line = br.readLine()) != null) {
			html.append(line);
		}
		httpget.abort();
		Document doc = Jsoup.parse(new String(html));
		Element form = doc.getElementsByClass("renew_materials").last();
		return "http://202.112.118.30/" + form.attr("action");
	}

	private boolean login(String postUrl) throws ClientProtocolException,
			IOException {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("user_id", this.userName));
		param.add(new BasicNameValuePair("password", this.password));
		this.httpPost = new HttpPost(postUrl);
		httpPost.setEntity(new UrlEncodedFormEntity(param));
		HttpResponse response = httpclient.execute(httpPost);
		BufferedReader br = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent(), "UTF-8"));
		String line = "";
		StringBuffer shtml = new StringBuffer();
		while ((line = br.readLine()) != null) {
			shtml.append(line);
		}
		String html = new String(shtml);
		int index = html.indexOf("登录无效");
		Document doc = Jsoup.parse(html);
		Elements tableRows = doc.getElementsByClass("itemlisting2");
		for (int i = 0; i < tableRows.size();i++){
			Element row = tableRows.get(i);
			if(row.text().equals("")) continue;
			books.add(row.text());
		}
		tableRows = doc.getElementsByClass("itemlisting");
		for (int i = 0; i < tableRows.size();i++){
			Element row = tableRows.get(i);
			if(row.text().equals(""))continue;
			books.add(row.text());
		}
		
		httpPost.abort();
		if (index != -1) return false;
		return true;
	}

	@Override
	public void run() {
		try {
			//Log.v("test", "startLoginLibrary");
			String postUrl = this.getPostUrl();
			//Log.v("test", "postUrl: " + postUrl);
			this.ok = this.login(postUrl); //登陆
			Message msg = new Message();
			if(this.ok) msg.what = 1; 	//登陆成功
			else msg.what = 0;		//登陆出错
			Bundle data = new Bundle();
			data.putSerializable("borrowBooksList", this.books);
			msg.setData(data);
			if(LoginLibraryActivity.myHandler!=null){
				LoginLibraryActivity.myHandler.sendMessage(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
			Message msg = new Message();
			msg.what = 0;
			LoginActivity.myHandler.sendMessage(msg);
		} finally {
			if (this.httpget != null)
				this.httpget.abort();
			if (this.httpPost != null)
				this.httpPost.abort();
		}
	}
}
