package com.vruc;

import java.util.ArrayList;
import java.util.HashMap;
import com.ruchelper.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class QueryMyCourseAcitivity extends Activity {
	public static Handler myHandler = null;
	private ProgressDialog pd;
	private TextView empty;//第一列为空
	private TextView monColum,tueColum , wedColum,thuColum,friColum,satColum,sunColum;//周一到周天的列
	private RelativeLayout course_table_layout; // 课程表body部分布局
	private int gridWidth; // 格子宽度
	private int gridHeight; // 格子高度
	private final int row = 14;
	private final int col = 8;
	HashMap<String, String> hashmap = new HashMap<String, String>() {
		private static final long serialVersionUID = 3732116102755121365L;
		{
			put("", "0");
			put("星期一", "1");
			put("星期二", "2");
			put("星期三", "3");
			put("星期四", "4");
			put("星期五", "5");
			put("星期六", "6");
			put("星期日", "7");
		}
	};

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_course);
		init();
		try {
			QueryMyCourse myCourse = new QueryMyCourse();
			pd= ProgressDialog.show(QueryMyCourseAcitivity.this, "课表查询", "正在查询…");	      
			myCourse.start();
		          			
			myHandler = new Handler(){
				// 定义处理消息的方法
				@Override
				public void handleMessage(Message msg) {
					pd.dismiss();
					@SuppressWarnings("unchecked")
					ArrayList<Course> myCourse = (ArrayList<Course>) msg.obj;//得到课程信息
					drawTimetable(myCourse);	//绘制课程表
				}
			};
		} catch (Exception e) {
		}

	}

	void drawTimetable(ArrayList<Course> myCourse) {
		for (int i = 1; i <= row; i++) {
			for (int j = 1; j <= col; j++) {
				TextView tx = new TextView(QueryMyCourseAcitivity.this);
				tx.setId((i - 1) * 8 + j);
				// 使用course_text_view_bg背景
				tx.setBackgroundResource(R.drawable.course_text_view_bg);
				// 相对布局参数
				RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
						gridWidth * 33 / 32 + 1, gridHeight);
				// 文字对齐方式
				tx.setGravity(Gravity.CENTER);
				// 字体样式
				// tx.setTextAppearance(this, R.style.courseTableText);
				// 如果是第一列，需要设置课的序号（1 到 12）
				if (j == 1) {
					tx.setText(String.valueOf(i));
					rp.width = gridWidth * 3 / 4;
					// 设置他们的相对位置
					if (i == 1)
						rp.addRule(RelativeLayout.BELOW, empty.getId());
					else
						rp.addRule(RelativeLayout.BELOW, (i - 1) * 8);
				} else {
					rp.addRule(RelativeLayout.RIGHT_OF, (i - 1) * 8 + j - 1);
					rp.addRule(RelativeLayout.ALIGN_TOP, (i - 1) * 8 + j - 1);
					tx.setText("");
				}

				tx.setLayoutParams(rp);
				course_table_layout.addView(tx);
			}
		}
		// 五种颜色的背景
		int[] background = { R.drawable.course_info_blue,
				R.drawable.course_info_green, R.drawable.course_info_red,
				R.drawable.course_info_red, R.drawable.course_info_yellow };

		for (int i = 0; i < myCourse.size(); i++) {
			// 添加课程信息
			Course cur = myCourse.get(i);
			TextView courseInfo = new TextView(this);

			String param = cur.getName();
			String text = param + "\n" + cur.getPlace();
			SpannableStringBuilder ss = new SpannableStringBuilder(text);
			ss.setSpan(new StyleSpan(Typeface.BOLD), 0, param.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			ss.setSpan(new AbsoluteSizeSpan(9, true), 0, param.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			ss.setSpan(new AbsoluteSizeSpan(8, true), param.length(),
					text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			courseInfo.setText(ss);

			// 该textview的高度根据其节数的跨度来设置
			RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
					gridWidth * 31 / 32, (gridHeight - 5)
							* (cur.getEndTime() - cur.getStartTime() + 1));

			// textview的位置由课程开始节数和上课的时间（day of week）确定
			rlp.topMargin = 5 + (cur.getStartTime() - 1) * gridHeight;
			rlp.leftMargin = 1;
			rlp.addRule(RelativeLayout.RIGHT_OF,
					Integer.parseInt(hashmap.get(cur.getWeek())));

			courseInfo.setGravity(Gravity.CENTER);// 字体居中
			courseInfo.setBackgroundResource(background[i % 5]);// 设置一种背景
			courseInfo.setLayoutParams(rlp);
			courseInfo.setTextColor(Color.WHITE);
			courseInfo.getBackground().setAlpha(222);// 设置不透明度
			course_table_layout.addView(courseInfo);
		}
	}
	
	//获取相应的组件并且初始化
	void init() {
		empty = (TextView) this.findViewById(R.id.myCourse_empty);
		monColum = (TextView) this.findViewById(R.id.myCourse_monday);
		tueColum = (TextView) this.findViewById(R.id.myCourse_tuesday);
		wedColum = (TextView) this.findViewById(R.id.myCourse_wednesday);
		thuColum = (TextView) this.findViewById(R.id.myCourse_thursday);
		friColum = (TextView) this.findViewById(R.id.myCourse_friday);
		satColum = (TextView) this.findViewById(R.id.myCourse_saturday);
		sunColum = (TextView) this.findViewById(R.id.myCourse_sunday);
		course_table_layout = (RelativeLayout) this
				.findViewById(R.id.myCourse_RelativeLayout);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels; // 屏幕宽度
		int aveWidth = screenWidth / 8; // 平均宽度
		empty.setWidth(aveWidth * 3 / 4); // 第一个空白格子设置为25宽
		monColum.setWidth(aveWidth * 33 / 32 + 1);
		tueColum.setWidth(aveWidth * 33 / 32 + 1);
		wedColum.setWidth(aveWidth * 33 / 32 + 1);
		thuColum.setWidth(aveWidth * 33 / 32 + 1);
		friColum.setWidth(aveWidth * 33 / 32 + 1);
		satColum.setWidth(aveWidth * 33 / 32 + 1);
		sunColum.setWidth(aveWidth * 33 / 32 + 1);
		this.gridWidth = aveWidth;
		this.gridHeight = dm.heightPixels / 15;
	}
}
