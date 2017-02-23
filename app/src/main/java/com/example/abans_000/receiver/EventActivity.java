package com.example.abans_000.receiver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        long startTime = intent.getLongExtra("stime", System.currentTimeMillis());
        long finishTime = intent.getLongExtra("ftime", System.currentTimeMillis());
        String description = intent.getStringExtra("description");

        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM hh:mm aaa");

        TextView titleTextView, startTextView, finishTextView, descTextView;
        titleTextView = (TextView) findViewById(R.id.event_title_text);
        startTextView = (TextView) findViewById(R.id.event_start_text);
        finishTextView = (TextView) findViewById(R.id.event_finish_text);
        descTextView = (TextView) findViewById(R.id.event_description_text);

        titleTextView.setText(title);
        startTextView.setText(formatter.format(new Date(startTime)));
        finishTextView.setText(formatter.format(new Date(finishTime)));
        descTextView.setText(description);

    }
}
