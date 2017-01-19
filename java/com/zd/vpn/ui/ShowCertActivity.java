package com.zd.vpn.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

import com.zd.vpn.R;
import com.zd.vpn.util.Sender;
import com.zd.vpn.util.TFJniUtils;
import com.zd.vpn.util.ToastUtils;


/**
 * 查看证书
 */
public class ShowCertActivity extends Activity {

    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zd_cert);
        listView = (ListView) findViewById(R.id.list);
        CertListAdapter adapter = new CertListAdapter(getApplicationContext());
        listView.setAdapter(adapter);
        adapter.setData(loadSettings(getApplicationContext()));
        adapter.notifyDataSetChanged();
    }


    private List loadSettings(Context context) {
        List list = new ArrayList();
        TFJniUtils tfJniUtils = new TFJniUtils();
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
        boolean read = sharedPreferences.getBoolean("vpn.read", false);
        if (!read) {
            ToastUtils.showLong(context, "请先初始化基本配置！");
            CertListItem item = new CertListItem("信息", "请先初始化基本配置！");
            list.add(item);
            return list;
        } else {
            if (tfJniUtils.compareSN(context)) {
                String serialNumber = sharedPreferences.getString("vpn.serialNumber", "");
                CertListItem item = new CertListItem("证书序列号", serialNumber.length() > 0 ? serialNumber : "信息不全");
                list.add(item);

                String subject = sharedPreferences.getString("vpn.subject", "");
                item = new CertListItem("主题", subject.length() > 0 ? subject : "信息不全");
                list.add(item);

                String issue = sharedPreferences.getString("vpn.issue", "");
                item = new CertListItem("签发者", issue.length() > 0 ? issue : "信息不全");
                list.add(item);

                String city = sharedPreferences.getString("vpn.notBefore", "");
                item = new CertListItem("于以下日期之前无效", city.length() > 0 ? city : "信息不全");
                list.add(item);

                String company = sharedPreferences.getString("vpn.notAfter", "");
                item = new CertListItem("于以下日期之后无效", company.length() > 0 ? company : "信息不全");
                list.add(item);
                return list;
            } else {
                ToastUtils.showLong(context, "TF卡已更换，请重新进行初始化配置！");
                CertListItem item = new CertListItem("信息", "TF卡已更换，请重新进行初始化配置！");
                list.add(item);
                return list;
            }

        }
    }
}
