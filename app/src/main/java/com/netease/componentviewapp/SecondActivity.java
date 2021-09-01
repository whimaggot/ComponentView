package com.netease.componentviewapp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    public String data = "222";

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.tv_activity_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirstFragment firstFragment = new FirstFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.tttttt,firstFragment).commitAllowingStateLoss();
//                getSupportFragmentManager().beginTransaction().show(firstFragment).commitNowAllowingStateLoss();
            }
        });
    }
}
