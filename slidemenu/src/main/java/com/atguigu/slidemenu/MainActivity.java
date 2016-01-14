package com.atguigu.slidemenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView tv_title;
    private SlideLayout ml_main;
    private ImageView main_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ml_main = (SlideLayout)findViewById(R.id.ml_main);
        tv_title = (TextView)findViewById(R.id.tv_title);
        main_back = (ImageView)findViewById(R.id.main_back);
        main_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ml_main.switchState();
            }
        });
    }

    public void clickMenuItem(View v) {
        TextView textView = (TextView) v;
        String name = textView.getText().toString();
        tv_title.setText(name);
        ml_main.switchState();
    }
}
