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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity {
    EditText name;
    EditText email;
    EditText password;
    EditText rePassword;
    Button signIn;
    TextView havaAccountText;
    RadioGroup radioGridGroup;
    RadioButton radioButton;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getSupportActionBar().hide();
        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        rePassword = findViewById(R.id.editTextConfirmPassword);
        signIn = findViewById(R.id.BtnSignIn);
        radioGridGroup = findViewById(R.id.radioGridGroup);
        int selectedId = radioGridGroup.getCheckedRadioButtonId();
        radioButton = findViewById(selectedId);






        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mail = email.getText().toString().trim();
                final String paswd = password.getText().toString().trim();
                int selectedId = radioGridGroup.getCheckedRadioButtonId();
                radioButton = findViewById(selectedId);
                if(checkFieldsInput()){
                    createAccount(mail, paswd);
                }

            }
        });
        havaAccountText = findViewById(R.id.credentialSwitch);

        mAuth = FirebaseAuth.getInstance();
    }



    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("LoginPage", "SUCCESS");
                            accountSetup();
                        } else {
                            Log.w("LoginPage", "LOGIN FAILED!", task.getException());
                            Toast.makeText(getApplicationContext(), "LOGIN FAILED!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private boolean checkFieldsInput() {
        String user = name.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String paswd = password.getText().toString().trim();
        String repeat = rePassword.getText().toString().trim();

        if(user.isEmpty()){
            name.setError("Name is required");
            name.requestFocus();
        }else if (mail.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            email.setError("Enter a valid email address");
            email.requestFocus();
            return false;
        } else if (paswd.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return false;
        } else if (repeat.isEmpty()) {
            rePassword.setError("Confirm your Password");
            rePassword.requestFocus();
            return false;
        } else if(!paswd.equals(repeat)){
            rePassword.setError("Password does not match");
            rePassword.setText("");
            rePassword.requestFocus();
            return false;
        } else {
            return true;
        }

        return false;
    }

    private void accountSetup(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("name", name.getText().toString().trim());
        user.put("email", email.getText().toString().trim());

        db.collection("Users").document(mAuth.getCurrentUser().getUid())
                .set(user, SetOptions.merge());

        Intent intent = new Intent(CreateAccount.this, Dashboard.class);
        startActivity(intent);
    }

    public void onClick(View view) {
        Intent intent = new Intent(CreateAccount.this, Login.class);
        startActivity(intent);
    }

}
