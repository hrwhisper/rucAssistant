package com.vruc;

import java.util.ArrayList;
import com.ruchelper.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

public class QueryScoreActivity extends Activity {
	private ArrayList<String[]> scores = null;
	private ProgressDialog pd;
	public static Handler myHandler = null;
	@SuppressLint("HandlerLeak")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		final ExpandableListView expandListView = (ExpandableListView) findViewById(R.id.scoreExpandableListView);

		try {
			QueryScore score = new QueryScore();
			pd= ProgressDialog.show(QueryScoreActivity.this, "成绩查询", "正在查询…");
			score.start();
			myHandler = new Handler() {
				// 定义处理消息的方法
				@SuppressWarnings("unchecked")
				@Override
				public void handleMessage(Message msg) {
					pd.dismiss();
					scores = (ArrayList<String[]>) msg.obj;
//					for(int i=0;i<scores.size();i++){
//						for(int j=0;j<scores.get(i).length;j++){
//							Log.v("test",scores.get(i)[j]);
//						}
//					}
					expandListView.setAdapter(new ScoreExpandableListAdaper(
							scores));
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "成绩查询失败", Toast.LENGTH_LONG)
					.show();
		}
	}

	class ScoreExpandableListAdaper extends BaseExpandableListAdapter {
		ArrayList<String[]> scores = null;

		public ScoreExpandableListAdaper(ArrayList<String[]> scores) {
			this.scores = scores;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return scores.get(groupPosition)[childPosition+1];
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			String name = (String)getChild(groupPosition,childPosition);
			return getChildTextView(name);
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return scores.get(groupPosition).length-1;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return scores.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return scores.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			String name = scores.get(groupPosition)[0];
			return getTextView(name);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		private TextView getChildTextView(String s) {
			Resources r = getResources();
			int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34, r.getDisplayMetrics());
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,  px);
			TextView textView = new TextView(QueryScoreActivity.this);
			textView.setLayoutParams(lp);
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, r.getDisplayMetrics());
			textView.setPadding(px, 0, 0, 0);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
			textView.setText(s);
			return textView;
		}
		
		private TextView getTextView(String s) {
			Resources r = getResources();
			int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42, r.getDisplayMetrics());
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,  px);
			TextView textView = new TextView(QueryScoreActivity.this);
			textView.setLayoutParams(lp);
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics());
			textView.setPadding(px, 0, 0, 0);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
			textView.setText(s);
			return textView;
		}
	}
}
