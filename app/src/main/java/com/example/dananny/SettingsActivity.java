package com.example.dananny;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    private ArrayList<SettingsPOJO> mArrayList = new ArrayList<>();
    private CustomSettingsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbarSetup();
        recyclerViewSetup();
        prepareData();

    }

    //Method that displays back button in toolbar and ends this activity when button is clicked.
    public void toolbarSetup() {
        Toolbar mToolbar = findViewById(R.id.settingsToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Your code
                finish();
            }
        });
    }




    //Method that adds data into object array list type SettingsPOJO and display it in recycler view.
    private void prepareData() {
        SettingsPOJO settings = null;
        settings = new SettingsPOJO("Example 1", "Sublabel 1");
        mArrayList.add(settings);
        settings = new SettingsPOJO("Example 2", "Sublabel 2");
        mArrayList.add(settings);
        mAdapter.notifyDataSetChanged();
    }



    //Method that find recycler view by the id and displays it.
    private void recyclerViewSetup() {
        RecyclerView mRecyclerView1;
        mRecyclerView1 = findViewById(R.id.settingsRecyclerView);
        mAdapter = new CustomSettingsAdapter(mArrayList, new OnSettingsClickListener() {
            @Override
            public void onSettingsViewItemClicked(int position, int id) {
                switch (position) {
                    case 0:
                        Toast.makeText(SettingsActivity.this, "0 Selected", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(SettingsActivity.this, "1 Selected", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(SettingsActivity.this, "2 Selected", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(SettingsActivity.this, "Sign Out Selected", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        mRecyclerView1.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView1.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView1.addItemDecoration(new DividerItemDecoration(SettingsActivity.this, LinearLayoutManager.VERTICAL));
        mRecyclerView1.setAdapter(mAdapter);
    }
}
