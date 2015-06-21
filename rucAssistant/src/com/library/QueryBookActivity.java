package com.library;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.ruchelper.R;

public class QueryBookActivity extends Activity {
	private EditText bookName = null;
	static Handler myHandler =null;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_query_book);
		init();
	}

	void init() {
		bookName = (EditText) this.findViewById(R.id.queryBook);
	}

	@SuppressLint("HandlerLeak")
	public void queryBook(View v) {
		// 空格呢
		if (this.bookName.getText().toString().trim().equals("")) {
			return;
		}
		// Intent intent = new Intent();
		// intent.putExtra("bookname", this.bookName.getText().toString());
		// intent.setClass(QueryBookActivity.this,
		// QueryBookResultActivity.class);
		// startActivity(intent);
		
		QueryBook mytask = new QueryBook(this.bookName.getText().toString());
		pd = ProgressDialog.show(QueryBookActivity.this, "书籍查询", "正在查询…");
		mytask.start();//开始查询
		myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				pd.dismiss();	
				Bundle data = msg.getData();
				int totBooks = data.getInt("totBooks");
				if (totBooks == -1)
					Toast.makeText(QueryBookActivity.this, "没有找到您要的书籍",
							Toast.LENGTH_SHORT).show();
				else {
					Intent intent = new Intent();
					if (totBooks == 0) {//单本的情况
						ArrayList<String> detail = (ArrayList<String>) data
								.getStringArrayList("bookDetail");
						for (int i = 0; i < detail.size(); i++)
							Log.d("book", detail.get(i));
						intent.setClass(QueryBookActivity.this,QueryBookDetailActivity.class);
					} else {//多本的情况
						intent.setClass(QueryBookActivity.this,QueryBookResultActivity.class);
					}
					intent.putExtras(data);
					startActivity(intent);
				}
			}
		};
	}
}
