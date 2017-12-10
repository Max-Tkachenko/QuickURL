package com.example.max.quickurl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        final Intent intent = new Intent(this, MainActivity.class);

        Thread splash_time = new Thread() {

            public void run() {
                try {
                    int SplashTimer = 0;
                    while (SplashTimer < 3000) {
                        sleep(100);
                        SplashTimer = SplashTimer + 100;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                    startActivity(intent);
                }

            }
        };
        splash_time.start();
    }
}
