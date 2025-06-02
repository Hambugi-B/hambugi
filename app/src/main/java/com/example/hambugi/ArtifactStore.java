package com.example.hambugi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class ArtifactStore extends AppCompatActivity {

    private final Map<String, Integer> artifactButtonMap = new HashMap<>();
    private final Map<String, String> artifactDisplayName = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_artifact_store);

        ImageButton btn_exit = findViewById(R.id.btn_exit);

        // 아티팩트 ID → 버튼 ID 매핑
        artifactButtonMap.put("waterbottle", R.id.btn_waterbottle);
        artifactButtonMap.put("curtain", R.id.btn_curtain);
        artifactButtonMap.put("doll", R.id.btn_doll);

        // 아티팩트 ID → 표시이름 매핑
        artifactDisplayName.put("waterbottle", "물병");
        artifactDisplayName.put("curtain", "커튼");
        artifactDisplayName.put("doll", "인형");

        for (String artifactId : artifactButtonMap.keySet()) {
            int buttonId = artifactButtonMap.get(artifactId);
            Button button = findViewById(buttonId);

            // 이미 구매했으면 disable
            if (GameManager.getInstance().isPurchased(artifactId)) {
                button.setEnabled(false);
                button.setText("구매완료");
            }

            button.setOnClickListener(v -> {
                GameManager.PurchaseResult result = GameManager.getInstance().purchaseArtifact(artifactId);

                switch (result) {
                    case SUCCESS:
                        button.setEnabled(false);
                        button.setText("구매완료");
                        Toast.makeText(this, artifactDisplayName.get(artifactId) + " 구매 성공!", Toast.LENGTH_SHORT).show();
                        break;
                    case NOT_ENOUGH_GOLD:
                        Toast.makeText(this, "골드가 부족합니다!", Toast.LENGTH_SHORT).show();
                        break;
                    case ALREADY_PURCHASED:
                        Toast.makeText(this, "이미 구매한 아이템입니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        }

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArtifactStore.this, EndDay.class);
                startActivity(intent);
            }
        });
    }
}
