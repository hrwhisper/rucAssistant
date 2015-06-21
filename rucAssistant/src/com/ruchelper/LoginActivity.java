package com.ruchelper;

import com.vruc.LoginVruc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	public static Handler myHandler=null;
	private EditText userName, password;
	private CheckBox rememberPassword, autoLogin;
	private ProgressDialog pd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		init();
		if(getRestoreInfo()){	//获取保存的账号密码
			login(new View(this));
		}
	}
		
	
	@SuppressLint("HandlerLeak")
	public void login(View v){
		final String userNameValue = userName.getText().toString();//获取用户名
		final String passwordValue = password.getText().toString();//获取密码
		if(userNameValue.equals("") || passwordValue.equals("")){//判断用户名密码是否为空
			Toast.makeText(getApplicationContext(), "用户名和密码不能为空！", Toast.LENGTH_SHORT)
			.show();
			return;
		}
		
		try {
			final LoginVruc mytask = new LoginVruc(userNameValue,passwordValue);
			pd= ProgressDialog.show(LoginActivity.this, "登录", "正在登录…");
		    mytask.start();
		          			
			myHandler = new Handler() {
				// 定义处理消息的方法
				@Override
				public void handleMessage(Message msg) {
					if(msg.what==1){//登陆成功
						Intent intent = new Intent(LoginActivity.this,
								MainActivity.class);//跳转主界面
						if(rememberPassword.isChecked()){ //若有记住密码选项进行记住密码操作
							writeData(userNameValue,passwordValue);
						}
						startActivity(intent);
						finish();
					}else{//登录失败
						Log.e("test","用户名或密码错误");
						Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_LONG)
						.show();
					}
					pd.dismiss();
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG)
					.show();
		}
	}
	//初始化并获取各项组件
	void init(){
		this.userName = (EditText) findViewById(R.id.username);
		this.password = (EditText) findViewById(R.id.password);
		this.rememberPassword = (CheckBox) findViewById(R.id.rememberPassword);
		this.autoLogin = (CheckBox) findViewById(R.id.autoLogin);
	}
	
	//获取保存的账号密码
	boolean getRestoreInfo(){
		SharedPreferences sharedPreferences = getSharedPreferences("vrucLogin",
				Activity.MODE_PRIVATE);
		this.userName.setText(sharedPreferences.getString("userName", ""));
		this.password.setText(sharedPreferences.getString("password", ""));
		this.rememberPassword.setChecked(sharedPreferences.getBoolean("rememberPassword", false));
		this.autoLogin.setChecked(sharedPreferences.getBoolean("autoLogin", false));
		return this.autoLogin.isChecked();//返回是否为自动登录
	}
	//保存账户密码
	void writeData(String userNameValue,String passwordValue){
		SharedPreferences mySharedPreferences = getSharedPreferences("vrucLogin",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("userName", userNameValue);
		editor.putString("password", passwordValue);
		editor.putBoolean("rememberPassword", this.rememberPassword.isChecked());
		editor.putBoolean("autoLogin", this.autoLogin.isChecked());
		editor.commit();	
	}
}
