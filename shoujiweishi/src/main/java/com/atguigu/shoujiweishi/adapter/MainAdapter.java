package com.atguigu.shoujiweishi.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.shoujiweishi.R;
import com.atguigu.shoujiweishi.util.SPUtils;

/**
 * Created by admin on 2015/12/27.
 */
public class MainAdapter extends BaseAdapter {

    private Context context;
    private String[] names;
    private int[] icons;
    //private SharedPreferences sp;

    public MainAdapter(Context context, String[] names, int[] icons) {
        this.context = context;
        this.names = names;
        this.icons = icons;
        //sp = context.getSharedPreferences("ms", Context.MODE_PRIVATE);
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
        //1. 创建或得到ViewHolder对象
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_main, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_icon);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //2. 得到当前地的数据
        //3. 设置数据
        holder.imageView.setImageResource(icons[position]);
        holder.textView.setText(names[position]);

        //第一个item的文本根据保存的名称显示
        if(position == 0) {
           //String saveName = sp.getString("NAME",null);
            String saveName = SPUtils.getInstance(context).getValue(SPUtils.NAME, null);
            if(saveName!=null) {
                holder.textView.setText(saveName);
            }
        }

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
