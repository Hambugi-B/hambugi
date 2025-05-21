package com.example.hambugi;

import android.app.Activity;
import android.content.ActivityNotFoundException;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Counter extends AppCompatActivity {
    private TextView txt_menubar_time, txt_menubar_gold, txt_order;
    private ImageButton btn_change_view, btn_menu;

    private Handler handler = new Handler();
    private Runnable customerCheckRunnable;
    private final int checkInterval = 1000; // 손님 상태 확인 주기: 1초

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_counter);

        txt_menubar_time = findViewById(R.id.txt_menubar_time);
        txt_menubar_gold = findViewById(R.id.txt_menubar_gold);
        txt_order = findViewById(R.id.txt_order);
        btn_change_view = findViewById(R.id.btn_change_view);
        btn_menu = findViewById(R.id.btn_menubar_menu);

        // 햄버거 제작 화면으로 이동
        btn_change_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Counter.this, CookingTable.class);
                intent.putStringArrayListExtra("order", new ArrayList<>(GameManager.getInstance().getCurrentOrder()));
                startActivity(intent);
            }
        });

        // 손님 상태 주기적으로 확인 (만료 시 새 손님 생성)
        customerCheckRunnable = new Runnable() {
            @Override
            public void run() {
                GameManager gm = GameManager.getInstance();

                if (gm.isCustomerExpired()) {
                    Toast.makeText(Counter.this, "Time Over", Toast.LENGTH_SHORT).show();
                    gm.generateNewOrder();
                    animateNewCustomer();
                }

                updateOrderText();
                handler.postDelayed(this, checkInterval);
            }
        };

        // 첫 손님 등장 애니메이션 및 텍스트 표시
        animateNewCustomer();
        updateOrderText();
        handler.postDelayed(customerCheckRunnable, checkInterval);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(virtualClockTask);
    }


        handler.removeCallbacks(customerCheckRunnable);
    }

    // 주문 텍스트를 표시하는 함수
    private void updateOrderText() {
        List<String> currentOrder = GameManager.getInstance().getCurrentOrder();
        String orderText = TextUtils.join(" > ", currentOrder);
        txt_order.setText(orderText);
    }

    // 손님 등장 애니메이션 + 텍스트 지연 표시
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

}
