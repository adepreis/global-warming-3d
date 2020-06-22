package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Modelizes a temporal animation. An AnimationModel object encapsulates
 * a speed property and a playing state.
 *
 * @author Antonin
 */
public class AnimationModel {
    private final IntegerProperty speed = new SimpleIntegerProperty();
    private boolean playing;
    
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 5;

    public AnimationModel(int value) {
        speed.set(value);
        playing = false;
    }

    /**
     * Decreases the speed value while respecting the min speed bound.
     */
    public void slowDown() {
        if (getSpeed() > MIN_SPEED) {
            // speed.subtract(1); doesn't works !!?
            speed.set(getSpeed() - 1);
        }
    }

    /**
     * Increases the speed value while respecting the max speed bound.
     */
    public void speedUp() {
        if (getSpeed() < MAX_SPEED) {
            // speed.add(1); doesn't works !!?
            speed.set(getSpeed() + 1);
        }
    }

    public int getSpeed() {
        return speed.get();
    }

    public IntegerProperty speedProperty() {
        return speed;
    }

    /**
     * Get the value of playing
     *
     * @return the value of playing
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * Set the value of playing
     *
     * @param playing new value of playing
     */
    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    /**
     * Invert playing value.
     */
    public void togglePlaying() {
        this.playing = !this.playing;
    }

    @Override
    public String toString() {
        return "Animation = speed multiplier :" + getSpeed()
                + ", is playing ? " + playing;
    }
}
