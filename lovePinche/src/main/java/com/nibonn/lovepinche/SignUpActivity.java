package com.nibonn.lovepinche;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class SignUpActivity extends ActionBarActivity {

    private EditText unSignUpView;
    private EditText pwSignUpView;
    private EditText pwConfirmSignUpView;
    private EditText trueNameView;
    private EditText idNumView;
    private EditText phoneNumView;

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
        switch (v.getId()) {
            case R.id.unCheckSignUpBtn:
                checkUsername();
                break;
            case R.id.uploadSignUpBtn:
                signUp();
                break;
            case R.id.quitSignUpBtn:
                finish();
                break;
            default:
                Toast.makeText(this, "undefined operation with id " + v.getId(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUsername() {
        // TODO check username
    }

    private void signUp() {
        // TODO sign up
    }
}
