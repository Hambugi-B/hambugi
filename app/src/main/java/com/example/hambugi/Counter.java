package com.example.hambugi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Counter extends AppCompatActivity {

    private TextView txt_order;
    private ImageButton btn_change_view;
    private ProgressBar progressBar;

    private Handler handler = new Handler();
    private Runnable patienceRunnable;

    private final int interval = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_counter);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.menubar_fragment_container, new MenuBarFragment())
                    .commit();
        }

        txt_order = findViewById(R.id.txt_order);
        btn_change_view = findViewById(R.id.btn_change_view);
        progressBar = findViewById(R.id.progress_patience);

        btn_change_view.setOnClickListener(v -> {
            Intent intent = new Intent(Counter.this, CookingTable.class);
            intent.putStringArrayListExtra("order", new ArrayList<>(GameManager.getInstance().getCurrentOrder()));
            startActivity(intent);
        });

        updateOrderText();
        animateNewCustomer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPatienceTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(patienceRunnable);
    }

    private void updateOrderText() {
        List<String> currentOrder = GameManager.getInstance().getCurrentOrder();
        String orderText = TextUtils.join(" > ", currentOrder);
        txt_order.setText(orderText);
    }

    private void animateNewCustomer() {
        txt_order.setVisibility(View.INVISIBLE);
        ImageView img_customer = findViewById(R.id.img_customer);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.anim_customer_up);
        img_customer.startAnimation(slideUp);

        new Handler().postDelayed(() -> {
            txt_order.setVisibility(View.VISIBLE);
            Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in);
            fadeIn.setDuration(300);
            txt_order.startAnimation(fadeIn);
        }, 200);
    }

    private void startPatienceTimer() {
        progressBar.setMax(100);
        progressBar.setProgress(100);
        handler.removeCallbacks(patienceRunnable);

        patienceRunnable = new Runnable() {
            @Override
            public void run() {
                if (GameManager.getInstance().isPaused() || GameManager.getInstance().isPatiencePaused()) {
                    handler.postDelayed(this, interval);
                    return;
                }

                long remaining = GameManager.getInstance().getRemainingPatience();
                long totalLimit = GameManager.getInstance().getCurrentPatienceLimit();
                int progress = (int) (((float) remaining / totalLimit) * 100);
                progressBar.setProgress(progress);

                if (remaining > 0) {
                    handler.postDelayed(this, interval);
                } else {
                    customerLeaves();
                }
            }
        };

        handler.post(patienceRunnable);
    }

    private void customerLeaves() {
        Toast.makeText(this, "손님이 떠났습니다!", Toast.LENGTH_SHORT).show();
        GameManager.getInstance().serveCustomer();
        updateOrderText();
        animateNewCustomer();
        startPatienceTimer();
    }
}
