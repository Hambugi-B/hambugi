package com.example.hambugi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CookingTable extends AppCompatActivity {

    Button btn_menubar;

    Button btn_delete;
    Button btn_complete;
    Button btn_counter_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.view_cookingtable);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cookingtable), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_menubar=findViewById(R.id.btn_menubar_menu);

        btn_menubar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CookingTable.this,fragment_menu.class);
                startActivityForResult(intent,1);
            }
        });




        btn_counter_view=findViewById(R.id.btn_change_view);

        btn_counter_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CookingTable.this,Counter.class);
                startActivityForResult(intent,1);
            }
        });



    }



}
