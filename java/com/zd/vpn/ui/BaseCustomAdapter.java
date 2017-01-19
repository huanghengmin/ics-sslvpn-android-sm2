package com.zd.vpn.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

public abstract class BaseCustomAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater layoutInflater;

	public BaseCustomAdapter(Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
	}

	protected View inflateView(int layout) {
		return layoutInflater.inflate(layout, null);
	}

	protected Context getContext() {
		return context;
	}

	@SuppressWarnings("unchecked")
	protected <V extends View> V findViewById(int id, View convertView) {
		return (V) convertView.findViewById(id);
	}
}
