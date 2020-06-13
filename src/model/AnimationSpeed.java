package model;

/**
 * Modelizes an animation speed..
 *
 * @author Antonin
 */
public class AnimationSpeed {
    // TODO : convert speed type into SimpleIntegerProperty ??
    private int speed;
    
    private final int MIN_SPEED = 0;
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

    public void slowDown() {
        // limit min speed
        if (this.speed > MIN_SPEED) {
            this.speed -= 1;
        }
    }

    public void speedUp() {
        // limit max speed
        if (this.speed < MAX_SPEED) {
            this.speed += 1;
        }
    }
}
