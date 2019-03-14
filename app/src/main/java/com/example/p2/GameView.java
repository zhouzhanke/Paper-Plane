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

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class GameView extends View {
    private int canvas_width, canvas_height;
    private boolean touched = false;
    private int touch_x, touch_y;
    private int move_x, move_y;
    private int global_speed;
    private double distance;
    double angle_rad;

    // score_board
    private int score;

    // score
    private Paint score_board = new Paint();

    // plane (player)
    private int plane_stage;
    private int plane_stage_max = 2;
    private int plane_x, plane_y, plane_s;
    private Bitmap plane[] = new Bitmap[plane_stage_max];

    // butterfly blue
    private int butterfly_blue_x, butterfly_blue_y, butterfly_blue_s;
    boolean butterfly_blue_t = false;
    private Bitmap butterfly_blue;

    // butterfly red
    private int butterfly_red_x, butterfly_red_y, butterfly_red_s;
    boolean butterfly_red_t = false;
    private Bitmap butterfly_red;

    // bonus box
    private int box_x, box_y, box_s;
    private Bitmap box;


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

        // global attr
        global_speed = 0;
        distance = 0;

        // score_board board
        score_board.setColor(Color.rgb(30, 210, 30));
        score_board.setTextSize(100);
        score_board.setAntiAlias(true);
        score = 0;

        // plane
//        plane_stage = plane_stage_max - 1;
        plane_stage = 0;
        plane[0] = BitmapFactory.decodeResource(getResources(), R.drawable.plane_0);
        plane[1] = BitmapFactory.decodeResource(getResources(), R.drawable.plane_1);
        plane_x = screen_width / 2 - plane[1].getWidth() / 2;
        plane_y = (int) (screen_height - plane[1].getHeight() * 1.5);
        plane_s = 20;

        // butterfly_red
        butterfly_blue = BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_blue);
        butterfly_blue_s = 15;

        // butterfly_red
        butterfly_red = BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_red);
        butterfly_red_s = 15;

        // box
        box = BitmapFactory.decodeResource(getResources(), R.drawable.box_shield);
        box_s = 5;

        touch_x = 0;
        touch_y = 0;
        move_x = 0;
        move_y = 0;
        angle_rad = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas_width = canvas.getWidth();
//        canvas_height = canvas.getHeight();
        canvas_width = getWidth();
        canvas_height = getHeight();

        // plane boundary
        int min_plane_x = 0;
        int max_plane_x = canvas_width - plane[plane_stage].getWidth();
        int min_plane_y = 0;
        int max_plane_y = canvas_height - plane[plane_stage].getHeight();

//        // plane motion 1
//        plane_x = plane_x + plane_s;
//
//        // touched dirction on touch
//        if (touched) {
//            touched = false;
//            plane_s = plane_s * (-1);
//        }

        // plane motion 2
        if (touched) {
            touched = false;
            distance = Math.sqrt(Math.pow(touch_x - plane_x, 2) + Math.pow(touch_y - plane_y, 2));
            angle_rad = Math.atan2((double) touch_y - (double) plane_y, (double) touch_x - (double) plane_x);
        }

        if (distance > 0) {
            int move = plane_s;
            distance -= move;
            if (distance < 0) {
                move = (int) distance;
                distance = 0;
            }

            plane_x += (int) Math.floor(Math.cos(angle_rad) * move);
            plane_y += (int) Math.floor(Math.sin(angle_rad) * move);
        }

        // border
        if (plane_x < min_plane_x) {
            plane_x = min_plane_x;
        }
        if (plane_x > max_plane_x) {
            plane_x = max_plane_x;
        }
        if (plane_y < min_plane_y) {
            plane_y = min_plane_y;
        }
        if (plane_y > max_plane_y) {
            plane_y = max_plane_y;
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

        // butterfly_blue hit check
        for (int i = butterfly_blue_x; i <= butterfly_blue.getWidth() + butterfly_blue_x; i++) {
            for (int j = butterfly_blue_y; j <= butterfly_blue.getHeight() + butterfly_blue_y; j++) {
                if (hit_check(i, j)) {
                    butterfly_blue_y = canvas_height + 100;
                    plane_stage--;

                    if (plane_stage == -1) {
                        plane_stage = plane_stage_max - 1;
                        // game over switch page
//                Toast.makeText(getContext(), "Game Over", Toast.LENGTH_LONG).show();

                        Intent game_over = new Intent(getContext(), OverPage.class);
                        game_over.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        game_over.putExtra("score", score);
                        getContext().startActivity(game_over);

                    }

                    i = max_plane_x * 2;
                    break;
                }
            }
        }

        // butterfly_red hit check
        for (int i = butterfly_red_x; i <= butterfly_red.getWidth() + butterfly_red_x; i++) {
            for (int j = butterfly_red_y; j <= butterfly_red.getHeight() + butterfly_red_y; j++) {
                if (hit_check(i, j)) {
                    butterfly_red_y = canvas_height + 100;
                    plane_stage--;

                    if (plane_stage == -1) {
                        plane_stage = plane_stage_max - 1;
                        // game over switch page
//                Toast.makeText(getContext(), "Game Over", Toast.LENGTH_LONG).show();

                        Intent game_over = new Intent(getContext(), OverPage.class);
                        game_over.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        game_over.putExtra("score", score);
                        getContext().startActivity(game_over);

                    }

                    i = max_plane_x * 2;
                    break;
                }
            }
        }

        // box hit check
        for (int i = box_x; i <= box.getWidth() + box_x; i++) {
            for (int j = box_y; j <= box.getHeight() + box_y; j++) {
                if (hit_check(i, j)) {
                    box_y = canvas_height + 100;
                    if (plane_stage == 0) {
                        plane_stage++;
                    }

                    i = max_plane_x * 2;
                    break;
                }
            }
        }

        // generate butterfly_blue
        butterfly_blue_y = butterfly_blue_y + butterfly_blue_s;
        if (butterfly_blue_y > canvas_height) {
            butterfly_blue_y = -butterfly_blue.getHeight();
            butterfly_blue_x = random(butterfly_blue.getWidth());
        }
        canvas.drawBitmap(butterfly_blue, butterfly_blue_x, butterfly_blue_y, null);

        // generate butterfly_red
        butterfly_red_y = butterfly_red_y + butterfly_red_s;
        if (butterfly_red_t) {
            butterfly_red_x = butterfly_red_x + 20;
            if (butterfly_red_x >= canvas_width - butterfly_red.getWidth()) {
                butterfly_red_t = false;
            }
        } else {
            butterfly_red_x = butterfly_red_x - 20;
            if (butterfly_red_x <= min_plane_x) {
                butterfly_red_t = true;
            }
        }
        if (butterfly_red_y > canvas_height) {
            butterfly_red_y = -butterfly_red.getHeight();
            butterfly_red_x = random(butterfly_red.getWidth());
        }
        canvas.drawBitmap(butterfly_red, butterfly_red_x, butterfly_red_y, null);

        // generate box
        box_y = box_y + box_s;
        if ((score % 1000) == 0) {
            box_y = -box.getHeight();
            box_x = random(box.getWidth());
        }
        canvas.drawBitmap(box, box_x, box_y, null);

        // draw score_board board
        score = score + 1 + global_speed;
        canvas.drawText("Score: " + score, 10, 100, score_board);
    }

    public boolean hit_check(int x, int y) {
        return plane_x < x && x < (plane_x + plane[1].getWidth()) && plane_y < y && y < (plane_y + plane[1].getHeight());
    }

    public int random(int margin) {
//        int min_plane_x = 0;
//        int max_plane_x = canvas_width - plane[plane_stage].getWidth();
        int res = (int) Math.floor(Math.random() * canvas_width - margin);
        if (res < 0) {
            res = 0;
        }

        return res;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touched = true;
            touch_x  = (int) event.getX();
            touch_y = (int) event.getY();
        }
        return true;
    }
}
