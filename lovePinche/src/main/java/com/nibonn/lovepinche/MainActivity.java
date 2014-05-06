package com.nibonn.lovepinche;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nibonn.model.*;
import com.nibonn.util.HttpUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends ActionBarActivity {

    private static final int STATUS_ING = 0;
    private static final int STATUS_SUCCESS = 1;
    private static final int STATUS_FAIL = 2;

    private static final int REQUIRE_START_ADDRESS = 0;
    private static final int REQUIRE_ARRIVE_ADDRESS = 1;
    private static final int NO_REQUIRE = 2;

    private static final int NETWORK_ERROR = -1;
    private static final int CHANGE_INFO_SUCCESS = 0;
    private static final int CHANGE_INFO_FAIL = 1;
    private static final int CHANGE_PW_SUCCESS = 2;
    private static final int CHANGE_PW_FAIL = 3;
    private static final int MATCH_SUCCESS = 4;
    private static final int SEND_MSG_SUCCESS = 5;
    private static final int SEND_MSG_FAIL = 6;
    private static final int NEW_MESSAGE = 7;
    private static final int SUBMIT_SUCCESS = 8;
    private static final int INSTANT_MSG = 9;

    private TabHost tabHost;
    private TabHost.TabSpec findCarTab;
    private TabHost.TabSpec recordTab;
    private TabHost.TabSpec settingTab;
    private TabHost.TabSpec chatTab;
    private TabHost.TabSpec frequentAddressTab;
    private TabHost.TabSpec[] currentTab;
    private LinearLayout recordLayout;
    private LinearLayout frequentAddressLayout;

    private TextView userTitle;

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
    private EditText toSendMsgView;

    private User self;
    private String otherId;
    private SQLiteDatabase db;
    private Handler handler;
    private Map<View, PincheRecord> records;
    private Map<String, List<UserMessage>> messages;
    private int frequentAddressEntrance;
    private int requireAddress;
    private SimpleDateFormat dateFormat;
    private ExecutorService pool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        self = new User(getIntent().getExtras());
        userTitle = (TextView) findViewById(R.id.user_title);
        userTitle.setText("Hi//" + self.getUsername());
        initHandler();
        initDB();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        records = new HashMap<View, PincheRecord>();
        messages = new HashMap<String, List<UserMessage>>();
        pool = Executors.newCachedThreadPool();
        initTabs();
        initRecordLayout();
        initFrequentAddressLayout();
        findNeededView();
        resetFindCar();
        startReceiveMsg();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            // find car tab
            case R.id.submitBtn:
                String startAddr = startAddressView.getText().toString();
                String arriveAddr = arriveAddressView.getText().toString();
                String startDate = startDateView.getText().toString();
                String startTime = startTimeView.getText().toString();
                String endDate = endDateView.getText().toString();
                String endTime = endTimeView.getText().toString();
                int num;
                switch (manGroup.getCheckedRadioButtonId()) {
                    case R.id.oneManRadioBtn:
                        num = 1;
                        break;
                    case R.id.twoManRadioBtn:
                        num = 2;
                        break;
                    case R.id.threeManRadioBtn:
                        num = 3;
                        break;
                    default:
                        num = 1;
                }
                Toast.makeText(this, "connecting...", Toast.LENGTH_SHORT).show();
                submitRequest(startAddr, arriveAddr, startDate, startTime, endDate, endTime, num);
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
                Toast.makeText(this, records.get(v.getParent()).info(), Toast.LENGTH_LONG).show();
                break;

            // chat tab
            case R.id.sendMesBtn:
                sendMessage(toSendMsgView.getText().toString());
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
            case R.id.changeInformationBtn:
                Toast.makeText(this, "connecting...", Toast.LENGTH_SHORT).show();
                changeInformation();
                break;
            case R.id.changePWBtn:
                Toast.makeText(this, "connecting...", Toast.LENGTH_SHORT).show();
                changePassword();
                break;
            case R.id.helpSettingBtn:
                showMessage("有问题请联系我们", "爱拼车小组/手机：18817554328；email:387775245@qq.com");
                break;
            case R.id.aboutSettingBtn:
                showMessage("关于", "爱拼车小组 版权所有/版本：1.0/All Rights Reserved.");
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
            case R.id.addFrequentAddressBtn:
                showAddFrequentAddress();
                break;

            default:
        }
    }

    private void initDB() {
        db = openOrCreateDatabase("pinche.db", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS address ( address VARCHAR(255) NOT NULL, PRIMARY KEY (address) )");
        db.execSQL("CREATE TABLE IF NOT EXISTS record ( otherUser VARCHAR(63), startAddr VARCHAR(255) NOT NULL," +
                "arriveAddr VARCHAR(255) NOT NULL, startTime VARCHAR(31), endTime VARCHAR(31), otherUserId VARCHAR(15) , status INT )");
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
        Cursor c = db.rawQuery("SELECT * FROM record", new String[0]);
        if (!c.moveToFirst()) {
            c.close();
            return;
        }
        do {
            PincheRecord record = new PincheRecord();
            record.setEndTime(c.getString(c.getColumnIndex("endTime")));
            record.setArriveAddress(c.getString(c.getColumnIndex("arriveAddr")));
            record.setStartTime(c.getString(c.getColumnIndex("startTime")));
            record.setStartAddress(c.getString(c.getColumnIndex("startAddr")));
            record.setOtherUser(c.getString(c.getColumnIndex("otherUser")));
            record.setOtherUserId(c.getString(c.getColumnIndex("otherUserId")));
            addRecord(record, c.getInt(c.getColumnIndex("status")), false);
        } while (c.moveToNext());
        c.close();
    }

    private void initFrequentAddressLayout() {
        requireAddress = NO_REQUIRE;
        frequentAddressLayout = (LinearLayout) findViewById(R.id.frequent_address_layout);
        Cursor c = db.rawQuery("SELECT * FROM address", new String[0]);
        if (!c.moveToFirst()) {
            c.close();
            return;
        }
        do {
            addFrequentAddress(c.getString(0), false);
        } while (c.moveToNext());
        c.close();
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CHANGE_INFO_SUCCESS:
                        Toast.makeText(MainActivity.this, "修改信息成功", Toast.LENGTH_SHORT).show();
                        break;
                    case CHANGE_INFO_FAIL:
                        Toast.makeText(MainActivity.this, "修改信息失败", Toast.LENGTH_SHORT).show();
                        break;
                    case CHANGE_PW_SUCCESS:
                        Toast.makeText(MainActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                        break;
                    case CHANGE_PW_FAIL:
                        Toast.makeText(MainActivity.this, "修改密码失败", Toast.LENGTH_SHORT).show();
                        break;
                    case MATCH_SUCCESS:
                        Toast.makeText(MainActivity.this, "匹配成功", Toast.LENGTH_SHORT).show();
                        PincheRecord record = new PincheRecord();
                        record.fromBundle(msg.getData());
                        for (View v : records.keySet()) {
                            if (records.get(v).equals(record)) {
                                updateRecord(record, v, STATUS_SUCCESS);
                                break;
                            }
                        }
                        break;
                    case SEND_MSG_SUCCESS:
                        Bundle data = msg.getData();
                        chatContent.append(String.format("我 %s\n\t%s\n\n", data.getString("time"), data.getString("msg")));
                        toSendMsgView.setText("");
                        break;
                    case INSTANT_MSG:
                        data = msg.getData();
                        chatContent.append(String.format("%s %s\n\t%s\n\n", data.getString("id"), data.getString("time"), data.getString("msg")));
                        break;
                    case SEND_MSG_FAIL:
                        Toast.makeText(MainActivity.this, "发送信息失败", Toast.LENGTH_SHORT).show();
                        break;
                    case NEW_MESSAGE:
                        Toast.makeText(MainActivity.this, "新消息", Toast.LENGTH_SHORT).show();
                        break;
                    case NETWORK_ERROR:
                        Toast.makeText(MainActivity.this, "fail to connect", Toast.LENGTH_SHORT).show();
                        break;
                    case SUBMIT_SUCCESS:
                        record = new PincheRecord();
                        record.fromBundle(msg.getData());
                        addRecord(record, STATUS_ING, true);
                        findMatch(msg.getData().getString("srcid"), msg.getData().getString("desid"), record.getStartTime(),
                                record.getEndTime(), msg.getData().getInt("man"), record);
                        break;
                    default:
                }
            }
        };
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
        toSendMsgView = (EditText) findViewById(R.id.toSendMsgView);
    }

    private View addRecord(PincheRecord pincheRecord, int status, boolean addToDB) {
        View record = LayoutInflater.from(this).inflate(R.layout.record_layout, null);
        ((TextView) record.findViewById(R.id.routeTextView)).setText(pincheRecord.toString());
        TextView statusView = (TextView) record.findViewById(R.id.statusTextView);
        switch (status) {
            case STATUS_ING:
                statusView.setBackgroundDrawable(getResources().getDrawable(R.drawable.status_ing));
                statusView.setText("匹配中");
                break;
            case STATUS_SUCCESS:
                statusView.setBackgroundDrawable(getResources().getDrawable(R.drawable.status_success));
                statusView.setText("已匹配");
                break;
            case STATUS_FAIL:
                statusView.setBackgroundDrawable(getResources().getDrawable(R.drawable.status_fail));
                statusView.setText("已超时");
                break;
            default:
        }
        records.put(record, pincheRecord);
        recordLayout.addView(record);
        if (addToDB) {
            db.execSQL("INSERT INTO record (otherUser , startAddr, arriveAddr, startTime, endTime, otherUserId, status) " +
                            "VALUES ( ?, ?, ?, ?, ?, ?, ? )",
                    new Object[]{pincheRecord.getOtherUser(), pincheRecord.getStartAddress(), pincheRecord.getArriveAddress(),
                            pincheRecord.getStartTime(), pincheRecord.getEndTime(), pincheRecord.getOtherUserId(), status}
            );
        }
        return record;
    }

    private void updateRecord(PincheRecord record, View recordView, int status) {
        TextView statusView = (TextView) recordView.findViewById(R.id.statusTextView);
        switch (status) {
            case STATUS_ING:
                statusView.setBackgroundDrawable(getResources().getDrawable(R.drawable.status_ing));
                statusView.setText("匹配中");
                break;
            case STATUS_SUCCESS:
                statusView.setBackgroundDrawable(getResources().getDrawable(R.drawable.status_success));
                statusView.setText("已匹配");
                break;
            case STATUS_FAIL:
                statusView.setBackgroundDrawable(getResources().getDrawable(R.drawable.status_fail));
                statusView.setText("已超时");
                break;
            default:
        }
        db.execSQL("UPDATE record SET status = ?, otherUserId = ?, otherUser = ? WHERE startAddr = ? AND arriveAddr = ? AND " +
                        "startTime = ? AND endTime = ?",
                new Object[]{status, record.getOtherUserId(), record.getOtherUser(), record.getStartAddress(), record.getArriveAddress(),
                        record.getStartTime(), record.getEndTime()}
        );
    }

    private void addFrequentAddress(String address, boolean addToDB) {
        View addressLayout = LayoutInflater.from(this).inflate(R.layout.address_layout, null);
        Button addressText = (Button) addressLayout.findViewById(R.id.frequentAddressBtn);
        addressText.setText(address);
        frequentAddressLayout.addView(addressLayout);
        if (addToDB) {
            db.execSQL("INSERT INTO address VALUES ( ? )", new Object[]{address});
        }
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
        chatTitle.setText(String.format("正在和用户%s交流", record.getOtherUser()));
        chatContent.setText("");
        otherId = record.getOtherUserId();
        List<UserMessage> userMessages = messages.get(otherId);
        if (userMessages != null) {
            for (UserMessage um : userMessages) {
                chatContent.append(String.format("%s %s\n\t%s\n\n", otherId, um.getDate(), um.getMessage()));
            }
            userMessages.clear();
        }
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

    private void changeInformation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("手机号码");
        final EditText view = new EditText(this);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String newPhoneNum = view.getText().toString();
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String res = HttpUtils.post(getString(R.string.change_info_url),
                                    String.format("username=%s&password=%s&phonenumber=%s", self.getUsername(), self.getPassword(), newPhoneNum));
                            if (res.equals("oknull")) {
                                handler.sendEmptyMessage(CHANGE_INFO_SUCCESS);
                            } else {
                                handler.sendEmptyMessage(CHANGE_INFO_FAIL);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(NETWORK_ERROR);
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void changePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("密码");
        final EditText view = new EditText(this);
        view.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String newPw = view.getText().toString();
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String res = HttpUtils.post(getString(R.string.change_info_url),
                                    String.format("username=%s&password=%s&phonenumber=%s", self.getUsername(), newPw, self.getPhonenumber()));
                            if (res.equals("oknull")) {
                                handler.sendEmptyMessage(CHANGE_PW_SUCCESS);
                            } else {
                                handler.sendEmptyMessage(CHANGE_PW_FAIL);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(NETWORK_ERROR);
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showAddFrequentAddress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("新地址");
        final EditText view = new EditText(this);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addFrequentAddress(view.getText().toString(), true);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void submitRequest(final String startAddr, final String arriveAddr,
                               final String startDate, final String startTime,
                               final String endDate, final String endTime, final int man) {
        final String postData = String.format("src=%s&des=%s&userid=%s&peoplenumber=%d&time=%s %s&lasttime=%s %s",
                startAddr, arriveAddr, self.getUserid(), man, startDate, startTime, endDate, endTime);
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("submit: " + postData);
                    String res = HttpUtils.post(getString(R.string.submit_url), postData);
                    if (res.startsWith("<pre>")) {
                        res = res.substring(5);
                    }
                    if (res.endsWith("</pre>")) {
                        res = res.substring(0, res.length() - 6);
                    }
                    Gson gson = new Gson();
                    SubmitResult submitResult = gson.fromJson(res, SubmitResult.class);
                    final PincheRecord record = new PincheRecord();
                    record.fromSubmitResult(submitResult);
                    Bundle data = new Bundle();
                    data.putString("startAddr", record.getStartAddress());
                    data.putString("arriveAddr", record.getArriveAddress());
                    data.putString("startTime", record.getStartTime());
                    data.putString("endTime", record.getEndTime());
                    data.putString("otherUser", record.getOtherUser());
                    data.putString("otherUserId", record.getOtherUserId());
                    data.putString("srcid", submitResult.getSrcid());
                    data.putString("desid", submitResult.getDesid());
                    data.putInt("man", man);
                    Message msg = new Message();
                    msg.what = SUBMIT_SUCCESS;
                    msg.setData(data);
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(NETWORK_ERROR);
                }
            }
        });
    }

    private void findMatch(String startId, String arriveId,
                           String startTime, String endTime, int man, final PincheRecord record) {
        final String postData = String.format("srcid=%s&desid=%s&userid=%s&peoplenumber=%d&time=%s&lasttime=%s",
                startId, arriveId, self.getUserid(), man, startTime, endTime);
        pool.execute(new Runnable() {
            @Override
            public void run() {
                boolean findMatch = false;
                while (!findMatch) {
                    try {
                        System.out.println("match post: " + postData);
                        String res = HttpUtils.post(getString(R.string.match_url), postData);
                        System.out.println("match: " + res);
                        if (res.equals("0")) {
                            Thread.sleep(5000);
                            continue;
                        }
                        Gson gson = new Gson();
                        List<MatchResult> matchResults = gson.fromJson(res, new TypeToken<List<MatchResult>>() {
                        }.getType());
                        if (matchResults.size() <= 0) {
                            Thread.sleep(5000);
                            continue;
                        }
                        record.setOtherUserId(matchResults.get(0).getUserid());
                        findMatch = true;
                        Message msg = new Message();
                        msg.what = MATCH_SUCCESS;
                        Bundle data = new Bundle();
                        data.putString("startAddr", record.getStartAddress());
                        data.putString("arriveAddr", record.getArriveAddress());
                        data.putString("startTime", record.getStartTime());
                        data.putString("endTime", record.getEndTime());
                        data.putString("otherUser", record.getOtherUser());
                        data.putString("otherUserId", record.getOtherUserId());
                        msg.setData(data);
                        handler.sendMessage(msg);
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void sendMessage(final String msg) {
        final String date = dateFormat.format(new Date());
        final String postData = String.format("srcid=%s&desid=%s&date=%s&message=%s", self.getUserid(), otherId, date, msg);
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String res = HttpUtils.post(getString(R.string.send_msg_url), postData);
                    if (res.equals(msg)) {
                        Message message = new Message();
                        message.what = SEND_MSG_SUCCESS;
                        Bundle data = new Bundle();
                        data.putString("msg", msg);
                        data.putString("time", date);
                        message.setData(data);
                        handler.sendMessage(message);
                    } else {
                        handler.sendEmptyMessage(SEND_MSG_FAIL);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(NETWORK_ERROR);
                }
            }
        });
    }

    private void startReceiveMsg() {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                String postData = String.format("desid=%s", self.getUserid());
                String charset = getString(R.string.charset);
                String postUrl = getString(R.string.receive_url);
                Gson gson = new Gson();
                while (true) {
                    try {
                        String res = HttpUtils.post(postUrl, postData, 5000, charset);
                        System.out.println("msg: " + res);
                        if (res.equals("0")) {
                            Thread.sleep(5000);
                            continue;
                        }
                        List<UserMessage> messages = gson.fromJson(res, new TypeToken<List<UserMessage>>() {
                        }.getType());
                        if (messages.size() <= 0) {
                            Thread.sleep(5000);
                            continue;
                        }
                        for (UserMessage um : messages) {
                            List<UserMessage> msgList = MainActivity.this.messages.get(um.getSrcid());
                            if (msgList == null) {
                                msgList = new LinkedList<UserMessage>();
                                MainActivity.this.messages.put(um.getSrcid(), msgList);
                            }
                            msgList.add(um);
                        }
                        if (tabHost.getCurrentTabTag().equals("chat")) {
                            List<UserMessage> ums = MainActivity.this.messages.get(otherId);
                            if (ums == null) {
                                continue;
                            }
                            for (UserMessage um : ums) {
                                Bundle data = new Bundle();
                                data.putString("id", otherId);
                                data.putString("time", um.getDate());
                                data.putString("msg", um.getMessage());
                                Message message = new Message();
                                message.setData(data);
                                message.what = INSTANT_MSG;
                                handler.sendMessage(message);
                            }
                            ums.clear();
                        } else {
                            handler.sendEmptyMessage(NEW_MESSAGE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void showMessage(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        final TextView view = new TextView(this);
        view.setText(msg);
        builder.setView(view);
        builder.setPositiveButton("ok", null);
        builder.show();
    }

}
