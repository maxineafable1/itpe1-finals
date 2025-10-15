package com.example.marketplace;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "DocSnippets";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText editTitleTxt = findViewById(R.id.editTitleTxt);
        EditText editPriceTxt = findViewById(R.id.editPriceTxt);
        Button updateSubmitBtn = findViewById(R.id.updateSubmitBtn);


        Post post = (Post) getIntent().getSerializableExtra("post");

        if (post != null) {
            String postTitle = post.getTitle();
            String postPrice = post.getPrice();
            String postUid = post.getUid();

            editTitleTxt.setText(postTitle);
            editPriceTxt.setText(postPrice);

            updateSubmitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentReference ref = db.collection("posts").document(postUid);

                    Map<String, Object> car = new HashMap<>();
                    car.put("title", editTitleTxt.getText().toString());
                    car.put("price", editPriceTxt.getText().toString());

                    ref
                        .update(car)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                getDocument(postUid);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditPostActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            });

            editTitleTxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    int minLen = 3;
                    if (s.toString().length() < minLen) {
                        editTitleTxt.setError("Must be at least " + minLen + " characters");
                    } else {
                        editTitleTxt.setError(null);
                    }
                    checkValid(editTitleTxt, editPriceTxt, updateSubmitBtn);
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            editPriceTxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    int minLen = 1;
                    if (s.toString().length() < minLen || s.toString().startsWith("0")) {
                        editPriceTxt.setError("Must be at least PHP" + minLen);
                    } else {
                        editPriceTxt.setError(null);
                    }
                    checkValid(editTitleTxt, editPriceTxt, updateSubmitBtn);
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
        }
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

    public void getDocument(String uid) {
        // [START get_document]
        DocumentReference docRef = db.collection("posts").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Post post = document.toObject(Post.class);
                        post.setUid(document.getId());
                        Intent intent = new Intent(EditPostActivity.this, PostDetailActivity.class);
                        intent.putExtra("post", post);
                        startActivity(intent);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        // [END get_document]
    }
}