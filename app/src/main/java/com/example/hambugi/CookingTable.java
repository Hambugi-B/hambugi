package com.example.hambugi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;


public class CookingTable extends Activity {
    private TextView txt_menubar_time, txt_menubar_gold;
    private ImageButton btn_delete, btn_complete, btn_change_view, btn_menu;

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

            }
        });

        // 햄버거 완성 버튼 클릭 시
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
