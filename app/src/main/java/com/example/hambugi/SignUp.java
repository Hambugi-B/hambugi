package com.example.hambugi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends Activity {
    private EditText edit_name, edit_email, edit_id, edit_pw;
    private ImageButton btn_check_duplicate;
    private ImageButton btn_signUp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_signup);

        edit_name = findViewById(R.id.et_name);
        edit_email = findViewById(R.id.et_email);
        edit_id = findViewById(R.id.et_id);
        edit_pw = findViewById(R.id.et_pw);
        btn_check_duplicate = findViewById(R.id.btn_check_duplicate);
        btn_signUp = findViewById(R.id.btn_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btn_check_duplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edit_id.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(SignUp.this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.collection("users")
                        .whereEqualTo("username", username)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots.isEmpty()) {
                                Toast.makeText(SignUp.this, "사용 가능한 ID입니다", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUp.this, "이미 사용 중인 ID입니다", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(SignUp.this, "중복 확인 실패", Toast.LENGTH_SHORT).show());
            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edit_email.getText().toString().trim();
                String pw = edit_pw.getText().toString().trim();
                String name = edit_name.getText().toString().trim();
                String username = edit_id.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pw)
                        || TextUtils.isEmpty(name) || TextUtils.isEmpty(username)) {
                    Toast.makeText(SignUp.this, "모든 항목을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, pw)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String uid = mAuth.getCurrentUser().getUid();

                                    Map<String, Object> user = new HashMap<>();
                                    user.put("name", name);
                                    user.put("email", email);
                                    user.put("uid", uid);
                                    user.put("username", username);

                                    DocumentReference docRef = db.collection("users").document(uid);
                                    docRef.set(user)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(SignUp.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignUp.this, MainActivity.class));
                                                finish();
                                            })
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(SignUp.this, "DB 저장 실패 ", Toast.LENGTH_SHORT).show());
                                } else {
                                    Toast.makeText(SignUp.this, "회원 가입 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
