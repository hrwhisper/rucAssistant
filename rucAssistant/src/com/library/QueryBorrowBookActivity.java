package com.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ruchelper.R;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
public class QueryBorrowBookActivity extends ListActivity {
	protected void onCreate(Bundle savedInstanceState) {
		ArrayList<String> books = getIntent().getStringArrayListExtra("borrowBooksList");
		ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
		String title = "";
		//Log.v("book","books"+books.size()+"");
		for (int i = 0; i < books.size(); i++) {
			Log.d("book",books.get(i));
			if ((i & 1) != 0) {//为两个一组，为奇数的时候恰好为一组
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("title", title);
				item.put("text", books.get(i));
				mData.add(item);
			} else {
				title = books.get(i);
			}
		}
		SimpleAdapter adapter = new SimpleAdapter(this, mData,
				R.layout.activity_query_borrow_book, 
						new String[] { "title","text" }, 
						new int[] { R.id.borrowBook_title,R.id.borrowBook_date });
		setListAdapter(adapter);
		super.onCreate(savedInstanceState);
		ListView list = getListView(); 
		list.setBackgroundResource(R.drawable.app_background);//设置背景
	}
}
