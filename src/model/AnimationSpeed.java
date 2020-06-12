package model;

/**
 * Modelizes an animation speed..
 *
 * @author Antonin
 */
public class AnimationSpeed {
    // TODO : convert speed type into SimpleIntegerProperty ??
    private int speed;

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
        // TODO : limit min speed
        this.speed -= 1;
    }

    public void speedUp() {
        // TODO : limit max speed
        this.speed += 1;
    }
}
