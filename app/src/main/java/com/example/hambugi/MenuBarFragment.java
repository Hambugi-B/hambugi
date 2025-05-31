package com.example.hambugi;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class MenuBarFragment extends Fragment {

    private TextView txt_time, txt_gold;
    private ImageButton btn_menu;
    private FrameLayout menu_container;

    // [추가] 게임 시간 계산용 상수
    private static final int  START_HOUR        = 8;             // 08:00 시작
    private static final long FULL_MILLIS       = 5 * 60 * 1000; // 실제 5분 = 게임 하루
    private static final int  GAME_DAY_MINUTES  = 14 * 60;       // 게임 하루 = 14시간

    private int  currentHour   = START_HOUR;
    private int  currentMinute = 0;
    private long remainingMillis = FULL_MILLIS;

    private boolean isMenuVisible = false;

    // [추가] 타이머 일시정지 여부
    private boolean isClockPaused = false;

    // [추가] 일시정지 기능이 내장된 CountDownTimer
    private PausableCountDownTimer gameClock;

    @Nullable
    @Override
    public android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                          @Nullable android.view.ViewGroup container,
                                          @Nullable Bundle savedInstanceState) {

        android.view.View view = inflater.inflate(R.layout.fragment_menubar, container, false);

        txt_time       = view.findViewById(R.id.txt_menubar_time);
        txt_gold       = view.findViewById(R.id.txt_menubar_gold);
        btn_menu       = view.findViewById(R.id.btn_menubar_menu);
        menu_container = view.findViewById(R.id.menu_container);

        txt_gold.setText("G:0");

        startClock(remainingMillis);               // 타이머 시작
        btn_menu.setOnClickListener(v -> toggleMenuFragment());
        updateGoldText();

        return view;
    }

    // [추가] 일시정지·재개 지원 CountDownTimer 정의
    private class PausableCountDownTimer extends CountDownTimer {

        PausableCountDownTimer(long millisInFuture, long interval) {
            super(millisInFuture, interval);
            remainingMillis = millisInFuture;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            remainingMillis = millisUntilFinished;

            // [추가] 게임 전역 또는 개별 일시정지 모두 고려
            if (isClockPaused || GameManager.getInstance().isPaused()) return;

            int elapsedSec       = (int) ((FULL_MILLIS - millisUntilFinished) / 1000);
            int totalGameMinutes = (int) ((elapsedSec / 300.0) * GAME_DAY_MINUTES);

            currentHour   = START_HOUR + totalGameMinutes / 60;
            currentMinute = totalGameMinutes % 60;

            if (currentHour > 22) { currentHour = 22; currentMinute = 0; }

            txt_time.setText(String.format(Locale.getDefault(),
                    "%02d:%02d", currentHour, currentMinute));
        }

        @Override
        public void onFinish() {
            txt_time.setText("22:00");
        }
    }

    private void startClock(long millis) {
        gameClock = new PausableCountDownTimer(millis, 1000);
        gameClock.start();
    }

    // [추가] 타이머 일시정지
    public void pauseClock() {
        if (gameClock != null) {
            gameClock.cancel();      // ★ 실제 스레드 정지
            isClockPaused = true;
        }
    }

    // [추가] 타이머 재개
    public void resumeClock() {
        if (isClockPaused) {
            startClock(remainingMillis); // ★ 멈춘 시점부터 다시 시작
            isClockPaused = false;
        }
    }

    public void stopClock() {
        if (gameClock != null) gameClock.cancel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopClock();                 // 화면 종료 시 타이머 정리
    }

    private void toggleMenuFragment() {
        Fragment existing = getParentFragmentManager().findFragmentByTag("MENU_FRAGMENT");

        if (existing != null && isMenuVisible) {
            ((DialogFragment) existing).dismiss();
            isMenuVisible = false;
            // [추가] 메뉴 닫힘 → 타이머 재개
            resumeClock();
        } else {
            fragment_menu menuDialog = new fragment_menu();
            menuDialog.show(getParentFragmentManager(), "MENU_FRAGMENT");
            isMenuVisible = true;
            // [추가] 메뉴 열림 → 타이머 일시정지
            pauseClock();
        }
    }

    public void updateGoldText() {
        if (txt_gold != null) {
            txt_gold.setText("G: " + GameManager.getInstance().getGold());
        }
    }
}
