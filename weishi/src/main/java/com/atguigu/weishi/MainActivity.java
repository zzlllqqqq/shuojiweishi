package com.atguigu.weishi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.atguigu.weishi.adapter.MainAdapter;
import com.atguigu.weishi.util.MsUtils;
import com.atguigu.weishi.util.SPUtils;



public class MainActivity extends Activity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private GridView gv_main;
    private static final String[] NAMES = new String[]{
            "手机防盗", "通讯卫士", "软件管理", "流量管理", "进程管理",
            "手机杀毒", "缓存清理", "高级工具", "设置中心"};

    private static final int[] ICONS = new int[]{R.drawable.widget01,
            R.drawable.widget02, R.drawable.widget03, R.drawable.widget04,
            R.drawable.widget05, R.drawable.widget06, R.drawable.widget07,
            R.drawable.widget08, R.drawable.widget09};
    private MainAdapter adapter;
    private String passWrod;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 2:
                    exit = false;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gv_main = (GridView) findViewById(R.id.gv_main);
        adapter = new MainAdapter(this, NAMES, ICONS);
        gv_main.setAdapter(adapter);

        gv_main.setOnItemLongClickListener(this);

        gv_main.setOnItemClickListener(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            final EditText editText = new EditText(this);
            final TextView textView = (TextView) view.findViewById(R.id.tv_item_name);
            String name = textView.getText().toString();
            editText.setHint(name);
            new AlertDialog.Builder(this)
                    .setTitle("修改名称")
                    .setView(editText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newName = editText.getText().toString();
                            textView.setText(newName);
                            SPUtils.getInstanse(MainActivity.this).save(SPUtils.NAME, newName);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                showSetOrLoginDialog();
                break;
            case 1:
                startActivity(new Intent(this, BlackNumberActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, AppManagerActivity.class));
                break;
            case 3:

                break;
            case 4:

                break;
            case 5:

                break;
            case 6:

                break;
            case 7:
                startActivity(new Intent(this, AtoolActivity.class));
                break;
            case 8:

                break;
        }
    }

    private void showSetOrLoginDialog() {
        passWrod = SPUtils.getInstanse(MainActivity.this).getValue(SPUtils.PASSWORD, null);
        if (passWrod == null) {
            showSetDialog();
        } else {
            showLoginDialog();
        }
    }

    private EditText et_login_dialog_pwd;
    private Button btn_login_dialog_confirm;
    private Button btn_login_dialog_cancel;

    private void showLoginDialog() {
        View view = View.inflate(this, R.layout.login_dialog_layout, null);
        et_login_dialog_pwd = (EditText) view.findViewById(R.id.et_login_dialog_pwd);
        btn_login_dialog_confirm = (Button) view.findViewById(R.id.btn_login_dialog_confirm);
        btn_login_dialog_cancel = (Button) view.findViewById(R.id.btn_login_dialog_cancel);
        btn_login_dialog_confirm.setOnClickListener(this);
        btn_login_dialog_cancel.setOnClickListener(this);
        dialog = new AlertDialog.Builder(this).setView(view).show();
    }

    private AlertDialog dialog;
    private EditText et_set_dialog_pwd;
    private EditText et_set_dialog_pwd2;
    private Button btn_set_dialog_confirm;
    private Button btn_set_dialog_cancel;

    private void showSetDialog() {
        View view = View.inflate(this, R.layout.set_dialog_layout, null);
        et_set_dialog_pwd = (EditText) view.findViewById(R.id.et_set_dialog_pwd);
        et_set_dialog_pwd2 = (EditText) view.findViewById(R.id.et_set_dialog_pwd2);
        btn_set_dialog_confirm = (Button) view.findViewById(R.id.btn_set_dialog_confirm);
        btn_set_dialog_cancel = (Button) view.findViewById(R.id.btn_set_dialog_cancel);
        btn_set_dialog_cancel.setOnClickListener(this);
        btn_set_dialog_confirm.setOnClickListener(this);
        dialog = new AlertDialog.Builder(this).setView(view).show();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_set_dialog_confirm) {
            if ("".equals(et_set_dialog_pwd.getText().toString().trim())) {
                MsUtils.showMsg(this, "请输入密码");
                return;
            }
            if (!(et_set_dialog_pwd.getText().toString().trim()).equals(et_set_dialog_pwd2.getText().toString().trim())) {
                MsUtils.showMsg(this, "两次输入不一致");
                return;
            }
            SPUtils.getInstanse(MainActivity.this).save(SPUtils.PASSWORD, MsUtils.md5(et_set_dialog_pwd.getText().toString().trim()));
            dialog.dismiss();
        } else if (v == btn_set_dialog_cancel) {
            dialog.dismiss();
        } else if (v == btn_login_dialog_cancel) {
            dialog.dismiss();
        } else if (v == btn_login_dialog_confirm) {
            if(!MsUtils.md5(et_login_dialog_pwd.getText().toString().trim()).equals(passWrod)) {
                MsUtils.showMsg(this, "密码输入错误");
            } else {
                toProtect();
                dialog.dismiss();
            }
        }
    }

    private void toProtect() {
        Boolean configure = SPUtils.getInstanse(this).getValue(SPUtils.CONFIGURE, false);
        if(!configure) {
            startActivity(new Intent(MainActivity.this, Setup1Activity.class));
        } else {
            startActivity(new Intent(MainActivity.this, ProtectinfoActivity.class));
        }

    }

    boolean exit = false;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(!exit) {
                exit = true;
                MsUtils.showMsg(this, "请再次点击返回键退出");
                handler.sendEmptyMessageDelayed(2, 2000);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
