package com.library;

import java.util.ArrayList;

import com.ruchelper.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class LoginLibraryActivity extends Activity {
	public static Handler myHandler;
	private ProgressDialog pd;
	private EditText userName, password;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login_library);
		init();
	}

	@SuppressLint("HandlerLeak")
	public void login(View v) {
		final String userNameValue = userName.getText().toString();//获取用户名
		final String passwordValue = password.getText().toString();//获取密码
		if (userNameValue.equals("") || passwordValue.equals("")) {
			Toast.makeText(getApplicationContext(), "用户名和密码不能为空！",
					Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			final LoginLibrary mytask = new LoginLibrary(userNameValue,
					passwordValue);//登录
			pd = ProgressDialog.show(LoginLibraryActivity.this, "查询", "正在进行查询…");
			mytask.start();

			myHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					pd.dismiss();
					if (msg.what == 1)//登陆成功
					{
						ArrayList<String> books = msg.getData().getStringArrayList("borrowBooksList");
						if (books.size() == 0) {//没有借阅书籍的情况直接在屏幕上说明
							Toast.makeText(getApplicationContext(), "您没有借阅任何书籍",
									Toast.LENGTH_LONG).show();
						} 
						else{//有借阅书籍的情况跳转到结果页并显示
							Intent intent = new Intent(LoginLibraryActivity.this,QueryBorrowBookActivity.class);
							intent.putExtra("borrowBooksList", books);
							startActivity(intent);
							finish();
						}
					} 
					else {//密码出错情况
						pd.dismiss();
						Toast.makeText(getApplicationContext(), "用户名或密码错误",
								Toast.LENGTH_LONG).show();
					}
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG)
					.show();
		}
	}

	//初始化，获取相应的组件
	void init() {
		setContentView(R.layout.activity_login_library);
		this.userName = (EditText) findViewById(R.id.lib_username);
		this.password = (EditText) findViewById(R.id.lib_password);
		SharedPreferences sharedPreferences = getSharedPreferences("vrucLogin",
				Activity.MODE_PRIVATE);
		this.userName.setText(sharedPreferences.getString("userName", ""));
	}
}
