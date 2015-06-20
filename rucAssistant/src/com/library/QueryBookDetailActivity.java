package com.library;

import java.util.ArrayList;
import com.ruchelper.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class QueryBookDetailActivity extends Activity{
	static Handler myHandler=null;
	private int firstBookNumber = 1;
	private String ps;
	private String time;
	private int viewId;
	private TableLayout tableLayout = null;
	private TableLayout tableLayoutPlace = null;
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_book_detail);
		init();
		if(viewId != -1)
		{
			QueryBook mytask = new QueryBook(viewId,ps, time, firstBookNumber,firstBookNumber + 20);
			mytask.start();
			myHandler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					ArrayList<String> detail = msg.getData().getStringArrayList("bookDetail");
					addTextView(detail);
				}
			};
		}
		else
		{
			ArrayList<String> detail = getIntent().getStringArrayListExtra("bookDetail");
			addTextView(detail);
		}
		
	}
	
	private void init(){
		tableLayout = (TableLayout) findViewById(R.id.library_detail_tableLayout);
		tableLayoutPlace = (TableLayout) findViewById(R.id.library_detail_tableLayout_place);
		viewId=getIntent().getIntExtra("viewId",-1);
		ps=getIntent().getStringExtra("ps");
		time = getIntent().getStringExtra("time");
		firstBookNumber = getIntent().getIntExtra("firstBookNumber",-1);
	}
	
	public void addTextView(ArrayList<String> detail){
		for(int i=0;i < 7;i++){
			TableRow tableRow = new TableRow(this);  
			TextView col1 = new TextView(this); 
	        TextView col2 = new TextView(this);
	        String colContent = null;
	        switch(i){
		        case 0:colContent="Title";			break;
		        case 1:colContent="Author";			break;
		        case 2:colContent="出版者";			break;
		        case 3:colContent="出版日期";		break;
		        case 4:colContent="附注";			break;
		        case 5:colContent="ISBN";			break;
		        case 6:colContent="馆藏分布状况";	break;
	        }
	        SpannableStringBuilder ss =new SpannableStringBuilder(colContent);
	        ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, colContent.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        col1.setText(ss);
			col2.setText(detail.get(i).trim());
			tableRow.addView(col1);  
		    tableRow.addView(col2);
		    tableLayout.addView(tableRow);
		}
		for(int i=7;i<detail.size();i++){
			TableRow tableRow = new TableRow(this); 
			String []row = detail.get(i).split("~!");
			for(String r :row){
				TextView textview = new TextView(this); 
				textview.setText(r);
				tableRow.addView(textview);
			}
			this.tableLayoutPlace.addView(tableRow);
		}
	}
}