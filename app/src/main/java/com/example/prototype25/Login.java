package com.example.prototype25;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    Button login;
    EditText username, password;
    TextView signup, passwordReset;
    private FirebaseAuth mAuth;

    String useName;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.loginBtn);
        signup = findViewById(R.id.signUp);
        username = findViewById(R.id.usernameLog);
        password = findViewById(R.id.passwordLog);
        mAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(v -> {
            Intent reg = new Intent(getApplicationContext(), Registration.class);
            startActivity(reg);
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uname = username.getText().toString().trim();
                final String upass = password.getText().toString().trim();
                useName = uname;
                if(uname.isEmpty() || upass.isEmpty()){
                    Toast.makeText(Login.this, "Please enter valid details", Toast.LENGTH_SHORT).show();
                    username.requestFocus();
                }
                else{
                    reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(uname)){
                                final String getPass = snapshot.child(uname).child("password").getValue(String.class);
                                if(getPass.equals(upass)) {
                                    final String getEmail = snapshot.child(uname).child("email").getValue(String.class);
                                    mAuth.signInWithEmailAndPassword(getEmail,getPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()){
                                                SharedPreferences  sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                                                SharedPreferences.Editor myedit = sharedPreferences.edit();
                                                myedit.putString("name", useName);
                                                myedit.apply();
                                                Intent feed = new Intent(getApplicationContext(), Feeds.class);
                                                feed.putExtra("username", uname);
                                                startActivity(feed);
                                            }
                                            else {
                                                Toast.makeText(Login.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(Login.this, "No User found", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });


    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
                        Intent feed = new Intent(getApplicationContext(), Feeds.class);
                        feed.putExtra("username", useName);
                        startActivity(feed);
        }
    }
}