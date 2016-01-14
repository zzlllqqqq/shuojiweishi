package com.atguigu.weishi.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.weishi.R;
import com.atguigu.weishi.util.SPUtils;

/**
 * Created by admin on 2015/12/27.
 */
public class MainAdapter extends BaseAdapter {

    private Context context;
    private String[] names;
    private int[] icons;

    public MainAdapter(Context context, String[] names, int[] icons) {
        this.context = context;
        this.names = names;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return names[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_main, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_icon);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imageView.setImageResource(icons[position]);
        holder.textView.setText(names[position]);
        if(position == 0) {
            String savedName = SPUtils.getInstanse(context).getValue(SPUtils.NAME, null);
            if(savedName!=null) {
                holder.textView.setText(savedName);
            }
        }
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
