package com.nibonn.lovepinche;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nibonn.model.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class StartActivity extends ActionBarActivity {

    private EditText unLoginView;
    private EditText pwLoginView;

    private Handler handler;
    private final int LOGIN_SUCCESS = 0;
    private final int LOGIN_FAIL = 1;
    private final int NETWORK_ERROR = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case LOGIN_SUCCESS:
                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        intent.putExtras(msg.getData());
                        startActivity(intent);
                        break;
                    case LOGIN_FAIL:
                        Toast.makeText(StartActivity.this, "username or password wrong!", Toast.LENGTH_SHORT).show();
                        break;
                    case NETWORK_ERROR:
                        Toast.makeText(StartActivity.this, "fail to connect", Toast.LENGTH_SHORT).show();
                    default:
                }
            }
        };
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                View splash = findViewById(R.id.splash);
                splash.startAnimation(AnimationUtils.loadAnimation(StartActivity.this, R.anim.abc_slide_out_bottom));
                findViewById(R.id.login_layout).startAnimation(AnimationUtils.loadAnimation(StartActivity.this, R.anim.abc_slide_in_top));
                splash.setVisibility(View.GONE);
            }
        }, 2000);
        unLoginView = (EditText) findViewById(R.id.unLoginView);
        pwLoginView = (EditText) findViewById(R.id.pwLoginView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                final String un = unLoginView.getText().toString();
                final String pw = pwLoginView.getText().toString();
                Toast.makeText(this, "connecting...", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        login(un, pw);
                    }
                }).start();
                break;
            case R.id.signUpBtn:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, "undefined operation with id " + v.getId(), Toast.LENGTH_SHORT).show();
        }
    }

    private void login(String un, String pw) {
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) new URL(getString(R.string.login_url)).openConnection();
            conn.setRequestMethod("POST");
        } catch (IOException e) {
            handler.sendEmptyMessage(NETWORK_ERROR);
            e.printStackTrace();
            return;
        }
        conn.setDoOutput(true);
        conn.setReadTimeout(3000);
        BufferedReader in = null;
        OutputStream out = null;
        final String CHARSET = getString(R.string.charset);

        try {
            out = conn.getOutputStream();
            out.write(String.format("username=%s&password=%s", un, pw).getBytes(CHARSET));
        } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(NETWORK_ERROR);
            return;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }

        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), CHARSET));
            String res = in.readLine();
            if (res.equals("0")) {
                handler.sendEmptyMessage(LOGIN_FAIL);
                return;
            }
            Gson gson = new Gson();
            List<User> users = gson.fromJson(res, new TypeToken<List<User>>(){}.getType());
            User user = users.get(0);
            if (user != null && user.getUsername().equals(un) && user.getPassword().equals(pw)) {
                Message msg = new Message();
                msg.what = LOGIN_SUCCESS;
                Bundle data = new Bundle();
                data.putString("userid", user.getUserid());
                data.putString("username", user.getUsername());
                data.putString("password", user.getPassword());
                data.putString("realname", user.getRealname());
                data.putString("idcard", user.getIdcard());
                data.putString("phonenumber", user.getPhonenumber());
                msg.setData(data);
                handler.sendMessage(msg);
            } else {
                handler.sendEmptyMessage(LOGIN_FAIL);
            }
        } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(NETWORK_ERROR);
            return;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
