package com.library;

import java.io.Serializable;

public class Book implements Serializable{
	private static final long serialVersionUID = -7574222344588238013L;
	private String title;				//书名
	private String author;				//作者
	private String callNumber;			//索书号
	private String holdingsStatement;	//馆藏位置
	private String urlDetail; 			//url_detail
	private String time; 				//归还日期
	public Book(String title,String author,String callNumber,String holdingsStatement,String urlDetail){
		this.title=title;
		this.author=author;
		this.callNumber=callNumber;
		this.holdingsStatement = holdingsStatement;
		this.urlDetail = urlDetail;
	}
	public Book(String title,String time){
		this.title=title;
		this.time=time;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getCallNumber() {
		return callNumber;
	}
	public void setCallNumber(String callNumber) {
		this.callNumber = callNumber;
	}
	public String getHoldingsStatement() {
		return holdingsStatement;
	}
	public void setHoldingsStatement(String holdingsStatement) {
		this.holdingsStatement = holdingsStatement;
	}
	public String getUrlDetail() {
		return urlDetail;
	}
	public void setUrlDetail(String urlDetail) {
		this.urlDetail = urlDetail;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
