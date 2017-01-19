package com.zd.vpn.ui;

public class CertListItem {
	private String title;
	private String value;

	public CertListItem(String title, String value) {
		this.title = title;
		this.value = value;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
