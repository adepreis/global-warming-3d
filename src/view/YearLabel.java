package view;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * YearLabel class is the view of a year as a label.
 *
 * @author Antonin
 */
public class YearLabel extends Label {

    /**
     * Initializes the label with a font size of 35.
     * 
     * @param year 
     */
    public YearLabel(int year) {
        super(Integer.toString(year));
        setFont(Font.font("System", FontWeight.BOLD, 35));
    }
    
}
