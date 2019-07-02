package com.example.dananny;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewDevice extends AppCompatActivity {

    TextInputLayout nameText;
    EditText gpioText;
    TextInputLayout roomText;
    TextView stateText;
    Switch stateSwitch;
    Button cancelButton;
    Button saveButton;
    private static final String TAG = "NewDevice";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);

        //Assign Visual Elements
        nameText = findViewById(R.id.textInputLayout);
        gpioText = findViewById(R.id.editText3);
        roomText = findViewById(R.id.textInputLayout2);
        stateText = findViewById(R.id.textView3);
        stateSwitch = findViewById(R.id.switch1);
        cancelButton = findViewById(R.id.button2);
        saveButton = findViewById(R.id.button4);

        //Switch State Change
        stateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                stateText.setText((isChecked) ? "State: ON" : "State: OFF");
            }
        });


        //Buttons functionality
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> device = new HashMap<>();
                device.put("gpio", Integer.parseInt(gpioText.getText().toString()));
                device.put("room", roomText.getEditText().getText().toString());
                device.put("status", (stateSwitch.isChecked()) ? "ON" : "OFF");

                String name = nameText.getEditText().getText().toString();
                Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();

                db.collection("own").document(name)
                        .set(device)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                finish();
                            }
                        });


            }
        });
    }


}
