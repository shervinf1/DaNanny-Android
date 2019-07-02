package com.example.dananny;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Equipment extends AppCompatActivity {

    LinearLayout listView;
    Button button;
    private static final String TAG = "Equipment";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);

        button = findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Equipment.this, NewDevice.class);
                startActivity(intent);
            }
        });
        listView = findViewById(R.id.listMode);

        db.collection("own")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        ArrayList<Device> devices = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            Device d = doc.toObject(Device.class);
                            d.setName(doc.getId());
                            devices.add(d);
                        }
                        addDataToList(devices);
                    }
                });
    }

    private void addDataToList(ArrayList<Device> devices){
        listView.removeAllViews();
        for(Device d : devices){
            LinearLayout.LayoutParams textParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,2f);

            LinearLayout.LayoutParams oneSpaceParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1f);

            TextView textView = new TextView(getApplicationContext());
            textView.setText(d.getName());
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(oneSpaceParam);

            final Switch state = new Switch(getApplicationContext());
            state.setId(d.getGpio());
            state.setChecked(d.getStatus().equals("ON"));
            state.setLayoutParams(oneSpaceParam);
            state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                    final CollectionReference documents = db.collection("own");
                    documents.whereEqualTo("gpio", state.getId())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<Object, String> map = new HashMap<>();
                                    map.put("status", (isChecked)?"ON":"OFF");
                                    documents.document(document.getId()).set(map, SetOptions.merge());
                                }
                            }
                        }
                    });

                }
            });

            Button deleteButton = new Button(getApplicationContext());
            deleteButton.setText("Delete");
            deleteButton.setBackgroundColor(Color.TRANSPARENT);
            deleteButton.setTextColor(Color.RED);
            deleteButton.setId(d.getGpio());
            deleteButton.setLayoutParams(oneSpaceParam);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CollectionReference documents = db.collection("own");
                    documents.whereEqualTo("gpio", state.getId())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    document.getReference().delete();
                                }
                            }
                        }
                    });
                }
            });

            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            //layout.setWeightSum(5f);

            layout.addView(textView);
            layout.addView(state);
            layout.addView(deleteButton);

            listView.addView(layout);
        }
    }
}
