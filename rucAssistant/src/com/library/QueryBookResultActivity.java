package com.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import com.ruchelper.R;

public class QueryBookResultActivity extends ListActivity {
	static Handler myHandler;
	Button button_lastPage, button_nextPage;
	private int firstBookNumber = 1;
	private ListView list =null;
	private int totBooks = 0;
	private String ps = "";
	private String time = "";

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		Bundle data=getIntent().getExtras();
		@SuppressWarnings("unchecked")
		ArrayList<Book> books = (ArrayList<Book>) data.getSerializable("bookResult");
		ps = data.getString("ps");
		time = data.getString("time");
		totBooks = data.getInt("totBooks");
		if (totBooks != 0) addBookContent(books);
	}

	//下一页的实现
	@SuppressLint("HandlerLeak")
	public void nexgPage(View v) {
		Log.e("test", firstBookNumber + "");
		QueryBook mytask = new QueryBook(ps, time, firstBookNumber,
				firstBookNumber + 20);
		mytask.start();
		myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// list.removeAllViews();
				firstBookNumber += 20;	//本页第一本书需要进行+20操作
				Bundle data = msg.getData();
				@SuppressWarnings("unchecked")
				ArrayList<Book> books = (ArrayList<Book>) data.getSerializable("bookResult");//获取书籍信息
				ps = data.getString("ps");//更新ps和time值
				time = data.getString("time");
				addBookContent(books);	//显示结果
			}
		};
	}
	//上一页的实现
	@SuppressLint("HandlerLeak")
	public void lastPage(View v) {
		Log.e("test", firstBookNumber + "");
		QueryBook mytask = new QueryBook(ps, time, firstBookNumber,
				firstBookNumber - 20);
		mytask.start();
		myHandler = new Handler() {
			@SuppressLint("HandlerLeak")
			@Override
			public void handleMessage(Message msg) {
				firstBookNumber -= 20;	//本页第一本书需要进行-20操作
				Bundle data = msg.getData();
				@SuppressWarnings("unchecked")
				ArrayList<Book> books = (ArrayList<Book>) data.getSerializable("bookResult");//获取书籍信息
				ps = data.getString("ps");	//更新ps和time值
				time = data.getString("time");
				addBookContent(books);	//显示结果
			}
		};
	}

	//初始化并且获取相应组件
	public void init() {
		list = getListView();  
		button_lastPage = new Button(this);	//新建上一页按钮
		button_nextPage = new Button(this); //新建下一页按钮
		button_lastPage.setText("<");
		button_nextPage.setText(">");
		button_lastPage.setBackgroundResource(R.drawable.pagebutton_guide_bg);
		button_nextPage.setBackgroundResource(R.drawable.pagebutton_guide_bg);
		button_lastPage.setOnClickListener(new OnClickListener() {//设置上一页事件监听
			@Override
			public void onClick(View v) {
				lastPage(v);
			}
		});
		button_nextPage.setOnClickListener(new OnClickListener() {//设置下一页事件监听
			@Override
			public void onClick(View v) {
				nexgPage(v);
			}
		});
		//显示效果微调
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				-2, -2);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, -1);
		RelativeLayout relativeLayout = new RelativeLayout(this);
		relativeLayout.addView(button_lastPage, layoutParams);
		layoutParams = new RelativeLayout.LayoutParams(-2, -2);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, -1);
		relativeLayout.addView(button_nextPage, layoutParams);
		list.addFooterView(relativeLayout);
		
		//一定要在添加进layout后才能更改大小
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics());
		button_lastPage.getLayoutParams().width=(int) px;
		button_nextPage.getLayoutParams().width=(int) px;
	}

	//将结果添加进Listview并显示结果
	public void addBookContent(ArrayList<Book> books) {
		ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < books.size(); i++) {
			Book book = books.get(i);
			String title = firstBookNumber + i + ". " + book.getTitle();
			String author = "作者 : " + book.getAuthor();
			String call_number = "索书号 : " + book.getCallNumber();
			String holdings_statement = "馆藏信息 : " + book.getHoldingsStatement();
		
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("title", title);
			item.put("author", author);
			item.put("call_number", call_number);
			item.put("holdings_statement", holdings_statement);
			mData.add(item);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, mData,
				R.layout.activity_query_book_result, new String[] { "title",
						"author", "call_number", "holdings_statement" },
				new int[] { R.id.book_title, R.id.book_author,
						R.id.book_call_number, R.id.book_holdings_statement });
		list.setBackgroundResource(R.drawable.app_background);
		list.setAdapter(adapter);
		//设置点击后的事件（跳转到详细信息）
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(QueryBookResultActivity.this,
						QueryBookDetailActivity.class);
				intent.putExtra("viewId", position + firstBookNumber);
				// Log.v("test",(position+firstBookNumber)+"");
				intent.putExtra("ps", getPs());
				intent.putExtra("time", getTime());
				intent.putExtra("firstBookNumber", firstBookNumber);
				startActivity(intent);
			}
		});
		//根据是否为第一页判断是否显示上一页按钮
		if (firstBookNumber == 1)
			button_lastPage.setVisibility(View.INVISIBLE);
		else
			button_lastPage.setVisibility(View.VISIBLE);
		//根据是否为最后一页判断是否显示下一页按钮
		if (firstBookNumber + 20 >= totBooks)
			button_nextPage.setVisibility(View.INVISIBLE);
		else
			button_nextPage.setVisibility(View.VISIBLE);
	}

	public String getTime() {
		return time;
	}

	public String getPs() {
		return ps;
	}

}
