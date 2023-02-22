package com.example.prototype25;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Feeds extends AppCompatActivity {
    Button camBtns;
    TextView userNames;
    RecyclerView recyclerView;
    myAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseUiException firebaseUiException;
    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);
        recyclerView = findViewById(R.id.recyclerView);
        camBtns = findViewById(R.id.cameraBtn);
        userNames = findViewById(R.id.userNameFeed);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String value = extras.getString("username");
            userNames.setText(value);
        }
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String name =  sh.getString("name","");
        userNames.setText(name);

        camBtns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userNames.getText().toString().trim();
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                main.putExtra("username",user);
                startActivity(main);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerOptions<image_model> options =
                new FirebaseRecyclerOptions.Builder<image_model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("imageDetail"), image_model.class)
                        .build();

        adapter = new myAdapter(options);
        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void openMain(View view) {
        Toast.makeText(this, "Opening Main", Toast.LENGTH_SHORT).show();
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
    }
    }
