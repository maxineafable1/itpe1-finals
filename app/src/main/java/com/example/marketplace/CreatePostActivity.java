package com.example.marketplace;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText titleTxt = findViewById(R.id.titleTxt);
        EditText priceTxt = findViewById(R.id.priceTxt);
        Button submitBtn = findViewById(R.id.createSubmitBtn);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new user with a first and last name
                Map<String, Object> car = new HashMap<>();
                car.put("title", titleTxt.getText().toString());
                car.put("price", priceTxt.getText().toString());
//                car.put("userid", user.getUid());

                // Add a new document with a generated ID
                db.collection("posts")
                    .add(car)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            startActivity(new Intent(CreatePostActivity.this, MainActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreatePostActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });

        titleTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int minLen = 3;
                if (s.toString().length() < minLen) {
                    titleTxt.setError("Must be at least " + minLen + " characters");
                } else {
                    titleTxt.setError(null);
                }
                checkValid(titleTxt, priceTxt, submitBtn);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        priceTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int minLen = 1;
                if (s.toString().length() < minLen || s.toString().startsWith("0")) {
                    priceTxt.setError("Must be at least PHP" + minLen);
                } else {
                    priceTxt.setError(null);
                }
                checkValid(titleTxt, priceTxt, submitBtn);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private void checkValid(EditText titleTxt, EditText priceTxt, Button btn) {
        if (titleTxt.getError() == null && !titleTxt.getText().toString().isEmpty()
                && priceTxt.getError() == null && !priceTxt.getText().toString().isEmpty()
        ) {
            btn.setEnabled(true);
        } else {
            btn.setEnabled(false);
        }
    }
}