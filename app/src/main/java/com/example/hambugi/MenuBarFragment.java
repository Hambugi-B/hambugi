
package com.example.hambugi;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private int currentGold = 0;
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

        return view;
    }

    private void startClock(long millis) {
        gameClock = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isPaused) return;
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

    public void earnMoney(int amount) {
        currentGold += amount;
        txt_gold.setText("G:" + currentGold);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (gameClock != null) gameClock.cancel();
    }

    private void toggleMenuFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        Fragment existingFragment = getParentFragmentManager().findFragmentByTag("MENU_FRAGMENT");

        if (existingFragment != null && isMenuVisible) {
            transaction.remove(existingFragment);
            isMenuVisible = false;
            menu_container.setVisibility(View.GONE);
        } else {
            Fragment menuFragment = new fragment_menu();
            transaction.add(R.id.menu_container, menuFragment, "MENU_FRAGMENT");
            isMenuVisible = true;
            menu_container.setVisibility(View.VISIBLE);
        }

        transaction.commit();
    }
}
