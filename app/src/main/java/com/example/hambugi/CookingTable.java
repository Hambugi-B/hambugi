package com.example.hambugi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public class CookingTable extends AppCompatActivity {

    private FrameLayout burgerLayout;
    private Stack<String> burgerStack = new Stack<>();
    private int stackIndex = 0;
    private final int STACK_GAP = -15;

    private Handler handler = new Handler();
    private Runnable patienceCheckRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_cookingtable);

        setupIngredientButtons();

        findViewById(R.id.btn_change_view).setOnClickListener(v -> {
            Intent intent = new Intent(CookingTable.this, Counter.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btn_delete).setOnClickListener(v -> clearBurger());
        findViewById(R.id.btn_complete).setOnClickListener(v -> completeBurger());
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPatienceCheck();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(patienceCheckRunnable);
    }

    private void startPatienceCheck() {
        patienceCheckRunnable = new Runnable() {
            @Override
            public void run() {
                if (!GameManager.getInstance().isPaused() && !GameManager.getInstance().isPatiencePaused()) {
                    if (GameManager.getInstance().isCustomerExpired()) {
                        customerLeaves();
                        return;
                    }
                }
                handler.postDelayed(this, 500);
            }
        };
        handler.post(patienceCheckRunnable);
    }

    private void customerLeaves() {
        Toast.makeText(this, "손님이 떠났습니다!", Toast.LENGTH_SHORT).show();
        GameManager.getInstance().serveCustomer();
        // 여기선 화면 전환은 필요없고 주문 정보 갱신 정도만 가능
    }

    private void setupIngredientButtons() {
        List<String> unlocked = GameManager.getInstance().getUnlockedIngredients();

        setupButton(R.id.btn_bun_top, "bun_top", unlocked);
        setupButton(R.id.btn_patty, "patty", unlocked);
        setupButton(R.id.btn_lettuce, "lettuce", unlocked);
        setupButton(R.id.btn_cheese, "cheese", unlocked);
        setupButton(R.id.btn_tomato, "tomato", unlocked);
        setupButton(R.id.btn_bun_bottom, "bun_bottom", unlocked);
    }

    private void setupButton(int id, String ingredient, List<String> unlocked) {
        ImageButton button = findViewById(id);
        if (unlocked.contains(ingredient)) {
            button.setOnClickListener(v -> addIngredient(ingredient));
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }
    }

    private void addIngredient(String ingredient) {
        burgerStack.push(ingredient);
        ImageView img = new ImageView(this);
        int resId = getResources().getIdentifier(ingredient, "drawable", getPackageName());
        img.setImageResource(resId);

        int dpWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
        int dpHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dpWidth, dpHeight);
        img.setLayoutParams(params);
        img.setTranslationY(stackIndex * STACK_GAP);
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (burgerLayout == null) burgerLayout = findViewById(R.id.layout_hamburger_stack);
        burgerLayout.addView(img);
        stackIndex++;
    }

    private void clearBurger() {
        burgerStack.clear();
        burgerLayout.removeAllViews();
        stackIndex = 0;
    }

    private void completeBurger() {
        List<String> madeBurger = new ArrayList<>(burgerStack);
        List<String> order = GameManager.getInstance().getCurrentOrder();

        if (matchIgnoringOrder(madeBurger, order)) {
            Toast.makeText(this, "Perfect Order!", Toast.LENGTH_SHORT).show();
            int price = GameManager.getInstance().calculateBurgerPrice(madeBurger);
            GameManager.getInstance().addGold(price);
        } else {
            Toast.makeText(this, "Wrong Order!", Toast.LENGTH_SHORT).show();
        }

        GameManager.getInstance().generateNewOrder();
        clearBurger();

        Intent intent = new Intent(CookingTable.this, Counter.class);
        startActivity(intent);
        finish();
    }

    private boolean matchIgnoringOrder(List<String> a, List<String> b) {
        if (a.size() != b.size()) return false;
        for (String item : new HashSet<>(a)) {
            if (Collections.frequency(a, item) != Collections.frequency(b, item)) return false;
        }
        return true;
    }
}
