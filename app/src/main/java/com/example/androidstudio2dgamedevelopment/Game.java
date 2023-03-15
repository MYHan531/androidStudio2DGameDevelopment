package com.example.androidstudio2dgamedevelopment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

/**
 * Game manages all objects in the game and is responsible for updating all states and
 * render all objects to the screen
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback{
    private final Joystick joystick;
    private GameLoop gameLoop;
    private Context context;
    private final Player player;

    public Game(Context context) {
        super(context);

        //Get surface holder and add callback
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        this.context = context;
        gameLoop = new GameLoop(this, surfaceHolder);

        //Initialize game objects
        joystick = new Joystick(275, 700, 70, 40);
        player = new Player(getContext(), 2*500, 500, 30);


        setFocusable(true);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //handle touch event actions
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(joystick.isPressed((double) event.getX(), (double)event.getY())) {
                    joystick.setIsPressed(true);
                }
                //player.setPosition((double) event.getX(), (double)event.getY());
                return true;
            case MotionEvent.ACTION_MOVE:
                if(joystick.getIsPressed()) {
                    joystick.setActuator((double) event.getX(), (double)event.getY());
                }
                //player.setPosition((double) event.getX(), (double)event.getY());
                return true;
            case MotionEvent.ACTION_UP:
                joystick.setIsPressed(false);
                joystick.resetActuator();
                return true;

        }
        return super.onTouchEvent(event);
    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawUPS(canvas);
        drawFPS(canvas);

        joystick.draw(canvas);
        player.draw(canvas);
    }

    public void drawUPS(Canvas canvas) {
        String averageUPS = Double.toString(gameLoop.getAverageUPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("UPS: " + averageUPS, 100,100, paint);
    }
    public void drawFPS(Canvas canvas) {
        String averageFPS = Double.toString(gameLoop.getAverageFPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("FPS: " + averageFPS, 100,200, paint);
    }

    public void update() {
        //update game state
        joystick.update();
        player.update(joystick);

    }
}