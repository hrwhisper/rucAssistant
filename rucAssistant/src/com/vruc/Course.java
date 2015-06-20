package com.vruc;

public class Course {
	private String name;	//课程名称
	private String place;	//上课地点
	private String week;	//星期几
	private int startTime;	//起始节数
	private int endTime;	//终止节数

	public Course(String name, String place, String week, int startTime,
			int endTime) {
		this.name = name;
		this.place = place;
		this.week = week;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

}