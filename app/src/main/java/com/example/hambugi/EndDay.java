package com.example.hambugi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class EndDay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_end_day);

        TextView txt_final_score = findViewById(R.id.txt_final_score);
        TextView txt_final_gold = findViewById(R.id.txt_final_gold);
        ImageButton btn_new_stage = findViewById(R.id.btn_new_stage);
        ImageButton btn_store = findViewById(R.id.btn_store);
        ImageButton btn_exit = findViewById(R.id.btn_exit);

        int stage = GameManager.getInstance().getCurrentStage();

        if(stage >= 10) {
            txt_final_score.setVisibility(View.VISIBLE);
            txt_final_gold.setVisibility(View.VISIBLE);

            txt_final_score.setText("Score: " + GameManager.getInstance().getScore());
            txt_final_gold.setText("Gold: " + GameManager.getInstance().getGold());

            btn_new_stage.setVisibility(View.GONE);
            btn_store.setVisibility(View.GONE);
        } else {
            btn_new_stage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GameManager.getInstance().nextStage();
                    GameManager.getInstance().resetGameTime();
                    Intent intent = new Intent(EndDay.this, Counter.class);
                    startActivity(intent);
                }
            });

            btn_store.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EndDay.this, ArtifactStore.class);
                    startActivity(intent);
                }
            });
        }

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EndDay.this, Start.class);
                startActivity(intent);
            }
        });
    }
}
