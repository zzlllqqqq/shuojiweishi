package com.atguigu.weishi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.atguigu.weishi.dao.AddressDao;

public class AddressQueryActivity extends Activity {

    private EditText et_address_number;
    private TextView tv_address_result;
    private AddressDao addressDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_query);

        TextView textView = (TextView) findViewById(R.id.tv_title);
        textView.setText("号码归属地查询");

        et_address_number = (EditText)findViewById(R.id.et_address_number);
        tv_address_result = (TextView)findViewById(R.id.tv_address_result);
        addressDao = new AddressDao(this);

    }

    public void queryAddress(View v) {
        String number = et_address_number.getText().toString().trim();
        if("".equals(number)) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
            et_address_number.startAnimation(animation);
        } else {
            String address = addressDao.getAddress(number);
            tv_address_result.setText(address);
        }
    }
}
