package com.example.hambugi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.channels.SelectableChannel;

public class SelectStore extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_select_store);

        ImageButton btn_ingredient_store = findViewById(R.id.btn_ingredient_store);
        ImageButton btn_artifact_store = findViewById(R.id.btn_artifact_store);
        ImageButton btn_exit = findViewById(R.id.btn_exit);

        btn_ingredient_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectStore.this, IngredientStore.class);
                startActivity(intent);
                finish();
            }
        });

        btn_artifact_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectStore.this, ArtifactStore.class);
                startActivity(intent);
                finish();
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
