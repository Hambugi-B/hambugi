package com.example.hambugi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.*;
import android.content.Context;

import androidx.annotation.Nullable;

import java.util.Stack;


public class CookingTable extends Activity {
    private TextView txt_menubar_time, txt_menubar_gold;
    private ImageButton btn_delete, btn_complete, btn_change_view, btn_menu;
    private Stack<String> burgerStack = new Stack<>();
    private FrameLayout burgerLayout;
    private Context context;

    private int stackIndex = 0;     // 재료 개수 추적용
    private final int STACK_GAP = -15;   // 재료 겹침 정도

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

        findViewById(R.id.btn_ingredient_1).setOnClickListener(v -> addIngredient("bun_top"));
        findViewById(R.id.btn_ingredient_2).setOnClickListener(v -> addIngredient("patty"));
        findViewById(R.id.btn_ingredient_3).setOnClickListener(v -> addIngredient("lettuce"));
        findViewById(R.id.btn_ingredient_4).setOnClickListener(v -> addIngredient("cheese"));
        findViewById(R.id.btn_ingredient_5).setOnClickListener(v -> addIngredient("tomato"));
        findViewById(R.id.btn_ingredient_6).setOnClickListener(v -> addIngredient("bun_bottom"));

        // 화면 전환 버튼 클릭 시
        btn_change_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CookingTable.this, Counter.class);
                startActivity(intent);
            }
        });

        // 햄버거 삭제 버튼 클릭 시
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBurger();
            }
        });

        // 햄버거 완성 버튼 클릭 시
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeBurger();
            }
        });
    }

    // 재료 추가하여 이미지 반영하는 함수
    private void addIngredient(String ingerdient){
        burgerStack.push(ingerdient);
        ImageView img = new ImageView(context);

        switch(ingerdient){
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
            default: break;
        }

        int dpWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
        int dpHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dpWidth, dpHeight
        );
        img.setLayoutParams(params);
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // 위로 조금씩 이동하면서 겹치게
        img.setTranslationY(stackIndex * STACK_GAP);
        burgerLayout.addView(img);
        stackIndex++;
    }

    // 햄버거 이미지 삭제 및 초기화
    private void deleteBurger() {
        burgerStack.clear();
        burgerLayout.removeAllViews();
        stackIndex = 0;
    }

    // 햄버거 완성 시 주문과 비교 및 화면 전환
    private void completeBurger() {
        // TODO: 주문과 비교하는 로직


        deleteBurger();
        Intent intent = new Intent(CookingTable.this, Counter.class);
        startActivity(intent);
    }
}
