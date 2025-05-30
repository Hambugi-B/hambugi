
package com.example.hambugi;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;

public class MenuBarFragment extends Fragment {

    private TextView txt_time, txt_gold;
    private ImageButton btn_menu;
    private FrameLayout menu_container;

    private int startHour = 8;
    private int currentHour = 8;
    private int currentMinute = 0;
    private long remainingMillis = 5 * 60 * 1000;
    private boolean isPaused = false;
    private boolean isMenuVisible = false;

    private CountDownTimer gameClock;

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable android.view.ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menubar, container, false);

        txt_time = view.findViewById(R.id.txt_menubar_time);
        txt_gold = view.findViewById(R.id.txt_menubar_gold);
        btn_menu = view.findViewById(R.id.btn_menubar_menu);
        menu_container = view.findViewById(R.id.menu_container);

        txt_gold.setText("G:0");
        startClock(remainingMillis);

        btn_menu.setOnClickListener(v -> toggleMenuFragment());

        updateGoldText();

        return view;
    }

    private void startClock(long millis) {
        gameClock = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (GameManager.getInstance().isPaused()) return;  // 일시정지 중이면 아무것도 하지 않음

                remainingMillis = millisUntilFinished;

                int elapsed = (int) ((5 * 60 * 1000 - millisUntilFinished) / 1000);
                int totalGameMinutes = (int) ((elapsed / 300.0) * (14 * 60));

                currentHour = startHour + totalGameMinutes / 60;
                currentMinute = totalGameMinutes % 60;

                if (currentHour > 22) {
                    currentHour = 22;
                    currentMinute = 0;
                }

                String time = String.format(Locale.getDefault(), "%02d:%02d", currentHour, currentMinute);
                txt_time.setText(time);
            }
            @Override
            public void onFinish() {
                txt_time.setText("22:00");
            }
        };
        gameClock.start();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (gameClock != null) gameClock.cancel();
    }

    private void toggleMenuFragment() { //손님보다 위에 보이게 수정
        Fragment existingFragment = getParentFragmentManager().findFragmentByTag("MENU_FRAGMENT");

        if (existingFragment != null && isMenuVisible) {
            // DialogFragment는 dismiss()로 닫는다
            ((DialogFragment) existingFragment).dismiss();
            isMenuVisible = false;
        } else {
            // 새로 DialogFragment를 show()로 띄운다
            fragment_menu menuDialog = new fragment_menu();
            menuDialog.show(getParentFragmentManager(), "MENU_FRAGMENT");
            isMenuVisible = true;
        }
    }

    public void onTick(long millisUntilFinished) {
        if (GameManager.getInstance().isPaused()) return;  // 일시정지 중이면 시간 업데이트 안 함

        remainingMillis = millisUntilFinished;

        int elapsed = (int) ((5 * 60 * 1000 - millisUntilFinished) / 1000);
        int totalGameMinutes = (int) ((elapsed / 300.0) * (14 * 60));

        currentHour = startHour + totalGameMinutes / 60;
        currentMinute = totalGameMinutes % 60;

        if (currentHour > 22) {
            currentHour = 22;
            currentMinute = 0;
        }

        String time = String.format(Locale.getDefault(), "%02d:%02d", currentHour, currentMinute);
        txt_time.setText(time);
    }
    //골드량을 표기
    public void updateGoldText() {
        if (txt_gold != null) {
            txt_gold.setText("G: " + GameManager.getInstance().getGold());
        }
    }

}
