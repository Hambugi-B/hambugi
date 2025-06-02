package com.example.hambugi;

import android.content.Intent;
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

    private TextView txt_stage, txt_time, txt_gold;
    private ImageButton btn_menu;
    private FrameLayout menu_container;

    private PausableCountDownTimer gameClock;
    private boolean isStageEnded = false;

    @Override
    public @NonNull android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                                   @Nullable android.view.ViewGroup container,
                                                   @Nullable Bundle savedInstanceState) {
        android.view.View v = inflater.inflate(R.layout.fragment_menubar, container, false);

        txt_stage = v.findViewById(R.id.txt_menubar_stage);
        txt_time = v.findViewById(R.id.txt_menubar_time);
        txt_gold = v.findViewById(R.id.txt_menubar_gold);
        btn_menu = v.findViewById(R.id.btn_menubar_menu);
        menu_container = v.findViewById(R.id.menu_container);

        updateStageText();

        if (GameManager.getInstance().isGoldInitialized()) {
            updateGoldText();
        } else {
            GameManager.getInstance().loadGameDataAndNotify(this::updateGoldText);
        }

        btn_menu.setOnClickListener(view -> toggleMenuFragment());

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStageText();
        if (gameClock == null)
            startClock();
    }
    @Override
    public void onPause() {
        super.onPause();
        if (gameClock != null) {
            gameClock.cancel();
            gameClock = null;  // 완전히 제거 (메모리 누수 방지)
        }
    }

    private void updateStageText() {
        int stage = GameManager.getInstance().getCurrentStage();
        txt_stage.setText("Stage: " + stage);
    }

    private void startClock() {
        if (gameClock != null) return;
        gameClock = new PausableCountDownTimer(1000);
        gameClock.start();
    }

    private class PausableCountDownTimer extends CountDownTimer {
        public PausableCountDownTimer(long step) {
            super(Long.MAX_VALUE, step);  // 계속 반복
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (GameManager.getInstance().isPaused()) return;

            if(isStageEnded) return;

            long remaining = GameManager.getInstance().getRemainingMillis();
            GameManager.getInstance().decreaseRemainingMillis(1000);

            // 시간 변환
            long elapsedMillis = GameManager.getInstance().getFullMillis() - remaining;
            int elapsedSec = (int) (elapsedMillis / 1000);
            int totalGameMinutes = (int) ((elapsedSec / 300.0) * (14 * 60));

            int hour = 8 + totalGameMinutes / 60;
            int minute = totalGameMinutes % 60;

            if (hour > 22) {
                hour = 22;
                minute = 0;
            }

            // 스테이지 종료 조건
            if (hour >= 8 && minute == 8) {
                isStageEnded = true;
                endStage();
                return;
            }

            if(txt_time != null)
                txt_time.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
        }

        @Override
        public void onFinish() { }
    }

    private void endStage() {
        if (gameClock != null){
            gameClock.cancel();
            gameClock = null;
        }

        if(getActivity() != null) {
            try{
                Intent intent = new Intent(getActivity(), EndDay.class);
                startActivity(intent);
                getActivity().finish();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void toggleMenuFragment() {
        Fragment existing = getParentFragmentManager().findFragmentByTag("MENU_FRAGMENT");
        if (existing != null) {
            ((DialogFragment) existing).dismiss();
            GameManager.getInstance().resumeGame();
        } else {
            GameManager.getInstance().pauseGame();
            fragment_menu dlg = new fragment_menu();
            dlg.show(getParentFragmentManager(), "MENU_FRAGMENT");
        }
    }

    public void updateGoldText() {
        txt_gold.setText("G: " + GameManager.getInstance().getGold());
    }
}
