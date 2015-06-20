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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.ruchelper.HttpClientRestore;

public class QueryBook extends Thread {
	String url_queryBook = "http://202.112.118.30/uhtbin/cgisirsi/0/人大图书馆/0/123";
	final HttpClient httpclient = HttpClientRestore.getHttpClient();
	ArrayList<Book> books = null;

	public ArrayList<Book> getBooks() {
		return books;
	}
	
	ArrayList<String> detail=null;
	public ArrayList<String> getDetail() {
		return detail;
	}
	
	String bookName = null;
	String ps = null;
	public String getPS() {
		return ps;
	}
	String time = null;
	public String getTime() {
		return time;
	}
	boolean firstQuery = false;
	int viewId = -1;
	int totBooks = -1;
	int firstBookNumber =1;
	public int getFirstBookNumber(){
		return firstBookNumber;
	}
	int jumpNumber;
	public int getJumpNumber(){
		return jumpNumber;
	}

	public QueryBook(String bookName) {
		this.bookName = bookName;
		this.firstQuery = true;
	}

	public QueryBook(String ps, String time, int firstBookNumber, int jumpNumber) {
		this.ps = ps;
		this.time = time;
		this.firstBookNumber = firstBookNumber;
		this.jumpNumber = jumpNumber;
	}

	public QueryBook(int viewId, String ps, String time, int firstBookNumber,
			int jumpNumber) {
		this.viewId = viewId;
		this.ps = ps;
		this.time = time;
		this.firstBookNumber = firstBookNumber;
		this.jumpNumber = jumpNumber;
	}

	@Override
	public void run() {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (ps == null) {
			//第一次查询
			Log.e("test", "startQueryBook");
			nvps.add(new BasicNameValuePair("searchdata1", this.bookName));
			nvps.add(new BasicNameValuePair("srchfield1",
					"TI^TITLE^SERIES^Title Processing^题名"));
			nvps.add(new BasicNameValuePair("library", "ALL"));
			nvps.add(new BasicNameValuePair("sort_by", "TI"));
			nvps.add(new BasicNameValuePair("relevance", "off"));
		} else {
			//翻页
			nvps.add(new BasicNameValuePair("first_hit", this.firstBookNumber
					+ ""));
			nvps.add(new BasicNameValuePair("last_hit", this.firstBookNumber
					+ 19 + ""));
			if (viewId != -1) {
				nvps.add(new BasicNameValuePair("form_type", ""));
				nvps.add(new BasicNameValuePair("VIEW^" + viewId, "详细资料"));
			} else
				nvps.add(new BasicNameValuePair("form_type", "JUMP^"
						+ jumpNumber));
			this.url_queryBook = "http://202.112.118.30/uhtbin/cgisirsi/" + ps
					+ "/人大图书馆/" + time + "/9";
		}
		for (int i = 0; i < nvps.size(); i++) {
			Log.d("test", nvps.get(i).getName() + "  " + nvps.get(i).getValue());
		}
		HttpPost httpPost = new HttpPost(url_queryBook);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			HttpResponse response = httpclient.execute(httpPost);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			String line = "";
			StringBuffer sbHTML = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sbHTML.append(line);
			}
			if (this.viewId == -1) {
				this.books = getBooks(new String(sbHTML));
				Message msg = new Message();
				Bundle data = new Bundle();
				if (this.totBooks == -1) {
					int index = new String(sbHTML).indexOf("没找到所需文献");
					Log.d("book","book index"+index);
					if(index == -1){ //存在这本书
						detail = getBookDetails(new String(sbHTML));
						data.putStringArrayList("bookDetail", detail);
						Log.d("book", detail.size() + "");
						this.totBooks = 0;
					}
				}
				
				data.putCharSequence("ps", this.ps);
				data.putCharSequence("time", this.time);
				data.putInt("totBooks", this.totBooks);
				data.putSerializable("bookResult", this.books);
				//Log.e("test", this.ps + "  " + this.time);

				msg.setData(data);
				if (firstQuery){
					if(QueryBookActivity.myHandler!=null){
						QueryBookActivity.myHandler.sendMessage(msg);
					}
				}
					
				else{
					if(QueryBookResultActivity.myHandler!=null){
						QueryBookResultActivity.myHandler.sendMessage(msg);
					}
				}
					
			} else {
				this.detail = getBookDetails(new String(sbHTML));
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putStringArrayList("bookDetail", detail);
				msg.setData(data);
				if(QueryBookDetailActivity.myHandler!=null){
					QueryBookDetailActivity.myHandler.sendMessage(msg);
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpPost.abort();
		}
	}

	protected ArrayList<String> getBookDetails(String html) {
		ArrayList<String> detail = new ArrayList<String>();
		Document doc = Jsoup.parse(html);
		Elements tableRows = doc.getElementsByTag("dd");
		for (int i = 0; i < 7; i++) {
			Element row = tableRows.get(i);
			detail.add(row.text());
		}

		tableRows = doc.getElementsByTag("table").get(0).getElementsByTag("tr");
		for (Element row : tableRows) {
			Elements tds = row.getElementsByTag("td");
			String res = "";
			for (Element td : tds) {
				res = res + td.text().trim() + "~!";
			}
			detail.add(res);
		}
		return detail;
	}

	protected ArrayList<Book> getBooks(String html) {
		ArrayList<Book> books = new ArrayList<Book>();
		Document doc = Jsoup.parse(html);
		Elements tableRows = doc.getElementsByClass("hit_list_row");
		for (Element row : tableRows) {
			String title = row.getElementsByClass("title").text();
			String author = row.getElementsByClass("author").text();
			String callNumber = row.getElementsByClass("call_number").text();
			String holdingsStatement = row.getElementsByClass(
					"holdings_statement").text();
			String urlDetail = "";
			Book cur = new Book(title, author, callNumber, holdingsStatement,
					urlDetail);
			books.add(cur);
		}
		tableRows = doc.getElementsByClass("hit_list_form");

		if (tableRows.size() > 0) {
			Element info = tableRows.get(0);
			String temp = info.attr("action");
			//System.out.println(temp);

			String[] t = temp.split("/");
			for (int i = 0; i < t.length; i++) {
				Log.v("test", i + "  " + t[i]);
			}

			this.ps = t[3];
			this.time = t[5];
		}

		tableRows = doc.getElementsByClass("searchsummary");
		if (tableRows.size() > 0) {
			Element info = tableRows.get(0);
			String temp = info.getElementsByTag("em").text().trim();
			if (!(temp == null || temp == ""))
				this.totBooks = Integer.parseInt(temp);
		}
		return books;
	}
}
