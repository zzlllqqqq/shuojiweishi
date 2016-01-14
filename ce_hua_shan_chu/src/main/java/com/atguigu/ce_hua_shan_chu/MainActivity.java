package com.atguigu.ce_hua_shan_chu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapter.base.CommonBaseAdapter;
import com.example.adapter.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ListView lv_main;
    private CommonBaseAdapter<MyBean> adapter;
    private List<MyBean> data = new ArrayList<>();
    private SlideLayout openedLayout;
    private SlideLayout.OnStateChangeListener onStateChangeListener = new SlideLayout.OnStateChangeListener() {
        @Override
        public void onOpen(SlideLayout slideLayout) {
            //打开时候将新的slideLayout保存到定义的成员变量中去
            openedLayout = slideLayout;
        }

        @Override
        public void onClose(SlideLayout slideLayout) {
            if (slideLayout == openedLayout) {
                //关闭后将它赋空
                openedLayout = null;
            }
        }

        @Override
        public void onDown(SlideLayout slideLayout) {
            if (openedLayout != null && slideLayout != openedLayout) {
                //当点击发生时将前一个slideLayout关闭
                //同在这之前需要判断新点击的slideLayout是不是之前已经打开的layout
                //时需要判断是否为首次展开一个slideLayout
                //调用menuClose()之后再close方法中已经将slideLayout赋空
                //所以完全没有必要再次将layout赋空
                openedLayout.menuClose();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv_main = (ListView) findViewById(R.id.lv_main);
        initData();
        adapter = new CommonBaseAdapter<MyBean>(this, data, R.layout.item_main) {
            @Override
            public void convert(ViewHolder holder, final int position) {
                final MyBean bean = data.get(position);
                holder.setText(R.id.tv_item_content, bean.content)
                        .setOnclickListener(R.id.tv_item_content, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, bean.content, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setOnclickListener(R.id.tv_item_menu, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                data.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        });

                //要判断slideLayout的改变状态通过给其添加自定义监听从而通知activity
                //之后activity可以调用slideLayout自身的打开关闭方法
                SlideLayout slideLayout = (SlideLayout) holder.getConvertView();
                slideLayout.setOnStateChangeListener(onStateChangeListener);
            }
        };
        lv_main.setAdapter(adapter);
    }

    private void initData() {
        for (int i = 0; i < 30; i++) {
            data.add(new MyBean("content" + i));
        }
    }
}
