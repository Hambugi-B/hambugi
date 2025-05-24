package com.example.hambugi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class Store extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_store);

        ImageButton cheeseBtn = findViewById(R.id.plus_cheese);
        ImageButton lettuceBtn = findViewById(R.id.plus_lettuce);
        ImageButton tomatoBtn = findViewById(R.id.plus_tomato);

        View.OnClickListener ingredientListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredient = "";

                int id = v.getId();
                if (id == R.id.plus_cheese) {
                    ingredient = "cheese";
                } else if (id == R.id.plus_lettuce) {
                    ingredient = "lettuce";
                } else if (id == R.id.plus_tomato) {
                    ingredient = "tomato";
                }

                Intent intent = new Intent(Store.this, CookingTable.class);
                intent.putExtra("ingredient", ingredient);
                startActivity(intent);
            }
        };

        cheeseBtn.setOnClickListener(ingredientListener);
        lettuceBtn.setOnClickListener(ingredientListener);
        tomatoBtn.setOnClickListener(ingredientListener);
    }

}
