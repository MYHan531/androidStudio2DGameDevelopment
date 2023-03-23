package object;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.androidstudio2dgamedevelopment.GameLoop;
import com.example.androidstudio2dgamedevelopment.R;

/**
 * Enemy is a character which always moves in the direction of the player
 * The Enemy class is an extension of a Circle,
 * which is an extension of the GameObject
 */
public class Enemy extends Circle {
    private static final double SPEED_PIXELS_PER_SECOND = Player.SPEED_PIXELS_PER_SECOND * 0.6;
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private final Player player;

    public Enemy (Context context, Player player, double positionX, double positionY, double radius) {
        super(context, ContextCompat.getColor(context, R.color.enemy),positionX,positionY, radius);
        this.player = player;
    }
    @Override
    public void update() {
        /**
         * Update the velocity of the enemy so that the velocity is in the direction of the player
         */
        // Calculate vector from enemy to player (in x and y)
        double distanceToPlayerX = player.getPositionX() - positionX;
        double distanceToPlayerY = player.getPositionY() - positionY;

        //Calculate (absolute) distance between enemy (this) and player
        double distanceToPlayer = GameObject.getDistanceBetweenObjects(this,player);

        // Calculate direction from enemy to player
        double directionX = distanceToPlayerX/distanceToPlayer;
        double directionY = distanceToPlayerY/distanceToPlayer;

        //set velocity in the direction to the player
        if (distanceToPlayer > 0) { //avoid divison by zero
            velocityX = directionX*MAX_SPEED;
            velocityY = directionY*MAX_SPEED;
        } else {
            velocityX = 0;
            velocityY = 0;
        }
        //update position of the emeny
        positionX += velocityX;
        positionY += velocityY;
    }



    public void setPosition(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }
}
