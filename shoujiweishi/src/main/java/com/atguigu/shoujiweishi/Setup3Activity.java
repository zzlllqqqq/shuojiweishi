package com.atguigu.shoujiweishi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.atguigu.shoujiweishi.util.MSUtils;
import com.atguigu.shoujiweishi.util.SPUtils;

public class Setup3Activity extends Activity {

    private EditText et_setup3_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        et_setup3_number = (EditText) findViewById(R.id.et_setup3_number);

        String savedSafeNumber = SPUtils.getInstance(this).getValue(SPUtils.SAFE_NUMBER, null);
        if(savedSafeNumber != null) {
            et_setup3_number.setText(savedSafeNumber);
        }
    }

    public void previous(View v) {
        finish();
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void next(View v) {
        String safeNumber = et_setup3_number.getText().toString();
        if(safeNumber.trim().equals("")) {
            MSUtils.showMsg(this, "必须指定安全号码！");
            return;
        }
        SPUtils.getInstance(this).save(SPUtils.SAFE_NUMBER, safeNumber);

        Intent intent = new Intent(this, Setup4Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void showContacts(View v) {
        startActivityForResult(new Intent(this, ContactListActivity.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK) {
            String number = data.getStringExtra("NUMBER");
            et_setup3_number.setText(number);
        }
    }
}
