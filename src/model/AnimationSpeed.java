package model;

/**
 * Modelizes an animation speed..
 *
 * @author Antonin
 */
public class AnimationSpeed {
    // TODO : convert speed type into SimpleIntegerProperty ??
    private int speed;
    
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 5;

    public AnimationSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "x" + speed;
    }    

    /**
     * Decreases the speed value while respecting the min speed bound.
     */
    public void slowDown() {
        if (this.speed > MIN_SPEED) {
            this.speed -= 1;
        }
    }

    /**
     * Increases the speed value while respecting the max speed bound.
     */
    public void speedUp() {
        if (this.speed < MAX_SPEED) {
            this.speed += 1;
        }
    }
}
