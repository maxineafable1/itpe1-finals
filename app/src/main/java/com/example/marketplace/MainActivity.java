package com.example.marketplace;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // [START initialize_auth]
        // Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        // [END initialize_auth]

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RecyclerView carRecyclerView = findViewById(R.id.carRecyclerView);
        carRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<Post> postList = new ArrayList<>();
        adapter = new PostAdapter(postList, post -> {
            Intent intent = new Intent(MainActivity.this, PostDetailActivity.class);
            intent.putExtra("post", post);
            startActivity(intent);
        });
        carRecyclerView.setAdapter(adapter);

        ImageButton hamburger = findViewById(R.id.hamburger);
        hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(MainActivity.this, ActiveUserActivity.class));
                }
            }
        });

        TextView userDisplay = findViewById(R.id.userDisplay);

        if (user != null) {
            userDisplay.setText(user.getEmail());
            hamburger.setImageResource(R.drawable.ic_launcher_background);
        }

        EditText searchTxt = findViewById(R.id.searchTxt);

        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Post> tempList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Post post = document.toObject(Post.class);
                                post.setUid(document.getId());
                                tempList.add(post);
                            }

                            searchTxt.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    filterItems(searchTxt.getText().toString().trim(), tempList);
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                }
                            });

                            adapter.updateList(tempList);
                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
                            Toast.makeText(MainActivity.this, "Log in to see posts", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void filterItems(String query, List<Post> originalItemList) {
        List<Post> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(originalItemList); // Show all items if query is empty
        } else {
            for (Post item : originalItemList) {
                if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.updateList(filteredList); // Method in your adapter to update data
    }
}