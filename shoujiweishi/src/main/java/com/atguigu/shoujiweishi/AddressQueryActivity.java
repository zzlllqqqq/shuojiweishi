package com.atguigu.shoujiweishi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.atguigu.shoujiweishi.dao.AddressDao;

public class AddressQueryActivity extends Activity {

    private EditText et_address_number;
    private TextView tv_address_result;
    private AddressDao addressDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_query);
        tv_address_result = (TextView) findViewById(R.id.tv_address_result);
        et_address_number = (EditText) findViewById(R.id.et_address_number);
        addressDao = new AddressDao(this);
    }

    public void queryAddress(View v) {
        String number = et_address_number.getText().toString().trim();
        if("".equals(number)){
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
            et_address_number.startAnimation(animation);
        } else {
            String address = addressDao.getAddress(number);
            tv_address_result.setText(address);
        }
    }
}
