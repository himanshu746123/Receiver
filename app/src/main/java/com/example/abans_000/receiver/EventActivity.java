package com.example.abans_000.receiver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abans_000.receiver.services.ScheduleClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventActivity extends AppCompatActivity {

    private ScheduleClient mScheduleClient;

    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        mScheduleClient = new ScheduleClient(this);
        mScheduleClient.doBindService();

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        startTime = intent.getLongExtra("stime", System.currentTimeMillis());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_reminder:
                // Create a new calendar set to the date chosen
                // we set the time to midnight (i.e. the first minute of that day)
                Calendar d = Calendar.getInstance();
                d.setTimeInMillis(startTime);

                // Ask our service to set an alarm for that date, this activity talks to the client that talks to the service
                mScheduleClient.setAlarmForNotification(d);
                // Notify the user what they just did
                Toast.makeText(getApplicationContext(), "Reminder set successfull", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        // When our activity is stopped ensure we also stop the connection to the service
        // this stops us leaking our activity into the system *bad*
        if(mScheduleClient != null)
            mScheduleClient.doUnbindService();
        super.onStop();
    }
}
