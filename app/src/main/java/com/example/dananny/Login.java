package com.example.dananny;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
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

public class Login extends AppCompatActivity {

    EditText Username;
    EditText Password;
    TextView Switcher;
    Button SignButton;
    final String haveAccountString = "Already have an account?";
    final String noAccountString = "Don't have an account?";
    private boolean isNewUser = false;
    private static final String TAG = "Login";
    private FirebaseAuth mAuth;

    @Override
    public void onStart(){
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();


        Username = findViewById(R.id.editTextEmail);
        Password = findViewById(R.id.editTextPassword);
        Switcher = findViewById(R.id.credentialSwitch);


        SignButton = findViewById(R.id.BtnSignIn);
        SignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = Username.getText().toString().trim();
                final String paswd = Password.getText().toString().trim();
                if(checkFieldsInput()){
                    if(isNewUser){
                        createAccount(email,paswd);
                    } else {
                        signin(email,paswd);
                    }
                }
            }
        });
    }

    private boolean checkFieldsInput(){
        String user = Username.getText().toString().trim();
        String paswd = Password.getText().toString().trim();

        if(user.isEmpty()){
            Username.setError("Email is required");
            Username.requestFocus();
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(user).matches()){
            Username.setError("Enter a valid email address");
            Username.requestFocus();
            return false;
        }
        else if(paswd.isEmpty()){
            Password.setError("Password is required");
            Password.requestFocus();
            return false;
        }
        else{
            return true;
        }
    }

    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("LoginPage", "SUCCESS");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else{
                            Log.w("LoginPage", "LOGIN FAILED!", task.getException());
                            Toast.makeText(getApplicationContext(), "LOGIN FAILED!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signin(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("LoginPage", "SUCCESS");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else{
                           Log.w("LoginPage", "LOGIN FAILED!", task.getException());
                            Toast.makeText(getApplicationContext(), "LOGIN FAILED!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onClick(View view) {
        /*
        Definitions:
        haveAccountString = "Already have an account?";
        noAccountString = "Don't have an account?";
         */

        if(Switcher.getText().toString()== noAccountString){
            //Since tapped, is a new user to Sign UP
            isNewUser = true;
            SignButton.setText("Sign Up");

            //Change text to go back if needed
            Switcher.setText(haveAccountString);
        }
        else{
            //Since tapped, is a new user to Sign IN
            isNewUser = false;
            SignButton.setText("Sign In");

            //Change text to go back if needed
            Switcher.setText(noAccountString);
        }
    }
}
