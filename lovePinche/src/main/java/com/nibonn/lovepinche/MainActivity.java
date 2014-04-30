package com.nibonn.lovepinche;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private TabHost.TabSpec findCarTab;
    private TabHost.TabSpec recordTab;
    private TabHost.TabSpec settingTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        Bundle data = getIntent().getExtras();

        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        View indicator = LayoutInflater.from(this).inflate(R.layout.tab_layout, null);
        ImageView tabIcon = (ImageView) indicator.findViewById(R.id.tab_icon);
        tabIcon.setImageResource(R.drawable.find);
        TextView tabName = (TextView) indicator.findViewById(R.id.tab_name);
        tabName.setText("我要拼车");

        findCarTab = tabHost.newTabSpec("scroll_find_car");
        findCarTab.setIndicator(indicator);
        findCarTab.setContent(R.id.find_car_tab);
        tabHost.addTab(findCarTab);

        indicator = LayoutInflater.from(this).inflate(R.layout.tab_layout, null);
        tabIcon = (ImageView) indicator.findViewById(R.id.tab_icon);
        tabIcon.setImageResource(R.drawable.record);
        tabName = (TextView) indicator.findViewById(R.id.tab_name);
        tabName.setText("拼车记录");
        recordTab = tabHost.newTabSpec("record");
        recordTab.setIndicator(indicator);
        recordTab.setContent(R.id.record_tab);
        tabHost.addTab(recordTab);

        indicator = LayoutInflater.from(this).inflate(R.layout.tab_layout, null);
        tabIcon = (ImageView) indicator.findViewById(R.id.tab_icon);
        tabIcon.setImageResource(R.drawable.setting);
        tabName = (TextView) indicator.findViewById(R.id.tab_name);
        tabName.setText("设置");
        settingTab = tabHost.newTabSpec("setting");
        settingTab.setIndicator(indicator);
        settingTab.setContent(R.id.setting_tab);
        tabHost.addTab(settingTab);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
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
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
    }
}
