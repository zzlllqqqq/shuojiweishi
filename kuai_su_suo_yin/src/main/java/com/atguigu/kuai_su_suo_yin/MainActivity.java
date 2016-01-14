package com.atguigu.kuai_su_suo_yin;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.adapter.base.CommonBaseAdapter;
import com.example.adapter.base.ViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends Activity {

    private ListView lv_main;
    private TextView tv_main_word;
    private QuickIndexView qiv_main_words;

    private List<Person> data;
    private CommonBaseAdapter<Person> adapter;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv_main = (ListView)findViewById(R.id.lv_main);
        tv_main_word = (TextView)findViewById(R.id.tv_main_word);
        qiv_main_words = (QuickIndexView)findViewById(R.id.qiv_main_words);

        qiv_main_words.setOnIndexChangeListener(new QuickIndexView.OnIndexChangeListener() {
            @Override
            public void OnIndexChange(String word) {
                handler.removeCallbacksAndMessages(null);
                tv_main_word.setVisibility(View.VISIBLE);
                tv_main_word.setText(word);
                for (int i = 0; i < data.size(); i++) {
                    if(data.get(i).getPinyin().substring(0, 1).equals(word)) {
                        lv_main.setSelection(i);
                        return;
                    }
                }
            }

            @Override
            public void OnUp() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_main_word.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        });
        initData();
        adapter = new CommonBaseAdapter<Person>(this, data, R.layout.item_main) {
            @Override
            public void convert(ViewHolder holder, int position) {
                Person person = data.get(position);
                holder.setText(R.id.tv_item_word, person.getPinyin().substring(0, 1))
                        .setText(R.id.tv_item_name, person.getName());
                if(position == 0) {
                    holder.setVisibility(R.id.tv_item_word, View.VISIBLE);
                } else {
                    String word = person.getPinyin().substring(0, 1);
                    String preWord = data.get(position - 1).getPinyin().substring(0, 1);
                    if(word.equals(preWord)) {
                        holder.setVisibility(R.id.tv_item_word, View.GONE);
                    } else {
                        holder.setVisibility(R.id.tv_item_word, View.VISIBLE);
                    }
                }
            }
        };
        lv_main.setAdapter(adapter);
    }

    private void initData() {
        data = new ArrayList<>();
        // 虚拟数据
        data.add(new Person("张晓飞"));
        data.add(new Person("杨光福"));
        data.add(new Person("胡继群"));
        data.add(new Person("刘畅"));

        data.add(new Person("钟泽兴"));
        data.add(new Person("尹革新"));
        data.add(new Person("安传鑫"));
        data.add(new Person("张骞壬"));

        data.add(new Person("温松"));
        data.add(new Person("李凤秋"));
        data.add(new Person("刘甫"));
        data.add(new Person("娄全超"));
        data.add(new Person("张猛"));

        data.add(new Person("王英杰"));
        data.add(new Person("李振南"));
        data.add(new Person("孙仁政"));
        data.add(new Person("唐春雷"));
        data.add(new Person("牛鹏伟"));
        data.add(new Person("姜宇航"));

        data.add(new Person("刘挺"));
        data.add(new Person("张洪瑞"));
        data.add(new Person("张建忠"));
        data.add(new Person("侯亚帅"));
        data.add(new Person("刘帅"));

        data.add(new Person("乔竞飞"));
        data.add(new Person("徐雨健"));
        data.add(new Person("吴亮"));
        data.add(new Person("王兆霖"));

        data.add(new Person("阿三"));

        //对集合中的对象进行排序 person类中实现comparable接口
        Collections.sort(data);
    }
}
