package com.nibonn.lovepinche;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.nibonn.util.HttpUtils;

import java.io.IOException;

public class SignUpActivity extends ActionBarActivity {

    private EditText unSignUpView;
    private EditText pwSignUpView;
    private EditText pwConfirmSignUpView;
    private EditText trueNameView;
    private EditText idNumView;
    private EditText phoneNumView;

    private Handler handler;

    private static final int NETWORK_ERROR = 0;
    private static final int DUP_USERNAME = 1;
    private static final int VALID_USERNAME = 2;
    private static final int SIGN_UP_SUCCESS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        unSignUpView = (EditText) findViewById(R.id.unSignUpView);
        pwSignUpView = (EditText) findViewById(R.id.pwSignUpView);
        pwConfirmSignUpView = (EditText) findViewById(R.id.pwConfirmSignUpView);
        trueNameView = (EditText) findViewById(R.id.trueNameSignUpView);
        idNumView = (EditText) findViewById(R.id.idSignUpView);
        phoneNumView = (EditText) findViewById(R.id.phoneNumSignUpView);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case NETWORK_ERROR:
                        Toast.makeText(SignUpActivity.this, "fail to connect.", Toast.LENGTH_SHORT).show();
                        break;
                    case DUP_USERNAME:
                        Toast.makeText(SignUpActivity.this, "username has already been used.", Toast.LENGTH_SHORT).show();
                        break;
                    case VALID_USERNAME:
                        Toast.makeText(SignUpActivity.this, "username has not been used.", Toast.LENGTH_SHORT).show();
                        break;
                    case SIGN_UP_SUCCESS:
                        Toast.makeText(SignUpActivity.this, "sign up succeed, please login.", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    default:
                }
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.sign_up, menu);
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
        final String un = unSignUpView.getText().toString();
        switch (v.getId()) {
            case R.id.unCheckSignUpBtn:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        isValidUsername(un);
                    }
                }).start();
                break;
            case R.id.uploadSignUpBtn:
                final String pw = pwSignUpView.getText().toString();
                if (!pw.equals(pwConfirmSignUpView.getText().toString())) {
                    Toast.makeText(this, "two passwords are not same!", Toast.LENGTH_SHORT).show();
                    break;
                }
                final String realname = trueNameView.getText().toString();
                final String idcard = idNumView.getText().toString();
                final String phonenum = phoneNumView.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                    signUp(un, pw, realname, idcard, phonenum);
                    }
                }).start();
                break;
            case R.id.quitSignUpBtn:
                finish();
                break;
            default:
                Toast.makeText(this, "undefined operation with id " + v.getId(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidUsername(String un) {
        try {
            String res = HttpUtils.post(getString(R.string.check_username_url), String.format("username=%s", un));
            if (res.equals("1")) {
                handler.sendEmptyMessage(VALID_USERNAME);
                return true;
            } else {
                handler.sendEmptyMessage(DUP_USERNAME);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(NETWORK_ERROR);
            return false;
        }
    }

    private void signUp(String un, String pw, String realname, String id, String phone) {
        if (!isValidUsername(un)) {
            return;
        }
        // TODO sign up
        try {
            String res = HttpUtils.post(getString(R.string.sign_up_url),
                    String.format("username=%s&password=%s&realname=%s&idcard=%s&phonenumber=%s", un, pw, realname, id, phone));
        } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(NETWORK_ERROR);
        }
    }
}
