package com.example.hambugi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class IngredientStore extends AppCompatActivity {
    private ImageButton btn_cheese, btn_temp, btn_tomato, btn_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_ingredient_store);

        btn_cheese = findViewById(R.id.btn_unlock_cheese);
        btn_tomato = findViewById(R.id.btn_unlock_tomato);
        //btn_temp = findViewById(R.id.btn_unlock_temp);

        btn_cheese.setOnClickListener(v -> unlockIngredient("cheese", btn_cheese));
        btn_tomato.setOnClickListener(v -> unlockIngredient("tomato", btn_cheese));
        // 재료가 1개 빔 추가 재료 생각하기
        //btn_tomato.setOnClickListener(v -> unlockIngredient("", btn_cheese));

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IngredientStore.this, EndDay.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // 해금 처리 함수
    private void unlockIngredient(String ingredient, ImageButton button){
        GameManager gm = GameManager.getInstance();

        if (gm.getUnlockedIngredients().contains(ingredient)) {
            Toast.makeText(this, "이미 해금된 재료입니다!", Toast.LENGTH_SHORT).show();
        } else {
            gm.unlockIngredient(ingredient);
            Toast.makeText(this, ingredient + " 해금 완료!", Toast.LENGTH_SHORT).show();
            button.setEnabled(false);   // 버튼 비활성화
            button.setAlpha(0.5f);      // 흐리게 표시
        }
    }

}
