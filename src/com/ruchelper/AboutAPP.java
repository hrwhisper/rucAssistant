package com.ruchelper;

import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;

public class AboutAPP extends Activity {
	protected final int SPLASH_DISPLAY_LENGHT = 1300; // 延迟 ms

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_about_app);
		ImageView imageView = (ImageView) findViewById(R.id.about_imageView);
		imageView.setImageBitmap(readBitMap(this,R.drawable.about_app));
		int id = getIntent().getIntExtra("aboutAPP", -1);
		if (id == -1) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					startActivity(new Intent(AboutAPP.this, LoginActivity.class));
					AboutAPP.this.finish();
				}
			}, SPLASH_DISPLAY_LENGHT);
		}
	}

	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}
}
