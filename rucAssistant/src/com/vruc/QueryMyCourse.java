package com.vruc;

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
import com.ruchelper.HttpClientRestore;
import android.os.Message;
import android.util.Log;

public class QueryMyCourse extends Thread{
	private String url_myCourse="http://app.ruc.edu.cn/idc/education/selectcourses/studentselectcourse/StudentSelectCourseAction.do";
	private final HttpClient httpclient = HttpClientRestore.getHttpClient();
	private ArrayList<Course> myCourse = null;
	
	
	public ArrayList<Course> getMyCourse() {
		return myCourse;
	}

	@Override
	public void run() {
		Log.e("test", "startQueryMyCourse");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();//相应的参数
		nvps.add(new BasicNameValuePair("method", "queryXkjg"));
		nvps.add(new BasicNameValuePair("isNeedInitSQL", "true"));
		nvps.add(new BasicNameValuePair("xnd", "2014-2015"));
		nvps.add(new BasicNameValuePair("xq", "2"));
		nvps.add(new BasicNameValuePair("condition_xnd", "2014-2015"));
		nvps.add(new BasicNameValuePair("condition_xq", "2"));
		nvps.add(new BasicNameValuePair("condition_kclb", ""));
		nvps.add(new BasicNameValuePair("condition_spbz", "3"));
		nvps.add(new BasicNameValuePair("pageNo", "1"));
		nvps.add(new BasicNameValuePair("pageSize", "100"));
		HttpPost httpPost = new HttpPost(url_myCourse);
		try {
			//设置头部
			httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
			httpPost.addHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			HttpResponse response = httpclient.execute(httpPost);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "GBK"));

			String line = "";
			StringBuffer sbHTML = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sbHTML.append(line);
			}
			this.myCourse = getMyCourse(new String(sbHTML));//获取课程
			Message msg = new Message();
			msg.obj = this.myCourse;
			if(QueryMyCourseAcitivity.myHandler!=null)
				QueryMyCourseAcitivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpPost.abort();
		}
	}
	//html解析并且获取相应的课程
	protected ArrayList<Course> getMyCourse(String html) throws IOException{
		Document doc = Jsoup.parse(html);
		Elements tableRows = doc.getElementsByTag("tr");
		ArrayList<Course> mycourse = new ArrayList<Course>();

		for (Element row : tableRows) {
			Elements tds = row.getElementsByClass("cellContent");
			if (tds.size() == 0 || !row.id().equals("ECNUTR"))
				continue;
			String name = tds.get(6).text(); // 获取课程名
			//String status = tds.get(5).text();  //通过状态
			//Log.d("test",status);
			//if(!status.equals("通过")) continue;
			//System.out.println(name);
			String t=tds.get(10).text();
			String place="",week="" ;
			int startTime = 0;
			int endTime = 0;
			if(!t.equals("查看详细...")){
				String[] info = tds.get(10).text().split(" ");
				int cnt=(info.length - 1)/3;
//				System.out.println(info.length);
//				
//				System.out.println("info length" + cnt);
//				for(int i=0;i<info.length;i++){
//					System.out.println(i +" "+ info[i]);
//				}
				for(int i=0;i<cnt;i++){
//					System.out.println(3 + i * 3);
					place = info[3 + i * 3 ].substring(1); // 获取上课地点
					String[] temp = info[2 + i*3].split(",");
					week = temp[0].substring(1); // 获取星期几
					//System.out.println(temp[1]);
					int index = temp[1].indexOf("～");
					String start = temp[1].substring(1,index),end = temp[1].substring(index + 1, temp[1].length() - 1);
					if(!start.equals("")) startTime = Integer.parseInt(start);
					if(!end.equals("")) endTime = Integer.parseInt(end);
					mycourse.add(new Course(name, place, week, startTime, endTime));
				}
			}
		}
		
//		for (int i = 0; i < mycourse.size(); i++) {
//			Course cur = mycourse.get(i);
//			Log.v("test",cur.getName());
//			Log.v("test",cur.getPlace());
//			Log.v("test",cur.getWeek());
//			Log.v("test",cur.getStartTime()+"");
//			Log.v("test",cur.getEndTime()+"");
//		}
		return mycourse;
	}
}

