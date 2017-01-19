package com.zd.vpn.ui;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zd.vpn.R;

public class CertListAdapter extends BaseCustomAdapter {

	List<CertListItem> data;

	public List<CertListItem> getData() {
		return data;
	}

	public void setData(List<CertListItem> data) {
		this.data = data;
	}

	public CertListAdapter(Context context) {
		super(context);
	}

	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflateView(R.layout.zd_cert_item);
			holder = new ViewHolder();
			holder.title = findViewById(R.id.title, convertView);
			holder.value = findViewById(R.id.value, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CertListItem item = data.get(position);
		holder.title.setText(item.getTitle());
		holder.value.setText(item.getValue());
		return convertView;
	}

	class ViewHolder {
		TextView title;
		TextView value;
	}
}
