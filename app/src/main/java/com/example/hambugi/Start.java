package com.example.hambugi;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

public class Start extends Activity {
    ImageButton btn_new_game, btn_start, btn_exit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_start);

        btn_new_game = findViewById(R.id.btn_new_game);
        btn_start = findViewById(R.id.btn_start);
        btn_exit = findViewById(R.id.btn_exit);

        // 새 게임 버튼 클릭 시 counter 화면으로 전환 (login Activity에서 받아온 회원정보를 Counter에 넘겨 새 게임 생성)
        btn_new_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Start.this, Counter.class);
                startActivity(intent);
            }
        });

        // 시작 버튼 클릭 시 counter 화면으로 전환 (login Activity에서 받아온 회원정보를 Counter에 넘겨 기존 게임 불러오기)
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Start.this, Counter.class);
                startActivity(intent);
            }
        });


        // 종료 버튼 클릭 시 종료
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
