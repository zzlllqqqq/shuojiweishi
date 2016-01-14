package com.atguigu.ce_hua_caidan;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private ImageView main_back;
    private TextView tv_title;
    private SlideLayout lm_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_back = (ImageView)findViewById(R.id.main_back);
        tv_title = (TextView)findViewById(R.id.tv_title);
        lm_main = (SlideLayout)findViewById(R.id.lm_main);

        main_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lm_main.changState();
            }
        });
    }

    public void clickMenuItem(View v) {
        TextView textView = (TextView) v;
        String name = textView.getText().toString();
        tv_title.setText(name);
        lm_main.changState();
    }
}
