package com.nibonn.lovepinche;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;
import com.nibonn.model.PincheRecord;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private static final int STATUS_ING = 0;
    private static final int STATUS_SUCCESS = 1;
    private static final int STATUS_FAIL = 2;

    private static final int REQUIRE_START_ADDRESS = 0;
    private static final int REQUIRE_ARRIVE_ADDRESS = 1;
    private static final int NO_REQUIRE = 2;

    private TabHost tabHost;
    private TabHost.TabSpec findCarTab;
    private TabHost.TabSpec recordTab;
    private TabHost.TabSpec settingTab;
    private TabHost.TabSpec chatTab;
    private TabHost.TabSpec frequentAddressTab;
    private TabHost.TabSpec[] currentTab;
    private LinearLayout recordLayout;
    private LinearLayout frequentAddressLayout;

    private EditText startAddressView;
    private EditText arriveAddressView;
    private EditText startDateView;
    private EditText startTimeView;
    private EditText endDateView;
    private EditText endTimeView;
    private RadioGroup manGroup;
    private RadioButton defaultMan;

    private TextView chatTitle;
    private TextView chatContent;

    private Map<View, PincheRecord> records;
    private int frequentAddressEntrance;
    private int requireAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        Bundle userData = getIntent().getExtras();
        records = new HashMap<View, PincheRecord>();
        initTabs();
        initRecordLayout();
        initFrequentAddressLayout();
        findNeededView();
        resetFindCar();
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
        switch (v.getId()) {
            // find car tab
            case R.id.submitBtn:
                // TODO submit request
                break;
            case R.id.resetAllBtn:
                resetFindCar();
                break;
            case R.id.frequentStartAddressBtn:
                requireAddress = REQUIRE_START_ADDRESS;
                enterFrequentAddressTab(0);
                break;
            case R.id.frequentArriveAddressBtn:
                requireAddress = REQUIRE_ARRIVE_ADDRESS;
                enterFrequentAddressTab(0);
                break;

            // record tab
            case R.id.chatBtn:
                enterChatTab(records.get(v.getParent()));
                break;
            case R.id.statusBtn:
                Toast.makeText(this, records.get(v.getParent()).toString(), Toast.LENGTH_LONG).show();
                break;

            // chat tab
            case R.id.sendMesBtn:
                // TODO send message
                break;
            case R.id.returnChatBtn:
                returnFromChat();
                break;

            // setting tab
            case R.id.frequentAddressSettingBtn:
                enterFrequentAddressTab(2);
                break;
            case R.id.exitSettingBtn:
                finish();
                break;

            // frequent address tab
            case R.id.returnFrequentAddressBtn:
                requireAddress = NO_REQUIRE;
                returnFromFrequentAddress();
                break;
            case R.id.frequentAddressBtn:
                if (requireAddress == NO_REQUIRE) {
                    break;
                }
                CharSequence addressResult = ((Button) v).getText();
                switch (requireAddress) {
                    case REQUIRE_START_ADDRESS:
                        startAddressView.setText(addressResult);
                        requireAddress = NO_REQUIRE;
                        break;
                    case REQUIRE_ARRIVE_ADDRESS:
                        arriveAddressView.setText(addressResult);
                        requireAddress = NO_REQUIRE;
                        break;
                }
                returnFromFrequentAddress();
                break;

            default:
        }
    }

    private void initTabs() {
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();


        View indicator = LayoutInflater.from(this).inflate(R.layout.tab_layout, null);
        ImageView tabIcon = (ImageView) indicator.findViewById(R.id.tab_icon);
        tabIcon.setImageResource(R.drawable.find);
        TextView tabName = (TextView) indicator.findViewById(R.id.tab_name);
        tabName.setText("我要拼车");

        findCarTab = tabHost.newTabSpec("scroll_find_car");
        findCarTab.setIndicator(indicator);
        findCarTab.setContent(R.id.find_car_tab);

        indicator = LayoutInflater.from(this).inflate(R.layout.tab_layout, null);
        tabIcon = (ImageView) indicator.findViewById(R.id.tab_icon);
        tabIcon.setImageResource(R.drawable.record);
        tabName = (TextView) indicator.findViewById(R.id.tab_name);
        tabName.setText("拼车记录");
        recordTab = tabHost.newTabSpec("record");
        recordTab.setIndicator(indicator);
        recordTab.setContent(R.id.scroll_record_tab);

        chatTab = tabHost.newTabSpec("chat");
        chatTab.setIndicator(indicator);
        chatTab.setContent(R.id.chat_tab);

        indicator = LayoutInflater.from(this).inflate(R.layout.tab_layout, null);
        tabIcon = (ImageView) indicator.findViewById(R.id.tab_icon);
        tabIcon.setImageResource(R.drawable.setting);
        tabName = (TextView) indicator.findViewById(R.id.tab_name);
        tabName.setText("设置");
        settingTab = tabHost.newTabSpec("setting");
        settingTab.setIndicator(indicator);
        settingTab.setContent(R.id.setting_tab);

        frequentAddressTab = tabHost.newTabSpec("frequent_address_tab");
        frequentAddressTab.setIndicator(indicator);
        frequentAddressTab.setContent(R.id.frequent_address_tab);

        currentTab = new TabHost.TabSpec[3];
        currentTab[0] = findCarTab;
        currentTab[1] = recordTab;
        currentTab[2] = settingTab;

        tabHost.addTab(currentTab[0]);
        tabHost.addTab(currentTab[1]);
        tabHost.addTab(currentTab[2]);
    }

    private void setCurrentTab(int index, TabHost.TabSpec tab, int returnTab) {
        currentTab[index] = tab;
        tabHost.clearAllTabs();
        tabHost.addTab(currentTab[0]);
        tabHost.addTab(currentTab[1]);
        tabHost.addTab(currentTab[2]);
        tabHost.setCurrentTab(returnTab);
    }

    private void initRecordLayout() {
        recordLayout = (LinearLayout) findViewById(R.id.record_tab);
        addRecord("ing route", STATUS_ING);
        addRecord("success route", STATUS_SUCCESS);
        addRecord("fail route", STATUS_FAIL);
        for (int i = 0; i < 15; ++i) {
            addRecord("record " + i, STATUS_ING);
        }
    }

    private void initFrequentAddressLayout() {
        frequentAddressLayout = (LinearLayout) findViewById(R.id.frequent_address_layout);
        addFrequentAddress("address1");
        addFrequentAddress("address2");
        addFrequentAddress("address3");
    }

    private void findNeededView() {
        startAddressView = (EditText) findViewById(R.id.startAddressView);
        arriveAddressView = (EditText) findViewById(R.id.arriveAddressView);
        startDateView = (EditText) findViewById(R.id.beginDateView);
        startTimeView = (EditText) findViewById(R.id.beginTimeView);
        endDateView = (EditText) findViewById(R.id.endDateView);
        endTimeView = (EditText) findViewById(R.id.endTimeView);
        manGroup = (RadioGroup) findViewById(R.id.manRadioGroup);
        defaultMan = (RadioButton) findViewById(R.id.oneManRadioBtn);

        chatTitle = (TextView) findViewById(R.id.chatTitleTextView);
        chatContent = (TextView) findViewById(R.id.chatContentTextView);
    }

    // TODO format args
    private void addRecord(String route, int status) {
        View record = LayoutInflater.from(this).inflate(R.layout.record_layout, null);
        ((TextView) record.findViewById(R.id.routeTextView)).setText(route);
        TextView statusView = (TextView) record.findViewById(R.id.statusTextView);
        switch (status) {
            case STATUS_ING:
                statusView.setBackgroundDrawable(getResources().getDrawable(R.drawable.status_ing));
                statusView.setText("匹配中");
                break;
            case STATUS_SUCCESS:
                statusView.setBackgroundDrawable(getResources().getDrawable(R.drawable.status_success));
                statusView.setText("匹配成功");
                break;
            case STATUS_FAIL:
                statusView.setBackgroundDrawable(getResources().getDrawable(R.drawable.status_fail));
                statusView.setText("匹配失败");
                break;
            default:
        }
        // TODO create record
        PincheRecord pincheRecord = new PincheRecord();
        pincheRecord.setOtherUser(route);
        records.put(record, pincheRecord);
        recordLayout.addView(record);
        // TODO save to database
    }

    private void addFrequentAddress(String address) {
        View addressLayout = LayoutInflater.from(this).inflate(R.layout.address_layout, null);
        Button addressText = (Button) addressLayout.findViewById(R.id.frequentAddressBtn);
        addressText.setText(address);
        frequentAddressLayout.addView(addressLayout);
        // TODO save to database
    }

    private void resetFindCar() {
        startAddressView.setText("");
        arriveAddressView.setText("");
        startDateView.setText("");
        startTimeView.setText("");
        endDateView.setText("");
        endTimeView.setText("");
        defaultMan.setChecked(true);
    }

    private void enterChatTab(PincheRecord record) {
        chatTitle.setText(String.format("正在与正在和用户%s交流", record.getOtherUser()));
        chatContent.setText("");
        setCurrentTab(1, chatTab, 1);
    }

    private void returnFromChat() {
        setCurrentTab(1, recordTab, 1);
    }


    private void enterFrequentAddressTab(int fromTab) {
        frequentAddressEntrance = fromTab;
        setCurrentTab(2, frequentAddressTab, 2);
    }

    private void returnFromFrequentAddress() {
        setCurrentTab(2, settingTab, frequentAddressEntrance);
    }
}
