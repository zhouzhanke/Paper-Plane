package com.example.p2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
    double angle_rad;
    private int canvas_width, canvas_height;
    private boolean touched = false;
    private int touch_x, touch_y;
    private boolean touch_start = false;
    private int move_x, move_y;
    private int global_speed;
    private double distance;
    // start message
    private String msg_1 = "Tap to Start";
    private Paint msg_start = new Paint();
    private int msg_start_x, msg_start_y, msg_start_s;

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
//    private int butterfly_blue_x, butterfly_blue_y, butterfly_blue_s;
//    private Bitmap butterfly_blue;
    private int butterfly_blue_limit = 1;
    private int butterfly_blue_max = 3;
    private int butterfly_blue_x[] = new int[butterfly_blue_max];
    private int butterfly_blue_y[] = new int[butterfly_blue_max];
    private int butterfly_blue_s[] = new int[butterfly_blue_max];
    private Bitmap butterfly_blue[] = new Bitmap[butterfly_blue_max];

    // butterfly red
//    private int butterfly_red_x, butterfly_red_y, butterfly_red_s;
//    boolean butterfly_red_t = false;
//    private Bitmap butterfly_red;

    private int butterfly_red_limit = 0;
    private int butterfly_red_max = 3;
    private boolean butterfly_red_t[] = new boolean[butterfly_red_max];
    private int butterfly_red_x[] = new int[butterfly_red_max];
    private int butterfly_red_y[] = new int[butterfly_red_max];
    private int butterfly_red_s[] = new int[butterfly_red_max];
    private Bitmap butterfly_red[] = new Bitmap[butterfly_red_max];

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

        // start message
        msg_start.setColor(Color.rgb(30, 30, 210));
        msg_start.setTextSize(100);
        msg_start.setAntiAlias(true);
        Rect bound = new Rect();
        msg_start.getTextBounds(msg_1, 0, msg_1.length(), bound);
        msg_start_x = (screen_width - bound.width()) / 2;
        msg_start_y = 500;
        msg_start_s = 10;

        // score_board board
        score_board.setColor(Color.rgb(30, 210, 30));
        score_board.setTextSize(100);
        score_board.setAntiAlias(true);
        score = 0;

        // plane
        plane_stage = 0;
        plane[0] = BitmapFactory.decodeResource(getResources(), R.drawable.plane_0);
        plane[1] = BitmapFactory.decodeResource(getResources(), R.drawable.plane_1);
//        plane_x = screen_width / 2 - plane[1].getWidth() / 2;
//        plane_y = (int) (screen_height - plane[1].getHeight() * 1.5);
        plane_x = screen_width / 2;
        plane_y = screen_height - plane[1].getHeight();
        plane_s = 20;

        // TODO: texture should be changed later on
        // butterfly_red
        butterfly_blue[0] = BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_blue);
        butterfly_blue[1] = BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_blue);
        butterfly_blue[2] = BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_blue);
        butterfly_blue_s[0] = 15;
        butterfly_blue_s[1] = 15;
        butterfly_blue_s[2] = 15;

        // butterfly_red
        butterfly_red[0] = BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_red);
        butterfly_red[1] = BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_red);
        butterfly_red[2] = BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_red);
        butterfly_red_s[0] = 15;
        butterfly_red_s[1] = 15;
        butterfly_red_s[2] = 15;
        for (int i = 0; i < butterfly_red_max; i++) {
            butterfly_red_t[i] = false;
        }

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

        canvas.drawBitmap(plane[plane_stage], plane_x - plane[plane_stage].getWidth() / 2,
                plane_y - plane[plane_stage].getHeight() / 2, null);
        if (!touch_start) {
            canvas.drawText("Tap to Start", msg_start_x, msg_start_y, msg_start);
            return;
        }
        if (msg_start_y < canvas_height * 2) {
            msg_start_y += msg_start_s;
            canvas.drawText(msg_1, msg_start_x, msg_start_y, msg_start);
        }

        // plane motion
        if (touched) {
            touched = false;
            // adjust touched point to center of plane
//            touch_x -= plane[plane_stage].getWidth() / 2;
//            touch_y -= plane[plane_stage].getHeight() / 2;

            distance = Math.sqrt(Math.pow(touch_x - plane_x, 2) + Math.pow(touch_y - plane_y, 2));
            angle_rad = Math.atan2((double) touch_y - (double) plane_y, (double) touch_x - (double) plane_x);
        }

        if (distance > 0) {
            int move = plane_s;
            distance -= move;
            if (distance < 0) {
                move = (int) Math.abs(distance);
                distance = 0;
            }

            plane_x += (int) Math.floor(Math.cos(angle_rad) * move);
            plane_y += (int) Math.floor(Math.sin(angle_rad) * move);
        }

        // border
        if (plane_x < min_plane_x + plane[plane_stage].getWidth() / 2) {
            plane_x = min_plane_x + plane[plane_stage].getWidth() / 2;
        }
        if (plane_x > max_plane_x + plane[plane_stage].getWidth() / 2) {
            plane_x = max_plane_x + plane[plane_stage].getWidth() / 2;
        }
        if (plane_y < min_plane_y + plane[plane_stage].getHeight() / 2) {
            plane_y = min_plane_y + plane[plane_stage].getHeight() / 2;
        }
        if (plane_y > max_plane_y + plane[plane_stage].getHeight() / 2) {
            plane_y = max_plane_y + plane[plane_stage].getHeight() / 2;
        }

        // draw plane
//        canvas.drawBitmap(plane[plane_stage], plane_x, plane_y, null);

        // butterfly_blue hit check
        for (int a = 0; a < butterfly_blue_limit; a++) {
            for (int i = butterfly_blue_x[a]; i <= butterfly_blue[a].getWidth() + butterfly_blue_x[a]; i++) {
                for (int j = butterfly_blue_y[a]; j <= butterfly_blue[a].getHeight() + butterfly_blue_y[a]; j++) {
                    if (hit_check(i, j)) {
                        butterfly_blue_y[a] = canvas_height + 100;
                        plane_stage--;

                        if (plane_stage == -1) {
                            plane_stage = plane_stage_max - 1;
                            // game over, switch page
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
        }

        // butterfly_red hit check
        for (int a = 0; a < butterfly_red_limit; a++) {
            for (int i = butterfly_red_x[a]; i <= butterfly_red[a].getWidth() + butterfly_red_x[a]; i++) {
                for (int j = butterfly_red_y[a]; j <= butterfly_red[a].getHeight() + butterfly_red_y[a]; j++) {
                    if (hit_check(i, j)) {
                        butterfly_red_y[a] = canvas_height + 100;
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
        if (score == 500 || score == 1000 || score == 4000) {
            butterfly_blue_limit++;
        }
        if (butterfly_blue_limit > butterfly_blue_limit) {
            butterfly_blue_limit = butterfly_blue_max;
        }

        for (int i = 0; i < butterfly_blue_limit; i++) {
            butterfly_blue_y[i] = butterfly_blue_y[i] + butterfly_blue_s[i];
        }

        for (int i = 0; i < butterfly_blue_limit; i++) {
            if (butterfly_blue_y[i] > canvas_height) {
                butterfly_blue_y[i] = -butterfly_blue[i].getHeight();
                butterfly_blue_x[i] = random(butterfly_blue[i].getWidth());
                butterfly_blue_s[i] = (int) Math.floor(Math.random() * 10 + 10);
            }
        }
        for (int i = 0; i < butterfly_blue_limit; i++) {
            canvas.drawBitmap(butterfly_blue[i], butterfly_blue_x[i], butterfly_blue_y[i], null);
        }

        // generate butterfly_red
        if (score == 2000 || score == 3000 || score == 5000) {
            butterfly_red_limit++;
        }
        if (butterfly_red_limit > butterfly_red_limit) {
            butterfly_red_limit = butterfly_red_max;
        }

        for (int i = 0; i < butterfly_red_limit; i++) {
            butterfly_red_y[i] = butterfly_red_y[i] + butterfly_red_s[i];
        }
        for (int i = 0; i < butterfly_red_limit; i++) {
            if (butterfly_red_t[i]) {
                butterfly_red_x[i] = butterfly_red_x[i] + 20;
                if (butterfly_red_x[i] >= canvas_width - butterfly_red[i].getWidth()) {
                    butterfly_red_t[i] = false;
                }
            } else {
                butterfly_red_x[i] = butterfly_red_x[i] - 20;
                if (butterfly_red_x[i] <= min_plane_x) {
                    butterfly_red_t[i] = true;
                }
            }
            if (butterfly_red_y[i] > canvas_height) {
                butterfly_red_y[i] = -butterfly_red[i].getHeight();
                butterfly_red_x[i] = random(butterfly_red[i].getWidth());
                butterfly_red_s[i] = (int) Math.floor(Math.random() * 10 + 10);
            }
        }
        for (int i = 0; i < butterfly_red_limit; i++) {
            canvas.drawBitmap(butterfly_red[i], butterfly_red_x[i], butterfly_red_y[i], null);
        }

        // generate bonus box
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
//        return plane_x < x && x < (plane_x + plane[1].getWidth()) && plane_y < y && y < (plane_y + plane[1].getHeight());
        boolean res = false;
        switch (plane_stage) {
            case 0:
                // triangle
                for (int i = 1; i <= 25; i++) {
                    res = (x > (plane_x - i))
                            && (x < (plane_x + i))
                            && (y > (plane_y - (plane[plane_stage].getHeight() / 2)))
                            && (y < ((plane_y + (plane[plane_stage].getHeight() / 2)) - (4 * i)));
                    if (res) {
                        break;
                    }
                }
                break;

            case 1:
                // square
                res = x > (plane_x - plane[plane_stage].getWidth() / 2)
                        && x < (plane_x + plane[plane_stage].getWidth() / 2)
                        && y > (plane_y - plane[plane_stage].getHeight() / 2)
                        && y < (plane_y + plane[plane_stage].getHeight() / 2);
                break;
        }
        return res;
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
            touch_start = true;
            touched = true;
            touch_x = (int) event.getX();
            touch_y = (int) event.getY();
        }
        return true;
    }
}
