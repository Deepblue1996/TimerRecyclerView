package com.prohua.testtextviewrun;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.prohua.testtextviewrun.adapter.DefaultAdapter;
import com.prohua.testtextviewrun.adapter.DefaultViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DefaultAdapter defaultAdapter;
    private List<String> stringList;

    private List<DefaultViewHolder> defaultViewHolderList = new ArrayList<>();

    // 定时刷新界面
    private final static int REFRESH_UI = 1;
    private final static int REFRESH_UI_TIME = 1000;

    private Thread timeThread = new Thread(new TimeThread());

    @SuppressLint("SimpleDateFormat")
    public String refFormatNowDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_UI:
                    // 刷新列表
                    notifyAllData();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private class TimeThread implements Runnable {

        public void run() {
            // 满足条件,循环执行
            while (!Thread.currentThread().isInterrupted()) {

                // 向主线程执行刷新
                Message message = new Message();
                message.what = REFRESH_UI;

                // 发送一条刷新指令
                myHandler.sendMessage(message);
                try {
                    // 线程阻塞等待1s
                    Thread.sleep(REFRESH_UI_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void startTimer() {

        // 判断是否在进行
        if (!timeThread.isAlive()) {
            timeThread = new Thread(new TimeThread());
            // 继续运行
            timeThread.start();
        }
    }

    public void stopTimer() {
        timeThread.interrupt();
    }

    public void notifyAllData() {
        for (int i = 0; i < defaultViewHolderList.size(); i++) {
            ((TextView)(defaultViewHolderList.get(i).findViewById(R.id.text))).setText(refFormatNowDate());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stringList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            stringList.add(i+"");
        }

        recyclerView = findViewById(R.id.recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        defaultAdapter = new DefaultAdapter(this, stringList, R.layout.item);
        defaultAdapter.setOnBindItemView(new DefaultAdapter.OnBindItemView() {
            @Override
            public void onBindItemViewHolder(DefaultViewHolder holder, int position) {

                holder.text(R.id.text, refFormatNowDate());

                holder.setDataPosition(position);
                if(!(defaultViewHolderList.contains(holder))) {
                    defaultViewHolderList.add(holder);
                }
            }
        });

        recyclerView.setAdapter(defaultAdapter);

        startTimer();
    }
}
