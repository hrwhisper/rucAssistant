package com.ruchelper;

import com.library.LoginLibraryActivity;
import com.library.QueryBookActivity;
import com.vruc.QueryClassroomActivity;
import com.vruc.QueryMyCourseAcitivity;
import com.vruc.QueryScoreActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class MainActivity extends Activity {
	MenuInflater menuInflater;
	View guide_button_view;
	long mills = 300;
	boolean isLogin = false;
	boolean isShow = false;
	private RelativeLayout guide_button_Layout,main_relativelayout;
	private ImageView guide_button;
	private ImageView guide_button_about;
	private ImageView guide_button_logOut;
	private long firstTime = 0;
	private DisplayMetrics dm ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//设置不显示标题
		setContentView(R.layout.activity_main);
		findViewById();	//获取组件
		setListener();	//设置时间监听
	}
	//查找组件
	public void findViewById() {
		guide_button_view = (View) findViewById(R.id.desktop_button_menu);
		guide_button_Layout = (RelativeLayout) findViewById(R.id.guide_button_layout);
		guide_button = (ImageView) findViewById(R.id.guide_button);
		guide_button_about = (ImageView) findViewById(R.id.guide_button_AboutApp);
		guide_button_logOut = (ImageView) findViewById(R.id.guide_button_logOut);
		main_relativelayout = (RelativeLayout) findViewById(R.id.main_relativelayout);
		
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		RelativeLayout.LayoutParams labelParams =  (LayoutParams) main_relativelayout.getLayoutParams();
		labelParams.setMargins(dm.widthPixels/16, 0, dm.widthPixels/16, 0);
		main_relativelayout.setLayoutParams(labelParams);
		changeButtonSize(R.id.queryMyCourse);
		changeButtonSize(R.id.queryScore);
		changeButtonSize(R.id.queryClassroom);
		changeButtonSize(R.id.queryBook);
		changeButtonSize(R.id.queryBorrowBook);
		changeImageSize(R.id.vruc_logo);
		changeImageSize(R.id.library_logo);
	}

	//动态改变button大小
	void changeButtonSize(int viewId){
		Button b = (Button) findViewById(viewId);
		b.getLayoutParams().width=(int) ((int) dm.widthPixels/3.2) ;
		b.getLayoutParams().height=(int) ((int) dm.widthPixels/3.2)/3;
	}
	//动态改变imageView大小
	void changeImageSize(int viewId){
		ImageView bt = (ImageView) findViewById(viewId);
		bt.getLayoutParams().width=(int) ((int) dm.widthPixels/4.2) ;
		bt.getLayoutParams().height=(int) ((int) dm.widthPixels/4.2);
	}
	
	
	//成绩查询
	public void getScore(View v) {
		startActivity(new Intent(MainActivity.this, QueryScoreActivity.class));
	}
	//课程查询
	public void queryMyCourse(View v) {
		startActivity(new Intent(MainActivity.this,
				QueryMyCourseAcitivity.class));
	}
	//空教室查询
	public void queryClassroom(View v) {
		startActivity(new Intent(MainActivity.this,
				QueryClassroomActivity.class));
	}
	//书籍查询
	public void queryBook(View v) {
		startActivity(new Intent(MainActivity.this, QueryBookActivity.class));
	}
	//已借阅书籍查询
	public void queryBorrowBook(View v) {
		startActivity(new Intent(MainActivity.this, LoginLibraryActivity.class));
	}
	//关闭功能按钮
	public void closeUgc() {
		isShow = false;
		UgcAnimations.startCloseAnimation(guide_button_Layout,
				guide_button, mills);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK://返回键的事件监听
			if (isShow) {
				isShow = false;
				UgcAnimations.startCloseAnimation(guide_button_Layout,
						 guide_button, mills);
				return false;
			}
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) { // 如果两次按键时间间隔大于2秒，则不退出
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				firstTime = secondTime;// 更新firstTime
				return true;
			} else {
				// 两次按键小于2秒时，退出应用
				finish();
				System.exit(0);
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void setListener() {
		//菜单监听
		guide_button_view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (isShow) {
					isShow = false;
					UgcAnimations.startCloseAnimation(guide_button_Layout,
							guide_button, mills);
				}
				return false;
			}
		});
		//显示菜单
		guide_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (isShow) {
					UgcAnimations.startCloseAnimation(guide_button_Layout,
							guide_button, mills);
				} else {
					UgcAnimations.startOpenAnimation(guide_button_Layout,
						 guide_button, mills);
				}
				isShow = !isShow;
			}
		});

		//退出登录按钮
		guide_button_logOut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Animation animation = UgcAnimations.clickAnimation(mills);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						HttpClientRestore.colseHttpClient(); //删除cookie
						SharedPreferences mySharedPreferences = getSharedPreferences("vrucLogin",
								Activity.MODE_PRIVATE);
						SharedPreferences.Editor editor = mySharedPreferences.edit();
						editor.putBoolean("autoLogin", false); //取消自动登录
						editor.commit();	
						closeUgc();
						startActivity(new Intent(MainActivity.this,
								LoginActivity.class));
						finish();
					}
				});
				guide_button_logOut.startAnimation(animation);
			}
		});
		
		//关于app按钮监听
		guide_button_about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Animation animation = UgcAnimations.clickAnimation(mills);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						Intent intent = new Intent(MainActivity.this,AboutAPP.class);
						intent.putExtra("aboutAPP", 1);
						startActivity(intent);
						closeUgc();
					}
				});
				guide_button_about.startAnimation(animation);
			}
		});
	}
}
