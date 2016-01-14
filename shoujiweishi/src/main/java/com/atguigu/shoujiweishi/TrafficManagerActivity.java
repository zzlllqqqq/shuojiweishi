package com.atguigu.shoujiweishi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.atguigu.shoujiweishi.adapter.base.CommonBaseAdapter;
import com.atguigu.shoujiweishi.adapter.base.ViewHolder;
import com.atguigu.shoujiweishi.bean.TrafficInfo;
import com.atguigu.shoujiweishi.util.MSUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TrafficManagerActivity extends Activity {

    private TextView tv_traffic_2g_3g;
    private TextView tv_traffic_wifi;
    private SlidingDrawer sd_traffic;
    private ListView lv_traffic;
    private List<TrafficInfo> data;
    private CommonBaseAdapter<TrafficInfo> adapter;
    private ProgressDialog pd;
    private long moblieSieze;
    private long wifiSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_manager);

        tv_traffic_2g_3g = (TextView)findViewById(R.id.tv_traffic_2g_3g);
        tv_traffic_wifi = (TextView)findViewById(R.id.tv_traffic_wifi);
        sd_traffic = (SlidingDrawer)findViewById(R.id.sd_traffic);
        lv_traffic = (ListView)findViewById(R.id.lv_traffic);

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPreExecute() {
                pd = ProgressDialog.show(TrafficManagerActivity.this, null, "正在读取中");
            }

            @Override
            protected Void doInBackground(Void... params) {
                data = MSUtils.getAllTrafficInfos(TrafficManagerActivity.this);
                Collections.sort(data, new Comparator<TrafficInfo>() {
                    @Override
                    public int compare(TrafficInfo lhs, TrafficInfo rhs) {
                        return -(int) (lhs.getInSize() + lhs.getOutSize() - rhs.getInSize() - rhs.getOutSize());
                    }
                });
                //得到手机总的下载流量和上传流量(2g/3g/wifi)
                long totalRxBytes = TrafficStats.getTotalRxBytes();
                long totalTxBytes = TrafficStats.getTotalTxBytes();
                //得到手机的下载流量和上传流量(2g/3g)
                long mobileRxBytes = TrafficStats.getMobileRxBytes();
                long mobileTxBytes = TrafficStats.getMobileTxBytes();

                moblieSieze = mobileRxBytes + mobileTxBytes;
                wifiSize = totalRxBytes + totalTxBytes - moblieSieze;

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                pd.dismiss();
                tv_traffic_2g_3g.setText("2g/3g流量" + MSUtils.formatSize(TrafficManagerActivity.this, moblieSieze));
                tv_traffic_wifi.setText("wifi流量" + MSUtils.formatSize(TrafficManagerActivity.this, wifiSize));
                adapter = new CommonBaseAdapter<TrafficInfo>(TrafficManagerActivity.this, data, R.layout.item_traffic_manager) {
                    @Override
                    public void convert(ViewHolder holder, int position) {
                        TrafficInfo info = data.get(position);
                        holder.setImageDrawable(R.id.iv_traffic_icon, info.getIcon())
                        .setText(R.id.tv_traffic_name, info.getAppName())
                        .setText(R.id.tv_traffic_transmitted, MSUtils.formatSize(TrafficManagerActivity.this, info.getOutSize()))
                        .setText(R.id.tv_traffic_received, MSUtils.formatSize(TrafficManagerActivity.this, info.getInSize()));
                    }
                };
                lv_traffic.setAdapter(adapter);
                sd_traffic.animateOpen();
            }
        }.execute();
    }
}
