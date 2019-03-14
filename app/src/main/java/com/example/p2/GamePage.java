package com.example.p2;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class GamePage extends AppCompatActivity {
    private GameView game_view;
    private Handler hand = new Handler();
    private final static int time_space = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        game_view = new GameView(this);
        setContentView(game_view);
        game_view.setBackground(getResources().getDrawable(R.drawable.bg_1));

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                hand.post(new Runnable() {
                    @Override
                    public void run() {
                        game_view.invalidate();
                    }
                });
            }
        }, 0, time_space);
    }
}
