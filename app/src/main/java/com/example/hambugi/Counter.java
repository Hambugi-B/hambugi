package com.example.hambugi;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Counter extends AppCompatActivity {

    private TextView txtMenubarTime;
    private Handler handler = new Handler();

    // 시작 시간: 08:00 (Calendar 객체로 관리)
    private Calendar virtualTime = Calendar.getInstance();

    // 10초마다 1분씩 증가시키는 Runnable
    private Runnable virtualClockTask = new Runnable() {
        @Override
        public void run() {
            // 시간 형식: "hh:mm a" (예: 08:05 AM)
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String currentTimeStr = sdf.format(virtualTime.getTime());

            // TextView에 시간 표시
            txtMenubarTime.setText(currentTimeStr + " :");

            // 종료 조건: 오후 5시 (17:00) 이후면 stop
            int hour = virtualTime.get(Calendar.HOUR_OF_DAY);
            int minute = virtualTime.get(Calendar.MINUTE);
            if (hour >= 17 && minute >= 0) {
                return;
            }

            // 1분 증가
            virtualTime.add(Calendar.MINUTE, 1);

            // 10초 후에 다시 실행
            handler.postDelayed(this, 10000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_cookingtable);

        txtMenubarTime = findViewById(R.id.txt_menubar_time);

        // 가상 시작 시간: 08:00
        virtualTime.set(Calendar.HOUR_OF_DAY, 8);
        virtualTime.set(Calendar.MINUTE, 0);
        virtualTime.set(Calendar.SECOND, 0);

        // 시작
        handler.post(virtualClockTask);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(virtualClockTask);
    }


}
