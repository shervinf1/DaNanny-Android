package com.example.dananny;

import android.content.Intent;
import android.os.Bundle;

import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

public class OnboardActivity extends TutorialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_onboard);


        addFragment(new Step.Builder().setTitle("This is header")
                .setContent("This is content")
                .setBackgroundColor(R.color.colorWhite) // int background color
                .setDrawable(R.drawable.image) // int top drawable
                .setSummary("This is summary")
                .build());
        addFragment(new Step.Builder().setTitle("This is header 2")
                .setContent("This is content 2")
                .setBackgroundColor(R.color.colorPrimary) // int background color
                .setDrawable(R.drawable.image) // int top drawable
                .setSummary("This is summary 2")
                .build());
        addFragment(new Step.Builder().setTitle("This is header 3")
                .setContent("This is content 3")
                .setBackgroundColor(R.color.colorPrimary) // int background color
                .setDrawable(R.drawable.image) // int top drawable
                .setSummary("This is summary 3")
                .build());

    }

    @Override
    public void currentFragmentPosition(int position) {

    }
    @Override
    public void finishTutorial() {
        Intent intent = new Intent(OnboardActivity.this, Dashboard.class);
        startActivity(intent);
    }
}
