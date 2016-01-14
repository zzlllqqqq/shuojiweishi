package com.atguigu.quickindex;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
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

        //给qiv_main_words设置自定义的下标改变的监听
        qiv_main_words.setOnIndexChangeListener(new QuickIndexView.OnIndexChangeListener() {
            @Override
            public void OnIndexChange(String word) {
                //移除未处理的消息
                handler.removeCallbacksAndMessages(null);

                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(300);
                tv_main_word.setAnimation(scaleAnimation);
                tv_main_word.setVisibility(View.VISIBLE);

                tv_main_word.setText(word);
                //根据字母找到对应的下标
                for(int i = 0; i < data.size(); i++) {
                    if(data.get(i).getPinyin().substring(0, 1).equals(word)) {
                        //设置指定的显示位置，这个位置Activity顶部
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
                        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                        alphaAnimation.setDuration(2000);
                        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotateAnimation.setDuration(2000);
                        AnimationSet animationSet = new AnimationSet(true);
                        animationSet.addAnimation(rotateAnimation);
                        animationSet.addAnimation(alphaAnimation);
                        //setAnimation 和 startAnimation区别
                        tv_main_word.startAnimation(animationSet);
                        //tv_main_word.setAnimation(animationSet);
                        tv_main_word.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        });
        //得到数据
        initData();
        //使用通用Adapter创建所需的adapter
        adapter = new CommonBaseAdapter<Person>(this, data, R.layout.item_main) {
            @Override
            public void convert(ViewHolder holder, int position) {
                Person person = data.get(position);
                //设置数据
                holder.setText(R.id.tv_item_word, person.getPinyin().substring(0, 1))
                    .setText(R.id.tv_item_name, person.getName());
                //设置数据显示状态
                if(position == 0) {
                    holder.setVisibility(R.id.tv_item_word, View.VISIBLE);
                } else {
                    //拿后数据一个和前一个进行比较，如果首字母相同那么就将 后一个的字母行隐藏
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
