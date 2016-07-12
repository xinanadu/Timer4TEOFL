package com.symbio.android.webview.timer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmMgr;
    Intent intentAlarmReceiver = null;
    private PendingIntent pendingIntentAlarm;

    private SharedPreferences prefs;

    CompoundButton.OnCheckedChangeListener checkListner = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String prefsName = "NIGHT";

            int hour = 0;
            if (buttonView.getId() == R.id.ckbAlarmMorning) {
                hour = 7;
                prefsName = "MORNING";
            }

            if (isChecked) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.add(Calendar.DAY_OF_YEAR, 1);

                intentAlarmReceiver.putExtra("flag", prefsName);
                pendingIntentAlarm = PendingIntent.getBroadcast(MainActivity.this, 0, intentAlarmReceiver, 0);
                alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntentAlarm);
                setReceiver(true);
                prefs.edit().putLong(prefsName, calendar.getTimeInMillis()).commit();
            } else {
                alarmMgr.cancel(pendingIntentAlarm);
                setReceiver(false);
                prefs.edit().remove(prefsName).commit();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intentAlarmReceiver = new Intent(MainActivity.this, AlarmReceiver.class);
        prefs = getSharedPreferences("alarm",
                Activity.MODE_PRIVATE);
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        CheckBox checkBoxAlarmMorning = (CheckBox) findViewById(R.id.ckbAlarmMorning);
        CheckBox checkBoxAlarmNight = (CheckBox) findViewById(R.id.ckbAlarmNight);
        TextView tvETA = (TextView) findViewById(R.id.tvETA);
        if (prefs.getLong("MORNING", 0) > 0) {
            checkBoxAlarmMorning.setChecked(true);
        }
        if (prefs.getLong("NIGHT", 0) > 0) {
            checkBoxAlarmNight.setChecked(true);
        }

        checkBoxAlarmMorning.setOnCheckedChangeListener(checkListner);
        checkBoxAlarmNight.setOnCheckedChangeListener(checkListner);

        final Calendar cal = Calendar.getInstance();
        int startDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        cal.set(2016, 7, 20);
        int inteval = cal.get(Calendar.DAY_OF_YEAR) - startDayOfYear;
        final String finalStr = "距离TOEFL(Aug,20)还有(天)\n";
        SpannableString spanString = new SpannableString(finalStr + inteval);
        AbsoluteSizeSpan spanSize = new AbsoluteSizeSpan(260);
        ForegroundColorSpan spanForeColor = new ForegroundColorSpan(Color.BLUE);
        spanString.setSpan(spanSize, finalStr.length(), spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(spanForeColor, finalStr.length(), spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvETA.setText(spanString);

        findViewById(R.id.btnTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.SECOND, 3);
                intentAlarmReceiver.putExtra("flag", "TEST");
                pendingIntentAlarm = PendingIntent.getBroadcast(MainActivity.this, 0, intentAlarmReceiver, 0);
                alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntentAlarm);
                setReceiver(true);
            }
        });
    }

    private void setReceiver(boolean enabled) {
        ComponentName receiver = new ComponentName(this, AlarmReceiver.class);
        PackageManager pm = getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
