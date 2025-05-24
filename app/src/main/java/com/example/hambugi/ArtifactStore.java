package com.example.hambugi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class ArtifactStore extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_artifact_store);

        AppCompatButton btnWater = findViewById(R.id.btn_waterbottle);
        AppCompatButton btnCurtain = findViewById(R.id.btn_curtain);
        AppCompatButton btnDoll = findViewById(R.id.btn_doll);

        View.OnClickListener listener = v -> {
            String ingredient = "";

            if (v.getId() == R.id.btn_waterbottle) {
                ingredient = "waterbottle";
            } else if (v.getId() == R.id.btn_curtain) {
                ingredient = "curtain";
            } else if (v.getId() == R.id.btn_doll) {
                ingredient = "doll";
            }

            // 재료 정보를 CookingTable에 전달
            Intent intent = new Intent(ArtifactStore.this, CookingTable.class);
            intent.putExtra("ingredient", ingredient);
            startActivity(intent);
        };

        btnWater.setOnClickListener(listener);
        btnCurtain.setOnClickListener(listener);
        btnDoll.setOnClickListener(listener);
    }
}
