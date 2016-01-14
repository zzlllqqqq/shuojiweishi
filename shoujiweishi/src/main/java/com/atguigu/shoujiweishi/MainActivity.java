package com.atguigu.shoujiweishi;

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

import com.atguigu.shoujiweishi.adapter.MainAdapter;
import com.atguigu.shoujiweishi.util.MSUtils;
import com.atguigu.shoujiweishi.util.SPUtils;


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
    //private SharedPreferences sp;
    private String password;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
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

        //给gv_main设置长按监听
        gv_main.setOnItemLongClickListener(this);

        //sp = getSharedPreferences("ms", MODE_PRIVATE);

        //给gv_main设置点击监听
        gv_main.setOnItemClickListener(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //只给第一个图标设置改名
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
                            //保存新名称
                            //sp.edit().putString("NAME", newName).commit();
                            SPUtils.getInstance(MainActivity.this).save(SPUtils.NAME, newName);
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
                startActivity(new Intent(this, BlackNumberActiity.class));
                break;
            case 2:
                startActivity(new Intent(this, AppManagerActivity.class));
                break;
            case 3:
                startActivity(new Intent(this, TrafficManagerActivity.class));
                break;
            case 4:
                startActivity(new Intent(this, ProcessManagerActivity.class));
                break;
            case 5:
                startActivity(new Intent(this, AntivirusActivity_.class));
                break;
            case 6:
                startActivity(new Intent(this, CacheClearActivity.class));
                break;
            case 7:
                startActivity(new Intent(this, AToolActivity.class));
                break;
            case 8:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }

    /**
     * 显示设置或登陆的dialog
     * 是否保存了密码: PASSWORD
     */
    private void showSetOrLoginDialog() {
        //password = sp.getString("PASSWORD", null);
        password = SPUtils.getInstance(MainActivity.this).getValue(SPUtils.PASSWORD, null);
        //1. 取出sp中保存密码
        if (password == null) {
            //2. 如果没有, 显示设置dialog
            showSetDialog();
        } else {
            //3. 否则显示登陆dialog
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
        btn_set_dialog_confirm.setOnClickListener(this);
        btn_set_dialog_cancel.setOnClickListener(this);
        dialog = new AlertDialog.Builder(this).setView(view).show();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_set_dialog_confirm) {
            //1. 对输入的密码进行检查, 如果不合法, Toast提示
            if ("".equals(et_set_dialog_pwd.getText().toString().trim())) {
                MSUtils.showMsg(this, "必须指定密码");
                return;
            }
            if (!(et_set_dialog_pwd.getText().toString().trim()).equals(et_set_dialog_pwd2.getText().toString().trim())) {
                MSUtils.showMsg(this, "两次密码不一致");
                return;
            }
            //2. 如果合法, 保存密码, 移除dialog
            //sp.edit().putString("PASSWORD", MSUtils.md5(et_set_dialog_pwd.getText().toString())).commit();
            SPUtils.getInstance(MainActivity.this).save(SPUtils.PASSWORD, MSUtils.md5(et_set_dialog_pwd.getText().toString()));
            dialog.dismiss();
        } else if (v == btn_set_dialog_cancel) {
            dialog.dismiss();
        } else if (v == btn_login_dialog_cancel) {
            dialog.dismiss();
        } else if (v == btn_login_dialog_confirm) {
            //1. 检查密码是否正确
            String pwd = et_login_dialog_pwd.getText().toString();
            //2. 如果不正确, toast提示
            if (!MSUtils.md5(pwd).equals(password)) {
                MSUtils.showMsg(this, "密码不正确");
            } else {
                //3. 否则, 进入防盗流程
                dialog.dismiss();
                toProtect();
            }
        }
    }

    /**
     * 进入防盗流程
     */
    private void toProtect() {
    //1. 从sp中获取防盗配置是否完成 key=configure
        Boolean configure = SPUtils.getInstance(this).getValue(SPUtils.CONFIGURE, false);
        if(!configure) {
    //2. 如果没有, 进入设置向导1
        startActivity(new Intent(MainActivity.this, Setup1Activity.class));
        } else {
            //3. 否则进入防盗信息界面
            startActivity(new Intent(MainActivity.this, ProtectinfoActivity.class));
        }
    }

    private boolean exit = false;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!exit) {
                exit = true;
                MSUtils.showMsg(this, "请再次点击返回键退出");
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
