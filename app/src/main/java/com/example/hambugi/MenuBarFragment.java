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

    /* 게임 시간 계산용 상수 */
    private static final int  START_HOUR       = 8;              // 08:00
    private static final long FULL_MILLIS      = 5 * 60 * 1000;  // 실제 5분 = 게임 하루
    private static final int  GAME_DAY_MINUTES = 14 * 60;        // 게임 하루 = 14h

    private int  currentHour;
    private int  currentMinute;
    private long remainingMillis;           // ★ GameManager에서 받아옴

    private boolean isMenuVisible = false;
    private PausableCountDownTimer gameClock;

    /* ─────────────────────────────── */

    @Override
    public @NonNull android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                                   @Nullable android.view.ViewGroup container,
                                                   @Nullable Bundle savedInstanceState) {

        android.view.View v = inflater.inflate(R.layout.fragment_menubar, container, false);

        txt_time = v.findViewById(R.id.txt_menubar_time);
        txt_gold = v.findViewById(R.id.txt_menubar_gold);
        btn_menu = v.findViewById(R.id.btn_menubar_menu);
        menu_container = v.findViewById(R.id.menu_container);

        txt_gold.setText("G:0");

        /* ★ 저장돼 있던 남은 ms를 우선 사용 */
        long stored = GameManager.getInstance().getRemainingMillis();
        remainingMillis = (stored < 0) ? FULL_MILLIS : stored;

        currentHour   = START_HOUR;
        currentMinute = 0;

        startClock(remainingMillis);
        btn_menu.setOnClickListener(view -> toggleMenuFragment());
        return v;
    }

    /* ───────── 내부 CountDownTimer ───────── */
    private class PausableCountDownTimer extends CountDownTimer {

        PausableCountDownTimer(long millis, long step) {
            super(millis, step);
            remainingMillis = millis;
        }

        @Override public void onTick(long millisUntilFinished) {
            remainingMillis = millisUntilFinished;
            GameManager.getInstance().setRemainingMillis(remainingMillis);   // ★ 매 tick 저장

            if (GameManager.getInstance().isPaused()) return;                // 전역 일시정지

            int elapsedSec       = (int) ((FULL_MILLIS - millisUntilFinished) / 1000);
            int totalGameMinutes = (int) ((elapsedSec / 300.0) * GAME_DAY_MINUTES);

            currentHour   = START_HOUR + totalGameMinutes / 60;
            currentMinute = totalGameMinutes % 60;

            if (currentHour > 22) { currentHour = 22; currentMinute = 0; }

            txt_time.setText(String.format(Locale.getDefault(),
                    "%02d:%02d", currentHour, currentMinute));
        }

        @Override public void onFinish() { txt_time.setText("22:00"); }
    }

    /* ───────── 타이머 제어 ───────── */
    private void startClock(long ms) {
        if (gameClock != null) gameClock.cancel();     // ★ 중복 방지
        gameClock = new PausableCountDownTimer(ms, 1000);
        gameClock.start();
    }

    public void pauseClock() {                         // 메뉴 열 때 호출
        if (gameClock != null) gameClock.cancel();
        GameManager.getInstance().setPaused(true);         // ★
        GameManager.getInstance().setRemainingMillis(remainingMillis); // ★
    }

    public void resumeClock() {                        // 메뉴 닫을 때 호출
        if (GameManager.getInstance().isPaused()) {
            GameManager.getInstance().setPaused(false);    // ★
            remainingMillis = GameManager.getInstance().getRemainingMillis();
            startClock(remainingMillis);
        }
    }

    public void stopClock() {
        if (gameClock != null) gameClock.cancel();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        stopClock();
    }

    /* ───────── 메뉴 DialogFragment 토글 ───────── */
    private void toggleMenuFragment() {
        Fragment existing = getParentFragmentManager().findFragmentByTag("MENU_FRAGMENT");
        if (existing != null && isMenuVisible) {
            ((DialogFragment) existing).dismiss();
            isMenuVisible = false;
            resumeClock();                 // ★ 메뉴 닫힘 → 재개
        } else {
            fragment_menu dlg = new fragment_menu();
            dlg.show(getParentFragmentManager(), "MENU_FRAGMENT");
            isMenuVisible = true;
            pauseClock();                  // ★ 메뉴 열림 → 일시정지
        }
    }

    /* ───────── 골드 표시 업데이트 ───────── */
    public void updateGoldText() {
        txt_gold.setText("G: " + GameManager.getInstance().getGold());
    }
}

