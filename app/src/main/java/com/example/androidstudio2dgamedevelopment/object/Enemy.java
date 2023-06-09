package com.example.androidstudio2dgamedevelopment.object;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.androidstudio2dgamedevelopment.GameLoop;
import com.example.androidstudio2dgamedevelopment.R;

public class Enemy extends Circle {
    private static final double SPEED_PIXELS_PER_SECOND = Player.SPEED_PIXELS_PER_SECOND*0.6;
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private static double SPAWNS_PER_MINUTE = 20.0;
    private static double SPAWNS_PER_SECONDS = SPAWNS_PER_MINUTE/60.0;
    private static double UPDATES_PER_SPAWN = GameLoop.MAX_UPS/SPAWNS_PER_SECONDS;
    private static double updateUntilNextSpawn = UPDATES_PER_SPAWN;
    private final Player player;

    public Enemy (Context context, Player player, double positionX, double positionY, double radius) {
        super(
                context,
                ContextCompat.getColor(context, R.color.enemy),
                positionX,
                positionY,
                radius
        );
        this.player = player;
    }

    public Enemy(Context context, Player player) {
        super(
                context,
                ContextCompat.getColor(context, R.color.enemy),
                Math.random()*1000,
                Math.random()*1000,
                30
        );
        this.player = player;
    }

    /**
     * readyToSpawn checks if a new enemy should spawn, according to the decided number of spawns
     * per minute (See SPAWNS_PER_MINUTE at the top)
     * @return
     */
    public static boolean readyToSpawn() {
        if (updateUntilNextSpawn <= 0) {
            updateUntilNextSpawn += UPDATES_PER_SPAWN;
            return true;
        } else {
            updateUntilNextSpawn--;
            return false;
        }
        
    }

    public void update() {
        /**
         * Update velocity of the enemy so that the velocity is in the direction of the player
         */
        //Calculate vector from enemy to player (in x and y)
        double distanceToPlayerX = player.getPositionX() - positionX;
        double distanceToPlayerY = player.getPositionY() - positionY;

        //Calculate (absolute) distance between enemy (this) and player
        double distanceToPlayer = getDistanceBetweenObject(this, player);

        //calculate direction from enemy to player
        double directionX = distanceToPlayerX/distanceToPlayer;
        double directionY = distanceToPlayerY/distanceToPlayer;

        //set velocity in the direction to player
        if (distanceToPlayer > 0) { //avoid division by zero
            velocityX = directionX*MAX_SPEED;
            velocityY = directionY*MAX_SPEED;

        } else {
            velocityX = 0;
            velocityY = 0;
        }


        //update position of the enemy
        positionX += velocityX;
        positionY += velocityY;
    }

}
