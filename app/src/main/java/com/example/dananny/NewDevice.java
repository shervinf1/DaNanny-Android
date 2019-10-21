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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewDevice extends AppCompatActivity {

    TextInputLayout nameText;
    EditText gpioText;
    TextInputLayout roomText;
    EditText wattText;
    TextView stateText;
    Switch stateSwitch;
    Button cancelButton;
    Button saveButton;
    EditText threshold;
    private static final String TAG = "NewDevice";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userID = firebaseAuth.getCurrentUser().getUid();
    final DocumentReference userDoc = db.collection("Users").document(userID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);


        //Assign Visual Elements
        nameText = findViewById(R.id.textInputLayout);
        gpioText = findViewById(R.id.editText3);
        roomText = findViewById(R.id.textInputLayout2);
        wattText = findViewById(R.id.editText4);
        stateText = findViewById(R.id.textView3);
        stateSwitch = findViewById(R.id.switch1);
        cancelButton = findViewById(R.id.button2);
        saveButton = findViewById(R.id.button4);
        threshold=findViewById(R.id.editText5);

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
                device.put("watt",Integer.parseInt(wattText.getText().toString()));
                device.put("threshold",Integer.parseInt(threshold.getText().toString()));
                device.put("name",nameText.getEditText().getText().toString());
                device.put("userID",userDoc);

                String name = nameText.getEditText().getText().toString();
                Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();

                db.collection("Devices").document()
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
