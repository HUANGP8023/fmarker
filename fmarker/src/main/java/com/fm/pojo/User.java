package com.fm.pojo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {



	public User(long sid, String uName, String uPass) {
		super();
		this.sid = sid;
		this.uName = uName;
		this.uPass = uPass;
	}
	
	private long sid;
	private String uName;
	private String uPass;
	
	
	
	public long getSid() {
		return sid;
	}
	public void setSid(long sid) {
		this.sid = sid;
	}
	public String getuName() {
		return uName;
	}
	public void setuName(String uName) {
		this.uName = uName;
	}
	public String getuPass() {
		return uPass;
	}
	public void setuPass(String uPass) {
		this.uPass = uPass;
	}
}
