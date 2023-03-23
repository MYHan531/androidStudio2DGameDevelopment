package com.example.androidstudio2dgamedevelopment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.androidstudio2dgamedevelopment.object.Circle;
import com.example.androidstudio2dgamedevelopment.object.Enemy;
import com.example.androidstudio2dgamedevelopment.object.Player;
import com.example.androidstudio2dgamedevelopment.object.Spell;

/**
 * Game manages all objects in the game and is responsible for updating all states and
 * render all objects to the screen
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback{
    private final Joystick joystick;
    private GameLoop gameLoop;
    private Context context;
    private final Player player;
    private List<Enemy> enemyList = new ArrayList<Enemy>();
    private List<Spell> spellList = new ArrayList<Spell>();
    private int joystickPointerId = 0;
    private int numberOfSpellsToCast = 0;

    public Game(Context context) {
        super(context);

        //Get surface holder and add callback
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        this.context = context;
        gameLoop = new GameLoop(this, surfaceHolder);

        //Initialize game objects
        joystick = new Joystick(275, 700, 70, 40);
        player = new Player(getContext(), joystick, 2*500, 500, 30);
        //enemy = new Enemy(getContext(), player, 500, 200, 30);


        setFocusable(true);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //handle touch event actions
        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: case MotionEvent.ACTION_POINTER_DOWN:
                if (joystick.getIsPressed()) {
                    //Joystick was pressed before this event -> cast spell
                    numberOfSpellsToCast++;
                } else if(joystick.isPressed((double) event.getX(), (double)event.getY())) {
                    //Joystick is pressed in this event -> setIsPressed(true)
                    joystickPointerId = event.getPointerId(event.getActionIndex());
                    joystick.setIsPressed(true);
                }else {
                    //JoyStick was not pressed previously, and is not pressed in this event -> cast new spell
                    spellList.add(new Spell(getContext(), player));
                }

                return true;
            case MotionEvent.ACTION_MOVE:
                //Joystick was pressed previously and is now moved
                if(joystick.getIsPressed()) {
                    joystick.setActuator((double) event.getX(), (double)event.getY());
                }

                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if(joystickPointerId == event.getPointerId(event.getAction())) {
                    //Joystick is let go of -> setIsPressed(false) and resetActuator
                    joystick.setIsPressed(false);
                    joystick.resetActuator();
                }
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
        for (Enemy enemy: enemyList) {
            enemy.draw(canvas);
        }
        for (Spell spell: spellList) {
            spell.draw(canvas);
        }
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
        player.update();

        //Spawn enemy if it is time to spawn new enemies
        if (Enemy.readyToSpawn()) {
            enemyList.add(new Enemy(getContext(), player));
        }

        //update state of each enemy

        for (Enemy enemy : enemyList) {
            enemy.update();
        }
        while(numberOfSpellsToCast > 0) {
            spellList.add(new Spell(getContext(), player));
            numberOfSpellsToCast--;
        }
        //update state of each spell
        for (Spell spell : spellList) {
            spell.update();
        }

        //iterate through enemyList and check for collision between each enemy and the player and all spells
        Iterator<Enemy> iteratorEnemy = enemyList.iterator();
        while(iteratorEnemy.hasNext()) {
            Circle enemy = iteratorEnemy.next();
            if(Circle.isColliding(enemy, player)) {
                //Remove enemy if it collides with the player
                iteratorEnemy.remove();
                continue;
            }
            Iterator<Spell> iteratorSpell = spellList.iterator();
            while(iteratorSpell.hasNext()) {
                Circle spell = iteratorSpell.next();
                //remove spell if it collides with an enemy
                if(Circle.isColliding(spell, enemy)){
                    iteratorSpell.remove();
                    iteratorEnemy.remove();
                    break;
                }
            }
        }
    }


}
