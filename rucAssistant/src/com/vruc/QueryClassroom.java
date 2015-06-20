package com.vruc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import com.ruchelper.HttpClientRestore;
import android.os.Message;
import android.util.Log;

public class QueryClassroom extends Thread {
	private String url_classroom = "http://ruchelper.hrwhisper.me/emptyClassroom.php";
	final HttpClient httpclient = HttpClientRestore.getHttpClient();
	private int week=0;				
	private String building=null;	//上课地点
	private int startTime=0;
	private int endTime=0;
	private String result="";
	
	
	public String getResult() {
		return result;
	}

	public QueryClassroom(int week,String building,int startTime,int endTime){
		this.week=week;
		this.building=building;
		this.startTime=startTime;
		this.endTime=endTime;
	}
	
	@Override
	public void run() {
		Log.e("class", "startQueryClassroom");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("week", this.week+""));
		params.add(new BasicNameValuePair("building", this.building));
		params.add(new BasicNameValuePair("startTime", this.startTime+""));
		params.add(new BasicNameValuePair("endTime", this.endTime+""));
		
		HttpPost httpPost = new HttpPost(url_classroom);
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpResponse response = httpclient.execute(httpPost);
			this.result=EntityUtils.toString(response.getEntity());
			Message msg = new Message();
			msg.obj = result;
			if(QueryClassroomActivity.myHandler!=null)
				QueryClassroomActivity.myHandler.sendMessage(msg);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpPost.abort();
		}
	}
}
