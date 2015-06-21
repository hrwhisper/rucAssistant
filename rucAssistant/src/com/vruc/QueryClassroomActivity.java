package com.vruc;

import com.ruchelper.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class QueryClassroomActivity extends Activity {
	private LinearLayout linearLayout = null;
	static Handler myHandler;
	private ProgressDialog pd;

	private static final int[] week = { 1, 2, 3, 4, 5, 6, 7 };// 星期
	private static final String[] building = { "公共教学一楼", "公共教学二楼", "公共教学三楼",
			"公共教学四楼", "明德主楼", "明德商学楼", "明德国际楼", "明德新闻楼", "明德法学楼", "求是楼" };// 教学楼
	private static final int[] startTime = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
			12, 13, 14, 15 };// 开始节数
	private static final int[] endTime = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
			12, 13, 14, 15 };// 结束的节数
	private String[] week_string = { "星期一", "星期二", "星期三", "星期四", "星期五", "星期六",
			"星期日" };
	private String[] startTime_string = new String[startTime.length];
	private String[] endTime_string = new String[endTime.length];
	private TextView textView_week; // 教学周
	private TextView textView_building; // 教学楼
	private TextView textView_startTime; // 起始时间
	private TextView textView_endTime; // 结束时间
	private Spinner spinner_week , spinner_building,spinner_startTime,spinner_endTime;//相应的Spinner
	private ArrayAdapter<String> adapter_week,adapter_building,adapter_startTime,adapter_endTime;//相应的ArrayAdapter

	private int post_week = 0; // 选定的星期几
	private String post_building = null;// 选定的教学楼
	private int post_startTime = 0; // 选定的开始时间
	private int post_endTime = 0; // 选定的结束时间

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置无标题模式
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_classroom);
		init();
		setListener();
	}

	// 获取相应的组件并且初始化
	private void init() {
		for (int index = 0; index < startTime.length; index++) {
			startTime_string[index] = "第" + startTime[index] + "节";
			endTime_string[index] = "第" + endTime[index] + "节";
		}
		linearLayout = (LinearLayout) findViewById(R.id.classroom_linearLayout);
		textView_week = (TextView) findViewById(R.id.spinnerText1);
		spinner_week = (Spinner) findViewById(R.id.spinner_week);
		textView_building = (TextView) findViewById(R.id.spinnerText2);
		spinner_building = (Spinner) findViewById(R.id.spinner_building);
		textView_startTime = (TextView) findViewById(R.id.spinnerText3);
		spinner_startTime = (Spinner) findViewById(R.id.spinner_startTime);
		textView_endTime = (TextView) findViewById(R.id.spinnerText4);
		spinner_endTime = (Spinner) findViewById(R.id.spinner_endTime);
		// 将可选内容与ArrayAdapter连接起来
		adapter_week = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, week_string);
		adapter_building = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, building);
		adapter_startTime = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, startTime_string);
		adapter_endTime = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, endTime_string);
		// 设置下拉列表的风格
		adapter_week
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter_building
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter_startTime
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter_endTime
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 将adapter 添加到spinner中
		spinner_week.setAdapter(adapter_week);
		spinner_building.setAdapter(adapter_building);
		spinner_startTime.setAdapter(adapter_startTime);
		spinner_endTime.setAdapter(adapter_endTime);
		// 设置默认值
		spinner_week.setVisibility(View.VISIBLE);
		spinner_building.setVisibility(View.VISIBLE);
		spinner_startTime.setVisibility(View.VISIBLE);
		spinner_endTime.setVisibility(View.VISIBLE);
	}

	// 设置时间监听
	private void setListener() {
		// 添加点击侦听器
		spinner_week.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				textView_week.setText("教学日：");
				post_week = week[arg2];
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				Toast.makeText(getApplicationContext(), "没选中",
						Toast.LENGTH_SHORT).show();
			}
		});
		
		spinner_building
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						textView_building.setText("教学楼：");
						post_building = building[arg2];
					}

					public void onNothingSelected(AdapterView<?> arg0) {
						Toast.makeText(getApplicationContext(), "没选中",
								Toast.LENGTH_SHORT).show();
					}
				});

		spinner_startTime
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						textView_startTime.setText("起始时间：");
						post_startTime = startTime[arg2];
					}

					public void onNothingSelected(AdapterView<?> arg0) {
						Toast.makeText(getApplicationContext(), "没选中",
								Toast.LENGTH_SHORT).show();
					}
				});

		spinner_endTime.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				textView_endTime.setText("结束时间：");
				post_endTime = endTime[arg2];
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				Toast.makeText(getApplicationContext(), "没选中",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@SuppressLint("HandlerLeak")
	public void queryClassroom(View v) {
		if (post_week == 0 || post_building == "" || post_startTime == 0
				|| post_endTime == 0) {
			return;
		}
		if (post_startTime > post_endTime) {
			Toast.makeText(getApplicationContext(), "起始时间不得大于结束时间",
					Toast.LENGTH_SHORT).show();
			return;
		}
		QueryClassroom mytask = new QueryClassroom(post_week, post_building,
				post_startTime, post_endTime);
		pd = ProgressDialog.show(QueryClassroomActivity.this, "空教室查询", "正在查询…");
		mytask.start();
		Log.e("mytask", "start");
		myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.e("Handler", "start");
				pd.dismiss();
				String classrooms = (String) msg.obj; //获取空教室信息
				addTextView(classrooms);	//显示空教室
			}
		};
	}
	
	//添加textView用于显示空教室结果
	public void addTextView(String temp) {
		linearLayout.removeAllViews();	//移除之前的textView，防止多次查询多个textView
		TextView textView = new TextView(this);
		String[] classrooms = temp.split("\n");
		String res = "";
		for (String classroom : classrooms) {
			res = res + classroom + "   ";
		}
		textView.setText(res);
		textView.setPadding(20, 15, 20, 15);
		textView.setBottom(10);
		linearLayout.addView(textView);
	}
}
