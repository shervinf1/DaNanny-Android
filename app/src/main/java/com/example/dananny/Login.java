package com.example.dananny;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    EditText user;
    EditText pass;
    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = findViewById(R.id.editText);
        pass = findViewById(R.id.editText2);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = user.getText().toString();
                String passInput = pass.getText().toString();

                if(userInput=="user@email.com" && passInput=="1234"){
                    Intent intent = new Intent(Login.this, Dashboard.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(Login.this, Dashboard.class);
                    startActivity(intent);
                }
            }
        });
    }
}
