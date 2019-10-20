package com.example.dananny;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText Username;
    EditText Password;
    Button SignButton;
    LinearLayout SigningPage;
    ProgressBar progressBar;
    private static final String TAG = "Login";
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();


        Username = findViewById(R.id.editTextEmail);
        Password = findViewById(R.id.editTextPassword);
        SigningPage = findViewById(R.id.SigningPageLayout);

        SignButton = findViewById(R.id.BtnSignIn);
        SignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = Username.getText().toString().trim();
                final String paswd = Password.getText().toString().trim();
                if (checkFieldsInput()) {
                    signin(email, paswd);
                }
            }
        });

        progressBar = findViewById(R.id.progress_circular);
    }

    private boolean checkFieldsInput() {
        String user = Username.getText().toString().trim();
        String paswd = Password.getText().toString().trim();

        if (user.isEmpty()) {
            Username.setError("Email is required");
            Username.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
            Username.setError("Enter a valid email address");
            Username.requestFocus();
            return false;
        } else if (paswd.isEmpty()) {
            Password.setError("Password is required");
            Password.requestFocus();
            return false;
        } else {
            return true;
        }
    }



    private void signin(String email, String password) {
        if(progressBar.getVisibility()==View.GONE){
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Log.d("LoginPage", "SUCCESS");
                                Intent intent = new Intent(Login.this, Dashboard.class);
                                startActivity(intent);
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Log.w("LoginPage", "LOGIN FAILED!", task.getException());
                                Toast.makeText(getApplicationContext(), "LOGIN FAILED!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    public void onClick(View view) {
        Intent intent = new Intent(Login.this, CreateAccount.class);
        startActivity(intent);
    }
}
