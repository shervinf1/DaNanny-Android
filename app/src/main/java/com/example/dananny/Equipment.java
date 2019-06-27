package com.example.dananny;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Equipment extends AppCompatActivity {

    LinearLayout listView;
    private static final String TAG = "Equipment";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);

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

                            Log.d(TAG, d.getName());
                            Log.d(TAG, String.valueOf(d.getGpio()));
                            Log.d(TAG, d.getStatus());

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

            LinearLayout.LayoutParams switchParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1f);

            TextView textView = new TextView(getApplicationContext());
            textView.setText(d.getName());
            textView.setLayoutParams(textParam);

            Switch state = new Switch(getApplicationContext());
            state.setChecked(d.getStatus().equals("ON"));
            state.setLayoutParams(switchParam);

            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setWeightSum(3f);

            layout.addView(textView);
            layout.addView(state);

            listView.addView(layout);
        }
    }
}
