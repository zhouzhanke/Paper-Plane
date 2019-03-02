package com.example.p2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
    private int canvas_width, canvas_height;
    private boolean turn = false;
    private int global_speed;

    // score_board
    private int score;

    // plane (player)
    private int plane_stage;
    private int plane_stage_max = 2;
    private int plane_x, plane_y, plane_speed;
    private Bitmap plane[] = new Bitmap[plane_stage_max];

    // spark
    private int spark_x, spark_y, spark_speed;
    private Bitmap spark;

    private Paint score_board = new Paint();

    public GameView(Context context) {
        super(context);
        // get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int screen_height = displayMetrics.heightPixels;
        int screen_width = displayMetrics.widthPixels;

        global_speed = 0;
        // score_board board
        score_board.setColor(Color.rgb(30,210,30));
        score_board.setTextSize(100);
        score_board.setAntiAlias(true);
        score = 0;

        // plane
        plane_stage = plane_stage_max - 1;
        plane[0] = BitmapFactory.decodeResource(getResources(), R.drawable.plane_0);
        plane[1] = BitmapFactory.decodeResource(getResources(), R.drawable.plane_1);
        plane_x = screen_width/2 - plane[1].getWidth()/2;
        plane_y = (int) (screen_height - plane[1].getHeight() * 1.5);
        plane_speed = 20;

        // spark
        spark = BitmapFactory.decodeResource(getResources(), R.drawable.spark);
        spark_speed = 30;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas_width = canvas.getWidth();
        canvas_height = canvas.getHeight();

        // plane boundary
        int min_plane_x = 0;
        int max_plane_x = canvas_width - plane[plane_stage].getWidth();
        plane_x = plane_x + plane_speed;

        if (plane_x < min_plane_x) {
            plane_x = min_plane_x;
        }
        if (plane_x > max_plane_x) {
            plane_x = max_plane_x;
        }
        // turn on touch
        if (turn) {
            turn = false;
            plane_speed = plane_speed * (-1);
        }
        // draw plane
        switch (plane_stage) {
            case 0:
                break;

            case 1:
                break;

            default:
                break;
        }
        canvas.drawBitmap(plane[plane_stage], plane_x, plane_y, null);

        // spark
        spark_y = spark_y + spark_speed;
        // hit check
        if (hit_check(spark_x,spark_y)) {
            spark_y = canvas_height  * 2;
            plane_stage--;
            if (plane_stage == -1) {
                plane_stage = plane_stage_max - 1;
                // game over switch page
//                Toast.makeText(getContext(), "Game Over", Toast.LENGTH_LONG).show();

                Intent  game_over= new Intent(getContext(), OverPage.class);
                game_over.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                game_over.putExtra("score", score);
                getContext().startActivity(game_over);
            }

        }

        // generate spark
        if (spark_y > canvas_height) {
            spark_y  = -spark.getHeight();
            spark_x = (int) Math.floor(Math.random() * max_plane_x - min_plane_x) + min_plane_x;
        }
        canvas.drawBitmap(spark, spark_x, spark_y, null);


        // draw score_board board
        score = score + 1 + global_speed;
        canvas.drawText("Score: " + score, 10,100, score_board);
    }

    public boolean hit_check(int x, int y) {
        if (plane_x < x && x < (plane_x + plane[1].getWidth()) &&  plane_y < y && y < (plane_y + plane[1].getHeight())) {
            return true;
        }
        else  {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            turn = true;
        }
        return true;
    }
}
