package com.atguigu.shoujiweishi.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.shoujiweishi.R;
import com.atguigu.shoujiweishi.bean.AppInfo;

import java.util.List;

/**
 * Created by admin on 2016/1/5.
 */
public class AppLockAdapter extends BaseAdapter {

    private Context context;
    private List<AppInfo> data;
    private List<String> lockAppPackageName;

    public AppLockAdapter(Context context, List<AppInfo> data, List<String> lockAppPackageName) {
        this.context = context;
        this.data = data;
        this.lockAppPackageName = lockAppPackageName;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.item_app_lock, null);
            holder = new ViewHolder();
            holder.iconIv = (ImageView) convertView.findViewById(R.id.iv_item_lock_icon);
            holder.lockIv = (ImageView) convertView.findViewById(R.id.iv_item_lock_unlock);
            holder.nameTv = (TextView) convertView.findViewById(R.id.tv_item_lock_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AppInfo appInfo = data.get(position);
        holder.iconIv.setImageDrawable(appInfo.getIcon());
        holder.nameTv.setText(appInfo.getAppName());
        if(lockAppPackageName.contains(appInfo.getPackageName())) {
            holder.lockIv.setImageResource(R.drawable.lock);
            holder.lockIv.setTag(true);//是否锁定:锁定
        } else {
            holder.lockIv.setImageResource(R.drawable.unlock);
            holder.lockIv.setTag(false);//是否锁定:不锁定
        }
        return convertView;
    }

    class ViewHolder{
        ImageView iconIv;
        ImageView lockIv;
        TextView nameTv;
    }
}
