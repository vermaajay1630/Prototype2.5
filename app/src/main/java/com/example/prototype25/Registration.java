package com.example.prototype25;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Registration extends AppCompatActivity {

    Button reg;
    EditText username, email, contact, password;
    TextView signIn;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        reg = findViewById(R.id.regBtn);
        username = findViewById(R.id.usernameReg);
        email = findViewById(R.id.emailReg);
        password = findViewById(R.id.passwordReg);
        signIn = findViewById(R.id.signIn);
        mAuth = FirebaseAuth.getInstance();
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uname = username.getText().toString().trim();
                final String uemail = email.getText().toString().trim();
                final String upass = password.getText().toString().trim();
                if(uname.isEmpty() || uemail.isEmpty() || upass.isEmpty()){
                    Toast.makeText(Registration.this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(uname)){
                                Toast.makeText(Registration.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                createUser();
                                reference.child("Users").child(uname).child("email").setValue(uemail);
                                reference.child("Users").child(uname).child("password").setValue(upass);
                                Intent log = new Intent(getApplicationContext(), Login.class);
                                log.putExtra("username", uname);
                                startActivity(log);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Registration.this, "Registration Unsuccessfull", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent log = new Intent(getApplicationContext(), Login.class);
                startActivity(log);
            }
        });
    }

    private void createUser() {
        final String uemail = email.getText().toString().trim();
        final String upass = password.getText().toString().trim();
        mAuth.createUserWithEmailAndPassword(uemail, upass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Registration.this, "Signed Up Successfully", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }
}