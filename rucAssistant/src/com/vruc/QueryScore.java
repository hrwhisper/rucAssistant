package com.vruc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.ruchelper.HttpClientRestore;
import android.os.Message;

public class QueryScore extends Thread {
	private String url_score = "http://app.ruc.edu.cn/idc/education/report/xscjreport/XscjReportAction.do?method=printXscjReport&xh=";
	private final HttpClient httpclient = HttpClientRestore.getHttpClient();
	private ArrayList<String[]> scores = null;

	public ArrayList<String[]> getScore(){
		return scores;
	}
	
	@Override
	public void run() {
		HttpGet httpget = new HttpGet(url_score);
		try {
			//设置头部
			httpget.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
			httpget.addHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpget.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
			
			HttpResponse response = httpclient.execute(httpget);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "GBK"));
			
			String line = "";
			StringBuffer sbHTML = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sbHTML.append(line);
			}
			this.scores = getScore(new String(sbHTML));//获取score

			Message msg = new Message();
			msg.obj = scores;
			if(QueryScoreActivity.myHandler!=null)
				QueryScoreActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpget.abort();
		}
	}
	//html解析，从中获取score
	protected ArrayList<String[]> getScore(String html) throws IOException {
		Document doc = Jsoup.parse(html);
		Elements tableRows = doc.getElementsByTag("tr");
		ArrayList<String[]> scores = new ArrayList<String[]>();
		//表格的内容
		final String[] names = new String[]{"课程名称","教师",	"课程类别",	"学分","平时",	"期中",	"期末",	"最终成绩","学分绩点","缺考原因"};
		for (Element row : tableRows) {
			Elements tds = row.getElementsByClass("cellContent");
			if (tds.size() == 0)continue;
			if(tds.get(0).attr("colspan").equals("13")) continue;
			
			String[] cur = new String[tds.size()];
			int j = 0;
			for (Element td : tds) {
				String linkText = td.text();
				if(j!=0) cur[j] = names[j]+":  " +linkText;
				else cur[j] = linkText;
				j++;
			}
		//	Log.d("score", j+"");
			
			scores.add(cur);
		}
		//Log.e("score", i + "  name " + names.length+" score  "+scores.size());
		return scores;
	}
}
