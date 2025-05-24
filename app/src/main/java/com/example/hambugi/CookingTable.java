package com.example.hambugi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public class CookingTable extends Activity {
    private TextView txt_menubar_time, txt_menubar_gold;
    private ImageButton btn_delete, btn_complete, btn_change_view, btn_menu;
    private Stack<String> burgerStack = new Stack<>();
    private FrameLayout burgerLayout;
    private Context context;

    private int stackIndex = 0;     // 재료 개수 추적용
    private final int STACK_GAP = -15;   // 재료 겹침 정도

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_cookingtable);

        txt_menubar_time = findViewById(R.id.txt_menubar_time);
        txt_menubar_gold = findViewById(R.id.txt_menubar_gold);
        btn_delete = findViewById(R.id.btn_delete);
        btn_complete = findViewById(R.id.btn_complete);
        btn_change_view = findViewById(R.id.btn_change_view);
        btn_menu = findViewById(R.id.btn_menubar_menu);
        burgerLayout = findViewById(R.id.layout_hamburger_stack);
        context = this;


        // Store에서 재료가 전달된 경우
        String ingredientFromStore = getIntent().getStringExtra("ingredient");
        if (ingredientFromStore != null) {
            // 위쪽 빵 제거 후 재료 추가 → 다시 위빵 추가
            if (!burgerStack.isEmpty() && burgerStack.peek().equals("bun_top")) {
                burgerLayout.removeViewAt(burgerLayout.getChildCount() - 1);
                burgerStack.pop();
                stackIndex--;
            }

            addIngredient(ingredientFromStore);
            addIngredient("bun_top");
        }

        // 각 재료 추가 버튼 리스너
        findViewById(R.id.btn_bun_top).setOnClickListener(v -> addIngredient("bun_top"));
        findViewById(R.id.btn_patty).setOnClickListener(v -> addIngredient("patty"));
        findViewById(R.id.btn_lettuce).setOnClickListener(v -> addIngredient("lettuce"));
        findViewById(R.id.btn_cheese).setOnClickListener(v -> addIngredient("cheese"));
        findViewById(R.id.btn_tomato).setOnClickListener(v -> addIngredient("tomato"));
        findViewById(R.id.btn_bun_bottom).setOnClickListener(v -> addIngredient("bun_bottom"));

        // 화면 전환
        btn_change_view.setOnClickListener(v -> {
            Intent intent = new Intent(CookingTable.this, Counter.class);
            startActivity(intent);
        });

        // 햄버거 삭제
        btn_delete.setOnClickListener(v -> deleteBurger());

        // 햄버거 완성
        btn_complete.setOnClickListener(v -> completeBurger());
    }

    // 재료를 스택에 추가하고 화면에 이미지로 표시
    private void addIngredient(String ingerdient) {
        burgerStack.push(ingerdient);
        ImageView img = new ImageView(context);

        switch (ingerdient) {
            case "bun_top":
                img.setImageResource(R.drawable.bun_top);
                break;
            case "patty":
                img.setImageResource(R.drawable.patty);
                break;
            case "lettuce":
                img.setImageResource(R.drawable.lettuce);
                break;
            case "tomato":
                img.setImageResource(R.drawable.tomato);
                break;
            case "cheese":
                img.setImageResource(R.drawable.cheese);
                break;
            case "bun_bottom":
                img.setImageResource(R.drawable.bun_bottom);
                break;
            default:
                break;
        }

        int dpWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
        int dpHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dpWidth, dpHeight);
        img.setLayoutParams(params);
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // 재료가 위로 겹치도록 위치 조정
        img.setTranslationY(stackIndex * STACK_GAP);
        burgerLayout.addView(img);
        stackIndex++;
    }

    // 햄버거 삭제: 스택과 UI 초기화
    private void deleteBurger() {
        burgerStack.clear();
        burgerLayout.removeAllViews();
        stackIndex = 0;
    }

    // 햄버거 완성 → 정답 여부 확인 → 다음 손님
    private void completeBurger() {
        List<String> madeBurger = new ArrayList<>(burgerStack);
        List<String> order = GameManager.getInstance().getCurrentOrder();

        if (matchIgnoringOrder(madeBurger, order)) {
            Toast.makeText(this, "Perfect Order!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong Order!", Toast.LENGTH_SHORT).show();
        }

        GameManager.getInstance().serveCustomer();
        deleteBurger();

        Intent intent = new Intent(CookingTable.this, Counter.class);
        startActivity(intent);
        finish();
    }

    // 순서 상관 없이 햄버거 재료 비교
    private boolean matchIgnoringOrder(List<String> a, List<String> b) {
        if (a.size() != b.size()) return false;

        for (String item : new HashSet<>(a)) {
            if (Collections.frequency(a, item) != Collections.frequency(b, item)) {
                return false;
            }
        }

        return true;
    }
}
