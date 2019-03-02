package com.example.p2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OverPage extends AppCompatActivity {
    private Button play_again;
    private TextView player_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over_page);

        play_again = (Button) findViewById(R.id.game_over_button);
        player_score = (TextView) findViewById(R.id.play_score) ;

        play_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent game = new Intent(OverPage.this, GamePage.class);
                startActivity(game);
            }
        });

        player_score.setText("Your Score: " + getIntent().getExtras().get("score").toString());
    }
}
