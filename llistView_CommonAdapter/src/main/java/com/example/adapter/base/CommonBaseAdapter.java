package com.example.adapter.base;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 通用的baseAdapter
 * @author xfzhang
 *
 * @param <T>
 */
public abstract class CommonBaseAdapter<T> extends BaseAdapter {

	private Context context;
	private List<T> data;
	private int layoutId;

	public CommonBaseAdapter(Context context, List<T> data, int layoutId) {
		this.context = context;
		this.data = data;
		this.layoutId = layoutId;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public T getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//得到Viewholder对象
		ViewHolder holder = ViewHolder.getHolder(context, convertView,
				layoutId, position);
		//调用未实现的抽象方法设置数据
		convert(holder, position);
		
		//返回holder中的convertView
		return holder.getConvertView();
	}

	/**
	 * 设置视图数据的抽象方法, 由具体的adapter子类来实现
	 * @param holder
	 * @param position
	 */
	public abstract void convert(ViewHolder holder, int position);
}
