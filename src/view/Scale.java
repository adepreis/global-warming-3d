package view;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

/**
 * Scale class represents a color gradient corresponding to the temperature
 * fluctuations.
 *
 * @author Antonin
 */
public class Scale extends Rectangle {

//    private final double MIN = 100 ;    // width
//    private final double MAX = 1000 ;   // height ?
//    private final double MIN_HUE;
//    private final double MAX_HUE;

    public Scale(int width, int height, Color... colors) {
        super(width, height);
        
        this.setGradient(colors);
    }

    public void setGradient(Color... colors) {
        double tabSize = colors.length;
        
        Stop[] stops = new Stop[(int)tabSize];
        
        for (int i = 0; i < tabSize; i++) {
            stops[i] = new Stop(i/(tabSize-1), colors[i]);
        }
        
        LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        this.setFill(lg);
    }
    
}
