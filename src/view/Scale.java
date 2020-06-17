package view;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

/**
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
        
        double tabSize = colors.length;
        
        Stop[] stops = new Stop[(int)tabSize];
        
        for (int i = 0; i < tabSize; i++) {
            stops[i] = new Stop(i/(tabSize-1), colors[i]);
        }
        
        LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        
//        MIN_HUE = colorBottom.getHue();
//        MAX_HUE = colorTop.getHue();
        
        this.setFill(lg);
//        this.setStroke(Color.BLACK);
//        this.setStrokeWidth(1);
    }

    // from https://stackoverflow.com/a/25214819
//    public Color getColorForValue(double value) {
////        if (value < MIN || value > MAX) {
////            return Color.BLACK ;
////        }
//        double hue = MIN_HUE + (MAX_HUE - MIN_HUE) * value; //(value - MIN) / (MAX - MIN) ;
//        return Color.hsb(hue, 1.0, 1.0);
//    }
    
}
